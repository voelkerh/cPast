package com.voelkerh.cPast.persistence;

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
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Locale;

public class MediaImageStore implements ImageStore {
    private static final String TAG = "MediaImageStore";
    private static final String TEMP_FILE_NAME = "temp.jpg";
    private static final String MIME_TYPE = "image/jpeg";
    private static final int JPEG_QUALITY = 100;
    private final Context context;

    public MediaImageStore(Context context) {
        this.context = context;
    }

    public File createTempImageFile(File storageDir) throws IOException {
        if (storageDir == null) throw new IOException("No storage directory found");
        return new File(storageDir, TEMP_FILE_NAME);
    }

    public boolean saveImageToGallery(String imageFileName, String note, String currentPhotoPath, String[] directoryNames) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        if (bitmap == null) throw new IOException("No bitmap found");

        ExifInterface sourceExif = new ExifInterface(currentPhotoPath);

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE);
        String relative = getRelativePath(directoryNames);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relative);
        ContentResolver contentResolver = context.getContentResolver();
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        if (imageUri == null) throw new IOException("Failed to create MediaStore entry");

        try (ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(imageUri, "w");
            OutputStream os = new FileOutputStream(pfd.getFileDescriptor())) {

            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, os);
            pfd.getFileDescriptor().sync();

            String userComment = imageFileName + " " + note;
            copyExifData(imageUri, sourceExif, userComment);

            return true;

        } catch (IOException e) {
            Log.e(TAG, e.toString());
            contentResolver.delete(imageUri, null, null);
            throw e;
        } finally {
            bitmap.recycle();
        }
    }

    private String getRelativePath(String[] directoryNames) {
        return Environment.DIRECTORY_PICTURES
                + "/CapturingThePast/"
                + String.join("/", directoryNames) + "/";
    }

    private void copyExifData(Uri targetUri, ExifInterface sourceExif, String userComment) {

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

            targetExif.setAttribute(ExifInterface.TAG_USER_COMMENT, userComment);
            targetExif.saveAttributes();

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public int getHighestCounterForRecord(String[] directoryNames) {
        String relative = getRelativePath(directoryNames);

        String[] projection = { MediaStore.Images.Media.DISPLAY_NAME };
        String selection = MediaStore.Images.Media.RELATIVE_PATH + " = ?";
        String[] selectionArgs = { relative };

        int max = 0;

        ContentResolver cr = context.getContentResolver();
        try (Cursor c = cr.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
        )) {
            if (c == null) return 0;

            int nameCol = c.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            while (c.moveToNext()) {
                String name = c.getString(nameCol);
                int counter = extractCounter(name);
                if (counter > max) max = counter;
            }
        }
        return max;
    }

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
