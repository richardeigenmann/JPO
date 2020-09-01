package org.jpo.gui;

import org.jpo.datamodel.Settings;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static junit.framework.TestCase.*;

/**
 * Tests for the Scalable Picture Class
 *
 * @author Richard Eigenmann
 */
public class ScalablePictureTest {

    @BeforeClass
    public static void beforeClass() {
        Settings.loadSettings(); // We need to start the cache
    }


    /**
     * Test that we can load a source image
     */
    @Test
    public void testLoading() {
        final ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull( "Checking that the scalablePicture is not null", scalablePicture );
        final URL imageUrl = ScalablePictureTest.class.getClassLoader().getResource( "exif-test-nikon-d100-1.jpg" );
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (URISyntaxException e) {
            fail("Could not convert resource to File: " + e.getMessage());
        }
        scalablePicture.loadPictureImd( imageFile, 0.0 );
        assertEquals( "Check that the image is 350 pixels wide", 350, scalablePicture.getSourcePicture().getWidth() );
        assertEquals( "Check that the image is 233 pixels high", 233, scalablePicture.getSourcePicture().getHeight() );
    }

    /**
     * Test that we can load a source image and rotate it
     */
    @Test
    public void testLoadingWithRotation() {
        final ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull( "Checking that the scalablePicture is not null", scalablePicture );
        final URL imageUrl = ScalablePictureTest.class.getClassLoader().getResource( "exif-test-nikon-d100-1.jpg" );
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (URISyntaxException e) {
            fail("Could not convert resource to File: " + e.getMessage());
        }
        scalablePicture.loadPictureImd( imageFile, 90.0 );
        assertEquals( "Check that the image is 233 pixels wide", 233, scalablePicture.getSourcePicture().getWidth() );
        assertEquals( "Check that the image is 350 pixels high", 350, scalablePicture.getSourcePicture().getHeight() );
    }

    /**
     * Test that we can load a source image, rotate it and scale it up
     */
    @Test
    public void testLoadingWithRotationAndUpscaling() {
        final ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull( "Checking that the scalablePicture is not null", scalablePicture );

        URL imageUrl = ScalablePictureTest.class.getClassLoader().getResource( "exif-test-nikon-d100-1.jpg" );
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (URISyntaxException e) {
            fail("Could not convert resource to File: " + e.getMessage());
        }
        scalablePicture.loadPictureImd( imageFile, 90.0 );
        assertEquals( "Check that the image is 233 pixels wide", 233, scalablePicture.getSourcePicture().getWidth() );
        assertEquals( "Check that the image is 350 pixels high", 350, scalablePicture.getSourcePicture().getHeight() );

        scalablePicture.setScaleFactor( 2.0 );
        scalablePicture.scalePicture();
        assertEquals( "Check that the image is 466 pixels wide", 466, scalablePicture.getScaledWidth() );
        assertEquals( "Check that the image is 700 pixels high", 700, scalablePicture.getScaledHeight() );
    }

    /**
     * Test that we can load a source image, scale it and write it
     */
    @Test
    public void testLoadingScalingWriting() {
        final ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull( "Checking that the scalablePicture is not null", scalablePicture );

        //Settings.loadSettings();
        URL imageUrl = ScalablePictureTest.class.getClassLoader().getResource( "exif-test-nikon-d100-1.jpg" );
        File imageFile = null;
        try {
            imageFile = new File(Objects.requireNonNull(imageUrl).toURI());
        } catch (URISyntaxException e) {
            fail("Could not convert resource to File: "+e.getMessage());
        }
        scalablePicture.loadPictureImd( imageFile, 0.0 );
        assertEquals( "Check that the image is 350 pixels wide", 350, scalablePicture.getSourcePicture().getWidth() );
        assertEquals( "Check that the image is 233 pixels high", 233, scalablePicture.getSourcePicture().getHeight() );

        scalablePicture.setScaleFactor( 2.0 );
        scalablePicture.scalePicture();
        assertEquals( "Check that the image is 700 pixels wide", 700, scalablePicture.getScaledWidth() );
        assertEquals( "Check that the image is 466 pixels high", 466, scalablePicture.getScaledHeight() );

        try {
            Path tempFile = Files.createTempFile( null, null );
            File outputFile = tempFile.toFile();
            assertTrue(outputFile.delete());
            assertFalse( "Checking that output file does not exit", outputFile.exists() );
            scalablePicture.writeScaledJpg( outputFile );

            assertTrue( "Checking if output file was created", outputFile.exists() );

            SourcePicture sourcePicture = new SourcePicture();
            sourcePicture.loadPicture( outputFile, 0.0 );
            assertEquals( "Check that the image is 700 pixels wide", 700, sourcePicture.getWidth() );
            assertEquals( "Check that the image is 466 pixels high", 466, sourcePicture.getHeight() );

            assertTrue(outputFile.delete());
            assertFalse( "Checking that output file was removed", outputFile.exists() );
        } catch ( IOException x ) {
            fail( "We hit an IOException. This must not happen. Exception: " + x.getMessage() );
        }

    }
    
       /**
     * test for the scaling up of a zoom
     */
    @Test
    public void scaleUp() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget( 100, 100, 200, 200 );
        assertEquals( "Expecting a scale factor of 2", 2, scaleFactor, 0.001 );
    }

    /**
     * test the scale down of a zoom
     */
    @Test
    public void scaleDown() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget( 200, 200, 100, 100 );
        assertEquals( "Expecting a scale factor of 0.5", 0.5, scaleFactor, 0.001 );
    }

    /**
     * Test a scale where the bounds are horizontal
     */
    @Test
    public void scaleHorizontally() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget( 200, 100, 400, 400 );
        assertEquals( "Expecting a scale factor of 2", 2, scaleFactor, 0.001 );
    }

    /**
     * Test a scale where the bounds are vertical
     */
    @Test
    public void scaleVertically() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget( 100, 200, 400, 400 );
        assertEquals( "Expecting a scale factor of 2", 2, scaleFactor, 0.001 );
    }

}
