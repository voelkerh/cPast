package com.voelkerh.cPast.persistence;

import com.voelkerh.cPast.domainLogic.Archive;

import java.util.List;

public interface ArchiveStore {
    List<Archive> loadArchives();

    boolean saveArchives(List<Archive> archives);
}
