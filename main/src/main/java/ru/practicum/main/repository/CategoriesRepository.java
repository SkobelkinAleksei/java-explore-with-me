package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.model.category.CategoryEntity;

@Repository
public interface CategoriesRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("""
        SELECT (COUNT(c.name) > 0 )
        FROM CategoryEntity as c
        WHERE c.name = :name
    """)
    boolean isCategoryExists(String name);
}
