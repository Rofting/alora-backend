package com.alora.reminder.model.dto;

import lombok.Data;
import java.time.LocalTime;

@Data
public class ReminderDto {
    private Long id;
    private String title;
    private LocalTime time;
    private boolean isActive;

    private String daysOfWeek;
}