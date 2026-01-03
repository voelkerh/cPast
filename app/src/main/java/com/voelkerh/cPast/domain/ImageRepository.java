package com.voelkerh.cPast.domain;

import java.io.IOException;

public interface ImageRepository {
    TempImageData getTempImageData();

    boolean saveImageToGallery(String imageFileName, String note, String currentPhotoPath) throws IOException;

    int getHighestCounterForRecord(String baseReference);

}
