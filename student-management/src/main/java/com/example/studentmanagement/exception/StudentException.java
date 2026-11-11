package com.example.studentmanagement.exception;

/** Unchecked exception carrying a user-friendly message for database problems. */
public class StudentException extends RuntimeException {
    public StudentException(String message, Throwable cause) {
        super(message, cause);
    }
}
