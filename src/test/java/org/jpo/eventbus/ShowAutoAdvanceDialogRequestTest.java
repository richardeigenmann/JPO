package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.assertj.swing.edt.GuiActionRunner;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SingleNodeNavigator;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.gui.PictureViewer;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/*
 Copyright (C) 2017 - 2025 Richard Eigenmann.
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
        //assumeFalse(GraphicsEnvironment.isHeadless());

        final var myEventBusSubscriber = new EventBusSubscriber();
        jpoEventBus.register(myEventBusSubscriber);

        final var jFrame = GuiActionRunner.execute(() -> new JFrame());
        final var pictureInfo = new PictureInfo();
        try {
            final var imageFile = new File(ClassLoader.getSystemResources("exif-test-nikon-d100-1.jpg").nextElement().toURI());
            pictureInfo.setImageLocation(imageFile);
        } catch (URISyntaxException | IOException e) {
            fail("Could not load image file: " + e.getMessage());
        }
        final var pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
        final var navigator = new SingleNodeNavigator(pictureNode);
        final var request = new ShowPictureRequest(navigator, 0);
        final var pictureViewer = GuiActionRunner.execute(() -> new PictureViewer(request));

        final var showAutoAdvanceDialogRequest = GuiActionRunner.execute(() -> new ShowAutoAdvanceDialogRequest(jFrame, pictureNode, pictureViewer));
        GuiActionRunner.execute(() -> {
            try {
                final var robot = new Robot();
                robot.delay(200);
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.delay(20);
                robot.keyRelease(KeyEvent.VK_ENTER);
            } catch (final AWTException e) {
                fail(e.getMessage());
                Thread.currentThread().interrupt();
            }
        });

        jpoEventBus.post(showAutoAdvanceDialogRequest);

        assertEquals(showAutoAdvanceDialogRequest, responseEvent);
        assertEquals(jFrame, responseEvent.parentComponent());
        assertEquals(pictureNode, responseEvent.currentNode());
        assertEquals(pictureViewer, responseEvent.autoAdvanceTarget());
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
