package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.comment.CommentByAdminDto;
import ru.practicum.ewm.model.comment.CommentDto;
import ru.practicum.ewm.model.comment.CommentEntity;
import ru.practicum.ewm.model.comment.NewCommentDto;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public CommentEntity toEntity(NewCommentDto newCommentDto, Long userId, Long eventId) {
        return CommentEntity.builder()
                .userId(userId)
                .eventId(eventId)
                .message(newCommentDto.getMessage())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public CommentDto toDto(
            String userName,
            CommentEntity savedCommentEntity
    ) {
       return CommentDto.builder()
                .userName(userName)
                .message(savedCommentEntity.getMessage())
                .createdAt(savedCommentEntity.getCreatedAt())
                .build();
    }

    public CommentByAdminDto toAdminDto(
            String userName,
            CommentEntity commentEntity
    ) {
        return CommentByAdminDto.builder()
                .commentId(commentEntity.getId())
                .userName(userName)
                .message(commentEntity.getMessage())
                .createdAt(commentEntity.getCreatedAt())
                .build();
    }
}