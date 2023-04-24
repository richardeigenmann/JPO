package org.jpo.datamodel;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/*
 Copyright (C) 2023 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 *
 * @author Richard Eigenmann
 */
class SaxEventHandlerTest {

    /**
     * Jpo uses the dtd file in the classpath. As this can go missing if the
     * build is poor this unit test checks whether it is there
     */
    @Test
    void testGetCollectionDtdInputSource() {
        try {
            final var inputSource = SaxEventHandler.getCollectionDtdInputSource();
            assertNotNull(inputSource);

            try (final var inputSourceByteStream = inputSource.getByteStream()) {
                final var dtdDocument = IOUtils.toString(inputSourceByteStream, StandardCharsets.UTF_8);
                assert (dtdDocument.contains("collection"));
            }

        } catch (IOException e) {
            fail("Could not find collection.dtd file: " + e.getMessage());
        }

    }

}
