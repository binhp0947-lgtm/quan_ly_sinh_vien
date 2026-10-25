package com.mkii.coursemanagementsystem.rest;

import com.mkii.coursemanagementsystem.dto.ClassInfoRequestDTO;
import com.mkii.coursemanagementsystem.dto.ClassInfoResponseDTO;
import com.mkii.coursemanagementsystem.entity.ClassInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ClassInfoRest {

    // Tạo ClassInfo mới (Chỉ ADMIN hoặc LECTURER)
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    ResponseEntity<ClassInfo> createClassInfo(@RequestBody ClassInfoRequestDTO requestDTO);

    // Cập nhật ClassInfo (Chỉ ADMIN hoặc LECTURER)
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    ResponseEntity<ClassInfo> updateClassInfo(@PathVariable Long id, @RequestBody ClassInfoRequestDTO requestDTO);

    // Xóa ClassInfo (Chỉ ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<String> deleteClassInfo(@PathVariable Long id);

    // Lấy ClassInfo theo ID
    @GetMapping("/{id}")
    ResponseEntity<ClassInfoResponseDTO> getClassInfoById(@PathVariable Long id);

    // Lấy ClassInfo theo mã lớp
    @GetMapping("/code/{code}")
    ResponseEntity<ClassInfoResponseDTO> getClassInfoByCode(@PathVariable String code);

    // Lấy tất cả ClassInfo
    @GetMapping("/list")
    ResponseEntity<List<ClassInfoResponseDTO>> getAllClassInfo();

    // Lấy ClassInfo theo khoa/phòng ban
    @GetMapping("/department/{departmentId}")
    ResponseEntity<List<ClassInfoResponseDTO>> getClassInfoByDepartment(@PathVariable Long departmentId);

    // Tìm kiếm ClassInfo theo từ khóa
    @GetMapping("/search")
    ResponseEntity<List<ClassInfoResponseDTO>> searchClassInfo(@RequestParam(required = false) String keyword);

    // Đếm số lượng ClassInfo (Chỉ ADMIN)
    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Integer> getClassInfoCount();
}
