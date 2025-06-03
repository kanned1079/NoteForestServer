package com.example.noteforestserver.controller;

import com.example.noteforestserver.dto.*;
import com.example.noteforestserver.dto.user.*;
import com.example.noteforestserver.model.User;
import com.example.noteforestserver.service.UserServices;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
//    private final UserServices userServices;
    private final UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public List<User> findAll() {
        return this.userServices.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public User findOneById(@PathVariable("id") String id) {
        return this.userServices.findById(id);
    }

    @PostMapping("/login")
    public UserLoginResponseDto authenticate(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto) {
        return this.userServices.authenticate(userLoginRequestDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/password/update/{id}")
    public UniversalApiResponseDto updateUserPassword(@PathVariable("id") String id, @Valid @RequestBody UpdateUserPasswordRequestDto updateUserPasswordRequestDto) {
        String currentUserIdFromToken = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.userServices.resetUserPasswordById(currentUserIdFromToken, id, updateUserPasswordRequestDto);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/username/update/{id}")
    public UniversalApiResponseDto updateUsernameById(@PathVariable("id") String id, @Valid @RequestBody UpdateUserUsernameRequestDto updateUserUsernameRequestDto) {
        String currentUserIdFromToken = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.userServices.updateUsernameById(currentUserIdFromToken, id, updateUserUsernameRequestDto.getNewUsername());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/avatar")
    public SaveUserAvatarResponseDto uploadAvatar(@RequestParam("file")MultipartFile file) {
        return this.userServices.uploadUserAvatar(file);
    }


    @PostMapping()
    public User createUser(@Valid @RequestBody CreateNewUserRequestDto createNewUserDto) {
        return this.userServices.create(createNewUserDto);
    }

}

