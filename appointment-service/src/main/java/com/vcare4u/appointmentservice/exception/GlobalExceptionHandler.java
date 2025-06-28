package com.vcare4u.appointmentservice.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignException(FeignException ex, WebRequest request) {
        return buildResponse("Service communication failed: " + ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        return buildResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<?> buildResponse(String message, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(body, status);
    }
}
