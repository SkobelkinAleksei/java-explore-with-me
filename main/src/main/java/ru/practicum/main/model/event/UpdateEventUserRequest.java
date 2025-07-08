package ru.practicum.main.model.event;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.model.location.LocationDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000, message = "Длина должна быть не менее 20 и не более 2000 символов.")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Длина должна быть не менее 20 и не более 7000 символов.")
    private String description;

    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 130, message = "Длина должна быть не менее 3 и не более 130 символов.")
    private String title;
}
