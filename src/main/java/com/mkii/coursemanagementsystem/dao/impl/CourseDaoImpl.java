package com.mkii.coursemanagementsystem.dao.impl;

import com.mkii.coursemanagementsystem.dao.ICourseDao;
import com.mkii.coursemanagementsystem.entity.Course;
import com.mkii.coursemanagementsystem.hibernateDao.HibernateGenericDao;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class CourseDaoImpl extends HibernateGenericDao<Long, Course> implements ICourseDao {

    private static final Logger logger = LoggerFactory.getLogger(CourseDaoImpl.class);

    public CourseDaoImpl(EntityManager entityManager) {
        super(Course.class, entityManager);
    }

    @Override
    public Course save(Course course) {
        logger.info("Saving Course: {}", course.getTitle());
        return create(course);
    }

    @Override
    public Course update(Course course) {
        logger.info("Updating Course: {}", course.getId());
        return super.update(course);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting Course: {}", id);
        deleteByPk(id);
    }

    @Override
    public Course getById(Long id) {
        logger.info("Getting Course by id: {}", id);
        try {
            return getEntityManager().find(Course.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Course getByTitle(String title) {
        logger.info("Getting Course by title: {}", title);
        return getByField("title", title);
    }

    @Override
    public List<Course> getAll() {
        logger.info("Getting all Courses");
        try {
            TypedQuery<Course> query = getEntityManager()
                    .createQuery("SELECT c FROM Course c ORDER BY c.id DESC", Course.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Course> searchByTitle(String keyword) {
        logger.info("Searching Courses by title keyword: {}", keyword);
        try {
            TypedQuery<Course> query = getEntityManager()
                    .createQuery("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))", Course.class)
                    .setParameter("keyword", keyword);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Course> findByPriceRange(Double minPrice, Double maxPrice) {
        logger.info("Getting Courses by price range: {} - {}", minPrice, maxPrice);
        try {
            TypedQuery<Course> query = getEntityManager()
                    .createQuery("SELECT c FROM Course c WHERE c.price BETWEEN :minPrice AND :maxPrice", Course.class)
                    .setParameter("minPrice", minPrice)
                    .setParameter("maxPrice", maxPrice);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Integer getCount() {
        logger.info("Getting Course count");
        try {
            TypedQuery<Long> countQuery = getEntityManager()
                    .createQuery("SELECT COUNT(c) FROM Course c", Long.class);
            return countQuery.getSingleResult().intValue();
        } catch (NoResultException e) {
            return 0;
        }
    }
}
