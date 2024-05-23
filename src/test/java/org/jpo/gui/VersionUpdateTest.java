package org.jpo.gui;

import org.jpo.datamodel.Settings;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/*
 Copyright (C) 2023-2024 Richard Eigenmann, Zurich, Switzerland
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */


class VersionUpdateTest {

    @Test
    void testGetLatestJpoVersion() {
        try {
            final String latestVersion = VersionUpdate.getLatestJpoVersionTestOnly();
            assertNotNull(latestVersion);
            assertTrue(Float.parseFloat(latestVersion) > 0.14);
        } catch (final IOException | URISyntaxException e) {
            fail(String.format("IOException instead of latest version retrieved from %s : %s", Settings.JPO_VERSION_URL, e.getMessage()));
        }
    }

}
