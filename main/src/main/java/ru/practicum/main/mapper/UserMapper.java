package ru.practicum.main.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.main.model.user.UserDto;
import ru.practicum.main.model.user.UserEntity;
import ru.practicum.main.model.user.UserShortDto;
import ru.practicum.main.requests.NewUserRequest;

@UtilityClass
public class UserMapper {

    public UserEntity toEntity(NewUserRequest newUserRequest) {
        return new UserEntity(
                newUserRequest.getEmail(),
                newUserRequest.getName()
        );
    }

    public UserDto toDto(UserEntity userEntity) {
        return new UserDto(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getName()
        );
    }

    public UserShortDto toUserShortDto(UserEntity userEntity) {
        return UserShortDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .build();
    }

}
