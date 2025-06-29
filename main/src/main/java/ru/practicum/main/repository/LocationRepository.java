package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.model.location.LocationEntity;

@Repository
public interface LocationRepository extends JpaRepository<LocationEntity, Long> {

}
