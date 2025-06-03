package com.example.noteforestserver.service;

import com.example.noteforestserver.dto.*;
import com.example.noteforestserver.http.HttpStatus.*;
import com.example.noteforestserver.model.User;
import com.example.noteforestserver.repository.UserRepository;
import com.example.noteforestserver.utils.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class UserServices {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String AVATAR_UPLOAD_DIR = "/Users/kanna/IdeaProjects/avatars";

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

    public SaveUserAvatarResponseDto uploadUserAvatar(MultipartFile file) {
        System.out.println("file");
        if (file == null || file.isEmpty()) {
            return new SaveUserAvatarResponseDto(false, "empty file", "");
        }
        // 创建目录（如果不存在）
        File uploadDir = new File(AVATAR_UPLOAD_DIR);
        if (!uploadDir.exists()) {
            boolean created =  uploadDir.mkdirs();
            if (!created) {
                throw new RuntimeException("mkdirs failed");
            }
        }
        try {
            // 给文件名加时间戳避免重复（可用 UUID 更安全）
            String originalFilename = file.getOriginalFilename();
            String filename = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = Paths.get(AVATAR_UPLOAD_DIR, filename);

            // 保存文件
            file.transferTo(filePath.toFile());

            // 构造访问路径，例如：/images/xxx.jpg（你配置静态资源映射后）
            String imageUrl = "/public/images/" + filename;

            return new SaveUserAvatarResponseDto(true, "上传成功", imageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return new SaveUserAvatarResponseDto(false, "上传失败：" + e.getMessage(), null);
        }
    }

    public void delete(UUID id) {
        userRepository.deleteById(id);

    }

}
