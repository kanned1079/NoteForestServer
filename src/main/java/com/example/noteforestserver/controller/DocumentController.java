package com.example.noteforestserver.controller;

import com.example.noteforestserver.dto.document.CreateDocumentRequestDto;
import com.example.noteforestserver.dto.document.UpdateDocumentByIdRequestDto;
import com.example.noteforestserver.dto.user.UniversalApiResponseDto;
import com.example.noteforestserver.model.Document;
import com.example.noteforestserver.service.DocumentServices;
import jakarta.validation.Valid;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/document")
public class DocumentController {
    private final DocumentServices documentServices;
    public DocumentController(DocumentServices documentServices) {
        this.documentServices = documentServices;
    }

    @GetMapping
    public List<Document> findAll() {
        return this.documentServices.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UniversalApiResponseDto createNewDocument(@Valid @RequestBody CreateDocumentRequestDto createDocumentRequestDto) {
        return this.documentServices.create(createDocumentRequestDto);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UniversalApiResponseDto updateDocById(@PathVariable String id, @Valid @RequestBody UpdateDocumentByIdRequestDto updateDocumentByIdRequestDto) {
        return this.documentServices.updateDocumentById(updateDocumentByIdRequestDto);
    }

}
