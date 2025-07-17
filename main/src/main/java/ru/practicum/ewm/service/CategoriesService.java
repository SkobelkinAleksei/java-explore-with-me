package ru.practicum.ewm.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exeption.CategoryAlreadyExists;
import ru.practicum.ewm.exeption.ForbiddenException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.model.category.CategoryEntity;
import ru.practicum.ewm.model.category.NewCategoryDto;
import ru.practicum.ewm.repository.CategoriesRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.utils.DefaultMessagesForException;

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
        if (categoriesRepository.isCategoryExistsByName(newCategoryDto.getName())) {
            throw new ForbiddenException(DefaultMessagesForException.CATEGORY_ALREADY_EXISTS);
        }
        CategoryEntity categoryEntity = categoriesRepository.save(CategoryMapper.toEntity(newCategoryDto));
        log.info("Категория успешно создана с id: {}", categoryEntity.getId());

        return CategoryMapper.toDto(categoryEntity);
    }

    @Transactional
    public void deleteCategory(Long catId) throws NumberFormatException {
        if (!categoriesRepository.isCategoryExistsById(catId))
            throw new EntityNotFoundException(DefaultMessagesForException.CATEGORY_NOT_FOUND);

        if (eventRepository.isCategoryEntityExistsEvents(catId))
            throw new ForbiddenException(DefaultMessagesForException.CANNOT_DELETE_CATEGORY_WITH_EVENTS);

        log.info("Удаление категории с id: {}", catId);
        categoriesRepository.deleteById(catId);
        log.info("Категория с id: {} успешно удалена", catId);
    }

    @Transactional
    public CategoryDto updateCategory(
            Long catId,
            CategoryDto categoryDto
    ) throws ConstraintViolationException, NumberFormatException {
        CategoryEntity categoryEntity = categoriesRepository.findById(catId)
                .orElseThrow(() ->
                        new CategoryAlreadyExists("Категория не была найдена.")
                );

        if (categoriesRepository.isCategoryExistsByName(categoryDto.getName()) && !categoryEntity.getName().equals(categoryDto.getName())) {
            throw new ForbiddenException("Категория с таким названием уже существует.");
        }
        categoryEntity.setName(categoryDto.getName());
        return CategoryMapper.toDto(categoriesRepository.save(categoryEntity));
    }
}
