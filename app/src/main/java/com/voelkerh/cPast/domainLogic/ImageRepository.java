package com.voelkerh.cPast.domainLogic;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.voelkerh.cPast.persistence.ImageStore;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class  ImageRepository {
    private static final String TAG = "ImageRepository";
    private static final String FILE_PROVIDER_AUTHORITY = "com.voelkerh.cPast.fileprovider";
    private final Context context;
    private final ImageStore imageStore;

    public ImageRepository(Context context, ImageStore imageStore) {
        this.context = context;
        this.imageStore = imageStore;
    }

    /**
     * Creates a temporary image file and returns its URI and file path.
     * <p>
     * This method prepares a temporary file in the app's external pictures directory
     * for the camera to write to. The returned {@link TempImageInfo} contains both
     * a content URI (for passing to the camera intent) and the absolute file path
     * (for later processing and moving the image to the gallery).
     *
     * @return a {@link TempImageInfo} object containing the file URI and path,
     *         or {@code null} if the temp file could not be created
     * @see TempImageInfo
     * @see #saveImageToGallery(String, String, String)
     */
    public TempImageInfo getTempImageInfo() {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File image = imageStore.createTempImageFile(storageDir);
            Uri uri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, image);
            return new TempImageInfo(uri, image.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    public boolean saveImageToGallery(String imageFileName, String note, String currentPhotoPath) throws IOException {
        String[] directoryNames = getNecessaryDirectories(imageFileName);
        return imageStore.saveImageToGallery(imageFileName, note, currentPhotoPath, directoryNames);
    }

    private String[] getNecessaryDirectories(String imageFileName) {
        String baseName = imageFileName;
        if (imageFileName.endsWith(".jpg")) {
            baseName = imageFileName.substring(0, imageFileName.length() - 4);
            String[] parts = baseName.split("_+");
            return Arrays.copyOf(parts, parts.length - 1);
        } else {
            String[] parts = baseName.split("_+");
            return Arrays.copyOf(parts, parts.length);
        }

    }

    public int getHighestCounterForRecord(String baseReference) {
        String[] directoryNames = getNecessaryDirectories(baseReference);
        return imageStore.getHighestCounterForRecord(directoryNames);
    }

    public static class TempImageInfo {
        public final Uri uri;
        public final String path;

        TempImageInfo(Uri uri, String path) {
            this.uri = uri;
            this.path = path;
        }
    }
}
