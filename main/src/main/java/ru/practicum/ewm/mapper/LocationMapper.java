package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.model.location.LocationDto;
import ru.practicum.ewm.model.location.LocationEntity;

@UtilityClass
public class LocationMapper {

    public LocationDto toLocationDto(LocationEntity locationEntity) {
        return LocationDto.builder()
                .id(locationEntity.getId())
                .lat(locationEntity.getLat())
                .lon(locationEntity.getLon())
                .build();
    }
}
