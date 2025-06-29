package ru.practicum.main.model.category;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {

    @Size(min = 1,
            max = 50,
            message = "Доступное количество символов от 1 до 50."
    )
    private String name;

}
