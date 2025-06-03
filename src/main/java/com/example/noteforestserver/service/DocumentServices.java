package com.example.noteforestserver.service;

import com.example.noteforestserver.dto.document.CreateDocumentRequestDto;
import com.example.noteforestserver.dto.user.UniversalApiResponseDto;
import com.example.noteforestserver.http.HttpStatus.UniversalConflictException;
import com.example.noteforestserver.model.Document;
import com.example.noteforestserver.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentServices {
    private final DocumentRepository documentRepository;

    public DocumentServices(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> findAll() {
        return this.documentRepository.findAll();
    }

    public UniversalApiResponseDto create(CreateDocumentRequestDto createDocumentRequestDto) {
        Optional<Document> existingDocument = this.documentRepository.findByTitle(createDocumentRequestDto.getTitle());
        if (existingDocument.isPresent()) {
            throw new UniversalConflictException("this document already exists");
        } else {
            Document newDocument = new Document();
            newDocument.setTitle(createDocumentRequestDto.getTitle());
            newDocument.setSubtitle(createDocumentRequestDto.getSubTitle());
            newDocument.setContent(createDocumentRequestDto.getContent());
            newDocument.setContent(createDocumentRequestDto.getContent());
            Document doc =  this.documentRepository.save(newDocument);
            return UniversalApiResponseDto.success("document created successfully").addData("doc", doc);
        }

    }
}
