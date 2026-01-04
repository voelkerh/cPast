package com.voelkerh.cPast.domain.repository;

import com.voelkerh.cPast.domain.model.Capture;

import java.util.List;

public interface RecentCapturesRepository {
    boolean addFileToRecentCaptures(Capture capture);

    List<Capture> getRecentCaptures();
}
