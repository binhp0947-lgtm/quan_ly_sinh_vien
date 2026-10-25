package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.dto.DocumentDownloadDTO;
import com.mkii.coursemanagementsystem.dto.DocumentResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    /**
     * Upload 1 tài liệu lên MinIO, tự động đánh version nếu trùng tên
     */
    DocumentResponseDTO upload(MultipartFile file);

    /**
     * Upload danh sách nhiều file tài liệu cùng lúc
     */
    List<DocumentResponseDTO> uploadMultiple(List<MultipartFile> files);

    /**
     * Tải file từ MinIO theo document ID
     */
    DocumentDownloadDTO download(Long id);

    /**
     * Xóa mềm (Soft delete) tài liệu
     */
    void delete(Long id);

    /**
     * Xóa vĩnh viễn tài liệu trên MinIO và trong CSDL
     */
    void hardDelete(Long id);

    /**
     * Lấy thông tin tài liệu theo ID
     */
    DocumentResponseDTO getById(Long id);

    /**
     * Lấy danh sách tất cả tài liệu đang hoạt động (active & non-deleted)
     */
    List<DocumentResponseDTO> getAll();

    /**
     * Lấy lịch sử các phiên bản (version history) của một file
     */
    List<DocumentResponseDTO> getHistory(String fileName);

    /**
     * Tìm kiếm tài liệu theo tên file
     */
    List<DocumentResponseDTO> search(String keyword);

    /**
     * Tạo presigned URL tải trực tiếp từ MinIO (hạn 2 giờ)
     */
    String getPresignedDownloadUrl(Long id);

    /**
     * Đếm tổng số tài liệu đang hoạt động
     */
    Integer getCount();
}
