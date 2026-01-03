package com.voelkerh.cPast.domain;

public class RecordReferenceCreator {

    public static String createBaseReference(String strArchiveShort, String strRecordReference) {
        StringBuilder sb = new StringBuilder(strArchiveShort);
        if (!strRecordReference.isEmpty()) {
            sb.append('_').append(strRecordReference);
        }

        return sb.toString()
                .replace(" ", "")
                .replace("//", "/")
                .replace("/", "_")
                .replaceAll("[!@#$%^&*]", "_")
                .replaceAll("\\\\\\\\", "\\\\")
                .replaceAll("\\\\", "_")
                .replaceAll("_{2,}", "_");
    }

    public static String addCounterAndFileExtension(String baseReference, String strCounter) {
        StringBuilder sb = new StringBuilder(baseReference);
        if (!strCounter.isEmpty() && !baseReference.isEmpty()) {
            sb.append('_').append(strCounter);
        }
        sb.append(".jpg");

        return sb.toString()
                .replace(" ", "")
                .replace("//", "/")
                .replace("/", "_")
                .replaceAll("[!@#$%^&*]", "_")
                .replaceAll("\\\\\\\\", "\\\\")
                .replaceAll("\\\\", "_")
                .replaceAll("_{2,}", "_");
    }

}
