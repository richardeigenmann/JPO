package org.jpo.cache;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

/*
 Copyright (C) 2017-2023 Richard Eigenmann.
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


/**
 * @author Richard Eigenmann
 */
class ImageBytesTest {


    @Test
    void constructorTest() {
        final var sourceBytes = "Any String you want".getBytes();
        final var imageBytes = new ImageBytes(sourceBytes);
        final var bytes = imageBytes.getBytes();
        assertArrayEquals(sourceBytes, bytes);
    }

    @Test
    void testSerializable() {
        final var sourceBytes = "Any String you want".getBytes();
        final var imageBytes1 = new ImageBytes(sourceBytes);
        try {
            final var tempFile = File.createTempFile("testSerializable", ".jpg");
            try (final var fileOutputStream = new FileOutputStream(tempFile);
                 final var objectOutputStream = new ObjectOutputStream(fileOutputStream)
            ) {
                objectOutputStream.writeObject(imageBytes1);
            } catch (final IOException e) {
                Files.delete(tempFile.toPath());
                fail(e.getMessage());
            }

            try (final var fileInputStream = new FileInputStream(tempFile);
                 final var objectInputStream = new ObjectInputStream(fileInputStream)
            ) {
                // Method for deserialization of object
                final var imageBytes = (ImageBytes) objectInputStream.readObject();
                assertArrayEquals(imageBytes1.getBytes(), imageBytes.getBytes());
            } catch (final IOException | ClassNotFoundException ex) {
                fail(ex.getMessage());
            } finally {
                Files.delete(tempFile.toPath());
            }
        } catch (final IOException e) {
            fail(e.getMessage());
        }


    }
}
