package com.mkii.coursemanagementsystem.repository;

import com.mkii.coursemanagementsystem.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassInfoRepository extends JpaRepository<ClassInfo, Long> {

    Optional<ClassInfo> findByClassCode(String classCode);

    List<ClassInfo> findByDepartmentId(Long departmentId);
}
