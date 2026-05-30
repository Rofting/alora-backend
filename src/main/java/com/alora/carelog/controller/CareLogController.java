package com.alora.carelog.controller;


import com.alora.carelog.model.dto.CareLogDto;
import com.alora.carelog.service.CareLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class CareLogController {

    private final CareLogService careLogService;

    @GetMapping("/{profileId}/logs")
    public ResponseEntity<List<CareLogDto>> getLogs(@PathVariable Long profileId) {
        return ResponseEntity.ok(careLogService.getLogs(profileId));
    }

    @GetMapping("/{profileId}/logs/{logId}")
    public ResponseEntity<CareLogDto> getLog(@PathVariable Long profileId, @PathVariable Long logId) {
        return ResponseEntity.ok(careLogService.getLogById(profileId, logId));
    }

    @PostMapping("/{profileId}/logs")
    public ResponseEntity<CareLogDto> createLog(
            @PathVariable Long profileId,
            @RequestBody CareLogDto dto
    ) {
        return ResponseEntity.ok(careLogService.createLog(profileId, dto));
    }

    @PutMapping("/{profileId}/logs/{logId}")
    public ResponseEntity<CareLogDto> updateLog(
            @PathVariable Long profileId,
            @PathVariable Long logId,
            @RequestBody CareLogDto dto
    ) {
        return ResponseEntity.ok(careLogService.updateLog(profileId, logId, dto));
    }

    @DeleteMapping("/{profileId}/logs/{logId}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long profileId, @PathVariable Long logId) {
        careLogService.deleteLog(profileId, logId);
        return ResponseEntity.noContent().build();
    }
}
