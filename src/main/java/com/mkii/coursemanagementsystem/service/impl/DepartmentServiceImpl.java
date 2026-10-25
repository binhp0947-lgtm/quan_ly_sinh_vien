package com.mkii.coursemanagementsystem.service.impl;

import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dao.IDepartmentDao;
import com.mkii.coursemanagementsystem.dto.DepartmentRequestDTO;
import com.mkii.coursemanagementsystem.dto.DepartmentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Department;
import com.mkii.coursemanagementsystem.service.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    private IDepartmentDao departmentDao;

    @Override
    public Department create(DepartmentRequestDTO requestDTO) {
        logger.info("Creating Department: {}", requestDTO.getDepartmentName());
        Department department = new Department();
        mapRequestToEntity(requestDTO, department);
        return departmentDao.save(department);
    }

    @Override
    public Department update(DepartmentRequestDTO requestDTO, Long id) {
        logger.info("Updating Department: {}", id);
        Department existing = departmentDao.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.DEPARTMENT_NOT_FOUND, id));
        }
        mapRequestToEntity(requestDTO, existing);
        return departmentDao.update(existing);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting Department: {}", id);
        Department existing = departmentDao.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.DEPARTMENT_NOT_FOUND, id));
        }
        departmentDao.delete(id);
    }

    @Override
    public DepartmentResponseDTO getById(Long id) {
        logger.info("Getting Department by id: {}", id);
        Department department = departmentDao.getById(id);
        if (department == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.DEPARTMENT_NOT_FOUND, id));
        }
        return mapToDTO(department);
    }

    @Override
    public List<DepartmentResponseDTO> getAll() {
        logger.info("Getting all Departments");
        return departmentDao.getAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentResponseDTO> searchByKeyword(String keyword) {
        logger.info("Searching Departments by keyword: {}", keyword);
        return departmentDao.searchByKeyword(keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponseDTO getByCode(String departmentCode) {
        logger.info("Getting Department by code: {}", departmentCode);
        Department department = departmentDao.getByCode(departmentCode);
        if (department == null) {
            throw new ResourceNotFoundException("Không tìm thấy khoa/phòng ban với mã: " + departmentCode);
        }
        return mapToDTO(department);
    }

    @Override
    public DepartmentResponseDTO getByField(String field, Object value) {
        logger.info("Getting Department by field: {} = {}", field, value);
        Department department = departmentDao.getByField(field, value);
        if (department == null) {
            throw new ResourceNotFoundException("Không tìm thấy khoa/phòng ban với " + field + ": " + value);
        }
        return mapToDTO(department);
    }

    @Override
    public Integer getCount() {
        logger.info("Getting Department count");
        return departmentDao.getCount();
    }

    // ---- private helpers ----

    private void mapRequestToEntity(DepartmentRequestDTO requestDTO, Department department) {
        if (requestDTO.getDepartmentCode() != null) {
            department.setDepartmentCode(requestDTO.getDepartmentCode());
        }
        if (requestDTO.getDepartmentName() != null) {
            department.setDepartmentName(requestDTO.getDepartmentName());
        }
        if (requestDTO.getDescription() != null) {
            department.setDescription(requestDTO.getDescription());
        }
    }

    private DepartmentResponseDTO mapToDTO(Department department) {
        return DepartmentResponseDTO.builder()
                .id(department.getId())
                .departmentCode(department.getDepartmentCode())
                .departmentName(department.getDepartmentName())
                .description(department.getDescription())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }
}
