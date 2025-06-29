package ru.practicum.main.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String name;

    public UserDto(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
