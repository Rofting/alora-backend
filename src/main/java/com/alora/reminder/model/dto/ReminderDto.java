package com.alora.reminder.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalTime;

@Data
public class ReminderDto {
    private Long id;

    @NotBlank(message = "El título del recordatorio es obligatorio")
    private String title;

    @NotNull(message = "La hora del recordatorio es obligatoria")
    private LocalTime time;

    private boolean isActive;
    private String daysOfWeek;
}