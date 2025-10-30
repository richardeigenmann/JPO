package org.jpo.gui;

import org.jetbrains.annotations.TestOnly;
import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.datamodel.*;
import org.jpo.datamodel.ScalablePicture.ScalablePictureStatus;
import org.jpo.eventbus.*;
import org.jpo.gui.swing.ChangeWindowPopupMenu;
import org.jpo.gui.swing.ResizableJFrame.WindowSize;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.*;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.datamodel.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_ERROR;
import static org.jpo.datamodel.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_READY;

/*
 Copyright (C) 2002-2025 Richard Eigenmann, Zürich, Switzerland
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
 * PictureViewer is a Controller that manages a window which displays a picture. It
 * concerns itself with which picture from the Naviator Context should be displayed.
 * It provides navigation control over the collection as well as mouse and
 * keyboard control over the zooming.
 * <p> The user can zoom in on a picture coordinate by clicking the left mouse
 * button. The middle button scales the picture so that it fits in the available
 * space and centers it there. The right mouse button zooms out.</p>
 *
 * <img src="PictureViewer.png" alt="Picture Viewer">
 */
public class PictureViewer implements NodeNavigatorListener, AutoAdvanceInterface {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PictureViewer.class.getName());

    private final PictureFrameController pictureFrameController = new PictureFrameController();
    /**
     * popup menu for window mode changing
     */
    private final ChangeWindowPopupMenu changeWindowPopupMenu = new ChangeWindowPopupMenu(pictureFrameController.getPictureFrame().getResizableJFrame());
    /**
     * the context of the browsing
     */
    private NodeNavigatorInterface myNodeNavigator;
    /**
     * the position in the context being shown
     */
    private int myIndex;
    /**
     * the timer that can call back into the object with the instruction to load
     * the next image
     */
    private Timer advanceTimer;

    /**
     * This is a Controller that works off a NodeNavigatorInterface. It opens a new window
     * and shows the picture pointed at by the NodeNavigator and the index.
     * It has buttons to navigate, shows the description and responds to mouse actions of the
     * user.
     */
    public PictureViewer(final ShowPictureRequest request) {
        attachListeners();
        showNode(request.nodeNavigator(), request.currentIndex());
    }

    private void attachListeners() {

        pictureFrameController.getPictureFrame().getResizableJFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                closeViewer();
            }
        });

        addKeyListener(pictureFrameController.getPictureFrame().getPictureController());

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().rotateLeftJButton.addActionListener((ActionEvent e) -> {
            JpoEventBus.getInstance().post(new RotatePicturesRequest(List.of(getCurrentNode()), 270, QUEUE_PRIORITY.HIGH_PRIORITY));
            pictureFrameController.getPictureFrame().getPictureController().requestFocusInWindow();
        });

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().rotateRightJButton.addActionListener((ActionEvent e) -> {
            JpoEventBus.getInstance().post(new RotatePicturesRequest(List.of(getCurrentNode()), 90, QUEUE_PRIORITY.HIGH_PRIORITY));
            pictureFrameController.getPictureFrame().getPictureController().requestFocusInWindow();
        });

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().zoomInJButton.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new PictureControllerZoomRequest(pictureFrameController.getPictureFrame().getPictureController(), Zoom.IN)));

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().zoomOutJButton.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new PictureControllerZoomRequest(pictureFrameController.getPictureFrame().getPictureController(), Zoom.OUT)));

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().fullScreenJButton.addActionListener((ActionEvent e) -> requestScreenSizeMenu());

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().popupMenuJButton.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPicturePopUpMenuRequest(myNodeNavigator, myIndex, pictureFrameController.getPictureFrame().getPictureViewerNavBar(), 120, 0)));

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().infoJButton.addActionListener((ActionEvent e) -> cycleInfoDisplay());

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().resetJButton.addActionListener((ActionEvent e) -> pictureFrameController.getPictureFrame().getPictureController().resetPicture());

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().speedSlider.addChangeListener((ChangeEvent ce) -> {
            if (!pictureFrameController.getPictureFrame().getPictureViewerNavBar().speedSlider.getValueIsAdjusting()) {
                setTimerDelay(pictureFrameController.getPictureFrame().getPictureViewerNavBar().speedSlider.getValue());
            }
        });

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().closeJButton.addActionListener((ActionEvent e) -> closeViewer());

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().previousJButton.addActionListener((ActionEvent e) -> requestPriorPicture());

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().getNextJButton().addActionListener((ActionEvent e) -> requestNextPicture());

        pictureFrameController.getPictureFrame().getPictureViewerNavBar().clockJButton.addActionListener((ActionEvent e) -> doAutoAdvanceClick());
    }

    private void addKeyListener(OverlayedPictureController pictureJPanel) {
        pictureJPanel.addKeyListener(new KeyAdapter() {
            /**
             * method that analysed the key that was pressed
             */
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                final var k = keyEvent.getKeyCode();
                if ((k == KeyEvent.VK_I)) {
                    pictureFrameController.getPictureFrame().cycleInfoDisplay();
                    keyEvent.consume();
                } else if ((k == KeyEvent.VK_N)) {
                    requestNextPicture();
                    keyEvent.consume();
                } else if ((k == KeyEvent.VK_M)) {
                    keyEvent.consume();
                    JpoEventBus.getInstance().post(new ShowPicturePopUpMenuRequest(myNodeNavigator, myIndex, pictureFrameController.getPictureFrame().getPictureViewerNavBar(), 120, 0));
                } else if ((k == KeyEvent.VK_P)) {
                    requestPriorPicture();
                    keyEvent.consume();
                } else if ((k == KeyEvent.VK_F)) {
                    requestScreenSizeMenu();
                    keyEvent.consume();
                } else if ((k == KeyEvent.VK_ESCAPE)) {
                    keyEvent.consume();
                    closeViewer();
                }
                if (!keyEvent.isConsumed()) {
                    JOptionPane.showMessageDialog(pictureFrameController.getPictureFrame().getResizableJFrame(),
                            JpoResources.getResource("PictureViewerKeycodes"),
                            JpoResources.getResource("PictureViewerKeycodesTitle"),
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }




    /**
     * Don't use: accessor to the private closeViewer function for unit tests.
     */
    @TestOnly
    public void closeViewerTest() {
        closeViewer();
    }

    /**
     * Closes the PictureViewer and all dangling references.
     */
    private void closeViewer() {
        if (myNodeNavigator != null) {
            myNodeNavigator.removeNodeNavigatorListener(this);
        }
        stopTimer();
        pictureFrameController.getPictureFrame().getRid();
    }


    /**
     * Returns the current Node.
     *
     * @return The current node as defined by the mySetOfNodes
     * NodeNavigatorInterface and the myIndex. If the set of nodes has not been
     * initialised or there is some other error null shall be returned.
     */
    private SortableDefaultMutableTreeNode getCurrentNode() {
        try {
            return myNodeNavigator.getNode(myIndex);
        } catch (final NullPointerException npe) {
            LOGGER.log(Level.WARNING, "Got a npe on node {0}. Message: {1}", new Object[]{myIndex, npe.getMessage()});
            return null;
        }
    }

    /**
     * Don't use: accessor to the private getCurrentNode method for unit tests.
     *
     * @return the current node
     */
    @TestOnly
    public SortableDefaultMutableTreeNode getCurrentNodeTest() {
        return getCurrentNode();
    }

    /**
     * Shows a resize popup menu
     */
    private void requestScreenSizeMenu() {
        changeWindowPopupMenu.show(pictureFrameController.getPictureFrame().getPictureViewerNavBar(), 96, (int) (0 - changeWindowPopupMenu.getSize().getHeight()));
        pictureFrameController.getPictureFrame().getPictureController().requestFocusInWindow();
    }

    /**
     * Puts the picture of the indicated node onto the viewer panel
     *
     * @param newNodeNavigator The set of nodes from which one picture is to be
     *                     shown
     * @param newIndex      The index of the set of nodes to be shown.
     * TODO: this should probably become a request from the bus and possibly this should be private
     * The AutoAdvanceDialog is making this problematic
     */
    public void showNode(final NodeNavigatorInterface newNodeNavigator,
                         final int newIndex) {
        LOGGER.log(Level.INFO, "Navigator: {0} Nodes: {1} Index: {2}", new Object[]{newNodeNavigator, newNodeNavigator.getNumberOfNodes(), newIndex});
        Tools.checkEDT();

        // Validate the inputs
        final var node = newNodeNavigator.getNode(newIndex);
        if (node == null) {
            LOGGER.log(Level.SEVERE, "The new node is null. Aborting. mySetOfNodes: {0}, index: {1}", new Object[]{newNodeNavigator, newIndex});
            closeViewer();
            return;
        }

        if (!(node.getUserObject() instanceof final PictureInfo pictureInfo)) {
            LOGGER.log(Level.SEVERE,
                    "The new node is not for a PictureInfo object. Aborting. userObject class: {0}, mySetOfNodes: {1}, index: {2}",
                    new Object[]{node.getUserObject().getClass(), newNodeNavigator, newIndex});
            closeViewer();
            return;
        }

        if (this.myNodeNavigator == null) {
            LOGGER.log(Level.INFO, "We do not have a nodeNavigator, so we set it fresh.");
            this.myNodeNavigator = newNodeNavigator;
            newNodeNavigator.addNodeNavigatorListener(this);
        } else {
            if (!this.myNodeNavigator.equals(newNodeNavigator)) {
                LOGGER.log(Level.FINE, "Got a new navigator: old: {0} new: {1}", new Object[]{this.myNodeNavigator, newNodeNavigator});
                LOGGER.log(Level.FINE, "Removing the navigatorListener from the the old nodeNavigator");
                this.myNodeNavigator.removeNodeNavigatorListener(this);
                LOGGER.log(Level.FINE, "Attaching the navigatorListener to the new nodeNavigator");
                this.myNodeNavigator = newNodeNavigator;
                newNodeNavigator.addNodeNavigatorListener(this);
            }
        }

        LOGGER.log(Level.FINE, "Setting my Index to {0}", newIndex);
        this.myIndex = newIndex;


        pictureFrameController.setPicture(pictureInfo);

        setIconDecorations();
        pictureFrameController.getPictureFrame().getPictureController().requestFocusInWindow();
    }


    /**
     * gets called when the Navigator notices a change
     */
    @Override
    public void nodeLayoutChanged() {
        LOGGER.info("Got notified to relayout");
        showNode(myNodeNavigator, myIndex);

    }

    /**
     * Request the PictureViewer to display the next picture. It calls
     * {@link SortableDefaultMutableTreeNode#getNextPicture} to find the image.
     * If the call returned a non-null node {@link #showNode} is called to
     * request the loading and display of the new picture.
     *
     * @see #requestPriorPicture()
     */
    private void requestNextPicture() {
        if (myNodeNavigator.getNumberOfNodes() > myIndex + 1) {

            SwingUtilities.invokeLater(
                    () -> showNode(myNodeNavigator, myIndex + 1)
            );
        }
    }

    /**
     * if a request comes in to show the previous picture the data model is
     * asked for the prior image and if one is returned it is displayed.
     *
     * @see #requestNextPicture()
     */
    private void requestPriorPicture() {
        if (myIndex > 0) {
            SwingUtilities.invokeLater(() -> showNode(myNodeNavigator, myIndex - 1));
        }
    }

    /**
     * Handles the click on the clock icon by bringing up the dialog for the AutoAdvance timer
     * or shutting a running AutoAdvance timer down.
     */
    private void doAutoAdvanceClick() {
        if (advanceTimer != null) {
            stopTimer();
            pictureFrameController.getPictureFrame().getPictureViewerNavBar().clockJButton.setClockIdle();
        } else {
            JpoEventBus.getInstance().post(
                    new ShowAutoAdvanceDialogRequest(pictureFrameController.getPictureFrame().getResizableJFrame(), Objects.requireNonNull(getCurrentNode()), this));
        }

        pictureFrameController.getPictureFrame().getPictureController().requestFocusInWindow();
    }

    /**
     * This method sets up the Advance Timer
     *
     * @param seconds Seconds
     */
    @Override
    public void startAdvanceTimer(int seconds) {

        Tools.checkEDT();
        advanceTimer = new Timer(seconds * 1000, (ActionEvent e) -> {
            if (readyToAdvance()) {
                requestNextPicture();
            }
        });
        advanceTimer.start();
        pictureFrameController.getPictureFrame().getPictureViewerNavBar().clockJButton.setClockBusy();
        pictureFrameController.getPictureFrame().getPictureViewerNavBar().showDelaySlider();
    }

    /**
     * This method sets up the Advance Timer
     *
     * @param delay the delay (in seconds)
     */
    private void setTimerDelay(int delay) {
        if (advanceTimer != null) {
            advanceTimer.setDelay(delay * 1000);
        }
    }

    /**
     * method to stop any timer that might be running
     */
    private void stopTimer() {
        if (advanceTimer != null) {
            advanceTimer.stop();
        }

        advanceTimer = null;
        pictureFrameController.getPictureFrame().getPictureViewerNavBar().hideDelaySlider();
    }

    /**
     * Tells the AdvanceTimer whether it is OK to advance the picture or not
     * This important to avoid the submission of new picture requests before the
     * old ones have been met.
     *
     * @return true if ready to advance
     */
    private boolean readyToAdvance() {
        final OverlayedPictureController pictureJPanel = pictureFrameController.getPictureFrame().getPictureController();
        final ScalablePictureStatus status = pictureJPanel.getScalablePicture().getStatusCode();
        return (status == SCALABLE_PICTURE_READY) || (status == SCALABLE_PICTURE_ERROR);
    }

    /**
     * This function cycles to the next info display overlay.
     */
    private void cycleInfoDisplay() {
        pictureFrameController.getPictureFrame().cycleInfoDisplay();
    }

    /**
     * The location and size of the Window can be changed by a call to this
     * method
     *
     * @param newMode new window mode
     */
    public void switchWindowMode(final WindowSize newMode) {
        pictureFrameController.getPictureFrame().switchWindowMode(newMode);
    }

    /**
     * This method looks at the position the currentNode is in regard to its
     * siblings and changes the forward and back icons to reflect the position
     * of the current node.
     */
    private void setIconDecorations() {
        setIconDecorationPreviousButton();
        setIconDecorationNextButton();
    }

    private void setIconDecorationPreviousButton() {
        final var currentNode = getCurrentNode();
        if (currentNode == null) {
            return;
        }
        // let's see what we have in the way of previous siblings..
        if (currentNode.getPreviousSibling() != null) {
            pictureFrameController.getPictureFrame().getPictureViewerNavBar().setPreviousButtonHasLeft();
        } else {
            // determine if there are any previous nodes that are not groups.
            var testNode = currentNode.getPreviousNode();
            while ((testNode != null) && (!(testNode.getUserObject() instanceof PictureInfo))) {
                testNode = testNode.getPreviousNode();
            }
            if (testNode == null) {
                pictureFrameController.getPictureFrame().getPictureViewerNavBar().setPreviousButtonBeginning();
            } else {
                pictureFrameController.getPictureFrame().getPictureViewerNavBar().setPreviousButtonHasPrevious();
            }
        }
    }

    private void setIconDecorationNextButton() {
        final SortableDefaultMutableTreeNode currentNode = getCurrentNode();
        if (currentNode == null) {
            return;
        }
        // Set the next and back icons
        final DefaultMutableTreeNode nextNode = currentNode.getNextSibling();
        if (nextNode != null) {
            if (nextNode.getUserObject() instanceof PictureInfo) {
                // because there is a next sibling object of type
                // PictureInfo we should set the next icon to the
                // icon that indicates a next picture in the group
                pictureFrameController.getPictureFrame().getPictureViewerNavBar().setNextButtonHasRight();
            } else {
                // it must be a GroupInfo node
                // since we must descend into it, it gets a nextnext icon.
                pictureFrameController.getPictureFrame().getPictureViewerNavBar().setNextButtonHasNext();
            }
        } else // the getNextSibling() method returned null
        // if the getNextNode also returns null this was the end of the album
        // otherwise there are more pictures in the next group.
        {
            if (currentNode.getNextNode() != null) {
                pictureFrameController.getPictureFrame().getPictureViewerNavBar().setNextButtonHasNext();
            } else {
                pictureFrameController.getPictureFrame().getPictureViewerNavBar().setNextButtonEnd();
            }
        }
    }

}
