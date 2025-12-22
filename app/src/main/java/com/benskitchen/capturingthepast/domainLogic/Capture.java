package com.benskitchen.capturingthepast.domainLogic;

public class Capture {

    private final String fileName;
    private final String note;

    public Capture(String fileName, String note) {
        this.fileName = fileName;
        if (note == null || note.isEmpty()) this.note = "No note taken.";
        else this.note = note;
    }

    public String getFileName() {
        return fileName;
    }

    public String getNote() {
        return note;
    }

}
