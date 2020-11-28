package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/*
 Copyright (C) 2017-2020  Richard Eigenmann.
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
        final GroupInfo gi = new GroupInfo( "Test" );
        assertEquals( "Test", gi.toString() );
    }

    /**
     * Test of getGroupName method, of class GroupInfo.
     */
    @Test
    public void testGetGroupName() {
        final GroupInfo gi = new GroupInfo( "Test" );
        final String EXPECTED = "Tarrantino";
        gi.setGroupName( EXPECTED );
        assertEquals( EXPECTED, gi.getGroupName() );
    }
    
    /**
     * Test of getGroupNameHtml method, of class GroupInfo.
     */
    @Test
    public void testGetGroupNameHtml() {
        final GroupInfo gi = new GroupInfo( "Test" );
        final String EXPECTED = "Tarrantino";
        gi.setGroupName( EXPECTED );
        assertEquals( EXPECTED, gi.getGroupNameHtml() );
        
        final String QUOTED_STRING = "Holiday in <Cambodia> a 1970's Hit by Kim Wilde";
        final String EXPECTED_QUOTED_STRING = "Holiday in &lt;Cambodia&gt; a 1970's Hit by Kim Wilde";
        gi.setGroupName( QUOTED_STRING);
        assertEquals( EXPECTED_QUOTED_STRING, gi.getGroupNameHtml() );
        
        final String UMLAUT_STRING = "Rüeblitorten gären im Brötlichorb";
        final String UMLAUT_EXPECTED_STRING = "R&uuml;eblitorten g&auml;ren im Br&ouml;tlichorb";
        gi.setGroupName( UMLAUT_STRING);
        assertEquals( UMLAUT_EXPECTED_STRING, gi.getGroupNameHtml() );
    }

    /**
     * A dumb PictureInfoChangeListener that only counts the events received
     *
     private final GroupInfoChangeListener groupInfoChangeListener = new GroupInfoChangeListener() {

    @Override public void groupInfoChangeEvent( final GroupInfoChangeEvent event ) {
    eventsReceived++;
    }
    };*/

    /**
     * Tests the change listener
     */
    @Test
    public void testGroupInfoChangeListener() {
        final List<GroupInfoChangeEvent> receivedGroupInfoChangedEvents = new ArrayList<>();
        Settings.getPictureCollection().setSendModelUpdates(true);
        final GroupInfo gi = new GroupInfo("Create GroupInfo when no Change listener attached");
        assertEquals(0, receivedGroupInfoChangedEvents.size());

        gi.setGroupName("Change the name without a change listener attached");
        assertEquals(0, receivedGroupInfoChangedEvents.size());

        final GroupInfoChangeListener listener = event -> receivedGroupInfoChangedEvents.add(event);
        gi.addGroupInfoChangeListener(listener);
        gi.setGroupName("Change with Change Listener");
        assertEquals(1, receivedGroupInfoChangedEvents.size());
        assertEquals(gi, receivedGroupInfoChangedEvents.get(0).getGroupeInfo());
        assertTrue(receivedGroupInfoChangedEvents.get(0).getGroupNameChanged());
        assertFalse(receivedGroupInfoChangedEvents.get(0).getThumbnailChanged());
        assertFalse(receivedGroupInfoChangedEvents.get(0).getWasSelected());
        assertFalse(receivedGroupInfoChangedEvents.get(0).getWasUnselected());

        Settings.getPictureCollection().setSendModelUpdates(false);
        gi.setGroupName("Change with sendModelUpdates false");
        assertEquals(1, receivedGroupInfoChangedEvents.size());

        Settings.getPictureCollection().setSendModelUpdates(true);
        gi.setGroupName("Change with sendModelUpdates true");
        assertEquals(2, receivedGroupInfoChangedEvents.size());
        assertEquals(gi, receivedGroupInfoChangedEvents.get(1).getGroupeInfo());
        assertTrue(receivedGroupInfoChangedEvents.get(1).getGroupNameChanged());

        gi.removeGroupInfoChangeListener(listener);
        gi.setGroupName("Last change, without Listener");
        assertEquals(2, receivedGroupInfoChangedEvents.size());
    }

    
    /**
     * Test dumpToXml
     */
    @Test
    public void testDumpToXmlRootNotProtected() {
        final GroupInfo gi = new GroupInfo( "Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign" );

        final StringWriter sw = new StringWriter();
        try (
                final BufferedWriter bw = new BufferedWriter( sw )) {
            gi.dumpToXml( bw, true, false );
            gi.endGroupXML (bw, true);
        } catch ( final IOException ex ) {
            Logger.getLogger( GroupInfoTest.class.getName() ).log( Level.SEVERE, "The dumpToXml should really not throw an IOException", ex );
            fail( ex.getMessage());
        }

        final String expected = "<collection collection_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\" collection_created=\""
                + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() ) 
                + "\" collection_protected=\"Yes\">\n\n";
        
        final String result = sw.toString();
        assertEquals( expected, result );
    }

    /**
     * Test dumpToXml
     */
    @Test
    public void testDumpToXmlRootProtected() {
        final GroupInfo gi = new GroupInfo( "Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign" );

        final StringWriter sw = new StringWriter();
        try (
                final BufferedWriter bw = new BufferedWriter( sw )) {
            gi.dumpToXml( bw, true, true );
            gi.endGroupXML (bw, true);
        } catch ( final  IOException ex ) {
            Logger.getLogger( GroupInfoTest.class.getName() ).log( Level.SEVERE, "The dumpToXml should really not throw an IOException", ex );
            fail( "Unexpected IOException" );
        }

        final String expected = "<collection collection_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\" collection_created=\""
                + DateFormat.getDateInstance().format( Calendar.getInstance().getTime() ) 
                + "\" collection_protected=\"No\">\n\n";
        
        final String result = sw.toString();
        assertEquals( expected, result );
    }

    /**
     * Test dumpToXml
     */
    @Test
    public void testDumpToXmlNormalNodeProtected() {
        final GroupInfo gi = new GroupInfo( "Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign" );

        final StringWriter sw = new StringWriter();
        try (
                final BufferedWriter bw = new BufferedWriter( sw )) {
            gi.dumpToXml( bw, false, true );
            gi.endGroupXML (bw, false);
        } catch ( final IOException ex ) {
            Logger.getLogger( GroupInfoTest.class.getName() ).log( Level.SEVERE, "The dumpToXml should really not throw an IOException", ex );
            fail( "Unexpected IOException" );
        }

        final String expected = "<group group_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\">\n</group>\n";
        
        final String result = sw.toString();
        assertEquals( expected, result );
    }

}
