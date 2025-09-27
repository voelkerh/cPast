package com.benskitchen.capturingthepast.data;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class ImageRepository {

    Context context;
    String currentFolderPath;
    String currentPhotoPath;

    public ImageRepository(Context context) {
        this.context = context;
    }

    public File createTempImageFile() throws IOException {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir == null) throw new IOException("No storage directory found");

        File image = new File(storageDir, "temp.jpg");
        currentFolderPath = storageDir.getAbsolutePath();
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}
