package com.voelkerh.cPast.domain.repository;

import com.voelkerh.cPast.domain.model.Capture;

/**
 * Repository abstraction for persisting notes in {@link Capture} entities.
 *
 * <p>This interface defines the contract for saving notes to be used by users.
 * Concrete implementations are provided by the data layer.</p>
 */
public interface NotesRepository {

    /**
     * Persists a note from a given capture in a format to be used by users for further analysis.
     *
     * @param capture the capture holding information to persist
     * @return boolean if the operation completed successfully
     */
    boolean save(Capture capture);
}
