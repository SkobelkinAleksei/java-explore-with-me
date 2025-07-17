package ru.practicum.ewm.model.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentByAdminDto(
    Long commentId,
    String userName,
    String message,
    LocalDateTime createdAt
    ) {
}