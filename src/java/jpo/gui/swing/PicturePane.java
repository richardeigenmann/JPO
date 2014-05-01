package jpo.gui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import jpo.dataModel.ExifInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import jpo.gui.ScalablePicture;
import jpo.gui.ScalablePicture.ScalablePictureStatus;
import static jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_LOADED;
import static jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_READY;
import jpo.gui.ScalablePictureListener;
import jpo.gui.SourcePicture.SourcePictureStatus;
import static jpo.gui.swing.PicturePane.InfoOverlay.APPLICATION_OVERLAY;
import static jpo.gui.swing.PicturePane.InfoOverlay.NO_OVERLAY;
import static jpo.gui.swing.PicturePane.InfoOverlay.PHOTOGRAPHIC_OVERLAY;


/*
 PicturePane.java:  a component that can display an image

 Copyright (C) 2002 - 2014 Richard Eigenmann.
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
 * The job of this Component is to scale and display a picture.
 *
 * It notifies the registered parent about status changes so that a description
 * object can be updated. When the image has been rendered and displayed the
 * legend of the image is send to the parent with the ready status.<p>
 *
 * The image is centred on the component to the {@link #focusPoint} in the
 * coordinate space of the image. This translated using the
 * {@link ScalablePicture#setScaleFactor( double )} to the coordinate space of
 * the JComponent<p>
 *
 * <img src=../Mathematics.png border=0><p>
 *
 *
 * The {@link #showInfo} flag controls whether information about the picture is
 * overlayed on the image.
 */
public class PicturePane
        extends JComponent
        implements ScalablePictureListener {

    /**
     * The currently displayed ScalablePicture.
     */
    public ScalablePicture scalablePicture = new ScalablePicture();

    /**
     * Flag that lets this JComponent know if the picture is to be fitted into
     * the available space when the threads return the scaled picture.
     */
    public boolean centerWhenScaled;

    /**
     * This point of the picture will be put at the middle of the screen
     * component. The coordinates are in x,y in the coordinate space of the
     * picture.
     */
    public Point focusPoint = new Point();

    /**
     * The legend of the picture. Is sent to the listener when the image is
     * ready.
     */
    public String legend;

    /**
     * location of the info texts if shown
     */
    private static final Point infoPoint = new Point( 15, 15 );

    /**
     * line spacing for the info text that can be superimposed on the picture
     */
    private static final int lineSpacing = 12;

    /**
     * line spacing for the info text that can be superimposed on the picture
     */
    private static final int tabstop = 90;

    /**
     * Font for the info if shown.
     */
    private static final Font infoFont = Font.decode( Settings.jpoResources.getString( "PicturePaneInfoFont" ) );

    /**
     * Color for the info overly
     */
    private static final Color infoFontColor = Color.white;

    /**
     * This object is a reference to an Exif Info object that tries to keep tabs
     * on the information in the image.
     */
    public ExifInfo exifInfo;

    /**
     * class to format the scale
     */
    private static final DecimalFormat TWO_DECIMAL_FORMATTER = new DecimalFormat( "###0.00" );

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( PicturePane.class.getName() );

    /**
     * Constructs a PicturePane components.
     *
     */
    public PicturePane() {
        // make graphics faster
        this.setDoubleBuffered( false );

        scalablePicture.addStatusListener( PicturePane.this );
        if ( Settings.pictureViewerFastScale ) {
            scalablePicture.setFastScale();
        } else {
            scalablePicture.setQualityScale();
        }

        this.addComponentListener( new ComponentAdapter() {

            @Override
            public void componentResized( ComponentEvent event ) {
                zoomToFit();
            }
        } );

        setFont( infoFont );
        setMinimumSize( new Dimension( 100, 100 ) );
    }

    /////////////////////////
    // Zooming Methods     //
    /////////////////////////
    /**
     * Multiplies the scale factor so that paint() method scales the image
     * larger. This method calls
     * {@link ScalablePicture#createScaledPictureInThread(int)} which in turn
     * will tell this object by means of the status update that the image is
     * ready and should be repainted.
     */
    public void zoomIn() {
        double OldScaleFactor = scalablePicture.getScaleFactor();
        double NewScaleFactor = OldScaleFactor * 1.5;

        // If scaling goes from scale down to scale up, set ScaleFactor to exactly 1
        if ( ( OldScaleFactor < 1 ) && ( NewScaleFactor > 1 ) ) {
            NewScaleFactor = 1;
        }

        // Check if the picture would get to large and cause the system to "hang"
        if ( ( scalablePicture.getOriginalWidth() * scalablePicture.getScaleFactor() < Settings.maximumPictureSize ) && ( scalablePicture.getOriginalHeight() * scalablePicture.getScaleFactor() < Settings.maximumPictureSize ) ) {
            scalablePicture.setScaleFactor( NewScaleFactor );
            scalablePicture.createScaledPictureInThread( Thread.MAX_PRIORITY );
        }
    }

    /**
     * method that zooms out on the image leaving the centre where it is. This
     * method calls {@link ScalablePicture#createScaledPictureInThread(int)}
     * which in turn will tell this object by means of the status update that
     * the image is ready and should be repainted.
     */
    public void zoomOut() {
        scalablePicture.setScaleFactor( scalablePicture.getScaleFactor() / 1.5 );
        scalablePicture.createScaledPictureInThread( Thread.MAX_PRIORITY );
    }

    /**
     * this method sets the desired scaled size of the ScalablePicture to the
     * size of the JPanel and fires off a createScaledPictureInThread request if
     * the ScalablePicture has been loaded or is ready.
     *
     * @see ScalablePicture#createScaledPictureInThread(int)
     *
     */
    public void zoomToFit() {
        scalablePicture.setScaleSize( getSize() );
        // prevent useless rescale events when the picture is not ready
        if ( scalablePicture.getStatusCode() == SCALABLE_PICTURE_LOADED || scalablePicture.getStatusCode() == SCALABLE_PICTURE_READY ) {
            scalablePicture.createScaledPictureInThread( Thread.MAX_PRIORITY );
        }
    }

    /**
     * method that zooms the image to 100%. This method calls
     * {@link ScalablePicture#createScaledPictureInThread(int)} which in turn
     * will tell this object by means of the status update that the image is
     * ready and should be repainted.
     */
    public void zoomFull() {
        scalablePicture.setScaleFactor( 1 );
        scalablePicture.createScaledPictureInThread( Thread.MAX_PRIORITY );
    }

    ///////////////////////////////////////////////////////////////
    // Scrolling Methods                                         //
    ///////////////////////////////////////////////////////////////
    /**
     * Set image to centre of panel by putting the coordinates of the middle of
     * the original image into the Centre to X and Centre to Y variables by
     * invoking the setCenterLoaction method. This method calls
     * <code>repaint()</code> directly since no time consuming image operations
     * need to take place.
     */
    public void centerImage() {
        if ( scalablePicture.getOriginalImage() != null ) {
            setCenterLocation( scalablePicture.getOriginalWidth() / 2, scalablePicture.getOriginalHeight() / 2 );
            repaint();
        }
    }

    /**
     * This is the factor by how much the scrollxxx methods will scroll.
     * Currently set to a fixed 10%.
     */
    private static final float scrollFactor = 0.05f;

    /**
     * method that moves the image up by 10% of the pixels shown on the screen.
     * This method calls <code>repaint()</code> directly since no time consuming
     * image operations need to take place.
     * <p>
     * <img src=../scrollUp.png border=0><p>
     * @see #scrollUp()
     * @see #scrollDown()
     * @see #scrollLeft()
     * @see #scrollRight()
     *
     */
    public void scrollUp() {
        // if the bottom edge of the picture is visible, do not scroll
        if ( ( ( scalablePicture.getOriginalHeight() - focusPoint.y ) * scalablePicture.getScaleFactor() ) + getSize().height / (double) 2 > getSize().height ) {
            focusPoint.y = focusPoint.y + (int) ( getSize().height * scrollFactor / scalablePicture.getScaleFactor() );
            repaint();
        } else {
            LOGGER.warning( "scrollUp rejected because bottom of picture is already showing." );
        }
    }

    /**
     * method that moves the image down by 10% of the pixels shown on the
     * screen. This method calls <code>repaint()</code> directly since no time
     * consuming image operations need to take place.
     * <p>
     * <img src=../scrollDown.png border=0><p>
     * @see #scrollUp()
     * @see #scrollDown()
     * @see #scrollLeft()
     * @see #scrollRight()
     */
    public void scrollDown() {
        if ( getSize().height / (double) 2 - focusPoint.y * scalablePicture.getScaleFactor() < 0 ) {
            focusPoint.y = focusPoint.y - (int) ( getSize().height * scrollFactor / scalablePicture.getScaleFactor() );
            repaint();
        } else {
            LOGGER.warning( "PicturePane.scrollDown rejected because top edge is aready visible" );
        }
    }

    /**
     * method that moves the image left by 10% of the pixels shown on the
     * screen. This method calls <code>repaint()</code> directly since no time
     * consuming image operations need to take place. works just like
     * {@link #scrollUp()}.
     *
     * @see #scrollUp()
     * @see #scrollDown()
     * @see #scrollLeft()
     * @see #scrollRight()
     */
    public void scrollLeft() {
        // if the bottom edge of the picture is visible, do not scroll
        if ( ( ( scalablePicture.getOriginalWidth() - focusPoint.x ) * scalablePicture.getScaleFactor() ) + getSize().width / (double) 2 > getSize().width ) {
            focusPoint.x = focusPoint.x + (int) ( getSize().width * scrollFactor / scalablePicture.getScaleFactor() );
            repaint();
        } else {
            LOGGER.warning( "scrollLeft rejected because right edge of picture is already showing." );
        }
    }

    /**
     * method that moves the image right by 10% of the pixels shown on the
     * screen. This method calls <code>repaint()</code> directly since no time
     * consuming image operations need to take place. works just liks
     * {@link #scrollDown()}.
     *
     * @see #scrollUp()
     * @see #scrollDown()
     * @see #scrollLeft()
     * @see #scrollRight()
     */
    public void scrollRight() {
        if ( getSize().width / (double) 2 - focusPoint.x * scalablePicture.getScaleFactor() < 0 ) {
            focusPoint.x = focusPoint.x - (int) ( getSize().width * scrollFactor / scalablePicture.getScaleFactor() );
            repaint();
        } else {
            LOGGER.warning( "scrollRight rejected because left edge is aready visible" );
        }
    }

    /**
     * method to set the center of the image to the true coordinates in the
     * picture but doesn't call <code>repaint()</code>
     *
     * @param Xparameter
     * @param Yparameter
     */
    public void setCenterLocation( int Xparameter, int Yparameter ) {
        focusPoint.setLocation( Xparameter, Yparameter );
    }

    /**
     * we are overriding the default paintComponent method, grabbing the
     * Graphics handle and doing our own drawing here. Essentially this method
     * draws a large black rectangle. A drawRenderedImage is then painted doing
     * an affine transformation on the scaled image to position it so the the
     * desired point is in the middle of the Graphics object. The picture is not
     * scaled here because this is a slow operation and only needs to be done
     * once, while moving the image is something the user is likely to do more
     * often.
     *
     * @param g
     */
    @Override
    public void paintComponent( Graphics g ) {
        int WindowWidth = getSize().width;
        int WindowHeight = getSize().height;

        if ( scalablePicture.getScaledPicture() != null ) {
            Graphics2D g2d = (Graphics2D) g;

            int X_Offset = (int) ( (double) ( WindowWidth / (double) 2 ) - ( focusPoint.x * scalablePicture.getScaleFactor() ) );
            int Y_Offset = (int) ( (double) ( WindowHeight / (double) 2 ) - ( focusPoint.y * scalablePicture.getScaleFactor() ) );

            // clear damaged component area
            Rectangle clipBounds = g2d.getClipBounds();
            g2d.setColor( getBackground() );
            g2d.fillRect( clipBounds.x,
                    clipBounds.y,
                    clipBounds.width,
                    clipBounds.height );

            g2d.drawRenderedImage( scalablePicture.getScaledPicture(), AffineTransform.getTranslateInstance( X_Offset, Y_Offset ) );

            g2d.setColor( infoFontColor );
            switch ( showInfo ) {
                case NO_OVERLAY:
                    break;
                case PHOTOGRAPHIC_OVERLAY:
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoCamera" ), infoPoint.x, infoPoint.y + ( 0 * lineSpacing ) );
                    g2d.drawString( exifInfo.camera, infoPoint.x + tabstop, infoPoint.y + ( 0 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoLens" ), infoPoint.x, infoPoint.y + ( 1 * lineSpacing ) );
                    g2d.drawString( exifInfo.lens, infoPoint.x + tabstop, infoPoint.y + ( 1 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoShutterSpeed" ), infoPoint.x, infoPoint.y + ( 2 * lineSpacing ) );
                    g2d.drawString( exifInfo.shutterSpeed, infoPoint.x + tabstop, infoPoint.y + ( 2 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoAperture" ), infoPoint.x, infoPoint.y + ( 3 * lineSpacing ) );
                    g2d.drawString( exifInfo.aperture, infoPoint.x + tabstop, infoPoint.y + ( 3 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoFocalLength" ), infoPoint.x, infoPoint.y + ( 4 * lineSpacing ) );
                    g2d.drawString( exifInfo.focalLength, infoPoint.x + tabstop, infoPoint.y + ( 4 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoISO" ), infoPoint.x, infoPoint.y + ( 5 * lineSpacing ) );
                    g2d.drawString( exifInfo.iso, infoPoint.x + tabstop, infoPoint.y + ( 5 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoTimeStamp" ), infoPoint.x, infoPoint.y + ( 6 * lineSpacing ) );
                    g2d.drawString( exifInfo.getCreateDateTime(), infoPoint.x + tabstop, infoPoint.y + ( 6 * lineSpacing ) );
                    break;
                case APPLICATION_OVERLAY:
                    g2d.drawString( legend, infoPoint.x, infoPoint.y + ( 0 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "PicturePaneSize" ) + Integer.toString( scalablePicture.getOriginalWidth() ) + " x " + Integer.toString( scalablePicture.getOriginalHeight() ) + Settings.jpoResources.getString( "PicturePaneMidpoint" ) + Integer.toString( focusPoint.x ) + " x " + Integer.toString( focusPoint.y ) + " Scale: " + TWO_DECIMAL_FORMATTER.format( scalablePicture.getScaleFactor() ), infoPoint.x, infoPoint.y + ( 1 * lineSpacing ) );
                    g2d.drawString( "File: " + scalablePicture.getFilename(), infoPoint.x, infoPoint.y + ( 2 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "PicturePaneLoadTime" ) + TWO_DECIMAL_FORMATTER.format( scalablePicture.getSourcePicture().loadTime / 1000F ) + Settings.jpoResources.getString( "PicturePaneSeconds" ), infoPoint.x, infoPoint.y + ( 3 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "PicturePaneFreeMemory" ) + Tools.freeMemory(), infoPoint.x, infoPoint.y + ( 4 * lineSpacing ) );
                    break;
            }
        } else {
            // paint a black square
            g.setClip( 0, 0, WindowWidth, WindowHeight );
            g.setColor( Color.black );
            g.fillRect( 0, 0, WindowWidth, WindowHeight );
        }
    }

    /**
     * The type of overlay that should be shown
     */
    public static enum InfoOverlay {

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
    public void cylceInfoDisplay() {
        switch ( showInfo ) {
            case NO_OVERLAY:
                showInfo = PHOTOGRAPHIC_OVERLAY;
                break;
            case PHOTOGRAPHIC_OVERLAY:
                showInfo = APPLICATION_OVERLAY;
                break;
            case APPLICATION_OVERLAY:
                showInfo = NO_OVERLAY;
                break;
        }
        repaint();
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
     * @param pictureStatusCode
     * @param pictureStatusMessage
     */
    @Override
    public void scalableStatusChange( ScalablePictureStatus pictureStatusCode,
            String pictureStatusMessage ) {
        LOGGER.log( Level.FINE, "PicturePane.scalableStatusChange: got a status change: {0}", pictureStatusMessage );

        if ( pictureStatusCode == SCALABLE_PICTURE_READY ) {
            LOGGER.fine( "PicturePane.scalableStatusChange: a READY status" );
            //pictureStatusMessage = legend;
            pictureStatusMessage = Settings.jpoResources.getString( "PicturePaneReadyStatus" );
            if ( centerWhenScaled ) {
                LOGGER.fine( "PicturePane.scalableStatusChange: centering image" );
                centerImage();
            }
            LOGGER.fine( "PicturePane.scalableStatusChange: forcing Panel repaint" );
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
     * @param statusCode
     * @param percentage
     */
    @Override
    public void sourceLoadProgressNotification( SourcePictureStatus statusCode, int percentage ) {
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
    private final Set<ScalablePictureListener> picturePaneListeners = Collections.synchronizedSet( new HashSet<ScalablePictureListener>() );

    /**
     * method to register the listening object of the status events
     *
     * @param listener
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
     * Returns the text area with the description
     * @return the text area
     */
    public Object getDescriptionJTextArea() {
        throw new UnsupportedOperationException( "Not supported yet." ); //To change body of generated methods, choose Tools | Templates.
    }
}
