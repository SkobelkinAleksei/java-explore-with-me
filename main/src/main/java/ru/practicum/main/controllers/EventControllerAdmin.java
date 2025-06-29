package ru.practicum.main.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.service.EventService;

@Slf4j
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventControllerAdmin {
    private final EventService eventService;

//    @GetMapping
//    public ResponseEntity<E>
}
