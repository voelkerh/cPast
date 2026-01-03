package com.voelkerh.cPast.data;

import com.voelkerh.cPast.domain.Capture;

public interface NoteStore {
    boolean saveNote(Capture capture);
}
