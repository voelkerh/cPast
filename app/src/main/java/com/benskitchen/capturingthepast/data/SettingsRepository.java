package com.benskitchen.capturingthepast.data;

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
    private final String params = "ArchonParams.json";
    private String strPrefix = "cpast";
    private int nCaptureCounter = 0;
    boolean bTimestamped = true;
    private String[] repos;

    private JSONArray repositories;
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
                writeArchons();
            }
        } catch (IOException e) {
            writeArchons();
            e.printStackTrace();
        }
    }

    private void applyJsonSettings(String jsonString) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            strPrefix = jsonObject.getString("strPrefix");
            bTimestamped = jsonObject.getBoolean("bTimestamp");
            nCaptureCounter = jsonObject.getInt("nCaptureCount");

            repositories = jsonObject.getJSONArray("data");
            repos = new String[repositories.length()];
            int length = repositories.length();
            for (int i = 0; i < length; i++) {
                JSONObject jo = repositories.getJSONObject(i);
                repos[i] = jo.getString("Repository");
            }
        } catch (JSONException e) {
            writeArchons();
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

    public String getArchonAt(int index) {
        final String DEFAULT_ARCHON = "GB0000";

        if (repositories == null || index < 0 || index >= repositories.length()) {
            return DEFAULT_ARCHON;
        }
        JSONObject obj = repositories.optJSONObject(index);
        if (obj == null) return DEFAULT_ARCHON;

        String archon = obj.optString("Archon", DEFAULT_ARCHON);
        return archon == null || archon.isEmpty() ? DEFAULT_ARCHON : archon;
    }

    // Fallback for applyJsonSettings()
    public void writeArchons() {
        String jsonString = "{\"strPrefix\":\"cpast\",\"bTimestamp\":\"TRUE\",\"nCaptureCount\":\"0\",\"recentFiles\":[],\"data\":[{\"Repository\":\"Repository - GB0000\",\"Archon\":\"GB0000\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Archives and Cornish Studies Service\",\"Archon\":\"GB0021\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Bedfordshire Archives & Record Service\",\"Archon\":\"GB0004\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Berkshire Record Office\",\"Archon\":\"GB0005\",\"Enabled\":\"TRUE\"},{\"Repository\":\"British Library Manuscript Collections\",\"Archon\":\"GB0058\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Brotherton Library - Leeds University\",\"Archon\":\"GB1471\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Buckinghamshire Archives\",\"Archon\":\"GB0008\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cambridge University Library: Department of Manuscripts and University Archives\",\"Archon\":\"GB0012\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cambridgeshire Archives\",\"Archon\":\"GB0010\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Ceredigion Archives\",\"Archon\":\"GB0212\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Chester Archives and Local Studies\",\"Archon\":\"GB0017\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Coventry Archives and Local Record Office\",\"Archon\":\"GB0144\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cumbria Archive Service (Barrow)\",\"Archon\":\"GB0025\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cumbria Archive Service (Carlisle)\",\"Archon\":\"GB0023\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cumbria Archive Service (Kendal)\",\"Archon\":\"GB0024\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cumbria Archive Service (Whitehaven)\",\"Archon\":\"GB1831\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Derby Local Studies and Family History Library\",\"Archon\":\"GB1160\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Derbyshire Local Studies Library\",\"Archon\":\"GB1944\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Derbyshire Record Office\",\"Archon\":\"GB0026\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Devon Archives and Local Studies Service (South WestHeritage Trust)\",\"Archon\":\"GB0027\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Dorset History Centre\",\"Archon\":\"GB0031\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Dr Williams’s Library\",\"Archon\":\"GB0123\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Dudley Archives and Local History Centre\",\"Archon\":\"GB0145\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Durham County Record Office\",\"Archon\":\"GB0032\",\"Enabled\":\"TRUE\"},{\"Repository\":\"East Sussex and Brighton and Hove Record Office\",\"Archon\":\"GB0179\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Essex Record Office\",\"Archon\":\"GB0037\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Gloucestershire Archives\",\"Archon\":\"GB0040\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Hampshire Archives and Local Studies\",\"Archon\":\"GB0041\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Herefordshire Archives and Records Centre\",\"Archon\":\"GB0044\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Hertfordshire Archives and Local Studies\",\"Archon\":\"GB0046\",\"Enabled\":\"TRUE\"},{\"Repository\":\"John Rylands Library\",\"Archon\":\"GB3191\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Kent History and Library Centre\",\"Archon\":\"GB0051\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Lambeth Palace Library\",\"Archon\":\"GB0109\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Lincolnshire Archives\",\"Archon\":\"GB0057\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Liverpool Record Office\",\"Archon\":\"GB1623\",\"Enabled\":\"TRUE\"},{\"Repository\":\"London Metropolitan Archives\",\"Archon\":\"GB0074\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Manchester City Archives\",\"Archon\":\"GB0127\",\"Enabled\":\"TRUE\"},{\"Repository\":\"National Library of Scotland\",\"Archon\":\"GB0233\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Norfolk Record Office\",\"Archon\":\"GB0153\",\"Enabled\":\"TRUE\"},{\"Repository\":\"North Yorkshire County Record Office\",\"Archon\":\"GB0191\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Northamptonshire Archives\",\"Archon\":\"GB0154\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Northumberland Archives\",\"Archon\":\"GB0155\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Northumberland Record Office - Morpeth\",\"Archon\":\"GB1834\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Nottinghamshire Archives\",\"Archon\":\"GB0157\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Oxford University: Bodleian Library - Special Collections\",\"Archon\":\"GB0161\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Oxfordshire History Centre\",\"Archon\":\"GB0160\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Religious Society of Friends Library\",\"Archon\":\"GB0111\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Sheffield City Archives\",\"Archon\":\"GB1163\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Sheffield Local Studies Library\",\"Archon\":\"GB1783\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Shropshire Archives\",\"Archon\":\"GB0166\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Somerset Heritage Centre\",\"Archon\":\"GB0168\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Suffolk Record Office - Bury St Edmunds Branch\",\"Archon\":\"GB0174\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Suffolk Record Office - Ipswich Branch\",\"Archon\":\"GB0173\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Surrey History Centre\",\"Archon\":\"GB0176\",\"Enabled\":\"TRUE\"},{\"Repository\":\"The National Archives - Kew\",\"Archon\":\"GB0066\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Tyne & Wear Archives\",\"Archon\":\"GB0183\",\"Enabled\":\"TRUE\"},{\"Repository\":\"University of Birmingham: Cadbury Research Library\",\"Archon\":\"GB0150\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Ushaw College Library (Durham University Special Collections)\",\"Archon\":\"GB0033\",\"Enabled\":\"TRUE\"},{\"Repository\":\"WellcomeCollection\",\"Archon\":\"GB0120\",\"Enabled\":\"TRUE\"},{\"Repository\":\"West Sussex Record Office\",\"Archon\":\"GB0182\",\"Enabled\":\"TRUE\"},{\"Repository\":\"West Yorkshire Archive Service (Bradford)\",\"Archon\":\"GB0202\",\"Enabled\":\"TRUE\"},{\"Repository\":\"West Yorkshire Archive Service (Calderdale)\",\"Archon\":\"GB0203\",\"Enabled\":\"TRUE\"},{\"Repository\":\"West Yorkshire Archive Service (Kirklees)\",\"Archon\":\"GB0204\",\"Enabled\":\"TRUE\"},{\"Repository\":\"West Yorkshire Archive Service (Leeds)\",\"Archon\":\"GB0205\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Wiltshire and Swindon History Centre\",\"Archon\":\"GB0190\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Wolverhampton City Archives\",\"Archon\":\"GB0149\",\"Enabled\":\"TRUE\"},{\"Repository\":\"York City Archives\",\"Archon\":\"GBYORK\",\"Enabled\":\"TRUE\"}]}";
        createAndSaveFile(params, jsonString);
    }

    // Data layer - Move
    public void resetArchons(String str) {
        String jsonString = "{\"strPrefix\":\"cpast\",\"bTimestamp\":\"TRUE\",\"nCaptureCount\":\"0\",\"recentFiles\":[],\"data\":[{\"Repository\":\"Repository - GB0000\",\"Archon\":\"GB0000\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Archives and Cornish Studies Service\",\"Archon\":\"GB0021\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Bedfordshire Archives & Record Service\",\"Archon\":\"GB0004\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Berkshire Record Office\",\"Archon\":\"GB0005\",\"Enabled\":\"TRUE\"},{\"Repository\":\"British Library Manuscript Collections\",\"Archon\":\"GB0058\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Brotherton Library - Leeds University\",\"Archon\":\"GB1471\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Buckinghamshire Archives\",\"Archon\":\"GB0008\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cambridge University Library: Department of Manuscripts and University Archives\",\"Archon\":\"GB0012\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cambridgeshire Archives\",\"Archon\":\"GB0010\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Ceredigion Archives\",\"Archon\":\"GB0212\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Chester Archives and Local Studies\",\"Archon\":\"GB0017\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Coventry Archives and Local Record Office\",\"Archon\":\"GB0144\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cumbria Archive Service (Barrow)\",\"Archon\":\"GB0025\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cumbria Archive Service (Carlisle)\",\"Archon\":\"GB0023\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cumbria Archive Service (Kendal)\",\"Archon\":\"GB0024\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cumbria Archive Service (Whitehaven)\",\"Archon\":\"GB1831\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Derby Local Studies and Family History Library\",\"Archon\":\"GB1160\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Derbyshire Local Studies Library\",\"Archon\":\"GB1944\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Derbyshire Record Office\",\"Archon\":\"GB0026\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Devon Archives and Local Studies Service (South WestHeritage Trust)\",\"Archon\":\"GB0027\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Dorset History Centre\",\"Archon\":\"GB0031\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Dr Williams’s Library\",\"Archon\":\"GB0123\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Dudley Archives and Local History Centre\",\"Archon\":\"GB0145\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Durham County Record Office\",\"Archon\":\"GB0032\",\"Enabled\":\"TRUE\"},{\"Repository\":\"East Sussex and Brighton and Hove Record Office\",\"Archon\":\"GB0179\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Essex Record Office\",\"Archon\":\"GB0037\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Gloucestershire Archives\",\"Archon\":\"GB0040\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Hampshire Archives and Local Studies\",\"Archon\":\"GB0041\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Herefordshire Archives and Records Centre\",\"Archon\":\"GB0044\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Hertfordshire Archives and Local Studies\",\"Archon\":\"GB0046\",\"Enabled\":\"TRUE\"},{\"Repository\":\"John Rylands Library\",\"Archon\":\"GB3191\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Kent History and Library Centre\",\"Archon\":\"GB0051\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Lambeth Palace Library\",\"Archon\":\"GB0109\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Lincolnshire Archives\",\"Archon\":\"GB0057\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Liverpool Record Office\",\"Archon\":\"GB1623\",\"Enabled\":\"TRUE\"},{\"Repository\":\"London Metropolitan Archives\",\"Archon\":\"GB0074\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Manchester City Archives\",\"Archon\":\"GB0127\",\"Enabled\":\"TRUE\"},{\"Repository\":\"National Library of Scotland\",\"Archon\":\"GB0233\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Norfolk Record Office\",\"Archon\":\"GB0153\",\"Enabled\":\"TRUE\"},{\"Repository\":\"North Yorkshire County Record Office\",\"Archon\":\"GB0191\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Northamptonshire Archives\",\"Archon\":\"GB0154\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Northumberland Archives\",\"Archon\":\"GB0155\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Northumberland Record Office - Morpeth\",\"Archon\":\"GB1834\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Nottinghamshire Archives\",\"Archon\":\"GB0157\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Oxford University: Bodleian Library - Special Collections\",\"Archon\":\"GB0161\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Oxfordshire History Centre\",\"Archon\":\"GB0160\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Religious Society of Friends Library\",\"Archon\":\"GB0111\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Sheffield City Archives\",\"Archon\":\"GB1163\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Sheffield Local Studies Library\",\"Archon\":\"GB1783\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Shropshire Archives\",\"Archon\":\"GB0166\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Somerset Heritage Centre\",\"Archon\":\"GB0168\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Suffolk Record Office - Bury St Edmunds Branch\",\"Archon\":\"GB0174\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Suffolk Record Office - Ipswich Branch\",\"Archon\":\"GB0173\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Surrey History Centre\",\"Archon\":\"GB0176\",\"Enabled\":\"TRUE\"},{\"Repository\":\"The National Archives - Kew\",\"Archon\":\"GB0066\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Tyne & Wear Archives\",\"Archon\":\"GB0183\",\"Enabled\":\"TRUE\"},{\"Repository\":\"University of Birmingham: Cadbury Research Library\",\"Archon\":\"GB0150\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Ushaw College Library (Durham University Special Collections)\",\"Archon\":\"GB0033\",\"Enabled\":\"TRUE\"},{\"Repository\":\"WellcomeCollection\",\"Archon\":\"GB0120\",\"Enabled\":\"TRUE\"},{\"Repository\":\"West Sussex Record Office\",\"Archon\":\"GB0182\",\"Enabled\":\"TRUE\"},{\"Repository\":\"West Yorkshire Archive Service (Bradford)\",\"Archon\":\"GB0202\",\"Enabled\":\"TRUE\"},{\"Repository\":\"West Yorkshire Archive Service (Calderdale)\",\"Archon\":\"GB0203\",\"Enabled\":\"TRUE\"},{\"Repository\":\"West Yorkshire Archive Service (Kirklees)\",\"Archon\":\"GB0204\",\"Enabled\":\"TRUE\"},{\"Repository\":\"West Yorkshire Archive Service (Leeds)\",\"Archon\":\"GB0205\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Wiltshire and Swindon History Centre\",\"Archon\":\"GB0190\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Wolverhampton City Archives\",\"Archon\":\"GB0149\",\"Enabled\":\"TRUE\"},{\"Repository\":\"York City Archives\",\"Archon\":\"GBYORK\",\"Enabled\":\"TRUE\"}]}";
        if (str.equals("short")) {
            jsonString = "{\"strPrefix\":\"cpast\",\"bTimestamp\":\"TRUE\",\"nCaptureCount\":\"0\",\"recentFiles\":[],\"data\":[{\"Repository\":\"Default Repository - GB0000\",\"Archon\":\"GB0000\",\"Enabled\":\"TRUE\"},{\"Repository\":\"TNA\",\"Archon\":\"GB0066\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Cumbria\",\"Archon\":\"GB0023\",\"Enabled\":\"TRUE\"},{\"Repository\":\"East Sussex\",\"Archon\":\"GB0179\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Staffordshire\",\"Archon\":\"GB0169\",\"Enabled\":\"TRUE\"},{\"Repository\":\"St Bartholomew's Hospital Archives\",\"Archon\":\"GB0405\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Royal London Hospital Archives\",\"Archon\":\"GB0387\",\"Enabled\":\"TRUE\"}]}";
        }
        else if (str.equals("alternative")) {
            jsonString = "{\"strPrefix\":\"cpast\",\"bTimestamp\":\"TRUE\",\"nCaptureCount\":\"0\",\"recentFiles\":[],\"data\":[{\"Repository\":\"Repository - Example\",\"Archon\":\"EXAMPLE\",\"Enabled\":\"TRUE\"},{\"Repository\":\"Mum's papers\",\"Archon\":\"GBMUM\",\"Enabled\":\"TRUE\"}]}";
        }
        createAndSaveFile(params, jsonString);
    }

    // Data layer - Move
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

    // Data layer - Move
    public void writePreferences() {
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("strPrefix", strPrefix);
            String strTimeStamped = "FALSE";
            if (bTimestamped) strTimeStamped = "TRUE";
            jsonObj.put("bTimestamp", strTimeStamped);
            jsonObj.put("nCaptureCount", nCaptureCounter);
            jsonObj.put("recentFiles", recentFileStore);
            jsonObj.put("data", repositories);
            String output = jsonObj.toString();
            createAndSaveFile(params, output);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Data layer - Move
    public void deleteRepository(int n) {
        try {
            JSONArray list = new JSONArray();
            int len = repositories.length();
            if (repositories != null) {
                for (int i = 0; i < len; i++) {
                    //Excluding the item at position
                    if (i != n) {
                        list.put(repositories.get(i));
                    }
                }
            }
            repositories = list;
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("strPrefix", strPrefix);
            String strTimeStamped = "FALSE";
            if (bTimestamped) {
                strTimeStamped = "TRUE";
            }
            jsonObj.put("bTimestamp", strTimeStamped);
            jsonObj.put("data", repositories);
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

    public boolean isTimestamped() {
        return bTimestamped;
    }

    public String[] getRepos() {
        return repos;
    }

    public JSONArray getRepositories() {
        return repositories;
    }

    public void setRepositories(JSONArray repositories) {
        this.repositories = repositories;
    }

    public List<String> getRecentFiles() {
        return List.copyOf(recentFiles);
    }
}
