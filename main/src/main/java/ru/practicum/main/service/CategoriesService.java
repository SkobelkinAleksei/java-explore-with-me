package ru.practicum.main.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.exeption.CategoryAlreadyExists;
import ru.practicum.main.mapper.CategoryMapper;
import ru.practicum.main.model.category.CategoryDto;
import ru.practicum.main.model.category.CategoryEntity;
import ru.practicum.main.model.category.NewCategoryDto;
import ru.practicum.main.repository.CategoriesRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) throws NumberFormatException {
        log.info("Получение списка категорий. from: {}, size: {}", from, size);

        PageRequest pageRequest = PageRequest.of(
                from != null ? from : 0,
                size != null ? size : 10,
                Sort.by("id")
                        .descending()
        );

        return categoriesRepository.findAll((pageRequest))
                .stream()
                .map(CategoryMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryDto getCategory(Integer catId) throws EntityNotFoundException, NumberFormatException {
        log.info("Получение категории по id: {}", catId);

        return CategoryMapper.toDto(
                categoriesRepository.findById(Long.valueOf(catId))
                .orElseThrow(
                        () -> new EntityNotFoundException("Категория не найдена.")
                )
        );
    }

    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) throws CategoryAlreadyExists {
        log.info("Создание новой категории с названием: {}", newCategoryDto.getName());

        if (categoriesRepository.isCategoryExists(newCategoryDto.getName())) {
            throw new CategoryAlreadyExists("Категория с таким названием уже добавлена.");
        }

        CategoryEntity categoryEntity = categoriesRepository.save(CategoryMapper.toEntity(newCategoryDto));
        log.info("Категория успешно создана с id: {}", categoryEntity.getId());

        return CategoryMapper.toDto(categoryEntity);
    }

    @Transactional
    public void deleteCategory(Long catId) throws EntityNotFoundException {
        if (!categoriesRepository.existsById(catId)) {
            throw new EntityNotFoundException("Категория не найдена");
        }

        log.info("Удаление категории с id: {}", catId);
        categoriesRepository.deleteById(catId);
        log.info("Категория с id: {} успешно удалена", catId);
    }
}
