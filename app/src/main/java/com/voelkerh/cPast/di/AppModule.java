package com.voelkerh.cPast.di;

import android.content.Context;
import com.voelkerh.cPast.data.archives.ArchiveRepositoryImpl;
import com.voelkerh.cPast.data.images.ImageRepositoryImpl;
import com.voelkerh.cPast.data.images.MediaImageStore;
import com.voelkerh.cPast.data.notes.CsvNotesRepositoryImpl;
import com.voelkerh.cPast.data.recentCaptures.JsonRecentCapturesStore;
import com.voelkerh.cPast.data.recentCaptures.RecentCapturesRepositoryImpl;
import com.voelkerh.cPast.domain.repository.ArchiveRepository;
import com.voelkerh.cPast.domain.repository.ImageRepository;
import com.voelkerh.cPast.domain.repository.NotesRepository;
import com.voelkerh.cPast.domain.repository.RecentCapturesRepository;
import com.voelkerh.cPast.domain.usecase.ManageArchivesUseCase;
import com.voelkerh.cPast.domain.usecase.WriteNotesUseCase;

/**
 * Dependency injection container that provides app-wide dependencies.
 *
 * <p>This class serves as composition root. It provides the {@link ViewModelFactory} with all necessary repositories.
 * It ensures that a single instance of each repository is created and shared application-wide.
 * This is the only class that knows about concrete implementations from all layers.</p>
 */
public class AppModule {

    private static AppModule instance;

    private final ManageArchivesUseCase manageArchivesUseCase;
    private final ImageRepository imageRepository;
    private final WriteNotesUseCase writeNotesUseCase;
    private final RecentCapturesRepository recentCapturesRepository;

    private AppModule(Context context) {
        ArchiveRepository archiveRepository = new ArchiveRepositoryImpl(context);
        this.manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        MediaImageStore mediaImageStore = new MediaImageStore(context);
        this.imageRepository = new ImageRepositoryImpl(context, mediaImageStore);

        NotesRepository notesRepository = new CsvNotesRepositoryImpl(context);
        this.writeNotesUseCase = new WriteNotesUseCase(notesRepository);

        JsonRecentCapturesStore jsonRecentCapturesStore = new JsonRecentCapturesStore(context);
        this.recentCapturesRepository = new RecentCapturesRepositoryImpl(jsonRecentCapturesStore);
    }

    /**
     * Initializes the {@link AppModule} singleton.
     *
     * <p>This method is called once upon application startup by {@link com.voelkerh.cPast.CPastApplication#onCreate()}.
     * Only afterward the {@link AppModule} can be called via {@link #getInstance()}.</p>
     *
     * @param context application context
     */
    public static void init(Context context) {
        if (instance == null) {
            instance = new AppModule(context.getApplicationContext());
        }
    }

    /**
     * Returns the initialized {@link AppModule} singleton instance.
     *
     * @return initialized {@link AppModule}
     * @throws IllegalStateException if {@link #init(Context)} has not been called before.
     */
    public static AppModule getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Module not initialized");
        }
        return instance;
    }

    /**
     * Returns the application-wide {@link ManageArchivesUseCase}.
     *
     * <p>The returned instance is shared and provides access to archive-related operations.</p>
     *
     * @return application-wide {@link ManageArchivesUseCase}
     */
    public ManageArchivesUseCase getManageArchivesUseCase() {
        return manageArchivesUseCase;
    }

    /**
     * Returns the application-wide {@link ImageRepository}.
     *
     * <p>The returned instance is shared and provides access to operations related to photo captures.</p>
     *
     * @return application-wide {@link ImageRepository}
     */
    public ImageRepository getImageRepository() {
        return imageRepository;
    }

    /**
     * Returns the application-wide {@link WriteNotesUseCase}.
     *
     * <p>The returned instance is shared and provides access to operations related to the notes file.</p>
     *
     * @return application-wide {@link WriteNotesUseCase}
     */
    public WriteNotesUseCase getWriteNotesUseCase() {
        return writeNotesUseCase;
    }

    /**
     * Returns the application-wide {@link RecentCapturesRepository}.
     *
     * <p>The returned instance is shared and provides access to operations related to a list of recent captures.</p>
     *
     * @return application-wide {@link RecentCapturesRepository}
     */
    public RecentCapturesRepository getRecentCapturesRepository() {
        return recentCapturesRepository;
    }
}
