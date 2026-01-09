package com.voelkerh.cPast.domain.model;

import java.time.LocalDateTime;

/**
 * Domain model representing a single captured image.
 *
 * <p>A capture combines an {@link Archive}, a file name, an optional note, and the timestamp at which the image was created.</p>
 */
public class Capture {

    private final Archive archive;
    private final String fileName;
    private final String note;
    private final LocalDateTime captureTime;

    public Capture(Archive archive, String fileName, String note) {
        this.archive = archive;
        this.fileName = fileName;
        if (note == null || note.isEmpty()) this.note = "No note taken.";
        else this.note = note;
        this.captureTime = LocalDateTime.now();
    }

    public String getFileName() {
        return fileName;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getCaptureTime() {
        return captureTime;
    }

    public Archive getArchive() {
        return archive;
    }

}
