package com.mkii.coursemanagementsystem.dto;

import com.mkii.coursemanagementsystem.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequestDTO {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private Role role; // Optional: default to ROLE_STUDENT if null
}
