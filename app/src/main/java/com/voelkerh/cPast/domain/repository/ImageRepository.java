package com.voelkerh.cPast.domain.repository;

import com.voelkerh.cPast.domain.model.TempImageData;

import java.io.IOException;

public interface ImageRepository {
    TempImageData getTempImageData();

    boolean saveImageToGallery(String imageFileName, String note, String currentPhotoPath) throws IOException;

    int getHighestCounterForRecord(String baseReference);

}
