package com.alora.reminder.repository;

import com.alora.reminder.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByProfileIdOrderByTimeAsc(Long profileId);
    Optional<Reminder> findByIdAndProfile_Id(Long id, Long profileId);
}