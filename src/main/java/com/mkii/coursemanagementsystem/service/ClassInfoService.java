package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.dto.ClassInfoRequestDTO;
import com.mkii.coursemanagementsystem.dto.ClassInfoResponseDTO;
import com.mkii.coursemanagementsystem.entity.ClassInfo;

import java.util.List;

public interface ClassInfoService {

    ClassInfo create(ClassInfoRequestDTO requestDTO);

    ClassInfo update(ClassInfoRequestDTO requestDTO, Long id);

    void delete(Long id);

    ClassInfoResponseDTO getById(Long id);

    List<ClassInfoResponseDTO> getAll();

    List<ClassInfoResponseDTO> getByDepartmentId(Long departmentId);

    List<ClassInfoResponseDTO> searchByKeyword(String keyword);

    ClassInfoResponseDTO getByCode(String classCode);

    ClassInfoResponseDTO getByField(String field, Object value);

    Integer getCount();
}
