package ru.practicum.ewm.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.event.*;

import java.time.LocalDateTime;
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
            SELECT ee
            FROM EventEntity as ee
            WHERE ee.initiator.id IN :userId
            """)
    List<EventEntity> findAllEventEntitiesByUserIds(List<Long> userId);

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
            """)
    Set<EventEntity> findAllByAdmin(PageRequest pageRequest);


    @EntityGraph(attributePaths = {"initiator", "category", "location"})
    @Query("""
                SELECT ee
                FROM EventEntity as ee
                WHERE ee.id = :eventId
            """)
    Optional<EventEntity> findEventEntityById(Long eventId);

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

    @Modifying
    @Query("""
            UPDATE EventEntity ee
            SET
                ee.annotation = :#{#updateEventAdminRequest.annotation},
                ee.category.id = :#{#updateEventAdminRequest.category},
                ee.description = :#{#updateEventAdminRequest.description},
                ee.eventDate = :eventDate,
                ee.location.id = :locationId,
                ee.paid = :#{#updateEventAdminRequest.paid},
                ee.participantLimit = :#{#updateEventAdminRequest.participantLimit},
                ee.requestModeration = :#{#updateEventAdminRequest.requestModeration},
                ee.state = :stateAction,
                ee.title = :#{#updateEventAdminRequest.title}
            WHERE ee.id = :eventId
            """)
    int updateEventEntity(@Param("eventId") Long eventId,
                          @Param("updateEventAdminRequest") UpdateEventAdminRequest updateEventAdminRequest,
                          LocalDateTime eventDate,
                          Long locationId,
                          State stateAction
    );

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

//    @Query("""
//            SELECT ee
//            FROM EventEntity as ee
//            """)
    Page<EventEntity> findAll(Specification<EventEntity> specification, Pageable pageable);

}