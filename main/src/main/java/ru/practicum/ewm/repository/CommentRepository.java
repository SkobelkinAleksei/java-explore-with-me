package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.comment.CommentEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query("""
            SELECT ce
            FROM CommentEntity as ce
            WHERE ce.id = :commentId
            """)
    Optional<CommentEntity> findCommentEntityById(Long commentId);

    @Query("""
            SELECT (COUNT(ce.id) > 0)
            FROM CommentEntity as ce
            JOIN EventEntity as ee
                        ON ce.eventId = ee.id
            WHERE ce.eventId = :eventId
                        AND ce.userId = :userId
            """)
    boolean isExistsByEventIdAndUserId(Long eventId, Long userId);

    @Query("""
                    SELECT ce
                    FROM CommentEntity as ce
                    WHERE ce.eventId = :eventId
                    AND ce.userId = :userId
                    ORDER BY ce.createdAt ASC
            """)
    List<CommentEntity> findAllCommentsForEventByUserId(Long eventId, Long userId);

    List<CommentEntity> findAllCommentsByEventId(Long eventId);
}