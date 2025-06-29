package ru.practicum.main.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.model.category.CategoryDto;
import ru.practicum.main.model.location.LocationEntity;
import ru.practicum.main.model.user.UserShortDto;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {
    private Long id;

    private String annotation;

    private UserShortDto initiator;

    private CategoryDto category;

    private LocationEntity location;

    private String eventDate;

    private String createdOn;

    private String publishedOn;

    private String state;

    private Boolean paid;

    private Boolean requestModeration;

    private Integer participantLimit;

    private Integer views;

    private Integer confirmedRequests;

    private String description;

    private String title;
}
