package ru.practicum.ewm.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.complitation.CompilationDto;
import ru.practicum.ewm.model.complitation.CompilationEntity;
import ru.practicum.ewm.model.complitation.NewCompilationDto;
import ru.practicum.ewm.model.complitation.UpdateCompilationRequest;
import ru.practicum.ewm.model.event.EventEntity;
import ru.practicum.ewm.model.event.EventShortDto;
import ru.practicum.ewm.model.user.UserEntity;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.utils.DefaultMessagesForException;
import ru.practicum.ewm.utils.EventServiceHelper;

import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.practicum.ewm.model.event.State.CONFIRMED;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;

    private final EventServiceHelper eventServiceHelper;
    private final EventService eventService;

    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compId, HttpServletRequest request) throws NumberFormatException {

        CompilationEntity compilationEntity = compilationRepository.findById(compId)
                .orElseThrow(() ->
                        new EntityNotFoundException(DefaultMessagesForException.COMPILATION_NOT_FOUND)
                );

        List<EventShortDto> eventShortDtos = compilationEntity.getEvents().stream()
                .map(eventEntity -> {
                    Integer countOfConfirmedRequests =
                            participationRequestRepository.findCountOfConfirmedRequests(eventEntity.getId(), CONFIRMED);
                    Long hits = eventService.getHits(request, eventEntity);
                    eventEntity.setConfirmedRequests(countOfConfirmedRequests);
                    return EventMapper.toShortEventDto(
                            eventEntity,
                            hits,
                            countOfConfirmedRequests
                    );
                }).toList();

        return CompilationMapper.toDto(compilationEntity, eventShortDtos);
    }

    @Transactional
    public CompilationDto saveCompilation(
            NewCompilationDto compilation
    ) throws ConstraintViolationException {
        if (isNull(compilation)) throw new IllegalArgumentException(DefaultMessagesForException.COMPILATION_IS_NULL);

        log.info("Создание новой подборки: {}", compilation);
        CompilationEntity compilationEntity = new CompilationEntity();

        if (nonNull(compilation.getPinned()))
            compilationEntity.setPinned(compilation.getPinned());
        else compilationEntity.setPinned(false);

        if (nonNull(compilation.getTitle())) {
            compilationEntity.setTitle(compilation.getTitle());
        }

        if (nonNull(compilation.getEvents()) && !compilation.getEvents().isEmpty()) {
            Set<EventEntity> eventEntitiesByIds = eventRepository.findEventEntitiesByIds(compilation.getEvents());
            log.debug("Найдено {} событий для подборки", eventEntitiesByIds.size());
            compilationEntity.setEvents(eventEntitiesByIds);
        } else compilationEntity.setEvents(new HashSet<>());

        CompilationEntity savedEntity = compilationRepository.save(compilationEntity);
        log.info("Подборка сохранена с id: {}", savedEntity.getId());

        List<EventShortDto> eventShortDtos = getEventShortDtos(compilationEntity.getEvents());
        log.debug("Создан список EventShortDtos для подборки");

        return CompilationMapper.toDto(savedEntity, eventShortDtos);
    }

    @Transactional
    public void deleteCompilation(Long id) throws EntityNotFoundException {
        log.info("Удаление подборки с id: {}", id);

        if (!compilationRepository.existsById(id))
            throw new EntityNotFoundException("Подборка с id: %s не найдена.".formatted(id));

        compilationRepository.deleteById(id);
        log.info("Подборка с id: {} успешно удалена", id);
    }

    @Transactional
    public CompilationDto updateCompilation(
            Long id,
            UpdateCompilationRequest compilationRequest
    ) {
        log.info("Обновление подборки с id: {}. Данные обновления: {}", id, compilationRequest);

        CompilationEntity compilationEntity = compilationRepository.findByIdCompilation(id).orElseThrow(
                () -> new EntityNotFoundException("Подборка не была найдена.")
        );
        log.debug("Найдена подборка: {}", compilationEntity);

        Set<EventEntity> oldEvents = compilationEntity.getEvents();
        List<Long> eventsId = compilationRequest.getEvents();

        if (nonNull(eventsId) && !eventsId.isEmpty()) {
            eventsId.forEach(eventId -> {
                EventEntity eventEntity = eventRepository.findById(eventId).orElseThrow(
                        () -> new EntityNotFoundException("")
                );
                oldEvents.add(eventEntity);
            });
            compilationEntity.setEvents(oldEvents);
        }

        if (nonNull(compilationRequest.getPinned())) {
            compilationEntity.setPinned(compilationRequest.getPinned());
        }

        if (nonNull(compilationRequest.getTitle())) {
            compilationEntity.setTitle(compilationRequest.getTitle());
        }
        CompilationEntity saved = compilationRepository.save(compilationEntity);
        log.info("Изменённая подборка {}", saved);

        List<EventShortDto> eventShortDtos = getEventShortDtos(saved.getEvents());
        return CompilationMapper.toDto(saved, eventShortDtos);
    }

    @Transactional(readOnly = true)
    public List<CompilationDto> findAll(
            Boolean pinned,
            Integer from,
            Integer size,
            HttpServletRequest request
    ) {
        PageRequest pageRequest = eventServiceHelper.getPageRequest(from, size);
        log.info("Запрос на получение подборок, входные параметры: {}, {}, {}", pageRequest, from, size);

        if (nonNull(pinned)) {
                List<CompilationEntity> allPinned = compilationRepository.findAllPinned(pinned, pageRequest);
                log.info("Полученный список подборок: {}, его размер = {}", allPinned, allPinned.size());
                return getCompilationDtos(allPinned, request);
        }

        List<CompilationEntity> allCompilationEntities = compilationRepository.findAllWithPagination(pageRequest);
        return getCompilationDtos(allCompilationEntities, request);

    }

    private List<EventShortDto> getEventShortDtos(Set<EventEntity> eventEntitiesByIds) {
        return eventEntitiesByIds
                .stream()
                .map(event -> {
                    Long initiatorId = event.getInitiator().getId();
                    log.debug("Обработка события с id: {}, инициатор: {}", event.getId(), initiatorId);

                    UserEntity userEntity = userRepository.findById(initiatorId).orElseThrow(
                            () -> new EntityNotFoundException("Инициатор с таким id %s не найден".formatted(initiatorId))
                    );

                    return EventMapper.toAdminShortEventDto(event, UserMapper.toUserShortDto(userEntity));
                })
                .toList();
    }

    private List<CompilationDto> getCompilationDtos(List<CompilationEntity> allCompilationEntities, HttpServletRequest request) {
        return allCompilationEntities.isEmpty()
                ? Collections.emptyList()
                : allCompilationEntities.stream()
                .map(compilationEntity -> {
                    List<EventShortDto> eventShortDtos = compilationEntity.getEvents().stream()
                            .map(eventEntity -> {
                                Integer countOfConfirmedRequests =
                                        participationRequestRepository.findCountOfConfirmedRequests(eventEntity.getId(), CONFIRMED);
                                Long hits = eventService.getHits(request, eventEntity);
                                eventEntity.setConfirmedRequests(countOfConfirmedRequests);
                                return EventMapper.toShortEventDto(eventEntity, hits, countOfConfirmedRequests);
                            }).toList();
                    return CompilationMapper.toDto(compilationEntity, eventShortDtos);
                })
                .toList();
    }
}