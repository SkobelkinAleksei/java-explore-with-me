package ru.practicum.ewm.utils;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.location.LocationEntity;
import ru.practicum.ewm.repository.LocationRepository;

@Component
@AllArgsConstructor
public class EventServiceHelper {

    private final LocationRepository locationRepository;

    @Transactional
    public LocationEntity checkLocation(LocationEntity locationEntity) {
        Float lat = locationEntity.getLat();
        Float lon = locationEntity.getLon();

        if (locationRepository.isExistsLocation(lat, lon)) {
            return locationRepository.findByLatAndLon(lat, lon);
        }

        return locationRepository.save(locationEntity);
    }

    public LocationEntity checkLocation(Float lat, Float lon) {

        if (locationRepository.isExistsLocation(lat, lon)) {
            return locationRepository.findByLatAndLon(lat, lon);
        }

        return locationRepository.save(new LocationEntity(lat, lon));
    }

    public PageRequest getPageRequest(Integer from, Integer size) {
        return PageRequest.of(
                from != null ? from : 0,
                size != null ? size : 10,
                Sort.by("id")
                        .descending()
        );
    }

    public PageRequest getPageRequestWithSort(Integer from, Integer size, String sort) {
        return PageRequest.of(
                from != null ? from : 0,
                size != null ? size : 10,
                Sort.by(sort)
                        .descending()
        );
    }

}
