package com.voelkerh.cPast.ui.capture.intent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.voelkerh.cPast.ui.capture.CaptureMethod;
import com.voelkerh.cPast.ui.capture.CaptureRequest;

public class IntentCaptureMethod implements CaptureMethod {

    private final PackageManager packageManager;
    private final Listener listener;
    private final ActivityResultLauncher<Intent> takePictureLauncher;
    private CaptureRequest captureRequest;

    public IntentCaptureMethod(ActivityResultCaller caller, Context context, Listener listener) {
        this.packageManager = context.getPackageManager();
        this.listener = listener;
        this.takePictureLauncher = caller.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::onActivityResult);
    }

    @Override
    public void capture(CaptureRequest captureRequest) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(packageManager) == null){
            listener.onError("No camera app available.");
            return;
        }
        this.captureRequest = captureRequest;
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.captureRequest.getUri());
        takePictureLauncher.launch(intent);

    }

    private void onActivityResult(ActivityResult result){
        CaptureRequest request = this.captureRequest;
        this.captureRequest = null;
        if (result.getResultCode() != Activity.RESULT_OK) {
            listener.onCancelled();
        } else if (request == null) {
            listener.onError("Capture request was lost.");
        } else {
            listener.onCaptured(request.getFilePath());
        }
    }

}
