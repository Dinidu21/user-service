package com.dinidu.user_service.controller;

import com.dinidu.user_service.dto.RegisterRequest;
import com.dinidu.user_service.entity.User;
import com.dinidu.user_service.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        User user = userService.register(
                req.getEmail(),
                req.getPassword(),
                req.getRole());

        return ResponseEntity.ok(toResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RegisterRequest req) {
        User user = userService.login(
                req.getEmail(),
                req.getPassword());

        return ResponseEntity.ok(toResponse(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                userService.getAllUsers()
                        .stream()
                        .map(this::toResponse)
                        .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(
                toResponse(userService.getUserById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest req) {

        User user = userService.updateUser(
                id,
                req.email(),
                req.role());

        return ResponseEntity.ok(toResponse(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name());
    }

    record UserResponse(Long id, String email, String role) {
    }

    record UpdateUserRequest(String email, String role) {
    }
}