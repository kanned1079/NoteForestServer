package com.example.noteforestserver.controller;

import com.example.noteforestserver.dto.CreateNewUserRequestDto;
import com.example.noteforestserver.dto.UserLoginRequestDto;
import com.example.noteforestserver.dto.UserLoginResponseDto;
import com.example.noteforestserver.model.User;
import com.example.noteforestserver.service.UserServices;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
//    private final UserServices userServices;
    private final UserServices userServices;

    public UserController(UserServices userServices) {
        this.userServices = userServices;
    }

    @GetMapping()
    public List<User> findAll() {
        return this.userServices.findAll();
    }

    @GetMapping("/{id}")
    public User findOneById(@PathVariable("id") String id) {
        return this.userServices.findById(id);
    }

    @PostMapping("/login")
    public UserLoginResponseDto authenticate(@Valid @RequestBody UserLoginRequestDto userLoginRequestDto) {
        return this.userServices.authenticate(userLoginRequestDto);
    }


    @PostMapping()
    public User createUser(@Valid @RequestBody CreateNewUserRequestDto createNewUserDto) {
        return this.userServices.create(createNewUserDto);
    }

}

