package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.dto.StudentRequestDTO;
import com.mkii.coursemanagementsystem.dto.StudentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Student;

import java.util.List;

public interface StudentService {

    Student create(StudentRequestDTO requestDTO);

    Student update(StudentRequestDTO requestDTO, Long id);

    void delete(Long id);

    StudentResponseDTO getById(Long id);

    List<StudentResponseDTO> getAll();

    List<StudentResponseDTO> searchByKeyword(String keyword);

    StudentResponseDTO getByEmail(String email);

    StudentResponseDTO getByField(String field, Object value);

    Integer getCount();
}
