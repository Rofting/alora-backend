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

    @PostMapping
    public ResponseEntity<ReminderDto> createReminder(@PathVariable Long profileId, @RequestBody ReminderDto reminderDto) {
        return ResponseEntity.ok(reminderService.createReminder(profileId, reminderDto));
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(@PathVariable Long profileId, @PathVariable Long reminderId) {
        reminderService.deleteReminder(reminderId);
        return ResponseEntity.ok().build();
    }
}