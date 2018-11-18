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

/*
 Copyright (C) 2017  Richard Eigenmann.
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
        String expected = "Tarrantino";
        gi.setGroupName( expected );
        assertEquals( "To String should give back what whent in", expected, gi.getGroupName() );
    }
    
    /**
     * Test of getGroupNameHtml method, of class GroupInfo.
     */
    @Test
    public void testGetGroupNameHtml() {
        GroupInfo gi = new GroupInfo( "Test" );
        String expected = "Tarrantino";
        gi.setGroupName( expected );
        assertEquals( "To String should give back what whent in", expected, gi.getGroupNameHtml() );
        
        String quotedString = "Holiday in <Cambodia> a 1970's Hit by Kim Wilde";
        String expectedQuotedString = "Holiday in &lt;Cambodia&gt; a 1970's Hit by Kim Wilde";
        gi.setGroupName( quotedString);
        assertEquals( "Special chars are to be escaped", expectedQuotedString, gi.getGroupNameHtml() );
        
        String umlautString = "Rüeblitorten gären im Brötlikorb";
        String expectedUmlautString = "R&uuml;eblitorten g&auml;ren im Br&ouml;tlikorb";
        gi.setGroupName( umlautString);
        assertEquals( "German umlauts are to be escaped", expectedUmlautString, gi.getGroupNameHtml() );
        
    }

    /**
     * A dumb PictureInfoChangeListener that only counts the events received
     */
    private GroupInfoChangeListener groupInfoChangeListener = new GroupInfoChangeListener() {

        @Override
        public void groupInfoChangeEvent( GroupInfoChangeEvent pice ) {
            eventsReceived++;
        }
    };

    private int eventsReceived;

    /**
     * Tests the change listener
     */
    @Test
    public void testGroupInfoChangeListener() {
        eventsReceived = 0;
        Settings.getPictureCollection().setSendModelUpdates( true );
        GroupInfo gi = new GroupInfo( "Step0" );
        assertEquals( "To start off there should be no events", 0, eventsReceived );
        gi.setGroupName( "Step 1" );
        assertEquals( "There is no listener attached so there is no event", 0, eventsReceived );
        gi.addGroupInfoChangeListener( groupInfoChangeListener );
        gi.setGroupName( "Step 2" );
        assertEquals( "The listener should have fired and we should have 1 event", 1, eventsReceived );
        Settings.getPictureCollection().setSendModelUpdates( false );
        gi.setGroupName( "Step 3" );
        assertEquals( "We should remain at 1 event", 1, eventsReceived );
        gi.removeGroupInfoChangeListener( groupInfoChangeListener );
        gi.setGroupName( "Step 4" );
        assertEquals( "The detached listener should not have fired", 1, eventsReceived );
        Settings.getPictureCollection().setSendModelUpdates( true );
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
                BufferedWriter bw = new BufferedWriter( sw )) {
            gi.dumpToXml( bw, true, false );
            gi.endGroupXML (bw, true);
        } catch ( IOException ex ) {
            Logger.getLogger( GroupInfoTest.class.getName() ).log( Level.SEVERE, "The dumpToXml should really not throw an IOException", ex );
            fail( "Unexpected IOException" );
        }

        String expected = "<collection collection_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\" collection_created=\"" 
                + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() ) 
                + "\" collection_protected=\"Yes\"\n>\n\n";
        
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
                BufferedWriter bw = new BufferedWriter( sw )) {
            gi.dumpToXml( bw, true, true );
            gi.endGroupXML (bw, true);
        } catch ( IOException ex ) {
            Logger.getLogger( GroupInfoTest.class.getName() ).log( Level.SEVERE, "The dumpToXml should really not throw an IOException", ex );
            fail( "Unexpected IOException" );
        }

        String expected = "<collection collection_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\" collection_created=\"" 
                + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() ) 
                + "\" collection_protected=\"No\"\n>\n\n";
        
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
                BufferedWriter bw = new BufferedWriter( sw )) {
            gi.dumpToXml( bw, false, true );
            gi.endGroupXML (bw, false);
        } catch ( IOException ex ) {
            Logger.getLogger( GroupInfoTest.class.getName() ).log( Level.SEVERE, "The dumpToXml should really not throw an IOException", ex );
            fail( "Unexpected IOException" );
        }

        String expected = "<group group_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\"\n>\n</group>\n";
        
        String result = sw.toString();
        assertEquals( expected, result );
    }

}
