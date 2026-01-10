package com.voelkerh.cPast.data.archives;

import android.content.Context;
import com.voelkerh.cPast.domain.model.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@RunWith(RobolectricTestRunner.class)
public class ArchiveRepositoryImplTest {

    @Test
    public void archiveRepositoryImpl_constructorNotNull() {
        Context context = mock(Context.class);

        ArchiveRepositoryImpl archiveRepositoryImpl = new ArchiveRepositoryImpl(context);

        assertNotNull(archiveRepositoryImpl);
    }

    @Test
    public void archiveRepositoryImpl_testConstructorNotNull() {
        InputStream is = mock(InputStream.class);
        OutputStream os = mock(OutputStream.class);

        ArchiveRepositoryImpl archiveRepositoryImpl = new ArchiveRepositoryImpl(is, os);

        assertNotNull(archiveRepositoryImpl);
    }

    @Test
    public void load_success() {
        String json =
                "{\"archives\":[{\"fullName\":\"full\",\"shortName\":\"short\"}]}";
        InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ArchiveRepositoryImpl archiveRepositoryImpl = new ArchiveRepositoryImpl(is, os);

        List<Archive> result = archiveRepositoryImpl.load();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("full", result.get(0).getFullName());
        assertEquals("short", result.get(0).getShortName());
    }



    @Test
    public void load_handlesEmptyFile(){
        String json = "";
        InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ArchiveRepositoryImpl archiveRepositoryImpl = new ArchiveRepositoryImpl(is, os);

        Object result = archiveRepositoryImpl.load();

        assertNotNull(result);
    }


    @Test
    public void load_handlesFileNotFoundException() throws IOException {
        InputStream is = mock(InputStream.class);
        when(is.read(any(byte[].class))).thenThrow(new FileNotFoundException("File not found"));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ArchiveRepositoryImpl archiveRepositoryImpl = new ArchiveRepositoryImpl(is, os);

        List<Archive> result = archiveRepositoryImpl.load();

        assertNotNull(result);
    }

    @Test
    public void load_handlesIOException() throws IOException {
        InputStream is = mock(InputStream.class);
        when(is.read(any(byte[].class))).thenThrow(new IOException("Error reading"));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ArchiveRepositoryImpl archiveRepositoryImpl = new ArchiveRepositoryImpl(is, os);

        List<Archive> result = archiveRepositoryImpl.load();

        assertNotNull(result);
    }

    @Test
    public void save_success() {
        List<Archive> archives = new ArrayList<>();
        archives.add(new Archive("Bundesarchiv", "BArch"));
        archives.add(new Archive("Universitätsarchiv", "UAHU"));

        InputStream is = mock(InputStream.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ArchiveRepositoryImpl archiveRepositoryImpl = new ArchiveRepositoryImpl(is, os);

        boolean result = archiveRepositoryImpl.save(archives);

        assertTrue(result);
    }

    @Test
    public void save_handlesNull() {
        InputStream is = mock(InputStream.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ArchiveRepositoryImpl archiveRepositoryImpl = new ArchiveRepositoryImpl(is, os);

        boolean result = archiveRepositoryImpl.save(null);

        assertFalse(result);
    }

    @Test
    public void save_handlesNullStream() {
        List<Archive> archives = new ArrayList<>();
        archives.add(new Archive("Bundesarchiv", "BArch"));
        InputStream is = mock(InputStream.class);
        ArchiveRepositoryImpl archiveRepositoryImpl = new ArchiveRepositoryImpl(is, null);

        boolean result = archiveRepositoryImpl.save(archives);

        assertFalse(result);
    }

    @Test
    public void save_handlesIOException() throws IOException {
        List<Archive> archives = new ArrayList<>();
        archives.add(new Archive("Bundesarchiv", "Barch"));
        OutputStream os = mock(OutputStream.class);
        doThrow(new IOException("Error writing")).when(os).write(any(byte[].class));
        ArchiveRepositoryImpl archiveRepositoryImpl = new ArchiveRepositoryImpl(null, os);

        boolean result = archiveRepositoryImpl.save(archives);

        assertFalse(result);
    }

}