package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.category.CategoryEntity;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.location.LocationEntity;
import ru.practicum.ewm.model.user.UserEntity;
import ru.practicum.ewm.model.user.UserShortDto;
import ru.practicum.ewm.utils.DateTimeHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@UtilityClass
public class EventMapper {

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

    public EventShortDto toShortEventDto(
            EventEntity event, UserShortDto userShortDto,
            Long hits,
            Integer countOfConfirmedRequests
    ) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory().getName())
                .eventDate(event.getEventDate().toString())
                .initiator(userShortDto)
                .views(hits)
                .confirmedRequests(countOfConfirmedRequests)
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public EventFullDto toEventFullDto(
            EventEntity eventEntity,
            Long hits,
            Integer confirmedRequests
    ) {
        State state = eventEntity.getRequestModeration() ? State.PENDING : eventEntity.getState();

        return EventFullDto.builder()
                .id(eventEntity.getId())
                .annotation(eventEntity.getAnnotation())
                .initiator(UserMapper.toUserShortDto(eventEntity.getInitiator()))
                .category(CategoryMapper.toDto(eventEntity.getCategory()))
                .location(LocationMapper.toLocationDto(eventEntity.getLocation()))
                .eventDate(eventEntity.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .createdOn(eventEntity.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .publishedOn(eventEntity.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .state(state.getName())
                .paid(eventEntity.getPaid())
                .requestModeration(eventEntity.getRequestModeration())
                .participantLimit(eventEntity.getParticipantLimit())
                .views(hits)
                .confirmedRequests(confirmedRequests)
                .description(eventEntity.getDescription())
                .title(eventEntity.getTitle())
                .build();
    }

    public EventEntity toEntity(
            NewEventDto newEventDto,
            CategoryEntity categoryEntity,
            LocationEntity locationEntity,
            UserEntity userEntity
    ) {
        if (isNull(newEventDto.getRequestModeration())) {
            newEventDto.setRequestModeration(true);
        }

        return EventEntity.builder()
                .initiator(userEntity)
                .category(categoryEntity)
                .location(locationEntity)
                .eventDate(DateTimeHelper.fromStringToLocalDateTime(newEventDto.getEventDate()))
                .requestModeration(newEventDto.getRequestModeration())
                .state(newEventDto.getRequestModeration() ? State.PENDING : State.CONFIRMED)
                .paid(nonNull(newEventDto.getPaid()) ? newEventDto.getPaid() : false)
                .participantLimit(nonNull(newEventDto.getParticipantLimit()) ? newEventDto.getParticipantLimit() : 0L)
                .annotation(newEventDto.getAnnotation())
                .confirmedRequests(0)
                .description(newEventDto.getDescription())
                .title(newEventDto.getTitle())
                .createdOn(LocalDateTime.now())
                .build();
    }
}

