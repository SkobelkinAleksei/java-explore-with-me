package ru.practicum.ewm.model.event;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.location.LocationDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {

    private Long category;

    @Size(min = 20, max = 2000, message = "Длина должна быть не менее 20 и не более 2000 символов.")
    private String annotation;

    @Size(min = 3, max = 120, message = "Длина должна быть не менее 3 и не более 120 символов.")
    private String title;

    @Size(min = 20, max = 7000, message = "Длина должна быть не менее 20 и не более 7000 символов.")
    private String description;

    private String stateAction;

    private String eventDate;

    private LocationDto location;

    private Integer participantLimit;

    private Boolean paid;

    private Boolean requestModeration;


}
