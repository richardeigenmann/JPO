package org.jpo.eventbus;

import org.jpo.datamodel.PictureCollection;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
Copyright (C) 2023-2025 Richard Eigenmann.
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


class FileLoadRequestTest {

    public static final String AN_ILLEGAL_ARGUMENT_EXCEPTION_WAS_SUPPOSED_TO_BE_THROWN = "An IllegalArgumentException was supposed to be thrown";

    @Test
    void makeFileLoadRequest() {
        final var pictureCollection = new PictureCollection();
        try {
            final var existingFile = new File(Objects.requireNonNull(FileLoadRequestTest.class.getClassLoader().getResource("exif-test-canon-eos-350d.jpg")).toURI());
            new FileLoadRequest(pictureCollection, existingFile);
        } catch (final IllegalArgumentException e) {
            fail("There wasn't supposed to be an IllegalArgumentException in this test. Exception reads: " + e.getMessage());
        } catch (final URISyntaxException _) {
            fail("Test was supposed to create a request for a file that exists");
        }
    }

    @Test
    void makeFileLoadRequestInexistantFile() {
        final var pictureCollection = new PictureCollection();

        final var inexistantFile = Paths.get("no_such_file.txt").toFile();
        try {
            new FileLoadRequest(pictureCollection, inexistantFile);
            fail(AN_ILLEGAL_ARGUMENT_EXCEPTION_WAS_SUPPOSED_TO_BE_THROWN);
        } catch (final IllegalArgumentException e) {
            assertEquals("File \"no_such_file.txt\" must exist before we can load it!", e.getMessage());
        }
    }

    /**
     * Apparently on Windows you can't set the permissions of a file so that you can't read it so this is
     * a Linux only test.
     */
    @Test
    void makeFileLoadRequestUnreadableFile() {
        assumeFalse(System.getProperty("os.name").toLowerCase().startsWith("win"));

        final var pictureCollection = new PictureCollection();
        Path unreadableFilePath = null;
        try {
            unreadableFilePath = Files.createTempFile("unreadableFile", ".xml");
            Files.writeString(unreadableFilePath, "Some random text", StandardCharsets.UTF_8);

            Files.setPosixFilePermissions(unreadableFilePath, PosixFilePermissions.fromString("---------"));
        } catch (IOException e) {
            fail("Something went wrong in the test: " + e.getMessage());
        }
        final var unreadableFile = unreadableFilePath.toFile();
        try {
            new FileLoadRequest(pictureCollection, unreadableFile);
            fail(AN_ILLEGAL_ARGUMENT_EXCEPTION_WAS_SUPPOSED_TO_BE_THROWN);
        } catch (final IllegalArgumentException e) {
            assertEquals("File \"" + unreadableFilePath + "\" must be readable for FileLoadRequest!", e.getMessage());
        } finally {
            try {
                Files.delete(unreadableFilePath);
            } catch (final IOException e) {
                fail("Could no clean up from test: " + e.getMessage());
            }
        }
    }

    @Test
    void makeFileLoadRequestOnDirectory() {
        final var pictureCollection = new PictureCollection();
        final var directory = new File(".");
        try {
            new FileLoadRequest(pictureCollection, directory);
            fail(AN_ILLEGAL_ARGUMENT_EXCEPTION_WAS_SUPPOSED_TO_BE_THROWN);
        } catch (final IllegalArgumentException e) {
            assertEquals("\".\" is a directory. FileLoadRequest can only handle actual files.", e.getMessage());
        }
    }


}