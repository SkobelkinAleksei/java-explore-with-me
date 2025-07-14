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

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Objects.nonNull;

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
    public List<ViewStats> findStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique
    ) throws DateTimeException {
        log.info("Вызываем метод findStats с параметрами {}, {}, {}, {}", start, end, uris, unique);
        if (isDataCorrect(start, end)) throw new DateTimeException("Неверный формат даты");
        if (unique) {
            log.info("Запрос на получение уникальных id");
            if (uris != null) {
                List<ViewStats> uniqueWithUrisStats = statsRepository.findUniqueWithUrisStats(uris, start, end);
                log.info("Получили список уникальных просмотров {}", uniqueWithUrisStats);
                return uniqueWithUrisStats;
            }else{
                return statsRepository.findUniqueAndNoUrisStats(start, end);
            }

        } else {
            log.info("Запрос на получение не уникальных ip");
            if (uris != null) {
                List<ViewStats> noUniqueWithUrisStats = statsRepository.findNoUniqueWithUrisStats(uris, start, end);
                log.info("Получили список не уникальных просмотров {}", noUniqueWithUrisStats);
                return noUniqueWithUrisStats;
            }else{
                return statsRepository.findNoUniqueAndNoUrisStats(start, end);
            }

        }
    }

    private Boolean isDataCorrect(LocalDateTime start,
                                  LocalDateTime end) {
        return start.isAfter(end);
    }

}
