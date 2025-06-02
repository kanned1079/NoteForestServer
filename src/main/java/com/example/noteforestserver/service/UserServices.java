package com.example.noteforestserver.service;

import com.example.noteforestserver.dto.*;
import com.example.noteforestserver.http.HttpStatus.*;
import com.example.noteforestserver.model.User;
import com.example.noteforestserver.repository.UserRepository;
import com.example.noteforestserver.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServices {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServices(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(String id) {
        UUID uuid = UUID.fromString(id);
        return userRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("user not exist"));
    }

    @Transactional
    public User create(CreateNewUserRequestDto createNewUserDto) {
        Optional<User> existingUser = this.userRepository.findByEmail(createNewUserDto.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailAlreadyExistsException("This email already exists: " + createNewUserDto.getEmail());
        }

        User user = new User();
        user.setEmail(createNewUserDto.getEmail());
        String encodedPassword = passwordEncoder.encode(createNewUserDto.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public UserLoginResponseDto authenticate(UserLoginRequestDto userLoginRequestDto) {
        Optional<User> existingUser = this.userRepository.findByEmail(userLoginRequestDto.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (this.authPassword(userLoginRequestDto.getPassword(), user.getPassword())) {
                String token = JwtUtil.generateToken(user.getId().toString(), user.getEmail());
                return new UserLoginResponseDto(user, token);
            } else {
                throw new InvalidPasswordException("Invalid password for email: " + userLoginRequestDto.getEmail());
            }
        } else {
            throw new EmailNotExistException("This email does not exist: " + userLoginRequestDto.getEmail());
        }
    }

    private boolean authPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public UniversalApiResponseDto resetUserPasswordById(String uuid, UpdateUserPasswordRequestDto updateUserPasswordRequestDto) {
        UUID userUuid = UUID.fromString(uuid);
        Optional<User> existingUser = this.userRepository.findById(userUuid);
        if (existingUser.isPresent()) {
            if (this.authPassword(updateUserPasswordRequestDto.getOldPassword(), existingUser.get().getPassword())) {
                User user = existingUser.get();
                String newEncodedPassword = passwordEncoder.encode(updateUserPasswordRequestDto.getNewPassword());
                user.setPassword(newEncodedPassword);
                userRepository.save(user);
                return new UniversalApiResponseDto(true, "updated successfully");
            } else {
                throw new PasswordNotMatch("password not match, please try again");
            }
        } else {
            throw new UserNotFound("user not found for: " + userUuid);
        }
    }

    public void delete(UUID id) {
        userRepository.deleteById(id);

    }

}
