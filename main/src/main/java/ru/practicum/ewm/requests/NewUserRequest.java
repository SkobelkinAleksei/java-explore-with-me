package ru.practicum.ewm.requests;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @Size(min = 6,
            max = 254,
            message = "Количество символов от 6 до 254")
    private String email;

    @Size(min = 2, max = 250, message = "Количество символов от 2 до 250")
    private String name;

}
