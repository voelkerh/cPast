package com.voelkerh.cPast.domain.repository;

import com.voelkerh.cPast.domain.model.Archive;

import java.util.List;

/**
 * Repository abstraction for accessing and persisting {@link Archive} entities.
 *
 * <p>This interface defines the contract for loading and saving archives.
 * Concrete implementations are provided by the data layer.</p>
 */
public interface ArchiveRepository {

    /**
     * Loads all persisted archives.
     *
     * @return a list of archives; If no archives are stored, an empty list is returned.
     */
    List<Archive> load();

    /**
     * Persists a given list of archives.
     *
     * @param archives the archives to persist
     * @return boolean if the operation completed successfully
     */
    boolean save(List<Archive> archives);
}
