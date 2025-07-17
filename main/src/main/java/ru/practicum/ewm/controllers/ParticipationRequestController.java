package ru.practicum.ewm.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.participation.ParticipationRequestDto;
import ru.practicum.ewm.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class ParticipationRequestController {
    private final ParticipationRequestService participationRequestService;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable Long userId,
                                                                 @RequestParam Long eventId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                participationRequestService.createRequest(userId, eventId)
        );
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getUserRequests(@PathVariable Long userId) {
        return ResponseEntity.ok().body(participationRequestService.getUserRequests(userId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(
            @PathVariable Long userId,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok().body(participationRequestService.cancelRequest(userId, requestId));
    }
}