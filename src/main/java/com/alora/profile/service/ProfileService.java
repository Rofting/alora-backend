package com.alora.profile.service;

import com.alora.auth.model.User;
import com.alora.carelog.model.CareLog;
import com.alora.carelog.model.dto.CareLogDto;
import com.alora.carelog.repository.CareLogRepository;
import com.alora.exception.InvalidPinException;
import com.alora.exception.NotFoundException;
import com.alora.profile.dto.PrivateProfileDto;
import com.alora.profile.dto.PublicProfileDto;
import com.alora.profile.model.Profile;
import com.alora.profile.repository.ProfileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProfileService {
    private final ProfileRepository repo;
    private final ModelMapper mapper;
    private final CareLogRepository careLogRepo;

    public ProfileService(ProfileRepository repo, ModelMapper mapper, CareLogRepository careLogRepo) {
        this.repo = repo;
        this.mapper = mapper;
        this.careLogRepo = careLogRepo;
    }

    // --- Parte Pública ---
    public PublicProfileDto getPublicByQrToken(String qrToken) {
        Profile p = repo.findByQrToken(qrToken)
                .orElseThrow(() -> new NotFoundException("QR token not found"));
        return mapper.map(p, PublicProfileDto.class);
    }

    // --- Parte Privada: Desbloqueo  ---
    public PrivateProfileDto unlockByQrAndPin(String qrToken, String pin) {
        Profile p = repo.findByQrToken(qrToken)
                .orElseThrow(() -> new NotFoundException("QR token not found"));

        // Comprobamos que el PIN coincida
        if (!pin.equals(p.getPinCode())) {
            throw new InvalidPinException("Invalid PIN");
        }

        //Convertimos Dto a base
        PrivateProfileDto dto = mapper.map(p, PrivateProfileDto.class);

        //buscamos los registros del paciente
        List<CareLog> logs = careLogRepo.findByProfileId(p.getId());

        //convertimos a dto
        List<CareLogDto> logsDto = logs.stream()
                .map(log -> mapper.map(log, CareLogDto.class))
                .toList();

        dto.setRecentLogs(logsDto);
        return dto;
    }

    // --- CREAR PERFIL
    public PrivateProfileDto createProfile(PrivateProfileDto dto) {
        // 1. Averiguar quién está logueado (El "Dueño")
        // SecurityContextHolder es como mirar el DNI de quien hace la petición
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. Convertir DTO a Entidad
        Profile profile = mapper.map(dto, Profile.class);

        // 3. Rellenar datos automáticos
        profile.setQrToken(UUID.randomUUID().toString()); // Generar QR único
        profile.setUser(currentUser);

        // 4. Guardar en la base de datos
        Profile savedProfile = repo.save(profile);

        return mapper.map(savedProfile, PrivateProfileDto.class);
    }

    // --- LISTAR PACIENTES
    public List<PrivateProfileDto> getMyProfiles() {
        // 1. Averiguar quién pregunta
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. Preguntar al repositorio: "Dame solo los perfiles de este usuario"
        // NOTA: Asegúrate de haber cambiado 'findAll' por 'findAllByUser' en el Repository
        List<Profile> profiles = repo.findAllByUser(currentUser);

        // 3. Convertir la lista de Entidades a lista de DTOs para devolverla
        return profiles.stream()
                .map(p -> mapper.map(p, PrivateProfileDto.class))
                .toList();
    }

    // --- ACTUALIZAR PERFIL (PUT) ---
    public PrivateProfileDto updateProfile(Long profileId, PrivateProfileDto dto) {

        // 1. OBTENER EL USUARIO ACTUAL
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 2. BUSCAR EL PERFIL EN LA BD
        Profile profile = repo.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        // 3. SEGURIDAD: VERIFICAR DUEÑO
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para editar este perfil");
        }

        // 4. ACTUALIZAR DATOS
        mapper.map(dto, profile);

        // 5. RESTAURAR DATOS CRÍTICOS
        profile.setUser(currentUser);

        // 6. GUARDAR CAMBIOS
        Profile updatedProfile = repo.save(profile);

        // 7. DEVOLVER DTO
        return mapper.map(updatedProfile, PrivateProfileDto.class);
    }

    // Eliminar perfil
    public void deleteProfile(Long profileId){
        //obtenemos el perfil a eliminar
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //buscamos en la base de datos el perfil

        Profile profile = repo.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        // Verificamos el dueño
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este perfil");
        }

        // Eliminamos el perfil
        repo.delete(profile);
    }
}