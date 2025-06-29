package ru.practicum.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.main.model.user.UserDto;
import ru.practicum.main.model.user.UserEntity;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("""
        SELECT new ru.practicum.main.model.user.UserDto(ue.id, ue.email, ue.name)
        FROM UserEntity as ue
    """)
    List<UserDto> findWithUsersPagination(Pageable pageable);

    @Query("""
        SELECT new ru.practicum.main.model.user.UserDto(ue.id, ue.email, ue.name)
        FROM UserEntity as ue
        WHERE ue.id IN :ids
    """)
    List<UserDto> findUsersByIds(List<Integer> ids);

    @Query("""
       SELECT (COUNT(ue.email) > 0)
       FROM UserEntity as ue
       WHERE ue.email = :email
    """)
    boolean isUserExistsByEmail(String email);
}
