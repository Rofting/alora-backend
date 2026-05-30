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

        // 🌟 NUEVO: Mapeamos los días de la semana de Android hacia la Base de Datos
        if (dto.getDaysOfWeek() != null) {
            reminder.setDaysOfWeek(dto.getDaysOfWeek());
        } else {
            reminder.setDaysOfWeek("TODOS"); // Valor por defecto de seguridad
        }

        reminder.setProfile(profile);

        Reminder saved = reminderRepository.save(reminder);
        return mapToDto(saved);
    }

    // 3. Obtener un recordatorio por ID
    public ReminderDto getReminderById(Long profileId, Long reminderId) {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Paciente no encontrado"));
        Reminder reminder = reminderRepository.findByIdAndProfile_Id(reminderId, profileId)
                .orElseThrow(() -> new NotFoundException("Recordatorio no encontrado"));
        return mapToDto(reminder);
    }

    // 4. Actualizar un recordatorio
    public ReminderDto updateReminder(Long profileId, Long reminderId, ReminderDto dto) {
        profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Paciente no encontrado"));
        Reminder reminder = reminderRepository.findByIdAndProfile_Id(reminderId, profileId)
                .orElseThrow(() -> new NotFoundException("Recordatorio no encontrado"));
        if (dto.getTitle() != null) reminder.setTitle(dto.getTitle());
        if (dto.getTime() != null) reminder.setTime(dto.getTime());
        if (dto.getDaysOfWeek() != null) reminder.setDaysOfWeek(dto.getDaysOfWeek());
        reminder.setActive(dto.isActive());
        return mapToDto(reminderRepository.save(reminder));
    }

    // 5. Borrar una alarma
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

        // 🌟 NUEVO: Empaquetamos los días de la semana para que lleguen a la aplicación de Android
        dto.setDaysOfWeek(reminder.getDaysOfWeek());

        return dto;
    }
}