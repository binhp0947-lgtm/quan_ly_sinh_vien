package com.mkii.coursemanagementsystem.dao.impl;

import com.mkii.coursemanagementsystem.dao.IDepartmentDao;
import com.mkii.coursemanagementsystem.entity.Department;
import com.mkii.coursemanagementsystem.hibernateDao.HibernateGenericDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class DepartmentDaoImpl extends HibernateGenericDao<Long, Department> implements IDepartmentDao {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentDaoImpl.class);

    public DepartmentDaoImpl(EntityManager entityManager) {
        super(Department.class, entityManager);
    }

    @Override
    public Department save(Department department) {
        logger.info("Saving Department: {}", department.getDepartmentName());
        return create(department);
    }

    @Override
    public Department update(Department department) {
        logger.info("Updating Department: {}", department.getId());
        return super.update(department);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting Department: {}", id);
        deleteByPk(id);
    }

    @Override
    public Department getById(Long id) {
        logger.info("Getting Department by id: {}", id);
        try {
            return getEntityManager().find(Department.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Department getByCode(String departmentCode) {
        logger.info("Getting Department by code: {}", departmentCode);
        return getByField("departmentCode", departmentCode);
    }

    @Override
    public Optional<Department> findByCode(String departmentCode) {
        logger.info("Getting Department optional by code: {}", departmentCode);
        return Optional.ofNullable(getByField("departmentCode", departmentCode));
    }

    @Override
    public List<Department> getAll() {
        logger.info("Getting all Departments");
        try {
            TypedQuery<Department> query = getEntityManager()
                    .createQuery("SELECT d FROM Department d ORDER BY d.id DESC", Department.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Department> searchByKeyword(String keyword) {
        logger.info("Searching Departments by keyword: {}", keyword);
        try {
            TypedQuery<Department> query = getEntityManager()
                    .createQuery("SELECT d FROM Department d WHERE LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.departmentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))", Department.class)
                    .setParameter("keyword", keyword);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Integer getCount() {
        logger.info("Getting Department count");
        try {
            TypedQuery<Long> countQuery = getEntityManager()
                    .createQuery("SELECT COUNT(d) FROM Department d", Long.class);
            return countQuery.getSingleResult().intValue();
        } catch (NoResultException e) {
            return 0;
        }
    }
}
