package ru.practicum.statsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.statsdto.ViewStats;
import ru.practicum.statsservice.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("""
        SELECT new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, COUNT(eh.uri))
        FROM EndpointHit as eh
        WHERE eh.created BETWEEN ?1 AND ?2
        GROUP BY eh.app, eh.uri
        ORDER BY COUNT(eh.uri) DESC
     """)
    List<ViewStats> findNoUniqueAndNoUrisStats(LocalDateTime start, LocalDateTime end);

    @Query("""
        SELECT new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip))
        FROM EndpointHit as eh
        WHERE eh.uri IN (?1) AND eh.created BETWEEN ?2 AND ?3
        GROUP BY eh.app, eh.uri
        ORDER BY COUNT(DISTINCT eh.ip) DESC
     """)
    List<ViewStats> findUniqueWithUrisStats(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("""
        SELECT new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip))
        FROM EndpointHit as eh
        WHERE eh.created BETWEEN ?1 AND ?2
        GROUP BY eh.app, eh.uri
        ORDER BY COUNT(DISTINCT eh.ip) DESC
     """)
    List<ViewStats> findUniqueAndNoUrisStats(LocalDateTime start, LocalDateTime end);

    @Query("""
        SELECT new ru.practicum.statsdto.ViewStats(eh.app, eh.uri, COUNT(eh.uri))
        FROM EndpointHit as eh
        WHERE eh.uri IN (?1) AND eh.created BETWEEN ?2 AND ?3
        GROUP BY eh.app, eh.uri
        ORDER BY COUNT(eh.uri) DESC
     """)
    List<ViewStats> findNoUniqueWithUrisStats(List<String> uris, LocalDateTime start, LocalDateTime end);
}
