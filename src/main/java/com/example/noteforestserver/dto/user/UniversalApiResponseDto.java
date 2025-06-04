package com.example.noteforestserver.dto.user;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class UniversalApiResponseDto {
    private boolean success;
    private String message;
    private final Map<String, Object> data = new HashMap<>();

    public UniversalApiResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
//        this.data = data;
    }

    public UniversalApiResponseDto addData(String key, Object value) {
        data.put(key, value);
        return this;
    }

    public static UniversalApiResponseDto success(String message) {
        return new UniversalApiResponseDto(true, message);
    }

}
