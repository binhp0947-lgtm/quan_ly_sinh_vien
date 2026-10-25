package com.mkii.coursemanagementsystem.service.impl;

import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dao.IClassInfoDao;
import com.mkii.coursemanagementsystem.dao.IDepartmentDao;
import com.mkii.coursemanagementsystem.dto.ClassInfoRequestDTO;
import com.mkii.coursemanagementsystem.dto.ClassInfoResponseDTO;
import com.mkii.coursemanagementsystem.entity.ClassInfo;
import com.mkii.coursemanagementsystem.entity.Department;
import com.mkii.coursemanagementsystem.service.ClassInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClassInfoServiceImpl implements ClassInfoService {

    private static final Logger logger = LoggerFactory.getLogger(ClassInfoServiceImpl.class);

    @Autowired
    private IClassInfoDao classInfoDao;

    @Autowired
    private IDepartmentDao departmentDao;

    @Override
    public ClassInfo create(ClassInfoRequestDTO requestDTO) {
        logger.info("Creating ClassInfo: {}", requestDTO.getClassName());
        ClassInfo classInfo = new ClassInfo();
        mapRequestToEntity(requestDTO, classInfo);
        return classInfoDao.save(classInfo);
    }

    @Override
    public ClassInfo update(ClassInfoRequestDTO requestDTO, Long id) {
        logger.info("Updating ClassInfo: {}", id);
        ClassInfo existing = classInfoDao.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.CLASS_INFO_NOT_FOUND, id));
        }
        mapRequestToEntity(requestDTO, existing);
        return classInfoDao.update(existing);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting ClassInfo: {}", id);
        ClassInfo existing = classInfoDao.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.CLASS_INFO_NOT_FOUND, id));
        }
        classInfoDao.delete(id);
    }

    @Override
    public ClassInfoResponseDTO getById(Long id) {
        logger.info("Getting ClassInfo by id: {}", id);
        ClassInfo classInfo = classInfoDao.getById(id);
        if (classInfo == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.CLASS_INFO_NOT_FOUND, id));
        }
        return mapToDTO(classInfo);
    }

    @Override
    public List<ClassInfoResponseDTO> getAll() {
        logger.info("Getting all ClassInfo");
        return classInfoDao.getAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassInfoResponseDTO> getByDepartmentId(Long departmentId) {
        logger.info("Getting ClassInfo by departmentId: {}", departmentId);
        return classInfoDao.getByDepartmentId(departmentId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassInfoResponseDTO> searchByKeyword(String keyword) {
        logger.info("Searching ClassInfo by keyword: {}", keyword);
        return classInfoDao.searchByKeyword(keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ClassInfoResponseDTO getByCode(String classCode) {
        logger.info("Getting ClassInfo by code: {}", classCode);
        ClassInfo classInfo = classInfoDao.getByCode(classCode);
        if (classInfo == null) {
            throw new ResourceNotFoundException("Không tìm thấy lớp học với mã: " + classCode);
        }
        return mapToDTO(classInfo);
    }

    @Override
    public ClassInfoResponseDTO getByField(String field, Object value) {
        logger.info("Getting ClassInfo by field: {} = {}", field, value);
        ClassInfo classInfo = classInfoDao.getByField(field, value);
        if (classInfo == null) {
            throw new ResourceNotFoundException("Không tìm thấy lớp học với " + field + ": " + value);
        }
        return mapToDTO(classInfo);
    }

    @Override
    public Integer getCount() {
        logger.info("Getting ClassInfo count");
        return classInfoDao.getCount();
    }

    // ---- private helpers ----

    private void mapRequestToEntity(ClassInfoRequestDTO requestDTO, ClassInfo classInfo) {
        if (requestDTO.getClassCode() != null) {
            classInfo.setClassCode(requestDTO.getClassCode());
        }
        if (requestDTO.getClassName() != null) {
            classInfo.setClassName(requestDTO.getClassName());
        }
        if (requestDTO.getDescription() != null) {
            classInfo.setDescription(requestDTO.getDescription());
        }
        if (requestDTO.getRoom() != null) {
            classInfo.setRoom(requestDTO.getRoom());
        }
        if (requestDTO.getDepartmentId() != null) {
            Department department = departmentDao.getById(requestDTO.getDepartmentId());
            if (department == null) {
                throw new ResourceNotFoundException(String.format(MessageConstants.DEPARTMENT_NOT_FOUND, requestDTO.getDepartmentId()));
            }
            classInfo.setDepartment(department);
        }
    }

    private ClassInfoResponseDTO mapToDTO(ClassInfo classInfo) {
        Long deptId = classInfo.getDepartment() != null ? classInfo.getDepartment().getId() : null;
        String deptName = classInfo.getDepartment() != null ? classInfo.getDepartment().getDepartmentName() : null;

        return ClassInfoResponseDTO.builder()
                .id(classInfo.getId())
                .classCode(classInfo.getClassCode())
                .className(classInfo.getClassName())
                .description(classInfo.getDescription())
                .room(classInfo.getRoom())
                .departmentId(deptId)
                .departmentName(deptName)
                .createdAt(classInfo.getCreatedAt())
                .updatedAt(classInfo.getUpdatedAt())
                .build();
    }
}
