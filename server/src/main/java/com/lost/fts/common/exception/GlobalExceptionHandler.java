package com.lost.fts.common.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<Map<String, String>> handleBadRequest(
                        IllegalArgumentException ex) {

                return ResponseEntity.badRequest()
                                .body(Map.of("message", ex.getMessage()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidation(
                        MethodArgumentNotValidException ex) {

                String message = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getField() + ": "
                                                + error.getDefaultMessage())
                                .collect(Collectors.joining(", "));

                return ResponseEntity.badRequest()
                                .body(Map.of("message", message));
        }

        @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
        public ResponseEntity<Map<String, String>> handleAuthentication(
                        org.springframework.security.core.AuthenticationException ex) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of(
                                                "message",
                                                "Invalid credentials or account unavailable"));
        }
}