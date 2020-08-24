package org.jpo.gui;

import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.RotatePictureRequest;
import org.jpo.eventbus.ShowAutoAdvanceDialogRequest;
import org.jpo.eventbus.ShowPicturePopUpMenuRequest;
import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.datamodel.*;
import org.jpo.gui.ScalablePicture.ScalablePictureStatus;
import org.jpo.gui.SourcePicture.SourcePictureStatus;
import org.jpo.gui.swing.ChangeWindowPopupMenu;
import org.jpo.gui.swing.PictureFrame;
import org.jpo.gui.swing.ResizableJFrame.WindowSize;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_ERROR;
import static org.jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_READY;


/*
 Copyright (C) 2002 - 2019  Richard Eigenmann, Zürich, Switzerland
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
 * PictureViewer is a Controller that manages a window which displays a picture.
 * It provides navigation control over the collection as well as mouse and
 * keyboard control over the zooming.
 * <p>
 * The user can zoom in on a picture coordinate by clicking the left mouse
 * button. The middle button scales the picture so that it fits in the available
 * space and centers it there. The right mouse button zooms out.<p>
 *
 *
 * <img src="../PictureViewer.png" alt="Picture Viewer">
 */
public class PictureViewer implements PictureInfoChangeListener, NodeNavigatorListener, AutoAdvanceInterface {

    /**
     * PictureFrame
     */
    private final PictureFrame pictureFrame = new PictureFrame();

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PictureViewer.class.getName());

    /**
     * Brings up a window in which a picture node is displayed. This class
     * handles all the user interaction such as zoom in / out, drag, navigation,
     * information display and keyboard keys.
     */
    public PictureViewer() {
        attachListeners();
    }

    private void attachListeners() {
        OverlayedPictureController pictureJPanel = pictureFrame.getPictureController();
        pictureJPanel.addStatusListener(new ScalablePictureListener() {

            /**
             * this method gets invoked from the PicturePane object to notify of
             * status changes. It updates the description panel at the bottom of
             * the screen with the status. If the status was a notification of
             * the image starting to load the progress bar is made visible. Any
             * other status hides the progress bar.
             *
             * @param pictureStatusCode the status code
             * @param pictureStatusMessage the status message
             */
            @Override
            public void scalableStatusChange(final ScalablePictureStatus pictureStatusCode,
                                             final String pictureStatusMessage) {
                Runnable runnable = () -> {
                    switch (pictureStatusCode) {
                        case SCALABLE_PICTURE_UNINITIALISED:
                            pictureFrame.setProgressBarVisible(false);
                            break;
                        case SCALABLE_PICTURE_GARBAGE_COLLECTION:
                            pictureFrame.setProgressBarVisible(false);
                            break;
                        case SCALABLE_PICTURE_LOADING:
                            pictureFrame.setProgressBarVisible(true);
                            break;
                        case SCALABLE_PICTURE_LOADED:
                            pictureFrame.setProgressBarVisible(false);
                            break;
                        case SCALABLE_PICTURE_SCALING:
                            pictureFrame.setProgressBarVisible(false);
                            break;
                        case SCALABLE_PICTURE_READY:
                            pictureFrame.setProgressBarVisible(false);
                            pictureFrame.getResizableJFrame().toFront();
                            break;
                        case SCALABLE_PICTURE_ERROR:
                            pictureFrame.setProgressBarVisible(false);
                            break;
                        default:
                            LOGGER.log(Level.WARNING, "Got called with a code that is not understood: {0} {1}", new Object[]{pictureStatusCode, pictureStatusMessage});
                            break;
                    }
                };
                if (SwingUtilities.isEventDispatchThread()) {
                    runnable.run();
                } else {
                    SwingUtilities.invokeLater(runnable);
                }
            }

            /**
             * method that gets invoked from the PicturePane object to notify of
             * status changes
             *
             * @param statusCode the status code
             * @param percentage the percentage
             */
            @Override
            public void sourceLoadProgressNotification(final SourcePictureStatus statusCode,
                                                       final int percentage) {
                Runnable runnable = () -> {
                    switch (statusCode) {
                        case SOURCE_PICTURE_LOADING_STARTED:
                            pictureFrame.setProgressBarValue(0);
                            pictureFrame.setProgressBarVisible(true);
                            break;
                        case SOURCE_PICTURE_LOADING_PROGRESS:
                            pictureFrame.setProgressBarValue(percentage);
                            pictureFrame.setProgressBarVisible(true);
                            break;
                        default: // SOURCE_PICTURE_LOADING_COMPLETED:
                            pictureFrame.setProgressBarVisible(false);
                            break;
                    }
                };
                if (SwingUtilities.isEventDispatchThread()) {
                    runnable.run();
                } else {
                    SwingUtilities.invokeLater(runnable);
                }
            }
        });

        pictureFrame.getResizableJFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                closeViewer();
            }
        });

        pictureFrame.getFocussableDescriptionField().addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                saveChangedDescription();
            }
        });

        pictureJPanel.addKeyListener(new KeyAdapter() {
            /**
             * method that analysed the key that was pressed
             */
            @Override
            public void keyPressed(KeyEvent keyEvent) {
                int k = keyEvent.getKeyCode();
                if ((k == KeyEvent.VK_I)) {
                    pictureFrame.cycleInfoDisplay();
                    keyEvent.consume();
                } else if ((k == KeyEvent.VK_N)) {
                    requestNextPicture();
                    keyEvent.consume();
                } else if ((k == KeyEvent.VK_M)) {
                    keyEvent.consume();
                    JpoEventBus.getInstance().post(new ShowPicturePopUpMenuRequest(mySetOfNodes, myIndex, pictureFrame.getPictureViewerNavBar(), 120, 0));
                } else if ((k == KeyEvent.VK_P)) {
                    requestPriorPicture();
                    keyEvent.consume();
                } else if ((k == KeyEvent.VK_F)) {
                    requestScreenSizeMenu();
                    keyEvent.consume();
                }
                if (!keyEvent.isConsumed()) {
                    JOptionPane.showMessageDialog(pictureFrame.getResizableJFrame(),
                            Settings.jpoResources.getString("PictureViewerKeycodes"),
                            Settings.jpoResources.getString("PictureViewerKeycodesTitle"),
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        pictureFrame.getPictureViewerNavBar().rotateLeftJButton.addActionListener((ActionEvent e) -> {
            JpoEventBus.getInstance().post(new RotatePictureRequest(getCurrentNode(), 270, QUEUE_PRIORITY.HIGH_PRIORITY));
            pictureFrame.getPictureController().requestFocusInWindow();
        });

        pictureFrame.getPictureViewerNavBar().rotateRightJButton.addActionListener((ActionEvent e) -> {
            JpoEventBus.getInstance().post(new RotatePictureRequest(getCurrentNode(), 90, QUEUE_PRIORITY.HIGH_PRIORITY));
            pictureFrame.getPictureController().requestFocusInWindow();
        });

        pictureFrame.getPictureViewerNavBar().zoomInJButton.addActionListener((ActionEvent e) -> pictureFrame.getPictureController().zoomIn());

        pictureFrame.getPictureViewerNavBar().zoomOutJButton.addActionListener((ActionEvent e) -> pictureFrame.getPictureController().zoomOut());

        pictureFrame.getPictureViewerNavBar().fullScreenJButton.addActionListener((ActionEvent e) -> requestScreenSizeMenu());

        pictureFrame.getPictureViewerNavBar().popupMenuJButton.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ShowPicturePopUpMenuRequest(mySetOfNodes, myIndex, pictureFrame.getPictureViewerNavBar(), 120, 0)));

        pictureFrame.getPictureViewerNavBar().infoJButton.addActionListener((ActionEvent e) -> cycleInfoDisplay());

        pictureFrame.getPictureViewerNavBar().resetJButton.addActionListener((ActionEvent e) -> pictureFrame.getPictureController().resetPicture());

        pictureFrame.getPictureViewerNavBar().speedSlider.addChangeListener((ChangeEvent ce) -> {
            if (!pictureFrame.getPictureViewerNavBar().speedSlider.getValueIsAdjusting()) {
                setTimerDelay(pictureFrame.getPictureViewerNavBar().speedSlider.getValue());
            }
        });

        pictureFrame.getPictureViewerNavBar().closeJButton.addActionListener((ActionEvent e) -> closeViewer());

        pictureFrame.getPictureViewerNavBar().previousJButton.addActionListener((ActionEvent e) -> requestPriorPicture());

        pictureFrame.getPictureViewerNavBar().getNextJButton().addActionListener((ActionEvent e) -> requestNextPicture());

        pictureFrame.getPictureViewerNavBar().clockJButton.addActionListener((ActionEvent e) -> requestAutoAdvance());

    }

    /**
     * This method saves the text of the textbox to the PictureInfo.
     */
    private void saveChangedDescription() {
        SortableDefaultMutableTreeNode node = getCurrentNode();
        if (node != null) {
            Object userObject = node.getUserObject();
            if (userObject instanceof PictureInfo) {
                ((PictureInfo) userObject).setDescription(
                        pictureFrame.getDescription());
            }
        }
    }

    /**
     * Closes the PictureViewer and all dangling references.
     */
    private void closeViewer() {
        if (mySetOfNodes != null) {
            mySetOfNodes.removeNodeNavigatorListener(this);
        }
        stopTimer();
        pictureFrame.getRid();
    }

    /**
     * the context of the browsing
     */
    private NodeNavigatorInterface mySetOfNodes;

    /**
     * the position in the context being shown
     */
    private int myIndex;

    /**
     * Returns the current Node.
     *
     * @return The current node as defined by the mySetOfNodes
     * NodeNavigatorInterface and the myIndex. If the set of nodes has not been
     * initialised or there is some other error null shall be returned. TODO:
     * Shouldn't this be more context aware?
     */
    private SortableDefaultMutableTreeNode getCurrentNode() {
        try {
            return mySetOfNodes.getNode(myIndex);
        } catch (NullPointerException npe) {
            LOGGER.warning(String.format("Got a npe on node %d. Message: %s", myIndex, npe.getMessage()));
            return null;
        }

    }

    /**
     * popup menu for window mode changing
     */
    private final ChangeWindowPopupMenu changeWindowPopupMenu = new ChangeWindowPopupMenu(pictureFrame.getResizableJFrame());

    /**
     * Shows a resize popup menu
     */
    private void requestScreenSizeMenu() {
        changeWindowPopupMenu.show(pictureFrame.getPictureViewerNavBar(), 96, (int) (0 - changeWindowPopupMenu.getSize().getHeight()));
        pictureFrame.getPictureController().requestFocusInWindow();
    }

    /**
     * Puts the picture of the indicated node onto the viewer panel
     *
     * @param mySetOfNodes The set of nodes from which one picture is to be
     *                     shown
     * @param myIndex      The index of the set of nodes to be shown.
     */
    public void showNode(NodeNavigatorInterface mySetOfNodes,
                         int myIndex) {
        LOGGER.fine(String.format("Navigator: %s Nodes: %d Index: %d", mySetOfNodes.toString(), mySetOfNodes.getNumberOfNodes(), myIndex));
        Tools.checkEDT();

        // Validate the inputs
        SortableDefaultMutableTreeNode node = mySetOfNodes.getNode(myIndex);
        if (node == null) {
            LOGGER.severe(String.format("The new node is null. Aborting. mySetOfNodes: %s, index: %d", mySetOfNodes.toString(), myIndex));
            closeViewer();
            return;
        }

        Object userObject = node.getUserObject();
        if (!(userObject instanceof PictureInfo)) {
            LOGGER.severe(String.format("The new node is not for a PictureInfo object. Aborting. userObject class: %s, mySetOfNodes: %s, index: %d", node.getUserObject().getClass().toString(), mySetOfNodes.toString(), myIndex));
            closeViewer();
            return;
        }

        // remove the pictureinfo change listener if present
        if (this.mySetOfNodes != null) {
            ((PictureInfo) this.mySetOfNodes.getNode(this.myIndex).getUserObject()).removePictureInfoChangeListener(this);
        }
        // attach the pictureinfo change listener
        PictureInfo pictureInfo = (PictureInfo) userObject;
        pictureInfo.addPictureInfoChangeListener(this);

        if (this.mySetOfNodes == null) {
            // add viewer to the new one
            this.mySetOfNodes = mySetOfNodes;
            mySetOfNodes.addNodeNavigatorListener(this);
        } else //did we get a new navigator?
        {
            if (!this.mySetOfNodes.equals(mySetOfNodes)) {
                LOGGER.info(String.format("Got a new navigator: old: %s new: %s", this.mySetOfNodes.toString(), mySetOfNodes.toString()));
                //get rid of the old navigator
                this.mySetOfNodes.removeNodeNavigatorListener(this);
                // add viewer to the new one
                this.mySetOfNodes = mySetOfNodes;
                mySetOfNodes.addNodeNavigatorListener(this);
            }
        }

        this.myIndex = myIndex;

        pictureFrame.setDescription(pictureInfo.getDescription());
        setPicture(pictureInfo);

        setIconDecorations();
        pictureFrame.getPictureController().requestFocusInWindow();
    }

    /**
     * brings up the indicated picture on the display.
     *
     * @param pictureInfo The PictureInfo object that should be displayed
     */
    private void setPicture(PictureInfo pictureInfo) {
        LOGGER.log(Level.FINE, "Set picture to PictureInfo: {0}", pictureInfo.toString());
        pictureFrame.getPictureController().setPicture(pictureInfo.getImageFile(), pictureInfo.getDescription(), pictureInfo.getRotation());
    }

    /**
     * here we get notified by the PictureInfo object that something has
     * changed.
     *
     * @param pictureInfoChangedEvent The event
     */
    @Override
    public void pictureInfoChangeEvent(PictureInfoChangeEvent pictureInfoChangedEvent) {
        if (pictureInfoChangedEvent.getDescriptionChanged()) {
            pictureFrame.setDescription(pictureInfoChangedEvent.getPictureInfo().getDescription());
        }

        if (pictureInfoChangedEvent.getHighresLocationChanged()) {
            SortableDefaultMutableTreeNode node = mySetOfNodes.getNode(myIndex);
            PictureInfo pictureInfo = (PictureInfo) node.getUserObject();
            setPicture(pictureInfo);
        }

        if (pictureInfoChangedEvent.getRotationChanged()) {
            PictureInfo pictureInfo = (PictureInfo) mySetOfNodes.getNode(myIndex).getUserObject();
            setPicture(pictureInfo);
        }
    }

    /**
     * gets called when the Navigator notices a change
     */
    @Override
    public void nodeLayoutChanged() {
        LOGGER.info("Got notified to relayout");
        showNode(mySetOfNodes, myIndex);

    }

    /**
     * Request the PictureViewer to display the next picture. It calls
     * {@link SortableDefaultMutableTreeNode#getNextPicture} to find the image.
     * If the call returned a non null node {@link #showNode} is called to
     * request the loading and display of the new picture.
     *
     * @see #requestPriorPicture()
     */
    private void requestNextPicture() {
        if (mySetOfNodes.getNumberOfNodes() > myIndex + 1) {

            SwingUtilities.invokeLater(
                    () -> showNode(mySetOfNodes, myIndex + 1)
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
            SwingUtilities.invokeLater(() -> showNode(mySetOfNodes, myIndex - 1));
        }
    }

    /**
     * Brings up the dialog for the AutoAdvance timer or shuts the running one
     * down.
     */
    private void requestAutoAdvance() {
        if (advanceTimer != null) {
            stopTimer();
            pictureFrame.getPictureViewerNavBar().clockJButton.setClockIdle();
        } else {
            JpoEventBus.getInstance().post(new ShowAutoAdvanceDialogRequest(pictureFrame.getResizableJFrame(), getCurrentNode(), this));
        }

        pictureFrame.getPictureController().requestFocusInWindow();
    }

    /**
     * the timer that can call back into the object with the instruction to load
     * the next image
     */
    //private AdvanceTimer advanceTimer;
    private Timer advanceTimer;

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
        pictureFrame.getPictureViewerNavBar().clockJButton.setClockBusy();
        pictureFrame.getPictureViewerNavBar().showDelaySilder();
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
        pictureFrame.getPictureViewerNavBar().hideDelaySilder();
    }

    /**
     * Tells the AdvanceTimer whether it is OK to advance the picture or not
     * This important to avoid the submission of new picture requests before the
     * old ones have been met.
     *
     * @return true if ready to advance
     */
    private boolean readyToAdvance() {
        OverlayedPictureController pictureJPanel = pictureFrame.getPictureController();
        ScalablePictureStatus status = pictureJPanel.getScalablePicture().getStatusCode();
        return (status == SCALABLE_PICTURE_READY) || (status == SCALABLE_PICTURE_ERROR);
    }

    /**
     * This function cycles to the next info display overlay.
     */
    private void cycleInfoDisplay() {
        //PictureController pictureJPanel = pictureFrame.getPictureController();
        pictureFrame.cycleInfoDisplay();
        //pictureJPanel.requestFocusInWindow();
    }

    /**
     * The location and size of the Window can be changed by a call to this
     * method
     *
     * @param newMode new window mode
     */
    public void switchWindowMode(WindowSize newMode) {
        pictureFrame.switchWindowMode(newMode);
    }

    /**
     * This method looks at the position the currentNode is in regard to it's
     * siblings and changes the forward and back icons to reflect the position
     * of the current node.
     */
    private void setIconDecorations() {
        SortableDefaultMutableTreeNode currentNode = getCurrentNode();
        if (currentNode == null) {
            return;
        }
        // Set the next and back icons
        DefaultMutableTreeNode NextNode = currentNode.getNextSibling();
        if (NextNode != null) {
            Object nodeInfo = NextNode.getUserObject();
            if (nodeInfo instanceof PictureInfo) {
                // because there is a next sibling object of type
                // PictureInfo we should set the next icon to the
                // icon that indicates a next picture in the group
                pictureFrame.getPictureViewerNavBar().setNextButtonHasRight();
            } else {
                // it must be a GroupInfo node
                // since we must descend into it it gets a nextnext icon.
                pictureFrame.getPictureViewerNavBar().setNextButtonHasNext();
            }
        } else // the getNextSibling() method returned null
        // if the getNextNode also returns null this was the end of the album
        // otherwise there are more pictures in the next group.
        {
            if (currentNode.getNextNode() != null) {
                pictureFrame.getPictureViewerNavBar().setNextButtonHasNext();
            } else {
                pictureFrame.getPictureViewerNavBar().setNextButtonEnd();
            }
        }

        // let's see what we have in the way of previous siblings..
        if (currentNode.getPreviousSibling() != null) {
            pictureFrame.getPictureViewerNavBar().setPreviousButtonHasLeft();
        } else {
            // determine if there are any previous nodes that are not groups.
            DefaultMutableTreeNode testNode;
            testNode = currentNode.getPreviousNode();
            while ((testNode != null) && (!(testNode.getUserObject() instanceof PictureInfo))) {
                testNode = testNode.getPreviousNode();
            }
            if (testNode == null) {
                pictureFrame.getPictureViewerNavBar().setPreviousButtonBeginning();
            } else {
                pictureFrame.getPictureViewerNavBar().setPreviousButtonHasPrevious();
            }
        }
    }

}
