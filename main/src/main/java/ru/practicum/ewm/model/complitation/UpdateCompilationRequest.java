package ru.practicum.ewm.model.complitation;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {
    private List<Long> eventsId;
    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Некорректные данные.")
    private String title;
}
