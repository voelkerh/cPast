package com.voelkerh.cPast.data;

import com.voelkerh.cPast.domain.Capture;

import java.util.List;

public interface RecentCapturesStore {
    List<Capture> loadRecentFiles();

    boolean saveRecentFiles(List<Capture> files);
}
