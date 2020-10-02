package org.jpo.cache;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

/*
 Copyright (C) 2017-2020  Richard Eigenmann.
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
 * @author Richard Eigenmann
 */
public class ImageBytesTest {


    @Test
    public void constructorTest() {
        byte[] sourceBytes = "Any String you want".getBytes();
        final ImageBytes ib = new ImageBytes(sourceBytes);
        byte[] bytes = ib.getBytes();
        assertArrayEquals(sourceBytes, bytes);
    }

    @Test
    public void testSerializable() {
        byte[] sourceBytes = "Any String you want".getBytes();
        final ImageBytes ib = new ImageBytes(sourceBytes);
        try {
            final File tempFile = File.createTempFile("testSerializable", ".jpg");
            try (final FileOutputStream file = new FileOutputStream(tempFile);
                 final ObjectOutputStream out = new ObjectOutputStream(file);
            ) {
                out.writeObject(ib);
            } catch (final IOException e) {
                fail(e.getMessage());
            }

            try (final FileInputStream file = new FileInputStream(tempFile);
                 final ObjectInputStream in = new ObjectInputStream(file);
            ) {
                // Method for deserialization of object
                final ImageBytes ib2 = (ImageBytes) in.readObject();
                assertArrayEquals(ib.getBytes(), ib2.getBytes());
            } catch (final IOException | ClassNotFoundException ex) {
                fail(ex.getMessage());
            }
        } catch (final IOException e) {
            fail(e.getMessage());
        }


    }
}
