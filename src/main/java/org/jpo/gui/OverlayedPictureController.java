package org.jpo.gui;

import org.jpo.datamodel.*;
import org.jpo.gui.swing.PictureController;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.datamodel.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_READY;
import static org.jpo.gui.OverlayedPictureController.InfoOverlay.*;

/*
Copyright (C) 2017 - 2022 Richard Eigenmann.
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
 * Overrides the Picture Controller with functionality to overlay the picture
 * with information
 * <p>
 * The {@link #showInfo} flag controls whether information about the picture is
 * overlayed on the image.
 */
public class OverlayedPictureController extends PictureController implements ScalablePictureListener {

    /**
     * Constructs the overlay that can show an info panel
     *
     * @param scalablePicture a reference to the picture
     */
    public OverlayedPictureController(final ScalablePicture scalablePicture) {
        super(scalablePicture);
        this.scalablePicture = scalablePicture;
        setFont(INFO_FONT);
        setBackground(Settings.getPictureviewerBackgroundColor());
        setMinimumSize(Settings.getPictureviewerMinimumSize());

        scalablePicture.addStatusListener(this);
        if (Settings.isPictureViewerFastScale()) {
            scalablePicture.setFastScale();
        } else {
            scalablePicture.setQualityScale();
        }

    }

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( OverlayedPictureController.class.getName() );

    /**
     * The currently displayed ScalablePicture.
     */
    private final transient ScalablePicture scalablePicture;

    /**
     * The type of overlay that should be shown
     */
    public enum InfoOverlay {
        /**
         * No overlay
         */
        NO_OVERLAY,
        /**
         * Overlay with Photographic information such as aperture and shutter
         * speed
         */
        PHOTOGRAPHIC_OVERLAY,
        /**
         * Overlay with technical information
         */
        APPLICATION_OVERLAY
    }

    /**
     * Code that determines what info is to be displayed over the picture.
     */
    private InfoOverlay showInfo = NO_OVERLAY;

    /**
     * This function cycles to the next info display. The first is DISPLAY_NONE,
     * DISPLAY_PHOTOGRAPHIC and DISPLAY_APPLICATION
     *
     */
    public void cycleInfoDisplay() {
        switch (showInfo) {
            case NO_OVERLAY -> showInfo = PHOTOGRAPHIC_OVERLAY;
            case PHOTOGRAPHIC_OVERLAY -> showInfo = APPLICATION_OVERLAY;
            default -> showInfo = NO_OVERLAY;
        }
        repaint();
    }

    /**
     * location of the info texts if shown
     */
    private final Point infoCoordinates = new Point(15, 15);

    /**
     * line spacing for the info text that can be superimposed on the picture
     */
    private static final int LINE_SPACING = 12;

    /**
     * Tabstop distance
     */
    private static final int TABSTOP = 90;

    /**
     * class to format the scale
     */
    private static final DecimalFormat TWO_DECIMAL_FORMATTER = new DecimalFormat( "###0.00" );

    /**
     * Font for the info if shown.
     */
    private static final Font INFO_FONT = Font.decode(Settings.getJpoResources().getString("PicturePaneInfoFont"));

    /**
     * Color for the info overly
     */
    private static final Color INFO_FONT_COLOR = Color.white;

    /**
     * This object is a reference to an Exif Info object that tries to keep tabs
     * on the information in the image.
     */
    private transient ExifInfo exifInfo;

    /**
     * Brings up the indicated picture on the display.
     *
     * @param pictureInfo The pictureInfo to display
     */
    public void setPicture(final PictureInfo pictureInfo) {
        scalablePicture.stopLoadingExcept(pictureInfo.getImageFile());

        setCenterWhenScaled(true);
        scalablePicture.setScaleSize(getSize());
        scalablePicture.loadAndScalePictureInThread(pictureInfo.getSha256(), pictureInfo.getImageFile(), Thread.MAX_PRIORITY, pictureInfo.getRotation());

        legend = pictureInfo.getDescription();
        exifInfo = new ExifInfo(pictureInfo.getImageFile());
        exifInfo.decodeExifTags();
    }

    /**
     * Overriding the paint to add the drawing of the info panel
     *
     * @param g Graphics
     */
    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(INFO_FONT_COLOR);
        switch (showInfo) {
            case NO_OVERLAY:
                break;
            case PHOTOGRAPHIC_OVERLAY:
                g2d.drawString(Settings.getJpoResources().getString("ExifInfoCamera"), infoCoordinates.x, infoCoordinates.y);
                g2d.drawString(exifInfo.getCamera(), infoCoordinates.x + TABSTOP, infoCoordinates.y);
                g2d.drawString(Settings.getJpoResources().getString("ExifInfoLens"), infoCoordinates.x, infoCoordinates.y + (LINE_SPACING));
                g2d.drawString(exifInfo.getLens(), infoCoordinates.x + TABSTOP, infoCoordinates.y + (LINE_SPACING));
                g2d.drawString(Settings.getJpoResources().getString("ExifInfoShutterSpeed"), infoCoordinates.x, infoCoordinates.y + (2 * LINE_SPACING));
                g2d.drawString(exifInfo.getShutterSpeed(), infoCoordinates.x + TABSTOP, infoCoordinates.y + (2 * LINE_SPACING));
                g2d.drawString(Settings.getJpoResources().getString("ExifInfoAperture"), infoCoordinates.x, infoCoordinates.y + (3 * LINE_SPACING));
                g2d.drawString(exifInfo.getAperture(), infoCoordinates.x + TABSTOP, infoCoordinates.y + (3 * LINE_SPACING));
                g2d.drawString(Settings.getJpoResources().getString("ExifInfoFocalLength"), infoCoordinates.x, infoCoordinates.y + (4 * LINE_SPACING));
                g2d.drawString(exifInfo.getFocalLength(), infoCoordinates.x + TABSTOP, infoCoordinates.y + (4 * LINE_SPACING));
                g2d.drawString(Settings.getJpoResources().getString("ExifInfoISO"), infoCoordinates.x, infoCoordinates.y + (5 * LINE_SPACING));
                g2d.drawString(exifInfo.getIso(), infoCoordinates.x + TABSTOP, infoCoordinates.y + (5 * LINE_SPACING));
                g2d.drawString(Settings.getJpoResources().getString("ExifInfoTimeStamp"), infoCoordinates.x, infoCoordinates.y + (6 * LINE_SPACING));
                g2d.drawString(exifInfo.getCreateDateTime(), infoCoordinates.x + TABSTOP, infoCoordinates.y + (6 * LINE_SPACING));
                break;
            case APPLICATION_OVERLAY:
                g2d.drawString(legend, infoCoordinates.x, infoCoordinates.y);
                g2d.drawString(Settings.getJpoResources().getString("PicturePaneSize")
                        + scalablePicture.getOriginalWidth()
                        + " x "
                        + scalablePicture.getOriginalHeight()
                        + Settings.getJpoResources().getString("PicturePaneMidpoint")
                        + focusPoint.x
                        + " x "
                        + focusPoint.y
                        + " Scale: "
                        + TWO_DECIMAL_FORMATTER.format(scalablePicture.getScaleFactor()), infoCoordinates.x, infoCoordinates.y
                        + (LINE_SPACING));
                g2d.drawString("File: " + scalablePicture.getFilename(), infoCoordinates.x, infoCoordinates.y + (2 * LINE_SPACING));
                g2d.drawString(Settings.getJpoResources().getString("PicturePaneLoadTime") + TWO_DECIMAL_FORMATTER.format(scalablePicture.getSourcePicture().getLoadTime() / 1000F) + Settings.getJpoResources().getString("PicturePaneSeconds"), infoCoordinates.x, infoCoordinates.y + (3 * LINE_SPACING));
                g2d.drawString(Settings.getJpoResources().getString("PicturePaneFreeMemory") + Tools.freeMemory(), infoCoordinates.x, infoCoordinates.y + (4 * LINE_SPACING));
                break;
            default: // case NO_OVERLAY:
                break;
        }

    }

    /**
     * method that gets invoked from the ScalablePicture object to notify of
     * status changes. The ScalablePicture goes through several statuses:
     * UNINITIALISED, GARBAGE_COLLECTION, LOADING, SCALING, READY, ERROR.<p>
     * Each status is passed to the listener upon receipt.<p>
     * When the ScalablePicture signals that it is READY the legend of the
     * picture is sent to the listener. The method {@link #centerImage} is
     * called and a repaint is requested.
     *
     * @param pictureStatusCode New Status Code
     * @param pictureStatusMessage New Status Message
     */
    @Override
    public void scalableStatusChange(final ScalablePicture.ScalablePictureStatus pictureStatusCode,
                                     final String pictureStatusMessage) {
        LOGGER.log(Level.FINE, "Got a status change: {0}", pictureStatusMessage);

        String message = pictureStatusMessage;
        if (pictureStatusCode == SCALABLE_PICTURE_READY) {
            LOGGER.fine("READY status");
            message = Settings.getJpoResources().getString("PicturePaneReadyStatus");
            if (isCenterWhenScaled()) {
                LOGGER.fine("centering image");
                centerImage();
            }
            LOGGER.fine( "forcing Panel repaint" );
            repaint();
        }

        synchronized ( picturePaneListeners ) {
            for ( ScalablePictureListener scalablePictureListener : picturePaneListeners ) {
                scalablePictureListener.scalableStatusChange(pictureStatusCode, message);
            }
        }
    }

    /**
     * pass messages about progress onto the PictureViewer for updating of the
     * progress bar
     *
     * @param statusCode status code
     * @param percentage percentage
     */
    @Override
    public void sourceLoadProgressNotification(final SourcePicture.SourcePictureStatus statusCode, final int percentage) {
        synchronized (picturePaneListeners) {
            for (ScalablePictureListener scalablePictureListener : picturePaneListeners) {
                scalablePictureListener.sourceLoadProgressNotification(statusCode, percentage);
            }
        }
    }

    /**
     * The objects that would like to receive notifications about what is going
     * on with the ScalablePicture being displayed in this PicturePane. These
     * objects must implement the ScalablePictureListener interface.
     */
    private final transient Set<ScalablePictureListener> picturePaneListeners = Collections.synchronizedSet(new HashSet<>());

    /**
     * method to register the listening object of the status events
     *
     * @param listener Listener
     */
    public void addStatusListener(final ScalablePictureListener listener) {
        picturePaneListeners.add(listener);
    }

    /**
     * de-register the listening object of the status events
     *
     * @param listener the listener to remove
     */
    public void removeStatusListener(final ScalablePictureListener listener) {
        picturePaneListeners.remove(listener);
    }

    /**
     * method that returns a handle to the scalable picture that this component
     * is displaying
     *
     * @return the scaled image
     */
    public ScalablePicture getScalablePicture() {
        return scalablePicture;
    }

    /**
     * The legend of the picture. Is sent to the listener when the image is
     * ready.
     */
    private String legend;

}
