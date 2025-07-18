package ru.practicum.ewm.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.comment.*;
import ru.practicum.ewm.model.event.State;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.utils.DefaultMessagesForException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CommentDto sendComment(
            Long userId,
            Long eventId,
            NewCommentDto newCommentDto
    ) throws NumberFormatException {
        log.info("Получили запрос на комментарий {}, {}, {}", userId, eventId, newCommentDto);
        if (!userRepository.isUserExistsById(userId))
            throw new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND);

        if (!eventRepository.existsById(eventId))
            throw new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_FOUND);

        if (!eventRepository.getEventStatusByEventId(eventId).equals(State.PUBLISHED))
            throw new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_PUBLICISED);

        String userName = userRepository.userNameById(userId);

        CommentEntity savedCommentEntity = commentRepository
                .save(CommentMapper.toEntity(newCommentDto, userId, eventId)
                );
        log.info("Сохраненный комментарий {}", savedCommentEntity);

        return CommentMapper.toDto(userName, savedCommentEntity);
    }

    @Transactional(readOnly = true)
    public CommentByAdminDto findCommentById(Long commentId) throws NumberFormatException {
        log.info("Получили запрос на получение комментария по id {}", commentId);
        CommentEntity commentEntity = commentRepository.findCommentEntityById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(DefaultMessagesForException.COMMENT_NOT_FOUND));

        String userName = Optional.ofNullable(
                userRepository.userNameById(commentEntity.getUserId())).orElse("Имя не найдено"
        );

        log.info("Найден комментарий: {}", commentEntity);
        return CommentMapper.toAdminDto(userName, commentEntity);
    }

    @Transactional
    public CommentDto updateCommentById(
            Long userId,
            Long eventId,
            Long commentId,
            UpdateCommentDto updateCommentDto
    ) throws NumberFormatException {
        log.info("Получили запрос на обновление комментария {}, {}, {}, {}",
                userId, eventId, commentId, updateCommentDto);

        checkIsValidData(userId, eventId);

        CommentEntity commentEntity = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException(DefaultMessagesForException.COMMENT_NOT_FOUND));

        if (nonNull(updateCommentDto)) {
            if (nonNull(updateCommentDto.message())) {
                log.info("Обновление сообщения комментария с '{}'' на '{}'", commentEntity.getMessage(), updateCommentDto.message());
                commentEntity.setMessage(updateCommentDto.message());
                commentEntity.setUpdatedAt(LocalDateTime.now());
            }
        }

        CommentEntity updatedComment = commentRepository.save(commentEntity);
        log.info("Обновленный комментарий {}", updatedComment);

        String userName = Optional.ofNullable(
                userRepository.userNameById(commentEntity.getUserId())).orElse("Имя не найдено"
        );

        return CommentMapper.toDto(
                userName,
                updatedComment
        );
    }

    @Transactional
    public void deleteComment(Long userId, Long eventId, Long commentId) throws NumberFormatException {
        log.info("Получили запрос на удаление комментария {}, {}, {}",
                userId, eventId, commentId);
        if (!commentRepository.existsById(commentId))
            throw new EntityNotFoundException(DefaultMessagesForException.COMMENT_NOT_FOUND);

        checkIsValidData(userId, eventId);

        commentRepository.deleteById(commentId);
        log.info("Комментарий удален");
    }

    @Transactional
    public void deleteCommentByAdmin(Long commentId) throws NumberFormatException {
        log.info("Получили запрос на удаление комментария от админа {}", commentId);

        if (!commentRepository.existsById(commentId))
            throw new EntityNotFoundException(DefaultMessagesForException.COMMENT_NOT_FOUND);

        commentRepository.deleteById(commentId);
        log.info("Комментарий удален");
    }

    @Transactional(readOnly = true)
    public List<CommentDto> findAllCommentsForEventByUserId(Long userId, Long eventId) throws NumberFormatException {
        log.info("Получили запрос от пользователя {} на получение списка комментариев для события {}",
                userId, eventId);

        checkIsValidData(userId, eventId);

        String userNameById = userRepository.userNameById(userId);
        List<CommentEntity> allCommentsForEventByUserId = commentRepository
                .findAllCommentsForEventByUserId(eventId, userId);
        log.info("Найдено {} комментариев для события {} от пользователя {}",
                allCommentsForEventByUserId.size(), eventId, userId);

        return allCommentsForEventByUserId.isEmpty()
                ? Collections.emptyList()
                : allCommentsForEventByUserId.stream()
                .map(comment -> CommentMapper.toDto(userNameById, comment))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CommentDto> findAllCommentsForEventEntity(Long eventId) throws NumberFormatException {
        log.info("Получили запрос на получение списка комментариев для события {}", eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_FOUND);
        }

        List<CommentEntity> allCommentsByEventId = commentRepository.findAllCommentsByEventId(eventId);
        log.info("Найдено {} комментариев для события {}", allCommentsByEventId.size(), eventId);

        return allCommentsByEventId.isEmpty()
                ? Collections.emptyList()
                : allCommentsByEventId.stream()
                .map(comment -> CommentMapper
                        .toDto(userRepository.userNameById(comment.getUserId()), comment)
                )
                .toList();
    }

    protected void checkIsValidData(Long userId, Long eventId) {
        if (!userRepository.isUserExistsById(userId)) {
            throw new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND);
        }

        if (!eventRepository.existsById(eventId)) {
            throw new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_FOUND);
        }

        if (!commentRepository.isExistsByEventIdAndUserId(eventId, userId))
            throw new EntityNotFoundException(DefaultMessagesForException.COMMENT_NOT_EXISTS);

    }
}
