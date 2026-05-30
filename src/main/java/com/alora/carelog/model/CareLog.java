package com.alora.carelog.model;

import com.alora.profile.model.Profile;
import jakarta.persistence.*;
import com.alora.config.BaseEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "care_logs")
public class CareLog extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN: Muchos logs pertenecen a UN Perfil
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    @Column(name = "log_type", nullable = false)
    private String logType; // Ej: "MEDICACION", "SINTOMAS", "COMIDA"

    private String note;

}