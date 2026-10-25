package com.mkii.coursemanagementsystem.service.impl;

import com.mkii.coursemanagementsystem.common.MessageConstants;
import com.mkii.coursemanagementsystem.common.exception.ResourceNotFoundException;
import com.mkii.coursemanagementsystem.dao.IStudentDao;
import com.mkii.coursemanagementsystem.dto.StudentRequestDTO;
import com.mkii.coursemanagementsystem.dto.StudentResponseDTO;
import com.mkii.coursemanagementsystem.entity.Student;
import com.mkii.coursemanagementsystem.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    @Autowired
    private IStudentDao studentDao;

    @Override
    public Student create(StudentRequestDTO requestDTO) {
        logger.info("Creating Student: {}", requestDTO.getFullName());
        Student student = new Student();
        mapRequestToEntity(requestDTO, student);
        return studentDao.save(student);
    }

    @Override
    public Student update(StudentRequestDTO requestDTO, Long id) {
        logger.info("Updating Student: {}", id);
        Student existing = studentDao.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.STUDENT_NOT_FOUND, id));
        }
        mapRequestToEntity(requestDTO, existing);
        return studentDao.update(existing);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting Student: {}", id);
        Student existing = studentDao.getById(id);
        if (existing == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.STUDENT_NOT_FOUND, id));
        }
        studentDao.delete(id);
    }

    @Override
    public StudentResponseDTO getById(Long id) {
        logger.info("Getting Student by id: {}", id);
        Student student = studentDao.getById(id);
        if (student == null) {
            throw new ResourceNotFoundException(String.format(MessageConstants.STUDENT_NOT_FOUND, id));
        }
        return mapToDTO(student);
    }

    @Override
    public List<StudentResponseDTO> getAll() {
        logger.info("Getting all Students");
        return studentDao.getAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StudentResponseDTO> searchByKeyword(String keyword) {
        logger.info("Searching Students by keyword: {}", keyword);
        return studentDao.searchByKeyword(keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudentResponseDTO getByEmail(String email) {
        logger.info("Getting Student by email: {}", email);
        Student student = studentDao.getByEmail(email);
        if (student == null) {
            throw new ResourceNotFoundException("Không tìm thấy sinh viên với email: " + email);
        }
        return mapToDTO(student);
    }

    @Override
    public StudentResponseDTO getByField(String field, Object value) {
        logger.info("Getting Student by field: {} = {}", field, value);
        Student student = studentDao.getByField(field, value);
        if (student == null) {
            throw new ResourceNotFoundException("Không tìm thấy sinh viên với " + field + ": " + value);
        }
        return mapToDTO(student);
    }

    @Override
    public Integer getCount() {
        logger.info("Getting Student count");
        return studentDao.getCount();
    }

    // ---- private helpers ----

    private void mapRequestToEntity(StudentRequestDTO requestDTO, Student student) {
        if (requestDTO.getFullName() != null) {
            student.setFullName(requestDTO.getFullName());
        }
        if (requestDTO.getEmail() != null) {
            student.setEmail(requestDTO.getEmail());
        }
        if (requestDTO.getPhone() != null) {
            student.setPhone(requestDTO.getPhone());
        }
        if (requestDTO.getAddress() != null) {
            student.setAddress(requestDTO.getAddress());
        }
    }

    private StudentResponseDTO mapToDTO(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getFullName(),
                student.getEmail(),
                student.getPhone(),
                student.getAddress()
        );
    }
}
