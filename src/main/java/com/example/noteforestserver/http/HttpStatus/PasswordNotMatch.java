package com.example.noteforestserver.http.HttpStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class PasswordNotMatch extends RuntimeException{
    public PasswordNotMatch(String message) {
        super(message);
    }
}
