package com.example.noteforestserver.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UpdateUserUsernameRequestDto {

    @NotBlank
    private String newUsername;

    public UpdateUserUsernameRequestDto(String newUsername) {
        this.newUsername = newUsername;
    }

}
