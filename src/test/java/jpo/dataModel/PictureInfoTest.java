package jpo.dataModel;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.TestCase;

/**
 *
 * @author Richard Eigenmann
 */
public class PictureInfoTest extends TestCase {

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


    /**
     * Test of setDescription method, of class PictureInfo.
     */
    public void testSetDescription() {
        PictureInfo pi = new PictureInfo();
        pi.setDescription( "Rubbish" );
        assertEquals( "Expecting what went in to come out", "Rubbish", pi.getDescription() );
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
     * Test of getChecksum method, of class PictureInfo.
     */
    public void testGetChecksum() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getChecksumAsString method, of class PictureInfo.
     */
    public void testGetChecksumAsString() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of setChecksum method, of class PictureInfo.
     */
    public void testSetChecksum() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of calculateChecksum method, of class PictureInfo.
     */
    public void testCalculateChecksum() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of appendToChecksum method, of class PictureInfo.
     */
    public void testAppendToChecksum() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of parseChecksum method, of class PictureInfo.
     */
    public void testParseChecksum() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getLowresLocation method, of class PictureInfo.
     */
    public void testGetLowresLocation() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getLowresFile method, of class PictureInfo.
     */
    public void testGetLowresFile() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getLowresURL method, of class PictureInfo.
     */
    public void testGetLowresURL() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getLowresURLOrNull method, of class PictureInfo.
     */
    public void testGetLowresURLOrNull() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of setLowresLocation method, of class PictureInfo.
     */
    public void testSetLowresLocation_String() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of setLowresLocation method, of class PictureInfo.
     */
    public void testSetLowresLocation_URL() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of appendToLowresLocation method, of class PictureInfo.
     */
    public void testAppendToLowresLocation() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getLowresFilename method, of class PictureInfo.
     */
    public void testGetLowresFilename() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of sendThumbnailChangedEvent method, of class PictureInfo.
     */
    public void testSendThumbnailChangedEvent() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of appendToFilmReference method, of class PictureInfo.
     */
    public void testAppendToFilmReference() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getFilmReference method, of class PictureInfo.
     */
    public void testGetFilmReference() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of setFilmReference method, of class PictureInfo.
     */
    public void testSetFilmReference() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of setCreationTime method, of class PictureInfo.
     */
    public void testSetCreationTime() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of appendToCreationTime method, of class PictureInfo.
     */
    public void testAppendToCreationTime() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getCreationTime method, of class PictureInfo.
     */
    public void testGetCreationTime() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getCreationTimeAsDate method, of class PictureInfo.
     */
    public void testGetCreationTimeAsDate() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getFormattedCreationTime method, of class PictureInfo.
     */
    public void testGetFormattedCreationTime() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of setComment method, of class PictureInfo.
     */
    public void testSetComment() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of appendToComment method, of class PictureInfo.
     */
    public void testAppendToComment() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getComment method, of class PictureInfo.
     */
    public void testGetComment() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of setPhotographer method, of class PictureInfo.
     */
    public void testSetPhotographer() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of appendToPhotographer method, of class PictureInfo.
     */
    public void testAppendToPhotographer() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getPhotographer method, of class PictureInfo.
     */
    public void testGetPhotographer() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of setCopyrightHolder method, of class PictureInfo.
     */
    public void testSetCopyrightHolder() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of appendToCopyrightHolder method, of class PictureInfo.
     */
    public void testAppendToCopyrightHolder() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getCopyrightHolder method, of class PictureInfo.
     */
    public void testGetCopyrightHolder() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of appendToRotation method, of class PictureInfo.
     */
    public void testAppendToRotation() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of parseRotation method, of class PictureInfo.
     */
    public void testParseRotation() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getRotation method, of class PictureInfo.
     */
    public void testGetRotation() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of setRotation method, of class PictureInfo.
     */
    public void testSetRotation_double() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of setRotation method, of class PictureInfo.
     */
    public void testSetRotation_int() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of clearCategoryAssignments method, of class PictureInfo.
     */
    public void testClearCategoryAssignments() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getCategoryAssignmentsAsArray method, of class PictureInfo.
     */
    public void testGetCategoryAssignmentsAsArray() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of appendToCategoryAssignment method, of class PictureInfo.
     */
    public void testAppendToCategoryAssignment() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of addCategoryAssignment method, of class PictureInfo.
     */
    public void testAddCategoryAssignment_String() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of addCategoryAssignment method, of class PictureInfo.
     */
    public void testAddCategoryAssignment_Object() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of setCategoryAssignment method, of class PictureInfo.
     */
    public void testSetCategoryAssignment() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of parseCategoryAssignment method, of class PictureInfo.
     */
    public void testParseCategoryAssignment() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of containsCategory method, of class PictureInfo.
     */
    public void testContainsCategory() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of removeCategory method, of class PictureInfo.
     */
    public void testRemoveCategory() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of sendWasSelectedEvent method, of class PictureInfo.
     */
    public void testSendWasSelectedEvent() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of sendWasUnselectedEvent method, of class PictureInfo.
     */
    public void testSendWasUnselectedEvent() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of sendWasMailSelectedEvent method, of class PictureInfo.
     */
    public void testSendWasMailSelectedEvent() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of sendWasMailUnselectedEvent method, of class PictureInfo.
     */
    public void testSendWasMailUnselectedEvent() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of getClone method, of class PictureInfo.
     */
    public void testGetClone() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of addPictureInfoChangeListener method, of class PictureInfo.
     */
    public void testAddPictureInfoChangeListener() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of removePictureInfoChangeListener method, of class PictureInfo.
     */
    public void testRemovePictureInfoChangeListener() {
        // TODO review the generated test code and remove the default call to fail.
    }


    /**
     * Test of anyMatch method, of class PictureInfo.
     */
    public void testAnyMatch() {
        // TODO review the generated test code and remove the default call to fail.
    }
}
