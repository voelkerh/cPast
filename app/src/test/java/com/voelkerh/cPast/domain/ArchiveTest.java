package com.voelkerh.cPast.domain;

import com.voelkerh.cPast.domain.model.Archive;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ArchiveTest {

    @Test
    void archive_constructorNotNull(){
        Archive archive = new Archive("Bundesarchiv", "BArch");

        assertNotNull(archive);
    }

    @Test
    void getFullName_returnsFullName() {
        Archive archive = new Archive("Bundesarchiv", "BArch");

        String expected = "Bundesarchiv";
        String actual = archive.getFullName();

        assertEquals(expected, actual);
    }

    @Test
    void getShortName_returnsShortName() {
        Archive archive = new Archive("Bundesarchiv", "BArch");

        String expected = "BArch";
        String actual = archive.getShortName();

        assertEquals(expected, actual);
    }

    @Test
    void setFullName_returnsTrue() {
        Archive archive = new Archive("Bundesarchiv", "BArch");

        boolean actual =archive.setFullName("Landesarchiv");

        assertTrue(actual);
    }

    @Test
    void setFullName_returnsFalseOnNull() {
        Archive archive = new Archive("Bundesarchiv", "BArch");

        boolean actual =archive.setFullName(null);

        assertFalse(actual);
    }

    @Test
    void setFullName_returnsFalseOnEmptyString() {
        Archive archive = new Archive("Bundesarchiv", "BArch");

        boolean actual =archive.setFullName("");

        assertFalse(actual);
    }

    @Test
    void setShortName_returnsTrue() {
        Archive archive = new Archive("Bundesarchiv", "BArch");

        boolean actual =archive.setShortName("LAB");

        assertTrue(actual);
    }

    @Test
    void setShortName_returnsFalseOnNull() {
        Archive archive = new Archive("Bundesarchiv", "BArch");

        boolean actual =archive.setShortName(null);

        assertFalse(actual);
    }

    @Test
    void setShortName_returnsFalseOnEmptyString() {
        Archive archive = new Archive("Bundesarchiv", "BArch");

        boolean actual =archive.setShortName("");

        assertFalse(actual);
    }
}
