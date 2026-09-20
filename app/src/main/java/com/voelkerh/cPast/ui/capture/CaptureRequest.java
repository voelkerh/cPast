package com.voelkerh.cPast.ui.capture;

import android.net.Uri;

public class CaptureRequest {

    private final String filePath;
    private final Uri uri;

    public CaptureRequest(String filePath, Uri uri) {
        this.filePath = filePath;
        this.uri = uri;
    }

    public String getFilePath(){
        return filePath;
    }

    public Uri getUri(){
        return uri;
    }
}
