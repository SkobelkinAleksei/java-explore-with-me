package ru.practicum.ewm.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.category.CategoryEntity;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.location.LocationEntity;
import ru.practicum.ewm.model.user.UserEntity;
import ru.practicum.ewm.repository.CategoriesRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.utils.DefaultMessagesForException;
import ru.practicum.ewm.utils.EventServiceHelper;
import ru.practicum.statsclient.StatsClientService;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStats;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ru.practicum.ewm.model.event.State.CANCELED;
import static ru.practicum.ewm.model.event.State.PUBLISHED;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    private final EventServiceHelper eventServiceHelper;

    private final StatsClientService statsClientService;

    private static final String APP = "ewm-main-service";

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByPrivateUser(
            Long userId,
            Integer from,
            Integer size
    ) {
        log.info("Запрос на получение событий пользователя с id: {}, from: {}, size: {}", userId, from, size);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не был найден."));
        log.debug("Найден пользователь: {}", userEntity);

        Set<EventEntity> eventEntitiesByUserId = eventRepository.findEventEntitiesByUserId(
                userId,
                eventServiceHelper.getPageRequest(from, size)
        );
        log.info("Найдено {} событий для пользователя с id: {}", eventEntitiesByUserId.size(), userId);

        return eventEntitiesByUserId.isEmpty() ? Collections.emptyList() : eventEntitiesByUserId.stream()
                .map(event -> EventMapper.toAdminShortEventDto(event, UserMapper.toUserShortDto(userEntity)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByPublicUser(String text,
                                                     List<Long> categories,
                                                     Boolean paid,
                                                     LocalDateTime rangeStart,
                                                     LocalDateTime rangeEnd,
                                                     Boolean onlyAvailable,
                                                     String sort,
                                                     Integer from,
                                                     Integer size
    ) {
        String sortedBy = "";
        if (nonNull(sort)) {
            if (sort.equals(EventSortType.EVENT_DATE.toString())) sortedBy = "eventDate";
            else sortedBy = "id";
        }


        Set<EventEntity> allEventEntities = getAllEventEntities(eventServiceHelper.getPageRequestWithSort(
                from,
                size,
                sortedBy)
        );

        return allEventEntities.stream()
                .filter(event -> {
                    if (text != null) {
                        return event.getTitle().toLowerCase().contains(text.toLowerCase().trim());
                    }
                    return true;
                }).filter(event -> {
                    if (categories != null) {
                        return categories.contains(event.getCategory().getId());
                    }
                    return true;
                }).filter(event -> {
                    if (paid != null) {
                        return event.getPaid();
                    }
                    return true;
                }).filter(event -> {
                    if (onlyAvailable != null) {
                        Integer participantLimit = event.getParticipantLimit();
                        Integer confirmedRequests = event.getConfirmedRequests();
                        return !confirmedRequests.equals(participantLimit);
                    }
                    return true;
                }).filter(event -> {
                    if (rangeStart != null && rangeEnd != null) {
                        return event.getEventDate().isAfter(rangeStart)
                               && event.getEventDate().isBefore(rangeEnd);
                    }
                    return true;
                }).map(eventEntity -> {
                    UserEntity userEntity = userRepository.findById(eventEntity.getInitiator().getId()).orElseThrow(
                            () -> new EntityNotFoundException("Такой инициатор не был найден.")
                    );

                    return EventMapper.toShortEventDto(eventEntity, UserMapper.toUserShortDto(userEntity));
                }).toList();
    }

    @Transactional(readOnly = true)
    public EventFullDto getEventById(
            Long userId,
            Long eventId,
            HttpServletRequest request
    ) throws NumberFormatException {

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() ->
                        new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND)
                );

        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Событие с таким id не найдено.")
                );

        if (!eventRepository.isExistsByEventIdAndUserId(eventId, userId))
            throw new IllegalArgumentException(DefaultMessagesForException.EVENT_NOT_FOUND_FOR_USER);

        if (!eventEntity.getState().equals(PUBLISHED))
            throw new IllegalArgumentException("Событие не было опубликовано.");

        List<ViewStats> viewStats = getViewStats(request, eventEntity);

        Long hits = (viewStats.isEmpty()) ? 0L : viewStats.getFirst().getHits();

        EndpointHitDto endpointHitDto = new EndpointHitDto(
                APP,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );

        statsClientService.saveHit(endpointHitDto);

        return EventMapper.toEventFullDto(
                eventEntity,
                UserMapper.toUserShortDto(userEntity),
                CategoryMapper.toDto(eventEntity.getCategory()),
                LocationMapper.toLocationDto(eventEntity.getLocation()),
                hits
        );
    }

    @Transactional
    public List<EventFullDto> getEventsByAdmin(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size,
            HttpServletRequest request
    ) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Некорректный запрос.");
        }

        Set<EventEntity> allEventEntities = eventRepository.findAllByAdmin(
                eventServiceHelper.getPageRequest(from, size)
        );

        if (allEventEntities.isEmpty()) return Collections.emptyList();

        List<EventEntity> filteredEvents = allEventEntities.stream()
                .filter(event -> {
                    if (users != null) {
                        Long initiatorId = event.getInitiator().getId();
                        return users.contains(initiatorId);
                    }
                    return true;
                }).filter(event -> {
                    if (states != null) {
                        return states.contains(event.getState().getName());
                    }
                    return true;
                }).filter(event -> {
                    if (categories != null) {
                        return categories.contains(event.getCategory().getId());
                    }
                    return true;
                }).filter(event -> {
                    if (rangeStart != null && rangeEnd != null) {
                        return event.getEventDate().isAfter(rangeStart)
                               && event.getEventDate().isBefore(rangeEnd);
                    }
                    return true;
                }).toList();

        return filteredEvents.stream().map(eventEntity -> {
            UserEntity userEntity = userRepository.findById(eventEntity.getInitiator().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Пользователь не был найден."));

            List<ViewStats> viewStats = getViewStats(request, eventEntity);

            return EventMapper.toEventFullDto(eventEntity,
                    UserMapper.toUserShortDto(userEntity),
                    CategoryMapper.toDto(eventEntity.getCategory()),
                    LocationMapper.toLocationDto(eventEntity.getLocation()),
                    viewStats.isEmpty() ? 0L : viewStats.getFirst().getHits());
        }).toList();
    }


    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        log.info("Создание нового события пользователя с id: {}. Данные события: {}", userId, newEventDto);

        CategoryEntity categoryEntity = categoriesRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Категория не была найдена."));
        log.debug("Найдена категория: {}", categoryEntity);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не был найден."));
        log.debug("Найден пользователь: {}", userEntity);

        LocationEntity savedLocationEntity = locationRepository.save(
                new LocationEntity(
                        newEventDto.getLocation().getLat(),
                        newEventDto.getLocation().getLon())
        );
        log.info("Создана локация с id: {}", savedLocationEntity.getId());

        EventEntity eventEntity = EventMapper.toEntity(newEventDto, categoryEntity, savedLocationEntity, userEntity);
        EventEntity savedEntity = eventRepository.save(eventEntity);
        log.info("Создано событие с id: {}", savedEntity.getId());

        return EventMapper.toEventFullDto(savedEntity,
                UserMapper.toUserShortDto(userEntity),
                CategoryMapper.toDto(categoryEntity),
                LocationMapper.toLocationDto(savedLocationEntity),
                0L
        );
    }

    @Transactional
    public EventFullDto updateEvent(Long eventId,
                                    UpdateEventAdminRequest updateEventAdminRequest,
                                    HttpServletRequest request
    ) {
        if (isNull(eventId) || isNull(updateEventAdminRequest)) {
            throw new IllegalArgumentException("Некорректные данные.");
        }
        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Такое событие не найдено."));

        UserEntity userEntity = userRepository.findById(eventEntity.getInitiator().getId())
                .orElseThrow(() -> new EntityNotFoundException("Инициатор с таким id не был найден."));

        CategoryEntity categoryEntity = categoriesRepository.findById(updateEventAdminRequest.getCategory())
                .orElseThrow(() -> new EntityNotFoundException("Такая категория не была найдена."));

        EventEntity updatedEventEntity = toUpdatedEventEntity(
                eventEntity,
                updateEventAdminRequest,
                categoryEntity
        );

        List<ViewStats> viewStats = getViewStats(request, eventEntity);

        return EventMapper.toEventFullDto(eventRepository.save(updatedEventEntity),
                UserMapper.toUserShortDto(userEntity),
                CategoryMapper.toDto(categoryEntity),
                LocationMapper.toLocationDto(updatedEventEntity.getLocation()),
                viewStats.isEmpty() ? 0L : viewStats.getFirst().getHits()
        );
    }

    @Transactional
    public EventFullDto updateEventByUserIdAndEventId(Long userId,
                                                      Long eventId,
                                                      UpdateEventUserRequest updateEventUserRequest,
                                                      HttpServletRequest request
    ) {
        if (!userRepository.isUserExistsById(userId))
            throw new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND);

        if (!eventRepository.isEventEntityExistsById(eventId))
            throw new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_FOUND);

        UserEntity userEntity = userRepository.findById(userId).get();

        EventEntity eventEntity = eventRepository.findEventEntityByUserId(userId, eventId)
                .orElseThrow(() ->
                        new IllegalArgumentException(DefaultMessagesForException.EVENT_NOT_FOUND_FOR_USER)
                );

        if (!eventEntity.getState().equals(CANCELED) || !eventEntity.getRequestModeration())
            throw new IllegalArgumentException("Событие нельзя обновить.");

        if (!LocalDateTime.parse(updateEventUserRequest.getEventDate()).isAfter(LocalDateTime.now().plusHours(2L)))
            throw new IllegalArgumentException("Некорректная дата.");

        LocationEntity locationEntity = eventServiceHelper.checkLocation(
                updateEventUserRequest.getLocation().getLat(),
                updateEventUserRequest.getLocation().getLon()
        );

        eventRepository.updateEventEntity(
                eventEntity.getId(),
                updateEventUserRequest,
                LocalDateTime.parse(updateEventUserRequest.getEventDate()),
                locationEntity.getId(),
                State.fromStringToState(updateEventUserRequest.getStateAction())

        );

        List<ViewStats> viewStats = getViewStats(request, eventEntity);

        return EventMapper.toEventFullDto(eventEntity,
                UserMapper.toUserShortDto(userEntity),
                CategoryMapper.toDto(eventEntity.getCategory()),
                LocationMapper.toLocationDto(locationEntity),
                viewStats.isEmpty() ? 0L : viewStats.getFirst().getHits()
        );
    }

    private Set<EventEntity> getAllEventEntities(PageRequest pageRequest) {
        return eventRepository.findAll(pageRequest);
    }

    private List<ViewStats> getViewStats(HttpServletRequest request, EventEntity eventEntity) {
        ResponseEntity<?> responseStats = statsClientService.getStats(
                eventEntity.getCreatedOn(),
                LocalDateTime.now(),
                List.of(request.getRequestURI()),
                true
        );

        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(
                responseStats.getBody(),
                new TypeReference<>() {
                });
    }

    private EventEntity toUpdatedEventEntity(
            EventEntity currentEvent,
            UpdateEventAdminRequest updateEventAdminRequest,
            CategoryEntity categoryEntity
    ) {
        String stateAction = updateEventAdminRequest.getStateAction();

        if (stateAction.equals("PUBLISH_EVENT")) {
            currentEvent.setState(PUBLISHED);
            currentEvent.setPublishedOn(LocalDateTime.now());
        } else {
            currentEvent.setState(CANCELED);
        }

        if (!currentEvent.getCategory().getId().equals(updateEventAdminRequest.getCategory())) {
            currentEvent.setCategory(categoryEntity);
        }

        if (updateEventAdminRequest.getAnnotation() != null &&
            !updateEventAdminRequest.getAnnotation().equals(currentEvent.getAnnotation())) {
            currentEvent.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getTitle() != null &&
            !updateEventAdminRequest.getTitle().equals(currentEvent.getTitle())) {
            currentEvent.setTitle(updateEventAdminRequest.getTitle());
        }

        if (updateEventAdminRequest.getDescription() != null &&
            !updateEventAdminRequest.getDescription().equals(currentEvent.getDescription())) {
            currentEvent.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            LocalDateTime newDate = LocalDateTime.parse(updateEventAdminRequest.getEventDate());
            if (!newDate.equals(currentEvent.getEventDate())) {
                currentEvent.setEventDate(newDate);
            }
        }

        if (updateEventAdminRequest.getLocation() != null &&
            !updateEventAdminRequest.getLocation().equals(currentEvent.getLocation())) {
            currentEvent.setLocation(eventServiceHelper.checkLocation(updateEventAdminRequest.getLocation()));
        }

        if (updateEventAdminRequest.getParticipantLimit() != null &&
            !updateEventAdminRequest.getParticipantLimit().equals(currentEvent.getParticipantLimit())) {
            currentEvent.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getPaid() != null &&
            !updateEventAdminRequest.getPaid().equals(currentEvent.getPaid())) {
            currentEvent.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getRequestModeration() != null &&
            !currentEvent.getRequestModeration().equals(updateEventAdminRequest.getRequestModeration())) {
            currentEvent.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        return currentEvent;
    }
}
