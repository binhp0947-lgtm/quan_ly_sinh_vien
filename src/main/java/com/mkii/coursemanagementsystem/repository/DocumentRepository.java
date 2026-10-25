package com.mkii.coursemanagementsystem.repository;

import com.mkii.coursemanagementsystem.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    Optional<Document> findByObjectName(String objectName);
}
