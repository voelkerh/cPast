package com.benskitchen.capturingthepast.persistence;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsRepository {
    private final String params = "AppSettings.json";
    private int nCaptureCounter = 0;
    boolean bTimestamped = true;
    private ArrayList<String> recentFiles = new ArrayList<>();
    JSONArray recentFileStore = new JSONArray();
    Context context;

    public SettingsRepository(Context context) {
        this.context = context.getApplicationContext();
        readJsonData(params);
    }

    // Read JSON file upon app start
    public void readJsonData(String params) {
        try {
            File f = new File("/data/data/" + context.getPackageName() + "/" + params);
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            if (size > 0) {
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String strResponse = new String(buffer);
                applyJsonSettings(strResponse);
            } else {
                writeDefaults();
            }
        } catch (IOException e) {
            writeDefaults();
            e.printStackTrace();
        }
    }

    private void applyJsonSettings(String jsonString) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            bTimestamped = jsonObject.getBoolean("bTimestamp");
            nCaptureCounter = jsonObject.getInt("nCaptureCount");
        } catch (JSONException e) {
            writeDefaults();
            e.printStackTrace();
        }
        try {
            jsonObject = new JSONObject(jsonString);
            recentFileStore = jsonObject.getJSONArray("recentFiles");
            recentFiles = new ArrayList<>();//(recentFileStore);
            for (int j = 0; j < recentFileStore.length(); j++) {
                recentFiles.add((String) recentFileStore.get(j));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Fallback for applyJsonSettings()
    public void writeDefaults() {
        String jsonString = "{\"bTimestamp\":\"TRUE\",\"nCaptureCount\":\"0\",\"recentFiles\":[]}";
        createAndSaveFile(params, jsonString);
    }

    public void createAndSaveFile(String params, String jsonString) {
        try {
            FileWriter file = new FileWriter("/data/data/" + context.getPackageName() + "/" + params);
            file.write(jsonString);
            file.flush();
            file.close();
            readJsonData(params);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePreferences() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("bTimestamp", bTimestamped);
            jsonObj.put("nCaptureCount", nCaptureCounter);
            jsonObj.put("recentFiles", recentFileStore);
            String output = jsonObj.toString();
            createAndSaveFile(params, output);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addFileToRecentFiles(String fName) {
        if (!fName.isEmpty()) {
            recentFiles.add(fName);
            if (recentFiles.size() > 5) {
                recentFiles.remove(0);
            }
            recentFileStore = new JSONArray(recentFiles);
        }
        writePreferences();
    }

    public int getCaptureCounter() {
        return nCaptureCounter;
    }

    public List<String> getRecentFiles() {
        return List.copyOf(recentFiles);
    }

}
