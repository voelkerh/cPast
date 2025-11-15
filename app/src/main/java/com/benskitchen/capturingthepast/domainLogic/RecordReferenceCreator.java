package com.benskitchen.capturingthepast.domainLogic;

public class RecordReferenceCreator {
    public static String createRecordReference(String strArchiveShort, String strRecordReference, String strCounter) {
        StringBuilder sb = new StringBuilder(strArchiveShort);
        if (!strRecordReference.isEmpty()){
            sb.append('_').append(strRecordReference);
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
