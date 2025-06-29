package ru.practicum.main.model.location;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "location")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Float lat;

    @Column(nullable = false)
    private Float lon;

    public LocationEntity(Float lat, Float lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
