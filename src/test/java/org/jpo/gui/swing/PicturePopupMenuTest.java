package org.jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jpo.datamodel.*;
import org.jpo.eventbus.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;


/*
 Copyright (C) 2017-2021  Richard Eigenmann.
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

    /*
     * Note these tests are burdened with reflection to get at the inner
     * workings of the popup menu. Should I open up the fields in the popup menu
     * class? I think not because other classes don't need to see into the inner
     * workings of the popup menu. With the exception of this one that has to
     * make sure the details of the class are working properly.
     *
     */
    private final PictureInfo myPictureInfo = new PictureInfo(new File("nosuchfile.jpg"), "My Picture");
    private final SortableDefaultMutableTreeNode myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
    private final SingleNodeNavigator myNavigator = new SingleNodeNavigator(myNode);


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
                    final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                    final SortableDefaultMutableTreeNode verifyNode = (SortableDefaultMutableTreeNode) popupNodeField.get(picturePopupMenu);
                    final PictureInfo verifyPictureInfo = (PictureInfo) verifyNode.getUserObject();
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

    /**
     * Get the children
     */
    @Test
    void testMenuTitle() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenuItem title = (JMenuItem) picturePopupMenu.getComponent(0);
                assertEquals("My Picture", title.getText());
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
                final var picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final var showPictureJMenuItem = (JMenuItem) picturePopupMenu.getComponent(2);
                assertEquals("Show Picture", showPictureJMenuItem.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleShowPictureRequest(ShowPictureRequest request) {
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
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenuItem showMap = (JMenuItem) picturePopupMenu.getComponent(3);
                assertEquals("Show Map", showMap.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleShowPictureOnMapRequest(ShowPictureOnMapRequest request) {
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
                final PictureInfo pictureInfo = new PictureInfo();
                final SortableDefaultMutableTreeNode pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
                final SingleNodeNavigator navigator = new SingleNodeNavigator(pictureNode);
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(navigator, 0);
                try {
                    final File tempFile = File.createTempFile("testOpenFolder", ".jpg");
                    tempFile.deleteOnExit();
                    pictureInfo.setImageLocation(tempFile);

                    final JMenuItem openFolder = (JMenuItem) picturePopupMenu.getComponent(4);
                    assertEquals("Open Folder", openFolder.getText());
                    final int[] eventsReceived = {0};
                    JpoEventBus.getInstance().register(new Object() {
                        @Subscribe
                        public void handleOpenFileExplorerRequest(OpenFileExplorerRequest request) {
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
                final PictureInfo pictureInfo = new PictureInfo();
                final SortableDefaultMutableTreeNode pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
                try {
                    final File tempFile = File.createTempFile("testNavigateTo", ".jpg");
                    tempFile.deleteOnExit();
                    pictureInfo.setImageLocation(tempFile);
                    final SortableDefaultMutableTreeNode parentNode = new SortableDefaultMutableTreeNode(new GroupInfo("Parent Group"));
                    parentNode.add(pictureNode);
                    Settings.getPictureCollection().getRootNode().add(parentNode);

                    final SingleNodeNavigator navigator = new SingleNodeNavigator(pictureNode);
                    final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(navigator, 0);
                    final JMenu navigateTo = (JMenu) picturePopupMenu.getComponent(5);
                    final JMenuItem navigateTo0 = navigateTo.getItem(0);
                    assertNotNull(navigateTo0);
                    assertEquals("Navigate to", navigateTo.getText());

                    final int[] eventsReceived = {0};
                    JpoEventBus.getInstance().register(new Object() {
                        @Subscribe
                        public void handleShowGroupRequest(ShowGroupRequest request) {
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
    void testCategories() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenuItem categories = (JMenuItem) picturePopupMenu.getComponent(6);
                assertEquals("Categories", categories.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleShowCategoryUsageEditorRequest(ShowCategoryUsageEditorRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                categories.doClick();
                assertEquals(1, eventsReceived[0]);
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
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenuItem selectForEmail = (JMenuItem) picturePopupMenu.getComponent(7);
                assertEquals("Select for email", selectForEmail.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleAddPictureModesToEmailSelectionRequest(AddPictureNodesToEmailSelectionRequest request) {
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
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenuItem unselectForEmail;
                unselectForEmail = (JMenuItem) picturePopupMenu.getComponent(8);
                assertEquals("Unselect for email", unselectForEmail.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRemovePictureModesFromEmailSelectionRequest(RemovePictureNodesFromEmailSelectionRequest request) {
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
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenuItem clearEmailSelection = (JMenuItem) picturePopupMenu.getComponent(9);
                assertEquals("Clear email selection", clearEmailSelection.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleClearEmailSelectionRequest(ClearEmailSelectionRequest request) {
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

    @Test
    void testUserFunctions() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenu userFunction = (JMenu) picturePopupMenu.getComponent(10);
                final JMenuItem userFunction0 = userFunction.getItem(0);
                final JMenuItem userFunction1 = userFunction.getItem(1);
                final JMenuItem userFunction2 = userFunction.getItem(2);
                assertEquals("User Function", userFunction.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRunUserFunctionRequest(RunUserFunctionRequest request) {
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
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenu rotation = (JMenu) picturePopupMenu.getComponent(11);
                final JMenuItem rotate90 = rotation.getItem(0);
                final JMenuItem rotate180 = rotation.getItem(1);
                final JMenuItem rotate270 = rotation.getItem(2);
                final JMenuItem rotate0 = rotation.getItem(3);
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
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenuItem refreshThumbnail = (JMenuItem) picturePopupMenu.getComponent(12);
                assertEquals("Refresh Thumbnail", refreshThumbnail.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRefreshThumbnailRequest(RefreshThumbnailRequest request) {
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
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenu move = (JMenu) picturePopupMenu.getComponent(13);
                final JMenuItem moveToTop = move.getItem(Settings.getMaxDropnodes() + 1);
                final JMenuItem moveUp = move.getItem(Settings.getMaxDropnodes() + 2);
                final JMenuItem moveDown = move.getItem(Settings.getMaxDropnodes() + 3);
                final JMenuItem moveToBottom = move.getItem(Settings.getMaxDropnodes() + 4);
                final JMenuItem moveIndent = move.getItem(Settings.getMaxDropnodes() + 5);
                final JMenuItem moveOutdent = move.getItem(Settings.getMaxDropnodes() + 6);
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
    void testCopy() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                assumeFalse(GraphicsEnvironment.isHeadless());
                final JMenu copyImage = (JMenu) picturePopupMenu.getComponent(14);
                final JMenuItem copyImageChooseTargetDir = copyImage.getItem(0);
                final JMenuItem copyImageToZipFile = copyImage.getItem(12);
                assertEquals("Copy Image", copyImage.getText());
                assertEquals("choose target directory", copyImageChooseTargetDir.getText());
                assertEquals("to zip file", copyImageToZipFile.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleCopyToNewLocationRequest(CopyToNewLocationRequest request) {
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
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenu copyImage = (JMenu) picturePopupMenu.getComponent(14);
                final JMenuItem copyToClipboard = copyImage.getItem(13);
                assertEquals("Copy Image", copyImage.getText());
                assertEquals("Copy Image to Clipboard", copyToClipboard.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleCopyToClipboardRequest(CopyImageToClipboardRequest request) {
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
    void testRemoveNode() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenuItem removeNode = (JMenuItem) picturePopupMenu.getComponent(15);
                assertEquals("Remove Node", removeNode.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRemoveNodeRequest(RemoveNodeRequest request) {
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
    void testMoveToNewLocation() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenu fileOperations = (JMenu) picturePopupMenu.getComponent(16);
                assertEquals("File operations", fileOperations.getText());
                final JMenu moveFile = (JMenu) fileOperations.getItem(2);
                assertEquals("Move File", moveFile.getText());
                final JMenuItem moveToNewLocation = moveFile.getItem(0);
                assertEquals("choose target directory", moveToNewLocation.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleMoveToNewLocation(MoveToNewLocationRequest request) {
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
    void testFileRename() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                assumeFalse(GraphicsEnvironment.isHeadless());
                final JMenu fileOperations = (JMenu) picturePopupMenu.getComponent(16);
                assertEquals("File operations", fileOperations.getText());
                final JMenu renameJMenu = (JMenu) fileOperations.getItem(3);
                assertEquals("Rename", renameJMenu.getText());
                final JMenuItem fileOperationsRename = renameJMenu.getItem(0);
                assertEquals("Rename", fileOperationsRename.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleRenamePictureRequest(RenamePictureRequest request) {
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
    void testFileDelete() {
        try {
            SwingUtilities.invokeAndWait(() -> {
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                assumeFalse(GraphicsEnvironment.isHeadless());
                final JMenu fileOperations = (JMenu) picturePopupMenu.getComponent(16);
                assertEquals("File operations", fileOperations.getText());
                final JMenuItem fileOperationsDelete = fileOperations.getItem(4);
                assertEquals("Delete", fileOperationsDelete.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleDeleteNodeFileRequest(DeleteNodeFileRequest request) {
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
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenu assignCategoryMenu;
                assignCategoryMenu = (JMenu) picturePopupMenu.getComponent(17);
                final JMenuItem editCategoriesMenuItem;
                editCategoriesMenuItem = assignCategoryMenu.getItem(0);
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleShowPictureInfoEditorRequest(OpenCategoryEditorRequest request) {
                        eventsReceived[0]++;
                    }
                });
                assertEquals(0, eventsReceived[0]);
                editCategoriesMenuItem.doClick();
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
                final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                final JMenuItem properties = (JMenuItem) picturePopupMenu.getComponent(18);
                assertEquals("Properties", properties.getText());
                final int[] eventsReceived = {0};
                JpoEventBus.getInstance().register(new Object() {
                    @Subscribe
                    public void handleShowPictureInfoEditorRequest(ShowPictureInfoEditorRequest request) {
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
                final PictureInfo pictureInfo = new PictureInfo();
                final SortableDefaultMutableTreeNode pictureNode = new SortableDefaultMutableTreeNode(pictureInfo);
                final SingleNodeNavigator navigator = new SingleNodeNavigator(pictureNode);
                try {
                    final File tempFile = File.createTempFile("testConsolidateHere", ".jpg");
                    tempFile.deleteOnExit();
                    pictureInfo.setImageLocation(tempFile);
                    final PicturePopupMenu picturePopupMenu = new PicturePopupMenu(navigator, 0);
                    final SortableDefaultMutableTreeNode parentNode = new SortableDefaultMutableTreeNode(new GroupInfo("Parent Group"));
                    parentNode.add(pictureNode);

                    final JMenuItem consolidateHere = (JMenuItem) picturePopupMenu.getComponent(19);
                    assertEquals("Consolidate Here", consolidateHere.getText());
                    final int[] eventsReceived = {0};
                    JpoEventBus.getInstance().register(new Object() {
                        @Subscribe
                        public void handleConsolidateGroupRequest(ConsolidateGroupDialogRequest request) {
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
        final String s = "filename%20extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceEscapedSpaces(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }

    @Test
    void testReplaceDoubleEscapedSpaces() {
        final String s = "filename%20%20extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceEscapedSpaces(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }


    @Test
    void testReplace2520() {
        final String s = "/dir1/dir2/filename%2520extension%2520more.jpg";
        final Optional<String> o = PicturePopupMenu.replace2520(s);
        assert (o.isPresent());
        final String expected = "/dir1/dir2/filename extension more.jpg";
        LOGGER.log(Level.FINE, "Expected: {0} Actual: {1}", new Object[]{expected, o.get()});
        assertEquals(expected, o.get());
    }

    @Test
    void testReplaceNoEscapedSpaces() {
        final String s = "filenameExtension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceEscapedSpaces(s);
        assertFalse(o.isPresent());
    }

    @Test
    void testReplaceNoEscapedSpacesSingeChars() {
        final String s1 = "filename2Extension.jpg";
        final Optional<String> o1 = PicturePopupMenu.replaceEscapedSpaces(s1);
        o1.ifPresent(s -> fail(String.format("There wasn't supposed to be anything that changed between%n%s and%n%s", s1, s)));

        final String s2 = "filename0Extension_.jpg";
        final Optional<String> o2 = PicturePopupMenu.replaceEscapedSpaces(s2);
        o2.ifPresent(s -> fail(String.format("There wasn't supposed to be anything that changed between%n%s and%n%s", s2, s)));

        final String s3 = "filename%Extension.jpg";
        final Optional<String> o3 = PicturePopupMenu.replaceEscapedSpaces(s3);
        o3.ifPresent(s -> fail(String.format("There wasn't supposed to be anything that changed between%n%s and%n%s", s3, s)));
    }

    @Test
    void testReplaceUnderscore() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final String s = "filename_extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceUnderscore(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }

    @Test
    void testReplaceDoubleUnderscores() {
        final String s = "filename__extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceUnderscore(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }

    @Test
    void testReplaceNoUnderscores() {
        final String s = "filenameExtension%20.jpg";
        final Optional<String> o = PicturePopupMenu.replaceUnderscore(s);
        assertFalse(o.isPresent());
    }


}
