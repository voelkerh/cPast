package com.voelkerh.cPast.domain.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecordReferenceCreatorTest {

    @Test
    void createBaseReference_underscoreInput() {
        String shortArchiveName = "BArch";
        String recordReference = "DQ1_16416";

        String actual = RecordReferenceCreator.createBaseReference(shortArchiveName, recordReference);
        String expected = "BArch_DQ1_16416";

        assertEquals(expected, actual);
    }

    @Test
    void createBaseReference_slashInput() {
        String shortArchiveName = "BArch";
        String recordReference = "DQ1/16416";

        String actual = RecordReferenceCreator.createBaseReference(shortArchiveName, recordReference);
        String expected = "BArch_DQ1_16416";

        assertEquals(expected, actual);
    }

    @Test
    void createBaseReference_periodInput() {
        String shortArchiveName = "BArch";
        String recordReference = "DQ1.16416";

        String actual = RecordReferenceCreator.createBaseReference(shortArchiveName, recordReference);
        String expected = "BArch_DQ1_16416";

        assertEquals(expected, actual);
    }

    @Test
    void createBaseReference_dashInput() {
        String shortArchiveName = "BArch";
        String recordReference = "DQ1-16416";

        String actual = RecordReferenceCreator.createBaseReference(shortArchiveName, recordReference);
        String expected = "BArch_DQ1_16416";

        assertEquals(expected, actual);
    }

    @Test
    void createBaseReference_doubleUnderscoreInput() {
        String shortArchiveName = "BArch";
        String recordReference = "DQ1__16416";

        String actual = RecordReferenceCreator.createBaseReference(shortArchiveName, recordReference);
        String expected = "BArch_DQ1_16416";

        assertEquals(expected, actual);
    }

    @Test
    void createBaseReference_doubleDashInput() {
        String shortArchiveName = "BArch";
        String recordReference = "DQ1--16416";

        String actual = RecordReferenceCreator.createBaseReference(shortArchiveName, recordReference);
        String expected = "BArch_DQ1_16416";

        assertEquals(expected, actual);
    }

    @Test
    void createBaseReference_emptyArchiveInput() {
        String shortArchiveName = "";
        String recordReference = "DQ1_16416";

        String actual = RecordReferenceCreator.createBaseReference(shortArchiveName, recordReference);
        String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    void createBaseReference_emptyRecordReferenceInput() {
        String shortArchiveName = "BArch";
        String recordReference = "";

        String actual = RecordReferenceCreator.createBaseReference(shortArchiveName, recordReference);
        String expected = "BArch";

        assertEquals(expected, actual);
    }

    @Test
    void addCounterAndFileExtension_validInput() {
        String baseReference = "BArch_DQ1_16416";
        String counter = "3";

        String actual = RecordReferenceCreator.addCounterAndFileExtension(baseReference, counter);
        String expected = "BArch_DQ1_16416_3.jpg";

        assertEquals(expected, actual);
    }

    @Test
    void addCounterAndFileExtension_emptyBaseReference() {
        String baseReference = "";
        String counter = "3";

        String actual = RecordReferenceCreator.addCounterAndFileExtension(baseReference, counter);
        String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    void addCounterAndFileExtension_emptyCounter() {
        String baseReference = "BArch";
        String counter = "";

        String actual = RecordReferenceCreator.addCounterAndFileExtension(baseReference, counter);
        String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    void addCounterAndFileExtension_baseReferenceNull() {
        String baseReference = null;
        String counter = "3";

        String actual = RecordReferenceCreator.addCounterAndFileExtension(baseReference, counter);
        String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    void addCounterAndFileExtension_counterNull() {
        String baseReference = "BArch";
        String counter = null;

        String actual = RecordReferenceCreator.addCounterAndFileExtension(baseReference, counter);
        String expected = "";

        assertEquals(expected, actual);
    }

    @Test
    void addCounterAndFileExtension_invalidSignsInBaseReference() {
        String baseReference = "-BArch*-_DQ1/16416";
        String counter = "3";

        String actual = RecordReferenceCreator.addCounterAndFileExtension(baseReference, counter);
        String expected = "BArch_DQ1_16416_3.jpg";

        assertEquals(expected, actual);
    }
}
