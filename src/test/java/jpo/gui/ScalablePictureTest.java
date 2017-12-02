package jpo.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import jpo.dataModel.Settings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for the Scalable Picture Class
 *
 * @author Richard Eigenmann
 */
public class ScalablePictureTest {


    /**
     * Test that we can load a source image
     */
    @Test
    @Ignore
    public void testLoading() {
        ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull( "Checking that the scalablePicture is not null", scalablePicture );
        Settings.loadSettings();
        URL imageUrl = Settings.CLASS_LOADER.getResource( "exif-test-nikon-d100-1.jpg" );
        scalablePicture.loadPictureImd( imageUrl, 0.0 );
        assertEquals( "Check that the image is 350 pixels wide", 350, scalablePicture.getSourcePicture().getWidth() );
        assertEquals( "Check that the image is 233 pixels high", 233, scalablePicture.getSourcePicture().getHeight() );
    }

    /**
     * Test that we can load a source image and rotate it
     */
    @Test
    @Ignore
    public void testLoadingWithRotation() {
        ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull( "Checking that the scalablePicture is not null", scalablePicture );
        Settings.loadSettings();
        URL imageUrl = Settings.CLASS_LOADER.getResource( "exif-test-nikon-d100-1.jpg" );
        scalablePicture.loadPictureImd( imageUrl, 90.0 );
        assertEquals( "Check that the image is 233 pixels wide", 233, scalablePicture.getSourcePicture().getWidth() );
        assertEquals( "Check that the image is 350 pixels high", 350, scalablePicture.getSourcePicture().getHeight() );
    }

    /**
     * Test that we can load a source image, rotate it and scale it up
     */
    @Test
    @Ignore
    public void testLoadingWithRotationAndUpscaling() {
        ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull( "Checking that the scalablePicture is not null", scalablePicture );

        Settings.loadSettings();
        URL imageUrl = Settings.CLASS_LOADER.getResource( "exif-test-nikon-d100-1.jpg" );
        scalablePicture.loadPictureImd( imageUrl, 90.0 );
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
    @Ignore
    public void testLoadingScalingWriting() {
        ScalablePicture scalablePicture = new ScalablePicture();
        assertNotNull( "Checking that the scalablePicture is not null", scalablePicture );

        Settings.loadSettings();
        URL imageUrl = Settings.CLASS_LOADER.getResource( "exif-test-nikon-d100-1.jpg" );
        scalablePicture.loadPictureImd( imageUrl, 0.0 );
        assertEquals( "Check that the image is 350 pixels wide", 350, scalablePicture.getSourcePicture().getWidth() );
        assertEquals( "Check that the image is 233 pixels high", 233, scalablePicture.getSourcePicture().getHeight() );

        scalablePicture.setScaleFactor( 2.0 );
        scalablePicture.scalePicture();
        assertEquals( "Check that the image is 700 pixels wide", 700, scalablePicture.getScaledWidth() );
        assertEquals( "Check that the image is 466 pixels high", 466, scalablePicture.getScaledHeight() );

        try {
            Path tempFile = Files.createTempFile( null, null );
            File outputFile = tempFile.toFile();
            outputFile.delete();
            assertFalse( "Checking that output file does not exit", outputFile.exists() );
            scalablePicture.writeScaledJpg( outputFile );

            assertTrue( "Checking if output file was created", outputFile.exists() );

            SourcePicture sourcePicture = new SourcePicture();
            sourcePicture.loadPicture( outputFile.toURI().toURL(), 0.0 );
            assertEquals( "Check that the image is 700 pixels wide", 700, sourcePicture.getWidth() );
            assertEquals( "Check that the image is 466 pixels high", 466, sourcePicture.getHeight() );

            outputFile.delete();
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
        double two = 2;
        assertEquals( "Expecting a scale factor of 2", two, scaleFactor, 0.001 );
    }

    /**
     * test the scale down of a zoom
     */
    @Test
    public void scaleDown() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget( 200, 200, 100, 100 );
        double half = 0.5;
        assertEquals( "Expecting a scale factor of 0.5", half, scaleFactor, 0.001 );
    }

    /**
     * Test a scale where the bounds are horizontal
     */
    @Test
    public void scaleHorizontally() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget( 200, 100, 400, 400 );
        double two = 2;
        assertEquals( "Expecting a scale factor of 2", two, scaleFactor, 0.001 );
    }

    /**
     * Test a scale where the bounds are vertical
     */
    @Test
    public void scaleVertically() {
        double scaleFactor = ScalablePicture.calcScaleSourceToTarget( 100, 200, 400, 400 );
        double two = 2;
        assertEquals( "Expecting a scale factor of 2", two, scaleFactor, 0.001 );
    }

}
