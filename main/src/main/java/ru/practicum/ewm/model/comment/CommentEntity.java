package ru.practicum.ewm.model.comment;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.practicum.ewm.model.event.EventEntity;

@Entity
@Table(name = "comment")
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
public class CommentEntity extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "eventId", insertable = false, updatable = false)
    private EventEntity event;

    public CommentEntity() {
    }
}