package com.dinidu.user_service.controller;

import com.dinidu.user_service.dto.RegisterRequest;
import com.dinidu.user_service.entity.User;
import com.dinidu.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User user = userService.register(req.getEmail(), req.getPassword(), req.getRole());
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail(), user.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegisterRequest req) {
        User user = userService.login(req.getEmail(), req.getPassword());
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getEmail(), user.getRole().name()));
    }

    record UserResponse(Long id, String email, String role) {
    }
}