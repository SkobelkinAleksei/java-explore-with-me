package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.main.model.complitation.CompilationEntity;

@Repository
public interface CompilationRepository extends JpaRepository<CompilationEntity, Long> {

}
