package com.benskitchen.capturingthepast.domainLogic;

import com.benskitchen.capturingthepast.persistence.NoteStore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class NoteRepository {

    private final NoteStore noteStore;

    public NoteRepository(NoteStore noteStore) {
        this.noteStore = noteStore;
    }

    public boolean saveNote(Capture capture) {
        String imageFileName = capture.getFileName();
        String note = capture.getNote();
        if (imageFileName == null || note == null || note.isEmpty()) return false;

        String humanisedTime = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).format(LocalDateTime.now());

        return noteStore.saveNote(humanisedTime, imageFileName, note);
    }
}
