package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

/*
 Copyright (C) 2017-2023 Richard Eigenmann.
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
 * Tests for GroupInfo class
 *
 * @author Richard Eigenmann
 */
class GroupInfoTest {

    /**
     * Test of toString method, of class GroupInfo.
     */
    @Test
    void testToString() {
        final var groupInfo = new GroupInfo("Test");
        assertEquals("Test", groupInfo.toString());
    }

    /**
     * Test of getGroupName method, of class GroupInfo.
     */
    @Test
    void testGetGroupName() {
        final var groupInfo = new GroupInfo("Test");
        final var EXPECTED = "Tarrantino";
        groupInfo.setGroupName(EXPECTED);
        assertEquals(EXPECTED, groupInfo.getGroupName());
    }

    /**
     * Test of getGroupNameHtml method, of class GroupInfo.
     */
    @Test
    void testGetGroupNameHtml() {
        final var groupInfo = new GroupInfo("Test");
        final var EXPECTED = "Tarrantino";
        groupInfo.setGroupName(EXPECTED);
        assertEquals(EXPECTED, groupInfo.getGroupNameHtml());

        final var QUOTED_STRING = "Holiday in <Cambodia> a 1970's Hit by Kim Wilde";
        final var EXPECTED_QUOTED_STRING = "Holiday in &lt;Cambodia&gt; a 1970's Hit by Kim Wilde";
        groupInfo.setGroupName(QUOTED_STRING);
        assertEquals(EXPECTED_QUOTED_STRING, groupInfo.getGroupNameHtml());
        
        final var UMLAUT_STRING = "Rüeblitorten gären im Brötlichorb";
        final var UMLAUT_EXPECTED_STRING = "R&uuml;eblitorten g&auml;ren im Br&ouml;tlichorb";
        groupInfo.setGroupName( UMLAUT_STRING);
        assertEquals( UMLAUT_EXPECTED_STRING, groupInfo.getGroupNameHtml() );
    }

    /**
     * Tests the change listener
     */
    @Test
    void testGroupInfoChangeListener() {
        final var pictureCollection = new PictureCollection();
        pictureCollection.setSendModelUpdates(true);
        final var groupInfo = new GroupInfo("Create GroupInfo when no Change listener attached");
        final var node = new SortableDefaultMutableTreeNode(groupInfo);
        pictureCollection.getRootNode().add(node);

        final var receivedGroupInfoChangedEvents = new ArrayList<GroupInfoChangeEvent>();
        assertEquals(0, receivedGroupInfoChangedEvents.size());

        groupInfo.setGroupName("Change the name without a change listener attached");
        assertEquals(0, receivedGroupInfoChangedEvents.size());

        final GroupInfoChangeListener listener = receivedGroupInfoChangedEvents::add;
        groupInfo.addGroupInfoChangeListener(listener);
        groupInfo.setGroupName("Change with Change Listener");
        assertEquals(1, receivedGroupInfoChangedEvents.size());
        assertEquals(groupInfo, receivedGroupInfoChangedEvents.get(0).getGroupInfo());
        assertTrue(receivedGroupInfoChangedEvents.get(0).getGroupNameChanged());
        assertFalse(receivedGroupInfoChangedEvents.get(0).getThumbnailChanged());
        assertFalse(receivedGroupInfoChangedEvents.get(0).getWasSelected());
        assertFalse(receivedGroupInfoChangedEvents.get(0).getWasUnselected());

        pictureCollection.setSendModelUpdates(false);
        groupInfo.setGroupName("Change with sendModelUpdates false");
        assertEquals(1, receivedGroupInfoChangedEvents.size());

        pictureCollection.setSendModelUpdates(true);
        groupInfo.setGroupName("Change with sendModelUpdates true");
        assertEquals(2, receivedGroupInfoChangedEvents.size());
        assertEquals(groupInfo, receivedGroupInfoChangedEvents.get(1).getGroupInfo());
        assertTrue(receivedGroupInfoChangedEvents.get(1).getGroupNameChanged());

        groupInfo.removeGroupInfoChangeListener(listener);
        groupInfo.setGroupName("Last change, without Listener");
        assertEquals(2, receivedGroupInfoChangedEvents.size());
    }



    /**
     * Test dumpToXml
     */
    @Test
    void testDumpToXmlNormalNodeProtected() {
        final var groupInfo = new GroupInfo("Holiday in <Cambodia> with Kim Wilde = 1970's music & a \" sign");

        final var stringWriter = new StringWriter();
        try (
                final var bufferedWriter = new BufferedWriter(stringWriter)) {
            groupInfo.dumpToXml(bufferedWriter, false, true);
            groupInfo.endGroupXML(bufferedWriter, false);
        } catch (final IOException ex) {
            Logger.getLogger(GroupInfoTest.class.getName()).log(Level.SEVERE, "The dumpToXml should really not throw an IOException", ex);
            fail("Unexpected IOException");
        }

        final String newline = System. lineSeparator();
        final var expected = "<group group_name=\"Holiday in &lt;Cambodia&gt; with Kim Wilde = 1970&apos;s music &amp; a &quot; sign\">" + newline + "</group>" + newline;

        assertEquals(expected, stringWriter.toString());
    }

    @Test
    void testCompareTo() {
        final var groupInfo1 = new GroupInfo("First Group");
        final var groupInfo2 = new GroupInfo("Second Group");
        assert (groupInfo1.compareTo(groupInfo2) < 0);
        assert (groupInfo2.compareTo(groupInfo1) > 0);
        assert (groupInfo1.compareTo(groupInfo1) == 0);
    }

}
