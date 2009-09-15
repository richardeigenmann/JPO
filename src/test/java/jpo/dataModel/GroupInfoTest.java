/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jpo.dataModel;

import java.io.BufferedWriter;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author richi
 */
public class GroupInfoTest
        extends TestCase {

    public GroupInfoTest( String testName ) {
        super( testName );
    }


    /**
     * Test of toString method, of class GroupInfo.
     */
    public void testToString() {
        GroupInfo gi = new GroupInfo( "Test" );
        assertEquals( "To String should give back what whent in", "Test", gi.toString() );
    }


    /**
     * Test of getGroupName method, of class GroupInfo.
     */
    public void testGetGroupName() {
        GroupInfo gi = new GroupInfo( "Test" );
        gi.setGroupName( "Tarrantino" );
        assertEquals( "To String should give back what whent in", "Tarrantino", gi.getGroupName() );
    }


    /**
     * Test of getLowresLocation method, of class GroupInfo.
     */
    public void testGetLowresLocation() {
        GroupInfo gi = new GroupInfo( "Test" );
        gi.setLowresLocation( "c:\\" );
        assertEquals( "To Lowres Location should give back what whent in", "c:\\", gi.getLowresFilename() );

        try {
            gi.setLowresLocation( new URL( "file:///test.jpg" ) );
        } catch ( MalformedURLException ex ) {
            Logger.getLogger( GroupInfoTest.class.getName() ).log( Level.SEVERE, null, ex );
        }
        try {
            assertEquals( "To Lowres Location should give back what whent in", new URL( "file:///test.jpg" ), gi.getLowresURL() );
        } catch ( MalformedURLException ex ) {
            Logger.getLogger( GroupInfoTest.class.getName() ).log( Level.SEVERE, null, ex );
        }

        assertEquals( "To Lowres Location should also give back as a file what went in", new File( "/test.jpg" ), gi.getLowresFile() );


    }


    /**
     * Test of getLowresURLOrNull method, of class GroupInfo.
     */
    public void testGetLowresURLOrNull() {
        GroupInfo gi = new GroupInfo( "Test" );
        assertNull( "Should get null on an uninitialised object", gi.getLowresURLOrNull() );
        gi.setLowresLocation( "c:\\" );
        assertNull( "Should not be null after setting the file", gi.getLowresURLOrNull() );
    }


    /**
     * Test of appendToLowresLocation method, of class GroupInfo.
     */
    public void testAppendToLowresLocation() {
        GroupInfo gi = new GroupInfo( "Test" );
        gi.setLowresLocation( "c:\\" );
        gi.appendToLowresLocation( "test.jpg");
        assertEquals( "To Lowres Location should give back the concatenated string", "c:\\test.jpg", gi.getLowresFilename() );
  }


    /**
     * A dumb PictureInfoChangeListener that only counts the events received
     */
    GroupInfoChangeListener groupInfoChangeListener = new GroupInfoChangeListener() {

        public void groupInfoChangeEvent( GroupInfoChangeEvent pice ) {
            eventsReceived++;
        }
    };

    int eventsReceived;


    public void testGroupInfoChangeListener() {
        eventsReceived = 0;
        GroupInfo gi = new GroupInfo( "Step0" );
        assertEquals( "To start off there should be no events", 0, eventsReceived );
        gi.setGroupName( "Step 1" );
        assertEquals( "There is no listener attached so there is no event", 0, eventsReceived );
        gi.addGroupInfoChangeListener( groupInfoChangeListener );
        gi.setGroupName( "Step 2" );
        assertEquals( "The listener should have fired and we should have 1 event", 1, eventsReceived );
        gi.removeGroupInfoChangeListener( groupInfoChangeListener );
        gi.setGroupName( "Step 3" );
        assertEquals( "The detached listener should not have fired", 1, eventsReceived );
    }
}
