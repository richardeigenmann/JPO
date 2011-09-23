package jpo.dataModel;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;

/**
 *
 * @author Richard Eigenmann
 */
public class PictureInfoTest
        extends TestCase {

    public PictureInfoTest( String testName ) {
        super( testName );
    }


    /**
     * Test of toString method, of class PictureInfo.
     */
    public void testToString() {
        PictureInfo pi = new PictureInfo( "c:\\picture.jpg", "file:///lores/thumbnail.jpg", "My Sample Picture", "Film 123" );
        assertEquals( "Should return the description", "My Sample Picture", pi.toString() );
    }


    /**
     * Test of getDescription method, of class PictureInfo.
     */
    public void testGetDescription() {
        PictureInfo pi = new PictureInfo( "c:\\picture.jpg", "file:///lores/thumbnail.jpg", "My Sample Picture", "Film 123" );
        assertEquals( "Should return the description", "My Sample Picture", pi.getDescription() );
    }

    int changeEvents;


    /**
     * Test of setDescription method, of class PictureInfo.
     */
    public void testSetDescription() {
        PictureInfo pi = new PictureInfo();
        changeEvents = 0;
        PictureInfoChangeListener picl = new PictureInfoChangeListener() {

            @Override
            public void pictureInfoChangeEvent( PictureInfoChangeEvent arg0 ) {
                changeEvents += 1;
            }
        };
        pi.addPictureInfoChangeListener( picl );
        pi.setDescription( "Rubbish" );
        assertEquals( "Expecting what went in to come out", "Rubbish", pi.getDescription() );
        assertEquals( "Expecting 1 change event", 1, changeEvents );
        pi.setDescription( "More Rubbish" );
        assertEquals( "Expecting what went in to come out", "More Rubbish", pi.getDescription() );
        assertEquals( "Expecting a second change event", 2, changeEvents );
    }

    int countEvents;


    /**
     * Test Description change event
     */
    public void testSetDescriptionSame() {
        PictureInfo pi = new PictureInfo();
        countEvents = 0;
        PictureInfoChangeListener picl = new PictureInfoChangeListener() {

            @Override
            public void pictureInfoChangeEvent( PictureInfoChangeEvent arg0 ) {
                countEvents += 1;
            }
        };
        pi.addPictureInfoChangeListener( picl );
        pi.setDescription( "Rubbish" );
        assertEquals( "Expecting what went in to come out", "Rubbish", pi.getDescription() );
        assertEquals( "Expecting 1 change event", 1, countEvents );
        pi.setDescription( "Rubbish" );
        assertEquals( "Expecting what went in to come out", "Rubbish", pi.getDescription() );
        assertEquals( "Expecting no new change event because it was the same that went in", 1, countEvents );
    }


    /**
     * Test of appendToDescription method, of class PictureInfo.
     */
    public void testAppendToDescription() {
        PictureInfo pi = new PictureInfo();
        pi.setDescription( "Rubbish" );
        pi.appendToDescription( "Bin" );
        assertEquals( "Expecting that the description concatenated", "RubbishBin", pi.getDescription() );
    }


    /**
     * Test of descriptionContains method, of class PictureInfo.
     */
    public void testDescriptionContains() {
        PictureInfo pi = new PictureInfo();
        pi.setDescription( "RubbishBinTrash" );
        assertEquals( "Expecting to find a substring", true, pi.descriptionContains( "Bin" ) );
    }


    /**
     * Test of getHighresLocation method, of class PictureInfo.
     */
    public void testGetHighresLocation() {
        PictureInfo pi = new PictureInfo( "file:///dir/picture.jpg", "file:///lores/thumbnail.jpg", "My Sample Picture", "Film 123" );
        String highresLocation = pi.getHighresLocation();
        assertEquals( "Checking getHighresLocation", "file:///dir/picture.jpg", highresLocation );
    }


    /**
     * Test of getHighresFile method, of class PictureInfo.
     */
    public void testGetHighresFile() {
        PictureInfo pi = new PictureInfo( "file:///dir/picture.jpg", "file:///lores/thumbnail.jpg", "My Sample Picture", "Film 123" );
        File highresFile = pi.getHighresFile();
        assertEquals( "Checking getHighresFile", new File( "/dir/picture.jpg" ), highresFile );
    }


    /**
     * Test of getHighresURL method, of class PictureInfo.
     * @throws Exception 
     */
    public void testGetHighresURL() throws Exception {
        PictureInfo pi = new PictureInfo( "file:///dir/picture.jpg", "file:///lores/thumbnail.jpg", "My Sample Picture", "Film 123" );
        URL highresURL = pi.getHighresURL();
        assertEquals( "Checking getHighresURL", new URL( "file:///dir/picture.jpg" ), highresURL );
    }


    /**
     * Test of getHighresURLOrNull method, of class PictureInfo.
     */
    public void testGetHighresURLOrNull() {
        PictureInfo pi1 = new PictureInfo( "file:///dir/picture.jpg", "file:///lores/thumbnail.jpg", "My Sample Picture", "Film 123" );
        URL highresURL1 = pi1.getHighresURLOrNull();
        try {
            assertEquals( "Checking getHighresURLOrNull", new URL( "file:///dir/picture.jpg" ), highresURL1 );
        } catch ( MalformedURLException ex ) {
            ex.printStackTrace();
            fail( "Test should not have thrown an exception: " + ex.getMessage() );
        }

        PictureInfo pi2 = new PictureInfo( "noProtocol:///dir/picture.jpg", "file:///lores/thumbnail.jpg", "My Sample Picture", "Film 123" );
        URL highresURL2 = pi2.getHighresURLOrNull();
        assertNull( "Checking getHighresURLOrNull", highresURL2 );
    }


    /**
     * Test of setHighresLocation method, of class PictureInfo.
     */
    public void testSetHighresLocation_String() {
        PictureInfo pi = new PictureInfo();
        pi.setHighresLocation( "file:///dir/picture.jpg" );
        File f = pi.getHighresFile();
        assertEquals( "Testing that the Highres Location was memorised correctly", f.toString(), "/dir/picture.jpg" );
    }


    /**
     * Test of setHighresLocation method, of class PictureInfo.
     * @throws MalformedURLException
     */
    public void testSetHighresLocation_URL() throws MalformedURLException {
        PictureInfo pi = new PictureInfo();
        pi.setHighresLocation( new URL( "file:///dir/picture.jpg" ) );
        File f = pi.getHighresFile();
        assertEquals( "Testing that the Highres Location was memorised correctly", f.toString(), "/dir/picture.jpg" );
    }


    /**
     * Test of appendToHighresLocation method, of class PictureInfo.
     */
    public void testAppendToHighresLocation() {
        PictureInfo pi = new PictureInfo();
        pi.setHighresLocation( "file:///dir/picture" );
        pi.appendToHighresLocation( ".jpg" );
        File f = pi.getHighresFile();
        assertEquals( "Testing that the Highres Location was memorised correctly", f.toString(), "/dir/picture.jpg" );
    }


    /**
     * Test of getHighresFilename method, of class PictureInfo.
     */
    public void testGetHighresFilename() {
        PictureInfo pi = new PictureInfo();
        pi.setHighresLocation( "file:///dir/picture.jpg" );
        String filename = pi.getHighresFilename();
        assertEquals( "Testing that the filename can be derived from the Highres Location correctly", filename, "picture.jpg" );
    }

    /**
     * A dumb PictureInfoChangeListener that only counts the events received
     */
    PictureInfoChangeListener pictureInfoChangeListener = new PictureInfoChangeListener() {

        public void pictureInfoChangeEvent( PictureInfoChangeEvent pice ) {
            eventsReceived++;
        }
    };

    int eventsReceived;

    public void testPictureInfoChangeListener() {
        eventsReceived = 0;
        PictureInfo pi = new PictureInfo();
        assertEquals( "To start off there should be no events", 0, eventsReceived);
        pi.setDescription( "Step 1");
        assertEquals( "There is no listener attached so there is no event", 0, eventsReceived);
        pi.addPictureInfoChangeListener( pictureInfoChangeListener );
        pi.setDescription( "Step 2");
        assertEquals( "The listener should have fired and we should have 1 event", 1, eventsReceived);
        pi.removePictureInfoChangeListener( pictureInfoChangeListener );
        pi.setDescription( "Step 3");
        assertEquals( "The detached listener should not have fired", 1, eventsReceived);
    }
}
