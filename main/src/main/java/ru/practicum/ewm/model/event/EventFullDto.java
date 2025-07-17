package ru.practicum.ewm.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.category.CategoryDto;
import ru.practicum.ewm.model.location.LocationDto;
import ru.practicum.ewm.model.user.UserShortDto;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private String eventDate;

    private String createdOn;

    private String publishedOn;

    private String state;

    private Boolean paid;

    private Boolean requestModeration;

    private Long participantLimit;

    private Long views;

    private Integer confirmedRequests;

    private String description;

    private String title;
}
