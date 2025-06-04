package com.example.noteforestserver.http.HttpStatus;

import com.example.noteforestserver.dto.user.UniversalApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UniversalNotFoundException extends RuntimeException{
    public UniversalNotFoundException(String message) {
        super(message);
    }
}
