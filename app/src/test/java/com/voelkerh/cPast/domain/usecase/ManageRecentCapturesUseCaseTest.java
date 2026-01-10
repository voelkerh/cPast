package com.voelkerh.cPast.domain.usecase;

import com.voelkerh.cPast.domain.model.Archive;
import com.voelkerh.cPast.domain.model.Capture;
import com.voelkerh.cPast.domain.repository.RecentCapturesRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ManageRecentCapturesUseCaseTest {

    @Test
    void manageRecentCapturesUseCase_constructorNotNull() {
        RecentCapturesRepository recentCapturesRepository = mock(RecentCapturesRepository.class);

        ManageRecentCapturesUseCase manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);

        assertNotNull(manageRecentCapturesUseCase);
    }

    @Test
    void manageRecentCapturesUseCase_constructorSetsList() {
        RecentCapturesRepository recentCapturesRepository = mock(RecentCapturesRepository.class);
        when(recentCapturesRepository.load()).thenReturn(null);

        ManageRecentCapturesUseCase manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);
        List<Capture> actual = manageRecentCapturesUseCase.getRecentCaptures();

        assertNotNull(actual);
    }

    @Test
    void addFileToRecentCaptures_returnsTrue() {
        RecentCapturesRepository recentCapturesRepository = mock(RecentCapturesRepository.class);
        when(recentCapturesRepository.save(any())).thenReturn(true);
        ManageRecentCapturesUseCase manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);
        Capture capture = new Capture(mock(Archive.class), "file.jpg", "note");

        boolean actual = manageRecentCapturesUseCase.addFileToRecentCaptures(capture);

        verify(recentCapturesRepository, times(1)).save(any());
        assertTrue(actual);
    }

    @Test
    void addFileToRecentCaptures_returnsFalseWhenCaptureNull() {
        RecentCapturesRepository recentCapturesRepository = mock(RecentCapturesRepository.class);
        ManageRecentCapturesUseCase manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);
        Capture capture = null;

        boolean actual = manageRecentCapturesUseCase.addFileToRecentCaptures(capture);

        assertFalse(actual);
    }

    @Test
    void addFileToRecentCaptures_returnsFalseWhenFileNameIsNull() {
        RecentCapturesRepository recentCapturesRepository = mock(RecentCapturesRepository.class);
        ManageRecentCapturesUseCase manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);
        Capture capture = new Capture(mock(Archive.class), null, "note");

        boolean actual = manageRecentCapturesUseCase.addFileToRecentCaptures(capture);

        assertFalse(actual);
    }

    @Test
    void addFileToRecentCaptures_returnsFalseWhenFileNameIsEmpty() {
        RecentCapturesRepository recentCapturesRepository = mock(RecentCapturesRepository.class);
        ManageRecentCapturesUseCase manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);
        Capture capture = new Capture(mock(Archive.class), "", "note");

        boolean actual = manageRecentCapturesUseCase.addFileToRecentCaptures(capture);

        assertFalse(actual);
    }

    @Test
    void addFileToRecentCaptures_captureListLonger() {
        RecentCapturesRepository recentCapturesRepository = mock(RecentCapturesRepository.class);
        ManageRecentCapturesUseCase manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);
        Capture capture = new Capture(mock(Archive.class), "file.jpg", "note");

        manageRecentCapturesUseCase.addFileToRecentCaptures(capture);
        int expected = 1;
        int actual = manageRecentCapturesUseCase.getRecentCaptures().size();

        assertEquals(expected, actual);
    }

    @Test
    void addFileToRecentCaptures_captureNotLongerThanMax() {
        RecentCapturesRepository recentCapturesRepository = mock(RecentCapturesRepository.class);
        when(recentCapturesRepository.save(any())).thenReturn(true);
        ManageRecentCapturesUseCase manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);
        Capture capture1 = new Capture(mock(Archive.class), "file.jpg", "note");
        Capture capture2 = new Capture(mock(Archive.class), "file.jpg", "note");
        Capture capture3 = new Capture(mock(Archive.class), "file.jpg", "note");
        Capture capture4 = new Capture(mock(Archive.class), "file.jpg", "note");
        Capture capture5 = new Capture(mock(Archive.class), "file.jpg", "note");
        Capture capture6 = new Capture(mock(Archive.class), "file.jpg", "note");

        manageRecentCapturesUseCase.addFileToRecentCaptures(capture1);
        manageRecentCapturesUseCase.addFileToRecentCaptures(capture2);
        manageRecentCapturesUseCase.addFileToRecentCaptures(capture3);
        manageRecentCapturesUseCase.addFileToRecentCaptures(capture4);
        manageRecentCapturesUseCase.addFileToRecentCaptures(capture5);
        manageRecentCapturesUseCase.addFileToRecentCaptures(capture6);
        int expected = manageRecentCapturesUseCase.getMaxRecentFiles();
        int actual = manageRecentCapturesUseCase.getRecentCaptures().size();

        assertEquals(expected, actual);
    }

    @Test
    void getRecentCaptures_returnsList() {
        RecentCapturesRepository recentCapturesRepository = mock(RecentCapturesRepository.class);
        ManageRecentCapturesUseCase manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);
        Capture capture1 = new Capture(mock(Archive.class), "file.jpg", "note");
        Capture capture2 = new Capture(mock(Archive.class), "file.jpg", "note");
        manageRecentCapturesUseCase.addFileToRecentCaptures(capture1);
        manageRecentCapturesUseCase.addFileToRecentCaptures(capture2);

        List<Capture> recentCaptures = manageRecentCapturesUseCase.getRecentCaptures();
        int expected = 2;
        int actual = recentCaptures.size();

        assertEquals(expected, actual);
    }

    @Test
    void getRecentCaptures_returnsEmptyList() {
        RecentCapturesRepository recentCapturesRepository = mock(RecentCapturesRepository.class);
        ManageRecentCapturesUseCase manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);

        List<Capture> recentCaptures = manageRecentCapturesUseCase.getRecentCaptures();
        int expected = 0;
        int actual = recentCaptures.size();

        assertEquals(expected, actual);
    }

    @Test
    void getMaxRecentFiles_returnsMax() {
        RecentCapturesRepository recentCapturesRepository = mock(RecentCapturesRepository.class);
        ManageRecentCapturesUseCase manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);

        int max = manageRecentCapturesUseCase.getMaxRecentFiles();

        assertNotEquals(0, max);
    }
}
