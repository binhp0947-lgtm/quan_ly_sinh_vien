package com.mkii.coursemanagementsystem.dao;

import com.mkii.coursemanagementsystem.entity.ClassInfo;

import java.util.List;
import java.util.Optional;

public interface IClassInfoDao extends IGenericDao<Long, ClassInfo> {

    ClassInfo save(ClassInfo classInfo);

    ClassInfo update(ClassInfo classInfo);

    void delete(Long id);

    ClassInfo getById(Long id);

    ClassInfo getByCode(String classCode);

    Optional<ClassInfo> findByCode(String classCode);

    List<ClassInfo> getAll();

    List<ClassInfo> getByDepartmentId(Long departmentId);

    List<ClassInfo> searchByKeyword(String keyword);

    Integer getCount();
}
