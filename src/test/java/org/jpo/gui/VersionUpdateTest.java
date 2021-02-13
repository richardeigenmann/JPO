package org.jpo.gui;

import org.jpo.datamodel.Settings;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class VersionUpdateTest {

    @Test
    void testGetLatestJpoVersion() {
        try {
            final String latestVersion = VersionUpdate.getLatestJpoVersionTestOnly();
            assertNotNull(latestVersion);
            assertTrue(Float.parseFloat(latestVersion) > 0.14);
        } catch (final IOException e) {
            fail(String.format("IOException instead of latest version retrieved from %s : %s", Settings.JPO_VERSION_URL, e.getMessage()));
        }
    }

}