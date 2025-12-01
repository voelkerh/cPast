package com.benskitchen.capturingthepast.persistence;

import android.content.Context;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.*;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class JsonArchiveStoreTest {

    @Before
    public void setUp() {

    }

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
        fail();
    }

    @Test
    public void loadArchives_handlesFileNotFoundException(){
        fail();
    }

    @Test
    public void loadArchives_handlesIOException(){
        fail();
    }

    @Test
    public void saveArchives_success() {
        fail();
    }

    @Test
    public void saveArchives_handlesNull() {
        fail();
    }

    @Test
    public void saveArchives_handlesIOException(){
        fail();
    }
}
