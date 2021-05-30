package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.gui.PictureViewer;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2017 - 2021 Richard Eigenmann.
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
 * Test for Group Selection Events
 *
 * @author Richard Eigenmann
 */
class ShowAutoAdvanceDialogRequestTest {

    /**
     * Constructor
     */
    public ShowAutoAdvanceDialogRequestTest() {
        jpoEventBus = JpoEventBus.getInstance();
    }

    private final JpoEventBus jpoEventBus;

    /**
     * Test receiving an event.
     */
    @Test
    void testReceivingEvent() {
        assumeFalse(GraphicsEnvironment.isHeadless());

        final var myEventBusSubscriber = new EventBusSubscriber();
        jpoEventBus.register(myEventBusSubscriber);

        try {
            SwingUtilities.invokeAndWait(() -> {
                final var jFrame = new JFrame();
                final var node = new SortableDefaultMutableTreeNode();
                final var pictureViewer = new PictureViewer();

                final var showAutoAdvanceDialogRequest = new ShowAutoAdvanceDialogRequest(jFrame, node, pictureViewer);
                SwingUtilities.invokeLater(() -> {
                    try {
                        final var r = new Robot();
                        r.delay(200);
                        r.keyPress(KeyEvent.VK_ENTER);
                        r.delay(20);
                        r.keyRelease(KeyEvent.VK_ENTER);
                    } catch (final AWTException e) {
                        fail(e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                });
                jpoEventBus.post(showAutoAdvanceDialogRequest);

                assertEquals(showAutoAdvanceDialogRequest, responseEvent);
                assertEquals(jFrame, responseEvent.parentComponent());
                assertEquals(node, responseEvent.currentNode());
                assertEquals(pictureViewer, responseEvent.autoAdvanceTarget());
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Receives the event.
     */
    private ShowAutoAdvanceDialogRequest responseEvent;

    /**
     * Subscribes to the vent.
     */
    private class EventBusSubscriber {

        @Subscribe
        public void handleGroupSelectionEvent(final ShowAutoAdvanceDialogRequest event) {
            responseEvent = event;
        }
    }

}
