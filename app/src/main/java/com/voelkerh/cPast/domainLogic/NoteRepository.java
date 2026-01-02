package com.voelkerh.cPast.domainLogic;

import com.voelkerh.cPast.persistence.NoteStore;

import java.time.LocalDateTime;

public class NoteRepository {

    private final NoteStore noteStore;

    public NoteRepository(NoteStore noteStore) {
        this.noteStore = noteStore;
    }

    public boolean saveNote(Capture capture) {
        String imageFileName = capture.getFileName();
        String note = capture.getNote();
        LocalDateTime captureTime = capture.getCaptureTime();
        if (imageFileName == null || note == null || note.isEmpty() || captureTime == null) return false;

        return noteStore.saveNote(capture);
    }
}
