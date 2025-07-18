package ru.practicum.ewm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.comment.CommentDto;
import ru.practicum.ewm.model.comment.NewCommentDto;
import ru.practicum.ewm.model.comment.UpdateCommentDto;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class CommentControllerPrivate {
    private final CommentService commentService;

    @GetMapping("/{eventId}/comments")
    public ResponseEntity<List<CommentDto>> getComments(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(
                commentService.findAllCommentsForEventByUserId(userId, eventId)
        );
    }

    @PostMapping("/{eventId}/comments")
    public ResponseEntity<CommentDto> createComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid NewCommentDto newCommentDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                commentService.sendComment(userId, eventId, newCommentDto)
        );
    }

    @PatchMapping("/{eventId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @RequestBody @Valid UpdateCommentDto updateCommentDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                commentService.updateCommentById(userId, eventId, commentId, updateCommentDto)
        );
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    public ResponseEntity<HttpStatus> deleteComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(userId, eventId, commentId);
        return ResponseEntity.noContent().build();
    }
}