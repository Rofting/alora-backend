package com.alora.carelog.api;

import com.alora.carelog.dto.CreateCareNoteDto;
import com.alora.carelog.model.CareNote;
import com.alora.carelog.service.CareService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{profileId}/notes") // Ruta anidada bajo el perfil
public class CareLogController {

    private final CareService careService;

    public CareLogController(CareService careService) {
        this.careService = careService;
    }

    /**
     * Obtiene todas las notas de la bitácora para un perfil específico.
     */
    @GetMapping
    public ResponseEntity<List<CareNote>> getNotesForProfile(@PathVariable Long profileId) {
        List<CareNote> notes = careService.listNotes(profileId);
        return ResponseEntity.ok(notes);
    }

    /**
     * Añade una nueva nota a la bitácora de un perfil.
     */
    @PostMapping
    public ResponseEntity<CareNote> addNoteToProfile(
            @PathVariable Long profileId,
            @Valid @RequestBody CreateCareNoteDto dto
    ) {
        // Creamos la entidad CareNote a partir del DTO y el ID de la URL
        CareNote newNote = new CareNote();
        newNote.setProfileId(profileId);
        newNote.setText(dto.getText());

        CareNote savedNote = careService.addNote(newNote);

        // Devolvemos 201 Created y la nota creada
        return ResponseEntity.status(HttpStatus.CREATED).body(savedNote);
    }
}