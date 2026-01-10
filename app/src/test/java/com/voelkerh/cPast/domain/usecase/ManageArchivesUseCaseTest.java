package com.voelkerh.cPast.domain.usecase;

import com.voelkerh.cPast.domain.model.Archive;
import com.voelkerh.cPast.domain.repository.ArchiveRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ManageArchivesUseCaseTest {

    @Test
    void manageArchivesUseCase_constructorNotNull() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);

        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        assertNotNull(manageArchivesUseCase);
    }

    @Test
    void manageArchivesUseCase_constructorSetsList() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.load()).thenReturn(null);

        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        List<Archive> archives = manageArchivesUseCase.readArchives();

        assertNotNull(archives);
    }

    @Test
    void createArchive_returnsFalseForNullFullName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        boolean actual = manageArchivesUseCase.createArchive(null, "BArch");

        assertFalse(actual);
    }

    @Test
    void createArchive_returnsFalseForNullShortName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        boolean actual = manageArchivesUseCase.createArchive("Bundesarchiv", null);

        assertFalse(actual);
    }

    @Test
    void createArchive_returnsFalseForSpaceShortName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        boolean actual = manageArchivesUseCase.createArchive("Bundesarchiv", " ");

        assertFalse(actual);
    }

    @Test
    void createArchive_returnsFalseForEmptyFullName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        boolean actual = manageArchivesUseCase.createArchive("", "BArch");

        assertFalse(actual);
    }

    @Test
    void createArchive_returnsFalseForExistingFullName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        manageArchivesUseCase.createArchive("Bundesarchiv", "LAB");
        boolean actual = manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");

        assertFalse(actual);
    }

    @Test
    void createArchive_returnsFalseForExistingShortName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        manageArchivesUseCase.createArchive("Bundesarchiv", "LAB");
        boolean actual = manageArchivesUseCase.createArchive("Landesarchiv", "LAB");

        assertFalse(actual);
    }

    @Test
    void createArchive_callsArchiveRepository() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");

        verify(archiveRepository).save(any());
    }

    @Test
    void createArchive_addsToArchiveList() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");
        manageArchivesUseCase.createArchive("Landesarchiv", "LAB");
        List<Archive> archives = manageArchivesUseCase.readArchives();
        int actual = archives.size();
        int expected = 2;

        assertEquals(expected, actual);
    }

    @Test
    void deleteArchive_callsArchiveRepository() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");

        manageArchivesUseCase.deleteArchive("Bundesarchiv");

        verify(archiveRepository, times(2)).save(any());
    }

    @Test
    void deleteArchive_returnsFalseForEmptyFullName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");

        boolean actual = manageArchivesUseCase.deleteArchive("");

        assertFalse(actual);
    }

    @Test
    void deleteArchive_returnsFalseForSpaceFullName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive(" ", "BArch");

        boolean actual = manageArchivesUseCase.deleteArchive(" ");

        assertFalse(actual);
    }

    @Test
    void deleteArchive_returnsFalseForNonExistingFullName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        boolean actual = manageArchivesUseCase.deleteArchive("Bundesarchiv");

        assertFalse(actual);
    }

    @Test
    void deleteArchive_deletesFromArchiveList() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");
        manageArchivesUseCase.createArchive("Landesarchive", "LAB");

        manageArchivesUseCase.deleteArchive("Bundesarchiv");
        List<Archive> archives = manageArchivesUseCase.readArchives();
        int actual = archives.size();
        int expected = 1;

        assertEquals(expected, actual);
    }

    @Test
    void updateArchive_callsArchiveRepository() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");
        manageArchivesUseCase.updateArchive("Bundesarchiv", "BArch", "Bundesarchiv", "BuArch");

        verify(archiveRepository, times(2)).save(any());
    }

    @Test
    void updateArchive_returnsFalseForEmptyOldFullName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");

        boolean actual = manageArchivesUseCase.updateArchive("", "BArch", "Bundesarchiv", "BuArch");

        assertFalse(actual);
    }

    @Test
    void updateArchive_returnsFalseForSpaceOldShortName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");

        boolean actual = manageArchivesUseCase.updateArchive("Bundesarchiv", " ", "Bundesarchiv", "BuArch");

        assertFalse(actual);
    }

    @Test
    void updateArchive_returnsFalseForEmptyNewFullName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");

        boolean actual = manageArchivesUseCase.updateArchive("Bundesarchiv", "BArch", "", "BuArch");

        assertFalse(actual);
    }

    @Test
    void updateArchive_returnsFalseForEmptyNewShortName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");

        boolean actual = manageArchivesUseCase.updateArchive("Bundesarchiv", "BArch", "Bundesarchiv", "");

        assertFalse(actual);
    }

    @Test
    void updateArchive_returnsFalseForNonExistingFullName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        boolean actual = manageArchivesUseCase.updateArchive("Bundesarchiv", "BArch", "Bundesarchiv", "BuArch");

        assertFalse(actual);
    }

    @Test
    void updateArchive_updatesFullName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");

        manageArchivesUseCase.updateArchive("Bundesarchive", "BArch", "Bundesarchiv", "BArch");
        List<Archive> archives = manageArchivesUseCase.readArchives();
        boolean actual = archives.get(0).getFullName().equals("Bundesarchiv");

        assertTrue(actual);
    }

    @Test
    void updateArchive_updatesShortName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");

        manageArchivesUseCase.updateArchive("Bundesarchiv", "BArch", "Bundesarchiv", "BuArch");
        List<Archive> archives = manageArchivesUseCase.readArchives();
        boolean actual = archives.get(0).getShortName().equals("BuArch");

        assertTrue(actual);
    }

    @Test
    void updateArchive_returnsFalseForChangeToOtherExistingFullName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");
        manageArchivesUseCase.createArchive("Landesarchiv", "LAB");

        boolean actual = manageArchivesUseCase.updateArchive("Landesarchiv", "LAB", "Bundesarchiv", "LAB");

        assertFalse(actual);
    }

    @Test
    void updateArchive_returnsFalseForChangeToOtherExistingShortName() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        when(archiveRepository.save(any())).thenReturn(true);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");
        manageArchivesUseCase.createArchive("Landesarchiv", "LAB");

        boolean actual = manageArchivesUseCase.updateArchive("Landesarchiv", "LAB", "Landesarchiv", "BArch");

        assertFalse(actual);
    }

    @Test
    void readArchives_returnsList() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);
        manageArchivesUseCase.createArchive("Bundesarchiv", "BArch");

        Object archives = manageArchivesUseCase.readArchives();

        assertInstanceOf(List.class, archives);
    }

    @Test
    void readArchives_returnsEmptyList() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        List<Archive> archives = manageArchivesUseCase.readArchives();
        int actual = archives.size();
        int expected = 0;

        assertEquals(expected, actual);
    }

    @Test
    void readArchives_copyChangeDoesNotChangeOriginal() {
        ArchiveRepository archiveRepository = mock(ArchiveRepository.class);
        ManageArchivesUseCase manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        List<Archive> archives = manageArchivesUseCase.readArchives();

        assertThrows(
                UnsupportedOperationException.class,
                () -> archives.add(new Archive("OtherArchive", "OA"))
        );
    }

}
