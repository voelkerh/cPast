package com.voelkerh.cPast.data.recentCaptures;

import com.voelkerh.cPast.domain.Capture;
import com.voelkerh.cPast.domain.RecentCapturesRepository;

import java.util.List;

public class RecentCapturesRepositoryImpl implements RecentCapturesRepository {

    private static final int MAX_RECENT_FILES = 5;

    private final JsonRecentCapturesStore recentCapturesStore;
    private final List<Capture> recentCaptures;

    public RecentCapturesRepositoryImpl(JsonRecentCapturesStore recentCapturesStore) {
        this.recentCapturesStore = recentCapturesStore;
        this.recentCaptures = recentCapturesStore.loadRecentFiles();
    }

    @Override
    public boolean addFileToRecentCaptures(Capture capture) {
        String fileName = capture.getFileName();
        if (fileName == null || fileName.isEmpty()) return false;

        recentCaptures.add(capture);

        while (recentCaptures.size() > MAX_RECENT_FILES) {
            recentCaptures.remove(0);
        }

        return recentCapturesStore.saveRecentFiles(recentCaptures);
    }

    @Override
    public List<Capture> getRecentCaptures() {
        return List.copyOf(recentCaptures);
    }

}
