package com.benskitchen.capturingthepast.persistence;

import android.content.Context;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
    public Map<String, String> loadArchives() {
        InputStream stream = getInputStream();
        if (stream == null) return new HashMap<>();

        try (InputStreamReader isr = new InputStreamReader(stream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if(sb.length() == 0) return new HashMap<>();
            return jsonToMap(sb.toString());

        } catch (FileNotFoundException e){
            return new HashMap<>();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return new HashMap<>();
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

    private Map<String, String> jsonToMap(String jsonString) {
        Map<String, String> archives = new HashMap<>();
        try {
            JSONObject root = new JSONObject(jsonString);
            JSONObject map = root.optJSONObject("archives");
            if (map != null) {
                Iterator<String> keys = map.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    archives.put(key, map.optString(key, ""));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
            return new HashMap<>();
        }
        return archives;
    }

    @Override
    public boolean saveArchives(Map<String, String> archives) {
        if (archives == null) return false;
        OutputStream stream = getOutputStream();
        if (stream == null) return false;

        try {
            String json = mapToJson(archives);
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

    private String mapToJson(Map<String, String> map) {
        try {
            JSONObject root = new JSONObject();
            JSONObject archive = new JSONObject();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                archive.put(entry.getKey(), entry.getValue());
            }
            root.put("archives", archive);
            return root.toString(2);
        } catch (JSONException e) {
            Log.e("JsonArchiveStore", e.toString());
            return "{}";
        }
    }
}
