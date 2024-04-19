package org.jpo.gui;

import org.apache.commons.compress.utils.IOUtils;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2016-2024 Richard Eigenmann.
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
class ConsolidateGroupWorkerTest {

    private static final String TEMP_IMAGE_FILENAME = "Image1.jpg";
    private static final String NIKON_D100_JPG = "exif-test-nikon-d100-1.jpg";
    private static final String UNEXPECTED_IOEXCEPTION = "Unexpected IOException: ";
    public static final String THE_INPUT_STREAM_OF_THE_IMAGE_MUST_NOT_BE_NULL = "The input stream of the image must not be null!";
    public static final String FAILED_TO_CREATE_TEST_IMAGE_FILE = "Failed to create test image file: ";

    /**
     * Show that a null image file doesn't need to be moved.
     */
    @Test
    void testNeedToMovePictureNull(@TempDir Path tempDir) {
        final var pictureInfo = new PictureInfo();
        try {
            ConsolidateGroupWorker.needToMovePicture(pictureInfo, tempDir);
            fail("the needToMovePicture should not handle null inputs; the are invalid");
        } catch (final NullPointerException ex) {
            // this is good
        }
    }

    /**
     * Show that an image that doesn't exist doesn't need to be moved.
     */
    @Test
    void testNeedToMoveNonexistentPicture(@TempDir Path tempDirSrc, @TempDir Path tempDirTgt) {
        final var sourceImageFile = new File(tempDirSrc.toFile(), TEMP_IMAGE_FILENAME);
        // Java File object exists but not on the disk

        final var pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation(sourceImageFile);

        // Based on the info in the filenames the picture would need to be moved
        assertTrue(ConsolidateGroupWorker.needToMovePicture(pictureInfo, tempDirTgt.toFile()));
    }

    /**
     * Test need to move a picture to the same directory returns false
     */
    @Test
    void testNeedToMovePictureSameDirectory(@TempDir Path tempDir) {
        final var sourceImageFile = new File(tempDir.toFile(), TEMP_IMAGE_FILENAME);
        sourceImageFile.deleteOnExit();

        try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
             final var fileOutputStream = new FileOutputStream(sourceImageFile)) {
            Objects.requireNonNull(inputStream, THE_INPUT_STREAM_OF_THE_IMAGE_MUST_NOT_BE_NULL);
            IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
        } catch (final IOException ex) {
            fail(FAILED_TO_CREATE_TEST_IMAGE_FILE + ex.getMessage());
        }
        // test that is really exists
        assertTrue(sourceImageFile.canRead());

        final var pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation(sourceImageFile);

        // Consolidation of a PictureInfo to the same directory should return false as nothing was moved
        assertFalse(ConsolidateGroupWorker.needToMovePicture(pictureInfo, tempDir.toFile()));
    }

    /**
     * Test need to Move a picture to a new directory
     */
    @Test
    void testNeedToMovePictureNewDirectory(@TempDir Path tempDirSrc, @TempDir Path tempDirTgt) {
        final var imageFile = new File(tempDirSrc.toFile(), TEMP_IMAGE_FILENAME);

        try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
             final var fileOutputStream = new FileOutputStream(imageFile)) {
            Objects.requireNonNull(inputStream, THE_INPUT_STREAM_OF_THE_IMAGE_MUST_NOT_BE_NULL);
            IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
        } catch (final IOException ex) {
            fail(FAILED_TO_CREATE_TEST_IMAGE_FILE + ex.getMessage());
        }
        // test that is really exists
        assertTrue(imageFile.canRead());

        final var pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation(imageFile);

        // Consolidation of a PictureInfo to a new directory should succeed
        assertTrue(ConsolidateGroupWorker.needToMovePicture(pictureInfo, tempDirTgt.toFile()));

    }

    /**
     * Test if we would need to move a picture to a new directory or if it can
     * stay in the place it was.
     *
     * @see <a href="http://stackoverflow.com/questions/28366433/file-canwrite-and-files-iswritable-not-giving-correct-value-on-linux">Stackoverflow</a>
     */
    @Test
    void testNeedToMoveReadonlyPicture(@TempDir Path tempDirSrc) {
        // This test doesn't work on CI platforms where the user is root as root can always write to a file
        assumeFalse(System.getProperty("user.name").equals("root"));
        final var sourceImageFile = new File(tempDirSrc.toFile(), "ReadOnlyImage.jpg");

        try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
             final var fileOutputStream = new FileOutputStream(sourceImageFile)) {
            Objects.requireNonNull(inputStream, THE_INPUT_STREAM_OF_THE_IMAGE_MUST_NOT_BE_NULL);
            IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
        } catch (final IOException ex) {
            fail(FAILED_TO_CREATE_TEST_IMAGE_FILE + ex.getMessage());
        }
        assertTrue(sourceImageFile.setReadOnly());
        assertTrue(sourceImageFile.canRead());

        final var pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation(sourceImageFile);

        final var tempDirTgt = new File(tempDirSrc.toFile(), "subdir");

        // Consolidation of a readonly PictureInfo to a new directory should return true
        assertTrue(ConsolidateGroupWorker.needToMovePicture(pictureInfo, tempDirTgt));

        assertTrue(sourceImageFile.setWritable(true));
    }

    /**
     * Show that consolidation of a PictureInfo with a null file succeeds
     * because they can't be moved.
     */
    @Test
    void testMovePictureNull(@TempDir Path tempDir) {
        try {
            final var returnCode = ConsolidateGroupWorker.movePicture(new PictureInfo(), tempDir.toFile());
            // Consolidation of a PictureInfo with a \"null\" highres file should return false
            assertFalse(returnCode);
        } catch (final NullPointerException ex) {
            return;
        }
        fail("Consolidation of a PictureInfo with a \"null\" highres file should throw a NPE");
    }

    /**
     * Move a picture to the same directory and verify that it stays in the same
     * place.
     */
    @Test
    void testMovePictureSameDirectory(@TempDir Path tempDir) {
        // we need to have a picture collection so that the search for other nodes can proceed
        final var pictureCollection = new PictureCollection();
        Settings.setPictureCollection(pictureCollection);

        final var sourceImageFile = new File(tempDir.toFile(), TEMP_IMAGE_FILENAME);
        sourceImageFile.deleteOnExit();

        try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
             final var fileOutputStream = new FileOutputStream(sourceImageFile)) {
            Objects.requireNonNull(inputStream, THE_INPUT_STREAM_OF_THE_IMAGE_MUST_NOT_BE_NULL);
            IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
        } catch (final IOException ex) {
            fail("Failed to create test image file in test testMovePictureSameDirectory: " + ex.getMessage());
        }
        assertThat(sourceImageFile).exists()
                .canRead();

        final var pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation(sourceImageFile);

        // Consolidation of a PictureInfo to the same directory should return true
        assertTrue(ConsolidateGroupWorker.movePicture(pictureInfo, tempDir.toFile()));

        // The image File must be in the same place
        assertThat(sourceImageFile).exists();
    }

    /**
     * Move a picture to a new directory
     */
    @Test
    void testMovePictureNewDirectory(@TempDir Path tempDir) {
        assumeFalse(GraphicsEnvironment.isHeadless());

        // we need to have a picture collection so that the search for other nodes can proceed
        final var pictureCollection = new PictureCollection();
        Settings.setPictureCollection(pictureCollection);

        final var sourceImageFile = new File(tempDir.toFile(), TEMP_IMAGE_FILENAME);
        sourceImageFile.deleteOnExit();

        try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
             final var fileOutputStream = new FileOutputStream(sourceImageFile)) {
            Objects.requireNonNull(inputStream, THE_INPUT_STREAM_OF_THE_IMAGE_MUST_NOT_BE_NULL);
            IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
        } catch (final IOException ex) {
            fail(FAILED_TO_CREATE_TEST_IMAGE_FILE + ex.getMessage());
        }
        // test that is really exists. The image File must exist and be readable
        assertTrue(sourceImageFile.canRead());

        final var pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation(sourceImageFile);

        final var tempTargetDirectory = new File(tempDir.toFile(), "subdir");
        tempTargetDirectory.deleteOnExit();
        assertTrue(tempTargetDirectory.mkdir());

        // Consolidation of a PictureInfo to a new directory should succeed
        assertTrue(ConsolidateGroupWorker.movePicture(pictureInfo, tempTargetDirectory));

        // The old image File must be gone
        assertFalse(sourceImageFile.canRead());
        pictureInfo.getImageFile().deleteOnExit();
        // Consolidation of a PictureInfo to a new directory should succeed
        assertTrue(pictureInfo.getImageFile().canRead());
    }

    /**
     * Try to move a read only picture to a new directory and verify that this
     * succeeds.
     */
    @Test
    void testMoveReadonlyPictureNewDirectory(@TempDir Path tempDirSrc, @TempDir Path tempDirTgt) {
        // we need to have a picture collection so that the search for other nodes can proceed
        final var pictureCollection = new PictureCollection();
        Settings.setPictureCollection(pictureCollection);

        final var sourceImageFile = new File(tempDirSrc.toFile(), "ReadOnlyImage.jpg");

        try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
             final var fileOutputStream = new FileOutputStream(sourceImageFile)) {
            Objects.requireNonNull(inputStream, THE_INPUT_STREAM_OF_THE_IMAGE_MUST_NOT_BE_NULL);
            IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
        } catch (final IOException ex) {
            fail(FAILED_TO_CREATE_TEST_IMAGE_FILE + ex.getMessage());
        }
        assertTrue(sourceImageFile.setReadOnly());
        assertTrue(sourceImageFile.canRead());
        if (!System.getProperty("user.name").equals("root")) {
            // on Linux as root a file is always writable therefore bypassing this non-essential check
            assertFalse(sourceImageFile.canWrite());
        }

        final var pictureInfo = new PictureInfo();
        pictureInfo.setImageLocation(sourceImageFile);

        // Consolidation of a readonly PictureInfo to a new directory should succeed
        assertTrue(ConsolidateGroupWorker.movePicture(pictureInfo, tempDirTgt.toFile()));

        assertThat(sourceImageFile).doesNotExist();
        // The PictureInfo points to the readable location
        assertThat(pictureInfo.getImageFile()).canRead();

        // File is in the new Location
        assertEquals(tempDirTgt.toFile(), pictureInfo.getImageFile().getParentFile());
        assertThat(sourceImageFile).doesNotExist();
    }

}
