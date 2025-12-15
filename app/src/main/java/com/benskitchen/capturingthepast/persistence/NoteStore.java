package com.benskitchen.capturingthepast.persistence;

public interface NoteStore {
    boolean saveNote(String time, String imageName, String note);
}
