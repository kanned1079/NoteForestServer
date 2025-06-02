package com.example.noteforestserver.dto;

import lombok.Getter;

@Getter
public class UniversalApiResponseDto {
    private boolean success;
    private String message;

    public UniversalApiResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

}
