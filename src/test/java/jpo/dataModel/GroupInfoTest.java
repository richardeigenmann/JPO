package jpo.dataModel;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import org.junit.Test;

/**
 * Tests for Groupinfo class
 *
 * @author Richard Eigenmann
 */
public class GroupInfoTest {

    /**
     * Test of toString method, of class GroupInfo.
     */
    @Test
    public void testToString() {
        GroupInfo gi = new GroupInfo( "Test" );
        assertEquals( "To String should give back what whent in", "Test", gi.toString() );
    }

    /**
     * Test of getGroupName method, of class GroupInfo.
     */
    @Test
    public void testGetGroupName() {
        GroupInfo gi = new GroupInfo( "Test" );
        gi.setGroupName( "Tarrantino" );
        assertEquals( "To String should give back what whent in", "Tarrantino", gi.getGroupName() );
    }

    /**
     * A dumb PictureInfoChangeListener that only counts the events received
     */
    GroupInfoChangeListener groupInfoChangeListener = new GroupInfoChangeListener() {

        @Override
        public void groupInfoChangeEvent( GroupInfoChangeEvent pice ) {
            eventsReceived++;
        }
    };

    int eventsReceived;

    /**
     * Tests the change listener
     */
    @Test
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

    /**
     * Test dumpToXml
     * TODO: Is the collectionprotected done correctly? Is it used for anything?
     */
    @Test
    public void testDumpToXmlRootNotProtected() {
        final GroupInfo gi = new GroupInfo( "Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign" );

        StringWriter sw = new StringWriter();
        try (
                //FileWriter sw = new FileWriter( "/tmp/output.xml" );
                BufferedWriter bw = new BufferedWriter( sw ); ) {
            gi.dumpToXml( bw, true, false );
        } catch ( IOException ex ) {
            Logger.getLogger( GroupInfoTest.class.getName() ).log( Level.SEVERE, "The dumpToXml should really not throw an IOException", ex );
            fail( "Unexpected IOException" );
        }

        String expected = "<collection collection_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\" collection_created=\"" 
                + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() ) 
                + "\" collection_protected=\"Yes\"\n>\n";
        
        String result = sw.toString();
        assertEquals( expected, result );
    }

    /**
     * Test dumpToXml
     * TODO: Is the collectionprotected done correctly? Is it used for anything?
     */
    @Test
    public void testDumpToXmlRootProtected() {
        final GroupInfo gi = new GroupInfo( "Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign" );

        StringWriter sw = new StringWriter();
        try (
                //FileWriter sw = new FileWriter( "/tmp/output.xml" );
                BufferedWriter bw = new BufferedWriter( sw ); ) {
            gi.dumpToXml( bw, true, true );
        } catch ( IOException ex ) {
            Logger.getLogger( GroupInfoTest.class.getName() ).log( Level.SEVERE, "The dumpToXml should really not throw an IOException", ex );
            fail( "Unexpected IOException" );
        }

        String expected = "<collection collection_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\" collection_created=\"" 
                + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() ) 
                + "\" collection_protected=\"No\"\n>\n";
        
        String result = sw.toString();
        assertEquals( expected, result );
    }

    /**
     * Test dumpToXml
     * TODO: Is the collectionprotected done correctly? Is it used for anything?
     */
    @Test
    public void testDumpToXmlNormalNodeProtected() {
        final GroupInfo gi = new GroupInfo( "Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign" );

        StringWriter sw = new StringWriter();
        try (
                //FileWriter sw = new FileWriter( "/tmp/output.xml" );
                BufferedWriter bw = new BufferedWriter( sw ); ) {
            gi.dumpToXml( bw, false, true );
        } catch ( IOException ex ) {
            Logger.getLogger( GroupInfoTest.class.getName() ).log( Level.SEVERE, "The dumpToXml should really not throw an IOException", ex );
            fail( "Unexpected IOException" );
        }

        String expected = "<group group_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\"\n>\n";
        
        String result = sw.toString();
        assertEquals( expected, result );
    }

}
