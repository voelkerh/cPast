package com.benskitchen.capturingthepast.persistence;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class LogWriter {

    private final Context context;

    public LogWriter(Context context) {
        this.context = context;
    }

    public String writePublicLog(String str) {
        Uri contentUri = MediaStore.Files.getContentUri("external");
        String selection = MediaStore.MediaColumns.RELATIVE_PATH + "=?";
        String[] selectionArgs = new String[]{Environment.DIRECTORY_DOCUMENTS + "/CapturingThePast/"};
        Cursor cursor = context.getContentResolver().query(contentUri, null, selection, selectionArgs, null);
        Uri uri = null;
        if (cursor.getCount() == 0) {
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "CapturingThePastLog");       //file name
                values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");        //file extension, will automatically add to file
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/CapturingThePast/");     //end "/" is not mandatory
                Uri uri2 = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);      //important!
                OutputStream outputStream = context.getContentResolver().openOutputStream(uri2);
                str = "\"Date\", \"Cat Ref\", \"Filename\", \"Note\"\n" + str;  // Add a header row to the string input as this is the opening line
                outputStream.write(str.getBytes());
                outputStream.close();
                cursor.close();
                return "";
            } catch (IOException e) {
                return "Fail to create log";
            }
        } else {
            String strLogFilename = "CapturingThePastLog.csv";
            while (cursor.moveToNext()) {
                int disName = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                int disID = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                if(disName>=0 && disID>=0) {
                    String fileName = cursor.getString(disName);
                    if (fileName.equals(strLogFilename)) {
                        long id = cursor.getLong(disID);

                        uri = ContentUris.withAppendedId(contentUri, id);
                        break;
                    }
                }
            }
            cursor.close();
            if (uri == null) {
                return "\"" + strLogFilename + "\" not found";
            } else {
                try {
                    InputStream inputStream = context.getContentResolver().openInputStream(uri);
                    int size = inputStream.available();
                    byte[] bytes = new byte[size];
                    inputStream.read(bytes);
                    inputStream.close();
                    String strFileTxt = new String(bytes, StandardCharsets.UTF_8);
                    strFileTxt += "\n";
                    strFileTxt += str;
                    OutputStream outputStream = context.getContentResolver().openOutputStream(uri, "rwt");      //overwrite mode, see below
                    outputStream.write(strFileTxt.getBytes());
                    outputStream.close();
                    return "";
                } catch (IOException e) {
                    return "Fail to read file";
                }
            }
        }
    }
}
