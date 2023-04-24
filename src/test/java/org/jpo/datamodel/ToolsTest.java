package org.jpo.datamodel;

import org.jpo.gui.swing.EdtViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2017-2022  Richard Eigenmann.
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
 * Tests for the Tools class
 *
 * @author Richard Eigenmann
 */
class ToolsTest {

    /**
     * Test of parseDate
     */
    @ParameterizedTest
    @CsvSource({
            "2017:01:28 12:26:04,   2017-01-28 12:26:04",
            "2017:01:28 11:51,      2017-01-28 11:51:00",
            "2017:01:28,            2017-01-28 00:00:00",
            "17:01:28,              2017-01-28 00:00:00",
            "15.01.2017, 2017-01-15 00:00:00",  // German
            "15.01.2017 18:11, 2017-01-15 18:11:00",  // German with Time
            "15.01.2017 18:11:33, 2017-01-15 18:11:33", // German with Seconds
            "9/11/2001, 2001-09-11 00:00:00",  // American
            "12/11/2001, 2001-12-11 00:00:00",  // American
            "9/11/01, 2001-09-11 00:00:00",  // American
            "9/11/2001 08:46, 2001-09-11 08:46:00",  // American with Time
            "9/11/01 08:46, 2001-09-11 08:46:00",  // American with Time
            "9/11/2001 08:46:22, 2001-09-11 08:46:22",  // American with Time
            "9/11/01 08:46:22, 2001-09-11 08:46:22",  // American with Time
            "2021, 2021-01-01 00:00:00",
            "20210314, 2021-03-14 00:00:00",
            "02.2021, 2021-02-01 00:00:00",
            "02-2021, 2021-02-01 00:00:00",
            "15-2-2021, 2021-02-15 00:00:00",
            "15.02.2021, 2021-02-15 00:00:00",
            "15.2.2021, 2021-02-15 00:00:00",
            "2.4.1978, 1978-04-02 00:00:00",
            "15.02.21, 2021-02-15 00:00:00",
            "15 Feb 21, 2021-02-15 00:00:00",
            "15 Feb 2021, 2021-02-15 00:00:00",
            "15-02-21, 2021-02-15 00:00:00",
            "2021-02-26, 2021-02-26 00:00:00",
            "2021-02-26 22:09:30, 2021-02-26 22:09:30",
            "2021-02-26 22.09.30, 2021-02-26 22:09:30",
            "2021-02-26 at 22.09.30, 2021-02-26 22:09:30" // found on a WhatsApp image
    })
    void testParseDateTime(final String d, final String expected) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String result = format.format(Objects.requireNonNull(Tools.parseDate(d)).getTime());
        assertEquals(expected, result);
    }

    /**
     * Constructor for the Tools Test class
     */
    @Test
    void testCheckEDTnotOnEDT() {
        // if not on EDT must throw Error
        final boolean[] errorThrown = {false};
        final Thread t = new Thread(() -> {
            try {
                Tools.checkEDT();
            } catch (final EdtViolationException ex) {
                errorThrown[0] = true;
            }
        });
        t.start();
        try {
            t.join();
            assertTrue(errorThrown[0]);
        } catch (final InterruptedException ex) {
            fail("EDT violation not thrown");
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Test that an error is thrown when we are on the EDT and call the checkEDT
     * method
     */
    @Test
    void testCheckEDTOnEDT() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        // if on EDT must not throw Error
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    Tools.checkEDT();
                } catch (final EdtViolationException ex) {
                    fail("An EDT violation should not have been thrown!");
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            fail("Something went wrong with the EDT thread test");
            Thread.currentThread().interrupt();
        }
    }


    private static final String TEST_IMAGE_FILENAME = "gaga.jpg";

    @Test
    void testInventFilename() {
        try {
            final Path tempDirWithPrefix = Files.createTempDirectory("testInventFilename");
            final File f = Tools.inventFilename(tempDirWithPrefix.toFile(), TEST_IMAGE_FILENAME);
            assertEquals(TEST_IMAGE_FILENAME, f.getName());
            Files.delete(tempDirWithPrefix);
        } catch (final IOException e) {
            fail("Could not run test testInventFilename");
        }
    }

    @Test
    void testInventFilenameExists() {
        try {
            final Path tempDirWithPrefix = Files.createTempDirectory("testInventFilenameExists");
            final File fExists = new File(tempDirWithPrefix.toFile(), TEST_IMAGE_FILENAME);
            new FileOutputStream(fExists).close();
            final File f = Tools.inventFilename(tempDirWithPrefix.toFile(), TEST_IMAGE_FILENAME);
            assertEquals("gaga_1.jpg", f.getName());
            Files.delete(fExists.toPath());
            Files.delete(tempDirWithPrefix);
        } catch (final IOException e) {
            fail("Could not run test testInventFilenameExists");
        }
    }

    @Test
    void testInventFilenameExists50() {
        try {
            final Path tempDirWithPrefix = Files.createTempDirectory("testInventFilenameExists50");
            final File fExists = new File(tempDirWithPrefix.toFile(), TEST_IMAGE_FILENAME);
            new FileOutputStream(fExists).close();
            final File[] existingFiles = new File[50];
            for (int i = 0; i < 50; i++) {
                final File fExists50 = new File(tempDirWithPrefix.toFile(), "gaga_" + i + ".jpg");
                new FileOutputStream(fExists50).close();
                existingFiles[i] = fExists50;
            }

            final File f = Tools.inventFilename(tempDirWithPrefix.toFile(), TEST_IMAGE_FILENAME);

            assertEquals(19, f.getName().length());

            Files.delete(fExists.toPath());
            for (int i = 0; i < 50; i++) {
                Files.delete(existingFiles[i].toPath());
            }
            Files.delete(tempDirWithPrefix);
        } catch (final IOException e) {
            fail("Could not run test testInventFilenameExists50");
        }
    }


}
