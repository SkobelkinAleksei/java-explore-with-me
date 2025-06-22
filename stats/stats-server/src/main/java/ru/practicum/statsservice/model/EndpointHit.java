package ru.practicum.statsservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String app;

    @Column(nullable = false)
    private String uri;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    public EndpointHit(String app, String uri, String ip, LocalDateTime timestamp) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp;
    }
}
