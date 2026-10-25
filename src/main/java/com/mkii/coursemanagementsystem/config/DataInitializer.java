package com.mkii.coursemanagementsystem.config;

import com.mkii.coursemanagementsystem.entity.Role;
import com.mkii.coursemanagementsystem.entity.User;
import com.mkii.coursemanagementsystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner initDefaultUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Khởi tạo tài khoản Admin mặc định
            if (!userRepository.existsByUsername("admin")) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .fullName("Quản trị viên hệ thống")
                        .email("admin@course.edu.vn")
                        .role(Role.ROLE_ADMIN)
                        .active(true)
                        .build();
                userRepository.save(admin);
                logger.info(">>> Khởi tạo tài khoản Admin mặc định: admin / admin123");
            }

            // Khởi tạo tài khoản Giảng viên mặc định
            if (!userRepository.existsByUsername("lecturer")) {
                User lecturer = User.builder()
                        .username("lecturer")
                        .password(passwordEncoder.encode("lecturer123"))
                        .fullName("Giảng viên mẫu")
                        .email("lecturer@course.edu.vn")
                        .role(Role.ROLE_LECTURER)
                        .active(true)
                        .build();
                userRepository.save(lecturer);
                logger.info(">>> Khởi tạo tài khoản Giảng viên mặc định: lecturer / lecturer123");
            }

            // Khởi tạo tài khoản Sinh viên mặc định
            if (!userRepository.existsByUsername("student")) {
                User student = User.builder()
                        .username("student")
                        .password(passwordEncoder.encode("student123"))
                        .fullName("Sinh viên mẫu")
                        .email("student@course.edu.vn")
                        .role(Role.ROLE_STUDENT)
                        .active(true)
                        .build();
                userRepository.save(student);
                logger.info(">>> Khởi tạo tài khoản Sinh viên mặc định: student / student123");
            }
        };
    }
}
