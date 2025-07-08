package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.model.location.LocationDto;
import ru.practicum.main.model.location.LocationEntity;

@UtilityClass
public class LocationMapper {

    public LocationDto toLocationDto(LocationEntity locationEntity) {
        return LocationDto.builder()
                .lat(locationEntity.getLat())
                .lon(locationEntity.getLon())
                .build();
    }
}
