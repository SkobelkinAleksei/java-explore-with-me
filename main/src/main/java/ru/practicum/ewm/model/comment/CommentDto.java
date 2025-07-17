package ru.practicum.ewm.model.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentDto(
        String userName,
        String message,
        LocalDateTime createdAt
) {
}
