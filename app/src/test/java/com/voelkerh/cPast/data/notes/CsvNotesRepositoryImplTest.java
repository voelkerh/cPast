package com.voelkerh.cPast.data.notes;

import android.content.Context;
import android.net.Uri;
import com.voelkerh.cPast.domain.model.Archive;
import com.voelkerh.cPast.domain.model.Capture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class CsvNotesRepositoryImplTest {

    @Test
    public void csvNotesRepositoryImpl_constructorNotNull() {
        Context context = mock(Context.class);

        CsvNotesRepositoryImpl csvNotesRepositoryImpl = new CsvNotesRepositoryImpl(context);

        assertNotNull(csvNotesRepositoryImpl);
    }

    @Test
    public void save_returnsTrueForValidCapture() {
        Context context = RuntimeEnvironment.getApplication();
        CsvNotesRepositoryImpl csvNotesRepositoryImpl = new CsvNotesRepositoryImpl(context);
        Capture capture = new Capture(new Archive("Bundesarchiv", "BArch"), "file.jpg", "note");

        boolean actual = csvNotesRepositoryImpl.save(capture);

        assertTrue(actual);
    }

    @Test
    public void save_returnsFalseWhenFileCannotBeCreated() {
        Context context = RuntimeEnvironment.getApplication();
        CsvNotesRepositoryImpl csvNotesRepository = spy(new CsvNotesRepositoryImpl(context));
        doReturn(null).when(csvNotesRepository).createNewFile();
        Capture capture = new Capture(new Archive("Bundesarchiv", "BArch"), "file.jpg", "note");

        boolean actual = csvNotesRepository.save(capture);

        assertFalse(actual);
    }

    @Test
    public void save_returnsFalseWhenAppendFails() {
        Context context = RuntimeEnvironment.getApplication();
        CsvNotesRepositoryImpl csvNotesRepository = spy(new CsvNotesRepositoryImpl(context));
        doReturn(Uri.parse("content://fake")).when(csvNotesRepository).findExistingFile();
        doReturn(false).when(csvNotesRepository).appendToFile(any(), any());
        Capture capture = new Capture(new Archive("Bundesarchiv", "BArch"), "file.jpg", "note");

        boolean actual = csvNotesRepository.save(capture);

        assertFalse(actual);
    }
}
