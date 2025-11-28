package com.alora.carelog.repository;

import com.alora.carelog.model.CareNote;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CareNoteRepository extends JpaRepository<CareNote, Long> {
    List<CareNote> findByProfileIdOrderByCreatedAtDesc(Long profileId);
}
