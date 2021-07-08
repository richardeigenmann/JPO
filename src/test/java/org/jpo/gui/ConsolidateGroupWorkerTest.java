package org.jpo.gui;

import org.apache.commons.compress.utils.IOUtils;
import org.jpo.datamodel.PictureInfo;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 ConsolidateGroupWorkerTest.java: 

 Copyright (C) 2016-2021  Richard Eigenmann.
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
class ConsolidateGroupWorkerTest {

    private static final String NIKON_D100_JPG = "exif-test-nikon-d100-1.jpg";

    /**
     * Show that a null image file doesn't need to be moved.
     */
    @Test
    void testNeedToMovePictureNull() {
        final PictureInfo pictureInfo = new PictureInfo();
        try {
            final var tempTargetDirectory = Files.createTempDirectory("testNeedToMovePictureNull").toFile();
            tempTargetDirectory.deleteOnExit();
            try {
                ConsolidateGroupWorker.needToMovePicture(pictureInfo, tempTargetDirectory);
                fail("the needToMovePicture should not handle null inputs; the are invalid");
            } catch (final NullPointerException ex) {
                // this is good
            }
        } catch (final IOException e) {
            fail("Unexpected IOException: " + e.getMessage());
        }
    }

    /**
     * Show that an image that doesn't exist doesn't need to be moved.
     */
    @Test
    void testNeedToMoveNonexistentPicture() {
        try {
            final var tempSourceDirectory = Files.createTempDirectory("testNeedToMoveNonexistentPicture-Source").toFile();
            tempSourceDirectory.deleteOnExit();
            final var sourceImageFile = new File(tempSourceDirectory, "Image1.jpg");
            sourceImageFile.deleteOnExit();
            // Java File object exists but not on the disk

            final var pictureInfo = new PictureInfo();
            pictureInfo.setImageLocation(sourceImageFile);

            final var tempTargetDirectory = Files.createTempDirectory("testNeedToMoveNonexistentPicture-Target").toFile();
            tempTargetDirectory.deleteOnExit();
            // Based on the info in the filenames the picture would need to be moved
            assertTrue(ConsolidateGroupWorker.needToMovePicture(pictureInfo, tempTargetDirectory));
        } catch (final IOException e) {
            fail("Could not clean up after test. Exception: " + e.getMessage());
        }
    }

    /**
     * Test need to move Move a picture to the same directory returns false
     */
    @Test
    void testNeedToMovePictureSameDirectory() {
        try {
            final var tempSourceDirectory = Files.createTempDirectory("testNeedToMovePictureSameDirectory-Source").toFile();
            tempSourceDirectory.deleteOnExit();
            final var sourceImageFile = new File(tempSourceDirectory, "Image1.jpg");
            sourceImageFile.deleteOnExit();

            try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
                 final var fileOutputStream = new FileOutputStream(sourceImageFile)) {
                Objects.requireNonNull(inputStream, "The input stream of the image must not be null!");
                IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
            } catch (final IOException ex) {
                fail("Failed to create test image file: " + ex.getMessage());
            }
            // test that is really exists
            assertTrue(sourceImageFile.canRead());

            final var pictureInfo = new PictureInfo();
            pictureInfo.setImageLocation(sourceImageFile);

            // Consolidation of a PictureInfo to the same directory should return false as nothing was moved
            assertFalse(ConsolidateGroupWorker.needToMovePicture(pictureInfo, tempSourceDirectory));
        } catch (final IOException e) {
            fail("Unexpected IOException: " + e.getMessage());
        }
    }

    /**
     * Test need to Move a picture to a new directory
     */
    @Test
    void testNeedToMovePictureNewDirectory() {
        try {
            final var tempSourceDirectory = Files.createTempDirectory("testNeedToMovePictureNewDirectory-Source").toFile();
            tempSourceDirectory.deleteOnExit();
            final var imageFile = new File(tempSourceDirectory, "Image1.jpg");
            imageFile.deleteOnExit();

            try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
                 final var fileOutputStream = new FileOutputStream(imageFile)) {
                Objects.requireNonNull(inputStream, "The input stream of the image must not be null!");
                IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
            } catch (final IOException ex) {
                fail("Failed to create test image file: " + ex.getMessage());
            }
            // test that is really exists
            assertTrue(imageFile.canRead());

            final var pictureInfo = new PictureInfo();
            pictureInfo.setImageLocation(imageFile);

            final var tempTargetDirectory = Files.createTempDirectory("testNeedToMovePictureNewDirectory-Target").toFile();
            tempTargetDirectory.deleteOnExit();

            // Consolidation of a PictureInfo to a new directory should succeed
            assertTrue(ConsolidateGroupWorker.needToMovePicture(pictureInfo, tempTargetDirectory));
        } catch (final IOException e) {
            fail("Unexpected IOException: " + e.getMessage());
        }

    }

    /**
     * Test if we would need to move a picture to a new directory or if it can
     * stay in the place it was.
     *
     * @see <a href="http://stackoverflow.com/questions/28366433/file-canwrite-and-files-iswritable-not-giving-correct-value-on-linux">Stackoverflow</a>
     */
    @Test
    void testNeedToMoveReadonlyPicture() {
        // This test doesn't work on CI platforms where the user is root as root can always write to a file
        assumeFalse(System.getProperty("user.name").equals("root"));
        try {
            final File tempSourceDirectory = Files.createTempDirectory("testNeedToMoveReadonlyPicture-Source").toFile();
            tempSourceDirectory.deleteOnExit();
            final File sourceImageFile = new File(tempSourceDirectory, "ReadOnlyImage.jpg");
            sourceImageFile.deleteOnExit();

            try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
                 final var fileOutputStream = new FileOutputStream(sourceImageFile)) {
                Objects.requireNonNull(inputStream, "The input stream of the image must not be null!");
                IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
            } catch (final IOException ex) {
                fail("Failed to create test image file: " + ex.getMessage());
            }
            assertTrue(sourceImageFile.setReadOnly());
            assertTrue(sourceImageFile.canRead());

            final var pictureInfo = new PictureInfo();
            pictureInfo.setImageLocation(sourceImageFile);

            final File tempTargetDirectory = new File(tempSourceDirectory, "subdir");

            // Consolidation of a readonly PictureInfo to a new directory should return true
            assertTrue(ConsolidateGroupWorker.needToMovePicture(pictureInfo, tempTargetDirectory));

            assertTrue(sourceImageFile.setWritable(true));
        } catch (final IOException e) {
            fail("Could not clean up after test. Exception: " + e.getMessage());
        }
    }

    /**
     * Show that consolidation of a PictureInfo with a null file succeeds
     * because they can't be moved.
     */
    @Test
    void testMovePictureNull() {
        try {
            final File tempTargetDirectory = Files.createTempDirectory("testMovePictureNull").toFile();
            tempTargetDirectory.deleteOnExit();

            try {
                final boolean returnCode = ConsolidateGroupWorker.movePicture(new PictureInfo(), tempTargetDirectory);
                // Consolidation of a PictureInfo with a \"null\" highres file should return false
                assertFalse(returnCode);
            } catch (final NullPointerException ex) {
                assertTrue(tempTargetDirectory.delete());
                return;
            }
        } catch (IOException ex) {
            fail("Unexpected IOException: " + ex.getMessage());
        }
        fail("Consolidation of a PictureInfo with a \"null\" highres file should throw a NPE");
    }

    /**
     * Move a picture to the same directory and verify that it stays in the same
     * place.
     */
    @Test
    void testMovePictureSameDirectory() {
        try {
            final var tempSourceDirectory = Files.createTempDirectory("testMovePictureSameDirectory").toFile();
            tempSourceDirectory.deleteOnExit();
            final var sourceImageFile = new File(tempSourceDirectory, "Image1.jpg");
            sourceImageFile.deleteOnExit();

            try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
                 final var fileOutputStream = new FileOutputStream(sourceImageFile)) {
                Objects.requireNonNull(inputStream, "The input stream of the image must not be null!");
                IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
            } catch (final IOException ex) {
                fail("Failed to create test image file in test testMovePictureSameDirectory: " + ex.getMessage());
            }
            assertTrue(sourceImageFile.exists());
            assertTrue(sourceImageFile.canRead());

            final var pictureInfo = new PictureInfo();
            pictureInfo.setImageLocation(sourceImageFile);

            // Consolidation of a PictureInfo to the same directory should return true
            assertTrue(ConsolidateGroupWorker.movePicture(pictureInfo, tempSourceDirectory));

            // The image File must be in the same place
            assertTrue(sourceImageFile.exists());
        } catch (final IOException e) {
            fail("Unexpected IOException: " + e.getMessage());
        }
    }

    /**
     * Move a picture to a new directory
     */
    @Test
    void testMovePictureNewDirectory() {
        assumeFalse(GraphicsEnvironment.isHeadless());

        try {
            final var tempSourceDirectory = Files.createTempDirectory("testMovePictureNewDirectory").toFile();
            tempSourceDirectory.deleteOnExit();
            final var sourceImageFile = new File(tempSourceDirectory, "Image1.jpg");
            sourceImageFile.deleteOnExit();

            try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
                 final var fileOutputStream = new FileOutputStream(sourceImageFile)) {
                Objects.requireNonNull(inputStream, "The input stream of the image must not be null!");
                IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
            } catch (final IOException ex) {
                fail("Failed to create test image file: " + ex.getMessage());
            }
            // test that is really exists. The image File must exist and be readable
            assertTrue(sourceImageFile.canRead());

            final var pictureInfo = new PictureInfo();
            pictureInfo.setImageLocation(sourceImageFile);

            final var tempTargetDirectory = new File(tempSourceDirectory, "subdir");
            tempTargetDirectory.deleteOnExit();
            assertTrue(tempTargetDirectory.mkdir());

            // Consolidation of a PictureInfo to a new directory should succeed
            assertTrue(ConsolidateGroupWorker.movePicture(pictureInfo, tempTargetDirectory));

            // The old image File must be gone
            assertFalse(sourceImageFile.canRead());
            pictureInfo.getImageFile().deleteOnExit();
            // Consolidation of a PictureInfo to a new directory should succeed
            assertTrue(pictureInfo.getImageFile().canRead());
        } catch (final IOException e) {
            fail("An unexpected IOException was thrown: " + e.getMessage());
        }
    }

    /**
     * Try to move a read only picture to a new directory and verify that this
     * succeeds.
     */
    @Test
    void testMoveReadonlyPictureNewDirectory() {
        try {
            final var tempSourceDirectory = Files.createTempDirectory("testMoveReadonlyPictureNewDirectory-Source").toFile();
            tempSourceDirectory.deleteOnExit();
            final var sourceImageFile = new File(tempSourceDirectory, "ReadOnlyImage.jpg");
            sourceImageFile.deleteOnExit();

            try (final var inputStream = ConsolidateGroupWorkerTest.class.getClassLoader().getResourceAsStream(NIKON_D100_JPG);
                 final var fileOutputStream = new FileOutputStream(sourceImageFile)) {
                Objects.requireNonNull(inputStream, "The input stream of the image must not be null!");
                IOUtils.copy(Objects.requireNonNull(inputStream), fileOutputStream);
            } catch (final IOException ex) {
                fail("Failed to create test image file: " + ex.getMessage());
            }
            assertTrue(sourceImageFile.setReadOnly());
            assertTrue(sourceImageFile.canRead());
            if (!System.getProperty("user.name").equals("root")) {
                // on Linux as root a file is always writable therefore bypassing this non-essential check
                assertFalse(sourceImageFile.canWrite());
            }

            final var pictureInfo = new PictureInfo();
            pictureInfo.setImageLocation(sourceImageFile);

            final var tempTargetDirectory = Files.createTempDirectory("testMoveReadonlyPictureNewDirectory-Target").toFile();
            tempTargetDirectory.deleteOnExit();
            // Consolidation of a readonly PictureInfo to a new directory should succeed
            assertTrue(ConsolidateGroupWorker.movePicture(pictureInfo, tempTargetDirectory));

            assertFalse(sourceImageFile.exists());
            // The PictureInfo points to the readable location
            assertTrue(pictureInfo.getImageFile().canRead());

            // File is in the new Location
            assertEquals(tempTargetDirectory, pictureInfo.getImageFile().getParentFile());
            assertFalse(sourceImageFile.exists());

            // Cleanup
            pictureInfo.getImageFile().deleteOnExit();
        } catch (final IOException e) {
            fail("Unexpected IOException: " + e.getMessage());
        }
    }

}
