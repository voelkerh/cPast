package com.voelkerh.cPast.data;

import com.voelkerh.cPast.domain.Archive;

import java.util.List;

public interface ArchiveStore {
    List<Archive> loadArchives();

    boolean saveArchives(List<Archive> archives);
}
