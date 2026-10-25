package com.mkii.coursemanagementsystem.rest;

import com.mkii.coursemanagementsystem.common.ApiConstants;
import com.mkii.coursemanagementsystem.dto.CourseRequestDTO;
import com.mkii.coursemanagementsystem.dto.CourseResponseDTO;
import com.mkii.coursemanagementsystem.entity.Course;
import com.mkii.coursemanagementsystem.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.BASE_COURSE_URL)
public class CourseRest {

    private final CourseService courseService;

    public CourseRest(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseResponseDTO> getAll() {
        return courseService.getAll();
    }

    @GetMapping("/{id}")
    public CourseResponseDTO getById(@PathVariable Long id) {
        return courseService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Course> create(@RequestBody CourseRequestDTO requestDTO) {
        Course created = courseService.create(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Course update(@PathVariable Long id, @RequestBody CourseRequestDTO requestDTO) {
        return courseService.update(requestDTO, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<CourseResponseDTO> search(@RequestParam String keyword) {
        return courseService.searchByTitle(keyword);
    }

    @GetMapping("/filter")
    public List<CourseResponseDTO> filterByPrice(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        return courseService.filterByPrice(minPrice, maxPrice);
    }

    @GetMapping("/by-title")
    public CourseResponseDTO getByTitle(@RequestParam String title) {
        return courseService.getByTitle(title);
    }

    @GetMapping("/by-field")
    public CourseResponseDTO getByField(@RequestParam String field, @RequestParam String value) {
        return courseService.getByField(field, value);
    }

    @GetMapping("/count")
    public Integer getCount() {
        return courseService.getCount();
    }
}
