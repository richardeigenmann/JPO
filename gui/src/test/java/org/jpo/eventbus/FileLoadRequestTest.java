package org.jpo.eventbus;

import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.Tools;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
Copyright (C) 2023-2026 Richard Eigenmann.
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
    void makeFileLoadRequest() throws IOException {
        final var pictureCollection = new PictureCollection();
        var imageFile = Tools.copyResourceToTempFile("/exif-test-canon-eos-350d.jpg");
        new FileLoadRequest(pictureCollection, imageFile);
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
    void makeFileLoadRequestUnreadableFile() throws IOException {
        assumeFalse(System.getProperty("os.name").toLowerCase().startsWith("win"));

        final var pictureCollection = new PictureCollection();
        Path unreadableFilePath = Files.createTempFile("unreadableFile", ".xml");
        Files.writeString(unreadableFilePath, "Some random text", StandardCharsets.UTF_8);

        Files.setPosixFilePermissions(unreadableFilePath, PosixFilePermissions.fromString("---------"));
        final var unreadableFile = unreadableFilePath.toFile();
        try {
            new FileLoadRequest(pictureCollection, unreadableFile);
            fail(AN_ILLEGAL_ARGUMENT_EXCEPTION_WAS_SUPPOSED_TO_BE_THROWN);
        } catch (final IllegalArgumentException e) {
            assertEquals("File \"" + unreadableFilePath + "\" must be readable for FileLoadRequest!", e.getMessage());
        } finally {
            Files.delete(unreadableFilePath);
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