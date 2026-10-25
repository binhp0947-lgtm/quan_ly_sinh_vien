package com.mkii.coursemanagementsystem.rest;

import com.mkii.coursemanagementsystem.common.ApiConstants;
import com.mkii.coursemanagementsystem.dto.StudentRequestDTO;
import com.mkii.coursemanagementsystem.dto.StudentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Student;
import com.mkii.coursemanagementsystem.service.StudentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.BASE_STUDENT_URL)
public class StudentRest {

    private final StudentService studentService;

    public StudentRest(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentResponseDTO> getAll() {
        return studentService.getAll();
    }

    @GetMapping("/{id}")
    public StudentResponseDTO getById(@PathVariable Long id) {
        return studentService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody StudentRequestDTO requestDTO) {
        Student created = studentService.create(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @RequestBody StudentRequestDTO requestDTO) {
        return studentService.update(requestDTO, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<StudentResponseDTO> search(@RequestParam String keyword) {
        return studentService.searchByKeyword(keyword);
    }

    @GetMapping("/by-email")
    public StudentResponseDTO getByEmail(@RequestParam String email) {
        return studentService.getByEmail(email);
    }

    @GetMapping("/by-field")
    public StudentResponseDTO getByField(@RequestParam String field, @RequestParam String value) {
        return studentService.getByField(field, value);
    }

    @GetMapping("/count")
    public Integer getCount() {
        return studentService.getCount();
    }
}
