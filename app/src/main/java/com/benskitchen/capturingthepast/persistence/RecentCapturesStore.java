package com.benskitchen.capturingthepast.persistence;

import com.benskitchen.capturingthepast.domainLogic.Capture;

import java.util.List;

public interface RecentCapturesStore {
    List<Capture> loadRecentFiles();
    boolean saveRecentFiles(List<Capture> files);
}
