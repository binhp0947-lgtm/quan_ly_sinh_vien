package com.mkii.coursemanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassInfoRequestDTO {
    private String classCode;
    private String className;
    private String description;
    private String room;
    private Long departmentId;
}
