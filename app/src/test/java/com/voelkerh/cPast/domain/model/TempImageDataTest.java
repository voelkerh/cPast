package com.voelkerh.cPast.domain.model;

import android.net.Uri;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class TempImageDataTest {

    @Test
    void tempImageData_constructorNotNull() {
        Uri uri = mock(Uri.class);

        TempImageData tempImageData = new TempImageData(uri, "filepath");

        assertNotNull(tempImageData);
    }

    @Test
    void getUri_returnsUri() {
        Uri expected = mock(Uri.class);
        TempImageData tempImageData = new TempImageData(expected, "filepath");

        Uri actual = tempImageData.getUri();

        assertEquals(expected, actual);
    }

    @Test
    void getPath_returnsPath() {
        Uri uri = mock(Uri.class);
        String expected = "filepath";
        TempImageData tempImageData = new TempImageData(uri, expected);

        String actual = tempImageData.getPath();

        assertEquals(expected, actual);
    }
}
