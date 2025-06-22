package ru.practicum.statsservice.exeption;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResponseError(String message,
                           String cause,
                           LocalDateTime timeStamp) {
}
