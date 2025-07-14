package ru.practicum.ewm.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.model.event.EventEntity;
import ru.practicum.ewm.model.location.LocationEntity;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.statsclient.StatsClient;
import ru.practicum.statsdto.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@AllArgsConstructor
public class EventServiceHelper {
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;

    @Transactional
    public LocationEntity checkLocation(LocationEntity locationEntity) {
        Float lat = locationEntity.getLat();
        Float lon = locationEntity.getLon();

        if (locationRepository.isExistsLocation(lat, lon)) {
            return locationRepository.findByLatAndLon(lat, lon);
        }

        return locationRepository.save(locationEntity);
    }

    public List<ViewStats> getViewStats(HttpServletRequest request, EventEntity eventEntity) {
        log.info("[DEBUG] Get view stats for {}, {}", eventEntity, request.getRequestURI());
        ResponseEntity<Object> responseStats = statsClient.getStats(
                eventEntity.getCreatedOn(),
                LocalDateTime.now(),
                List.of(request.getRequestURI()),
                true
        );

        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(
                responseStats.getBody(),
                new TypeReference<>() {
                });
    }

    public LocationEntity checkLocation(Float lat, Float lon) {

        if (locationRepository.isExistsLocation(lat, lon)) {
            return locationRepository.findByLatAndLon(lat, lon);
        }

        return locationRepository.save(new LocationEntity(lat, lon));
    }

    public PageRequest getPageRequest(
            Integer from,
            Integer size
    ) {
        if (nonNull(from)) {
            if (from != 0) from = 0;
        } else from = 0;
        if (nonNull(size)) {
            if (size != 10) size = 10;
        } else size = 10;

        return PageRequest.of(
                from,
                size
        );
    }

    public PageRequest getPageRequestWithSort(Integer from, Integer size, String sort) {
        if (nonNull(from)) {
            if (from != 0) from = 0;
        } else from = 0;
        if (nonNull(size)) {
            if (size != 10) size = 10;
        } else size = 10;

        return PageRequest.of(
                from,
                size > 10 ? 10 : size,
                Sort.by(sort).ascending()
        );
    }
}
