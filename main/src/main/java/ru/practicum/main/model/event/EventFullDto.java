package ru.practicum.main.model.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.model.category.CategoryDto;
import ru.practicum.main.model.location.LocationDto;
import ru.practicum.main.model.user.UserShortDto;

import static ch.qos.logback.core.joran.JoranConstants.NULL;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(value = NULL)
public class EventFullDto {

    private Long id;

    private String annotation;

    private UserShortDto initiator;

    private CategoryDto category;

    private LocationDto location;

    private String eventDate;

    private String createdOn;

    private String publishedOn;

    private String state;

    private Boolean paid;

    private Boolean requestModeration;

    private Integer participantLimit;

    private Long views;

    private Integer confirmedRequests;

    private String description;

    private String title;
}
