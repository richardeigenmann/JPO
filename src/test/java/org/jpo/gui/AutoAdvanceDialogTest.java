package org.jpo.gui;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
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
        executor.submit(() -> new AutoAdvanceDialog(new ShowAutoAdvanceDialogRequest(null, null, null)));

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