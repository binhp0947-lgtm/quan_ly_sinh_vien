package com.mkii.coursemanagementsystem.dao.impl;

import com.mkii.coursemanagementsystem.dao.IClassInfoDao;
import com.mkii.coursemanagementsystem.entity.ClassInfo;
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
public class ClassInfoDaoImpl extends HibernateGenericDao<Long, ClassInfo> implements IClassInfoDao {

    private static final Logger logger = LoggerFactory.getLogger(ClassInfoDaoImpl.class);

    public ClassInfoDaoImpl(EntityManager entityManager) {
        super(ClassInfo.class, entityManager);
    }

    @Override
    public ClassInfo save(ClassInfo classInfo) {
        logger.info("Saving ClassInfo: {}", classInfo.getClassName());
        return create(classInfo);
    }

    @Override
    public ClassInfo update(ClassInfo classInfo) {
        logger.info("Updating ClassInfo: {}", classInfo.getId());
        return super.update(classInfo);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting ClassInfo: {}", id);
        deleteByPk(id);
    }

    @Override
    public ClassInfo getById(Long id) {
        logger.info("Getting ClassInfo by id: {}", id);
        try {
            return getEntityManager().find(ClassInfo.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public ClassInfo getByCode(String classCode) {
        logger.info("Getting ClassInfo by code: {}", classCode);
        return getByField("classCode", classCode);
    }

    @Override
    public Optional<ClassInfo> findByCode(String classCode) {
        logger.info("Getting ClassInfo optional by code: {}", classCode);
        return Optional.ofNullable(getByField("classCode", classCode));
    }

    @Override
    public List<ClassInfo> getAll() {
        logger.info("Getting all ClassInfo");
        try {
            TypedQuery<ClassInfo> query = getEntityManager()
                    .createQuery("SELECT c FROM ClassInfo c LEFT JOIN FETCH c.department ORDER BY c.id DESC", ClassInfo.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ClassInfo> getByDepartmentId(Long departmentId) {
        logger.info("Getting ClassInfo by departmentId: {}", departmentId);
        try {
            TypedQuery<ClassInfo> query = getEntityManager()
                    .createQuery("SELECT c FROM ClassInfo c LEFT JOIN FETCH c.department WHERE c.department.id = :deptId ORDER BY c.id DESC", ClassInfo.class)
                    .setParameter("deptId", departmentId);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ClassInfo> searchByKeyword(String keyword) {
        logger.info("Searching ClassInfo by keyword: {}", keyword);
        try {
            TypedQuery<ClassInfo> query = getEntityManager()
                    .createQuery("SELECT c FROM ClassInfo c LEFT JOIN FETCH c.department WHERE LOWER(c.className) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.classCode) LIKE LOWER(CONCAT('%', :keyword, '%'))", ClassInfo.class)
                    .setParameter("keyword", keyword);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Integer getCount() {
        logger.info("Getting ClassInfo count");
        try {
            TypedQuery<Long> countQuery = getEntityManager()
                    .createQuery("SELECT COUNT(c) FROM ClassInfo c", Long.class);
            return countQuery.getSingleResult().intValue();
        } catch (NoResultException e) {
            return 0;
        }
    }
}
