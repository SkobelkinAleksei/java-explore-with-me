package ru.practicum.ewm.model.event;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.location.LocationEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {

    @NotBlank(message = "Аннотация не может быть пустой.")
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull(message = "Category не может быть null")
    private Long category;

    @NotBlank(message = "Описание не может быть пустым.")
    @Size(min = 20, max = 7000)
    private String description;

    @NotBlank(message = "Eventdate не может быть пустым")
    private String eventDate;

    @NotNull(message = "Location не может быть null")
    private LocationEntity location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Некорректное название")
    private String title;
}
