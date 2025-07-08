package ru.practicum.ewm.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.EventFullDto;
import ru.practicum.ewm.model.event.EventShortDto;
import ru.practicum.ewm.model.event.NewEventDto;
import ru.practicum.ewm.model.event.UpdateEventUserRequest;
import ru.practicum.ewm.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class UserPrivateController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@PathVariable Long userId,
                                                    @RequestBody @Valid NewEventDto newEventDto
    ) {
        log.info("Поступил запрос на создание нового события от пользователя с id: {}", userId);
        return ResponseEntity.ok().body(eventService.createEvent(userId, newEventDto));
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEvents(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size
    ) {
        log.info("Поступил запрос на список событий пользователя с id: {}. from: {}, size: {}", userId, from, size);
        return ResponseEntity.ok().body(eventService.getEventsByPrivateUser(userId, from, size));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long userId,
                                                 @PathVariable Long eventId,
                                                 HttpServletRequest servletRequest
    ) {
        return ResponseEntity.ok().body(eventService.getEventById(userId, eventId, servletRequest));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByUserIdAndEventId(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody UpdateEventUserRequest updateEventUserRequest,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok().body(
                eventService.updateEventByUserIdAndEventId(userId, eventId, updateEventUserRequest, request)
        );
    }

}
