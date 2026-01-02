package com.voelkerh.cPast.persistence;

import com.voelkerh.cPast.domainLogic.Capture;

public interface NoteStore {
    boolean saveNote(Capture capture);
}
