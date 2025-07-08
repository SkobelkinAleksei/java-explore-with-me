package ru.practicum.statsservice.exeption;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

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
}
