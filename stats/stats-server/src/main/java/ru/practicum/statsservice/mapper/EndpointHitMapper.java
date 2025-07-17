package ru.practicum.statsservice.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsservice.model.EndpointHit;

@UtilityClass
public class EndpointHitMapper {

    public EndpointHit toEntity(EndpointHitDto endpointHitDto) {
        return new EndpointHit(
                endpointHitDto.getApp(),
                endpointHitDto.getUri(),
                endpointHitDto.getIp(),
                endpointHitDto.getTimestamp()
        );
    }

    public EndpointHitDto toDto(EndpointHit endpointHit) {
        return new EndpointHitDto(
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getCreated()
        );
    }
}