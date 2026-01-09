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

    public HomeViewModel(ManageArchivesUseCase manageArchivesUseCase, ManageImagesUseCase manageImagesUseCase, WriteNotesUseCase writeNotesUseCase, ManageRecentCapturesUseCase manageRecentCapturesUseCase) {
        this.manageArchivesUseCase = manageArchivesUseCase;
        this.manageImagesUseCase = manageImagesUseCase;
        this.writeNotesUseCase = writeNotesUseCase;
        this.manageRecentCapturesUseCase = manageRecentCapturesUseCase;
        loadArchives();
    }

    public LiveData<List<Archive>> getArchives() {
        return archives;
    }

    public LiveData<Integer> getCurrentCounter() {
        return currentCounter;
    }

    public LiveData<String> getNextFileName() {
        return nextFileName;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public void onArchiveSelected(Archive archive) {
        this.selectedArchive = archive;
        updateCounterAndFileName();
    }

    public void onRecordReferenceChanged(String recordReference) {
        this.currentRecordReference = recordReference;
        updateCounterAndFileName();
    }

    public boolean isInputValid(String recordReference) {
        if (selectedArchive == null || selectedArchive.getShortName().isEmpty()) return false;
        if (recordReference.isEmpty()) return false;
        return recordReference.matches("^(?=.*[A-Za-zäöüÄÖÜ])[A-Za-z0-9äöüÄÖÜ_/]+$");
    }

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

    public void createArchive(String fullName, String shortName) {
        boolean created = manageArchivesUseCase.createArchive(fullName, shortName);
        if (created) {
            successMessage.setValue(fullName + " created");
            loadArchives();
        }
        else errorMessage.setValue(fullName + " could not be created");
    }

    public void updateArchive(String oldFullName, String oldShortName, String fullArchiveName, String shortArchiveName) {
        manageArchivesUseCase.updateArchive(oldFullName, oldShortName, fullArchiveName, shortArchiveName);
        successMessage.setValue(fullArchiveName + " updated");
        loadArchives();
    }

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

    public TempImageData prepareCameraCapture() {
        TempImageData tempData = manageImagesUseCase.getTempImageData();
        if (tempData == null) {
            errorMessage.setValue("No photoURI to capture image");
            return null;
        }
        return tempData;
    }
}
