package com.vcare4u.authservice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.vcare4u.authservice.exception.ResourceNotFoundException;

class ResourceNotFoundExceptionTest {

    @Test
    void testExceptionMessage() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        assertEquals(message, exception.getMessage());
    }

    @Test
    void testExceptionIsRuntimeException() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Test");

        assertTrue(exception instanceof RuntimeException);
    }
}
