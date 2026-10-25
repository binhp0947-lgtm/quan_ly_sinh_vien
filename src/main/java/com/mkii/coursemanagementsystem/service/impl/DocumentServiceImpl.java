package com.mkii.coursemanagementsystem.service.impl;

import com.mkii.coursemanagementsystem.common.ApiConstants;
import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.common.exception.BadRequestException;
import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dao.IDocumentDao;
import com.mkii.coursemanagementsystem.dto.DocumentDownloadDTO;
import com.mkii.coursemanagementsystem.dto.DocumentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Document;
import com.mkii.coursemanagementsystem.service.DocumentService;
import io.minio.*;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "xlsx", "xls", "doc", "docx", "jpg", "jpeg", "png", "txt", "csv", "zip"
    );

    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._\\-\\s\u00C0-\u1EF9]+$");

    @Autowired
    private IDocumentDao documentDao;

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                logger.info("Bucket '{}' chưa tồn tại, đang tự động tạo mới...", bucketName);
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                logger.info("Bucket '{}' đã được tạo thành công.", bucketName);
            }
        } catch (Exception e) {
            logger.error("Lỗi khi kiểm tra hoặc tạo bucket trên MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể kết nối hoặc khởi tạo bucket trên MinIO: " + e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException(MessageConstants.DOCUMENT_EMPTY);
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new BadRequestException("Tên file không hợp lệ.");
        }

        if (originalFileName.contains("..") || originalFileName.contains("/") || originalFileName.contains("\\")) {
            throw new BadRequestException("Tên file chứa ký tự đường dẫn không an toàn.");
        }

        if (originalFileName.length() > 100) {
            throw new BadRequestException("Tên file quá dài (tối đa 100 ký tự).");
        }

        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == originalFileName.length() - 1) {
            throw new BadRequestException("File phải có phần mở rộng định dạng (extension).");
        }

        String extension = originalFileName.substring(dotIndex + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException(String.format("Định dạng file '.%s' không được hỗ trợ. Định dạng cho phép: %s",
                    extension, String.join(", ", ALLOWED_EXTENSIONS)));
        }
    }

    @Override
    public DocumentResponseDTO upload(MultipartFile file) {
        validateFile(file);
        ensureBucketExists();

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String objectName = UUID.randomUUID() + extension;
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        // Tải object nhị phân lên MinIO
        try (InputStream inputStream = file.getInputStream()) {
            logger.info("Uploading file '{}' to MinIO bucket '{}' with objectName '{}'", originalFileName, bucketName, objectName);
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (Exception e) {
            logger.error("Lỗi khi upload file lên MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi upload file lên MinIO: " + e.getMessage(), e);
        }

        // Quản lý phiên bản (Versioning): nếu đã có file cùng tên đang active thì tăng version
        Document existingActive = documentDao.getActiveByName(originalFileName);
        double newVersion = 1.0;

        if (existingActive != null) {
            logger.info("File '{}' đã tồn tại phiên bản v{}, đang nâng cấp phiên bản...", originalFileName, existingActive.getVersion());
            existingActive.setActive(false);
            documentDao.update(existingActive);
            newVersion = existingActive.getVersion() + 1.0;
        }

        Document document = Document.builder()
                .originalFileName(originalFileName)
                .objectName(objectName)
                .contentType(contentType)
                .fileSize(file.getSize())
                .bucketName(bucketName)
                .version(newVersion)
                .active(true)
                .deleted(false)
                .build();

        Document savedDoc = documentDao.save(document);
        logger.info("Đã lưu tài liệu thành công: ID={}, version=v{}", savedDoc.getId(), savedDoc.getVersion());
        return mapToDTO(savedDoc);
    }

    @Override
    public List<DocumentResponseDTO> uploadMultiple(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException("Danh sách file tải lên không được để trống.");
        }
        List<DocumentResponseDTO> result = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                result.add(upload(file));
            }
        }
        return result;
    }

    @Override
    public DocumentDownloadDTO download(Long id) {
        Document document = documentDao.getById(id);
        if (document == null || Boolean.TRUE.equals(document.getDeleted())) {
            throw new ResourceNotFoundException(String.format(MessageConstants.DOCUMENT_NOT_FOUND, id));
        }

        logger.info("Downloading file ID {}: bucket={}, object={}", id, document.getBucketName(), document.getObjectName());
        try (GetObjectResponse response = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(document.getBucketName())
                        .object(document.getObjectName())
                        .build())) {

            byte[] data = response.readAllBytes();
            return DocumentDownloadDTO.builder()
                    .originalFileName(document.getOriginalFileName())
                    .contentType(document.getContentType())
                    .data(data)
                    .build();
        } catch (Exception e) {
            logger.error("Lỗi khi tải file từ MinIO: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tải file từ MinIO: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Long id) {
        // Soft delete: đánh dấu deleted = true, active = false
        Document document = documentDao.getById(id);
        if (document == null || Boolean.TRUE.equals(document.getDeleted())) {
            throw new ResourceNotFoundException(String.format(MessageConstants.DOCUMENT_NOT_FOUND, id));
        }

        logger.info("Soft-deleting document ID: {}", id);
        document.setDeleted(true);
        document.setActive(false);
        documentDao.update(document);
    }

    @Override
    public void hardDelete(Long id) {
        Document document = documentDao.getById(id);
        if (document == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.DOCUMENT_NOT_FOUND, id));
        }

        logger.info("Hard-deleting document ID {} and removing object from MinIO: {}", id, document.getObjectName());
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(document.getBucketName())
                            .object(document.getObjectName())
                            .build()
            );
        } catch (Exception e) {
            logger.warn("Cảnh báo: Không thể xóa object trên MinIO (có thể đã bị xóa trước đó): {}", e.getMessage());
        }

        documentDao.delete(id);
    }

    @Override
    public DocumentResponseDTO getById(Long id) {
        Document document = documentDao.getById(id);
        if (document == null || Boolean.TRUE.equals(document.getDeleted())) {
            throw new ResourceNotFoundException(String.format(MessageConstants.DOCUMENT_NOT_FOUND, id));
        }
        return mapToDTO(document);
    }

    @Override
    public List<DocumentResponseDTO> getAll() {
        return documentDao.getAllActive()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDTO> getHistory(String fileName) {
        return documentDao.getHistoryByName(fileName)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DocumentResponseDTO> search(String keyword) {
        return documentDao.searchByFileName(keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String getPresignedDownloadUrl(Long id) {
        Document document = documentDao.getById(id);
        if (document == null || Boolean.TRUE.equals(document.getDeleted())) {
            throw new ResourceNotFoundException(String.format(MessageConstants.DOCUMENT_NOT_FOUND, id));
        }

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(document.getBucketName())
                            .object(document.getObjectName())
                            .expiry(2, TimeUnit.HOURS)
                            .build()
            );
        } catch (Exception e) {
            logger.error("Lỗi khi tạo Presigned URL: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi tạo Presigned URL: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer getCount() {
        return documentDao.getActiveCount();
    }

    private DocumentResponseDTO mapToDTO(Document document) {
        return DocumentResponseDTO.builder()
                .id(document.getId())
                .originalFileName(document.getOriginalFileName())
                .objectName(document.getObjectName())
                .contentType(document.getContentType())
                .fileSize(document.getFileSize())
                .bucketName(document.getBucketName())
                .version(document.getVersion())
                .active(document.getActive())
                .downloadUrl(ApiConstants.BASE_DOCUMENT_URL + "/download/" + document.getId())
                .createdAt(document.getCreatedAt())
                .build();
    }
}
