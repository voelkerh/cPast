package com.voelkerh.cPast.persistence;

import java.io.File;
import java.io.IOException;

public interface ImageStore {
    File createTempImageFile(File storageDir) throws IOException;
    boolean saveImageToGallery(String imageFileName, String strNote, String currentPhotoPath, String[] directoryNames) throws IOException;
    int getHighestCounterForRecord(String[] directoryNames);
}
