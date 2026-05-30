package com.alora.carelog.service;

import com.alora.auth.model.User;
import com.alora.carelog.model.CareLog;
import com.alora.carelog.model.dto.CareLogDto;
import com.alora.carelog.repository.CareLogRepository;
import com.alora.exception.NotFoundException;
import com.alora.profile.model.Profile;
import com.alora.profile.repository.ProfileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CareLogService {
    private final CareLogRepository careLogRepo;
    private final ProfileRepository profileRepo;
    private final ModelMapper mapper;

    public CareLogService(CareLogRepository careLogRepo, ProfileRepository profileRepo, ModelMapper mapper) {
        this.careLogRepo = careLogRepo;
        this.profileRepo = profileRepo;
        this.mapper = mapper;
    }

    public CareLogDto createLog(Long profileId, CareLogDto dto) {
        Profile profile = getOwnedProfile(profileId);
        CareLog log = mapper.map(dto, CareLog.class);
        log.setId(null);
        log.setProfile(profile);
        return mapper.map(careLogRepo.save(log), CareLogDto.class);
    }

    public Page<CareLogDto> getLogs(Long profileId, Pageable pageable) {
        getOwnedProfile(profileId);
        return careLogRepo.findByProfile_IdOrderByCreatedAtDesc(profileId, pageable)
                .map(log -> mapper.map(log, CareLogDto.class));
    }

    public CareLogDto getLogById(Long profileId, Long logId) {
        getOwnedProfile(profileId);
        CareLog log = careLogRepo.findByIdAndProfile_Id(logId, profileId)
                .orElseThrow(() -> new NotFoundException("Log not found"));
        return mapper.map(log, CareLogDto.class);
    }

    public CareLogDto updateLog(Long profileId, Long logId, CareLogDto dto) {
        getOwnedProfile(profileId);
        CareLog log = careLogRepo.findByIdAndProfile_Id(logId, profileId)
                .orElseThrow(() -> new NotFoundException("Log not found"));
        if (dto.getLogType() != null) log.setLogType(dto.getLogType());
        if (dto.getNote() != null) log.setNote(dto.getNote());
        return mapper.map(careLogRepo.save(log), CareLogDto.class);
    }

    public void deleteLog(Long profileId, Long logId) {
        getOwnedProfile(profileId);
        CareLog log = careLogRepo.findByIdAndProfile_Id(logId, profileId)
                .orElseThrow(() -> new NotFoundException("Log not found"));
        careLogRepo.delete(log);
    }

    private Profile getOwnedProfile(Long profileId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para acceder a este perfil");
        }
        return profile;
    }
}
