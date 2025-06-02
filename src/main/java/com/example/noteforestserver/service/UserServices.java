package com.example.noteforestserver.service;

import com.example.noteforestserver.dto.CreateNewUserRequestDto;
import com.example.noteforestserver.dto.UserLoginRequestDto;
import com.example.noteforestserver.dto.UserLoginResponseDto;
import com.example.noteforestserver.http.HttpStatus.EmailAlreadyExistsException;
import com.example.noteforestserver.http.HttpStatus.EmailNotExistException;
import com.example.noteforestserver.http.HttpStatus.InvalidPasswordException;
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
            if (passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) {
                String token = JwtUtil.generateToken(user.getId().toString(), user.getEmail());
                return new UserLoginResponseDto(user, token);
            } else {
                throw new InvalidPasswordException("Invalid password for email: " + userLoginRequestDto.getEmail());
            }
        } else {
            throw new EmailNotExistException("This email does not exist: " + userLoginRequestDto.getEmail());
        }
    }

    public void delete(UUID id) {
        userRepository.deleteById(id);

    }

}
