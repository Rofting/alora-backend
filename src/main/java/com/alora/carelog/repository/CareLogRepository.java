package com.alora.carelog.repository;

import com.alora.carelog.model.CareLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareLogRepository extends JpaRepository<CareLog, Long> {
    Page<CareLog> findByProfile_IdOrderByCreatedAtDesc(Long profileId, Pageable pageable);
    List<CareLog> findByProfile_IdOrderByCreatedAtDesc(Long profileId);
    Optional<CareLog> findByIdAndProfile_Id(Long id, Long profileId);
}