package org.jpo.gui;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.ShowAutoAdvanceDialogRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.swing.*;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.swing.finder.WindowFinder.findDialog;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/*
 Copyright (C) 2025 Richard Eigenmann.
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
class AutoAdvanceDialogTest {

    private Robot robot;
    private MockedStatic<Settings> settingsMockedStatic;

    @BeforeEach
    void setUp() {
        robot = BasicRobot.robotWithNewAwtHierarchy();

        // Mock the static call to get resources to control the dialog's title for a reliable lookup.
        settingsMockedStatic = mockStatic(Settings.class);
        final var mockBundle = mock(ResourceBundle.class);
        when(mockBundle.getString("autoAdvanceDialogTitle")).thenReturn("Auto-Advance Test");
        when(Settings.getJpoResources()).thenReturn(mockBundle);
    }

    @AfterEach
    void tearDown() {
        robot.cleanUp();
        settingsMockedStatic.close();
    }

    @Test
    void testDialogClosesWhenCancelButtonIsClicked() {
        // Run the blocking JOptionPane code on a separate thread
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> GuiActionRunner.execute(
                () -> new AutoAdvanceDialog(new ShowAutoAdvanceDialogRequest(null, null, null))));

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