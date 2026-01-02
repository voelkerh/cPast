package com.voelkerh.cPast.persistence;

import com.voelkerh.cPast.domainLogic.Capture;

import java.util.List;

public interface RecentCapturesStore {
    List<Capture> loadRecentFiles();
    boolean saveRecentFiles(List<Capture> files);
}
