package ru.practicum.ewm.model.participation;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.model.event.EventEntity;
import ru.practicum.ewm.model.event.State;
import ru.practicum.ewm.model.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "participation_request")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipationRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at",
            columnDefinition = "TIMESTAMP")
    private LocalDateTime created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private UserEntity requester;

    @Enumerated(EnumType.STRING)
    private State status;
}
