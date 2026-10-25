package com.mkii.coursemanagementsystem.rest;

import com.mkii.coursemanagementsystem.common.ApiConstants;
import com.mkii.coursemanagementsystem.dto.DepartmentRequestDTO;
import com.mkii.coursemanagementsystem.dto.DepartmentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Department;
import com.mkii.coursemanagementsystem.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.BASE_DEPARTMENT_URL)
public class DepartmentRest {

    private final DepartmentService departmentService;

    public DepartmentRest(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<DepartmentResponseDTO> getAll() {
        return departmentService.getAll();
    }

    @GetMapping("/{id}")
    public DepartmentResponseDTO getById(@PathVariable Long id) {
        return departmentService.getById(id);
    }

    @PostMapping
    public ResponseEntity<Department> create(@RequestBody DepartmentRequestDTO requestDTO) {
        Department created = departmentService.create(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public Department update(@PathVariable Long id, @RequestBody DepartmentRequestDTO requestDTO) {
        return departmentService.update(requestDTO, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<DepartmentResponseDTO> search(@RequestParam String keyword) {
        return departmentService.searchByKeyword(keyword);
    }

    @GetMapping("/by-code")
    public DepartmentResponseDTO getByCode(@RequestParam String code) {
        return departmentService.getByCode(code);
    }

    @GetMapping("/count")
    public Integer getCount() {
        return departmentService.getCount();
    }
}
