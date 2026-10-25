package com.mkii.coursemanagementsystem.dto;

import com.mkii.coursemanagementsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
}
