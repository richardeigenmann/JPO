package org.jpo.gui;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SingleNodeNavigator;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.ShowAutoAdvanceDialogRequest;
import org.jpo.eventbus.ShowPictureRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import javax.swing.*;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executors;

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
 * Duplicate of ShowAutoAdvanceDialogRequestTest
 */
@Isolated
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

    @Test
    @Disabled
    void testDialogClosesWhenCancelButtonIsClicked() throws IOException {
        // Run the blocking JOptionPane code on a separate thread
        final var executor = Executors.newSingleThreadExecutor();
        final var imageFile = org.jpo.datamodel.Tools.copyResourceToTempFile("/exif-test-nikon-d100-1.jpg");
        final var pi = new PictureInfo(imageFile, "Picture");
        final var node = new SortableDefaultMutableTreeNode(pi);
        final var nodeNavigator = new SingleNodeNavigator(node);
        final var showPictureRequest = new ShowPictureRequest(nodeNavigator, 0);

        executor.submit(() -> GuiActionRunner.execute(
                () -> {
                    final var frame = new JFrame();
                    frame.pack();
                    frame.setVisible(true);
                    final var pictureViewer = new PictureViewer(showPictureRequest);

                    new AutoAdvanceDialog(new ShowAutoAdvanceDialogRequest(frame, node, pictureViewer));
                }));

        // Use WindowFinder to wait for the dialog to appear, identifying it by its mocked title.
        final DialogFixture dialogFixture = findDialog(JDialog.class)
                .withTimeout(5, SECONDS).using(robot);

        // Could there be an issue with different locale settings?
        assertEquals("Start Automatic Advance Timer",dialogFixture.target().getTitle() );
        // Find the "Cancel" button and click it
        dialogFixture.close();

        // Assert that the dialog is no longer visible
        dialogFixture.requireNotVisible();

        executor.shutdown();
    }

}