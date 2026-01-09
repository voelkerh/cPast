package com.voelkerh.cPast.domain.usecase;

import com.voelkerh.cPast.domain.model.Capture;
import com.voelkerh.cPast.domain.repository.RecentCapturesRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Use case that manages a list of {@link Capture} entities.
 *
 * <p>This class encapsulates all business rules related to adding, overwriting and reading recent captures.
 * It maintains an in-memory representation of the recent captures list and persists changes via an {@link RecentCapturesRepository}.</p>
 */
public class ManageRecentCapturesUseCase {

    private static final int MAX_RECENT_FILES = 5;

    private final RecentCapturesRepository recentCapturesRepository;
    private final List<Capture> recentCaptures;

    /**
     * Creates a new use case instance and initializes its state from the repository.
     *
     * @param recentCapturesRepository repository used to load and persist captures
     */
    public ManageRecentCapturesUseCase(RecentCapturesRepository recentCapturesRepository) {
        this.recentCapturesRepository = recentCapturesRepository;
        List<Capture> loaded = recentCapturesRepository.load();
        this.recentCaptures = new ArrayList<>(loaded == null ? List.of() : loaded);
    }

    /**
     * Adds a capture to the list of recent captures.
     *
     * <p>If the maximum number of recent captures is exceeded, the oldest entry
     * is removed. The updated list is persisted via the repository.</p>
     *
     * @param capture the capture to add
     * @return boolean if the capture was added and persisted successfully
     */
    public boolean addFileToRecentCaptures(Capture capture) {
        if (capture == null) return false;
        String fileName = capture.getFileName();
        if (fileName == null || fileName.isEmpty()) return false;

        recentCaptures.add(capture);

        while (recentCaptures.size() > MAX_RECENT_FILES) {
            recentCaptures.remove(0);
        }

        return recentCapturesRepository.save(recentCaptures);
    }

    /**
     * Returns the current list of recent captures.
     *
     * @return an immutable list of recent captures
     */
    public List<Capture> getRecentCaptures() {
        return List.copyOf(recentCaptures);
    }
}
