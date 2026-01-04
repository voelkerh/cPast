package com.voelkerh.cPast.di;

import android.content.Context;
import com.voelkerh.cPast.data.archives.ArchiveRepositoryImpl;
import com.voelkerh.cPast.data.archives.JsonArchiveStore;
import com.voelkerh.cPast.data.images.ImageRepositoryImpl;
import com.voelkerh.cPast.data.images.MediaImageStore;
import com.voelkerh.cPast.data.notes.CsvNotesStore;
import com.voelkerh.cPast.data.notes.NotesRepositoryImpl;
import com.voelkerh.cPast.data.notes.NotesStore;
import com.voelkerh.cPast.data.recentCaptures.JsonRecentCapturesStore;
import com.voelkerh.cPast.data.recentCaptures.RecentCapturesRepositoryImpl;
import com.voelkerh.cPast.domain.repository.ArchiveRepository;
import com.voelkerh.cPast.domain.repository.ImageRepository;
import com.voelkerh.cPast.domain.repository.NotesRepository;
import com.voelkerh.cPast.domain.repository.RecentCapturesRepository;

/**
 * Dependency injection container that provides app-wide dependencies.
 * This is the only class that knows about concrete implementations from all layers.
 */
public class AppModule {

    private static AppModule instance;

    private final ArchiveRepository archiveRepository;
    private final ImageRepository imageRepository;
    private final NotesRepository notesRepository;
    private final RecentCapturesRepository recentCapturesRepository;

    private AppModule(Context context) {
        JsonArchiveStore jsonArchiveStore = new JsonArchiveStore(context);
        this.archiveRepository = new ArchiveRepositoryImpl(jsonArchiveStore);

        MediaImageStore mediaImageStore = new MediaImageStore(context);
        this.imageRepository = new ImageRepositoryImpl(context, mediaImageStore);

        NotesStore notesStore = new CsvNotesStore(context);
        this.notesRepository = new NotesRepositoryImpl(notesStore);

        JsonRecentCapturesStore jsonRecentCapturesStore = new JsonRecentCapturesStore(context);
        this.recentCapturesRepository = new RecentCapturesRepositoryImpl(jsonRecentCapturesStore);
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new AppModule(context.getApplicationContext());
        }
    }

    public static AppModule getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Module not initialized");
        }
        return instance;
    }

    public ArchiveRepository getArchiveRepository() {
        return archiveRepository;
    }

    public ImageRepository getImageRepository() {
        return imageRepository;
    }

    public NotesRepository getNotesRepository() {
        return notesRepository;
    }

    public RecentCapturesRepository getRecentCapturesRepository() {
        return recentCapturesRepository;
    }
}
