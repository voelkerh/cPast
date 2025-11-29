package com.benskitchen.capturingthepast.persistence;

import android.content.Context;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static java.util.Collections.emptyMap;

public class JsonArchiveStore implements ArchiveStore {

    private final Context context;
    private static final String FILE = "archives.json";

    public JsonArchiveStore(Context context){
        this.context = context;
    }

    @Override
    public Map<String, String> loadArchives() {
        try (FileInputStream fis = context.openFileInput(FILE);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            if(sb.length() == 0) return emptyMap();
            return jsonToMap(sb.toString());

        } catch (FileNotFoundException e){
            return emptyMap();
        } catch (IOException e) {
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
            return emptyMap();
        }
        return archives;
    }

    @Override
    public boolean saveArchives(Map<String, String> archives) {
        if (archives == null) return false;
        try(FileOutputStream fos = context.openFileOutput(FILE, Context.MODE_PRIVATE)){
            String json = mapToJson(archives);
            fos.write(json.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String mapToJson(Map<String, String> map) {
        return null;
    }
}
