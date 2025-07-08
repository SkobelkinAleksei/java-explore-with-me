package ru.practicum.ewm.model.event;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.model.location.LocationEntity;
import ru.practicum.ewm.model.user.UserEntity;
import ru.practicum.ewm.model.category.CategoryEntity;

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

    @Column(name = "paid")
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
}
