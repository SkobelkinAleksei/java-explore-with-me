package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.category.CategoryEntity;
import ru.practicum.ewm.model.event.*;
import ru.practicum.ewm.model.location.LocationEntity;
import ru.practicum.ewm.model.user.UserEntity;
import ru.practicum.ewm.model.user.UserShortDto;
import ru.practicum.ewm.utils.DateTimeHelper;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@UtilityClass
public class EventMapper {

    public EventShortDto toAdminShortEventDto(EventEntity event, UserShortDto userShortDto) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .eventDate(DateTimeHelper.fromDateToLocalDateTime(event.getEventDate()))
                .initiator(userShortDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
    }

    public EventShortDto toShortEventDto(
            EventEntity event,
            Long hits,
            Integer countOfConfirmedRequests
    ) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .eventDate(DateTimeHelper.fromDateToLocalDateTime(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
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

        return EventFullDto.builder()
                .id(eventEntity.getId())
                .annotation(eventEntity.getAnnotation())
                .initiator(UserMapper.toUserShortDto(eventEntity.getInitiator()))
                .category(CategoryMapper.toDto(eventEntity.getCategory()))
                .location(LocationMapper.toLocationDto(eventEntity.getLocation()))
                .eventDate(DateTimeHelper.fromDateToLocalDateTime(eventEntity.getEventDate()))
                .createdOn(DateTimeHelper.fromDateToLocalDateTime(eventEntity.getCreatedOn()))
                .publishedOn(DateTimeHelper.fromDateToLocalDateTime(eventEntity.getCreatedOn()))
                .state(eventEntity.getState().getName())
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
                .state(State.PENDING)
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