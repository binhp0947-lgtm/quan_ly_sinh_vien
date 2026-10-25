package com.mkii.coursemanagementsystem.service;

import com.mkii.coursemanagementsystem.dto.CourseRequestDTO;
import com.mkii.coursemanagementsystem.dto.CourseResponseDTO;
import com.mkii.coursemanagementsystem.entity.Course;

import java.util.List;

public interface CourseService {

    Course create(CourseRequestDTO requestDTO);

    Course update(CourseRequestDTO requestDTO, Long id);

    void delete(Long id);

    CourseResponseDTO getById(Long id);

    List<CourseResponseDTO> getAll();

    List<CourseResponseDTO> searchByTitle(String keyword);

    List<CourseResponseDTO> filterByPrice(Double minPrice, Double maxPrice);

    CourseResponseDTO getByTitle(String title);

    CourseResponseDTO getByField(String field, Object value);

    Integer getCount();
}