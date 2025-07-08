package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.event.State;
import ru.practicum.ewm.model.participation.ParticipationRequestDto;
import ru.practicum.ewm.model.participation.ParticipationRequestEntity;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequestEntity, Long> {

    @Query("""
            SELECT (COUNT(pre.id) > 0)
            FROM ParticipationRequestEntity as pre
            WHERE pre.requester.id = :requesterId
            AND pre.event.id = :eventId
            """)
    boolean isExistsByRequesterAndEventId(Long requesterId, Long eventId);

    @Query("""
            SELECT pre
            FROM ParticipationRequestEntity as pre
            WHERE pre.requester.id = :id
            """)
    List<ParticipationRequestEntity> getAllByRequesterId(@Param("id") Long requesterId);

    @Query(
            value = """
                    SELECT
                     pre.id,
                     TO_CHAR(pre.created_at, 'YYYY-MM-DD HH24:MI:SS'),
                     pre.event_id,
                     pre.requester_id,
                     pre.status::text
                    FROM participation_request as pre
                    WHERE pre.requester_id = ?
                    AND pre.event_id = ?
                    """,
            nativeQuery = true)
    List<ParticipationRequestDto> getAllByRequesterAndEventId(Long requesterId, Long eventId);

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
            AND pre.status = :status
            """)
    Long findCountOfConfirmedRequests(Long eventId, State status);

    @Query("""
            SELECT (COUNT(pre.id) > 0)
            FROM ParticipationRequestEntity as pre
            WHERE pre.id = :requestId
            AND pre.requester.id = :userId
            """)
    boolean isParticipationRequestExistsByUserIdAndRequestId(Long userId, Long requestId);
}
