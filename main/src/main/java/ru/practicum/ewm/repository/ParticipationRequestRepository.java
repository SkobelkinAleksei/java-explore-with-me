package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.event.State;
import ru.practicum.ewm.model.participation.ParticipationRequestEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequestEntity, Long> {

    @Query("""
            SELECT (COUNT(pre.id) > 0)
            FROM ParticipationRequestEntity as pre
            WHERE pre.requester.id = :requesterId
            AND pre.event.id = :eventId
            """)
    boolean isExistsByRequesterAndEventId(Long requesterId, Long eventId);

    @EntityGraph(attributePaths = {"event"})
    @Query("""
            SELECT pre
            FROM ParticipationRequestEntity as pre
            WHERE pre.requester.id = :requesterId            
            """)
    Optional<ParticipationRequestEntity> findByRequesterId(Long requesterId);

    @Query("""
            SELECT pre
            FROM ParticipationRequestEntity as pre
            WHERE pre.requester.id = :requesterId
            AND pre.event.id = :eventId
            """)
    Optional<ParticipationRequestEntity> isExists(Long requesterId, Long eventId);

    @Query("""
            SELECT pre
            FROM ParticipationRequestEntity as pre
            WHERE pre.requester.id = :id
            """)
    List<ParticipationRequestEntity> getAllByRequesterId(@Param("id") Long requesterId);

    @Query("""
            SELECT pre
            FROM ParticipationRequestEntity as pre
            WHERE pre.event.id = :eventId
            """)
    List<ParticipationRequestEntity> getAllByRequesterAndEventId(Long eventId);

    @Query("""
            SELECT pre
            FROM ParticipationRequestEntity as pre
            WHERE pre.id IN :requestsIs
            AND pre.event.id = :eventId
            AND pre.status = :status
            """)
    List<ParticipationRequestEntity> findAllPendingRequestsConfirmed(List<Long> requestsIs,
                                                                     Long eventId,
                                                                     State status
    );

    @Query("""
            SELECT COUNT(pre.id)
            FROM ParticipationRequestEntity as pre
            WHERE pre.event.id = :eventId
            """)
    Long findCountOfRequests(Long eventId);

    @Query("""
            SELECT COUNT(pre.id)
            FROM ParticipationRequestEntity as pre
            WHERE pre.event.id = :eventId
            AND pre.status = :status
            """)
    Integer findCountOfConfirmedRequests(Long eventId, State status);

    @Query("""
            SELECT (COUNT(pre.id) > 0)
            FROM ParticipationRequestEntity as pre
            WHERE pre.id = :requestId
            AND pre.requester.id = :userId
            """)
    boolean isParticipationRequestExistsByUserIdAndRequestId(Long userId, Long requestId);

    @Query("""
            SELECT (COUNT(pre.event.id))
            FROM ParticipationRequestEntity as pre
            WHERE pre.event.id = :eventId
                         AND pre.status = :state
            """)
    Integer findByEventId(Long eventId, State state);
}
