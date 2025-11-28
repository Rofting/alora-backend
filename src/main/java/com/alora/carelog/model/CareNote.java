package com.alora.carelog.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.Instant;

@Entity
@Table(name = "care_note") // Asegúrate que coincide con tu SQL
@Getter
@Setter
@NoArgsConstructor
public class CareNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long profileId;

    @Column(nullable = false) // El DTO ya valida que no esté vacío
    private String text;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

}