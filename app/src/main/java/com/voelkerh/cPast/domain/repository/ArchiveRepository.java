package com.voelkerh.cPast.domain.repository;

import com.voelkerh.cPast.domain.model.Archive;

import java.util.List;

public interface ArchiveRepository {
    boolean createArchive(String fullName, String shortName);

    boolean deleteArchive(String fullName);

    boolean updateArchive(String oldFullName, String oldShortName, String fullName, String shortName);

    List<Archive> readArchives();
}
