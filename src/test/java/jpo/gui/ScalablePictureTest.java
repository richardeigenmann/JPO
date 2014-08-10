package jpo.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import jpo.dataModel.Settings;
import junit.framework.TestCase;

/**
 * Tests for the Scalable Picture Class
 *
 * @author Richard Eigenmann
 */
public class ScalablePictureTest extends TestCase {

    /**
     * Constructor for the tests
     *
     * @param testName test name
     */
    public ScalablePictureTest( String testName ) {
        super( testName );
    }

    /**
     * Test that we can load a source image
     */
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

}
