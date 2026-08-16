package com.dinidu.user_service.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        // Not-found lookups: getUserById, login (bad email)
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, Object>> handleNotFound(IllegalArgumentException ex) {
                return build(HttpStatus.NOT_FOUND, ex.getMessage());
        }

        // Business-rule conflicts: duplicate email on register/update
        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException ex) {
                return build(HttpStatus.CONFLICT, ex.getMessage());
        }

        // Database constraint violations
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
                return build(HttpStatus.CONFLICT, "Database constraint violation");
        }

        // Invalid JSON request body
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<Map<String, Object>> handleInvalidRequest(HttpMessageNotReadableException ex) {
                return build(HttpStatus.BAD_REQUEST, "Invalid request body");
        }

        // Bean validation failures
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
                String message = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .findFirst()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .orElse("Validation failed");
                return build(HttpStatus.BAD_REQUEST, message);
        }

        private ResponseEntity<Map<String, Object>> build(HttpStatus status, String message) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("timestamp", Instant.now().toString());
                body.put("status", status.value());
                body.put("error", status.getReasonPhrase());
                body.put("message", message);
                return ResponseEntity.status(status).body(body);
        }
}