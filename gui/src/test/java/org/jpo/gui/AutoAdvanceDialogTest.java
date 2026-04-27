package org.jpo.gui;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.jpo.datamodel.NodeNavigatorInterface;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.ShowAutoAdvanceDialogRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import javax.swing.*;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.swing.finder.WindowFinder.findDialog;
import static org.junit.jupiter.api.Assertions.assertEquals;

/*
 Copyright (C) 2025-2026 Richard Eigenmann.
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
 * Similar to ShowAutoAdvanceDialogRequestTest which tests the Request
 */
@Isolated // findDialog might find the wrong dialog
class AutoAdvanceDialogTest {

    private Robot robot;

    @BeforeEach
    void setUp() {
        robot = BasicRobot.robotWithNewAwtHierarchy();
        Settings.setLocale(Locale.ENGLISH);
    }

    @AfterEach
    void tearDown() {
        robot.cleanUp();
    }

    class MyAutoAdvanceImplementation implements AutoAdvanceInterface {
        private static final Logger LOGGER = Logger.getLogger(MyAutoAdvanceImplementation.class.getName());

        @Override
        public void startAdvanceTimer(int seconds) {
            LOGGER.info("Auto advance timer started for " + seconds + " seconds.");
        }

        @Override
        public void showNode(NodeNavigatorInterface mySetOfNodes, int myIndex) {
            LOGGER.log(Level.INFO, "showNode called with NodeNavigatorInterface {0} and index {1}", new Object[]{ mySetOfNodes, myIndex});
        }
    }

    @Test
    //@Disabled
    void testDialogClosesWhenCancelButtonIsClicked() throws IOException {
        final var executor = Executors.newSingleThreadExecutor();
        final var imageFile = org.jpo.datamodel.Tools.copyResourceToTempFile("/exif-test-nikon-d100-1.jpg");
        final var pi = new PictureInfo(imageFile, "Picture");
        final var node = new SortableDefaultMutableTreeNode(pi);

        executor.submit(() -> GuiActionRunner.execute(
                () -> {
                    final var jpanel = new JPanel();
                    new AutoAdvanceDialog(new ShowAutoAdvanceDialogRequest(jpanel, node, new MyAutoAdvanceImplementation()));
                }));

        // Portential race condition if a concurrent test were to open a dialog that might be found
        final DialogFixture dialogFixture = findDialog(JDialog.class)
                .withTimeout(2, SECONDS).using(robot);

        // Could there be an issue with different locale settings?
        assertEquals("Start Automatic Advance Timer",dialogFixture.target().getTitle() );
        // Find the "Cancel" button and click it
        dialogFixture.close();

        // Assert that the dialog is no longer visible
        dialogFixture.requireNotVisible();

        executor.shutdown();
    }

}