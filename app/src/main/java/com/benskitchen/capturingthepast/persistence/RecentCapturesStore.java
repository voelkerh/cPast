package com.benskitchen.capturingthepast.persistence;

import java.util.List;

public interface RecentCapturesStore {
    List<String> loadRecentFiles();
    boolean saveRecentFiles(List<String> files);
}
