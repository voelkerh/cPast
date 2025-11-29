package com.benskitchen.capturingthepast.domainLogic;

import com.benskitchen.capturingthepast.persistence.ArchiveStore;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArchiveRepository {

    private final ArchiveStore archiveStore;
    private final Map<String, String> archives;

    public ArchiveRepository(ArchiveStore archiveStore) {
        this.archiveStore = archiveStore;
        Map<String,String> loaded = archiveStore.loadArchives();
        this.archives = new LinkedHashMap<>(loaded == null ? Map.of() : loaded);
    }

    private boolean saveArchives(Map<String, String> archives) {
        return archiveStore.saveArchives(archives);
    }

    public boolean createArchive(String fullName, String shortName){
        if (fullName == null || shortName == null) return false;
        String fullNameNorm = normalizeString(fullName);
        String shortNameNorm = normalizeString(shortName);
        if (archives.containsKey(fullNameNorm) || archives.containsValue(shortNameNorm)) return false;
        archives.put(fullNameNorm, shortNameNorm);
        return saveArchives(archives);
    }

    public boolean deleteArchive(String fullName){
        if (fullName == null || !archives.containsKey(fullName)) return false;
        archives.remove(fullName);
        return saveArchives(archives);
    }

    public boolean updateArchive(String oldFullName, String oldShortName, String fullName, String shortName){
        if (oldFullName == null || fullName == null || shortName == null || !archives.containsKey(oldFullName)) return false;
        String fullNameNorm = normalizeString(fullName);
        String shortNameNorm = normalizeString(shortName);
        archives.put(fullNameNorm, shortNameNorm);
        if(saveArchives(archives)){
            if (!oldFullName.equals(fullNameNorm)) {
                archives.remove(oldFullName);
            }
            return true;
        } else {
        archives.put(oldFullName, oldShortName);
        archives.remove(fullNameNorm);
        return false;
    }
    }

    public List<String> readArchives(){
        List<String> output = new ArrayList<>();
        for (Map.Entry<String, String> entry : archives.entrySet()) {
            output.add(entry.getKey() + " - " + entry.getValue());
        }
        return output;
    }

    private static String normalizeString(String s){
        return s == null ? "" : s.trim();
    }

}
