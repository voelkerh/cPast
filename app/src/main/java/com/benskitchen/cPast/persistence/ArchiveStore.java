package com.benskitchen.cPast.persistence;

import java.util.Map;

public interface ArchiveStore {
    Map<String, String> loadArchives();

    boolean saveArchives(Map<String, String> archives);
}
