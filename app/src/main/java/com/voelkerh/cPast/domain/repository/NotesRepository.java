package com.voelkerh.cPast.domain.repository;

import com.voelkerh.cPast.domain.model.Capture;

public interface NotesRepository {
    boolean saveNote(Capture capture);
}
