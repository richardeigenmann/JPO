package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 Copyright (C) 2020-2022 Richard Eigenmann.
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

public class ShutdownApplicationRequestTest {

    private final JpoEventBus jpoEventBus;
    /**
     * Receives the event.
     */
    private ShutdownApplicationRequest receivedEvent;

    /**
     * Constructor
     */
    public ShutdownApplicationRequestTest() {
        jpoEventBus = JpoEventBus.getInstance();
    }

    /**
     * Test receiving an event.
     */
    @Test
    public void testReceivingEvent() {
        final ShutdownApplicationRequestTest.EventBusSubscriber myEventBusSubscriber = new ShutdownApplicationRequestTest.EventBusSubscriber();
        jpoEventBus.register(myEventBusSubscriber);

        final var newRequest = new ShutdownApplicationRequest();
        jpoEventBus.post(newRequest);
        // After firing a CloseApplicationRequest we expect it to be received by the listener
        assertEquals(receivedEvent, newRequest);
    }

    /**
     * Subscribes to the event.
     */
    private class EventBusSubscriber {

        @Subscribe
        public void handleCloseApplicationRequest(final ShutdownApplicationRequest event) {
            receivedEvent = event;
        }
    }

}