package com.voelkerh.cPast.domain.usecase;

import com.voelkerh.cPast.domain.model.Archive;
import com.voelkerh.cPast.domain.model.Capture;
import com.voelkerh.cPast.domain.repository.NotesRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class WriteNotesUseCaseTest {

    @Test
    void writeNotesUseCase_constructorNotNull() {
        NotesRepository notesRepository = mock(NotesRepository.class);

        WriteNotesUseCase writeNotesUseCase = new WriteNotesUseCase(notesRepository);

        assertNotNull(writeNotesUseCase);
    }

    @Test
    void saveNote_callsSaveOnRepository() {
        NotesRepository notesRepository = mock(NotesRepository.class);
        WriteNotesUseCase writeNotesUseCase = new WriteNotesUseCase(notesRepository);
        Capture capture = new Capture(mock(Archive.class), "file.jpg", "note");

        writeNotesUseCase.saveNote(capture);

        verify(notesRepository, times(1)).save(capture);
    }

    @Test
    void saveNote_returnsFalseWhenCaptureIsNull() {
        NotesRepository notesRepository = mock(NotesRepository.class);
        WriteNotesUseCase writeNotesUseCase = new WriteNotesUseCase(notesRepository);
        Capture capture = null;

        boolean actual = writeNotesUseCase.saveNote(capture);

        verify(notesRepository, never()).save(capture);
        assertFalse(actual);
    }

    @Test
    void saveNote_returnsFalseWhenNoteIsEmpty() {
        NotesRepository notesRepository = mock(NotesRepository.class);
        WriteNotesUseCase writeNotesUseCase = new WriteNotesUseCase(notesRepository);
        Capture capture = new Capture(mock(Archive.class), "file.jpg", "");

        boolean actual = writeNotesUseCase.saveNote(capture);

        verify(notesRepository, never()).save(capture);
        assertFalse(actual);
    }

    @Test
    void saveNote_returnsFalseWhenNoteIsNull() {
        NotesRepository notesRepository = mock(NotesRepository.class);
        WriteNotesUseCase writeNotesUseCase = new WriteNotesUseCase(notesRepository);
        Capture capture = new Capture(mock(Archive.class), "file.jpg", null);

        boolean actual = writeNotesUseCase.saveNote(capture);

        verify(notesRepository, never()).save(capture);
        assertFalse(actual);
    }

    @Test
    void saveNote_returnsFalseWhenFileNameIsNull() {
        NotesRepository notesRepository = mock(NotesRepository.class);
        WriteNotesUseCase writeNotesUseCase = new WriteNotesUseCase(notesRepository);
        Capture capture = new Capture(mock(Archive.class), null, "note");

        boolean actual = writeNotesUseCase.saveNote(capture);

        verify(notesRepository, never()).save(capture);
        assertFalse(actual);
    }

    @Test
    void saveNote_returnsFalseWhenArchiveIsNull() {
        NotesRepository notesRepository = mock(NotesRepository.class);
        WriteNotesUseCase writeNotesUseCase = new WriteNotesUseCase(notesRepository);
        Capture capture = new Capture(null, "file.jpg", "note");

        boolean actual = writeNotesUseCase.saveNote(capture);

        verify(notesRepository, never()).save(capture);
        assertFalse(actual);
    }
}
