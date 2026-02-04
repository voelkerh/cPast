package com.voelkerh.cPast.domain.usecase;

import com.voelkerh.cPast.domain.model.Capture;
import com.voelkerh.cPast.domain.repository.NotesRepository;

/**
 * Use case responsible for persisting textual notes associated with captures.
 *
 * <p>This use case validates input via {@link Capture} entities.
 * It delegates persistence to a {@link NotesRepository}.</p>
 */
public class WriteNotesUseCase {

    private final NotesRepository notesRepository;

    /**
     * Creates a new use case instance.
     *
     * @param notesRepository repository used to persist notes
     */
    public WriteNotesUseCase(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    /**
     * Persists the note associated with the given capture.
     *
     * <p>The operation is rejected if the capture is incomplete.</p>
     *
     * @param capture the capture containing note data
     * @return boolea if the note was successfully validated and persisted
     */
    public boolean saveNote(Capture capture) {
        if (capture == null) return false;

        if (capture.getFileName() == null ||
                capture.getNote() == null ||
                capture.getCaptureTime() == null ||
                capture.getArchive() == null) {
            return false;
        }

        return notesRepository.save(capture);
    }
}
