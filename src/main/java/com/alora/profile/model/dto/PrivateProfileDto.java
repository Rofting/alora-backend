package com.alora.profile.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PrivateProfileDto {
    private String fullName;
    private String email;
    private LocalDate birthDate;
    private String gender;

    private String medicalConditions;
    private String allergies;
    private String medications;

    private String approxAddress;
    private String city;

    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactEmail;
    private String relationship;
}
