package com.voelkerh.cPast.persistence;

import android.content.Context;
import android.util.Log;
import com.voelkerh.cPast.domainLogic.Archive;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonArchiveStore implements ArchiveStore {

    private static final String TAG = "JsonArchiveStore";
    private static final String FILE = "archives.json";
    private final Context context;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public JsonArchiveStore(Context context){
        this.context = context;
        this.inputStream = null;
        this.outputStream = null;
    }

    public JsonArchiveStore(InputStream inputStream, OutputStream outputStream){
        this.context = null;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    @Override
    public List<Archive> loadArchives() {
        InputStream stream = getInputStream();
        if (stream == null) return new ArrayList<>();

        try (InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if(sb.length() == 0) return new ArrayList<>();
            return jsonToList(sb.toString());

        } catch (FileNotFoundException e){
            return new ArrayList<>();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return new ArrayList<>();
        } finally {
            if (context != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing input stream", e);
                }
            }
        }
    }

    private InputStream getInputStream(){
        if(inputStream != null) return this.inputStream;
        if(context != null) {
            try {
                return context.openFileInput(FILE);
            } catch (FileNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    private List<Archive> jsonToList(String jsonString) {
        List<Archive> archives = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONArray array = root.optJSONArray("archives");
            if (array == null) return archives;

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.optJSONObject(i);
                if (obj == null) continue;

                String fullName = obj.optString("fullName", "").trim();
                String shortName = obj.optString("shortName", "").trim();
                if (fullName.isEmpty() || shortName.isEmpty()) continue;

                archives.add(new Archive(fullName, shortName));
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return new ArrayList<>();
        }
        return archives;
    }

    @Override
    public boolean saveArchives(List<Archive> archives) {
        if (archives == null) return false;
        OutputStream stream = getOutputStream();
        if (stream == null) return false;

        try {
            String json = listToJson(archives);
            stream.write(json.getBytes(StandardCharsets.UTF_8));
            stream.flush();
            return true;
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return false;
        } finally {
            if (context != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing output stream", e);
                }
            }
        }
    }

    private OutputStream getOutputStream(){
        if (outputStream != null) return this.outputStream;
        if (context != null) {
            try {
                return context.openFileOutput(FILE, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    private String listToJson(List<Archive> archives) {
        try {
            JSONObject root = new JSONObject();
            JSONArray array = new JSONArray();
            for (Archive archive : archives) {
                if (archive == null) continue;

                JSONObject obj = new JSONObject();
                obj.put("fullName", archive.getFullName());
                obj.put("shortName", archive.getShortName());
                array.put(obj);
            }
            root.put("archives", array);
            return root.toString(2);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return "{}";
        }
    }
}
