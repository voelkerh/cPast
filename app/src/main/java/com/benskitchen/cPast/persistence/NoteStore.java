package com.benskitchen.cPast.persistence;

public interface NoteStore {
    boolean saveNote(String time, String imageName, String note);
}
