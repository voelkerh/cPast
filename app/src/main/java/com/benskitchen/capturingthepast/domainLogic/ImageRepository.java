package com.benskitchen.capturingthepast.domainLogic;

import android.content.Context;
import android.net.Uri;
import com.benskitchen.capturingthepast.persistence.ImageStore;

import java.io.IOException;

public class ImageRepository {

    private final Context context;
    private final ImageStore imageStore;

    public ImageRepository(Context context, ImageStore imageStore) {
        this.context = context;
        this.imageStore = imageStore;
    }

    public Uri getTempImageFileUri() {
        return null;
    }

    public boolean saveImageToGallery(String imageFileName, String strNote) throws IOException {
        return false;
    }
}
