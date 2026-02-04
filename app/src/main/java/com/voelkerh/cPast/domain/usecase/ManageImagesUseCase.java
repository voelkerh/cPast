package com.voelkerh.cPast.domain.usecase;

import com.voelkerh.cPast.domain.model.TempImageData;
import com.voelkerh.cPast.domain.repository.ImageRepository;

import java.util.Arrays;

/**
 * Use case that coordinates image capture and image persistence.
 *
 * <p>This class encapsulates application-level rules related to image handling.
 * This includes the creation of temporary image data for camera capture, validation of image metadata,
 * derivation of directory structures from file names, and delegation of persistence actions to the {@link ImageRepository}.</p>
 */
public class ManageImagesUseCase {

    private final ImageRepository imageRepository;

    /**
     * Creates a new use case instance.
     *
     * @param imageRepository repository responsible for image persistence and retrieval of temporary image data
     */
    public ManageImagesUseCase(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * Returns metadata for a temporary image used during camera capture.
     *
     * <p>The returned {@link TempImageData} provides the {@code Uri} and file path
     * required to launch the camera application and store the captured image temporarily before final persistence.</p>
     *
     * @return temporary image metadata for camera capture
     */
    public TempImageData getTempImageData() {
        return imageRepository.getTempImageData();
    }

    /**
     * Persists a captured image to permanent storage.
     *
     * <p>This method implicitly derives a directory structure from the image file name.
     * It delegates the actual persistence operation to the {@link ImageRepository}.</p>
     *
     * @param imageFileName name of the final image file
     * @param note optional user-provided note associated with the image
     * @param currentPhotoPath absolute path to the temporary image file
     * @return boolean if the image was successfully persisted
     */
    public boolean saveImageToGallery(String imageFileName, String note, String currentPhotoPath) {
        if (imageFileName == null || imageFileName.isEmpty()) return false;
        if (currentPhotoPath == null || currentPhotoPath.isEmpty()) return false;
        if (note == null) return false;


        String[] directoryNames = deriveDirectoryStructure(imageFileName);
        return imageRepository.save(imageFileName, note, currentPhotoPath, directoryNames);
    }

    private String[] deriveDirectoryStructure(String fileName) {
        if (fileName.endsWith(".jpg")) {
            String baseName = fileName.substring(0, fileName.length() - 4);
            String[] parts = baseName.split("_+");
            return Arrays.copyOf(parts, parts.length - 1);
        } else {
            return fileName.split("_+");
        }
    }

    /**
     * Determines the highest numeric counter used for images belonging to a record.
     *
     * <p>The record is identified by a base reference from which the directory structure is derived.
     * The counter is extracted from image file names following the pattern {@code *_<number>.jpg}.</p>
     *
     * @param baseReference base identifier used to derive the record directory
     * @return the highest numeric counter found, or {@code 0} if none exist
     */
    public int getHighestCounterForRecord(String baseReference) throws NumberFormatException {
        if (baseReference == null) return 0;

        String[] directoryNames = deriveDirectoryStructure(baseReference);
        return imageRepository.getHighestCounterForRecord(directoryNames);
    }
}
