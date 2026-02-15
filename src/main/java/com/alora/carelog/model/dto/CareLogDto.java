package com.alora.carelog.model.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareLogDto {
    private Long id;

    private Long profileId;
    private String logType;
    private String note;

    private LocalDateTime createdAt;
}