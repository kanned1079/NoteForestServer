package com.example.noteforestserver.dto.document;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateDocumentByIdRequestDto {
    @NotBlank
    private String title;

    private String subTitle;

    private String category;

    @NotBlank
    private String content;

    public UpdateDocumentByIdRequestDto(String title, String subTitle, String category, String content) {
        this.title = title;
        this.subTitle = subTitle;
        this.category = category;
        this.content = content;
    }

}
