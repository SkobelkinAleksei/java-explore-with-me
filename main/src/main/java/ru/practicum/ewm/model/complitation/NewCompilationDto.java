package ru.practicum.ewm.model.complitation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private List<Long> events;

    private Boolean pinned;

    @Size(
            min = 1,
            max = 50, message = "Имя должно быть от 1 до 50 символов."
    )
    @NotBlank(message = "Пустая строка.")
    private String title;
}
