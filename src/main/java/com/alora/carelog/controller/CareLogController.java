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

    @PostMapping("/{profileId}/logs")
    public ResponseEntity<CareLogDto> createLog(
            @PathVariable Long profileId,
            @RequestBody CareLogDto dto
    ) {
        return ResponseEntity.ok(careLogService.createLog(profileId, dto));
    }

    @GetMapping("/{profileId}/logs")
    public ResponseEntity<List<CareLogDto>> getLogs(@PathVariable Long profileId) {
        return ResponseEntity.ok(careLogService.getLogs(profileId));
    }
}
