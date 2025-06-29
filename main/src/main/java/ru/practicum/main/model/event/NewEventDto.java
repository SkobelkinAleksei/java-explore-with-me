package ru.practicum.main.model.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.model.location.LocationEntity;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    //TODO прописать валидацию

    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull(message = "Category не может быть null")
    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @NotBlank(message = "Eventdate не может быть пустым")
    private String eventDate;

    @NotNull(message = "Location не может быть null")
    private LocationEntity location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120)
    private String title;
}
