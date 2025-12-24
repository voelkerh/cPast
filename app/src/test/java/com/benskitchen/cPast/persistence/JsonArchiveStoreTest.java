package com.benskitchen.cPast.persistence;

import android.content.Context;
import com.benskitchen.cPast.domainLogic.Archive;
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
public class JsonArchiveStoreTest {

    @Test
    public void jsonArchiveStore_constructorNotNull() {
        Context context = mock(Context.class);
        JsonArchiveStore jsonArchiveStore = new JsonArchiveStore(context);
        assertNotNull(jsonArchiveStore);
    }

    @Test
    public void loadArchives_success() {
        String json =
                "{\"archives\":[{\"fullName\":\"full\",\"shortName\":\"short\"}]}";
        InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(is, os);

        List<Archive> result = store.loadArchives();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("full", result.get(0).getFullName());
        assertEquals("short", result.get(0).getShortName());
    }



    @Test
    public void loadArchives_handlesEmptyFile(){
        String json = "";
        InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(is, os);

        Object result = store.loadArchives();

        assertNotNull(result);
        assertTrue(result instanceof List);
    }

    @Test
    public void loadArchives_handlesFileNotFoundException() throws IOException {
        InputStream mockStream = mock(InputStream.class);
        when(mockStream.read(any(byte[].class))).thenThrow(new FileNotFoundException("File not found"));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(mockStream, os);

        Object result = store.loadArchives();

        assertNotNull(result);
        assertTrue(result instanceof List);
    }

    @Test
    public void loadArchives_handlesIOException() throws IOException {
        InputStream mockStream = mock(InputStream.class);
        when(mockStream.read(any(byte[].class))).thenThrow(new IOException("Error reading"));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(mockStream, os);

        Object result = store.loadArchives();

        assertNotNull(result);
        assertTrue(result instanceof List);
    }

    @Test
    public void saveArchives_success() {
        List<Archive> archives = new ArrayList<>();
        Archive barch = new Archive("Bundesarchiv", "Barch");
        Archive uahu = new Archive("Universitätsarchiv", "UAHU");
        archives.add(barch);
        archives.add(uahu);

        InputStream mockStream = mock(InputStream.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(mockStream, os);

        boolean result = store.saveArchives(archives);

        assertTrue(result);
    }

    @Test
    public void saveArchives_handlesNull() {
        InputStream mockStream = mock(InputStream.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(mockStream, os);

        boolean result = store.saveArchives(null);

        assertFalse(result);
    }

    @Test
    public void saveArchives_handlesNullStream() {
        List<Archive> archives = new ArrayList<>();
        Archive barch = new Archive("Bundesarchiv", "Barch");
        archives.add(barch);
        InputStream mockStream = mock(InputStream.class);
        JsonArchiveStore store = new JsonArchiveStore(mockStream, null);

        boolean result = store.saveArchives(archives);

        assertFalse(result);
    }

    @Test
    public void saveArchives_handlesIOException() throws IOException {
        List<Archive> archives = new ArrayList<>();
        Archive barch = new Archive("Bundesarchiv", "Barch");
        archives.add(barch);

        OutputStream os = mock(OutputStream.class);
        doThrow(new IOException("Error writing")).when(os).write(any(byte[].class));

        JsonArchiveStore store = new JsonArchiveStore(null, os);

        boolean result = store.saveArchives(archives);

        assertFalse(result);
    }
}
