package org.jpo.gui;

import org.jpo.datamodel.Settings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Scalable Picture Class
 *
 * @author Richard Eigenmann
 */
public class ScalablePictureTest {

    @BeforeAll
    public static void beforeAll() {
        Settings.loadSettings(); // We need to start the cache
    }


    /**
     * Test that we can load a source image
     */
    @Test
    public void testLoading() {
        final ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull(scalablePicture);
        final URL imageUrl = ScalablePictureTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg");
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
        scalablePicture.loadPictureImd(imageFile, 0.0);
        assertEquals(350, scalablePicture.getSourcePicture().getWidth());
        assertEquals(233, scalablePicture.getSourcePicture().getHeight());
    }

    /**
     * Test that we can load a source image and rotate it
     */
    @Test
    public void testLoadingWithRotation() {
        final ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull(scalablePicture);
        final URL imageUrl = ScalablePictureTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg");
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
        scalablePicture.loadPictureImd(imageFile, 90.0);
        assertEquals(233, scalablePicture.getSourcePicture().getWidth());
        assertEquals(350, scalablePicture.getSourcePicture().getHeight());
    }

    /**
     * Test that we can load a source image, rotate it and scale it up
     */
    @Test
    public void testLoadingWithRotationAndUpscaling() {
        final ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull(scalablePicture);

        final URL imageUrl = ScalablePictureTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg");
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
        scalablePicture.loadPictureImd(imageFile, 90.0);
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
    public void testLoadingScalingWriting() {
        final ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull(scalablePicture);

        final URL imageUrl = ScalablePictureTest.class.getClassLoader().getResource("exif-test-nikon-d100-1.jpg");
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (final URISyntaxException e) {
            fail(e.getMessage());
        }
        scalablePicture.loadPictureImd(imageFile, 0.0);
        assertEquals(350, scalablePicture.getSourcePicture().getWidth());
        assertEquals(233, scalablePicture.getSourcePicture().getHeight());

        scalablePicture.setScaleFactor(2.0);
        scalablePicture.scalePicture();
        assertEquals(700, scalablePicture.getScaledWidth());
        assertEquals(466, scalablePicture.getScaledHeight());

        try {
            Path tempFile = Files.createTempFile(null, null);
            File outputFile = tempFile.toFile();
            Files.delete(outputFile.toPath());
            assertFalse(outputFile.exists());
            scalablePicture.writeScaledJpg(outputFile);

            assertTrue(outputFile.exists());

            SourcePicture sourcePicture = new SourcePicture();
            sourcePicture.loadPicture(outputFile, 0.0);
            assertEquals(700, sourcePicture.getWidth());
            assertEquals(466, sourcePicture.getHeight());

            Files.delete(outputFile.toPath());
            assertFalse(outputFile.exists());
        } catch (final IOException x) {
            fail(x.getMessage());
        }

    }

    /**
     * test for the scaling up of a zoom
     */
    @Test
    public void scaleUp() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget(100, 100, 200, 200);
        // Expecting a scale factor of 2
        assertEquals(2, scaleFactor, 0.001);
    }

    /**
     * test the scale down of a zoom
     */
    @Test
    public void scaleDown() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget(200, 200, 100, 100);
        assertEquals(0.5, scaleFactor, 0.001);
    }

    /**
     * Test a scale where the bounds are horizontal
     */
    @Test
    public void scaleHorizontally() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget(200, 100, 400, 400);
        assertEquals(2, scaleFactor, 0.001);
    }

    /**
     * Test a scale where the bounds are vertical
     */
    @Test
    public void scaleVertically() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget(100, 200, 400, 400);
        assertEquals(2, scaleFactor, 0.001);
    }

}
