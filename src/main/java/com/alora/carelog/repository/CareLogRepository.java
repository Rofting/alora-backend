package com.alora.carelog.repository;

import com.alora.carelog.model.CareLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareLogRepository extends JpaRepository<CareLog, Long> {
    List<CareLog> findByProfileId(Long profileId);
}