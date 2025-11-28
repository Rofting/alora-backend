package com.alora.profile.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "profiles",
        indexes = {
                @Index(name = "idx_profiles_qr_token", columnList = "qrToken", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ToString.Include
    private Long id;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(nullable = false, length = 150)
    private String email;

    private LocalDate birthDate;

    @Column(length = 20)
    private String gender;

    @Column(length = 1000)
    private String medicalConditions;

    @Column(length = 1000)
    private String allergies;

    @Column(length = 1000)
    private String medications;

    @Column(length = 300)
    private String approxAddress;

    @Column(length = 100)
    private String city;

    @Column(length = 120)
    private String emergencyContactName;

    @Column(length = 30)
    private String emergencyContactPhone;

    @Column(length = 100)
    private String emergencyContactEmail;

    @Column(length = 100)
    private String relationship;

    @Column(nullable = false, unique = true, length = 64)
    @ToString.Include
    private String qrToken;

    @Column(length = 12)
    private String pinCode;
}
