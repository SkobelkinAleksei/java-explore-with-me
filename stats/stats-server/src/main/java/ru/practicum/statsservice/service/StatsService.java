package ru.practicum.statsservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.mapper.EndpointHitMapper;
import ru.practicum.statsservice.model.EndpointHit;
import ru.practicum.statsservice.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    @Transactional
    public EndpointHitDto saveHits(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = statsRepository.save(EndpointHitMapper.toEntity(endpointHitDto));

        return EndpointHitMapper.toDto(endpointHit);
    }

    @Transactional(readOnly = true)
    public List<ViewStats> findStats(LocalDateTime start,
                                     LocalDateTime end,
                                     List<String> uris,
                                     Boolean unique) {
        log.info("Вызываем метод findStats с параметрами %t %t %d %b", start, end, uris, unique);

        if (!isDataCorrect(start, end)) throw new IllegalArgumentException("Неверный формат даты");

        if (unique) {
            if (uris != null) {
                return statsRepository.findUniqueWithUrisStats(uris, start, end);
            }

            return statsRepository.findUniqueAndNoUrisStats(start, end);
        } else {
            if (uris != null) {
                return statsRepository.findNoUniqueWithUrisStats(uris, start, end);
            }

            return statsRepository.findNoUniqueAndNoUrisStats(start, end);
        }
    }

    private Boolean isDataCorrect(LocalDateTime start,
                                  LocalDateTime end) {
        return start.isAfter(end);
    }
}
