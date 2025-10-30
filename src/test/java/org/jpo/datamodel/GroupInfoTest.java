package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/*
 Copyright (C) 2017-2025 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
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

    @Test
    void testToString() {
        final var groupInfo = new GroupInfo("Test");
        assertEquals("Test", groupInfo.toString());
    }

    @Test
    void testGetGroupName() {
        final var groupInfo = new GroupInfo("Test");
        final var EXPECTED = "Tarrantino";
        groupInfo.setGroupName(EXPECTED);
        assertEquals(EXPECTED, groupInfo.getGroupName());
    }


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


    @Test
    void testCompareTo() {
        final var groupInfo1 = new GroupInfo("First Group");
        final var groupInfo2 = new GroupInfo("Second Group");
        assertTrue(groupInfo1.compareTo(groupInfo2) < 0);
        assertTrue(groupInfo2.compareTo(groupInfo1) > 0);
        assertEquals(0, groupInfo1.compareTo(groupInfo1));
    }

}
