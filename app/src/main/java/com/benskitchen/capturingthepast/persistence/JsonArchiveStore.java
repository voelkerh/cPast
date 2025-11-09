package com.benskitchen.capturingthepast.persistence;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.Collections.emptyMap;

public class JsonArchiveStore implements ArchiveStore {

    Context context;
    private static final String FILE = "archives.json";

    public JsonArchiveStore(Context context){
        this.context = context;
    }

    @Override
    public Map<String, String> loadArchives() {
        try (FileInputStream fis = context.openFileInput(FILE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = fis.read(buffer)) != -1) baos.write(buffer, 0, read);
            if (baos.size() == 0) return emptyMap();
            String input = baos.toString();
            return jsonToMap(input);
        } catch (FileNotFoundException e){
            return emptyMap();
        } catch (Exception e) {
            e.printStackTrace();
            return emptyMap();
        }
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
            e.printStackTrace();
        }
        return archives;
    }

    @Override
    public boolean saveArchives(Map<String, String> archives) {
        return false;
    }

    private String mapToJson(Map<String, String> map) {
        return null;
    }
}
