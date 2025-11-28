package com.alora.carelog.service;

import com.alora.carelog.model.CareNote;
import com.alora.carelog.repository.CareNoteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CareService {
    private final CareNoteRepository repo;
    public CareService(CareNoteRepository repo) { this.repo = repo; }

    public CareNote addNote(CareNote note) { return repo.save(note); }
    public List<CareNote> listNotes(Long profileId) { return repo.findByProfileIdOrderByCreatedAtDesc(profileId); }
}