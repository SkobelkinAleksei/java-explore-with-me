package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.model.category.CategoryEntity;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.location.LocationDto;
import ru.practicum.ewm.model.location.LocationEntity;
import ru.practicum.ewm.model.user.UserEntity;
import ru.practicum.ewm.model.user.UserShortDto;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;

@UtilityClass
public class EventMapper {
    private final Integer defaultViews = 0;
    private final Integer confirmedRequests = 0;

    public EventShortDto toAdminShortEventDto(EventEntity event, UserShortDto userShortDto) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory().getName())
                .eventDate(event.getEventDate().toString())
                .initiator(userShortDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public EventShortDto toShortEventDto(EventEntity event, UserShortDto userShortDto) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory().getName())
                .eventDate(event.getEventDate().toString())
                .initiator(userShortDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public EventFullDto toEventFullDto(EventEntity eventEntity,
                                       UserShortDto userShortDto,
                                       CategoryDto categoryDto,
                                       LocationDto location,
                                       Long hits
    ) {

        return EventFullDto.builder()
                .id(eventEntity.getId())
                .annotation(eventEntity.getAnnotation())
                .initiator(userShortDto)
                .category(categoryDto)
                .location(location)
                .eventDate(eventEntity.getEventDate().toString())
                .createdOn(eventEntity.getCreatedOn().toString())
                .publishedOn(eventEntity.getPublishedOn() != null ? eventEntity.getPublishedOn().toString() : null)
                .state(eventEntity.getState().getName() != null ? eventEntity.getState().getName() : null)
                .paid(eventEntity.getPaid())
                .requestModeration(eventEntity.getRequestModeration())
                .participantLimit(eventEntity.getParticipantLimit())
                .views(isNull(hits) ? defaultViews : hits)
                .confirmedRequests(confirmedRequests)
                .description(eventEntity.getDescription())
                .title(eventEntity.getTitle())
                .build();
    }

    public EventEntity toEntity(NewEventDto newEventDto,
                                CategoryEntity categoryEntity,
                                LocationEntity locationEntity,
                                UserEntity userEntity
    ) {
        State pending = null;

        if (newEventDto.getRequestModeration()) {
            pending = State.valueOf("PENDING");
        }
        return EventEntity.builder()
                .initiator(userEntity)
                .category(categoryEntity)
                .location(locationEntity)
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate()))
                .state(pending)
                .paid(false)
                .requestModeration(true)
                .participantLimit(0)
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .title(newEventDto.getTitle())
                .createdOn(LocalDateTime.now())
                .build();
    }
}
