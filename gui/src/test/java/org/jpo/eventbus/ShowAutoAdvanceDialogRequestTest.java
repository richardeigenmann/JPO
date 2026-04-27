package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.edt.GuiActionRunner;
import org.jpo.datamodel.*;
import org.jpo.gui.AutoAdvanceInterface;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 Copyright (C) 2017-2026 Richard Eigenmann.
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
@Isolated
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
     * Similar to AutoAdvanceDialogTest but focussed on the event
     */
    @Test
    void testReceivingEvent() throws IOException {
        final var myEventBusSubscriber = new EventBusSubscriber();
        jpoEventBus.register(myEventBusSubscriber);

        final var jPanel = GuiActionRunner.execute(() -> new JPanel());
        final var pictureInfo = new PictureInfo();
        final var imageFile = Tools.copyResourceToTempFile("/exif-test-nikon-d100-1.jpg");
        pictureInfo.setImageLocation(imageFile);
        final var pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
        final var myAutoAdvanceImplementation = new MyAutoAdvanceImplementation();

        final var showAutoAdvanceDialogRequest = GuiActionRunner.execute(
                () -> new ShowAutoAdvanceDialogRequest(jPanel, pictureNode, myAutoAdvanceImplementation));
        var robot = BasicRobot.robotWithNewAwtHierarchy();
        robot.pressAndReleaseKey(KeyEvent.VK_ENTER);

        jpoEventBus.post(showAutoAdvanceDialogRequest);

        assertEquals(showAutoAdvanceDialogRequest, responseEvent);
        assertEquals(jPanel, responseEvent.parentComponent());
        assertEquals(pictureNode, responseEvent.currentNode());
        assertEquals(myAutoAdvanceImplementation, responseEvent.autoAdvanceTarget());
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



    class MyAutoAdvanceImplementation implements AutoAdvanceInterface {
        private static final Logger LOGGER = Logger.getLogger(ShowAutoAdvanceDialogRequestTest.MyAutoAdvanceImplementation.class.getName());

        @Override
        public void startAdvanceTimer(int seconds) {
            LOGGER.info("Auto advance timer started for " + seconds + " seconds.");
        }

        @Override
        public void showNode(NodeNavigatorInterface mySetOfNodes, int myIndex) {
            LOGGER.log(Level.INFO, "showNode called with NodeNavigatorInterface {0} and index {1}", new Object[]{ mySetOfNodes, myIndex});
        }
    }
}
