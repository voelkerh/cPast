package com.benskitchen.cPast.domainLogic;

import com.benskitchen.cPast.persistence.ArchiveStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ArchiveRepositoryTest {

    private ArchiveStore mockStore;
    private ArchiveRepository repository;

    @BeforeEach
    void setup() {
        mockStore = mock(ArchiveStore.class);
        when(mockStore.loadArchives()).thenReturn(new ArrayList<>());
        when(mockStore.saveArchives(any())).thenReturn(true);
        repository = new ArchiveRepository(mockStore);
    }

    @Test
    void archiveRepository_constructorNotNull() {
        assertNotNull(repository);
    }

    @Test
    void createArchive_success() {
        boolean result = repository.createArchive("Bundesarchiv", "BArch");
        assertTrue(result);
        assertEquals(1, repository.readArchives().size());
    }

    @Test
    void createArchive_preventsDuplicatesFullName() {
        repository.createArchive("Bundesarchiv", "BArch");
        boolean result = repository.createArchive("Bundesarchiv", "BArch2");
        assertFalse(result);
    }

    @Test
    void createArchive_preventsDuplicatesShortName() {
        repository.createArchive("Bundesarchiv", "BArch");
        boolean result = repository.createArchive("Archiv", "BArch");
        assertFalse(result);
    }

    @Test
    void createArchive_handlesNullFullName() {
        boolean result = repository.createArchive(null, "BArch");
        assertFalse(result);
    }

    @Test
    void createArchive_handlesNullShortName() {
        boolean result = repository.createArchive("Bundesarchiv", null);
        assertFalse(result);
    }

    @Test
    void createArchive_handlesEmptyFullName() {
        boolean result = repository.createArchive("", "BArch");
        assertFalse(result);
    }

    @Test
    void createArchive_handlesEmptyShortName() {
        boolean result = repository.createArchive("Bundesarchiv", "");
        assertFalse(result);
    }

    @Test
    void deleteArchive_success() {
        repository.createArchive("Bundesarchiv", "BArch");
        boolean result = repository.deleteArchive("Bundesarchiv");
        assertTrue(result);
        assertEquals(0, repository.readArchives().size());
    }

    @Test
    void deleteArchive_nonExistent() {
        boolean result = repository.deleteArchive("DoesNotExist");
        assertFalse(result);
    }

    @Test
    void deleteArchive_handlesNullFullName() {
        boolean result = repository.deleteArchive(null);
        assertFalse(result);
    }

    @Test
    void deleteArchive_handlesEmptyFullName() {
        boolean result = repository.deleteArchive("");
        assertFalse(result);
    }

    @Test
    void updateArchive_success() {
        repository.createArchive("Bundesarchiv", "BuArch");
        boolean result = repository.updateArchive("Bundesarchiv", "BuArch", "Bundesarchiv", "BArch");
        assertTrue(result);
        assertEquals("BArch", repository.readArchives().get(0).getShortName());
        assertEquals(1, repository.readArchives().size());
    }

    @Test
    void updateArchive_newFullName() {
        repository.createArchive("Bundesarchiv", "BArch");
        boolean result = repository.updateArchive("Bundesarchiv", "BArch", "Landesarchiv", "BArch");
        assertTrue(result);
        assertEquals("BArch", repository.readArchives().get(0).getShortName());
        assertNotEquals("Bundesarchiv", repository.readArchives().get(0).getFullName());
        assertEquals(1, repository.readArchives().size());
    }

    @Test
    void updateArchive_handlesStorageFailure() {
        repository.createArchive("Bundesarchiv", "BArch");

        when(mockStore.saveArchives(any())).thenReturn(false);
        boolean result = repository.updateArchive("Bundesarchiv", "New Name", "NN", "NN");

        assertFalse(result);
    }

    @Test
    void updateArchive_handlesNullArchiveName() {
        repository.createArchive("Bundesarchiv", "BArch");
        boolean result = repository.updateArchive("Bundesarchiv", null, "NN", "NN");
        assertFalse(result);
    }

    @Test
    void readArchive_success() {
        repository.createArchive("Bundesarchiv", "BArch");
        List<Archive> archives = repository.readArchives();
        assertEquals(1, archives.size());
        assertEquals("BArch", archives.get(0).getShortName());
    }

    @Test
    void normalizeString_trimsWhitespace() {
        repository.createArchive("  Bundesarchiv  ", "  BArch  ");
        List<Archive> archives = repository.readArchives();
        assertEquals("Bundesarchiv - BArch", archives.get(0).toString());
    }

}