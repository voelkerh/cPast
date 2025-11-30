package com.benskitchen.capturingthepast.persistence;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonRecentCapturesStore implements RecentCapturesStore {

    private static final String TAG = "JsonSettingsStore";
    private static final String FILE_NAME = "AppSettings.json";

    private final Context context;

    public JsonRecentCapturesStore(Context context) {
        this.context = context;
    }

    @Override
    public List<String> loadRecentFiles() {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) return Collections.emptyList();

        try(FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            return parseRecentFiles(sb.toString());

        } catch (IOException e){
            Log.e(TAG, "Error while loading recent files: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<String> parseRecentFiles(String string) {
        List<String> recentFiles = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(string);
            JSONArray filesArray = json.optJSONArray("recentFiles");

            if (filesArray !=null) {
                for (int i = 0; i < filesArray.length(); i++) {
                    recentFiles.add(filesArray.getString(i));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
        }
        return recentFiles;
    }

    @Override
    public boolean saveRecentFiles(List<String> files) {
        if (files == null || files.isEmpty()) return false;

        try {
            JSONObject json = new JSONObject();
            json.put("recentFiles", new JSONArray(files));

            File file = new File(context.getFilesDir(), FILE_NAME);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json.toString(2));
            }
            return true;
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Error saving recent files: " + e.getMessage());
            return false;
        }
    }
}
