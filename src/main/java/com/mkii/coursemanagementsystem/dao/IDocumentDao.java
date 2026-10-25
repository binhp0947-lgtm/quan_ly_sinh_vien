package com.mkii.coursemanagementsystem.dao;

import com.mkii.coursemanagementsystem.entity.Document;

import java.util.List;
import java.util.Optional;

public interface IDocumentDao extends IGenericDao<Long, Document> {

    Document save(Document document);

    Document update(Document document);

    void delete(Long id);

    Document getById(Long id);

    Document getActiveByName(String fileName);

    Optional<Document> findByObjectName(String objectName);

    List<Document> getAllActive();

    List<Document> getHistoryByName(String fileName);

    List<Document> searchByFileName(String keyword);

    Integer getActiveCount();
}
