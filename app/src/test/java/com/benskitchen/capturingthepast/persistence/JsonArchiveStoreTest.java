package com.benskitchen.capturingthepast.persistence;

import android.content.Context;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
        String json = "{\"archives\":{\"key\":\"value\"}}";
        InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(is, os);

        Map<String, String> result = store.loadArchives();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("value", result.get("key"));
    }


    @Test
    public void loadArchives_handlesEmptyFile(){
        String json = "";
        InputStream is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(is, os);

        Object result = store.loadArchives();

        assertNotNull(result);
        assertTrue(result instanceof Map);
    }

    @Test
    public void loadArchives_handlesFileNotFoundException() throws IOException {
        InputStream mockStream = mock(InputStream.class);
        when(mockStream.read(any(byte[].class))).thenThrow(new FileNotFoundException("File not found"));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(mockStream, os);

        Object result = store.loadArchives();

        assertNotNull(result);
        assertTrue(result instanceof Map);
    }

    @Test
    public void loadArchives_handlesIOException() throws IOException {
        InputStream mockStream = mock(InputStream.class);
        when(mockStream.read(any(byte[].class))).thenThrow(new IOException("Error reading"));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(mockStream, os);

        Object result = store.loadArchives();

        assertNotNull(result);
        assertTrue(result instanceof Map);
    }

    @Test
    public void saveArchives_success() {
        Map<String, String> archives = new HashMap<>();
        archives.put("Bundesarchiv", "BArch");
        archives.put("Universitätsarchiv", "UAHU");

        InputStream mockStream = mock(InputStream.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(mockStream, os);

        boolean result = store.saveArchives(archives);

        assertTrue(result);
    }

    @Test
    public void saveArchives_handlesNull() {
        Map<String, String> archives = null;
        InputStream mockStream = mock(InputStream.class);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        JsonArchiveStore store = new JsonArchiveStore(mockStream, os);

        boolean result = store.saveArchives(archives);

        assertFalse(result);
    }

    @Test
    public void saveArchives_handlesNullStream() {
        Map<String, String> archives = new HashMap<>();
        archives.put("Bundesarchiv", "BArch");
        InputStream mockStream = mock(InputStream.class);
        ByteArrayOutputStream os = null;
        JsonArchiveStore store = new JsonArchiveStore(mockStream, os);

        boolean result = store.saveArchives(archives);

        assertFalse(result);
    }

    @Test
    public void saveArchives_handlesIOException() throws IOException {
        Map<String, String> archives = new HashMap<>();
        archives.put("Bundesarchiv", "BArch");

        OutputStream os = mock(OutputStream.class);
        doThrow(new IOException("Error writing")).when(os).write(any(byte[].class));

        JsonArchiveStore store = new JsonArchiveStore(null, os);

        boolean result = store.saveArchives(archives);

        assertFalse(result);
    }
}
