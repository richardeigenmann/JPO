package org.jpo.datamodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2024-2025 Richard Eigenmann.
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


class JpoWriterTest {

    @Test
    void writeCollectionDTD(@TempDir Path tempDir) {
        try {
            final File expectedDtdFile = new File(tempDir.toFile(), "collection.dtd");
            assertThat(expectedDtdFile).doesNotExist();
            JpoWriter.writeCollectionDTDTestOnly(tempDir.toFile());
            assertThat(expectedDtdFile).exists();
            try (final Stream<String> s = Files.lines(expectedDtdFile.toPath())) {
                assertEquals(78, s.count());
            }
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void writeCategoriesBlock() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            final var tempFile = File.createTempFile("temp", null);
            try (final var bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
                final var pictureCollection = new PictureCollection();
                pictureCollection.addCategory("Trees");
                pictureCollection.addCategory("Houses");
                pictureCollection.addCategory("Cats");
                pictureCollection.addCategory("Dogs");
                JpoWriter.writeCategoriesBlockTestOnly(pictureCollection, bout);
            }
            try (final Stream<String> s = Files.lines(tempFile.toPath())) {
                assertEquals(14, s.count());
            }
            Files.delete(tempFile.toPath());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void writeXmlHeader() {
        try {
            final var tempFile = File.createTempFile("temp", null);
            try (final var bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
                JpoWriter.writeXmlHeaderTestOnly(bout);
            }
            try (final Stream<String> s = Files.lines(tempFile.toPath())) {
                assertEquals(1, s.count());
            }
            Files.delete(tempFile.toPath());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void writePicture() {
        try {
            final var tempFile = File.createTempFile("temp", null);
            try (final var bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
                final var pictureInfo = new PictureInfo();
                pictureInfo.setDescription("A Test image");
                pictureInfo.setFilmReference("Film Reference");
                pictureInfo.setImageLocation(new File("gaga.jpg"));
                pictureInfo.setCopyrightHolder("Richard Eigenmann");
                pictureInfo.setComment("A Comment");
                pictureInfo.setPhotographer("A master");
                JpoWriter.writePictureTestOnly(pictureInfo, bout, null, false, Paths.get(""));
            }

            try(final var lines = Files.lines(tempFile.toPath())) {
                assertEquals(8, lines.count());
            }
            Files.delete(tempFile.toPath());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test dumpToXml
     */
    @Test
    void testDumpToXmlNormalNodeProtected() {
        final var groupInfo = new GroupInfo("Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign");

        final var stringWriter = new StringWriter();
        try (
                final var bufferedWriter = new BufferedWriter(stringWriter)) {
            JpoWriter.dumpToXml(groupInfo, bufferedWriter, false, true);
        } catch (final IOException ex) {
            Logger.getLogger(GroupInfoTest.class.getName()).log(Level.SEVERE, "The dumpToXml should really not throw an IOException", ex);
            fail("Unexpected IOException");
        }

        final String newline = System. lineSeparator();
        final var expected = "<group group_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\">" + newline;

        assertEquals(expected, stringWriter.toString());
    }

    /**
     * Test dumpToXml
     */
    @Test
    void testWriteCollectionHeader() {
        final var groupInfo = new GroupInfo("Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign");
        final var node = new SortableDefaultMutableTreeNode(groupInfo);
        final var pictureCollection = new PictureCollection();
        pictureCollection.getRootNode().add(node);
        pictureCollection.setAllowEdits(true);

        final var stringWriter = new StringWriter();
        try (
                final var bufferedWriter = new BufferedWriter(stringWriter)) {
            JpoWriter.writeCollectionHeaderTestOnly(node, bufferedWriter, Paths.get("C:/User/Tom/Pictures"));
        } catch (final IOException ex) {
            Logger.getLogger(GroupInfoTest.class.getName()).log(Level.SEVERE, "The dumpToXml should really not throw an IOException", ex);
            fail("Unexpected IOException");
        }

        final String newline = System. lineSeparator();
        final var expected = "<collection collection_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\" collection_created=\""
                + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() )
                + "\" collection_protected=\"No\" basedir=\"C:" + File.separator + "User" + File.separator + "Tom" + File.separator + "Pictures\">" + newline;

        assertEquals( expected, stringWriter.toString() );
    }


    /**
     * Test dumpToXml
     */
    @Test
    void testWriteCollectionHeaderProtectedCollection() {
        final var groupInfo = new GroupInfo("Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign");
        final var node = new SortableDefaultMutableTreeNode(groupInfo);
        final var pictureCollection = new PictureCollection();
        pictureCollection.getRootNode().add(node);
        pictureCollection.setAllowEdits(false);

        final var stringWriter = new StringWriter();
        try (
                final var bufferedWriter = new BufferedWriter(stringWriter)) {
            JpoWriter.writeCollectionHeaderTestOnly(node, bufferedWriter, Paths.get("/linux/filesystem/pictures"));
        } catch (final IOException ex) {
            Logger.getLogger(GroupInfoTest.class.getName()).log(Level.SEVERE, "The dumpToXml should really not throw an IOException", ex);
            fail(ex.getMessage());
        }

        final String newline = System.lineSeparator();
        final var expected = "<collection collection_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\" collection_created=\""
                + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() )
                + "\" collection_protected=\"Yes\" basedir=\"" + File.separator + "linux" + File.separator + "filesystem" + File.separator + "pictures\">" + newline;

        assertEquals( expected, stringWriter.toString());
    }

}