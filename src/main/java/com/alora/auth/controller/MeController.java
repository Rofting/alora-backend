package com.alora.auth.controller;

import com.alora.auth.model.User;
import com.alora.auth.model.dto.UserSummaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    @GetMapping
    public ResponseEntity<UserSummaryDto> me(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(
                new UserSummaryDto(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name())
        );
    }
}
