package ru.practicum.ewm.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.complitation.CompilationEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompilationRepository extends JpaRepository<CompilationEntity, Long> {

    @EntityGraph(attributePaths = {"events"})
    @Query("""
            SELECT ce
            FROM CompilationEntity as ce
            WHERE ce.pinned = true
            """)
    List<CompilationEntity> findAllPinned(Boolean pinned, PageRequest pageRequest);

    @EntityGraph(attributePaths = {"events"})
    @Query("""
            SELECT ce
            FROM CompilationEntity as ce
            """)
    List<CompilationEntity> findAllWithPagination(PageRequest pageRequest);

    @EntityGraph(attributePaths = {"events"})
    @Query("""
            SELECT ce
            FROM CompilationEntity as ce
            WHERE ce.id = :compId
            """)
    Optional<CompilationEntity> findByIdCompilation(Long compId);
}
