package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.event.EventEntity;
import ru.practicum.ewm.model.participation.ParticipationRequestDto;
import ru.practicum.ewm.model.participation.ParticipationRequestEntity;
import ru.practicum.ewm.model.user.UserEntity;

import java.time.LocalDateTime;

@UtilityClass
public class ParticipationRequestMapper {
    public ParticipationRequestEntity toParticipationEntity(EventEntity event,
                                                            UserEntity requester) {
        return ParticipationRequestEntity.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .build();
    }

    public ParticipationRequestDto toParticipationDto(ParticipationRequestEntity participationRequestEntity) {
        return ParticipationRequestDto.builder()
                .id(participationRequestEntity.getId())
                .created(participationRequestEntity.getCreated().toString())
                .event(participationRequestEntity.getEvent().getId())
                .requester(participationRequestEntity.getRequester().getId())
                .status(participationRequestEntity.getStatus().toString())
                .build();
    }
}
