package ru.practicum.ewm.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.UpdateEventAdminRequest;
import ru.practicum.ewm.model.event.EventFullDto;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventControllerAdmin {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(required = false, defaultValue = "10") @Positive Integer size,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok().body(
                eventService.getEventsByAdmin(
                        users,
                        states,
                        categories,
                        rangeStart,
                        rangeEnd,
                        from,
                        size,
                        request)
        );
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(
            @PathVariable Long eventId,
            @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok().body(eventService.updateEvent(eventId, updateEventAdminRequest, request));
    }
}