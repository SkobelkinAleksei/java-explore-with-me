package ru.practicum.main.model.participation;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.main.model.event.EventEntity;
import ru.practicum.main.model.event.State;
import ru.practicum.main.model.user.UserEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "participation_request")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//TODO сделать таблицу
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
