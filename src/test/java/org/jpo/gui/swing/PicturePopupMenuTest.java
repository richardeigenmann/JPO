package org.jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.assertj.swing.edt.GuiActionRunnable;
import org.assertj.swing.edt.GuiActionRunner;
import org.jpo.datamodel.*;
import org.jpo.eventbus.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;


/*
 Copyright (C) 2017-2020  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 without even the implied warranty of MERCHANTABILITY or FITNESS 
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
public class PicturePopupMenuTest {

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
    private final PictureInfo myPictureInfo = new PictureInfo();
    private final GroupInfo myGroupInfo = new GroupInfo("Parent Group");
    private final SortableDefaultMutableTreeNode myNode = new SortableDefaultMutableTreeNode(myPictureInfo);
    private final SortableDefaultMutableTreeNode myParentNode = new SortableDefaultMutableTreeNode(myGroupInfo);
    private final SingleNodeNavigator myNavigator = new SingleNodeNavigator(myNode);
    private PicturePopupMenu myPicturePopupMenu;
    private JMenuItem title;
    private JMenuItem showPictureJMenuItem;
    private JMenuItem showMap;
    private JMenuItem openFolder;
    private JMenu navigateTo;
    private JMenuItem navigateTo_0;
    private JMenuItem categories;
    private JMenuItem selectForEmail;
    private JMenuItem unselectForEmail;
    private JMenuItem clearEmailSelection;
    private JMenu userFunction;
    private JMenuItem userFunction_0;
    private JMenuItem userFunction_1;
    private JMenuItem userFunction_2;
    private JMenu rotation;
    private JMenuItem rotate90;
    private JMenuItem rotate180;
    private JMenuItem rotate270;
    private JMenuItem rotate0;
    private JMenuItem refreshThumbnail;
    private JMenu move;
    private JMenuItem moveToTop;
    private JMenuItem moveUp;
    private JMenuItem moveDown;
    private JMenuItem moveToBottom;
    private JMenuItem moveIndent;
    private JMenuItem moveOutdent;
    private JMenu copyImage;
    private JMenuItem copyImageChooseTargetDir;
    private JMenuItem copyImageToZipFile;
    private JMenuItem copyToClipboard;
    private JMenuItem removeNode;
    private JMenu fileOperations;
    private JMenu renameJMenu;
    private JMenu moveImage;
    private JMenuItem moveToNewLocation;
    private JMenuItem fileOperationsRename;
    private JMenuItem fileOperationsDelete;
    private JMenu assignCategoryMenu;
    private JMenuItem editCategoriesMenuItem;
    private JMenuItem properties;
    private JMenuItem consolidateHere;

    @BeforeAll
    public static void setUpOnce() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        FailOnThreadViolationRepaintManager.install();
    }

    /**
     * Creates the objects for testing. Runs on the EDT.
     */
    @BeforeEach
    public void setUp() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        myPictureInfo.setDescription("My Picture");
        try {
            final File temp = File.createTempFile("JPO-Unit-Test", ".jpg");
            temp.deleteOnExit();
            myPictureInfo.setImageLocation(temp);
        } catch (final IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        myParentNode.add(myNode);
        Settings.getPictureCollection().getRootNode().add(myParentNode);

        try {
            SwingUtilities.invokeAndWait(() -> {
                myPicturePopupMenu = new PicturePopupMenu(myNavigator, 0);
                title = (JMenuItem) myPicturePopupMenu.getComponent(0);
                showPictureJMenuItem = (JMenuItem) myPicturePopupMenu.getComponent(2);
                showMap = (JMenuItem) myPicturePopupMenu.getComponent(3);
                openFolder = (JMenuItem) myPicturePopupMenu.getComponent(4);
                navigateTo = (JMenu) myPicturePopupMenu.getComponent(5);
                navigateTo_0 = navigateTo.getItem(0);
                categories = (JMenuItem) myPicturePopupMenu.getComponent(6);
                selectForEmail = (JMenuItem) myPicturePopupMenu.getComponent(7);
                unselectForEmail = (JMenuItem) myPicturePopupMenu.getComponent(8);
                clearEmailSelection = (JMenuItem) myPicturePopupMenu.getComponent(9);
                userFunction = (JMenu) myPicturePopupMenu.getComponent(10);
                userFunction_0 = userFunction.getItem(0);
                userFunction_1 = userFunction.getItem(1);
                userFunction_2 = userFunction.getItem(2);
                rotation = (JMenu) myPicturePopupMenu.getComponent(11);
                rotate90 = rotation.getItem(0);
                rotate180 = rotation.getItem(1);
                rotate270 = rotation.getItem(2);
                rotate0 = rotation.getItem(3);
                refreshThumbnail = (JMenuItem) myPicturePopupMenu.getComponent(12);
                move = (JMenu) myPicturePopupMenu.getComponent(13);
                moveToTop = move.getItem(Settings.getMaxDropnodes() + 1);
                moveUp = move.getItem(Settings.getMaxDropnodes() + 2);
                moveDown = move.getItem(Settings.getMaxDropnodes() + 3);
                moveToBottom = move.getItem(Settings.getMaxDropnodes() + 4);
                moveIndent = move.getItem(Settings.getMaxDropnodes() + 5);
                moveOutdent = move.getItem(Settings.getMaxDropnodes() + 6);
                copyImage = (JMenu) myPicturePopupMenu.getComponent(14);
                copyImageChooseTargetDir = copyImage.getItem(0);
                copyImageToZipFile = copyImage.getItem(12);
                copyToClipboard = copyImage.getItem(13);
                removeNode = (JMenuItem) myPicturePopupMenu.getComponent(15);
                fileOperations = (JMenu) myPicturePopupMenu.getComponent(16);
                moveImage = (JMenu) fileOperations.getItem(2);
                moveToNewLocation = moveImage.getItem(0);
                renameJMenu = (JMenu) fileOperations.getItem(3);
                fileOperationsRename = renameJMenu.getItem(0);
                fileOperationsDelete = fileOperations.getItem(4);
                assignCategoryMenu = (JMenu) myPicturePopupMenu.getComponent(17);
                editCategoriesMenuItem = assignCategoryMenu.getItem(0);
                properties = (JMenuItem) myPicturePopupMenu.getComponent(18);
                consolidateHere = (JMenuItem) myPicturePopupMenu.getComponent(19);
            });
        } catch (final InterruptedException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
            e.printStackTrace();
            fail(e.getMessage());
            Thread.currentThread().interrupt();
        } catch (final InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, "Hit a InvocationTargetException. Message is: {0}", e.getMessage());
            e.printStackTrace();
            final Throwable cause = e.getCause();
            LOGGER.log(Level.SEVERE, "Cause object: {0}, message: {1}", new Object[]{cause, cause.getMessage()});
            fail(e.getMessage());
        }

    }

    /**
     * Test that out Group Node was created for the correct node.
     */
    @Test
    public void testRememberingPopupNode() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    final Field popupNodeField;
                    popupNodeField = PicturePopupMenu.class.getDeclaredField("popupNode");
                    popupNodeField.setAccessible(true);
                    final SortableDefaultMutableTreeNode verifyNode = (SortableDefaultMutableTreeNode) popupNodeField.get(myPicturePopupMenu);
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
    public void testGetChildren() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                assertEquals("My Picture", title.getText());
                assertEquals("Show Picture", showPictureJMenuItem.getText());
                assertEquals("Show Map", showMap.getText());
                assertEquals("Navigate to", navigateTo.getText());
                assertEquals("Categories", categories.getText());
                assertEquals("Select for email", selectForEmail.getText());
                assertEquals("User Function", userFunction.getText());
                assertEquals("Rotation", rotation.getText());
                assertEquals("Rotate Right 90", rotate90.getText());
                assertEquals("Rotate 180", rotate180.getText());
                assertEquals("Rotate Left 270", rotate270.getText());
                assertEquals("No Rotation", rotate0.getText());
                assertEquals("Refresh Thumbnail", refreshThumbnail.getText());
                assertEquals("Move", move.getText());
                assertEquals("to Top", moveToTop.getText());
                assertEquals("Up", moveUp.getText());
                assertEquals("Down", moveDown.getText());
                assertEquals("indent", moveIndent.getText());
                assertEquals("outdent", moveOutdent.getText());
                assertEquals("to Bottom", moveToBottom.getText());
                assertEquals("Copy Image", copyImage.getText());
                assertEquals("choose target directory", copyImageChooseTargetDir.getText());
                assertEquals("to zip file", copyImageToZipFile.getText());
                assertEquals("Copy Image to Clipboard", copyToClipboard.getText());
                assertEquals("Remove Node", removeNode.getText());
                assertEquals("File operations", fileOperations.getText());
                assertEquals("Properties", properties.getText());
                assertEquals("Consolidate Here", consolidateHere.getText());
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
    public void testShowPictureJMenuItemClick() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleShowPictureRequest(ShowPictureRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) showPictureJMenuItem::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    /**
     * Test clicking showMap
     */
    @Test
    public void testShowMap() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleShowPictureOnMapRequest(ShowPictureOnMapRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) showMap::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    /**
     * Test clicking showMap
     */
    @Test
    public void testOpenFolder() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleOpenFileExplorerRequest(OpenFileExplorerRequest request) {
                eventsReceived[0]++;
            }
        });
        // Before clicking on the node the event count should be 0
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) openFolder::doClick);
        // After clicking on the node the event count should be 1
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testNavigateTo() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleShowGroupRequest(ShowGroupRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) navigateTo_0::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testCategories() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleShowCategoryUsageEditorRequest(ShowCategoryUsageEditorRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) categories::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testSelectForEmail() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleAddPictureModesToEmailSelectionRequest(AddPictureNodesToEmailSelectionRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) selectForEmail::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testUnselectForEmail() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleRemovePictureModesFromEmailSelectionRequest(RemovePictureNodesFromEmailSelectionRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) unselectForEmail::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testClearEmailSelection() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleClearEmailSelectionRequest(ClearEmailSelectionRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) clearEmailSelection::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testUserFunctions() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleRunUserFunctionRequest(RunUserFunctionRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute(() -> {
            userFunction_0.doClick();
            userFunction_1.doClick();
            userFunction_2.doClick();
        });
        assertEquals(3, eventsReceived[0]);
    }

    @Test
    public void testRotation() {
        assumeFalse(GraphicsEnvironment.isHeadless());
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
        GuiActionRunner.execute((GuiActionRunnable) rotate90::doClick);
        GuiActionRunner.execute((GuiActionRunnable) rotate180::doClick);
        GuiActionRunner.execute((GuiActionRunnable) rotate270::doClick);
        GuiActionRunner.execute((GuiActionRunnable) rotate0::doClick);
        assertEquals(4, eventsReceived[0]);
    }

    @Test
    public void testRefresh() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleRefreshThumbnailRequest(RefreshThumbnailRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) refreshThumbnail::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testMove() {
        assumeFalse(GraphicsEnvironment.isHeadless());
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
        GuiActionRunner.execute((GuiActionRunnable) moveToTop::doClick);
        GuiActionRunner.execute((GuiActionRunnable) moveDown::doClick);
        GuiActionRunner.execute((GuiActionRunnable) moveUp::doClick);
        GuiActionRunner.execute((GuiActionRunnable) moveToBottom::doClick);
        GuiActionRunner.execute((GuiActionRunnable) moveIndent::doClick);
        GuiActionRunner.execute((GuiActionRunnable) moveOutdent::doClick);
        assertEquals(6, eventsReceived[0]);
    }

    @Test
    public void testCopyToClipboard() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleCopyToClipboardRequest(CopyImageToClipboardRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) copyToClipboard::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testRemoveNode() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleRemoveNodeRequest(RemoveNodeRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute(() -> removeNode.doClick());
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testMoveToNewLocation() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleMoveToNewLocation(MoveToNewLocationRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) moveToNewLocation::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testFileRename() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleRenamePictureRequest(RenamePictureRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) fileOperationsRename::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testFileDelete() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleDeleteNodeFileRequest(DeleteNodeFileRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) fileOperationsDelete::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testAddCategoryMenuItem() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleShowPictureInfoEditorRequest(OpenCategoryEditorRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) editCategoriesMenuItem::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testProperties() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleShowPictureInfoEditorRequest(ShowPictureInfoEditorRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) properties::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testConsolidateHere() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final int[] eventsReceived = {0};
        JpoEventBus.getInstance().register(new Object() {
            @Subscribe
            public void handleConsolidateGroupRequest(ConsolidateGroupDialogRequest request) {
                eventsReceived[0]++;
            }
        });
        assertEquals(0, eventsReceived[0]);
        GuiActionRunner.execute((GuiActionRunnable) consolidateHere::doClick);
        assertEquals(1, eventsReceived[0]);
    }

    @Test
    public void testReplaceEscapedSpaces() {
        final String s = "filename%20extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceEscapedSpaces(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }

    @Test
    public void testReplaceDoubleEscapedSpaces() {
        final String s = "filename%20%20extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceEscapedSpaces(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }


    @Test
    public void testReplace2520() {
        final String s = "/dir1/dir2/filename%2520extension%2520more.jpg";
        final Optional<String> o = PicturePopupMenu.replace2520(s);
        assert (o.isPresent());
        final String expected = "/dir1/dir2/filename extension more.jpg";
        LOGGER.log(Level.FINE, "Expected: {0} Actual: {1}", new Object[]{expected, o.get()});
        assertEquals(expected, o.get());
    }

    @Test
    public void testReplaceNoEscapedSpaces() {
        final String s = "filenameExtension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceEscapedSpaces(s);
        assertFalse(o.isPresent());
    }

    @Test
    public void testReplaceNoEscapedSpacesSingeChars() {
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
    public void testReplaceUnderscore() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final String s = "filename_extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceUnderscore(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }

    @Test
    public void testReplaceDoubleUnderscores() {
        final String s = "filename__extension.jpg";
        final Optional<String> o = PicturePopupMenu.replaceUnderscore(s);
        assert (o.isPresent());
        assertEquals("filename extension.jpg", o.get());
    }

    @Test
    public void testReplaceNoUnderscores() {
        final String s = "filenameExtension%20.jpg";
        final Optional<String> o = PicturePopupMenu.replaceUnderscore(s);
        assertFalse(o.isPresent());
    }

    @Test
    public void testFindLinkingGroups() {
        final Set<SortableDefaultMutableTreeNode> linkingGroups = Settings.getPictureCollection().findLinkingGroups(myNode);
        assertEquals(1, ((Set<?>) linkingGroups).size());
    }

}
