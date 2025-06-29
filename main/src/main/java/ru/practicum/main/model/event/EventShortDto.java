package ru.practicum.main.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main.model.user.UserShortDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventShortDto {
    private Long id;
    private String annotation;
    private String category;
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
}
