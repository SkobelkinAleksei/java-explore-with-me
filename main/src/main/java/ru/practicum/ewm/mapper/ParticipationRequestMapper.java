package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.event.EventEntity;
import ru.practicum.ewm.model.event.State;
import ru.practicum.ewm.model.participation.ParticipationRequestDto;
import ru.practicum.ewm.model.participation.ParticipationRequestEntity;
import ru.practicum.ewm.model.user.UserEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class ParticipationRequestMapper {
    public ParticipationRequestEntity toParticipationEntity(
            EventEntity event,
            UserEntity requester
    ) {
        return ParticipationRequestEntity.builder()
                .event(event)
                .requester(requester)
                .created(LocalDateTime.now())
                .status(State.PENDING)
                .build();
    }

    public ParticipationRequestDto toParticipationDto(ParticipationRequestEntity participationRequestEntity) {
        return ParticipationRequestDto.builder()
                .id(participationRequestEntity.getId())
                .created(participationRequestEntity.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .event(participationRequestEntity.getEvent().getId())
                .requester(participationRequestEntity.getRequester().getId())
                .status(participationRequestEntity.getStatus().getName())
                .build();
    }
}
