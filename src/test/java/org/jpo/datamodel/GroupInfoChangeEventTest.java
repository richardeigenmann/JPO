package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GroupInfoChangeEventTest {

    @Test
    void testChangingGroupName() {
        final GroupInfo groupInfo = new GroupInfo("GroupInfo");
        final int[] count = new int[1];
        final GroupInfoChangeListener groupInfoChangeListener = new GroupInfoChangeListener() {
            @Override
            public void groupInfoChangeEvent(final GroupInfoChangeEvent groupInfoChangeEvent) {
                count[0]++;
                assertEquals(groupInfo, groupInfoChangeEvent.getGroupInfo());
                assertTrue(groupInfoChangeEvent.getGroupNameChanged());
                assertFalse(groupInfoChangeEvent.getThumbnailChanged());
                assertFalse(groupInfoChangeEvent.getWasSelected());
                assertFalse(groupInfoChangeEvent.getWasUnselected());

            }
        };
        groupInfo.addGroupInfoChangeListener(groupInfoChangeListener);
        assertEquals(0, count[0]);
        groupInfo.setGroupName("New Name");
        assertEquals(1, count[0]);
    }
}