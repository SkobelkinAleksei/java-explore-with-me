package ru.practicum.ewm.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.location.LocationDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000, message = "Длина должна быть не менее 20 и не более 2000 символов.")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Длина должна быть не менее 20 и не более 7000 символов.")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero
    private Long participantLimit;

    private Boolean requestModeration;

    private String stateAction;

    @Size(min = 3, max = 120, message = "Длина должна быть не менее 3 и не более 130 символов.")
    private String title;
}
