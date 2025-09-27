package com.benskitchen.capturingthepast.domainLogic;

import com.benskitchen.capturingthepast.data.SettingsRepository;

public class CaptureCounter {

    private int captureCount;

    public CaptureCounter(SettingsRepository settingsRepository) {
        this.captureCount = settingsRepository.getCaptureCounter();
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
