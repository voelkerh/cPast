package com.benskitchen.capturingthepast.domainLogic;

import java.util.Locale;

public class CatRefCreator {
    public static String createCatRef(String strArchon, String strRef) {
        // Build reference
        StringBuilder sb = new StringBuilder(strArchon);
        if (!strRef.isEmpty()) sb.append('/').append(strRef);

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
