package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.location.LocationEntity;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {
    @Query("""
            SELECT (COUNT(l) > 0)
            FROM LocationEntity as l
            WHERE l.lat = :lat AND l.lon = :lon
            """)
    boolean isExistsLocation(Float lat, Float lon);

    @Query("""
            SELECT l
            FROM LocationEntity as l
            WHERE l.lat = :lat AND l.lon = :lon
            """)
    LocationEntity findByLatAndLon(Float lat, Float lon);

    @Query("""
            SELECT l.id
            FROM LocationEntity as l
            WHERE l.lat = :lat AND l.lon = :lon
            """)
    Long findEntityIdByLatAndLon(Float lat, Float lon);
}
