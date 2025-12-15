package com.benskitchen.capturingthepast.persistence;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class CsvNoteStore implements NoteStore {

    private static final String TAG = "CsvNoteStore";
    private static final String LOG_FILENAME = "cPast_Notes.csv";
    private static final String LOG_DIR = Environment.DIRECTORY_DOCUMENTS + "/CapturingThePast/";
    private static final String CSV_HEADER = "\"Date\", \"Filename\", \"Note\"\n";

    private final Context context;

    public CsvNoteStore(Context context) {
        this.context = context;
    }

    @Override
    public boolean saveNote(String time, String imageName, String note) {
        if (note == null || note.isEmpty()) return false;

        Uri uri = findExistingFile();
        if (uri == null) uri = createNewFile();

        String csvEntry = formatEntry(time, imageName, note);
        return appendToFile(uri, csvEntry);
    }

    private String formatEntry(String time, String imageName, String note) {
        return "\"" + time + "\",\"" + imageName + "\",\"" + note + "\"\n";
    }

    private Uri findExistingFile() {
        Uri contentUri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = new String[]{LOG_DIR};

        try (Cursor cursor = context.getContentResolver().query(
                contentUri, null, selection, selectionArgs, null)) {

            if (cursor == null || cursor.getCount() == 0) {
                return null;
            }

            int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            int idIndex = cursor.getColumnIndex(MediaStore.MediaColumns._ID);

            if (nameIndex < 0 || idIndex < 0) {
                return null;
            }

            while (cursor.moveToNext()) {
                String fileName = cursor.getString(nameIndex);
                if (LOG_FILENAME.equals(fileName)) {
                    long id = cursor.getLong(idIndex);
                    return ContentUris.withAppendedId(contentUri, id);
                }
            }
        }
        return null;
    }

    private Uri createNewFile() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, LOG_FILENAME);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, LOG_DIR);

        Uri uri = context.getContentResolver().insert(
                MediaStore.Files.getContentUri("external"), values);

        if (uri == null) {
            Log.e(TAG, "Failed to create notes file");
            return null;
        }

        try (OutputStream os = context.getContentResolver().openOutputStream(uri)) {
            os.write(CSV_HEADER.getBytes(StandardCharsets.UTF_8));
            return uri;
        } catch (IOException e) {
            Log.e(TAG, "Failed to write to notes file: " + e.getMessage());
            return null;
        }
    }

    private boolean appendToFile(Uri uri, String note) {
        if(uri == null) return false;
        if (!note.endsWith("\n")) note = note + "\n";

        try (OutputStream os = context.getContentResolver().openOutputStream(uri, "wa")) {
            os.write(note.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to append to file: " + e.getMessage());
            return false;
        }
    }

}
