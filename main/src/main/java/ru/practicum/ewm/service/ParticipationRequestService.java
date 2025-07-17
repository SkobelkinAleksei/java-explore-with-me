package ru.practicum.ewm.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exeption.ForbiddenException;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.participation.ParticipationRequestDto;
import ru.practicum.ewm.model.participation.ParticipationRequestEntity;
import ru.practicum.ewm.model.user.UserEntity;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.utils.DefaultMessagesForException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public ParticipationRequestDto createRequest(
            Long userId,
            Long eventId
    ) {
        log.info("Создание запроса на участие: userId={}, eventId={}", userId, eventId);
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Пользователь не был найден.")
                );

        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Событие не было найдено.")
                );

        if (requestRepository.isExistsByRequesterAndEventId(userId, eventId)) {
            log.info("Заявка уже существует: userId={}, eventId={}", userId, eventId);
            throw new ForbiddenException("Заявка уже отправлена.");
        }

        if (eventEntity.getInitiator().getId().equals(userEntity.getId())) {
            log.info("Инициатор пытается добавить заявку на свое событие: userId={}, eventId={}", userId, eventId);
            throw new ForbiddenException("Инициатор события не может добавить запрос на участие в своём событии.");
        }

        if (!eventEntity.getState().equals(State.PUBLISHED)) {
            log.info("Попытка участвовать в неопубликованном событии: eventId={}", eventId);
            throw new ForbiddenException("Нельзя участвовать в неопубликованном событии.");
        }
        Integer countOfRequests = requestRepository.findCountOfConfirmedRequests(eventEntity.getId(), State.CONFIRMED);
        log.info("Текущее число подтвержденных заявок для события {}: {}", eventId, countOfRequests);

        if (eventEntity.getParticipantLimit() > 0
                && countOfRequests >= eventEntity.getParticipantLimit()
        ) {
            throw new ForbiddenException(DefaultMessagesForException.EVENT_LIMIT_REACHED);
        }

        ParticipationRequestEntity participationEntity = ParticipationRequestMapper.toParticipationEntity(
                eventEntity,
                userEntity
        );

        if (!eventEntity.getRequestModeration() || eventEntity.getParticipantLimit() == 0) {
            participationEntity.setStatus(State.CONFIRMED);
            log.info("Запрос подтвержден: userId={}, eventId={}", userId, eventId);
        }
        ParticipationRequestEntity saved = requestRepository.save(participationEntity);
        log.info("Запрос успешно сохранен: id={}", saved.getId());
        return ParticipationRequestMapper.toParticipationDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) throws NumberFormatException {
        log.info("Получение запросов пользователя: userId={}", userId);
        if (!userRepository.isUserExistsById(userId)) {
            throw new EntityNotFoundException("Пользователь не был найден.");
        }
        return requestRepository.getAllByRequesterId(userId).isEmpty()
                ? Collections.emptyList()
                : requestRepository.getAllByRequesterId(userId)
                .stream()
                .map(ParticipationRequestMapper::toParticipationDto)
                .toList();

    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventParticipantsByUserAndEventId(
            Long userId,
            Long eventId
    ) throws NumberFormatException {
        log.info("Получение участников события по пользователю и событию: userIid={}, eventIid={}", userId, eventId);
        if (!userRepository.isUserExistsById(userId))
            throw new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND);

        if (!eventRepository.isEventEntityExistsById(eventId))
            throw new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_FOUND);

        if (!eventRepository.isExistsByEventIdAndUserId(eventId, userId))
            throw new IllegalArgumentException(DefaultMessagesForException.EVENT_NOT_FOUND_FOR_USER);

        List<ParticipationRequestEntity> allByRequesterAndEventId =
                requestRepository.getAllByRequesterAndEventId(eventId);

        return allByRequesterAndEventId.isEmpty()
                ? Collections.emptyList()
                : allByRequesterAndEventId.stream()
                .map(ParticipationRequestMapper::toParticipationDto)
                .toList();
    }

    @Transactional
    public EventRequestStatusUpdateResult updateParticipationRequestsByUserAndEventId(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest
    ) {
        log.info("Обновление статусов заявок по пользователю и событию: пользователь={}, событие={}", userId, eventId);
        if (!userRepository.isUserExistsById(userId))
            throw new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND);

        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_FOUND));

        if (!eventRepository.isExistsByEventIdAndUserId(eventId, userId))
            throw new IllegalArgumentException(DefaultMessagesForException.EVENT_NOT_FOUND_FOR_USER);

        Integer countOfConfirmedRequests = requestRepository.findCountOfConfirmedRequests(
                eventEntity.getId(),
                State.CONFIRMED
        );

        if (countOfConfirmedRequests >= eventEntity.getParticipantLimit()) {
            throw new ForbiddenException("Достигнут лимит заявок");
        }

        List<ParticipationRequestEntity> allPendingRequests = requestRepository.findAllPendingRequestsConfirmed(
                eventRequestStatusUpdateRequest.getRequestIds(),
                eventId,
                State.PENDING
        );

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (ParticipationRequestEntity request : allPendingRequests) {
            if (eventRequestStatusUpdateRequest.getStatus().equals(State.REJECTED.getName())) {
                request.setStatus(State.REJECTED);
                requestRepository.save(request);
                rejectedRequests.add(ParticipationRequestMapper.toParticipationDto(request));
            } else if (eventRequestStatusUpdateRequest.getStatus().equals(State.CONFIRMED.getName())) {
                if (countOfConfirmedRequests < eventEntity.getParticipantLimit()) {
                    request.setStatus(State.CONFIRMED);
                    requestRepository.save(request);
                    confirmedRequests.add(ParticipationRequestMapper.toParticipationDto(request));
                } else {
                    request.setStatus(State.REJECTED);
                    requestRepository.save(request);
                    rejectedRequests.add(ParticipationRequestMapper.toParticipationDto(request));
                }
            }
        }

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(
            Long requestId,
            Long userId
    ) throws NumberFormatException {
        log.info("Отмена заявки. requestId={}, userId={}", requestId, userId);
        if (!userRepository.isUserExistsById(requestId))
            throw new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND);

        if (!requestRepository.isParticipationRequestExistsByUserIdAndRequestId(requestId, userId))
            throw new EntityNotFoundException(
                    DefaultMessagesForException.REQUEST_NOT_FOUND_FOR_USER
            );

        ParticipationRequestEntity participationRequestEntity =
                requestRepository.findByRequesterId(requestId).orElseThrow(() ->
                        new EntityNotFoundException("Заявка не была найдена.")
                );

        participationRequestEntity.setStatus(State.CANCELED);
        return ParticipationRequestMapper.toParticipationDto(
                requestRepository.save(participationRequestEntity)
        );
    }
}