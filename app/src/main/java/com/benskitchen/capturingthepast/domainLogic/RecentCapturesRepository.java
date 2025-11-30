package com.benskitchen.capturingthepast.domainLogic;

import com.benskitchen.capturingthepast.persistence.RecentCapturesStore;

import java.util.ArrayList;
import java.util.List;

public class RecentCapturesRepository {

    private static final int MAx_RECENT_FILES = 5;

    private final RecentCapturesStore recentCapturesStore;
    private final List<String> recentCaptures;

    public RecentCapturesRepository(RecentCapturesStore recentCapturesStore) {
        this.recentCapturesStore = recentCapturesStore;
        this.recentCaptures = recentCapturesStore.loadRecentFiles();
    }

    public boolean addFileToRecentCaptures(String fileName) {
        if (fileName == null || fileName.isEmpty()) return false;

        recentCaptures.add(fileName);

        while (recentCaptures.size() > MAx_RECENT_FILES) {
            recentCaptures.remove(0);
        }

        return recentCapturesStore.saveRecentFiles(recentCaptures);
    }

    public List<String> getRecentCaptures() {
        return List.copyOf(recentCaptures);
    }

    public boolean clearRecentCaptures() {
        recentCaptures.clear();
        return recentCapturesStore.saveRecentFiles(recentCaptures);
    }

}
