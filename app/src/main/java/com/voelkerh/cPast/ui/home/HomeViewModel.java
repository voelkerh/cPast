package com.voelkerh.cPast.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.voelkerh.cPast.domain.model.Archive;
import com.voelkerh.cPast.domain.model.Capture;
import com.voelkerh.cPast.domain.model.TempImageData;
import com.voelkerh.cPast.domain.service.RecordReferenceCreator;
import com.voelkerh.cPast.domain.usecase.ManageArchivesUseCase;
import com.voelkerh.cPast.domain.usecase.ManageImagesUseCase;
import com.voelkerh.cPast.domain.usecase.ManageRecentCapturesUseCase;
import com.voelkerh.cPast.domain.usecase.WriteNotesUseCase;

import java.util.List;

/**
 * ViewModel for the Home screen.
 *
 * <p>This ViewModel coordinates all user interactions on the Home screen and translates UI events into domain-level operations.</p>
 *
 * <p>Responsibilities include:
 * <ul>
 *   <li>Managing archive selection and record references</li>
 *   <li>Generating preview file names</li>
 *   <li>Preparing camera capture and persisting captured images</li>
 *   <li>Storing notes and recent captures</li>
 *   <li>Exposing UI state via {@link LiveData}</li>
 * </ul>
 * </p>
 */
public class HomeViewModel extends ViewModel {

    private final ManageArchivesUseCase manageArchivesUseCase;
    private final WriteNotesUseCase writeNotesUseCase;
    private final ManageImagesUseCase manageImagesUseCase;
    private final ManageRecentCapturesUseCase manageRecentCapturesUseCase;

    private final MutableLiveData<List<Archive>> archives = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentCounter = new MutableLiveData<>(0);
    private final MutableLiveData<String> nextFileName = new MutableLiveData<>("");
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> successMessage = new MutableLiveData<>();

    private Archive selectedArchive;
    private String currentRecordReference = "";

    /**
     * Creates the HomeViewModel and initializes its state using the provided use cases.
     *
     * <p>The ViewModel receives all required domain use cases via constructor injection
     * and immediately loads the available archives to initialize the UI state.</p>
     *
     * @param manageArchivesUseCase use case for creating, updating and reading archives
     * @param manageImagesUseCase use case for preparing camera captures and saving images
     * @param writeNotesUseCase use case for persisting notes associated with captures
     * @param manageRecentCapturesUseCase use case for maintaining the list of recent captures
     */
    public HomeViewModel(
            ManageArchivesUseCase manageArchivesUseCase,
            ManageImagesUseCase manageImagesUseCase,
            WriteNotesUseCase writeNotesUseCase,
            ManageRecentCapturesUseCase manageRecentCapturesUseCase
    ) {
        this.manageArchivesUseCase = manageArchivesUseCase;
        this.manageImagesUseCase = manageImagesUseCase;
        this.writeNotesUseCase = writeNotesUseCase;
        this.manageRecentCapturesUseCase = manageRecentCapturesUseCase;
        loadArchives();
    }

    /**
     * @return list of available archives for selection
     */
    public LiveData<List<Archive>> getArchives() {
        return archives;
    }

    /**
     * @return next suggested image file name
     */
    public LiveData<String> getNextFileName() {
        return nextFileName;
    }

    /**
     * @return error messages intended for user feedback
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * @return success messages intended for user feedback
     */
    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    /**
     * Handles archive selection changes from the UI.
     */
    public void onArchiveSelected(Archive archive) {
        this.selectedArchive = archive;
        updateCounterAndFileName();
    }

    /**
     * Handles changes to the record reference input.
     */
    public void onRecordReferenceChanged(String recordReference) {
        this.currentRecordReference = recordReference;
        updateCounterAndFileName();
    }

    /**
     * Validates user input before starting the camera.
     *
     * @param recordReference user-provided record reference
     * @return boolean if input is syntactically valid
     */
    public boolean isInputValid(String recordReference) {
        if (selectedArchive == null || selectedArchive.getShortName().isEmpty()) return false;
        if (recordReference.isEmpty()) return false;
        return recordReference.matches("^(?=.*[A-Za-zäöüÄÖÜ])[A-Za-z0-9äöüÄÖÜ_/]+$");
    }

    /**
     * Persists a captured image and associated note.
     *
     * <p>This method:
     * <ol>
     *   <li>Generates the final image file name</li>
     *   <li>Saves the image to the MediaStore via {@link ManageImagesUseCase}</li>
     *   <li>Updates counters and preview names</li>
     *   <li>Stores recent captures via {@link ManageRecentCapturesUseCase}</li>
     *   <li>Writes notes to output file via {@link WriteNotesUseCase}</li>
     * </ol>
     *
     * @param tempImagePath path to the temporary image file
     * @param note user-provided note
     */
    public void saveCapturedImage(String tempImagePath, String note) {
        String baseReference = getBaseReference();
        int counter = currentCounter.getValue() != null ? currentCounter.getValue() : 0;
        String imageFileName = createFileName(baseReference, String.valueOf(counter + 1));

        try {
            boolean saved = manageImagesUseCase.saveImageToGallery(imageFileName, note, tempImagePath);
            if (saved) {
                currentCounter.setValue(counter + 1);

                String newFileName = createFileName(baseReference, String.valueOf(counter + 2));
                nextFileName.setValue(newFileName);

                Capture capture = new Capture(selectedArchive, imageFileName, note);
                manageRecentCapturesUseCase.addFileToRecentCaptures(capture);

                boolean noteSaved = writeNotesUseCase.saveNote(capture);

                if (noteSaved) {
                    successMessage.setValue("Note and image saved\n " + imageFileName);
                } else {
                    successMessage.setValue("Image saved to " + imageFileName + "\nNote could not be saved");
                }
            }
            else errorMessage.setValue("Image could not be saved.");
        } catch (Exception e) {
            errorMessage.setValue("Error: Image not saved.\n" + e.getMessage());
        }
    }

    /**
     * Creates a new archive and refreshes the archive list.
     */
    public void createArchive(String fullName, String shortName) {
        boolean created = manageArchivesUseCase.createArchive(fullName, shortName);
        if (created) {
            successMessage.setValue(fullName + " created");
            loadArchives();
        }
        else errorMessage.setValue(fullName + " could not be created");
    }

    /**
     * Updates an existing archive and refreshes the archive list.
     */
    public void updateArchive(String oldFullName, String oldShortName, String fullArchiveName, String shortArchiveName) {
        manageArchivesUseCase.updateArchive(oldFullName, oldShortName, fullArchiveName, shortArchiveName);
        successMessage.setValue(fullArchiveName + " updated");
        loadArchives();
    }

    /**
     * Deletes an archive and refreshes the archive list.
     */
    public void deleteArchive(String fullArchiveName) {
        manageArchivesUseCase.deleteArchive(fullArchiveName);
        successMessage.setValue("Deleted -" + fullArchiveName);
        loadArchives();
    }

    public void refreshCounter() {
        updateCounterAndFileName();
    }

    private void loadArchives() {
        List<Archive> archiveList = manageArchivesUseCase.readArchives();
        archives.setValue(archiveList);
    }

    private void updateCounterAndFileName() {
        String baseReference = getBaseReference();

        if (baseReference.isEmpty()) {
            currentCounter.setValue(0);
            nextFileName.setValue("");
            return;
        }

        int counter = manageImagesUseCase.getHighestCounterForRecord(baseReference);
        currentCounter.setValue(counter);

        String fileName = createFileName(baseReference, String.valueOf(counter + 1));
        nextFileName.setValue(fileName);

        if (fileName.length() > 128) {
            errorMessage.setValue("Your catalogue reference is very long and may result in unusable file names.");
        }
    }

    private String getBaseReference() {
        String shortArchiveName = selectedArchive != null ? selectedArchive.getShortName() : "";
        return RecordReferenceCreator.createBaseReference(shortArchiveName, currentRecordReference);
    }

    private String createFileName(String baseReference, String counter) {
        return RecordReferenceCreator.addCounterAndFileExtension(baseReference, counter);
    }

    /**
     * Prepares the camera capture by creating temporary image data.
     *
     * @return metadata required for launching the camera app, or null on error
     */
    public TempImageData prepareCameraCapture() {
        TempImageData tempData = manageImagesUseCase.getTempImageData();
        if (tempData == null) {
            errorMessage.setValue("No photoURI to capture image");
            return null;
        }
        return tempData;
    }
}
