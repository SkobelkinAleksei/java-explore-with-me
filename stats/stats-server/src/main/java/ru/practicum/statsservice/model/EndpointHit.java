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

    @Column(nullable = false, name = "created")
    @JsonFormat(pattern = "yyyy-MM-dd 'T' HH:mm:ss")
    private LocalDateTime created;

    public EndpointHit(String app, String uri, String ip, LocalDateTime created) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.created = created;
    }
}
