package ru.practicum.ewm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.complitation.CompilationDto;
import ru.practicum.ewm.model.complitation.NewCompilationDto;
import ru.practicum.ewm.model.complitation.UpdateCompilationRequest;
import ru.practicum.ewm.service.CompilationService;

@Slf4j
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> saveCompilation(@RequestBody @Valid NewCompilationDto compilationDto) {
        log.info("Поступил запрос на создание новой подборки");
        return ResponseEntity.status(HttpStatus.CREATED).body(
                compilationService.saveCompilation(compilationDto)
        );
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<HttpStatus> deleteCompilation(@PathVariable Long compId) {
        log.info("Поступил запрос на удаление подборки с id: {}", compId);
        compilationService.deleteCompilation(compId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId,
                                                            @RequestBody @Valid UpdateCompilationRequest updateCompilation) {
        log.info("Поступил запрос на обновление подборки с id: {}", compId);
        return ResponseEntity.ok().body(compilationService.updateCompilation(compId, updateCompilation));
    }
}
