package ru.practicum.ewm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.event.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.model.event.EventRequestStatusUpdateResult;
import ru.practicum.ewm.model.participation.ParticipationRequestDto;
import ru.practicum.ewm.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
public class EventParticipantsController {
    private final ParticipationRequestService participationRequestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId
    ) {
        return ResponseEntity.ok().body(participationRequestService.getEventParticipantsByUserAndEventId(userId, eventId));
    }

    @PatchMapping
    public ResponseEntity<EventRequestStatusUpdateResult> updateParticipationRequests(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest
    ) {
        return ResponseEntity.ok().body(
                participationRequestService.updateParticipationRequestsByUserAndEventId(
                        userId,
                        eventId,
                        eventRequestStatusUpdateRequest
                )
        );
    }
}