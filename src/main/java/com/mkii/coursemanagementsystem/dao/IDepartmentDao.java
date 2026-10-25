package com.mkii.coursemanagementsystem.dao;

import com.mkii.coursemanagementsystem.entity.Department;

import java.util.List;
import java.util.Optional;

public interface IDepartmentDao extends IGenericDao<Long, Department> {

    Department save(Department department);

    Department update(Department department);

    void delete(Long id);

    Department getById(Long id);

    Department getByCode(String departmentCode);

    Optional<Department> findByCode(String departmentCode);

    List<Department> getAll();

    List<Department> searchByKeyword(String keyword);

    Integer getCount();
}
