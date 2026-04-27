package com.alora.reminder.service;

import com.alora.exception.NotFoundException;
import com.alora.profile.repository.ProfileRepository;
import com.alora.reminder.model.Reminder;
import com.alora.reminder.model.dto.ReminderDto;
import com.alora.reminder.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final ProfileRepository profileRepository;

    // 1. Obtener todas las alarmas de un paciente
    public List<ReminderDto> getReminders(Long profileId) {
        return reminderRepository.findByProfileIdOrderByTimeAsc(profileId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // 2. Crear una nueva alarma
    public ReminderDto createReminder(Long profileId, ReminderDto dto) {
        var profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Paciente no encontrado"));

        Reminder reminder = new Reminder();
        reminder.setTitle(dto.getTitle());
        reminder.setTime(dto.getTime());
        reminder.setActive(true);
        reminder.setProfile(profile);

        Reminder saved = reminderRepository.save(reminder);
        return mapToDto(saved);
    }

    // 3. Borrar una alarma
    public void deleteReminder(Long reminderId) {
        reminderRepository.deleteById(reminderId);
    }

    // Método ayudante para convertir de Base de Datos a DTO
    private ReminderDto mapToDto(Reminder reminder) {
        ReminderDto dto = new ReminderDto();
        dto.setId(reminder.getId());
        dto.setTitle(reminder.getTitle());
        dto.setTime(reminder.getTime());
        dto.setActive(reminder.isActive());
        return dto;
    }
}