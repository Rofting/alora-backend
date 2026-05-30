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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    public PublicProfileDto getPublicByQrToken(String qrToken) {
        Profile p = repo.findByQrToken(qrToken)
                .orElseThrow(() -> new NotFoundException("QR token not found"));
        return mapper.map(p, PublicProfileDto.class);
    }

    public PrivateProfileDto unlockByQrAndPin(String qrToken, String pin) {
        Profile p = repo.findByQrToken(qrToken)
                .orElseThrow(() -> new NotFoundException("QR token not found"));

        if (!pin.equals(p.getPinCode())) {
            throw new InvalidPinException("Invalid PIN");
        }

        PrivateProfileDto dto = mapper.map(p, PrivateProfileDto.class);

        List<CareLog> logs = careLogRepo.findByProfile_IdOrderByCreatedAtDesc(p.getId());
        List<CareLogDto> logsDto = logs.stream()
                .map(log -> mapper.map(log, CareLogDto.class))
                .toList();

        dto.setRecentLogs(logsDto);
        return dto;
    }

    public PrivateProfileDto getProfileById(Long profileId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = repo.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para ver este perfil");
        }
        return mapper.map(profile, PrivateProfileDto.class);
    }

    public PrivateProfileDto createProfile(PrivateProfileDto dto) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = mapper.map(dto, Profile.class);
        profile.setQrToken(UUID.randomUUID().toString());
        profile.setUser(currentUser);
        return mapper.map(repo.save(profile), PrivateProfileDto.class);
    }

    public List<PrivateProfileDto> getMyProfiles() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return repo.findAllByUser(currentUser).stream()
                .map(p -> mapper.map(p, PrivateProfileDto.class))
                .toList();
    }

    public PrivateProfileDto updateProfile(Long profileId, PrivateProfileDto dto) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = repo.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para editar este perfil");
        }

        // Preservar campos que el cliente no puede sobreescribir
        Long savedId = profile.getId();
        String savedQrToken = profile.getQrToken();
        String savedPhotoUrl = profile.getPhotoUrl();

        mapper.map(dto, profile);

        profile.setId(savedId);
        profile.setQrToken(savedQrToken);
        profile.setPhotoUrl(savedPhotoUrl);
        profile.setUser(currentUser);

        return mapper.map(repo.save(profile), PrivateProfileDto.class);
    }

    public void deleteProfile(Long profileId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = repo.findById(profileId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar este perfil");
        }
        repo.delete(profile);
    }

    public String uploadPhoto(Long profileId, MultipartFile file) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Profile profile = repo.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
        if (!profile.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("No tienes permiso para subir foto de perfil");
        }

        try {
            Path uploadPath = Paths.get("uploads");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String filename = "profile_" + profile.getId() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            profile.setPhotoUrl(filename);
            repo.save(profile);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Error al subir la foto", e);
        }
    }
}
