package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.event.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {

    @Query("""
            SELECT ee
            FROM EventEntity as ee
            WHERE ee.id = :id
            AND ee.state = :state
            """)
    Optional<EventEntity> findPublishedEventById(Long id, State state);

    @Query("""
            SELECT (COUNT(ee.id) > 0)
            FROM EventEntity as ee
            WHERE ee.id = :eventId
            """)
    boolean isEventEntityExistsById(Long eventId);

    @Query("""
            SELECT (COUNT(ee.id) > 0)
            FROM EventEntity as ee
            WHERE  ee.category.id = :catId
            """)
    boolean isCategoryEntityExistsEvents(Long catId);

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

    @Query("""
                SELECT ee
                FROM EventEntity as ee
                WHERE ee.initiator.id = :userId
                AND ee.id = :eventId
            """)
    Optional<EventEntity> findEventEntityByUserId(Long userId, Long eventId);

    @EntityGraph(attributePaths = {"initiator", "category", "location"})
    @Query("""
                SELECT ee
                FROM EventEntity as ee
                WHERE ee.id = :eventId
            """)
    Optional<EventEntity> findEventEntityById(Long eventId);

    @Query("""
            SELECT (COUNT(ee.id) > 0)
            FROM EventEntity as ee
            WHERE ee.id = :eventId
            AND ee.initiator.id = :initiatorId
            """)
    boolean isExistsByEventIdAndUserId(Long eventId, Long initiatorId);

    @Query("""
            SELECT ee
            FROM EventEntity as ee
            WHERE ee.confirmedRequests <= ee.participantLimit
            """)
    Page<EventEntity> findAllWithAvailableLimit(Specification<EventEntity> specification, Pageable pageable);

    Page<EventEntity> findAll(Specification<EventEntity> specification, Pageable pageable);

    @Query("""
            SELECT ee.state
            FROM EventEntity as ee
            WHERE ee.id = :eventId
            """)
    State getEventStatusByEventId(Long eventId);
}