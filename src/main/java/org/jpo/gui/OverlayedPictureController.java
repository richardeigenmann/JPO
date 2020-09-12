package org.jpo.gui;

import org.jpo.datamodel.ExifInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;
import org.jpo.gui.swing.PictureController;

import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.gui.OverlayedPictureController.InfoOverlay.*;
import static org.jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_READY;

/*
Copyright (C) 2017 - 2020 Richard Eigenmann.
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
 * Overrides the Picture Controller with functionality to overlay the picture
 * with information
 *
 * The {@link #showInfo} flag controls whether information about the picture is
 * overlayed on the image.
 *
 */
public class OverlayedPictureController extends PictureController implements ScalablePictureListener {


    public OverlayedPictureController(final ScalablePicture scalablePicture) {
        super(scalablePicture);
        this.scalablePicture = scalablePicture;
        setFont(INFO_FONT);
        setBackground(Settings.getPictureviewerBackgroundColor());
        setMinimumSize(Settings.getPictureviewerMinimumSize());

        scalablePicture.addStatusListener(this);
        if (Settings.pictureViewerFastScale) {
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
    private final ScalablePicture scalablePicture;

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
            case APPLICATION_OVERLAY -> showInfo = NO_OVERLAY;
            default -> showInfo = NO_OVERLAY;
        }
        repaint();
    }

    /**
     * location of the info texts if shown
     */
    private final Point INFO_COORDINATES = new Point( 15, 15 );

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
    private static final Font INFO_FONT = Font.decode( Settings.jpoResources.getString( "PicturePaneInfoFont" ) );

    /**
     * Color for the info overly
     */
    private static final Color INFO_FONT_COLOR = Color.white;

    /**
     * This object is a reference to an Exif Info object that tries to keep tabs
     * on the information in the image.
     */
    private ExifInfo exifInfo;

    /**
     * Brings up the indicated picture on the display.
     *
     * @param file        The URL of the picture to display
     * @param description The description of the picture
     * @param rotation    The rotation that should be applied
     */
    public void setPicture(final File file, final String description,
                           final double rotation) {
        scalablePicture.stopLoadingExcept(file);

        setCenterWhenScaled(true);
        scalablePicture.setScaleSize(getSize());
        scalablePicture.loadAndScalePictureInThread(file, Thread.MAX_PRIORITY, rotation);

        legend = description;
        exifInfo = new ExifInfo(file);
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
            case PHOTOGRAPHIC_OVERLAY:
                g2d.drawString(Settings.jpoResources.getString("ExifInfoCamera"), INFO_COORDINATES.x, INFO_COORDINATES.y);
                g2d.drawString(exifInfo.getCamera(), INFO_COORDINATES.x + TABSTOP, INFO_COORDINATES.y);
                g2d.drawString(Settings.jpoResources.getString("ExifInfoLens"), INFO_COORDINATES.x, INFO_COORDINATES.y + (LINE_SPACING));
                g2d.drawString(exifInfo.getLens(), INFO_COORDINATES.x + TABSTOP, INFO_COORDINATES.y + (LINE_SPACING));
                g2d.drawString(Settings.jpoResources.getString("ExifInfoShutterSpeed"), INFO_COORDINATES.x, INFO_COORDINATES.y + (2 * LINE_SPACING ) );
                g2d.drawString(exifInfo.getShutterSpeed(), INFO_COORDINATES.x + TABSTOP, INFO_COORDINATES.y + ( 2 * LINE_SPACING ) );
                g2d.drawString( Settings.jpoResources.getString( "ExifInfoAperture" ), INFO_COORDINATES.x, INFO_COORDINATES.y + ( 3 * LINE_SPACING ) );
                g2d.drawString(exifInfo.getAperture(), INFO_COORDINATES.x + TABSTOP, INFO_COORDINATES.y + ( 3 * LINE_SPACING ) );
                g2d.drawString( Settings.jpoResources.getString( "ExifInfoFocalLength" ), INFO_COORDINATES.x, INFO_COORDINATES.y + ( 4 * LINE_SPACING ) );
                g2d.drawString(exifInfo.getFocalLength(), INFO_COORDINATES.x + TABSTOP, INFO_COORDINATES.y + ( 4 * LINE_SPACING ) );
                g2d.drawString( Settings.jpoResources.getString( "ExifInfoISO" ), INFO_COORDINATES.x, INFO_COORDINATES.y + ( 5 * LINE_SPACING ) );
                g2d.drawString(exifInfo.getIso(), INFO_COORDINATES.x + TABSTOP, INFO_COORDINATES.y + ( 5 * LINE_SPACING ) );
                g2d.drawString( Settings.jpoResources.getString( "ExifInfoTimeStamp" ), INFO_COORDINATES.x, INFO_COORDINATES.y + ( 6 * LINE_SPACING ) );
                g2d.drawString( exifInfo.getCreateDateTime(), INFO_COORDINATES.x + TABSTOP, INFO_COORDINATES.y + ( 6 * LINE_SPACING ) );
                break;
            case APPLICATION_OVERLAY:
                g2d.drawString( legend, INFO_COORDINATES.x, INFO_COORDINATES.y);
                g2d.drawString( Settings.jpoResources.getString( "PicturePaneSize" )
                        + scalablePicture.getOriginalWidth()
                        + " x "
                        + scalablePicture.getOriginalHeight()
                        + Settings.jpoResources.getString( "PicturePaneMidpoint" )
                        + focusPoint.x
                        + " x "
                        + focusPoint.y
                        + " Scale: "
                        + TWO_DECIMAL_FORMATTER.format( scalablePicture.getScaleFactor() ), INFO_COORDINATES.x, INFO_COORDINATES.y
                        + (LINE_SPACING) );
                g2d.drawString( "File: " + scalablePicture.getFilename(), INFO_COORDINATES.x, INFO_COORDINATES.y + ( 2 * LINE_SPACING ) );
                g2d.drawString( Settings.jpoResources.getString( "PicturePaneLoadTime" ) + TWO_DECIMAL_FORMATTER.format( scalablePicture.getSourcePicture().loadTime / 1000F ) + Settings.jpoResources.getString( "PicturePaneSeconds" ), INFO_COORDINATES.x, INFO_COORDINATES.y + ( 3 * LINE_SPACING ) );
                g2d.drawString( Settings.jpoResources.getString( "PicturePaneFreeMemory" ) + Tools.freeMemory(), INFO_COORDINATES.x, INFO_COORDINATES.y + ( 4 * LINE_SPACING ) );
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
    public void scalableStatusChange( ScalablePicture.ScalablePictureStatus pictureStatusCode,
            String pictureStatusMessage ) {
        LOGGER.log( Level.FINE, "Got a status change: {0}", pictureStatusMessage );

        if ( pictureStatusCode == SCALABLE_PICTURE_READY ) {
            LOGGER.fine( "READY status" );
            //pictureStatusMessage = legend;
            pictureStatusMessage = Settings.jpoResources.getString( "PicturePaneReadyStatus" );
            if ( isCenterWhenScaled() ) {
                LOGGER.fine( "centering image" );
                centerImage();
            }
            LOGGER.fine( "forcing Panel repaint" );
            repaint();
        }

        synchronized ( picturePaneListeners ) {
            for ( ScalablePictureListener scalablePictureListener : picturePaneListeners ) {
                scalablePictureListener.scalableStatusChange( pictureStatusCode, pictureStatusMessage );
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
    public void sourceLoadProgressNotification( SourcePicture.SourcePictureStatus statusCode, int percentage ) {
        synchronized ( picturePaneListeners ) {
            for ( ScalablePictureListener scalablePictureListener : picturePaneListeners ) {
                scalablePictureListener.sourceLoadProgressNotification( statusCode, percentage );
            }
        }
    }

    /**
     * The objects that would like to receive notifications about what is going
     * on with the ScalablePicture being displayed in this PicturePane. These
     * objects must implement the ScalablePictureListener interface.
     */
    private final Set<ScalablePictureListener> picturePaneListeners = Collections.synchronizedSet(new HashSet<>() );

    /**
     * method to register the listening object of the status events
     *
     * @param listener Listener
     */
    public void addStatusListener( ScalablePictureListener listener ) {
        picturePaneListeners.add( listener );
    }

    /**
     * deregister the listening object of the status events
     *
     * @param listener the listener to remove
     */
    public void removeStatusListener( ScalablePictureListener listener ) {
        picturePaneListeners.remove( listener );
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
