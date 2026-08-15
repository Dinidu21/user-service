package com.dinidu.user_service.service;

import com.dinidu.user_service.entity.User;
import com.dinidu.user_service.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String email, String rawPassword, String role) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email already registered");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(rawPassword));
        user.setRole(User.Role.valueOf(role.toUpperCase()));

        return userRepository.save(user);
    }

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!encoder.matches(rawPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User updateUser(Long id, String email, String role) {
        User user = getUserById(id);

        if (email != null && !email.isBlank() && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new IllegalStateException("Email already registered");
            }
            user.setEmail(email);
        }

        if (role != null && !role.isBlank()) {
            user.setRole(User.Role.valueOf(role.toUpperCase()));
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}