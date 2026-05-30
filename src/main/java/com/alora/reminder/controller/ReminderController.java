package com.alora.reminder.controller;

import com.alora.reminder.model.dto.ReminderDto;
import com.alora.reminder.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping
    public ResponseEntity<List<ReminderDto>> getReminders(@PathVariable Long profileId) {
        return ResponseEntity.ok(reminderService.getReminders(profileId));
    }

    @GetMapping("/{reminderId}")
    public ResponseEntity<ReminderDto> getReminder(@PathVariable Long profileId, @PathVariable Long reminderId) {
        return ResponseEntity.ok(reminderService.getReminderById(profileId, reminderId));
    }

    @PostMapping
    public ResponseEntity<ReminderDto> createReminder(@PathVariable Long profileId, @RequestBody ReminderDto reminderDto) {
        return ResponseEntity.ok(reminderService.createReminder(profileId, reminderDto));
    }

    @PutMapping("/{reminderId}")
    public ResponseEntity<ReminderDto> updateReminder(
            @PathVariable Long profileId,
            @PathVariable Long reminderId,
            @RequestBody ReminderDto reminderDto
    ) {
        return ResponseEntity.ok(reminderService.updateReminder(profileId, reminderId, reminderDto));
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long profileId, @PathVariable Long reminderId) {
        reminderService.deleteReminder(reminderId);
        return ResponseEntity.noContent().build();
    }
}