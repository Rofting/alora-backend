package com.alora.carelog.service;

import com.alora.auth.model.User;
import com.alora.carelog.model.CareLog;
import com.alora.carelog.model.dto.CareLogDto;
import com.alora.carelog.repository.CareLogRepository;
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
            List<CareLog> logs = careLogRepo.findByProfileId(profileId);

            //convertimos a dtos

            return logs.stream()
                    .map(log -> mapper.map(log, CareLogDto.class))
                    .toList();
    }
}