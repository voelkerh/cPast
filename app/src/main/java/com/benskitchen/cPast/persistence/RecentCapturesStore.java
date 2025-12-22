package com.benskitchen.cPast.persistence;

import com.benskitchen.cPast.domainLogic.Capture;

import java.util.List;

public interface RecentCapturesStore {
    List<Capture> loadRecentFiles();
    boolean saveRecentFiles(List<Capture> files);
}
