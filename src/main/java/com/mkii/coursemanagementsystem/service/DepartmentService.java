package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.dto.DepartmentRequestDTO;
import com.mkii.coursemanagementsystem.dto.DepartmentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Department;

import java.util.List;

public interface DepartmentService {

    Department create(DepartmentRequestDTO requestDTO);

    Department update(DepartmentRequestDTO requestDTO, Long id);

    void delete(Long id);

    DepartmentResponseDTO getById(Long id);

    List<DepartmentResponseDTO> getAll();

    List<DepartmentResponseDTO> searchByKeyword(String keyword);

    DepartmentResponseDTO getByCode(String departmentCode);

    DepartmentResponseDTO getByField(String field, Object value);

    Integer getCount();
}
