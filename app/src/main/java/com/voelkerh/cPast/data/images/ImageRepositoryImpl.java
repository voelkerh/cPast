package com.voelkerh.cPast.data.images;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import com.voelkerh.cPast.domain.model.TempImageData;
import com.voelkerh.cPast.domain.repository.ImageRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Locale;

/**
 * Data-layer repository that manages image persistence and metadata handling.
 *
 * <p>This implementation is responsible for creating temporary image files and storing final images in the Android {@link MediaStore}.
 * It propagates relevant EXIF metadata from the temporary image to the persisted MediaStore entry.</p>
 *
 * <p>The class contains no business logic and relies on Android-specific APIs for file handling and media indexing.</p>
 */
public class ImageRepositoryImpl implements ImageRepository {

    private static final String TAG = "ImageRepositoryImpl";
    private static final String TEMP_FILE_NAME = "temp.jpg";
    private static final String MIME_TYPE = "image/jpeg";
    private static final int JPEG_QUALITY = 100;

    private final String fileProviderAuthority;
    private final Context context;

    /**
     * Creates a new repository instance using Android system services.
     *
     * <p>As this implementation uses Android MediaStore tests need to be instrumented.</p>
     *
     * @param context application context used to access storage, MediaStore and content resolvers
     */
    public ImageRepositoryImpl(Context context) {
        this.context = context;
        fileProviderAuthority = context.getPackageName() + ".fileprovider";
    }

    @Override
    public TempImageData getTempImageData() {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, TEMP_FILE_NAME);
        Uri uri = FileProvider.getUriForFile(context, fileProviderAuthority, image);
        return new TempImageData(uri, image.getAbsolutePath());
    }

    /**
     * Persists an image into the Android MediaStore and applies metadata.
     *
     * <p>This method decodes the temporary image, creates a new MediaStore entry, writes the image data,
     * and propagates EXIF metadata from the temporary image to the final MediaStore image.</p>
     *
     * @param imageFileName name of the final image file
     * @param note user-provided note stored in the image metadata
     * @param currentPhotoPath absolute path to the temporary image file
     * @param directoryNames directory structure under which the image is stored
     * @return boolean if the image was successfully written and indexed
     */
    @Override
    public boolean save(String imageFileName, String note, String currentPhotoPath, String[] directoryNames) {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        if (bitmap == null) return false;

        ExifInterface sourceExif = loadExif(currentPhotoPath);
        if (sourceExif == null) {
            Log.e(TAG, "ExifInterface could not be loaded");
            bitmap.recycle();
            return false;
        }

        Uri imageUri = createMediaStoreEntry(imageFileName, directoryNames);
        if (imageUri == null) {
            Log.e(TAG, "Failed to create MediaStore entry");
            bitmap.recycle();
            return false;
        }

        boolean success = writeImageAndExif(imageUri, bitmap, sourceExif, imageFileName, note);

        if (!success) {
            Log.e(TAG, "Failed to write image and Exif");
            context.getContentResolver().delete(imageUri, null, null);
        }

        bitmap.recycle();
        return success;
    }

    private ExifInterface loadExif(String currentPhotoPath) {
        try {
            return new ExifInterface(currentPhotoPath);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    private Uri createMediaStoreEntry(String imageFileName, String[] directoryNames) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, getRelativePath(directoryNames));

        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
    }

    private boolean writeImageAndExif(Uri uri, Bitmap bitmap, ExifInterface sourceExif, String imageFileName, String note) {
        try (ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "w");
             OutputStream outputStream = new FileOutputStream(pfd.getFileDescriptor())) {

            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream);
            pfd.getFileDescriptor().sync();

            String userComment = imageFileName + " " + note;
            copyExifDataFromTempImageToMediaStoreEntry(sourceExif, uri, userComment);

            return true;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }

    private String getRelativePath(String[] directoryNames) {
        return Environment.DIRECTORY_PICTURES
                + "/CapturingThePast/"
                + String.join("/", directoryNames) + "/";
    }

    private void copyExifDataFromTempImageToMediaStoreEntry(ExifInterface sourceExif, Uri targetUri, String userComment) {
        if (sourceExif == null || targetUri == null) return;

        try (ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(targetUri, "rw")) {
            if (pfd == null) {
                Log.w(TAG, "Could not open file descriptor for EXIF data");
                return;
            }

            ExifInterface targetExif = new ExifInterface(pfd.getFileDescriptor());

            Field[] fields = ExifInterface.class.getFields();
            for (Field field : fields) {
                if (field.getName().startsWith("TAG")) {
                    try {
                        String tag = (String) field.get(null);
                        if (tag == null) continue;
                        String value = sourceExif.getAttribute(tag);
                        if (value != null) targetExif.setAttribute(tag, value);
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "Could not access field: " + field.getName());
                    }
                }
            }

            if (userComment != null && !userComment.isEmpty()) {
                targetExif.setAttribute(ExifInterface.TAG_USER_COMMENT, userComment);
            }

            targetExif.saveAttributes();

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public int getHighestCounterForRecord(String[] directoryNames) {
        if (directoryNames == null || directoryNames.length == 0) return 0;

        String relative = getRelativePath(directoryNames);

        String[] projection = {MediaStore.Images.Media.DISPLAY_NAME};
        String selection = MediaStore.Images.Media.RELATIVE_PATH + " = ?";
        String[] selectionArgs = {relative};

        int max = 0;

        ContentResolver contentResolver = context.getContentResolver();
        try (Cursor cursor = contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        )) {
            if (cursor == null) return 0;

            int nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            while (cursor.moveToNext()) {
                String name = cursor.getString(nameCol);
                int counter = extractCounter(name);
                if (counter > max) max = counter;
            }
        }
        return max;
    }

    /**
     * Extracts a numeric counter from filenames following the pattern *_<number>.jpg.
     */
    private int extractCounter(String fileName) {
        if (fileName == null) return 0;
        if (!fileName.toLowerCase(Locale.ROOT).endsWith(".jpg")) return 0;

        String base = fileName.substring(0, fileName.length() - 4);
        int us = base.lastIndexOf('_');
        if (us < 0 || us == base.length() - 1) return 0;

        String tail = base.substring(us + 1);
        try {
            return Integer.parseInt(tail);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Could not convert to an integer: " + tail);
            return 0;
        }
    }

}
