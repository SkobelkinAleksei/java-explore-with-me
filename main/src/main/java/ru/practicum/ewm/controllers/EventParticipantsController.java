package ru.practicum.ewm.controllers;

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
@RequestMapping("/users/{userId}/events/{eventId}")
@RequiredArgsConstructor
public class EventParticipantsController {
    private final ParticipationRequestService participationRequestService;

    @GetMapping("/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventParticipants(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        return ResponseEntity.ok().body(participationRequestService.getEventParticipantsByUserAndEventId(userId, eventId));
    }


    @PatchMapping("/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateParticipationRequests(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest
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
