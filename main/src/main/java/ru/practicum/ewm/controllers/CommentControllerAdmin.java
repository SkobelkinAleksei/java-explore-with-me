package ru.practicum.ewm.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.comment.CommentByAdminDto;
import ru.practicum.ewm.service.CommentService;

@Slf4j
@RestController
@RequestMapping("/admin/events/comments/{commentId}")
@RequiredArgsConstructor
public class CommentControllerAdmin {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<CommentByAdminDto> findComment(@PathVariable Long commentId) {
        log.info("Поступил запрос на получение комментария по id: {}", commentId);
        return ResponseEntity.ok().body(commentService.findCommentById(commentId));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteCommentByAdmin(
            @PathVariable Long commentId
    ) {
        log.info("Поступил запрос на удаление комментария по id: {} с правами Админа", commentId);
        commentService.deleteCommentByAdmin(commentId);
        return ResponseEntity.noContent().build();
    }
}