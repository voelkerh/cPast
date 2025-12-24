package com.benskitchen.cPast.persistence;

import com.benskitchen.cPast.domainLogic.Capture;

public interface NoteStore {
    boolean saveNote(Capture capture);
}
