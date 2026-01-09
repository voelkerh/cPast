package com.voelkerh.cPast.di;

import android.content.Context;
import com.voelkerh.cPast.data.archives.ArchiveRepositoryImpl;
import com.voelkerh.cPast.data.images.ImageRepositoryImpl;
import com.voelkerh.cPast.data.notes.CsvNotesRepositoryImpl;
import com.voelkerh.cPast.data.recentCaptures.RecentCapturesRepositoryImpl;
import com.voelkerh.cPast.domain.repository.ArchiveRepository;
import com.voelkerh.cPast.domain.repository.ImageRepository;
import com.voelkerh.cPast.domain.repository.NotesRepository;
import com.voelkerh.cPast.domain.repository.RecentCapturesRepository;
import com.voelkerh.cPast.domain.usecase.ManageArchivesUseCase;
import com.voelkerh.cPast.domain.usecase.ManageImagesUseCase;
import com.voelkerh.cPast.domain.usecase.ManageRecentCapturesUseCase;
import com.voelkerh.cPast.domain.usecase.WriteNotesUseCase;

/**
 * Dependency injection container that provides app-wide dependencies.
 *
 * <p>This class serves as the composition root. It provides the {@link ViewModelFactory} with all necessary use cases.
 * It ensures that a single instance of each use case is created and shared application-wide.
 * This is the only class that knows about concrete implementations from all layers.</p>
 */
public class AppModule {

    private static AppModule instance;

    private final ManageArchivesUseCase manageArchivesUseCase;
    private final ManageImagesUseCase manageImagesUseCase;
    private final WriteNotesUseCase writeNotesUseCase;
    private final ManageRecentCapturesUseCase manageRecentCapturesUseCase;

    private AppModule(Context context) {
        ArchiveRepository archiveRepository = new ArchiveRepositoryImpl(context);
        this.manageArchivesUseCase = new ManageArchivesUseCase(archiveRepository);

        ImageRepository imageRepository = new ImageRepositoryImpl(context);
        this.manageImagesUseCase = new ManageImagesUseCase(imageRepository);

        NotesRepository notesRepository = new CsvNotesRepositoryImpl(context);
        this.writeNotesUseCase = new WriteNotesUseCase(notesRepository);

        RecentCapturesRepository recentCapturesRepository = new RecentCapturesRepositoryImpl(context);
        this.manageRecentCapturesUseCase = new ManageRecentCapturesUseCase(recentCapturesRepository);
    }

    /**
     * Initializes the {@link AppModule} singleton.
     *
     * <p>This method is called once upon application startup by {@link com.voelkerh.cPast.CPastApplication#onCreate()}.
     * After initialization, the module can be called via {@link #getInstance()}.</p>
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
     * Returns the application-wide {@link ManageImagesUseCase}.
     *
     * <p>The returned instance is shared and provides access to operations related to photo captures.</p>
     *
     * @return application-wide {@link ManageImagesUseCase}
     */
    public ManageImagesUseCase getManageImagesUseCase() {
        return manageImagesUseCase;
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
     * Returns the application-wide {@link ManageRecentCapturesUseCase}.
     *
     * <p>The returned instance is shared and provides access to operations related to a list of recent captures.</p>
     *
     * @return application-wide {@link ManageRecentCapturesUseCase}
     */
    public ManageRecentCapturesUseCase getManageRecentCapturesUseCase() {
        return manageRecentCapturesUseCase;
    }
}
