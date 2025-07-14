package ru.practicum.statsservice.exeption;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.zip.DataFormatException;

@RestControllerAdvice
public class StatsServerExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseError> internalServerError(Exception e) {
        Throwable cause = e.getCause();
        String causeMessage = cause != null ? cause.getMessage() : "Неверный запрос";

        ResponseError responseError = ResponseError.builder()
                .message(e.getMessage())
                .cause(causeMessage)
                .timeStamp(LocalDateTime.now())
                .build();

        return ResponseEntity.internalServerError().body(responseError);
    }

    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<ResponseError> handleDateTimeException(DateTimeException e) {
        ResponseError responseError = ResponseError.builder()
                .message("Некорректный формат даты")
                .cause(e.getMessage())
                .timeStamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(responseError);
    }
}
