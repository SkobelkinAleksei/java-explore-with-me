package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.model.category.CategoryDto;
import ru.practicum.main.model.category.CategoryEntity;
import ru.practicum.main.model.category.NewCategoryDto;

@UtilityClass
public class CategoryMapper {
    public CategoryEntity toEntity(NewCategoryDto newCategory) {
        return CategoryEntity.builder()
                .name(newCategory.getName())
                .build();
    }

    public CategoryDto toDto(CategoryEntity categoryEntity) {
        return CategoryDto.builder()
                .id(categoryEntity.getId())
                .name(categoryEntity.getName())
                .build();
    }
}
