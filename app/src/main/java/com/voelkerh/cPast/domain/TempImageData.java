package com.voelkerh.cPast.domain;

import android.net.Uri;

public class TempImageData {

    public final Uri uri;
    public final String path;

    public TempImageData(Uri uri, String path) {
        this.uri = uri;
        this.path = path;
    }

}
