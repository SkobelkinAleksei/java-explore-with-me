package ru.practicum.main.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.model.category.CategoryDto;
import ru.practicum.main.model.category.NewCategoryDto;
import ru.practicum.main.service.CategoriesService;

@Slf4j
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {
    private final CategoriesService categoriesService;

    @PostMapping
    public ResponseEntity<CategoryDto> createdCategory(@RequestBody @Valid NewCategoryDto categoryDto) {
        log.info("Поступил запрос на создание новой категории с названием: {}", categoryDto.getName());
        return ResponseEntity.ok().body(categoriesService.createCategory(categoryDto));
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable Long catId) {
        log.info("Поступил запрос на удаление категории с id: {}", catId);
        categoriesService.deleteCategory(catId);
        log.info("Категория с id: {} успешно удалена", catId);
        return ResponseEntity.noContent().build();
    }

}
