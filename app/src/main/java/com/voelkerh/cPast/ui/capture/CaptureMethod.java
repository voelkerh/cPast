package com.voelkerh.cPast.ui.capture;

public interface CaptureMethod {

    interface Listener {
        void onCaptured(String filePath);
        void onCancelled();
        void onError(String message);
    }

    void capture(CaptureRequest request);

}
