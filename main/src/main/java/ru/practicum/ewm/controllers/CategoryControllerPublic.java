package ru.practicum.ewm.controllers;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.service.CategoriesService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryControllerPublic {
    private final CategoriesService categoriesService;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10", required = false) @Positive Integer size
    ) {
        log.info("Поступил запрос на список категорий. from: {}, size: {}", from, size);

        return ResponseEntity.ok().body(categoriesService.getCategories(from, size));
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Integer catId) {
        log.info("Поступил запрос на поиск категории по id: {}", catId);
        return ResponseEntity.ok().body(categoriesService.getCategory(catId));
    }
}
