package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
 * Test for Group Selection Events
 *
 * @author Richard Eigenmann
 */
public class GroupSelectionEventTest {

    /**
     * Constructor
     */
    public GroupSelectionEventTest() {
        jpoEventBus = JpoEventBus.getInstance();
    }

    private final JpoEventBus jpoEventBus;

    /**
     * Test receiving an event.
     */
    @Test
    public void testReceivingEvent() {
        EventBusSubscriber myEventBusSubscriber = new EventBusSubscriber();
        jpoEventBus.register( myEventBusSubscriber );

        final GroupInfo groupInfo = new GroupInfo( "Empty Group" );
        final SortableDefaultMutableTreeNode node = new SortableDefaultMutableTreeNode( groupInfo );

        GroupSelectionEvent myGroupSelectionEvent = new GroupSelectionEvent( node );
        jpoEventBus.post( myGroupSelectionEvent );
        assertEquals( "After firing a GroupSelectionEvent we expect it to be received by the listener",
                myGroupSelectionEvent, responseEvent );
        assertEquals( node, responseEvent.getNode() );
    }

    /**
     * Receives the event.
     */
    private GroupSelectionEvent responseEvent;

    /**
     * Subscribes to the vent.
     */
    private class EventBusSubscriber {

        @Subscribe
        public void handleGroupSelectionEvent( GroupSelectionEvent event ) {
            responseEvent = event;
        }
    }

}
