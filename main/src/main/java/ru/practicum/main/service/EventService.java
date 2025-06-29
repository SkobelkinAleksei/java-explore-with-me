package ru.practicum.main.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.mapper.CategoryMapper;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.mapper.UserMapper;
import ru.practicum.main.model.category.CategoryEntity;
import ru.practicum.main.model.event.EventEntity;
import ru.practicum.main.model.event.EventFullDto;
import ru.practicum.main.model.event.EventShortDto;
import ru.practicum.main.model.event.NewEventDto;
import ru.practicum.main.model.location.LocationEntity;
import ru.practicum.main.model.user.UserEntity;
import ru.practicum.main.repository.CategoriesRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.LocationRepository;
import ru.practicum.main.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(
            Long userId,
            Integer from,
            Integer size
    ) {
        log.info("Запрос на получение событий пользователя с id: {}, from: {}, size: {}", userId, from, size);

        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не был найден."));
        log.debug("Найден пользователь: {}", userEntity);

        PageRequest pageRequest = PageRequest.of(
                from != null ? from : 0,
                size != null ? size : 10,
                Sort.by("id")
                        .descending()
        );

        Set<EventEntity> eventEntitiesByUserId = eventRepository.findEventEntitiesByUserId(
                userId,
                pageRequest
        );
        log.info("Найдено {} событий для пользователя с id: {}", eventEntitiesByUserId.size(), userId);

        return eventEntitiesByUserId.isEmpty() ? Collections.emptyList() : eventEntitiesByUserId.stream()
                .map(event -> EventMapper.toShortEventDto(event, UserMapper.toUserShortDto(userEntity)))
                .toList();
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
                savedLocationEntity
        );
    }
}
