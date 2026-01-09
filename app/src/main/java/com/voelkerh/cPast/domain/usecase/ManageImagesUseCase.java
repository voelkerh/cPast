package com.voelkerh.cPast.domain.usecase;

import com.voelkerh.cPast.domain.model.TempImageData;
import com.voelkerh.cPast.domain.repository.ImageRepository;

import java.util.Arrays;

public class ManageImagesUseCase {

    private final ImageRepository imageRepository;

    public ManageImagesUseCase(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public TempImageData getTempImageData() {
        return imageRepository.getTempImageData();
    }

    public boolean saveImageToGallery(String imageFileName, String note, String currentPhotoPath) {
        if (imageFileName == null || imageFileName.isEmpty()) return false;
        if (currentPhotoPath == null || currentPhotoPath.isEmpty()) return false;
        if (note == null || note.isEmpty()) return false;


        String[] directoryNames = deriveDirectoryStructure(imageFileName);
        return imageRepository.save(imageFileName, note, currentPhotoPath, directoryNames);
    }

    private String[] deriveDirectoryStructure(String fileName) {
        if (fileName.endsWith(".jpg")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        String[] parts = fileName.split("_+");
        return Arrays.copyOf(parts, parts.length);
    }

    public int getHighestCounterForRecord(String baseReference) {
        if (baseReference == null) return 0;

        String[] directoryNames = deriveDirectoryStructure(baseReference);
        return imageRepository.getHighestCounterForRecord(directoryNames);
    }
}
