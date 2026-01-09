package com.voelkerh.cPast.domain.service;

/**
 * Domain service responsible for generating normalized file name references from archive identifiers and record signatures.
 *
 * <p>This class provides utility methods to construct base record references and final image file names.
 * It applies a consistent naming scheme and sanitizes the result to ensure filesystem-safe strings.</p>
 */
public class RecordReferenceCreator {

    /**
     * Creates a normalized base reference from an archive short name and a record reference.
     *
     * <p>The resulting base reference is constructed by concatenating the archive
     * short name and the record reference (if present) using an underscore. The
     * result is then sanitized by removing whitespace, replacing path separators
     * and special characters, and collapsing multiple underscores.</p>
     *
     * @param strArchiveShort short identifier of the archive
     * @param strRecordReference record-specific reference or signature
     * @return sanitized base reference suitable for use in file names
     */
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

    /**
     * Creates a final image file name by appending a numeric counter and file extension.
     *
     * <p>The method appends the given counter to the base reference using an
     * underscore (if both are non-empty) and adds the {@code .jpg} file extension.
     * The resulting string is sanitized to ensure a consistent and filesystem-safe file name.</p>
     *
     * @param baseReference base record reference
     * @param strCounter numeric counter to distinguish multiple images of the same record
     * @return sanitized image file name with counter and {@code .jpg} extension
     */
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
