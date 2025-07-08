package ru.practicum.main.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.mapper.ParticipationRequestMapper;
import ru.practicum.main.model.event.*;
import ru.practicum.main.model.participation.ParticipationRequestDto;
import ru.practicum.main.model.participation.ParticipationRequestEntity;
import ru.practicum.main.model.user.UserEntity;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.ParticipationRequestRepository;
import ru.practicum.main.repository.UserRepository;
import ru.practicum.main.utils.DefaultMessagesForException;

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
    public ParticipationRequestDto createRequest(Long userId,
                                                 Long eventId
    ) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Пользователь не был найден.")
                );
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Событие не было найдено.")
                );

        if (requestRepository.isExistsByRequesterAndEventId(userId, eventId)) {
            throw new IllegalArgumentException("Заявка уже отправлена.");
        }

        if (eventEntity.getInitiator().getId().equals(userEntity.getId())) {
            throw new IllegalArgumentException("Инициатор события не может добавить запрос на участие в своём событии.");
        }

        if (!eventEntity.getState().equals(State.PUBLISHED)) {
            throw new IllegalArgumentException("Нельзя участвовать в неопубликованном событии.");
        }

        if (eventEntity.getParticipantLimit().equals(eventEntity.getConfirmedRequests())) {
            throw new IllegalArgumentException("Достигнут лимит запросов на участие.");
        }

        ParticipationRequestEntity participationEntity = ParticipationRequestMapper
                .toParticipationEntity(eventEntity, userEntity);

        if (!eventEntity.getRequestModeration() && eventEntity.getParticipantLimit() != 0) {
            participationEntity.setStatus(State.CONFIRMED);

        } else {
            participationEntity.setStatus(State.PENDING);
        }

        return ParticipationRequestMapper.toParticipationDto(requestRepository.save(participationEntity));
    }

    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) throws NumberFormatException {
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
        if (!userRepository.isUserExistsById(userId))
            throw new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND);

        if (!eventRepository.isEventEntityExistsById(eventId))
            throw new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_FOUND);

        if (!eventRepository.isExistsByEventIdAndUserId(eventId, userId))
            throw new IllegalArgumentException(DefaultMessagesForException.EVENT_NOT_FOUND_FOR_USER);

        List<ParticipationRequestDto> allByRequesterAndEventId =
                requestRepository.getAllByRequesterAndEventId(userId, eventId);

        return allByRequesterAndEventId.isEmpty() ? Collections.emptyList() : allByRequesterAndEventId;
    }

    @Transactional
    public EventRequestStatusUpdateResult updateParticipationRequestsByUserAndEventId(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest
    ) {
        if (!userRepository.isUserExistsById(userId))
            throw new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND);

        EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_FOUND));

        if (!eventRepository.isExistsByEventIdAndUserId(eventId, userId))
            throw new IllegalArgumentException(DefaultMessagesForException.EVENT_NOT_FOUND_FOR_USER);

        Long countOfConfirmedRequests = requestRepository.findCountOfConfirmedRequests(
                eventEntity.getId(),
                State.CONFIRMED
        );

        if (eventEntity.getParticipantLimit() > 0 && eventEntity.getParticipantLimit() <= countOfConfirmedRequests)
            throw new IllegalArgumentException(DefaultMessagesForException.EVENT_LIMIT_REACHED);


        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        List<ParticipationRequestEntity> allParticipationRequestsByPendingStatus =
                requestRepository.findAllPendingRequestsConfirmed(
                        eventRequestStatusUpdateRequest.getRequestIds(),
                        eventId,
                        State.PENDING
                );

        for (int i = 0; i < allParticipationRequestsByPendingStatus.size(); i++) {
            ParticipationRequestEntity participationRequestEntity = allParticipationRequestsByPendingStatus.get(i);
            if (eventRequestStatusUpdateRequest.getStatus().equals(State.REJECTED.getName())) {
                participationRequestEntity.setStatus(State.REJECTED);
                rejectedRequests.add(ParticipationRequestMapper.toParticipationDto(participationRequestEntity));
            }

            if (eventRequestStatusUpdateRequest.getStatus().equals(State.CONFIRMED.getName())
                && (countOfConfirmedRequests + i) < eventEntity.getParticipantLimit()
            ) {
                participationRequestEntity.setStatus(State.CONFIRMED);
                confirmedRequests.add(ParticipationRequestMapper.toParticipationDto(participationRequestEntity));
            }
        }

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) throws NumberFormatException {
        if (!userRepository.isUserExistsById(userId))
            throw new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND);

        ParticipationRequestEntity participationRequestEntity =
                requestRepository.findById(requestId).orElseThrow(() ->
                        new EntityNotFoundException("Заявка не была найдена.")
                );

        if (!requestRepository.isParticipationRequestExistsByUserIdAndRequestId(userId, requestId))
            throw new EntityNotFoundException(DefaultMessagesForException.REQUEST_NOT_FOUND_FOR_USER);

        participationRequestEntity.setStatus(State.CANCELED);

        return ParticipationRequestMapper.toParticipationDto(
                requestRepository.save(participationRequestEntity)
        );
    }
}
