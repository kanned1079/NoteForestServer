package com.example.noteforestserver.repository;

import com.example.noteforestserver.model.Document;
import com.example.noteforestserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
//    Optional<User> findByEmail(String email);
    Optional<Document> findByTitle(String title);
}