package ru.practicum.statsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
               SELECT new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, COUNT(eh.ip))
               FROM EndpointHit as eh
               WHERE eh.created BETWEEN :start AND :end
               GROUP BY eh.app, eh.uri
               ORDER BY COUNT(eh.uri) DESC
            """)
    List<ViewStats> findNoUniqueAndNoUrisStats(LocalDateTime start, LocalDateTime end);

    @Query("""
               SELECT new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip))
               FROM EndpointHit as eh
               WHERE eh.uri IN :uris AND eh.created BETWEEN :start AND :end
               GROUP BY eh.app, eh.uri
               ORDER BY COUNT(eh.ip) DESC
            """)
    List<ViewStats> findUniqueWithUrisStats(
            @Param("uris") List<String> uris,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
               SELECT new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip))
               FROM EndpointHit as eh
               WHERE eh.created BETWEEN :start AND :end
               GROUP BY eh.app, eh.uri
               ORDER BY COUNT(eh.ip) DESC
            """)
    List<ViewStats> findUniqueAndNoUrisStats(LocalDateTime start, LocalDateTime end);

    @Query("""
               SELECT new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, COUNT(eh.ip))
               FROM EndpointHit as eh
               WHERE eh.uri IN :uris AND eh.created BETWEEN :start AND :end
               GROUP BY eh.app, eh.uri
               ORDER BY COUNT(eh.uri) DESC
            """)
    List<ViewStats> findNoUniqueWithUrisStats(List<String> uris, LocalDateTime start, LocalDateTime end);
}