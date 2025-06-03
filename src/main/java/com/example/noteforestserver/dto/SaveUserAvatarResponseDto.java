package com.example.noteforestserver.dto;

import lombok.Getter;

@Getter
public class SaveUserAvatarResponseDto {
    private boolean success;
    private String message;
    private String url;

    public SaveUserAvatarResponseDto(boolean success, String message, String url) {
        this.success = success;
        this.message = message;
        this.url = url;
    }
}
