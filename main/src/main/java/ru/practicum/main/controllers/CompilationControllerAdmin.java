package ru.practicum.main.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.model.complitation.CompilationDto;
import ru.practicum.main.model.complitation.NewCompilationDto;
import ru.practicum.main.model.complitation.UpdateCompilationRequest;
import ru.practicum.main.service.CompilationService;

@Slf4j
@RestController
@RequestMapping("/admin/compilation")
@RequiredArgsConstructor
public class CompilationControllerAdmin {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> saveCompilation(NewCompilationDto compilationDto) {
        log.info("Поступил запрос на создание новой подборки");
        return ResponseEntity.ok().body(
                compilationService.saveCompilation(compilationDto)
        );
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<HttpStatus> deleteCompilation(@PathVariable Long compId) {
        log.info("Поступил запрос на удаление подборки с id: {}", compId);
        compilationService.deleteCompilation(compId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{compId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Long compId,
                                                            @RequestBody UpdateCompilationRequest updateCompilation) {
        log.info("Поступил запрос на обновление подборки с id: {}", compId);
        return ResponseEntity.ok().body(compilationService.updateCompilation(compId, updateCompilation));
    }
}
