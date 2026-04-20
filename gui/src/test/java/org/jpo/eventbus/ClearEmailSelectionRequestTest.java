package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureCollection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 Copyright (C) 2020-2024 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY
 without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

class ClearEmailSelectionRequestTest {

    /**
     * Constructor
     */
    public ClearEmailSelectionRequestTest() {
        jpoEventBus = JpoEventBus.getInstance();
    }

    private final JpoEventBus jpoEventBus;

    /**
     * Test receiving an event.
     */
    @Test
    void testReceivingEvent() {
        final var myEventBusSubscriber = new ClearEmailSelectionRequestTest.EventBusSubscriber();
        jpoEventBus.register( myEventBusSubscriber );

        final var pictureCollection = new PictureCollection();
        final var newRequest = new ClearEmailSelectionRequest(pictureCollection);
        jpoEventBus.post( newRequest );
        // After firing a ClearEmailSelectionRequest we expect it to be received by the listener
        assertEquals( receivedEvent, newRequest );
    }

    /**
     * Receives the event.
     */
    private ClearEmailSelectionRequest receivedEvent;

    /**
     * Subscribes to the event.
     */
    private class EventBusSubscriber {

        @Subscribe
        public void handleClearEmailSelectionRequest( ClearEmailSelectionRequest event ) {
            receivedEvent = event;
        }
    }


}