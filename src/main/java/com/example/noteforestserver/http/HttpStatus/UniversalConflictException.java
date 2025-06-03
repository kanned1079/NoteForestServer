package com.example.noteforestserver.http.HttpStatus;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UniversalConflictException extends RuntimeException{
    public UniversalConflictException(String message) {
        super(message);
    }
}
