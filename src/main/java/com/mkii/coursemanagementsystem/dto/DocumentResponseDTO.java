package com.mkii.coursemanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDTO {
    private Long id;
    private String originalFileName;
    private String objectName;
    private String contentType;
    private Long fileSize;
    private String bucketName;
    private Double version;
    private Boolean active;
    private String downloadUrl;
    private LocalDateTime createdAt;
}
