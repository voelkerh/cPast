package com.benskitchen.capturingthepast.persistence;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.*;
import java.lang.reflect.Field;

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

    public Uri getTempImageFileUri() {
        try {
            File photoFile = createTempImageFile();
            if (photoFile != null) return FileProvider.getUriForFile(context,
                    "com.benskitchen.capturingthepast.fileprovider",
                    photoFile);
        } catch (IOException e) {
            // Error occurred while creating the File
            e.printStackTrace();
        }
        return null;
    }

    public void saveImageToGallery(String imageFileName, String catRef, String strNote) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
        ExifInterface exif = new ExifInterface(currentPhotoPath);
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "CapturingThePast");
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        try (ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(imageUri, "w")) {
            FileDescriptor fd = pfd.getFileDescriptor();

            try (OutputStream stream = new FileOutputStream(fd)) {
                // Perform operations on "stream".
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }

            // Sync data with disk. It's mandatory to be able later to call writeExif
            fd.sync();    // <---- HERE THE SOLUTION
            final String userComment = "Capturing the Past image " + catRef + " " + strNote;
            writeExif(imageUri, exif, userComment);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Writes Metadata from temp image to new image in gallery
    public void writeExif(Uri uri, ExifInterface exif, String userComment) {

        try (ParcelFileDescriptor imagePfd = context.getContentResolver().openFileDescriptor(uri, "rw")) {
            ExifInterface exifNew = new ExifInterface(imagePfd.getFileDescriptor());

            // Copy existing tags
            Field[] fields = ExifInterface.class.getFields();
            for (Field field : fields) {
                if (field.getName().startsWith("TAG")) {
                    try {
                        String tag = (String) field.get(null);
                        String value = exif.getAttribute(tag);
                        if (value != null) exifNew.setAttribute(tag, value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            // Add user comment
            exifNew.setAttribute(ExifInterface.TAG_USER_COMMENT, userComment);

            exifNew.saveAttributes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
