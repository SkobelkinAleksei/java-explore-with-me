package ru.practicum.main.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.model.event.EventEntity;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("""
        SELECT ee
        FROM EventEntity as ee
        WHERE ee.id IN :ids
    """)
    Set<EventEntity> findEventEntitiesByIds(List<Long> ids);

    @Query("""
        SELECT ee
        FROM EventEntity as ee
        WHERE ee.initiator.id = :userId
    """)
    Set<EventEntity> findEventEntitiesByUserId(Long userId, PageRequest pageable);
}
//todo валидация, документация, контроллеры, таблицы