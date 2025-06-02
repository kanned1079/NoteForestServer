package com.example.noteforestserver.dto;

import com.example.noteforestserver.model.User;
import lombok.Getter;

@Getter
public class UserLoginResponseDto {
    private User user;
    private String token;

    public UserLoginResponseDto(User user, String token) {
        this.user = user;
        this.token = token;
    }
}
