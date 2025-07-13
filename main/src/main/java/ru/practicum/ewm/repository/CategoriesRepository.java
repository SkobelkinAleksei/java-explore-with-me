package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.category.CategoryEntity;

@Repository
public interface CategoriesRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("""
                SELECT (COUNT(c.id) > 0 )
                FROM CategoryEntity as c
                WHERE c.id = :catId
            """)
    boolean isCategoryExistsById(Long catId);

    @Query("""
                SELECT (COUNT(c.id) > 0 )
                FROM CategoryEntity as c
                WHERE c.name = :catName
            """)
    boolean isCategoryExistsByName(String catName);

    @Modifying
    @Query("""
                UPDATE CategoryEntity as ce
                SET ce.name = :categoryName
                WHERE ce.id = :catId
            """)
    Integer updateCategoryEntity(Long catId, String categoryName);
}
