package com.alora.profile.repository;

import com.alora.auth.model.User;
import com.alora.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByQrToken(String qrToken);
    Optional<Profile> findByQrTokenAndPinCode(String qrToken, String pinCode);
    List<Profile> findAllByUser(User user);
}
