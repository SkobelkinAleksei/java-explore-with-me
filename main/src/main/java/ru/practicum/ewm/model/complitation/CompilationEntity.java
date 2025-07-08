package ru.practicum.ewm.model.complitation;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.ewm.model.event.EventEntity;

import java.util.Set;

@Entity
@Table(name = "compilation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompilationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "compilation_event",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<EventEntity> events;

    private Boolean pinned;

    @Column(nullable = false)
    private String title;

    public CompilationEntity(String title) {
        this.pinned = false;
        this.title = title;
    }
}
