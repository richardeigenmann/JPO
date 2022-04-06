package org.jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jpo.datamodel.*;
import org.jpo.eventbus.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;


/*
 Copyright (C) 2017-2022  Richard Eigenmann.
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
 * Tests for the PicturePopupMenu Class
 *
 * @author Richard Eigenmann
 */
class PicturePopupMenuTest {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PicturePopupMenuTest.class.getName());
    public static final String MY_PICTURE = "My Picture";

    @BeforeAll
    public static void setUpOnce() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        FailOnThreadViolationRepaintManager.install();
    }

    /**
     * Test that out Group Node was created for the correct node.
     */
    @Test
    void testRememberingPopupNode() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    final Field popupNodeField;
                    popupNodeField = PicturePopupMenu.class.getDeclaredField("popupNode");
                    popupNodeField.setAccessible(true);
                    final var myPictureInfo = new PictureInfo(new File("nosuchfile-testRememberingPopupNode.jpg"), MY_PICTURE);
                    final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                    final var myNavigator = new SingleNodeNavigator(myNode);
                    final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                    final var verifyNode = (SortableDefaultMutableTreeNode) popupNodeField.get(picturePopupMenu);
                    final var verifyPictureInfo = (PictureInfo) verifyNode.getUserObject();
                    assertEquals(myPictureInfo, verifyPictureInfo);
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                    Logger.getLogger(PicturePopupMenuTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(PicturePopupMenuTest.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testMenuTitle() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testMenuTitle.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var title = (JMenuItem) picturePopupMenu.getComponent(0);
                assertEquals(MY_PICTURE, title.getText());
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Test clicking showPicture
     */
    @Test
    void testShowPictureJMenuItemClick() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testShowPictureJMenuItemClick.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var showPictureJMenuItem = (JMenuItem) picturePopupMenu.getComponent(2);
                assertEquals("Show Picture", showPictureJMenuItem.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(ShowPictureRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                showPictureJMenuItem.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Test clicking showMap
     */
    @Test
    void testShowMap() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testShowMap.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var showMap = (JMenuItem) picturePopupMenu.getComponent(3);
                assertEquals("Show Map", showMap.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(ShowPictureOnMapRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                showMap.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Test clicking showMap
     */
    @Test
    void testOpenFolder() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var pictureInfo = new PictureInfo();
                final var pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
                final var navigator = new SingleNodeNavigator(pictureNode);
                final var picturePopupMenu = new PicturePopupMenu(navigator, 0);
                try {
                    final var tempFile = File.createTempFile("testOpenFolder", ".jpg");
                    tempFile.deleteOnExit();
                    pictureInfo.setImageLocation(tempFile);

                    final var openFolder = (JMenuItem) picturePopupMenu.getComponent(4);
                    assertEquals("Open Folder", openFolder.getText());
                    final int[] eventsReceived = {0};
                    JpoEventBus.getInstance().register(new Object() {
                        @Subscribe
                        public void handleRequest(OpenFileExplorerRequest request) {
                            eventsReceived[0]++;
                        }
                    });
                    assertEquals(0, eventsReceived[0]);
                    openFolder.doClick();
                    assertEquals(1, eventsReceived[0]);
                    Files.delete(tempFile.toPath());
                } catch (final IOException e) {
                    fail(e.getMessage());
                }
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testNavigateTo() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var pictureInfo = new PictureInfo();
                final var pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
                try {
                    final var tempFile = File.createTempFile("testNavigateTo", ".jpg");
                    tempFile.deleteOnExit();
                    pictureInfo.setImageLocation(tempFile);
                    final var parentNode = new SortableDefaultMutableTreeNode(new GroupInfo("Parent Group"));
                    parentNode.add(pictureNode);
                    Settings.getPictureCollection().getRootNode().add(parentNode);

                    final var navigator = new SingleNodeNavigator(pictureNode);
                    final var picturePopupMenu = new PicturePopupMenu(navigator, 0);
                    final var navigateTo = (JMenu) picturePopupMenu.getComponent(5);
                    final var navigateTo0 = navigateTo.getItem(0);
                    assertNotNull(navigateTo0);
                    assertEquals("Navigate to", navigateTo.getText());

                    final int[] eventsReceived = {0};
                    JpoEventBus.getInstance().register(new Object() {
                        @Subscribe
                        public void handleRequest(ShowGroupRequest request) {
                            eventsReceived[0]++;
                        }
                    });
                    assertEquals(0, eventsReceived[0]);
                    navigateTo0.doClick();
                    assertEquals(1, eventsReceived[0]);
                    Files.delete(tempFile.toPath());
                } catch (final IOException e) {
                    fail(e.getMessage());
                }
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    @Test
    void testSelectForEmail() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testSelectForEmail.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var selectForEmail = (JMenuItem) picturePopupMenu.getComponent(6);
                assertEquals("Select for email", selectForEmail.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(AddPictureNodesToEmailSelectionRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                selectForEmail.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testUnselectForEmail() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testUnselectForEmail.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var unselectForEmail = (JMenuItem) picturePopupMenu.getComponent(7);
                assertEquals("Unselect for email", unselectForEmail.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(RemovePictureNodesFromEmailSelectionRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                unselectForEmail.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testClearEmailSelection() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testClearEmailSelection.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var clearEmailSelection = (JMenuItem) picturePopupMenu.getComponent(8);
                assertEquals("Clear email selection", clearEmailSelection.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(ClearEmailSelectionRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                clearEmailSelection.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Not happy with this test as it clicks on the user function and that may have been loaded by the settings
     * object which then actually runs the user function. We just want to test that the event is propagated
     * correctly. So I am setting the user functions to "". Hopefully we aren't concurrently running a test
     * that is writing the settings back to the local properties.
     * #BRITTLE
     */
    @Test
    void testUserFunctions() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testUserFunctions.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var userFunction = (JMenu) picturePopupMenu.getComponent(9);
                final var userFunction0 = userFunction.getItem(0);
                final var userFunction1 = userFunction.getItem(1);
                final var userFunction2 = userFunction.getItem(2);
                assertEquals("User Function", userFunction.getText());
                Settings.getUserFunctionCmd()[0] = "";
                Settings.getUserFunctionCmd()[1] = "";
                Settings.getUserFunctionCmd()[2] = "";
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(RunUserFunctionRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                userFunction0.doClick();
                userFunction1.doClick();
                userFunction2.doClick();
                assertEquals(3, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testRotation() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testRotation.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var rotation = (JMenu) picturePopupMenu.getComponent(10);
                final var rotate90 = rotation.getItem(0);
                final var rotate180 = rotation.getItem(1);
                final var rotate270 = rotation.getItem(2);
                final var rotate0 = rotation.getItem(3);
                assertEquals("Rotation", rotation.getText());
                assertEquals("Rotate Right 90", rotate90.getText());
                assertEquals("Rotate 180", rotate180.getText());
                assertEquals("Rotate Left 270", rotate270.getText());
                assertEquals("No Rotation", rotate0.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRotatePictureRequestRequest(RotatePictureRequest request) {
                        eventsReceived[0]++;
                    }

                    @Subscribe
                    public void handleSetPictureRotationRequest(SetPictureRotationRequest request) {
                        eventsReceived[0]++;
                    }

                });
                assertEquals(0, eventsReceived[0]);
                rotate90.doClick();
                rotate180.doClick();
                rotate270.doClick();
                rotate0.doClick();
                assertEquals(4, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testRefresh() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testRefresh.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var refreshThumbnail = (JMenuItem) picturePopupMenu.getComponent(11);
                assertEquals("Refresh Thumbnail", refreshThumbnail.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(RefreshThumbnailRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                refreshThumbnail.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testMove() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testMove.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var move = (JMenu) picturePopupMenu.getComponent(12);
                final var moveToTop = move.getItem(Settings.getMaxDropnodes() + 1);
                final var moveUp = move.getItem(Settings.getMaxDropnodes() + 2);
                final var moveDown = move.getItem(Settings.getMaxDropnodes() + 3);
                final var moveToBottom = move.getItem(Settings.getMaxDropnodes() + 4);
                final var moveIndent = move.getItem(Settings.getMaxDropnodes() + 5);
                final var moveOutdent = move.getItem(Settings.getMaxDropnodes() + 6);
                assertEquals("Move", move.getText());
                assertEquals("to Top", moveToTop.getText());
                assertEquals("Up", moveUp.getText());
                assertEquals("Down", moveDown.getText());
                assertEquals("indent", moveIndent.getText());
                assertEquals("outdent", moveOutdent.getText());
                assertEquals("to Bottom", moveToBottom.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleMoveNodeToTopRequest(MoveNodeToTopRequest request) {
                        eventsReceived[0]++;
                    }

                    @Subscribe
                    public void handleMoveNodeUpRequest(MoveNodeUpRequest request) {
                        eventsReceived[0]++;
                    }

                    @Subscribe
                    public void handleMoveNodeDownRequest(MoveNodeDownRequest request) {
                        eventsReceived[0]++;
                    }

                    @Subscribe
                    public void handleMoveNodeToBottomRequest(MoveNodeToBottomRequest request) {
                        eventsReceived[0]++;
                    }

                    @Subscribe
                    public void handleMoveIndentRequest(MoveIndentRequest request) {
                        eventsReceived[0]++;
                    }

                    @Subscribe
                    public void handleMoveOutdentRequest(MoveOutdentRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                moveToTop.doClick();
                moveDown.doClick();
                moveUp.doClick();
                moveToBottom.doClick();
                moveIndent.doClick();
                moveOutdent.doClick();
                assertEquals(6, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @Disabled
    void testCopy() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testCopy.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var copyImage = (JMenu) picturePopupMenu.getComponent(13);
                final var copyImageChooseTargetDir = copyImage.getItem(0);
                final var copyImageToZipFile = copyImage.getItem(12);
                assertEquals("Copy Image", copyImage.getText());
                assertEquals("choose target directory", copyImageChooseTargetDir.getText());
                assertEquals("to zip file", copyImageToZipFile.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(CopyToNewLocationRequest request) {
                        eventsReceived[0]++;
                    }

                    @Subscribe
                    public void handleCopyToNewZipfileRequest(CopyToNewZipfileRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                copyImageChooseTargetDir.doClick();
                copyImageToZipFile.doClick();
                assertEquals(2, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testCopyToClipboard() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testCopyToClipboard.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var copyImage = (JMenu) picturePopupMenu.getComponent(13);
                final var copyToClipboard = copyImage.getItem(13);
                assertEquals("Copy Image", copyImage.getText());
                assertEquals("Copy Image to Clipboard", copyToClipboard.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(CopyImageToClipboardRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                copyToClipboard.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @Disabled
    void testRemoveNode() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testRemoveNode.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var removeNode = (JMenuItem) picturePopupMenu.getComponent(14);
                assertEquals("Remove Node", removeNode.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(RemoveNodeRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                removeNode.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @Disabled
    void testMoveToNewLocation() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testMoveToNewLocation.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var fileOperations = (JMenu) picturePopupMenu.getComponent(15);
                assertEquals("File operations", fileOperations.getText());
                final var moveFile = (JMenu) fileOperations.getItem(2);
                assertEquals("Move File", moveFile.getText());
                final var moveToNewLocation = moveFile.getItem(0);
                assertEquals("choose target directory", moveToNewLocation.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(MoveToNewLocationRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                moveToNewLocation.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @Disabled
    void testFileRename() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testFileRename.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                assumeFalse(GraphicsEnvironment.isHeadless());
                final var fileOperations = (JMenu) picturePopupMenu.getComponent(15);
                assertEquals("File operations", fileOperations.getText());
                final var renameJMenu = (JMenu) fileOperations.getItem(3);
                assertEquals("Rename", renameJMenu.getText());
                final var fileOperationsRename = renameJMenu.getItem(0);
                assertEquals("Rename 1 file(s)", fileOperationsRename.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(RenamePictureRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                fileOperationsRename.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @Disabled
    void testFileRename4Files() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var groupInfo = new GroupInfo("GroupInfo");
                final var groupNode = new SortableDefaultMutableTreeNode(groupInfo);
                final var node1 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("nosuchfile1.jpg"), MY_PICTURE));
                groupNode.add(node1);
                final var node2 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("nosuchfile2.jpg"), MY_PICTURE));
                groupNode.add(node2);
                final var node3 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("nosuchfile3.jpg"), MY_PICTURE));
                groupNode.add(node3);
                final var node4 = new SortableDefaultMutableTreeNode(new PictureInfo(new File("nosuchfile4.jpg"), MY_PICTURE));
                groupNode.add(node4);
                final var flatGroupNavigator = new FlatGroupNavigator(groupNode);
                Settings.getPictureCollection().addToSelectedNodes(node1);
                Settings.getPictureCollection().addToSelectedNodes(node2);
                Settings.getPictureCollection().addToSelectedNodes(node3);
                Settings.getPictureCollection().addToSelectedNodes(node4);

                Settings.setLocale(Locale.ENGLISH);
                final var picturePopupMenu = new PicturePopupMenu(flatGroupNavigator, 0);
                final var fileOperations = (JMenu) picturePopupMenu.getComponent(15);
                assertEquals("File operations", fileOperations.getText());
                final var renameJMenu = (JMenu) fileOperations.getItem(3);
                assertEquals("Rename", renameJMenu.getText());
                final var fileOperationsRename = renameJMenu.getItem(0);
                assertEquals("Rename 4 file(s)", fileOperationsRename.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(final RenamePictureRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                fileOperationsRename.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    @Test
    @Disabled
    void testFileDelete() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testFileDelete.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                assumeFalse(GraphicsEnvironment.isHeadless());
                final var fileOperations = (JMenu) picturePopupMenu.getComponent(15);
                assertEquals("File operations", fileOperations.getText());
                final var fileOperationsDelete = fileOperations.getItem(4);
                assertEquals("Delete", fileOperationsDelete.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(DeleteNodeFileRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                fileOperationsDelete.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testAddCategoryMenuItem() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testAddCategoryMenuItem.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var assignCategoryMenu = (JMenuItem) picturePopupMenu.getComponent(16);
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRequest(CategoryAssignmentWindowRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                assignCategoryMenu.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testProperties() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var myPictureInfo = new PictureInfo(new File("nosuchfile-testProperties.jpg"), MY_PICTURE);
                final var myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
                final var myNavigator = new SingleNodeNavigator(myNode);
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var properties = (JMenuItem) picturePopupMenu.getComponent(17);
                assertEquals("Properties", properties.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleEvent(ShowPictureInfoEditorRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                properties.doClick();
                assertEquals(1, eventsReceived[0]);
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testConsolidateHere() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final var pictureInfo = new PictureInfo();
                final var pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
                final var navigator = new SingleNodeNavigator(pictureNode);
                try {
                    final var tempFile = File.createTempFile("testConsolidateHere", ".jpg");
                    tempFile.deleteOnExit();
                    pictureInfo.setImageLocation(tempFile);
                    final var picturePopupMenu = new PicturePopupMenu(navigator, 0);
                    final var parentNode = new SortableDefaultMutableTreeNode(new GroupInfo("Parent Group"));
                    parentNode.add(pictureNode);

                    final var consolidateHere = (JMenuItem) picturePopupMenu.getComponent(18);
                    assertEquals("Consolidate Here", consolidateHere.getText());
                    final int[] eventsReceived = {0};
                    JpoEventBus.getInstance().register(new Object() {
                        @Subscribe
                        public void handleRequest(ConsolidateGroupDialogRequest request) {
                            eventsReceived[0]++;
                        }
                    });
                    assertEquals(0, eventsReceived[0]);
                    consolidateHere.doClick();
                    assertEquals(1, eventsReceived[0]);
                    Files.delete(tempFile.toPath());
                } catch (final IOException e) {
                    fail(e.getMessage());
                }
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testReplaceEscapedSpaces() {
        final var s = "filename%20extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceEscapedSpaces(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }

    @Test
    void testReplaceDoubleEscapedSpaces() {
        final var s = "filename%20%20extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceEscapedSpaces(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }


    @Test
    void testReplace2520() {
        final var string = "/dir1/dir2/filename%2520extension%2520more.jpg";
        final Optional<String> optional = PicturePopupMenu.replace2520(string);
        assert (optional.isPresent());
        final String expected = "/dir1/dir2/filename extension more.jpg";
        LOGGER.log(Level.FINE, "Expected: {0} Actual: {1}", new Object[]{expected, optional.get()});
        assertEquals(expected, optional.get());
    }

    @Test
    void testReplaceNoEscapedSpaces() {
        final var s = "filenameExtension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceEscapedSpaces(s);
        assertFalse(o.isPresent());
    }

    @Test
    void testReplaceNoEscapedSpacesSingeChars() {
        final var s1 = "filename2Extension.jpg";
        final Optional<String> o1 = PicturePopupMenu.replaceEscapedSpaces(s1);
        o1.ifPresent(s -> fail(String.format("There wasn't supposed to be anything that changed between%n%s and%n%s", s1, s)));

        final var s2 = "filename0Extension_.jpg";
        final Optional<String> o2 = PicturePopupMenu.replaceEscapedSpaces(s2);
        o2.ifPresent(s -> fail(String.format("There wasn't supposed to be anything that changed between%n%s and%n%s", s2, s)));

        final var s3 = "filename%Extension.jpg";
        final Optional<String> o3 = PicturePopupMenu.replaceEscapedSpaces(s3);
        o3.ifPresent(s -> fail(String.format("There wasn't supposed to be anything that changed between%n%s and%n%s", s3, s)));
    }

    @Test
    void testReplaceUnderscore() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final var s = "filename_extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceUnderscore(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }

    @Test
    void testReplaceDoubleUnderscores() {
        final var s = "filename__extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceUnderscore(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }

    @Test
    void testReplaceNoUnderscores() {
        final var s = "filenameExtension%20.jpg";
        final Optional<String> o = PicturePopupMenu.replaceUnderscore(s);
        assertFalse(o.isPresent());
    }


}
