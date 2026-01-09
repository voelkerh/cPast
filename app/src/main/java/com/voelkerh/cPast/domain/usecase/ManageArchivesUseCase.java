package com.voelkerh.cPast.domain.usecase;

import com.voelkerh.cPast.domain.model.Archive;
import com.voelkerh.cPast.domain.repository.ArchiveRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Use case that manages the lifecycle of {@link Archive} entities.
 *
 * <p>This class encapsulates all business rules related to creating, updating,
 * deleting, and reading archives. It maintains an in-memory representation of
 * the current archive list and persists changes via an {@link ArchiveRepository}.</p>
 */
public class ManageArchivesUseCase {

    private final ArchiveRepository archiveRepository;
    private final List<Archive> archives;

    /**
     * Creates a new use case instance and initializes its state from the repository.
     *
     * @param archiveRepository repository used to load and persist archives
     */
    public ManageArchivesUseCase(ArchiveRepository archiveRepository) {
        this.archiveRepository = archiveRepository;
        List<Archive> loaded = archiveRepository.load();
        this.archives = new ArrayList<>(loaded == null ? List.of() : loaded);
    }

    /**
     * Creates a new archive if the given names are valid and unique.
     *
     * @param fullName  the full name of the archive
     * @param shortName the short name of the archive
     * @return boolean if the archive was created and persisted successfully
     */
    public boolean createArchive(String fullName, String shortName) {
        if (fullName == null || shortName == null) return false;

        String fullNameNorm = normalizeString(fullName);
        String shortNameNorm = normalizeString(shortName);
        if (fullName.isEmpty() || shortName.isEmpty()) return false;

        if (findByFullName(archives, fullNameNorm) != null) return false;
        if (findByShortName(archives, shortNameNorm) != null) return false;

        archives.add(new Archive(fullNameNorm, shortNameNorm));
        return archiveRepository.save(archives);
    }

    /**
     * Deletes an existing archive identified by its full name.
     *
     * @param fullName the full name of the archive to delete
     * @return boolean if the archive existed and was deleted successfully
     */
    public boolean deleteArchive(String fullName) {
        String fullNameNorm = normalizeString(fullName);
        if (fullNameNorm.isEmpty()) return false;

        Archive existing = findByFullName(archives, fullNameNorm);
        if (existing == null) return false;

        archives.remove(existing);
        return archiveRepository.save(archives);
    }

    /**
     * Updates the names of an existing archive.
     *
     * <p>The update is rejected if the original archive does not exist or if the new
     * names would conflict with another archive.</p>
     *
     * @param oldFullName  the current full name of the archive
     * @param oldShortName the current short name of the archive
     * @param fullName     the new full name
     * @param shortName    the new short name
     * @return boolean if the archive was updated and persisted successfully
     */
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

        return archiveRepository.save(archives);
    }

    /**
     * Returns all currently known archives.
     *
     * @return an immutable list of archives
     */
    public List<Archive> readArchives() {
        return List.copyOf(archives);
    }

    private static String normalizeString(String s) {
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
