package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.dto.AuthRequestDTO;
import com.mkii.coursemanagementsystem.dto.AuthResponseDTO;
import com.mkii.coursemanagementsystem.dto.RegisterRequestDTO;
import com.mkii.coursemanagementsystem.dto.UserResponseDTO;

public interface AuthService {

    AuthResponseDTO login(AuthRequestDTO requestDTO);

    UserResponseDTO register(RegisterRequestDTO requestDTO);

    UserResponseDTO getCurrentUser();
}
