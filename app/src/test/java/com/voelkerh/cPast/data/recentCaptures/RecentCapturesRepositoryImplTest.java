package com.voelkerh.cPast.data.recentCaptures;

import android.content.Context;
import com.voelkerh.cPast.domain.model.Archive;
import com.voelkerh.cPast.domain.model.Capture;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
public class RecentCapturesRepositoryImplTest {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
    }

    @Test
    public void recentCapturesRepositoryImpl_constructorNotNull() {
        Context context = mock(Context.class);

        RecentCapturesRepositoryImpl recentCapturesRepository = new RecentCapturesRepositoryImpl(context);

        assertNotNull(recentCapturesRepository);
    }

    @Test
    public void load_handlesEmptyFile(){
        RecentCapturesRepositoryImpl recentCapturesRepository = new RecentCapturesRepositoryImpl(context);

        List<Capture> result = recentCapturesRepository.load();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void save_thenLoad_returnsSavedCaptures() {
        RecentCapturesRepositoryImpl recentCapturesRepository = new RecentCapturesRepositoryImpl(context);

        List<Capture> captures = new ArrayList<>();
        captures.add(new Capture(new Archive("Bundesarchiv", "BArch"), "file1.jpg", "note1"));
        captures.add(new Capture(new Archive("Universitätsarchiv", "UAHU"), "file2.jpg", "note2"));

        recentCapturesRepository.save(captures);
        List<Capture> loaded = recentCapturesRepository.load();

        assertEquals(2, loaded.size());
        assertEquals("file1.jpg", loaded.get(0).getFileName());
        assertEquals("Bundesarchiv", loaded.get(0).getArchive().getFullName());
    }

    @Test
    public void save_success() {
        List<Capture> captures = new ArrayList<>();
        captures.add(new Capture(new Archive("Bundesarchiv", "BArch"), "file1.jpg", "note"));
        captures.add(new Capture(new Archive("Universitätsarchiv", "UAHU"), "file2.jpg", "note"));
        RecentCapturesRepositoryImpl recentCapturesRepository = new RecentCapturesRepositoryImpl(context);

        boolean result = recentCapturesRepository.save(captures);

        assertTrue(result);
    }

    @Test
    public void save_handlesNullList() {
        RecentCapturesRepositoryImpl recentCapturesRepository = new RecentCapturesRepositoryImpl(context);

        boolean result = recentCapturesRepository.save(null);

        assertFalse(result);
    }

    @Test
    public void save_handlesEmptyList() {
        RecentCapturesRepositoryImpl recentCapturesRepository = new RecentCapturesRepositoryImpl(context);
        List<Capture> captures = new ArrayList<>();

        boolean result = recentCapturesRepository.save(captures);

        assertFalse(result);
    }

}
