package org.jpo.gui;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.ChooseAndAddPicturesToGroupRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2024 Richard Eigenmann.
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

    private static final Logger LOGGER = Logger.getLogger(PictureFileChooserTest.class.getName());

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Test
    void testConstructorForNonGroupNode() {
        assumeFalse(GraphicsEnvironment.isHeadless());

        SwingUtilities.invokeLater(() -> {
            final var node = new SortableDefaultMutableTreeNode();
            Settings.setLocale(Locale.ENGLISH);
            final var chooseAndAddPicturesToGroupRequest = new ChooseAndAddPicturesToGroupRequest(node);
            new PictureFileChooser(chooseAndAddPicturesToGroupRequest);
            // EDT blocks there
        });

        // this is a different, non EDT thread...
        var errorJDialog = waitForDialog("Error");
        assertNotNull(errorJDialog, "The Error Dialog Window was not found");
        var jOptionPane = getJOptionPane(errorJDialog, Settings.getJpoResources().getString("notGroupInfo"));
        assertNotNull(jOptionPane, String.format("The Error Dialog should show the text %s but we can't find a matching JOptionPane", Settings.getJpoResources().getString("notGroupInfo")));
        clickJButton(getButton(errorJDialog, "OK"));
    }

    @Test
    void testConstructor() {
        assumeFalse(GraphicsEnvironment.isHeadless());

        SwingUtilities.invokeLater(() -> {
            final var groupInfo = new GroupInfo("GroupInfo");
            final var node = new SortableDefaultMutableTreeNode(groupInfo);
            final var pictureCollection = new PictureCollection();
            pictureCollection.getRootNode().add(node);

            Settings.setLocale(Locale.ENGLISH);
            final var chooseAndAddPicturesToGroupRequest = new ChooseAndAddPicturesToGroupRequest(node);
            new PictureFileChooser(chooseAndAddPicturesToGroupRequest);
            // EDT blocks there
        });

        // this is a different, non EDT thread...
        var jDialog = waitForDialog(Settings.getJpoResources().getString("PictureAdderDialogTitle"));
        assertNotNull(jDialog, "The File Choose Dialog Window was not found");
        var jFileChooser = getJFileChooser(jDialog);
        assertNotNull(jFileChooser, "Could not locate the JFileChooser");
        assertEquals(Settings.getDefaultSourceLocation(), jFileChooser.getCurrentDirectory());
        clickJButton(getButton(jDialog, "Cancel"));
    }


    /**
     * Clever way to find the dialog window in another thread
     *
     * @param titleToFind The title of the dialog window to locate
     * @return The JDialog that matches the title
     * @see <a href="https://stackoverflow.com/a/22417536/804766">https://stackoverflow.com/a/22417536/804766</a>
     */
    private static JDialog waitForDialog(final String titleToFind) {
        final JDialog[] foundJDialog = new JDialog[1];
        foundJDialog[0] = null;
        await().atMost(5, SECONDS).until(() -> searchForDialog(titleToFind, foundJDialog));
        return foundJDialog[0];
    }

    /**
     * Searches for the Dialog window and returns true if it found it. The actual dialog
     * is returned to the resultJDialog array (pass by reference)
     *
     * @param titleToFind the search term
     * @param resultJDialog the dialog box
     * @return if the term was found
     */
    private static boolean searchForDialog(final String titleToFind, final JDialog[] resultJDialog) {
        LOGGER.log(Level.FINE, "Searching for dialog with the tile {0}", titleToFind);
        for (final var window : Window.getWindows()) {
            if (window instanceof JDialog jDialog
                    && titleToFind.equals(jDialog.getTitle())) {
                resultJDialog[0] = jDialog;
                return true;
            }
        }
        return false;
    }

    /**
     * Clever way to find the JButton in a JDialog window. It walks down the components tree looking for JButtons
     * and when it finds one checks if it has the correct label.
     *
     * @param container The container to walk down
     * @param text      the text of the button
     * @return The JButton that matches the text or null
     * @see <a href="https://stackoverflow.com/a/22417536/804766">https://stackoverflow.com/a/22417536/804766</a>
     */
    private static JButton getButton(final @NotNull Container container, final String text) {
        JButton btn = null;
        var children = new ArrayList<Container>(25);
        for (final var child : container.getComponents()) {
            if (child instanceof JButton jButton) {
                if (text.equals(jButton.getText())) {
                    btn = jButton;
                    break;
                }
            } else if (child instanceof Container) {
                children.add((Container) child);
            }
        }
        if (btn == null) {
            for (final var cont : children) {
                final var jButton = getButton(cont, text);
                if (jButton != null) {
                    btn = jButton;
                    break;
                }
            }
        }
        return btn;
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

    /**
     * Clever way to find the JFileChooser in a JDialog window. It walks down the components tree looking for JFileChoosers
     * and when it returns it.
     *
     * @param container The container to walk down
     * @return The JFileChooser or null
     * @see <a href="https://stackoverflow.com/a/22417536/804766">https://stackoverflow.com/a/22417536/804766</a>
     */
    private static JFileChooser getJFileChooser(final @NotNull Container container) {
        JFileChooser jFileChooser = null;
        var children = new ArrayList<Container>(25);
        for (final var child : container.getComponents()) {
            if (child instanceof JFileChooser chooser) {
                jFileChooser = chooser;
                break;
            } else if (child instanceof Container) {
                children.add((Container) child);
            }
        }
        if (jFileChooser == null) {
            for (final var cont : children) {
                final var chooser = getJFileChooser(cont);
                if (chooser != null) {
                    jFileChooser = chooser;
                    break;
                }
            }
        }
        return jFileChooser;
    }


    private void clickJButton(final @NotNull JButton btn) {
        SwingUtilities.invokeLater(btn::doClick);
    }

}