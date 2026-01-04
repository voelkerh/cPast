package com.voelkerh.cPast.data.notes;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import com.voelkerh.cPast.domain.model.Archive;
import com.voelkerh.cPast.domain.model.Capture;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CsvNotesStore implements NotesStore {

    private static final String TAG = "CsvNoteStore";
    private static final String LOG_FILENAME = "cPast_Notes.csv";
    private static final String LOG_DIR = Environment.DIRECTORY_DOCUMENTS + "/CapturingThePast/";
    private static final String CSV_HEADER = "Date; Time; Archive; File; Note\n";
    private static final String CSV_DELIMITER = ";";

    private final Context context;

    public CsvNotesStore(Context context) {
        this.context = context;
    }

    private static String csvField(String s) {
        if (s == null) return "\"\"";
        s = s.replace("\"", "\"\"");
        return "\"" + s + "\"";
    }

    @Override
    public boolean saveNote(Capture capture) {
        String note = capture.getNote();
        String imageName = capture.getFileName();
        LocalDateTime captureTime = capture.getCaptureTime();
        Archive archive = capture.getArchive();

        if (note == null || note.isEmpty() || imageName.isEmpty() || captureTime == null || archive == null)
            return false;

        Uri uri = findExistingFile();
        if (uri == null) uri = createNewFile();

        String csvEntry = formatEntry(captureTime, archive, imageName, note);
        return appendToFile(uri, csvEntry);
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
            os.write(0xEF);
            os.write(0xBB);
            os.write(0xBF);
            os.write(CSV_HEADER.getBytes(StandardCharsets.UTF_8));
            return uri;
        } catch (IOException e) {
            Log.e(TAG, "Failed to write to notes file: " + e.getMessage());
            return null;
        }
    }

    private String formatEntry(LocalDateTime dateTime, Archive archive, String imageName, String note) {
        String date = dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
        String time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String archiveName = archive.getFullName();
        return csvField(date) + CSV_DELIMITER + csvField(time) + CSV_DELIMITER + csvField(archiveName) + CSV_DELIMITER + csvField(imageName) + CSV_DELIMITER + csvField(note) + "\n";
    }

    private boolean appendToFile(Uri uri, String note) {
        if (uri == null) return false;

        try (OutputStream os = context.getContentResolver().openOutputStream(uri, "wa")) {
            os.write(note.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to append to file: " + e.getMessage());
            return false;
        }
    }

}
