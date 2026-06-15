package com.alora.carelog.service;

import com.alora.auth.model.Role;
import com.alora.auth.model.User;
import com.alora.carelog.model.CareLog;
import com.alora.carelog.model.dto.CareLogDto;
import com.alora.carelog.repository.CareLogRepository;
import com.alora.profile.model.Profile;
import com.alora.profile.repository.ProfileRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CareLogServiceTest {

    @Mock private CareLogRepository careLogRepo;
    @Mock private ProfileRepository profileRepo;
    @Mock private ModelMapper mapper;

    @InjectMocks
    private CareLogService careLogService;

    private User owner;
    private User stranger;
    private Profile profile;
    private final Pageable defaultPageable = PageRequest.of(0, 20);

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L).email("owner@test.com").password("hashed")
                .fullName("Owner").role(Role.USER).createdAt(Instant.now())
                .build();

        stranger = User.builder()
                .id(2L).email("stranger@test.com").password("hashed")
                .fullName("Stranger").role(Role.USER).createdAt(Instant.now())
                .build();

        profile = new Profile();
        profile.setId(10L);
        profile.setFullName("Paciente A");
        profile.setEmail("paciente@test.com");
        profile.setQrToken("qr-token-test");
        profile.setUser(owner);

        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(
                new UsernamePasswordAuthenticationToken(owner, null, owner.getAuthorities())
        );
        SecurityContextHolder.setContext(ctx);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ─────────────────────────────────────────────
    // createLog()
    // ─────────────────────────────────────────────

    @Test
    void createLog_success_devuelveDtoDelLogGuardado() {
        CareLogDto inputDto = buildInputDto(null, "MEDICACION", "Tomó ibuprofeno");
        CareLog mappedLog = buildLog(null, "MEDICACION", "Tomó ibuprofeno");
        CareLog savedLog = buildLog(100L, "MEDICACION", "Tomó ibuprofeno");
        CareLogDto expectedDto = buildOutputDto(100L, 10L, "MEDICACION");

        when(profileRepo.findById(10L)).thenReturn(Optional.of(profile));
        when(mapper.map(inputDto, CareLog.class)).thenReturn(mappedLog);
        when(careLogRepo.save(any(CareLog.class))).thenReturn(savedLog);
        when(mapper.map(savedLog, CareLogDto.class)).thenReturn(expectedDto);

        CareLogDto result = careLogService.createLog(10L, inputDto);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getLogType()).isEqualTo("MEDICACION");
        verify(careLogRepo).save(any(CareLog.class));
    }

    @Test
    void createLog_asignaElPerfilAlLog() {
        CareLogDto inputDto = buildInputDto(null, "COMIDA", "Desayunó");
        CareLog mappedLog = buildLog(null, "COMIDA", "Desayunó");
        CareLog savedLog = buildLog(1L, "COMIDA", "Desayunó");

        when(profileRepo.findById(10L)).thenReturn(Optional.of(profile));
        when(mapper.map(inputDto, CareLog.class)).thenReturn(mappedLog);
        when(careLogRepo.save(any(CareLog.class))).thenReturn(savedLog);
        when(mapper.map(savedLog, CareLogDto.class)).thenReturn(buildOutputDto(1L, 10L, "COMIDA"));

        careLogService.createLog(10L, inputDto);

        ArgumentCaptor<CareLog> captor = ArgumentCaptor.forClass(CareLog.class);
        verify(careLogRepo).save(captor.capture());
        assertThat(captor.getValue().getProfile()).isSameAs(profile);
    }

    @Test
    void createLog_bloqueaInyeccionDeId() {
        CareLogDto dtoConId = buildInputDto(999L, "SINTOMAS", "Fiebre");
        CareLog logConId = buildLog(999L, "SINTOMAS", "Fiebre");

        when(profileRepo.findById(10L)).thenReturn(Optional.of(profile));
        when(mapper.map(dtoConId, CareLog.class)).thenReturn(logConId);
        when(careLogRepo.save(any(CareLog.class))).thenReturn(buildLog(1L, "SINTOMAS", "Fiebre"));
        when(mapper.map(any(), eq(CareLogDto.class))).thenReturn(buildOutputDto(1L, 10L, "SINTOMAS"));

        careLogService.createLog(10L, dtoConId);

        ArgumentCaptor<CareLog> captor = ArgumentCaptor.forClass(CareLog.class);
        verify(careLogRepo).save(captor.capture());
        assertThat(captor.getValue().getId())
                .as("El ID del cliente debe ignorarse y guardarse como null")
                .isNull();
    }

    @Test
    void createLog_perfilNoEncontrado_lanzaRuntimeException() {
        when(profileRepo.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> careLogService.createLog(10L, buildInputDto(null, "COMIDA", "Nota")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Profile not found");

        verify(careLogRepo, never()).save(any());
    }

    @Test
    void createLog_perfilDeOtroUsuario_lanzaRuntimeException() {
        profile.setUser(stranger);
        when(profileRepo.findById(10L)).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> careLogService.createLog(10L, buildInputDto(null, "COMIDA", "Nota")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No tienes permiso");

        verify(careLogRepo, never()).save(any());
    }

    @Test
    void createLog_perfilDeOtroUsuario_noLlamaAlRepositorio() {
        profile.setUser(stranger);
        when(profileRepo.findById(10L)).thenReturn(Optional.of(profile));

        try {
            careLogService.createLog(10L, buildInputDto(null, "COMIDA", "Nota"));
        } catch (RuntimeException ignored) {}

        verifyNoInteractions(careLogRepo);
    }

    // ─────────────────────────────────────────────
    // getLogs() — paginado
    // ─────────────────────────────────────────────

    @Test
    void getLogs_success_devuelvePaginaDeDtos() {
        CareLog log1 = buildLog(1L, "COMIDA", "Desayunó");
        CareLog log2 = buildLog(2L, "MEDICACION", "Pastilla");
        CareLogDto dto1 = buildOutputDto(1L, 10L, "COMIDA");
        CareLogDto dto2 = buildOutputDto(2L, 10L, "MEDICACION");

        when(profileRepo.findById(10L)).thenReturn(Optional.of(profile));
        when(careLogRepo.findByProfile_IdOrderByCreatedAtDesc(eq(10L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(log1, log2)));
        when(mapper.map(log1, CareLogDto.class)).thenReturn(dto1);
        when(mapper.map(log2, CareLogDto.class)).thenReturn(dto2);

        Page<CareLogDto> result = careLogService.getLogs(10L, defaultPageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting(CareLogDto::getId).containsExactly(1L, 2L);
    }

    @Test
    void getLogs_sinLogs_devuelvePaginaVacia() {
        when(profileRepo.findById(10L)).thenReturn(Optional.of(profile));
        when(careLogRepo.findByProfile_IdOrderByCreatedAtDesc(eq(10L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<CareLogDto> result = careLogService.getLogs(10L, defaultPageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void getLogs_perfilNoEncontrado_lanzaRuntimeException() {
        when(profileRepo.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> careLogService.getLogs(10L, defaultPageable))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Profile not found");

        verify(careLogRepo, never()).findByProfile_IdOrderByCreatedAtDesc(any(), any());
    }

    @Test
    void getLogs_perfilDeOtroUsuario_lanzaRuntimeException() {
        profile.setUser(stranger);
        when(profileRepo.findById(10L)).thenReturn(Optional.of(profile));

        assertThatThrownBy(() -> careLogService.getLogs(10L, defaultPageable))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("No tienes permiso");

        verify(careLogRepo, never()).findByProfile_IdOrderByCreatedAtDesc(any(), any());
    }

    @Test
    void getLogs_respetaParametrosDePaginacion() {
        Pageable pageable = PageRequest.of(2, 5);
        when(profileRepo.findById(10L)).thenReturn(Optional.of(profile));
        when(careLogRepo.findByProfile_IdOrderByCreatedAtDesc(eq(10L), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        careLogService.getLogs(10L, pageable);

        verify(careLogRepo).findByProfile_IdOrderByCreatedAtDesc(10L, pageable);
    }

    // ─────────────────────────────────────────────
    // helpers
    // ─────────────────────────────────────────────

    private CareLogDto buildInputDto(Long id, String logType, String note) {
        return CareLogDto.builder().id(id).logType(logType).note(note).build();
    }

    private CareLogDto buildOutputDto(Long id, Long profileId, String logType) {
        return CareLogDto.builder().id(id).profileId(profileId).logType(logType).build();
    }

    private CareLog buildLog(Long id, String logType, String note) {
        CareLog log = new CareLog();
        log.setId(id);
        log.setLogType(logType);
        log.setNote(note);
        return log;
    }
}
