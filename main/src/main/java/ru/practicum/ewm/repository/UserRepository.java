package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.user.UserDto;
import ru.practicum.ewm.model.user.UserEntity;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("""
        SELECT new ru.practicum.ewm.model.user.UserDto(ue.id, ue.email, ue.name)
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

    @Query("""
       SELECT (COUNT(ue.id) > 0)
       FROM UserEntity as ue
       WHERE ue.id = :userId
    """)
    boolean isUserExistsById(Long userId);
}
