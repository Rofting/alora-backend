package com.alora.profile.controller;

import com.alora.auth.service.JwtService;
import com.alora.profile.dto.PrivateProfileDto;
import com.alora.profile.service.ProfileService;
import com.alora.profile.service.QrCodeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// 🔥 NUEVA IMPORTACIÓN DE SPRING BOOT 3.4+:
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PublicProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    @MockitoBean
    private QrCodeService qrCodeService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("Debería devolver el perfil privado si el PIN es correcto")
    public void testUnlockProfile_PinCorrecto_DevuelveDatosPrivados() throws Exception {

        // 1. PREPARACIÓN (Arrange)
        String tokenSimulado = "token-qr-secreto-123";
        String pinSimulado = "1234";

        PrivateProfileDto perfilFalso = new PrivateProfileDto();
        perfilFalso.setFullName("María López");
        perfilFalso.setMedications("Paracetamol 500mg, Sintrom");
        perfilFalso.setEmergencyContactPhone("600123456");

        Mockito.when(profileService.unlockByQrAndPin(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(perfilFalso);

        String jsonRequest = "{\"pin\":\"" + pinSimulado + "\"}";

        // 2. EJECUCIÓN Y COMPROBACIÓN (Act & Assert)
        mockMvc.perform(post("/public/profile/" + tokenSimulado + "/unlock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("María López"))
                .andExpect(jsonPath("$.medications").value("Paracetamol 500mg, Sintrom"))
                .andExpect(jsonPath("$.emergencyContactPhone").value("600123456"));
    }
}
