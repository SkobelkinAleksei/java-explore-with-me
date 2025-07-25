package ru.practicum.ewm.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.model.category.NewCategoryDto;
import ru.practicum.ewm.service.CategoriesService;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoriesService categoriesService;

    @PostMapping
    public ResponseEntity<CategoryDto> createdCategory(@RequestBody @Valid NewCategoryDto categoryDto) {
        log.info("Поступил запрос на создание новой категории с названием: {}", categoryDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriesService.createCategory(categoryDto));
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<HttpStatus> deletedCategory(@PathVariable Long catId) {
        log.info("Поступил запрос на удаление категории с id: {}", catId);
        categoriesService.deleteCategory(catId);
        log.info("Категория с id: {} успешно удалена", catId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updatedCategory(
            @PathVariable Long catId,
            @RequestBody @Valid CategoryDto categoryDto
    ) {
        return ResponseEntity.ok().body(categoriesService.updateCategory(catId, categoryDto));
    }
}
