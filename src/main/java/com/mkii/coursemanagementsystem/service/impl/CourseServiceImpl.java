package com.mkii.coursemanagementsystem.service.impl;

import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dao.ICourseDao;
import com.mkii.coursemanagementsystem.dto.CourseRequestDTO;
import com.mkii.coursemanagementsystem.dto.CourseResponseDTO;
import com.mkii.coursemanagementsystem.entity.Course;
import com.mkii.coursemanagementsystem.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);

    @Autowired
    private ICourseDao courseDao;

    @Override
    public Course create(CourseRequestDTO requestDTO) {
        logger.info("Creating Course: {}", requestDTO.getTitle());
        Course course = new Course();
        mapRequestToEntity(requestDTO, course);
        return courseDao.save(course);
    }

    @Override
    public Course update(CourseRequestDTO requestDTO, Long id) {
        logger.info("Updating Course: {}", id);
        Course existing = courseDao.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.COURSE_NOT_FOUND, id));
        }
        mapRequestToEntity(requestDTO, existing);
        return courseDao.update(existing);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting Course: {}", id);
        Course existing = courseDao.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.COURSE_NOT_FOUND, id));
        }
        courseDao.delete(id);
    }

    @Override
    public CourseResponseDTO getById(Long id) {
        logger.info("Getting Course by id: {}", id);
        Course course = courseDao.getById(id);
        if (course == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.COURSE_NOT_FOUND, id));
        }
        return mapToDTO(course);
    }

    @Override
    public List<CourseResponseDTO> getAll() {
        logger.info("Getting all Courses");
        return courseDao.getAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDTO> searchByTitle(String keyword) {
        logger.info("Searching Courses by title: {}", keyword);
        return courseDao.searchByTitle(keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CourseResponseDTO> filterByPrice(Double minPrice, Double maxPrice) {
        logger.info("Filtering Courses by price range: {} - {}", minPrice, maxPrice);
        return courseDao.findByPriceRange(minPrice, maxPrice)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponseDTO getByTitle(String title) {
        logger.info("Getting Course by title: {}", title);
        Course course = courseDao.getByTitle(title);
        if (course == null) {
            throw new ResourceNotFoundException("Không tìm thấy khóa học với tiêu đề: " + title);
        }
        return mapToDTO(course);
    }

    @Override
    public CourseResponseDTO getByField(String field, Object value) {
        logger.info("Getting Course by field: {} = {}", field, value);
        Course course = courseDao.getByField(field, value);
        if (course == null) {
            throw new ResourceNotFoundException("Không tìm thấy khóa học với " + field + ": " + value);
        }
        return mapToDTO(course);
    }

    @Override
    public Integer getCount() {
        logger.info("Getting Course count");
        return courseDao.getCount();
    }

    // ---- private helpers ----

    private void mapRequestToEntity(CourseRequestDTO requestDTO, Course course) {
        if (requestDTO.getTitle() != null) {
            course.setTitle(requestDTO.getTitle());
        }
        if (requestDTO.getDescription() != null) {
            course.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getDuration() != null) {
            course.setDuration(requestDTO.getDuration());
        }
        if (requestDTO.getPrice() != null) {
            course.setPrice(requestDTO.getPrice());
        }
    }

    private CourseResponseDTO mapToDTO(Course course) {
        return new CourseResponseDTO(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getPrice()
        );
    }
}
