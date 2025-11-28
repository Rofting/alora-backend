package com.alora.profile.service;

import com.alora.exception.InvalidPinException;
import com.alora.exception.NotFoundException;
import com.alora.profile.dto.PrivateProfileDto;
import com.alora.profile.dto.PublicProfileDto;
import com.alora.profile.model.Profile;
import com.alora.profile.repository.ProfileRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    private final ProfileRepository repo;
    private final ModelMapper mapper;

    public ProfileService(ProfileRepository repo, ModelMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    // --- Público: buscar por QR (sin PIN) ---
    public PublicProfileDto getPublicByQrToken(String qrToken) {
        Profile p = repo.findByQrToken(qrToken)
                .orElseThrow(() -> new NotFoundException("QR token not found"));
        return mapper.map(p, PublicProfileDto.class);
    }

    // --- Privado: validar PIN y devolver datos extendidos ---
    public PrivateProfileDto unlockByQrAndPin(String qrToken, String pin) {
        Profile p = repo.findByQrToken(qrToken)
                .orElseThrow(() -> new NotFoundException("QR token not found"));
        if (!pin.equals(p.getPinCode())) {
            throw new InvalidPinException("Invalid PIN");
        }
        return mapper.map(p, PrivateProfileDto.class);
    }
}

