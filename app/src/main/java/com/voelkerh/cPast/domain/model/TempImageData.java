package com.voelkerh.cPast.domain.model;

import android.net.Uri;

/**
 * Domain model holding temporary image metadata used during camera capture.
 *
 * <p>This class encapsulates the {@link Uri} provided to the camera application
 * and the corresponding file system path where the temporary image is stored.</p>
 */
public class TempImageData {

    private final Uri uri;
    private final String path;

    public TempImageData(Uri uri, String path) {
        this.uri = uri;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public Uri getUri() {
        return uri;
    }
}
