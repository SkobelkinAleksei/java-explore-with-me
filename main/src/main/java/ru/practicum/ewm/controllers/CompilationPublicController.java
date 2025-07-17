package ru.practicum.ewm.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.complitation.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size,
            HttpServletRequest request
    ) {
        return ResponseEntity.ok().body(compilationService.findAll(pinned, from, size, request));
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@PathVariable Long compId, HttpServletRequest request) {
        return ResponseEntity.ok().body(compilationService.getCompilationById(compId, request));
    }
}