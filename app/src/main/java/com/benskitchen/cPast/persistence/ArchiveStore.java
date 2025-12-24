package com.benskitchen.cPast.persistence;

import com.benskitchen.cPast.domainLogic.Archive;

import java.util.List;

public interface ArchiveStore {
    List<Archive> loadArchives();

    boolean saveArchives(List<Archive> archives);
}
