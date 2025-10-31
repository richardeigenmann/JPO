package org.jpo.gui;

import org.assertj.swing.core.BasicRobot;
import org.assertj.swing.core.Robot;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.DialogFixture;
import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.ChooseAndAddPicturesToGroupRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Fail.fail;
import static org.assertj.swing.core.matcher.DialogMatcher.withTitle;
import static org.assertj.swing.core.matcher.JButtonMatcher.withText;
import static org.assertj.swing.finder.WindowFinder.findDialog;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

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

class PictureFileChooserTest {

    private Robot robot;

    @BeforeAll
    static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @BeforeEach
    void setUp() {
        Settings.setLocale(Locale.ENGLISH);
        robot = BasicRobot.robotWithNewAwtHierarchy();
    }

    @AfterEach
    void tearDown() {
        robot.cleanUp();
    }

    @Test
    void testConstructorForNonGroupNode() {
        assumeFalse(GraphicsEnvironment.isHeadless());

        final var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> GuiActionRunner.execute(() -> {
            final var node = new SortableDefaultMutableTreeNode(); // no user object i.e. it's not a group
            final var chooseAndAddPicturesToGroupRequest = new ChooseAndAddPicturesToGroupRequest(node);
            new PictureFileChooser(chooseAndAddPicturesToGroupRequest);
        }));

        final String expectedDialogTitle = JpoResources.getResource("genericError");
        final String expectedErrorText = JpoResources.getResource("notGroupInfo");

        final DialogFixture dialogFixture = findDialog(withTitle(expectedDialogTitle))
                .withTimeout(5, SECONDS).using(robot);

        assertNotNull(dialogFixture.target(), "The Dialog Window was not found by AssertJ-Swing");

        var jOptionPane = getJOptionPane(dialogFixture.target(), expectedErrorText);
        assertNotNull(jOptionPane, String.format("The Dialog should show the text %s but a matching JOptionPane was not found", expectedErrorText));

        // Click the button to dismiss the modal dialog
        // This unblocks the EDT task submitted in the executor
        dialogFixture.button(withText("OK")).click();
        dialogFixture.requireNotVisible();

        // Wait for the executor task to complete to ensure clean shutdown
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException _) {
            fail("Didn't terminate");
        }
        executor.shutdownNow();
    }

    @Test
    void testConstructor() {
        assumeFalse(GraphicsEnvironment.isHeadless());

        final var executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> GuiActionRunner.execute(() -> {
            final var groupInfo = new GroupInfo("GroupInfo");
            final var node = new SortableDefaultMutableTreeNode(groupInfo);
            final var pictureCollection = new PictureCollection();
            pictureCollection.getRootNode().add(node);

            Settings.setLocale(Locale.ENGLISH);
            final var chooseAndAddPicturesToGroupRequest = new ChooseAndAddPicturesToGroupRequest(node);
            new PictureFileChooser(chooseAndAddPicturesToGroupRequest);
        }));

        final String expectedDialogTitle = JpoResources.getResource("PictureAdderDialogTitle");
        final DialogFixture dialogFixture = findDialog(withTitle(expectedDialogTitle))
                .withTimeout(5, SECONDS).using(robot);

        assertNotNull(dialogFixture.target(), "The File Choose Dialog Window was not found by AssertJ-Swing");

        dialogFixture.button(withText("Cancel")).click();
        dialogFixture.requireNotVisible();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException _) {
            fail("Didn't terminate");
        }
        executor.shutdownNow();
    }

    /**
     * Clever way to find the JOptionPane in a JDialog window. It walks down the components tree looking for JOptionPane s
     * and when it finds one checks if it has the correct message.
     *
     * @param container     The container to walk down
     * @param messageToFind the message to find
     * @return The JOptionPane that matches the text or null
     * @see <a href="https://stackoverflow.com/a/22417536/804766">https://stackoverflow.com/a/22417536/804766</a>
     */
    private static JOptionPane getJOptionPane(final @NotNull Container container, final String messageToFind) {
        JOptionPane pane = null;
        var children = new ArrayList<Container>(25);
        for (final var child : container.getComponents()) {
            if (child instanceof JOptionPane jOptionPane) {
                if (messageToFind.equals(jOptionPane.getMessage())) {
                    pane = jOptionPane;
                    break;
                }
            } else if (child instanceof Container) {
                children.add((Container) child);
            }
        }
        if (pane == null) {
            for (final var cont : children) {
                final var jOptionPane = getJOptionPane(cont, messageToFind);
                if (jOptionPane != null) {
                    pane = jOptionPane;
                    break;
                }
            }
        }
        return pane;
    }


}