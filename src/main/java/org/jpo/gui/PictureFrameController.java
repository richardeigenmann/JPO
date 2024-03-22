package org.jpo.gui;

import org.jpo.datamodel.*;
import org.jpo.gui.swing.PictureFrame;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2024 Richard Eigenmann, Zürich, Switzerland
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
 * This class manages the PictureFrame. It is controlled by the PictureViewer which concerns itself with
 * which picture to show from the Navigator. The PictureFrameController handles showing the picture and the
 * description. It listens to PictureInfoChangeEvents so that it can update views when things change.
 * It can also write description changes back to the model.
 */
public class PictureFrameController implements PictureInfoChangeListener {


    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PictureFrameController.class.getName());

    /**
     * PictureFrame
     */
    private final PictureFrame pictureFrame = new PictureFrame();

    private PictureInfo myPictureInfo;

    public PictureFrameController() {
        pictureFrame.getFocussableDescriptionField().addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                saveChangedDescription();
            }
        });

        addStatusListener(pictureFrame.getPictureController());

    }

    /**
     * This method saves the text of the textbox to the PictureInfo.
     */
    private void saveChangedDescription() {
        myPictureInfo.setDescription(pictureFrame.getDescription());
    }

    public void setPicture(final PictureInfo pictureInfo) {
        LOGGER.log(Level.INFO, "Set picture to PictureInfo: {0}", pictureInfo);

        if (pictureInfo == myPictureInfo) {
            LOGGER.log(Level.INFO, "The PictureFrameController is being asked to show the same PictureInfo it is already showing. Ignoring request.");
            return;
        }

        if (myPictureInfo != null) {
            myPictureInfo.removePictureInfoChangeListener(this);
        }

        myPictureInfo = pictureInfo;

        pictureInfo.addPictureInfoChangeListener(this);
        pictureFrame.getPictureController().setPicture(pictureInfo);
        pictureFrame.setDescription(pictureInfo.getDescription());
    }

    public PictureFrame getPictureFrame() {
        return pictureFrame;
    }

    /**
     * here we get notified by the PictureInfo object that something has
     * changed.
     *
     * @param pictureInfoChangedEvent The event
     */
    @Override
    public void pictureInfoChangeEvent(final PictureInfoChangeEvent pictureInfoChangedEvent) {
        if (pictureInfoChangedEvent.getDescriptionChanged()) {
            pictureFrame.setDescription(pictureInfoChangedEvent.getPictureInfo().getDescription());
        }

        if (pictureInfoChangedEvent.getHighresLocationChanged()) {
            LOGGER.log(Level.INFO, "Got notified about highres changed. My highres was {0} new one is {1}", new Object[]{myPictureInfo.getImageFile(), pictureInfoChangedEvent.getPictureInfo().getImageFile() });
            pictureFrame.getPictureController().setPicture(pictureInfoChangedEvent.getPictureInfo());
        }

        if (pictureInfoChangedEvent.getRotationChanged()) {
            pictureFrame.getPictureController().setPicture(pictureInfoChangedEvent.getPictureInfo());
        }
    }

    private void addStatusListener(final OverlayedPictureController pictureJPanel) {
        pictureJPanel.addStatusListener(new ScalablePictureListener() {

            /**
             * This method gets invoked from the ScalablePicture to notify of
             * status changes. We use the notification to update the progress bar
             * at the bottom of the screen.
             *
             * @param pictureStatusCode    the status code
             * @param pictureStatusMessage the status message (not used)
             */
            @Override
            public void scalableStatusChange(final ScalablePicture.ScalablePictureStatus pictureStatusCode,
                                             final String pictureStatusMessage) {
                final Runnable runnable = () -> {
                    LOGGER.log(Level.INFO, "Got a scalableStatusChange callback. Code: {0}, Message; {1}", new Object[]{pictureStatusCode,pictureStatusMessage});
                    Thread.dumpStack();
                    switch (pictureStatusCode) {
                        case SCALABLE_PICTURE_UNINITIALISED,
                                SCALABLE_PICTURE_GARBAGE_COLLECTION,
                                SCALABLE_PICTURE_LOADED,
                                SCALABLE_PICTURE_SCALING -> pictureFrame.setProgressBarVisible(false);
                        case SCALABLE_PICTURE_ERROR -> {
                            LOGGER.log(Level.INFO, "Heard about a SCALABLE_PICTURE_ERROR - Calling showError");
                            pictureFrame.setProgressBarVisible(false);
                            pictureFrame.showError(pictureStatusMessage);
                        }
                        case SCALABLE_PICTURE_LOADING -> pictureFrame.setProgressBarVisible(true);
                        case SCALABLE_PICTURE_READY -> {
                            pictureFrame.setProgressBarVisible(false);
                            pictureFrame.hideError();
                            pictureFrame.getResizableJFrame().toFront();
                        }
                        default ->
                                LOGGER.log(Level.WARNING, "Got called with a code that is not understood: {0} {1}", new Object[]{pictureStatusCode, pictureStatusMessage});
                    }
                };
                if (SwingUtilities.isEventDispatchThread()) {
                    runnable.run();
                } else {
                    SwingUtilities.invokeLater(runnable);
                }
            }

            /**
             * method that gets invoked from the ScalablePicture to notify of
             * status changes in loading the image.
             *
             * @param statusCode the status code
             * @param percentage the percentage
             */
            @Override
            public void sourceLoadProgressNotification(final SourcePicture.SourcePictureStatus statusCode,
                                                       final int percentage) {
                final Runnable runnable = () -> {
                    switch (statusCode) {
                        case SOURCE_PICTURE_LOADING_STARTED -> {
                            pictureFrame.setProgressBarValue(0);
                            pictureFrame.setProgressBarVisible(true);
                        }
                        case SOURCE_PICTURE_LOADING_PROGRESS -> {
                            pictureFrame.setProgressBarValue(percentage);
                            pictureFrame.setProgressBarVisible(true);
                        }
                        default -> // SOURCE_PICTURE_LOADING_COMPLETED:
                            pictureFrame.setProgressBarVisible(false);
                    }
                };
                if (SwingUtilities.isEventDispatchThread()) {
                    runnable.run();
                } else {
                    SwingUtilities.invokeLater(runnable);
                }
            }
        });
    }



}
