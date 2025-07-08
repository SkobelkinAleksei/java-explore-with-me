package ru.practicum.ewm.exeption;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NumberFormatException.class,
            DataIntegrityViolationException.class})
    public ResponseEntity<ApiError> handleNumberFormatException(Exception e) {
        ApiError error = ApiError.builder()
                .errors(
                        Arrays.stream(e.getStackTrace())
                                .map(StackTraceElement::toString)
                                .toList()
                ).message(e.getMessage())
                .reason("Invalid input format.")
                .status(HttpStatus.BAD_REQUEST.toString())
                .timestamp(LocalDateTime.now().toString()).build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            UserAlreadyExistsByEmail.class,
            CategoryAlreadyExists.class
    })
    public ResponseEntity<ApiError> handleEntityAlreadyExist(Exception e) {
        ApiError error = ApiError.builder()
                .errors(
                        Arrays.stream(e.getStackTrace())
                                .map(StackTraceElement::toString)
                                .toList()
                ).message(e.getMessage())
                .reason("Entity already exists.")
                .status(HttpStatus.CONFLICT.toString())
                .timestamp(LocalDateTime.now().toString()).build();

        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException e) {
        ApiError error = ApiError.builder()
                .errors(
                        Arrays.stream(e.getStackTrace())
                                .map(StackTraceElement::toString)
                                .toList()
                ).message(e.getMessage())
                .reason("Entity not found.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .timestamp(LocalDateTime.now().toString()).build();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
