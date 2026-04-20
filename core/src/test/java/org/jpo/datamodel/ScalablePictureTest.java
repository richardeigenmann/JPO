package org.jpo.datamodel;

import com.google.common.hash.Hashing;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2024-2026 Richard Eigenmann.
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
 * Tests for the Scalable Picture Class
 *
 * @author Richard Eigenmann
 */
class ScalablePictureTest {

    private static final String EXIF_TEST_NIKON_D_100_1_JPG = "/exif-test-nikon-d100-1.jpg";

    /**
     * Test that we can load a source image
     */
    @Test
    void testLoading() throws IOException {
        final var scalablePicture = new ScalablePicture();
        assertNotNull(scalablePicture);
        final var imageFile = Tools.copyResourceToTempFile(EXIF_TEST_NIKON_D_100_1_JPG);
        final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
        scalablePicture.loadPictureImd(hash.toString(), imageFile, 0.0);
        assertEquals(350, scalablePicture.getSourcePicture().getWidth());
        assertEquals(233, scalablePicture.getSourcePicture().getHeight());
    }

    /**
     * Test that we can load a source image and rotate it
     */
    @Test
    void testLoadingWithRotation() throws IOException {
        final var scalablePicture = new ScalablePicture();
        assertNotNull(scalablePicture);
        final var imageFile = Tools.copyResourceToTempFile(EXIF_TEST_NIKON_D_100_1_JPG);
        final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
        scalablePicture.loadPictureImd(hash.toString(), imageFile, 90.0);
        assertEquals(233, scalablePicture.getSourcePicture().getWidth());
        assertEquals(350, scalablePicture.getSourcePicture().getHeight());
    }

    /**
     * Test that we can load a source image, rotate it and scale it up
     */
    @Test
    void testLoadingWithRotationAndUpscaling() throws IOException {
        final var scalablePicture = new ScalablePicture();
        assertNotNull(scalablePicture);

        final var imageFile = Tools.copyResourceToTempFile(EXIF_TEST_NIKON_D_100_1_JPG);
        final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
        scalablePicture.loadPictureImd(hash.toString(), imageFile, 90.0);
        assertEquals(233, scalablePicture.getSourcePicture().getWidth());
        assertEquals(350, scalablePicture.getSourcePicture().getHeight());

        scalablePicture.setScaleFactor(2.0);
        scalablePicture.scalePicture();
        assertEquals(466, scalablePicture.getScaledWidth());
        assertEquals(700, scalablePicture.getScaledHeight());
    }

    /**
     * Test that we can load a source image, scale it and write it
     */
    @Test
    void testLoadingScalingWriting() throws IOException {
        assumeFalse( System.getProperty("os.name").toLowerCase().startsWith("win") ); // Doesn't work on Windows
        final var scalablePicture = new ScalablePicture();
        assertNotNull(scalablePicture);

        final var imageFile = Tools.copyResourceToTempFile(EXIF_TEST_NIKON_D_100_1_JPG);
        final var hash = com.google.common.io.Files.asByteSource(imageFile).hash(Hashing.sha256());
        scalablePicture.loadPictureImd(hash.toString(), imageFile, 0.0);

        assertEquals(350, scalablePicture.getSourcePicture().getWidth());
        assertEquals(233, scalablePicture.getSourcePicture().getHeight());

        scalablePicture.setScaleFactor(2.0);
        scalablePicture.scalePicture();
        assertEquals(700, scalablePicture.getScaledWidth());
        assertEquals(466, scalablePicture.getScaledHeight());

        final var tempFile = Files.createTempFile("testLoadingScalingWriting", "jpg");
        final var outputFile = tempFile.toFile();
        Files.delete(outputFile.toPath());
        assertThat(outputFile).doesNotExist();
        scalablePicture.writeScaledJpg(outputFile);

        assertThat(outputFile).exists();

        final var sourcePicture = new SourcePicture();
        final var hash2 = com.google.common.io.Files.asByteSource(outputFile).hash(Hashing.sha256());
        sourcePicture.loadPicture(hash2.toString(), outputFile, 0.0);
        assertEquals(700, sourcePicture.getWidth());
        assertEquals(466, sourcePicture.getHeight());

        Files.delete(outputFile.toPath());
        assertThat(outputFile).doesNotExist();

    }

    /**
     * test for the scaling up of a zoom
     */
    @Test
    void scaleUp() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget(100, 100, 200, 200);
        // Expecting a scale factor of 2
        assertEquals(2, scaleFactor, 0.001);
    }

    /**
     * test the scale down of a zoom
     */
    @Test
    void scaleDown() {
        final var scaleFactor = ScalablePicture.calcScaleSourceToTarget(200, 200, 100, 100);
        assertEquals(0.5, scaleFactor, 0.001);
    }

    /**
     * Test a scale where the bounds are horizontal
     */
    @Test
    void scaleHorizontally() {
        final var scaleFactor = ScalablePicture.calcScaleSourceToTarget(200, 100, 400, 400);
        assertEquals(2, scaleFactor, 0.001);
    }

    /**
     * Test a scale where the bounds are vertical
     */
    @Test
    void scaleVertically() {
        final var scaleFactor = ScalablePicture.calcScaleSourceToTarget(100, 200, 400, 400);
        assertEquals(2, scaleFactor, 0.001);
    }

}
