package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

class JpoWriterTest {

    @Test
    void writeCollectionDTD() {
        try {
            final Path tempDirWithPrefix = Files.createTempDirectory("TestJpoWriter");
            final File tempDir = tempDirWithPrefix.toFile();
            final File expectedDtdFile = new File(tempDir, "collection.dtd");
            assertFalse(expectedDtdFile.exists());
            JpoWriter.writeCollectionDTDTestOnly(tempDirWithPrefix.toFile());
            assertTrue(expectedDtdFile.exists());
            try (final Stream<String> s = Files.lines(expectedDtdFile.toPath())) {
                assertEquals(78, s.count());
            }
            Files.delete(expectedDtdFile.toPath());
            Files.delete(tempDirWithPrefix);
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void writeCategoriesBlock() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            final File tempFile = File.createTempFile("temp", null);
            try (final BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
                final PictureCollection pictureCollection = new PictureCollection();
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
            final File tempFile = File.createTempFile("temp", null);
            try (final BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
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
            final File tempFile = File.createTempFile("temp", null);
            try (final BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
                final PictureInfo pictureInfo = new PictureInfo();
                pictureInfo.setDescription("A Test image");
                pictureInfo.setFilmReference("Film Reference");
                pictureInfo.setImageLocation(new File("gaga.jpg"));
                pictureInfo.setCopyrightHolder("Richard Eigenmann");
                pictureInfo.setComment("A Commment");
                pictureInfo.setPhotographer("A master");
                JpoWriter.writePictureTestOnly(pictureInfo, bout, null, false);
            }

            try(final Stream<String> s = Files.lines(tempFile.toPath())) {
                assertEquals(8, s.count());
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
    void testWriteCollectionHeader() {
        final var groupInfo = new GroupInfo("Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign");
        final var node = new SortableDefaultMutableTreeNode(groupInfo);
        //final var pictureCollection = new PictureCollection();
        final var pictureCollection = Settings.getPictureCollection();
        pictureCollection.getRootNode().add(node);
        pictureCollection.setAllowEdits(true);

        final var stringWriter = new StringWriter();
        try (
                final var bufferedWriter = new BufferedWriter(stringWriter)) {
            JpoWriter.writeCollectionHeaderTestOnly(node, bufferedWriter);
        } catch (final IOException ex) {
            Logger.getLogger(GroupInfoTest.class.getName()).log(Level.SEVERE, "The dumpToXml should really not throw an IOException", ex);
            fail("Unexpected IOException");
        }

        final var expected = "<collection collection_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\" collection_created=\""
                + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() )
                + "\" collection_protected=\"No\" basedir=\"\">\n";

        assertEquals( expected, stringWriter.toString() );
    }


    /**
     * Test dumpToXml
     */
    @Test
    void testWriteCollectionHeaderProtectedCollection() {
        final var groupInfo = new GroupInfo("Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign");
        final var node = new SortableDefaultMutableTreeNode(groupInfo);
        //final var pictureCollection = new PictureCollection();
        final var pictureCollection = Settings.getPictureCollection();
        pictureCollection.getRootNode().add(node);
        pictureCollection.setAllowEdits(false);

        final var stringWriter = new StringWriter();
        try (
                final var bufferedWriter = new BufferedWriter(stringWriter)) {
            JpoWriter.writeCollectionHeaderTestOnly(node, bufferedWriter);
        } catch (final IOException ex) {
            Logger.getLogger(GroupInfoTest.class.getName()).log(Level.SEVERE, "The dumpToXml should really not throw an IOException", ex);
            fail(ex.getMessage());
        }

        final var expected = "<collection collection_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\" collection_created=\""
                + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() )
                + "\" collection_protected=\"Yes\" basedir=\"\">\n";

        assertEquals( expected, stringWriter.toString());
    }

}