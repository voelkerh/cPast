package com.benskitchen.capturingthepast.persistence;

public interface NoteStore {
    boolean saveNote(String note);
}
