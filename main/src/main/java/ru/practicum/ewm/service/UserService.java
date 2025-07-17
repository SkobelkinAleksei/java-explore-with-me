package ru.practicum.ewm.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exeption.UserAlreadyExistsByEmail;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.user.UserDto;
import ru.practicum.ewm.model.user.UserEntity;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.requests.NewUserRequest;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto saveUser(NewUserRequest newUserRequest) throws UserAlreadyExistsByEmail {
        log.info("Запрос на создание пользователя с email: {}", newUserRequest.getEmail());
        if (userRepository.isUserExistsByEmail(newUserRequest.getEmail())) {
            throw new UserAlreadyExistsByEmail("Пользователь с email %s уже зарегистрирован"
                    .formatted(newUserRequest.getEmail()));
        }

        UserEntity savedUser = userRepository.save(
                UserMapper.toEntity(newUserRequest)
        );
        log.info("Пользователь успешно сохранен с id: {}", savedUser.getId());

        return UserMapper.toDto(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Integer> ids,
                                  Integer from,
                                  Integer size
    ) throws NumberFormatException {

        log.info("Запрос на получение пользователей. ids: {}, from: {}, size: {}", ids, from, size);

        PageRequest pageRequest = PageRequest.of(
                from != null ? from : 0,
                size != null ? size : 10,
                Sort.by("id")
                        .ascending()
        );

        if (ids != null) {
            log.info("Получение пользователей по списку ids");
            return userRepository.findUsersByIds(ids);
        } else {
            List<UserEntity> allUsers = userRepository.findAll(pageRequest).getContent();

            log.info("Общее количество полученных пользователей: {}", allUsers.size());
            return allUsers.isEmpty() ? Collections.emptyList() : allUsers.stream()
                    .map(UserMapper::toDto)
                    .peek(user -> log.debug("Обработка пользователя с id: {}", user.getId()))
                    .toList();
        }

    }

    @Transactional
    public void deleteUser(Long userId) throws EntityNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь с id %s не найден.".formatted(userId));
        }
        log.info("Удаление пользователя с id: {}", userId);
        userRepository.deleteById(userId);
        log.info("Пользователь с id {} успешно удален", userId);
    }

}
