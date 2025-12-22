package com.benskitchen.cPast.domainLogic;

public class CaptureCounter {

    private int captureCount;

    public CaptureCounter() {
        this.captureCount = 0;
    }

    public int getCaptureCount() {
        return captureCount;
    }

    public void setCaptureCount(int captureCount) {
        this.captureCount = captureCount;
    }

    public void incrementCaptureCount() {
        captureCount++;
    }
}
