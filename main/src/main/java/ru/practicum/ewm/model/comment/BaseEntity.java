package ru.practicum.ewm.model.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long eventId;

    @Size(min = 5, max = 255, message = "Размер должен находиться в диапазоне от 5 до 255")
    @Column(nullable = false)
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime updatedAt;
}
