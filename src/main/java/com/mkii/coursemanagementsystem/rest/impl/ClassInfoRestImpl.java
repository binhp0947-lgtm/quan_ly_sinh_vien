package com.mkii.coursemanagementsystem.rest.impl;

import com.mkii.coursemanagementsystem.common.ApiConstants;
import com.mkii.coursemanagementsystem.dto.ClassInfoRequestDTO;
import com.mkii.coursemanagementsystem.dto.ClassInfoResponseDTO;
import com.mkii.coursemanagementsystem.entity.ClassInfo;
import com.mkii.coursemanagementsystem.rest.ClassInfoRest;
import com.mkii.coursemanagementsystem.service.ClassInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiConstants.BASE_CLASS_INFO_URL)
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClassInfoRestImpl implements ClassInfoRest {

    private static final Logger logger = LoggerFactory.getLogger(ClassInfoRestImpl.class);

    @Autowired
    private ClassInfoService classInfoService;

    @Override
    public ResponseEntity<ClassInfo> createClassInfo(ClassInfoRequestDTO requestDTO) {
        try {
            logger.info("Creating new Class Info: {}", requestDTO.getClassName());
            ClassInfo classInfo = classInfoService.create(requestDTO);
            return new ResponseEntity<>(classInfo, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error creating Class Info: ", e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<ClassInfo> updateClassInfo(Long id, ClassInfoRequestDTO requestDTO) {
        try {
            logger.info("Updating Class Info ID: {}", id);
            ClassInfo classInfo = classInfoService.update(requestDTO, id);
            return ResponseEntity.ok(classInfo);
        } catch (Exception e) {
            logger.error("Error updating Class Info: ", e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<String> deleteClassInfo(Long id) {
        try {
            logger.info("Deleting Class Info ID: {}", id);
            classInfoService.delete(id);
            return ResponseEntity.ok("Class Info deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting Class Info: ", e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<ClassInfoResponseDTO> getClassInfoById(Long id) {
        try {
            logger.info("Getting Class Info by ID: {}", id);
            ClassInfoResponseDTO classInfo = classInfoService.getById(id);
            if (classInfo == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(classInfo);
        } catch (Exception e) {
            logger.error("Error getting Class Info by ID: ", e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<ClassInfoResponseDTO> getClassInfoByCode(String code) {
        try {
            logger.info("Getting Class Info by Code: {}", code);
            ClassInfoResponseDTO classInfo = classInfoService.getByCode(code);
            if (classInfo == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(classInfo);
        } catch (Exception e) {
            logger.error("Error getting Class Info by Code: ", e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<List<ClassInfoResponseDTO>> getAllClassInfo() {
        try {
            logger.info("Getting all Class Info");
            List<ClassInfoResponseDTO> classInfoList = classInfoService.getAll();
            return ResponseEntity.ok(classInfoList);
        } catch (Exception e) {
            logger.error("Error getting all Class Info: ", e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<List<ClassInfoResponseDTO>> getClassInfoByDepartment(Long departmentId) {
        try {
            logger.info("Getting Class Info by Department ID: {}", departmentId);
            List<ClassInfoResponseDTO> classInfoList = classInfoService.getByDepartmentId(departmentId);
            return ResponseEntity.ok(classInfoList);
        } catch (Exception e) {
            logger.error("Error getting Class Info by Department: ", e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<List<ClassInfoResponseDTO>> searchClassInfo(String keyword) {
        try {
            logger.info("Searching Class Info with keyword: {}", keyword);
            List<ClassInfoResponseDTO> classInfoList = (keyword != null && !keyword.isBlank())
                    ? classInfoService.searchByKeyword(keyword)
                    : classInfoService.getAll();
            return ResponseEntity.ok(classInfoList);
        } catch (Exception e) {
            logger.error("Error searching Class Info: ", e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<Integer> getClassInfoCount() {
        try {
            logger.info("Getting Class Info count");
            Integer count = classInfoService.getCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            logger.error("Error getting Class Info count: ", e);
            throw e;
        }
    }
}
