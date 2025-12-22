package com.benskitchen.capturingthepast.domainLogic;

import com.benskitchen.capturingthepast.persistence.RecentCapturesStore;

import java.util.List;

public class RecentCapturesRepository {

    private static final int MAX_RECENT_FILES = 5;

    private final RecentCapturesStore recentCapturesStore;
    private final List<Capture> recentCaptures;

    public RecentCapturesRepository(RecentCapturesStore recentCapturesStore) {
        this.recentCapturesStore = recentCapturesStore;
        this.recentCaptures = recentCapturesStore.loadRecentFiles();
    }

    public boolean addFileToRecentCaptures(Capture capture) {
        String fileName = capture.getFileName();
        if (fileName == null || fileName.isEmpty()) return false;

        recentCaptures.add(capture);

        while (recentCaptures.size() > MAX_RECENT_FILES) {
            recentCaptures.remove(0);
        }

        return recentCapturesStore.saveRecentFiles(recentCaptures);
    }

    public List<Capture> getRecentCaptures() {
        return List.copyOf(recentCaptures);
    }

}
