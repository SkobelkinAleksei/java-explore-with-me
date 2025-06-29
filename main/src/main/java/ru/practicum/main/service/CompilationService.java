package ru.practicum.main.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.mapper.CompilationMapper;
import ru.practicum.main.mapper.EventMapper;
import ru.practicum.main.mapper.UserMapper;
import ru.practicum.main.model.complitation.CompilationDto;
import ru.practicum.main.model.complitation.CompilationEntity;
import ru.practicum.main.model.complitation.NewCompilationDto;
import ru.practicum.main.model.complitation.UpdateCompilationRequest;
import ru.practicum.main.model.event.EventEntity;
import ru.practicum.main.model.event.EventShortDto;
import ru.practicum.main.model.user.UserEntity;
import ru.practicum.main.repository.CompilationRepository;
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public CompilationDto saveCompilation(NewCompilationDto compilation) throws DataIntegrityViolationException {
        log.info("Создание новой подборки: {}", compilation);
        CompilationEntity compilationEntity = CompilationMapper.toEntity(compilation);

        if (compilation.getEvents() == null) {
            throw new IllegalArgumentException("События не найдены.");
        }

        Set<EventEntity> eventEntitiesByIds = eventRepository.findEventEntitiesByIds(compilation.getEvents());
        log.debug("Найдено {} событий для подборки", eventEntitiesByIds.size());

        compilationEntity.setEvents(eventEntitiesByIds);
        CompilationEntity savedEntity = compilationRepository.save(compilationEntity);
        log.info("Подборка сохранена с id: {}", savedEntity.getId());

        List<EventShortDto> eventShortDtos = getEventShortDtos(eventEntitiesByIds);
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
    public CompilationDto updateCompilation(Long id, UpdateCompilationRequest compilationRequest) {
        log.info("Обновление подборки с id: {}. Данные обновления: {}", id, compilationRequest);

        CompilationEntity compilationEntity = compilationRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Подборка не была найдена.")
        );
        log.debug("Найдена подборка: {}", compilationEntity);

        Set<EventEntity> eventEntitiesByIds = eventRepository.findEventEntitiesByIds(compilationRequest.getEventsId());
        log.debug("Обновление событий подборки. Найдено {} событий", eventEntitiesByIds.size());

        CompilationEntity updatedCompilation = compilationRepository.save(
                CompilationMapper.toUpdateEntity(compilationRequest, compilationEntity, eventEntitiesByIds)
        );
        log.info("Подборка с id: {} обновлена", updatedCompilation.getId());

        List<EventShortDto> eventShortDtos = getEventShortDtos(eventEntitiesByIds);
        log.debug("Создан список EventShortDtos для обновленной подборки");

        return CompilationMapper.toDto(updatedCompilation, eventShortDtos);
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

                    return EventMapper.toShortEventDto(event, UserMapper.toUserShortDto(userEntity));
                })
                .toList();
    }
}
