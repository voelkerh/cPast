package com.benskitchen.capturingthepast.persistence;

import java.io.File;
import java.io.IOException;

public interface ImageStore {
    File createTempImageFile(File storageDir) throws IOException;
    boolean saveImageToGallery(String imageFileName, String strNote, String currentPhotoPath, String folderPath) throws IOException;
}
