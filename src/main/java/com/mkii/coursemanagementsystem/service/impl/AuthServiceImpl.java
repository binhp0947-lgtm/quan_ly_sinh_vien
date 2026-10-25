package com.mkii.coursemanagementsystem.service.impl;

import com.mkii.coursemanagementsystem.common.exception.BadRequestException;
import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dto.AuthRequestDTO;
import com.mkii.coursemanagementsystem.dto.AuthResponseDTO;
import com.mkii.coursemanagementsystem.dto.RegisterRequestDTO;
import com.mkii.coursemanagementsystem.dto.UserResponseDTO;
import com.mkii.coursemanagementsystem.entity.Role;
import com.mkii.coursemanagementsystem.entity.User;
import com.mkii.coursemanagementsystem.repository.UserRepository;
import com.mkii.coursemanagementsystem.security.CustomUserDetails;
import com.mkii.coursemanagementsystem.security.JwtUtils;
import com.mkii.coursemanagementsystem.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO requestDTO) {
        logger.info("Attempting login for user: {}", requestDTO.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getUsername(), requestDTO.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String token = jwtUtils.generateToken(authentication);

        return AuthResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .role(user.getRole())
                .expiresInMs(jwtUtils.getJwtExpirationMs())
                .build();
    }

    @Override
    public UserResponseDTO register(RegisterRequestDTO requestDTO) {
        logger.info("Registering new user: {}", requestDTO.getUsername());

        if (userRepository.existsByUsername(requestDTO.getUsername())) {
            throw new BadRequestException("Tên đăng nhập '" + requestDTO.getUsername() + "' đã được sử dụng.");
        }

        if (requestDTO.getEmail() != null && userRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Email '" + requestDTO.getEmail() + "' đã được sử dụng.");
        }

        Role assignedRole = requestDTO.getRole() != null ? requestDTO.getRole() : Role.ROLE_STUDENT;

        User user = User.builder()
                .username(requestDTO.getUsername())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .fullName(requestDTO.getFullName())
                .email(requestDTO.getEmail())
                .role(assignedRole)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        return mapToDTO(savedUser);
    }

    @Override
    public UserResponseDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new ResourceNotFoundException("Người dùng chưa được xác thực.");
        }
        return mapToDTO(userDetails.getUser());
    }

    private UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
