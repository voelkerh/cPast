package com.voelkerh.cPast.domain;

public class Archive {

    private String fullName;
    private String shortName;

    public Archive(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public boolean setFullName(String fullName) {
        if (fullName == null || fullName.isEmpty()) return false;
        this.fullName = fullName;
        return true;
    }

    public boolean setShortName(String shortName) {
        if (shortName == null || shortName.isEmpty()) return false;
        this.shortName = shortName;
        return true;
    }

    @Override
    public String toString() {
        return fullName + " - " + shortName;
    }
}
