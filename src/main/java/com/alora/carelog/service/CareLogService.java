package com.alora.carelog.service;

import com.alora.auth.model.User;
import com.alora.carelog.model.CareLog;
import com.alora.carelog.model.dto.CareLogDto;
import com.alora.carelog.repository.CareLogRepository;
import com.alora.exception.NotFoundException;
import com.alora.profile.model.Profile;
import com.alora.profile.repository.ProfileRepository;
import org.modelmapper.ModelMapper;
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

    // Crear registro nuevo
    public CareLogDto createLog(Long profileId, CareLogDto dto) {

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Profile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        //verificamos usuario
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para crear registros en este perfil");
        }
        // Convertimos el DTO a Entidad CareLog
        CareLog log = mapper.map(dto, CareLog.class);
        log.setId(null);
        // ASIGNAMOS EL PERFIL AL LOG
        log.setProfile(profile);

        // GUARDAMOS EN LA BD
        CareLog savedLog = careLogRepo.save(log);
        return mapper.map(savedLog, CareLogDto.class);


    }

    // Leer Registros
    public List<CareLogDto> getLogs(Long profileId) {
        //Obtenemos el usuario actual
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // buscamos el usuario en la BD
        Profile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // verificar dueño
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para ver registros de este perfil");
        }
            //buscamos los logs
            List<CareLog> logs = careLogRepo.findByProfile_IdOrderByCreatedAtDesc(profileId);

            //convertimos a dtos

            return logs.stream()
                    .map(log -> mapper.map(log, CareLogDto.class))
                    .toList();
    }

    // Obtener un log por ID
    public CareLogDto getLogById(Long profileId, Long logId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para ver registros de este perfil");
        }
        CareLog log = careLogRepo.findByIdAndProfile_Id(logId, profileId)
                .orElseThrow(() -> new NotFoundException("Log not found"));
        return mapper.map(log, CareLogDto.class);
    }

    // Actualizar un log
    public CareLogDto updateLog(Long profileId, Long logId, CareLogDto dto) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para editar registros de este perfil");
        }
        CareLog log = careLogRepo.findByIdAndProfile_Id(logId, profileId)
                .orElseThrow(() -> new NotFoundException("Log not found"));
        if (dto.getLogType() != null) log.setLogType(dto.getLogType());
        if (dto.getNote() != null) log.setNote(dto.getNote());
        return mapper.map(careLogRepo.save(log), CareLogDto.class);
    }

    // Eliminar un log
    public void deleteLog(Long profileId, Long logId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = profileRepo.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar registros de este perfil");
        }
        CareLog log = careLogRepo.findByIdAndProfile_Id(logId, profileId)
                .orElseThrow(() -> new NotFoundException("Log not found"));
        careLogRepo.delete(log);
    }
}