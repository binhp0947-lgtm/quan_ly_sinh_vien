package com.mkii.coursemanagementsystem.rest;

import com.mkii.coursemanagementsystem.common.ApiConstants;
import com.mkii.coursemanagementsystem.dto.DocumentDownloadDTO;
import com.mkii.coursemanagementsystem.dto.DocumentResponseDTO;
import com.mkii.coursemanagementsystem.service.DocumentService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiConstants.BASE_DOCUMENT_URL)
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocumentRest {

    private final DocumentService documentService;

    public DocumentRest(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponseDTO> upload(@RequestParam("file") MultipartFile file) {
        DocumentResponseDTO response = documentService.upload(file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<DocumentResponseDTO>> uploadMultiple(@RequestParam("files") List<MultipartFile> files) {
        List<DocumentResponseDTO> responseList = documentService.uploadMultiple(files);
        return new ResponseEntity<>(responseList, HttpStatus.CREATED);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        DocumentDownloadDTO downloadDTO = documentService.download(id);

        String encodedFilename = URLEncoder.encode(downloadDTO.getOriginalFileName(), StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        ByteArrayResource resource = new ByteArrayResource(downloadDTO.getData());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadDTO.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"; filename*=UTF-8''" + encodedFilename)
                .contentLength(downloadDTO.getData().length)
                .body(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard-delete")
    public ResponseEntity<Void> hardDelete(@PathVariable Long id) {
        documentService.hardDelete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public DocumentResponseDTO getById(@PathVariable Long id) {
        return documentService.getById(id);
    }

    @GetMapping
    public List<DocumentResponseDTO> getAll() {
        return documentService.getAll();
    }

    @GetMapping("/history")
    public List<DocumentResponseDTO> getHistory(@RequestParam String fileName) {
        return documentService.getHistory(fileName);
    }

    @GetMapping("/search")
    public List<DocumentResponseDTO> search(@RequestParam String keyword) {
        return documentService.search(keyword);
    }

    @GetMapping("/count")
    public Integer getCount() {
        return documentService.getCount();
    }

    @GetMapping("/{id}/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(@PathVariable Long id) {
        String presignedUrl = documentService.getPresignedDownloadUrl(id);
        return ResponseEntity.ok(Map.of("presignedUrl", presignedUrl));
    }
}
