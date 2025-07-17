package ru.practicum.ewm.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "email не может быть пустым.")
    @Size(min = 6,
            max = 254,
            message = "Количество символов от 6 до 254")
    @Email
    private String email;

    @NotBlank(message = "name не может быть пустым.")
    @Size(min = 2, max = 250, message = "Количество символов от 2 до 250")
    private String name;

}
