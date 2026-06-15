package com.alora.reminder.service;

import com.alora.auth.model.User;
import com.alora.config.GeminiService;
import com.alora.exception.NotFoundException;
import com.alora.profile.model.Profile;
import com.alora.profile.repository.ProfileRepository;
import com.alora.reminder.model.Reminder;
import com.alora.reminder.model.dto.ReminderDto;
import com.alora.reminder.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final ProfileRepository profileRepository;
    private final GeminiService geminiService;

    public List<ReminderDto> getReminders(Long profileId) {
        getOwnedProfile(profileId);
        return reminderRepository.findByProfileIdOrderByTimeAsc(profileId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ReminderDto createReminder(Long profileId, ReminderDto dto) {
        Profile profile = getOwnedProfile(profileId);
        Reminder reminder = new Reminder();
        reminder.setTitle(dto.getTitle());
        reminder.setTime(dto.getTime());
        reminder.setActive(true);
        reminder.setDaysOfWeek(dto.getDaysOfWeek() != null ? dto.getDaysOfWeek() : "TODOS");
        reminder.setProfile(profile);
        return mapToDto(reminderRepository.save(reminder));
    }

    public ReminderDto getReminderById(Long profileId, Long reminderId) {
        getOwnedProfile(profileId);
        Reminder reminder = reminderRepository.findByIdAndProfile_Id(reminderId, profileId)
                .orElseThrow(() -> new NotFoundException("Recordatorio no encontrado"));
        return mapToDto(reminder);
    }

    public ReminderDto updateReminder(Long profileId, Long reminderId, ReminderDto dto) {
        getOwnedProfile(profileId);
        Reminder reminder = reminderRepository.findByIdAndProfile_Id(reminderId, profileId)
                .orElseThrow(() -> new NotFoundException("Recordatorio no encontrado"));
        if (dto.getTitle() != null) reminder.setTitle(dto.getTitle());
        if (dto.getTime() != null) reminder.setTime(dto.getTime());
        if (dto.getDaysOfWeek() != null) reminder.setDaysOfWeek(dto.getDaysOfWeek());
        reminder.setActive(dto.isActive());
        return mapToDto(reminderRepository.save(reminder));
    }

    public void deleteReminder(Long profileId, Long reminderId) {
        getOwnedProfile(profileId);
        Reminder reminder = reminderRepository.findByIdAndProfile_Id(reminderId, profileId)
                .orElseThrow(() -> new NotFoundException("Recordatorio no encontrado"));
        reminderRepository.delete(reminder);
    }

    public String getSpeakText(Long profileId, Long reminderId) {
        Profile profile = getOwnedProfile(profileId);
        Reminder reminder = reminderRepository.findByIdAndProfile_Id(reminderId, profileId)
                .orElseThrow(() -> new NotFoundException("Recordatorio no encontrado"));

        String hora = reminder.getTime().toString(); // ej: "09:00"
        String prompt = """
                Eres Alora, una asistente de cuidados geriátricos empática y cercana.
                Genera un mensaje de voz corto (máximo 2 frases) para avisarle amablemente a %s
                que son las %s y es hora de: "%s".
                Menciona la hora de forma natural (por ejemplo "son las nueve de la mañana", "son las seis y media de la tarde").
                El tono debe ser cálido, claro y sin tecnicismos, como si fuera una persona de confianza.
                Termina con una pregunta sencilla para confirmar si ya lo ha hecho.
                Responde SOLO con el texto del mensaje, sin formato ni comillas.
                """.formatted(profile.getFullName(), hora, reminder.getTitle());

        try {
            return geminiService.generateText(prompt);
        } catch (com.alora.config.GeminiService.GeminiUnavailableException e) {
            return "Son las " + hora + ". Es hora de: " + reminder.getTitle() + ". ¿Ya lo has hecho?";
        }
    }

    private Profile getOwnedProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Paciente no encontrado"));
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para gestionar este perfil");
        }
        return profile;
    }

    private ReminderDto mapToDto(Reminder reminder) {
        ReminderDto dto = new ReminderDto();
        dto.setId(reminder.getId());
        dto.setTitle(reminder.getTitle());
        dto.setTime(reminder.getTime());
        dto.setActive(reminder.isActive());
        dto.setDaysOfWeek(reminder.getDaysOfWeek());
        return dto;
    }
}
