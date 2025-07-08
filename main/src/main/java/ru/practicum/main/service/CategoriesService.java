package ru.practicum.main.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
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
import ru.practicum.main.repository.EventRepository;
import ru.practicum.main.utils.DefaultMessagesForException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriesService {
    private final CategoriesRepository categoriesRepository;
    private final EventRepository eventRepository;

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
    public CategoryDto createCategory(
            NewCategoryDto newCategoryDto
    ) throws ConstraintViolationException {
        log.info("Создание новой категории с названием: {}", newCategoryDto.getName());
        CategoryEntity categoryEntity = categoriesRepository.save(CategoryMapper.toEntity(newCategoryDto));
        log.info("Категория успешно создана с id: {}", categoryEntity.getId());

        return CategoryMapper.toDto(categoryEntity);
    }

    @Transactional
    public void deleteCategory(Long catId) throws NumberFormatException {
        if (!categoriesRepository.isCategoryExistsById(catId))
            throw new EntityNotFoundException(DefaultMessagesForException.CATEGORY_NOT_FOUND);

        if (eventRepository.isEventEntityExistsById(catId))
            throw new IllegalArgumentException(DefaultMessagesForException.CANNOT_DELETE_CATEGORY_WITH_EVENTS);

        log.info("Удаление категории с id: {}", catId);
        categoriesRepository.deleteById(catId);
        log.info("Категория с id: {} успешно удалена", catId);
    }

    @Transactional
    public CategoryDto updateCategory(
            Long catId, CategoryDto categoryDto
    ) throws ConstraintViolationException, NumberFormatException {
        CategoryEntity categoryEntity = categoriesRepository.findById(catId)
                .orElseThrow(() ->
                        new CategoryAlreadyExists("Категория не была найдена.")
                );

        categoriesRepository.updateCategoryEntity(catId, categoryDto.getName());

        return CategoryMapper.toDto(categoryEntity);
    }
}
