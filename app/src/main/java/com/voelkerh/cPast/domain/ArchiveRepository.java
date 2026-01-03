package com.voelkerh.cPast.domain;

import java.util.List;

public interface ArchiveRepository {
    boolean createArchive(String fullName, String shortName);

    boolean deleteArchive(String fullName);

    boolean updateArchive(String oldFullName, String oldShortName, String fullName, String shortName);

    List<Archive> readArchives();
}
