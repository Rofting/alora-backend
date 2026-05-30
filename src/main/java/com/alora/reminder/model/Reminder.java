package com.alora.reminder.model;

import com.alora.profile.model.Profile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import com.alora.config.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reminders")
public class Reminder extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Ej: "Tomar pastilla"

    @Column(nullable = false)
    private LocalTime time; // Ej: 14:00

    private boolean isActive = true;

    @Column(name = "days_of_week")
    private String daysOfWeek;

    // Relación: Un perfil (paciente) tiene muchos recordatorios
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;
}