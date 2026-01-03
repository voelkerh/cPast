package com.voelkerh.cPast.data.notes;

import com.voelkerh.cPast.domain.model.Capture;

public interface NotesStore {

    boolean saveNote(Capture capture);

}
