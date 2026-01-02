package com.voelkerh.cPast.domainLogic;

import com.voelkerh.cPast.persistence.ArchiveStore;

import java.util.ArrayList;
import java.util.List;

public class ArchiveRepository {

    private final ArchiveStore archiveStore;
    private final List<Archive> archives;

    public ArchiveRepository(ArchiveStore archiveStore) {
        this.archiveStore = archiveStore;
        List<Archive> loaded = archiveStore.loadArchives();
        this.archives = new ArrayList<>(loaded == null ? List.of() : loaded);
    }

    private boolean saveArchives(List<Archive> archives) {
        if (archives == null) return false;
        return archiveStore.saveArchives(archives);
    }

    public boolean createArchive(String fullName, String shortName){
        if (fullName == null || shortName == null) return false;

        String fullNameNorm = normalizeString(fullName);
        String shortNameNorm = normalizeString(shortName);
        if (fullName.isEmpty() || shortName.isEmpty()) return false;

        if (findByFullName(archives, fullNameNorm) != null) return false;
        if (findByShortName(archives, shortNameNorm) != null) return false;

        archives.add(new Archive(fullNameNorm, shortNameNorm));
        return saveArchives(archives);
    }

    public boolean deleteArchive(String fullName){
        String fullNameNorm = normalizeString(fullName);
        if (fullNameNorm.isEmpty()) return false;

        Archive existing = findByFullName(archives, fullNameNorm);
        if (existing == null) return false;

        archives.remove(existing);
        return saveArchives(archives);
    }

    public boolean updateArchive(String oldFullName, String oldShortName, String fullName, String shortName) {
        String oldFullNameNorm = normalizeString(oldFullName);
        String oldShortNameNorm = normalizeString(oldShortName);
        String fullNameNorm = normalizeString(fullName);
        String shortNameNorm = normalizeString(shortName);

        if (oldFullNameNorm.isEmpty() || oldShortNameNorm.isEmpty()) return false;
        if (fullNameNorm.isEmpty() || shortNameNorm.isEmpty()) return false;

        Archive existing = findByFullName(archives, oldFullNameNorm);
        if (existing == null) return false;

        Archive sameFull = findByFullName(archives, fullNameNorm);
        if (sameFull != null && sameFull != existing) return false;

        Archive sameShort = findByShortName(archives, shortNameNorm);
        if (sameShort != null && sameShort != existing) return false;

        existing.setFullName(fullNameNorm);
        existing.setShortName(shortNameNorm);

        return saveArchives(archives);
    }


    public List<Archive> readArchives(){
        return archives;
    }

    private static String normalizeString(String s){
        return s == null ? "" : s.trim();
    }

    private static Archive findByFullName(List<Archive> archives, String fullNameNorm) {
        for (Archive archive : archives) {
            if (archive == null) continue;
            if (normalizeString(archive.getFullName()).equals(fullNameNorm)) return archive;
        }
        return null;
    }

    private static Archive findByShortName(List<Archive> archives, String shortNameNorm) {
        for (Archive archive : archives) {
            if (archive == null) continue;
            if (normalizeString(archive.getShortName()).equals(shortNameNorm)) return archive;
        }
        return null;
    }


}
