package ru.practicum.statsservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsdto.EndpointHitDto;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> saveHits(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(statsService.saveHits(endpointHitDto));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        return ResponseEntity.ok(statsService.findStats(start, end, uris, unique));
    }
}