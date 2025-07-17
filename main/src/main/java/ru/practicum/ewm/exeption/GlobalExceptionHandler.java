package ru.practicum.ewm.exeption;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.zip.DataFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            NumberFormatException.class,
            DataIntegrityViolationException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class,
            ValidationException.class,
            DataFormatException.class,
    })
    public ResponseEntity<ApiError> handleNumberFormatException(Exception e) {
        ApiError error = ApiError.builder()
                .errors(
                        Arrays.stream(e.getStackTrace())
                                .map(StackTraceElement::toString)
                                .toList()
                ).message(e.getMessage())
                .reason("BAD_REQUEST")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now().toString()).build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            ForbiddenException.class,
            UserAlreadyExistsByEmail.class,
            CategoryAlreadyExists.class
    })
    public ResponseEntity<ApiError> handleForbiddenException(Exception e) {
        ApiError error = ApiError.builder()
                .errors(
                        Arrays.stream(e.getStackTrace())
                                .map(StackTraceElement::toString)
                                .toList()
                ).message(e.getMessage())
                .reason("FORBIDDEN")
                .status(HttpStatus.CONFLICT.toString())
                .timestamp(LocalDateTime.now().toString()).build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleForbiddenException(EntityNotFoundException e) {
        ApiError error = ApiError.builder()
                .errors(
                        Arrays.stream(e.getStackTrace())
                                .map(StackTraceElement::toString)
                                .toList()
                ).message(e.getMessage())
                .reason("NOT_FOUND")
                .status(HttpStatus.NOT_FOUND.toString())
                .timestamp(LocalDateTime.now().toString()).build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}