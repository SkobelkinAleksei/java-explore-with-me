package ru.practicum.main.model.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import ru.practicum.main.model.location.LocationEntity;
import ru.practicum.main.model.user.UserEntity;
import ru.practicum.main.model.category.CategoryEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "event")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private UserEntity initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    @Column(name = "event_date",
            columnDefinition = "TIMESTAMP")
    private LocalDateTime eventDate;

    @Column(name = "created_on",
            columnDefinition = "TIMESTAMP")
    private LocalDateTime createdOn;

    @Column(name = "published_on",
            columnDefinition = "TIMESTAMP")
    private LocalDateTime publishedOn;

    @Enumerated(EnumType.STRING)
    private State state;

    private Boolean paid;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Column(name = "participant_limit")
    private Integer participantLimit;


    @Column(name = "confirmed_requests")
    private Integer confirmedRequests;

    private String annotation;

    private String description;

    @Column(nullable = false)
    private String title;

    public EventEntity(UserEntity initiator,
                       CategoryEntity category,
                       LocationEntity location,
                       LocalDateTime eventDate,
                       LocalDateTime createdOn,
                       LocalDateTime publishedOn,
                       State state, Boolean paid,
                       Boolean requestModeration,
                       Integer participantLimit,
                       Integer confirmedRequests,
                       String annotation,
                       String description,
                       String title
    ) {
        this.initiator = initiator;
        this.category = category;
        this.location = location;
        this.eventDate = eventDate;
        this.createdOn = createdOn;
        this.publishedOn = publishedOn;
        this.state = state;
        this.paid = paid;
        this.requestModeration = requestModeration;
        this.participantLimit = participantLimit;
        this.confirmedRequests = confirmedRequests;
        this.annotation = annotation;
        this.description = description;
        this.title = title;
    }

    public EventEntity(UserEntity initiator,
                       CategoryEntity category,
                       LocationEntity location,
                       LocalDateTime eventDate,
                       State state,
                       Boolean paid,
                       Boolean requestModeration,
                       Integer participantLimit,
                       String annotation,
                       String description,
                       String title,
                       LocalDateTime createdOn
    ) {
        this.initiator = initiator;
        this.category = category;
        this.location = location;
        this.eventDate = eventDate;
        this.state = state;
        this.paid = paid;
        this.requestModeration = requestModeration;
        this.participantLimit = participantLimit;
        this.annotation = annotation;
        this.description = description;
        this.title = title;
        this.createdOn = createdOn;
    }
}
