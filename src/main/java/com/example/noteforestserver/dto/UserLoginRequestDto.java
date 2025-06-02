package com.example.noteforestserver.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Getter
public class UserLoginRequestDto {
    @Email(message = "email format not valid")
    @NotBlank(message = "email field blank is not allowed")
    private String email;

    @NotBlank(message = "password field blank is not allowed")
    private String password;
}