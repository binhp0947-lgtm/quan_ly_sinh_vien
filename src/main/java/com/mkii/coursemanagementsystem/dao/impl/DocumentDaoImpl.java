package com.mkii.coursemanagementsystem.dao.impl;

import com.mkii.coursemanagementsystem.dao.IDocumentDao;
import com.mkii.coursemanagementsystem.entity.Document;
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
public class DocumentDaoImpl extends HibernateGenericDao<Long, Document> implements IDocumentDao {

    private static final Logger logger = LoggerFactory.getLogger(DocumentDaoImpl.class);

    public DocumentDaoImpl(EntityManager entityManager) {
        super(Document.class, entityManager);
    }

    @Override
    public Document save(Document document) {
        logger.info("Saving Document: {}", document.getOriginalFileName());
        return create(document);
    }

    @Override
    public Document update(Document document) {
        logger.info("Updating Document ID: {}", document.getId());
        return super.update(document);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting Document ID: {}", id);
        deleteByPk(id);
    }

    @Override
    public Document getById(Long id) {
        logger.info("Getting Document by id: {}", id);
        try {
            return getEntityManager().find(Document.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Document getActiveByName(String fileName) {
        logger.info("Getting active document by fileName: {}", fileName);
        try {
            TypedQuery<Document> query = getEntityManager()
                    .createQuery("SELECT d FROM Document d WHERE d.originalFileName = :fileName AND d.active = true AND d.deleted = false", Document.class)
                    .setParameter("fileName", fileName);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Optional<Document> findByObjectName(String objectName) {
        logger.info("Finding Document by objectName: {}", objectName);
        return Optional.ofNullable(getByField("objectName", objectName));
    }

    @Override
    public List<Document> getAllActive() {
        logger.info("Getting all active documents");
        try {
            TypedQuery<Document> query = getEntityManager()
                    .createQuery("SELECT d FROM Document d WHERE d.active = true AND d.deleted = false ORDER BY d.id DESC", Document.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Document> getHistoryByName(String fileName) {
        logger.info("Getting document version history for fileName: {}", fileName);
        try {
            TypedQuery<Document> query = getEntityManager()
                    .createQuery("SELECT d FROM Document d WHERE d.originalFileName = :fileName AND d.deleted = false ORDER BY d.version DESC", Document.class)
                    .setParameter("fileName", fileName);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Document> searchByFileName(String keyword) {
        logger.info("Searching documents by keyword: {}", keyword);
        try {
            TypedQuery<Document> query = getEntityManager()
                    .createQuery("SELECT d FROM Document d WHERE d.deleted = false AND LOWER(d.originalFileName) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY d.id DESC", Document.class)
                    .setParameter("keyword", keyword);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Integer getActiveCount() {
        logger.info("Getting active documents count");
        try {
            TypedQuery<Long> query = getEntityManager()
                    .createQuery("SELECT COUNT(d) FROM Document d WHERE d.active = true AND d.deleted = false", Long.class);
            return query.getSingleResult().intValue();
        } catch (NoResultException e) {
            return 0;
        }
    }
}
