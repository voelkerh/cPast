package com.voelkerh.cPast.domain;

import com.voelkerh.cPast.domain.model.Archive;
import com.voelkerh.cPast.domain.model.Capture;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class CaptureTest {

    @Test
    void capture_constructorNotNull() {
        Archive archive = mock(Archive.class);

        Capture capture = new Capture(archive, "filename.jpg", "note");

        assertNotNull(capture);
    }

    @Test
    void getFileName_returnsFilename() {
        Archive archive = mock(Archive.class);
        String expected = "filename.jpg";
        Capture capture = new Capture(archive, expected, "note");

        String actual = capture.getFileName();

        assertEquals(expected, actual);
    }

    @Test
    void getNote_returnsNote() {
        Archive archive = mock(Archive.class);
        String expected = "note";
        Capture capture = new Capture(archive, "filename.jpg", expected);

        String actual = capture.getNote();

        assertEquals(expected, actual);
    }

    @Test
    void getNote_returnsDefaultOnEmptyInput() {
        Archive archive = mock(Archive.class);
        Capture capture = new Capture(archive, "filename.jpg", "");

        String expected = "No note taken.";
        String actual = capture.getNote();

        assertEquals(expected, actual);
    }

    @Test
    void getCaptureTime_returnsCaptureTime() {
        Archive archive = mock(Archive.class);
        Capture capture = new Capture(archive, "filename.jpg", "note");

        LocalDateTime expected = LocalDateTime.now();
        LocalDateTime actual = capture.getCaptureTime();

        assertEquals(expected, actual);
    }

    @Test
    void getArchive_returnsArchive() {
        Archive expected = mock(Archive.class);
        Capture capture = new Capture(expected, "filename.jpg", "note");

        Archive actual = capture.getArchive();

        assertEquals(expected, actual);
    }
}
