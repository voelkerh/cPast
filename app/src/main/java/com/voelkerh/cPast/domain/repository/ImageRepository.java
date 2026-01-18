package com.voelkerh.cPast.domain.repository;

import com.voelkerh.cPast.domain.model.TempImageData;

/**
 * Repository abstraction for creating {@link TempImageData} and saving photos.
 *
 * <p>This interface defines the contract for creating temporary image data for the camera app and saving images,
 * as well as retrieving the highest counter for a given record.
 * Concrete implementations are provided by the data layer.</p>
 */
public interface ImageRepository {

    /**
     * Persists an image and associated metadata.
     *
     * @param imageFileName name of the final image file
     * @param note user-provided note stored in the image metadata
     * @param currentPhotoPath absolute path to the temporary image file
     * @param directoryNames directory structure under which the image is stored
     * @return boolean if the image was successfully written and indexed,
     */
    boolean save(String imageFileName, String note, String currentPhotoPath, String[] directoryNames);

    /**
     * Creates and returns metadata for a temporary image file used during capture.
     *
     * <p>The returned {@link TempImageData} contains a {@link android.net.Uri} for the camera application.
     * It also contains the absolute file path where the temporary image will be written.</p>
     *
     * @return temporary image metadata used during image capture
     */
    TempImageData getTempImageData();

    /**
     * Determines the highest numeric counter used in image filenames within a directory.
     *
     * <p>The counter is extracted from filenames following the pattern
     * {@code *_<number>.jpg}. If no matching files are found, {@code 0} is returned.</p>
     *
     * @param directoryNames directory path segments identifying the record
     * @return the highest numeric counter found, or {@code 0} if none exist
     * @throws NumberFormatException when counter exceeds Integer.MAX_VALUE
     */
    int getHighestCounterForRecord(String[] directoryNames) throws NumberFormatException;

}