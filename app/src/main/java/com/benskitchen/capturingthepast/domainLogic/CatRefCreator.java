package com.benskitchen.capturingthepast.domainLogic;

import java.util.Locale;

public class CatRefCreator {
    public static String createCatRef(String strArchon, String strRef, String strItem, String strSubItem, String strPart) {
        // Build reference
        StringBuilder sb = new StringBuilder(strArchon);
        if (!strRef.isEmpty()) sb.append('/').append(strRef);
        if (!strItem.isEmpty()) sb.append('/').append(strItem);
        if (!strSubItem.isEmpty()) sb.append('/').append(strSubItem);
        if (!strPart.isEmpty()) sb.append('/').append(strPart);

        // Clean reference
        String catRef = sb.toString().replace(" ", "")
                .toUpperCase(Locale.ROOT)
                .replace("//", "/")
                .replace("/", "_")
                .replaceAll("[!@#$%^&*]", "_")
                .replaceAll("\\\\\\\\", "\\\\")
                .replaceAll("\\\\", "_")
                .replaceAll("_{2,}", "_");

        if (catRef.equals("GB0000")) catRef = "Ref";

        return catRef;
    }

}
