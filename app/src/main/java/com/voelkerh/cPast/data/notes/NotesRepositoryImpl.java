package com.voelkerh.cPast.data.notes;

import com.voelkerh.cPast.domain.NotesRepository;
import com.voelkerh.cPast.domain.Capture;

import java.time.LocalDateTime;

public class NotesRepositoryImpl implements NotesRepository {

    private final CsvNotesStore noteStore;

    public NotesRepositoryImpl(CsvNotesStore noteStore) {
        this.noteStore = noteStore;
    }

    @Override
    public boolean saveNote(Capture capture) {
        String imageFileName = capture.getFileName();
        String note = capture.getNote();
        LocalDateTime captureTime = capture.getCaptureTime();
        if (imageFileName == null || note == null || note.isEmpty() || captureTime == null) return false;

        return noteStore.saveNote(capture);
    }
}
