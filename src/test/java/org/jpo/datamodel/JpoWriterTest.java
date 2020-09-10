package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class JpoWriterTest {

    @Test
    public void writeCollectionDTD() {
        try {
            final Path tempDirWithPrefix = Files.createTempDirectory("TestJpoWriter");
            final File tempDir = tempDirWithPrefix.toFile();
            final File expectedDtdFile = new File(tempDir, "collection.dtd");
            assertFalse( expectedDtdFile.exists() );
            JpoWriter.writeCollectionDTDTestOnly(tempDirWithPrefix.toFile());
            assertTrue( expectedDtdFile.exists() );
            try (final Stream<String> s = Files.lines(expectedDtdFile.toPath())) {
                assertEquals(80, s.count());
            }
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void writeCategoriesBlock() {
        try  {
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
    public void writeXmlHeader() {
        try  {
            final File tempFile = File.createTempFile("temp", null);
            try (final BufferedWriter bout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
                JpoWriter.writeXmlHeaderTestOnly(bout);
            }
            try (final Stream<String> s = Files.lines(tempFile.toPath())) {
                assertEquals(2, s.count());
            }
            Files.delete(tempFile.toPath());
        } catch (final IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void writePicture() {
        try  {
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

}