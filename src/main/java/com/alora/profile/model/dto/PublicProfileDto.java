package com.alora.profile.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PublicProfileDto {
    private String fullName;
    private String approxAddress;
    private String city;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String photoUrl;
}
