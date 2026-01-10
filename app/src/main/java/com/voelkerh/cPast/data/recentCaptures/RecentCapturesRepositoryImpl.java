package com.voelkerh.cPast.data.recentCaptures;

import android.content.Context;
import android.util.Log;
import com.voelkerh.cPast.domain.model.Archive;
import com.voelkerh.cPast.domain.model.Capture;
import com.voelkerh.cPast.domain.repository.RecentCapturesRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-layer repository that persists recent {@link Capture} entries as JSON.
 *
 * <p>This implementation stores and retrieves a bounded list of recent captures from application-private storage.
 * It contains no business validation logic.</p>
 */
public class RecentCapturesRepositoryImpl implements RecentCapturesRepository {
    private static final String TAG = "RecentCapturesRepositoryImpl";

    private static final String FILE_NAME = "recent_captures.json";

    private final Context context;

    /**
     * Creates a repository instance with application-private file storage.
     *
     * @param context application context used to access internal storage
     */
    public RecentCapturesRepositoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public List<Capture> load() {
        File file = new File(context.getFilesDir(), FILE_NAME);
        if (!file.exists()) return new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            return parseRecentFiles(sb.toString());

        } catch (IOException e) {
            Log.e(TAG, "Error while loading recent files: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<Capture> parseRecentFiles(String string) {
        List<Capture> recentFiles = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(string);
            JSONArray filesArray = json.optJSONArray("recentFiles");

            if (filesArray != null) {
                for (int i = 0; i < filesArray.length(); i++) {
                    JSONObject obj = filesArray.optJSONObject(i);
                    if (obj == null) continue;

                    Capture c = jsonToCapture(obj);
                    if (c != null) recentFiles.add(c);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
        }
        return recentFiles;
    }

    private Capture jsonToCapture(JSONObject obj) {
        String fileName = obj.optString("fileName", "");
        if (fileName.isEmpty()) return null;

        String note = obj.optString("note", "");

        Archive archive = null;
        JSONObject archObj = obj.optJSONObject("archive");
        if (archObj != null) {
            String fullName = archObj.optString("fullName", "").trim();
            String shortName = archObj.optString("shortName", "").trim();
            if (!fullName.isEmpty() && !shortName.isEmpty()) {
                archive = new Archive(fullName, shortName);
            }
        }

        return new Capture(archive, fileName, note);
    }

    @Override
    public boolean save(List<Capture> files) {
        if (files == null || files.isEmpty()) return false;

        try {
            JSONObject json = new JSONObject();
            JSONArray arr = new JSONArray();
            for (Capture c : files) {
                if (c == null) continue;
                arr.put(captureToJson(c));
            }
            json.put("recentFiles", arr);

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

    private JSONObject captureToJson(Capture capture) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("fileName", capture.getFileName());
        String note = capture.getNote();
        object.put("note", note != null ? note : "");

        Archive archive = capture.getArchive();
        if (archive != null) {
            JSONObject arch = new JSONObject();
            arch.put("fullName", archive.getFullName());
            arch.put("shortName", archive.getShortName());
            object.put("archive", arch);
        }

        return object;
    }

}
