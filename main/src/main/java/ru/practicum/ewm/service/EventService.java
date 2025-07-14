package ru.practicum.ewm.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exeption.ForbiddenException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.category.CategoryEntity;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.location.LocationEntity;
import ru.practicum.ewm.model.user.UserEntity;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.utils.DateTimeHelper;
import ru.practicum.ewm.utils.DefaultMessagesForException;
import ru.practicum.ewm.utils.EventServiceHelper;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.DataFormatException;

import static java.util.Objects.nonNull;
import static ru.practicum.ewm.model.event.State.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    private final EventServiceHelper eventServiceHelper;

    private final StatsClient statsClient;

    private static final String APP = "ewm-main-service";

    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByPrivateUser(
            Long userId,
            Integer from,
            Integer size
    ) throws NumberFormatException {
        log.info("Запрос на получение событий пользователя с id: {}, from: {}, size: {}", userId, from, size);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND));
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
    public List<EventShortDto> getEventsByPublicUser(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size,
            HttpServletRequest request
    ) throws NumberFormatException {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            try {
                throw new DataFormatException("Некорректный запрос.");
            } catch (DataFormatException e) {
                throw new RuntimeException(e);
            }
        }
        Specification<EventEntity> specification;
        specification = getEventEntitySpecificationByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        List<EventEntity> pageEvents = eventRepository
                .findAll(specification, getPageRequest(sort, from, size))
                .getContent();

        if (nonNull(onlyAvailable)) {
            if (onlyAvailable) {
                pageEvents = eventRepository.findAllWithAvailableLimit(specification, getPageRequest(sort, from, size))
                        .getContent();
            }
        }

        return pageEvents.isEmpty() ? Collections.emptyList() : pageEvents.stream()
                .map(eventEntity -> {
                    Integer countOfConfirmedRequests =
                            participationRequestRepository.findCountOfConfirmedRequests(eventEntity.getId(), CONFIRMED);
                    List<ViewStats> viewStats = getViewStats(request, eventEntity);
                    Long hits = 0L;
                    if (nonNull(viewStats)) {
                        if (!viewStats.isEmpty()) hits = viewStats.getFirst().getHits();
                    }
                    eventEntity.setConfirmedRequests(countOfConfirmedRequests);

                    return EventMapper.toShortEventDto(
                            eventEntity,
                            UserMapper.toUserShortDto(eventEntity.getInitiator()),
                            hits,
                            countOfConfirmedRequests
                    );
                })
                .toList();
    }

    private PageRequest getPageRequest(
            String sort,
            Integer from,
            Integer size
    ) {
        String sortBy = "views";

        if (nonNull(sort)) {
            if (sort.equalsIgnoreCase("event_date")) {
                sortBy = "eventDate";
            }
            return eventServiceHelper.getPageRequestWithSort(
                    from,
                    size,
                    sortBy
            );
        } else {
            return eventServiceHelper.getPageRequest(from, size);
        }
    }

    @Transactional(readOnly = true)
    public EventFullDto getEventByIdWithoutUser(
            Long eventId,
            HttpServletRequest request
    ) throws NumberFormatException {
        EventEntity eventEntity = eventRepository.findPublishedEventById(eventId, PUBLISHED)
                .orElseThrow(() -> new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_FOUND)
                );

        EndpointHitDto endpointHitDto = new EndpointHitDto(
                APP,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );
        statsClient.saveHit(endpointHitDto);

        List<ViewStats> viewStats = getViewStats(request, eventEntity);
        Long hits = viewStats.getFirst().getHits();
        Integer confirmedRequests = participationRequestRepository.findByEventId(eventEntity.getId(), CONFIRMED);

        return EventMapper.toEventFullDto(
                eventEntity,
                hits,
                confirmedRequests
        );
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
            throw new ForbiddenException(DefaultMessagesForException.EVENT_NOT_FOUND_FOR_USER);

        List<ViewStats> viewStats = getViewStats(request, eventEntity);

        Long hits = 0L;
        if (nonNull(viewStats)) {
            if (!viewStats.isEmpty()) hits = viewStats.getFirst().getHits();
        }

        EndpointHitDto endpointHitDto = new EndpointHitDto(
                APP,
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now()
        );

        statsClient.saveHit(endpointHitDto);
        Integer confirmedRequests = participationRequestRepository.findByEventId(eventEntity.getId(), CONFIRMED);

        return EventMapper.toEventFullDto(
                eventEntity,
                hits,
                confirmedRequests
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
    ) throws NumberFormatException {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            try {
                throw new DataFormatException("Некорректный запрос.");
            } catch (DataFormatException e) {
                throw new RuntimeException(e);
            }
        }

        Specification<EventEntity> specification =
                getEventEntitySpecificationByAdmin(users, states, categories, rangeStart, rangeEnd);

        List<EventEntity> filteredEvents = eventRepository.findAll(
                specification,
                eventServiceHelper.getPageRequest(from, size)
        ).getContent().stream().sorted(Comparator.comparing(EventEntity::getId)).toList();

        log.info("[DEBUG] Filtered events {}", filteredEvents);

        List<EventFullDto> eventFullDtos = getEventFullDtos(filteredEvents, request);
        log.info("[DEBUG] Event full dtos {}", eventFullDtos);

        return filteredEvents.isEmpty()
                ? Collections.emptyList()
                : eventFullDtos;
    }

    @Transactional
    public EventFullDto createEvent(
            Long userId,
            NewEventDto newEventDto
    ) throws NumberFormatException {
        log.info("Создание нового события пользователя с id: {}. Данные события: {}", userId, newEventDto);
        isCorrectDate(newEventDto.getEventDate());

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND));
        log.debug("[DEBUG] Найден пользователь: {}", userEntity);

        CategoryEntity categoryEntity = categoriesRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new EntityNotFoundException(DefaultMessagesForException.CATEGORY_NOT_FOUND));
        log.debug("Найдена категория: {}", categoryEntity);

        LocationEntity savedLocationEntity = locationRepository.save(
                new LocationEntity(
                        newEventDto.getLocation().getLat(),
                        newEventDto.getLocation().getLon())
        );
        log.info("Создана локация с id: {}", savedLocationEntity.getId());

        EventEntity eventEntity = EventMapper.toEntity(newEventDto, categoryEntity, savedLocationEntity, userEntity);
        EventEntity savedEntity = eventRepository.save(eventEntity);
        log.info("Создано событие : {}", savedEntity);
        Integer confirmedRequests = participationRequestRepository.findByEventId(eventEntity.getId(), CONFIRMED);

        return EventMapper.toEventFullDto(
                savedEntity,
                0L,
                confirmedRequests
        );
    }

    @Transactional
    public EventFullDto updateEvent(Long eventId,
                                    UpdateEventAdminRequest updateEventAdminRequest,
                                    HttpServletRequest request
    ) throws NumberFormatException {
        EventEntity eventEntity = eventRepository.findEventEntityById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_FOUND));

        UserEntity userEntity = userRepository.findById(eventEntity.getInitiator().getId())
                .orElseThrow(() -> new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND));

        if (!eventRepository.isExistsByEventIdAndUserId(eventId, userEntity.getId()))
            throw new IllegalArgumentException(DefaultMessagesForException.EVENT_NOT_FOUND_FOR_USER);

        if (eventEntity.getState().equals(PUBLISHED) || eventEntity.getState().equals(REJECTED)) {
            throw new ForbiddenException("Некорректное событие.");
        }

        if (nonNull(updateEventAdminRequest.getCategory())) {
            CategoryEntity categoryEntity = categoriesRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException(DefaultMessagesForException.CATEGORY_NOT_FOUND));
            eventEntity.setCategory(categoryEntity);
        }

        if (nonNull(updateEventAdminRequest.getAnnotation())) {
            eventEntity.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (nonNull(updateEventAdminRequest.getTitle())) {
            eventEntity.setTitle(updateEventAdminRequest.getTitle());
        }

        if (nonNull(updateEventAdminRequest.getDescription())) {
            eventEntity.setDescription(updateEventAdminRequest.getDescription());
        }

        if (nonNull(updateEventAdminRequest.getParticipantLimit())) {
            eventEntity.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (nonNull(updateEventAdminRequest.getStateAction())) {
            String state = "";
            if (updateEventAdminRequest.getStateAction().equals(StateActionAdmin.PUBLISH_EVENT.getName())) {
                state = PUBLISHED.getName();
                eventEntity.setPublishedOn(LocalDateTime.now());
            } else {
                state = REJECTED.getName();
            }

            eventEntity.setState(fromStringToState(state));
        }

        if (nonNull(updateEventAdminRequest.getEventDate())) {
            if (!LocalDateTime.parse(
                    updateEventAdminRequest.getEventDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).isAfter(LocalDateTime.now())) {
                throw new ValidationException("Некорректная дата");
            }

            LocalDateTime parse = LocalDateTime.parse(updateEventAdminRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            eventEntity.setEventDate(parse);
        }

        if (nonNull(updateEventAdminRequest.getLocation())) {
            eventEntity.setLocation(eventServiceHelper.checkLocation(
                    updateEventAdminRequest.getLocation().getLat(),
                    updateEventAdminRequest.getLocation().getLon())
            );
        }

        if (nonNull(updateEventAdminRequest.getPaid())) {
            eventEntity.setPaid(updateEventAdminRequest.getPaid());
        }

        if (nonNull(updateEventAdminRequest.getRequestModeration())) {
            eventEntity.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        EventEntity savedEvent = eventRepository.save(eventEntity);
        List<ViewStats> viewStats = getViewStats(request, savedEvent);

        Integer confirmedRequests = participationRequestRepository.findByEventId(eventEntity.getId(), CONFIRMED);
        return EventMapper.toEventFullDto(
                savedEvent,
                viewStats.isEmpty() ? 0L : viewStats.getFirst().getHits(),
                confirmedRequests
        );
    }

    @Transactional
    public EventFullDto updateEventByUserIdAndEventId(
            Long userId,
            Long eventId,
            UpdateEventUserRequest updateEventUserRequest,
            HttpServletRequest request
    ) throws NumberFormatException {
        if (nonNull(updateEventUserRequest.getEventDate())) {
            LocalDateTime parsedEventDate =
                    LocalDateTime.parse(updateEventUserRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (parsedEventDate.isBefore(LocalDateTime.now().plusHours(2L)))
                throw new ValidationException("Некорректная дата");
        }

        if (!userRepository.isUserExistsById(userId))
            throw new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND);

        if (!eventRepository.isEventEntityExistsById(eventId))
            throw new EntityNotFoundException(DefaultMessagesForException.EVENT_NOT_FOUND);

        EventEntity eventEntity = eventRepository.findEventEntityByUserId(userId, eventId)
                .orElseThrow(() ->
                        new IllegalArgumentException(DefaultMessagesForException.EVENT_NOT_FOUND_FOR_USER)
                );

        if (eventEntity.getState().equals(PUBLISHED))
            throw new ForbiddenException("");

        if (!eventRepository.isExistsByEventIdAndUserId(eventId, userId))
            throw new ForbiddenException(DefaultMessagesForException.EVENT_NOT_FOUND_FOR_USER);

        if (nonNull(updateEventUserRequest.getAnnotation())) {
            eventEntity.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (nonNull(updateEventUserRequest.getEventDate())) {
            isCorrectDate(updateEventUserRequest.getEventDate());
            eventEntity.setEventDate(LocalDateTime.parse(updateEventUserRequest.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        if (nonNull(updateEventUserRequest.getLocation())
                && nonNull(updateEventUserRequest.getLocation().getLat())
                && nonNull(updateEventUserRequest.getLocation().getLon())
        ) {
            LocationEntity location = eventServiceHelper.checkLocation(
                    updateEventUserRequest.getLocation().getLat(),
                    updateEventUserRequest.getLocation().getLon()
            );

            eventEntity.setLocation(location);
        }

        if (nonNull(updateEventUserRequest.getDescription()))
            eventEntity.setDescription(updateEventUserRequest.getDescription());

        if (nonNull(updateEventUserRequest.getPaid()))
            eventEntity.setPaid(updateEventUserRequest.getPaid());

        if (nonNull(updateEventUserRequest.getParticipantLimit()))
            eventEntity.setParticipantLimit(updateEventUserRequest.getParticipantLimit());

        if (nonNull(updateEventUserRequest.getRequestModeration()))
            eventEntity.setRequestModeration(updateEventUserRequest.getRequestModeration());

        if (nonNull(updateEventUserRequest.getStateAction())) {
            StateActionPrivate.isCorrectState(updateEventUserRequest.getStateAction());
            if (updateEventUserRequest.getStateAction().equals(StateActionPrivate.SEND_TO_REVIEW.getDescription())
            || eventEntity.getState().equals(CONFIRMED)) {
                eventEntity.setState(State.PENDING);
            } else {
                eventEntity.setState(CANCELED);
            }
        }

        if (nonNull(updateEventUserRequest.getTitle()))
            eventEntity.setTitle(updateEventUserRequest.getTitle());

        EventEntity savedEntity = eventRepository.save(eventEntity);
        List<ViewStats> viewStats = getViewStats(request, savedEntity);

        Long hits = 0L;
        if (nonNull(viewStats) && !viewStats.isEmpty()) {
            if (nonNull(viewStats.getFirst())) {
                hits = viewStats.getFirst().getHits();
            }
        }
        Integer confirmedRequests = participationRequestRepository.findByEventId(eventEntity.getId(), CONFIRMED);

        return EventMapper.toEventFullDto(
                savedEntity,
                hits,
                confirmedRequests
        );
    }

    private static Specification<EventEntity> getEventEntitySpecificationByAdmin(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd
    ) {
        Specification<EventEntity> specification = Specification.where(null);

        if (nonNull(users) && !users.isEmpty() && users.getFirst() != 0) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }

        if (nonNull(states)) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }

        if (nonNull(categories) && !categories.isEmpty() && categories.getFirst() != 0) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }

        if (nonNull(rangeStart)) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }

        if (nonNull(rangeEnd)) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        return specification;
    }

    private static Specification<EventEntity> getEventEntitySpecificationByUser(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort
    ) {
        Specification<EventEntity> specification = Specification.where(null);

        return Specification.where((root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (categories != null && !categories.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, root.get("category").get("id").in(categories));
            }

            if (rangeStart != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
            }

            if (rangeEnd != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
            }

            if (text != null && !text.isEmpty()) {
                String likePattern = "%" + text.toLowerCase() + "%";
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.or(
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), likePattern),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern),
                                criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern)
                        ));
            }

            if (paid != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("paid"), paid));
            }

            if (onlyAvailable != null && onlyAvailable) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("confirmedRequests"), root.get("participantLimit")));
            }

            if (sort != null && !sort.isEmpty()) {
                if ("eventDate".equalsIgnoreCase(sort)) {
                    query.orderBy(criteriaBuilder.asc(root.get("eventDate")));
                } else if ("views".equalsIgnoreCase(sort)) {
                    query.orderBy(criteriaBuilder.desc(root.get("views")));
                }
            }

            return predicate;
        });
    }

    private List<EventFullDto> getEventFullDtos(List<EventEntity> eventEntities, HttpServletRequest request) {
        return eventEntities.stream()
                .map(eventEntity -> {
                    UserEntity userEntity = userRepository.findById(eventEntity.getInitiator().getId())
                            .orElseThrow(() -> new EntityNotFoundException(DefaultMessagesForException.USER_NOT_FOUND));
                    log.info("[DEBUG] For event full dto {}", eventEntity);

                    List<ViewStats> viewStats = getViewStats(request, eventEntity);
                    log.info("[DEBUG] View stats {}", viewStats);

                    Integer confirmedRequests = participationRequestRepository.findByEventId(eventEntity.getId(), CONFIRMED);
                    if (nonNull(confirmedRequests)) {
                        eventEntity.setConfirmedRequests(confirmedRequests);
                    }

                    Long hits = 0L;
                    if (nonNull(viewStats) && !viewStats.isEmpty()) {
                        if (nonNull(viewStats.getFirst())) {
                            hits = viewStats.getFirst().getHits();
                        }
                    }

                    return EventMapper.toEventFullDto(
                            eventEntity,
                            hits,
                            confirmedRequests
                    );
                }).toList();
    }

    private static void isCorrectDate(String date) {
        LocalDateTime localDateTime = DateTimeHelper.fromStringToLocalDateTime(date);
        if (!localDateTime.isAfter(LocalDateTime.now().plusHours(2L)))
            throw new ValidationException("Некорректная дата.");
    }

    private List<ViewStats> getViewStats(HttpServletRequest request, EventEntity eventEntity) {
        log.info("[DEBUG] Get view stats for {}, {}", eventEntity, request.getRequestURI());
        ResponseEntity<Object> responseStats = statsClient.getStats(
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
}
