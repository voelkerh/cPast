package com.voelkerh.cPast.domain.repository;

import com.voelkerh.cPast.domain.model.Capture;

import java.util.List;

/**
 * Repository abstraction for accessing and persisting {@link Capture} entities.
 *
 * <p>This interface defines the contract for loading and saving the most recent captures.
 * Concrete implementations are provided by the data layer.</p>
 */
public interface RecentCapturesRepository {

    /**
     * Loads all persisted recent captures.
     *
     * @return a list of recent captures; If no captures are stored, an empty list is returned.
     */
    List<Capture> load();

    /**
     * Persists a given list of captures.
     *
     * @param recentCaptures the captures to persist
     * @return boolean if the operation completed successfully
     */
    boolean save(List<Capture> recentCaptures);
}
