package com.example.noteforestserver.service;

import com.example.noteforestserver.dto.*;
import com.example.noteforestserver.dto.user.*;
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
//    private static final String AVATAR_UPLOAD_DIR = "/Users/kanna/IdeaProjects/avatars";

    private final String currentDir = System.getProperty("user.dir") + "/local";

    private final String avatarSaveDir = currentDir + "/avatars";

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

    protected boolean authPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public UserLoginResponseDto authenticate(UserLoginRequestDto userLoginRequestDto) {
        Optional<User> existingUser = this.userRepository.findByEmail(userLoginRequestDto.getEmail());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (this.authPassword(userLoginRequestDto.getPassword(), user.getPassword())) {
                String token = JwtUtil.generateToken(user.getId().toString(), user.getEmail(), user.getRole());
                return new UserLoginResponseDto(user, token);
            } else {
                throw new InvalidPasswordException("Invalid password for email: " + userLoginRequestDto.getEmail());
            }
        } else {
            throw new EmailNotExistException("This email does not exist: " + userLoginRequestDto.getEmail());
        }
    }

    public UniversalApiResponseDto resetUserPasswordById(String userIdFromToken, String uuid, UpdateUserPasswordRequestDto dto) {
        UUID userUuid = UUID.fromString(uuid);
        if (!userIdFromToken.equals(uuid)) {
            throw new UniversalConflictException("you cannot act on behalf of others");
        }

        User user = this.userRepository.findById(userUuid)
                .orElseThrow(() -> new UserNotFound("user not found for: " + userUuid));

        if (!authPassword(dto.getOldPassword(), user.getPassword())) {
            throw new PasswordNotMatch("Old password incorrect, please try again");
        }
        if (authPassword(dto.getNewPassword(), user.getPassword())) {
            throw new UniversalConflictException("New password must be different from old password");
        }
        String encodedNewPassword = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(encodedNewPassword);
        userRepository.save(user);

        return new UniversalApiResponseDto(true, "Password updated successfully");
    }

    public SaveUserAvatarResponseDto uploadUserAvatar(MultipartFile file) {
        System.out.println("file");
        if (file == null || file.isEmpty()) {
            return new SaveUserAvatarResponseDto(false, "empty file", "");
        }
        File uploadDir = new File(this.avatarSaveDir);
        if (!uploadDir.exists()) {
            boolean created =  uploadDir.mkdirs();
            if (!created) {
                throw new RuntimeException("mkdirs failed");
            }
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = originalFilename.substring(dotIndex); // 包含点号
            }

            String filename = UUID.randomUUID().toString().replace("-", "") + "_" + System.currentTimeMillis() + extension;
            Path filePath = Paths.get(this.avatarSaveDir, filename);

            file.transferTo(filePath.toFile()); // 保存文件
            String imageUrl = "/public/images/avatars/" + filename;
            return new SaveUserAvatarResponseDto(true, "上传成功", imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return new SaveUserAvatarResponseDto(false, "上传失败：" + e.getMessage(), null);
        }
    }

    public UniversalApiResponseDto updateUsernameById(String uuidFromToken, String uuidStr, String newUsername) {
        if (!uuidFromToken.equals(uuidStr)) {
            throw new UniversalConflictException("you cannot act on behalf of others");
        }
        Optional<User> existingUser = this.userRepository.findById(UUID.fromString(uuidStr));
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getUsername().equals(newUsername)) {
                throw new UniversalConflictException("new username must be different");
            } else {
                user.setUsername(newUsername.trim());
                userRepository.save(user);
                return UniversalApiResponseDto.success("update username successfully").addData("username", user.getUsername());
            }
        } else {
            throw new UserNotFound("user not found for: " + uuidStr);
        }
    }


    public void delete(UUID id) {
        userRepository.deleteById(id);

    }

}
