package com.alora.profile.dto;

import com.alora.carelog.model.dto.CareLogDto;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PrivateProfileDto {
    private Long id;
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

    private String qrToken;
    private String pinCode;
    private String photoUrl;

    private List<CareLogDto> recentLogs;
}
