package com.alora.reminder.repository;

import com.alora.reminder.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    // Busca los recordatorios de un perfil ordenados por hora
    List<Reminder> findByProfileIdOrderByTimeAsc(Long profileId);
}