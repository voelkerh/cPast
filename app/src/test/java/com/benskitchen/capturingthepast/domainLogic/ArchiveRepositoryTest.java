package com.benskitchen.capturingthepast.domainLogic;

import com.benskitchen.capturingthepast.persistence.ArchiveStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArchiveRepositoryTest {

    private ArchiveStore mockStore;
    private ArchiveRepository repository;

    @BeforeEach
    void setup() {
        mockStore = mock(ArchiveStore.class);
        when(mockStore.loadArchives()).thenReturn(new HashMap<>());
        when(mockStore.saveArchives(any())).thenReturn(true);
        repository = new ArchiveRepository(mockStore);
    }

    @Test
    void archiveRepository_constructorNotNull(){
        assertNotNull(repository);
    }

    @Test
    void createArchive_success() {
        boolean result = repository.createArchive("Bundesarchiv", "BArch");
        assertTrue(result);
        assertEquals(1, repository.readArchives().size());
    }

    @Test
    void createArchive_preventsDuplicates() {
        repository.createArchive("Bundesarchiv", "BArch");
        boolean result = repository.createArchive("Bundesarchiv", "BArch2");
        assertFalse(result);
    }

    @Test
    void createArchive_handlesNull() {
        assertFalse(repository.createArchive(null, "BArch"));
        assertFalse(repository.createArchive("Bundesarchiv", null));
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
    void updateArchive_handlesStorageFailure() {
        repository.createArchive("Bundesarchiv", "BArch");

        when(mockStore.saveArchives(any())).thenReturn(false);
        boolean result = repository.updateArchive("Bundesarchiv", "New Name", "NN", "NN");

        assertFalse(result);
    }

    @Test
    void normalizeString_trimsWhitespace() {
        repository.createArchive("  Bundesarchiv  ", "  BArch  ");
        List<String> archives = repository.readArchives();
        assertEquals("Bundesarchiv - BArch", archives.get(0));
    }
}