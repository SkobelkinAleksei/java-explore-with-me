package ru.practicum.ewm.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.event.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {

    @Query("""
            SELECT (COUNT(ee.id) > 0)
            FROM EventEntity as ee
            WHERE ee.id = :eventId
            """)
    boolean isEventEntityExistsById(Long eventId);

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
    """)
    Set<EventEntity> findAllByAdmin(PageRequest pageRequest);

    @EntityGraph(attributePaths = {"initiator", "category", "location"})
    @Query("""
        SELECT ee
        FROM EventEntity as ee
    """)
    Set<EventEntity> findAll(PageRequest pageRequest);

    @Modifying
    @Query("""
        UPDATE EventEntity ee
        SET
            ee.annotation = :#{#updateEventUserRequest.annotation},
            ee.category.id = :#{#updateEventUserRequest.category},
            ee.description = :#{#updateEventUserRequest.description},
            ee.eventDate = :eventDate,
            ee.location.id = :locationId,
            ee.paid = :#{#updateEventUserRequest.paid},
            ee.participantLimit = :#{#updateEventUserRequest.participantLimit},
            ee.requestModeration = :#{#updateEventUserRequest.requestModeration},
            ee.state = :stateAction,
            ee.title = :#{#updateEventUserRequest.title}
        WHERE ee.id = :eventId
        """)
    int updateEventEntity(@Param("eventId") Long eventId,
                          @Param("updateEventUserRequest") UpdateEventUserRequest updateEventUserRequest,
                          LocalDateTime eventDate,
                          Long locationId,
                          State stateAction);

    @Query("""
            SELECT (COUNT(ee.id) > 0)
            FROM EventEntity as ee
            WHERE ee.id = :eventId
            AND ee.initiator.id = :initiatorId
            """)
    boolean isExistsByEventIdAndUserId(Long eventId, Long initiatorId);

}