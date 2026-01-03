package com.voelkerh.cPast.domain;

import java.util.List;

public interface RecentCapturesRepository {
    boolean addFileToRecentCaptures(Capture capture);

    List<Capture> getRecentCaptures();
}
