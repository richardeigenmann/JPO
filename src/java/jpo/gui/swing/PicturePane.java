package jpo.gui.swing;

import jpo.gui.*;
import jpo.dataModel.Tools;
import jpo.dataModel.ExifInfo;
import jpo.dataModel.Settings;
import jpo.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.Point;
import javax.swing.*;
import java.text.*;
import java.util.Vector;
import java.util.logging.Logger;


/*
PicturePane.java:  a component that can display an image

Copyright (C) 2002 - 2010 Richard Eigenmann.
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
 *   The job of this Component is to scale and display a picture.
 *
 *   It notifies the registered parent about status changes so that a description object can
 *   be updated. When the image has been rendered and displayed the legend of the image is 
 *   send to the parent with the ready status.<p>
 *
 *   The image is centred on the component to the {@link #focusPoint} in the coordinate space 
 *   of the image. This translated using the {@link ScalablePicture#setScaleFactor( double )} to the coordinate 
 *   space of the JComponent<p>
 *
 *   <img src=../Mathematics.png border=0><p>
 *
 * 
 *   The {@link #showInfo} flag controls whether information about the picture is overlayed 
 *   on the image.
 */
public class PicturePane
        extends JComponent
        implements ScalablePictureListener {

    /**
     *   The currently displayed ScalablePicture.
     */
    public ScalablePicture sclPic = new ScalablePicture();


    /**
     *  Flag that lets this JComponent know if the picture is to be fitted into the available space
     *  when the threads return the scaled picture.
     */
    public boolean centerWhenScaled;

    /**
     * This point of the picture will be put at the middle of the screen component.
     * The coordinates are in x,y in the coordinate space of the picture.
     */
    public Point focusPoint = new Point();

    /**
     *  The legend of the picture. Is sent to the listener when the image is ready.
     */
    public String legend = null;

    /**
     *   location of the info texts if shown
     */
    private static final Point infoPoint = new Point( 15, 15 );

    /**
     *   line spacing for the info text that can be superimposed on the picture
     */
    private static final int lineSpacing = 12;

    /**
     *   line spacing for the info text that can be superimposed on the picture
     */
    private static final int tabstop = 90;

    /**
     *   Font for the info if shown.
     */
    private static final Font infoFont = Font.decode( Settings.jpoResources.getString( "PicturePaneInfoFont" ) );

    /**
     *  Color for the info overly
     */
    private static final Color infoFontColor = Color.white;

    /**
     *  This object is a reference to an Exif Info object that tries to keep tabs on the
     *  information in the image.
     */
    public ExifInfo ei = new ExifInfo();

    /**
     *  class to format the scale
     */
    private DecimalFormat twoDecimalFormatter = new DecimalFormat( "###0.00" );

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( PicturePane.class.getName() );


    /**
     * Constructs a PicturePane components.
     **/
    public PicturePane() {
        // make graphics faster
        this.setDoubleBuffered( false );


        sclPic.addStatusListener( this );
        if ( Settings.pictureViewerFastScale ) {
            sclPic.setFastScale();
        } else {
            sclPic.setQualityScale();
        }

        this.addComponentListener( new ComponentAdapter() {

            @Override
            public void componentResized( ComponentEvent event ) {
                zoomToFit();
            }
        } );

        setFont( infoFont );
    }



    /////////////////////////
    // Zooming Methods     //
    /////////////////////////

    /**
     * Multiplies the scale factor so that paint() method scales the
     * image larger. This method calls
     * {@link ScalablePicture#createScaledPictureInThread(int)} which in
     * turn will tell this object by means of the status update that
     * the image is ready and should be repainted.
     */
    public void zoomIn() {
        double OldScaleFactor = sclPic.getScaleFactor();
        double NewScaleFactor = OldScaleFactor * 1.5;

        // If scaling goes from scale down to scale up, set ScaleFactor to exactly 1
        if ( ( OldScaleFactor < 1 ) && ( NewScaleFactor > 1 ) ) {
            NewScaleFactor = 1;
        }


        // Check if the picture would get to large and cause the system to "hang"
        if ( ( sclPic.getOriginalWidth() * sclPic.getScaleFactor() < Settings.maximumPictureSize ) && ( sclPic.getOriginalHeight() * sclPic.getScaleFactor() < Settings.maximumPictureSize ) ) {
            sclPic.setScaleFactor( NewScaleFactor );
            sclPic.createScaledPictureInThread( Thread.MAX_PRIORITY );
        }
    }


    /**
     *  method that zooms out on the image leaving the center where it is.
     *  This method calls
     * {@link ScalablePicture#createScaledPictureInThread(int)} which in
     * turn will tell this oject by means of the status update that
     * the image is ready and should be repainted.
     */
    public void zoomOut() {
        sclPic.setScaleFactor( sclPic.getScaleFactor() / 1.5 );
        sclPic.createScaledPictureInThread( Thread.MAX_PRIORITY );
    }


    /**
     *  this method sets the desired scaled size of the ScalablePicture
     *  to the size of the JPanel and fires off a createScaledPictureInThread
     *  request if the ScalablePicture has been loaded or is ready.
     *
     *  @see ScalablePicture#createScaledPictureInThread(int)
     *
     */
    public void zoomToFit() {
        sclPic.setScaleSize( getSize() );
        // prevent useless rescale events when the picture is not ready
        if ( sclPic.getStatusCode() == ScalablePicture.LOADED || sclPic.getStatusCode() == ScalablePicture.READY ) {
            sclPic.createScaledPictureInThread( Thread.MAX_PRIORITY );
        }
    }


    /**
     *  method that zooms the image to 100%.
     *  This method calls
     * {@link ScalablePicture#createScaledPictureInThread(int)} which in
     * turn will tell this object by means of the status update that
     * the image is ready and should be repainted.
     */
    public void zoomFull() {
        sclPic.setScaleFactor( 1 );
        sclPic.createScaledPictureInThread( Thread.MAX_PRIORITY );
    }

    ///////////////////////////////////////////////////////////////
    // Scrolling Methods                                         //
    ///////////////////////////////////////////////////////////////

    /**
     * Set image to center of panel by putting the coordinates of the middle of the original image into the
     * Center to X and Center to Y varaibles by invoking the setCenterLoaction method.
     * This method
     * calls <code>repaint()</code> directly since no time consuming image operations need to take place.
     */
    public void centerImage() {
        if ( sclPic.getOriginalImage() != null ) {
            setCenterLocation( sclPic.getOriginalWidth() / 2, sclPic.getOriginalHeight() / 2 );
            repaint();
        }
    }


    /**
     * This is the factor by how much the scrollxxx methods will scroll.
     * Currently set to a fixed 10%.
     */
    private static final float scrollFactor = 0.05f;

    /**
     *  method that moves the image up by 10% of the pixels shown on the screen.
     * This method
     * calls <code>repaint()</code> directly since no time consuming image operations need to take place.
     *  <p><img src=../scrollUp.png border=0><p>
     *  @see #scrollUp()
     *  @see #scrollDown()
     *  @see #scrollLeft()
     *  @see #scrollRight()
     *
     */
    public void scrollUp() {
        // if the bottom edge of the picture is visible, do not scroll
        if ( ( ( sclPic.getOriginalHeight() - focusPoint.y ) * sclPic.getScaleFactor() ) + getSize().height / 2 > getSize().height ) {
            focusPoint.y = focusPoint.y + (int) ( getSize().height * scrollFactor / sclPic.getScaleFactor() );
            repaint();
        } else {
            logger.warning( "PicturePane.scrollUp rejected because bottom of picture is already showing." );
        }
    }


    /**
     *  method that moves the image down by 10% of the pixels shown on the screen.
     * This method
     * calls <code>repaint()</code> directly since no time consuming image operations need to take place.
     *  <p><img src=../scrollDown.png border=0><p>
     *  @see #scrollUp()
     *  @see #scrollDown()
     *  @see #scrollLeft()
     *  @see #scrollRight()
     */
    public void scrollDown() {
        if ( getSize().height / 2 - focusPoint.y * sclPic.getScaleFactor() < 0 ) {
            focusPoint.y = focusPoint.y - (int) ( getSize().height * scrollFactor / sclPic.getScaleFactor() );
            repaint();
        } else {
            logger.warning( "PicturePane.scrollDown rejected because top edge is aready visible" );
        }
    }


    /**
     *  method that moves the image left by 10% of the pixels shown on the screen.
     * This method
     * calls <code>repaint()</code> directly since no time consuming image operations need to take place.
     *  works just like {@link #scrollUp()}.
     *  @see #scrollUp()
     *  @see #scrollDown()
     *  @see #scrollLeft()
     *  @see #scrollRight()
     */
    public void scrollLeft() {
        // if the bottom edge of the picture is visible, do not scroll
        if ( ( ( sclPic.getOriginalWidth() - focusPoint.x ) * sclPic.getScaleFactor() ) + getSize().width / 2 > getSize().width ) {
            focusPoint.x = focusPoint.x + (int) ( getSize().width * scrollFactor / sclPic.getScaleFactor() );
            repaint();
        } else {
            logger.warning( "scrollLeft rejected because right edge of picture is already showing." );
        }
    }


    /**
     *  method that moves the image right by 10% of the pixels shown on the screen.
     * This method
     * calls <code>repaint()</code> directly since no time consuming image operations need to take place.
     *  works just liks {@link #scrollDown()}.
     *  @see #scrollUp()
     *  @see #scrollDown()
     *  @see #scrollLeft()
     *  @see #scrollRight()
     */
    public void scrollRight() {
        if ( getSize().width / 2 - focusPoint.x * sclPic.getScaleFactor() < 0 ) {
            focusPoint.x = focusPoint.x - (int) ( getSize().width * scrollFactor / sclPic.getScaleFactor() );
            repaint();
        } else {
            logger.warning( "scrollRight rejected because left edge is aready visible" );
        }
    }


    /**
     *  method to set the center of the image to the true coordinates in the picture but doesn't call <code>repaint()</code>
     *
     * @param Xparameter
     * @param Yparameter
     */
    public void setCenterLocation( int Xparameter, int Yparameter ) {
        focusPoint.setLocation( Xparameter, Yparameter );
    }


    /**
     *   we are overriding the default paintComponent method, grabbing the Graphics
     *   handle and doing our own drawing here. Essentially this method draws a large
     *   black rectangle. A drawRenderedImage is then painted doing an affine transformation
     *   on the scaled image to position it so the the desired point is in the middle of the
     *   Graphics object. The picture is not scaled here because this is a slow operation
     *   and only needs to be done once, while moving the image is something the user is
     *   likely to do more often.
     * @param g
     */
    @Override
    public void paintComponent( Graphics g ) {
        int WindowWidth = getSize().width;
        int WindowHeight = getSize().height;

    

        if ( sclPic.getScaledPicture() != null ) {
            Graphics2D g2d = (Graphics2D) g;

            int X_Offset = (int) ( (double) ( WindowWidth / 2 ) - ( focusPoint.x * sclPic.getScaleFactor() ) );
            int Y_Offset = (int) ( (double) ( WindowHeight / 2 ) - ( focusPoint.y * sclPic.getScaleFactor() ) );

            // clear damaged component area
            Rectangle clipBounds = g2d.getClipBounds();
            g2d.setColor( getBackground() );
            g2d.fillRect( clipBounds.x,
                    clipBounds.y,
                    clipBounds.width,
                    clipBounds.height );


            g2d.drawRenderedImage( sclPic.getScaledPicture(), AffineTransform.getTranslateInstance( X_Offset, Y_Offset ) );

            g2d.setColor( infoFontColor );
            switch ( showInfo ) {
                case DISPLAY_NONE:
                    break;
                case DISPLAY_PHOTOGRAPHIC:
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoCamera" ), infoPoint.x, infoPoint.y + ( 0 * lineSpacing ) );
                    g2d.drawString( ei.camera, infoPoint.x + tabstop, infoPoint.y + ( 0 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoLens" ), infoPoint.x, infoPoint.y + ( 1 * lineSpacing ) );
                    g2d.drawString( ei.lens, infoPoint.x + tabstop, infoPoint.y + ( 1 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoShutterSpeed" ), infoPoint.x, infoPoint.y + ( 2 * lineSpacing ) );
                    g2d.drawString( ei.shutterSpeed, infoPoint.x + tabstop, infoPoint.y + ( 2 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoAperture" ), infoPoint.x, infoPoint.y + ( 3 * lineSpacing ) );
                    g2d.drawString( ei.aperture, infoPoint.x + tabstop, infoPoint.y + ( 3 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoFocalLength" ), infoPoint.x, infoPoint.y + ( 4 * lineSpacing ) );
                    g2d.drawString( ei.focalLength, infoPoint.x + tabstop, infoPoint.y + ( 4 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoISO" ), infoPoint.x, infoPoint.y + ( 5 * lineSpacing ) );
                    g2d.drawString( ei.iso, infoPoint.x + tabstop, infoPoint.y + ( 5 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "ExifInfoTimeStamp" ), infoPoint.x, infoPoint.y + ( 6 * lineSpacing ) );
                    g2d.drawString( ei.dateTime, infoPoint.x + tabstop, infoPoint.y + ( 6 * lineSpacing ) );
                    break;
                case DISPLAY_APPLICATION:
                    g2d.drawString( legend, infoPoint.x, infoPoint.y + ( 0 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "PicturePaneSize" ) + Integer.toString( sclPic.getOriginalWidth() ) + " x " + Integer.toString( sclPic.getOriginalHeight() ) + Settings.jpoResources.getString( "PicturePaneMidpoint" ) + Integer.toString( focusPoint.x ) + " x " + Integer.toString( focusPoint.y ) + " Scale: " + twoDecimalFormatter.format( sclPic.getScaleFactor() ), infoPoint.x, infoPoint.y + ( 1 * lineSpacing ) );
                    g2d.drawString( "File: " + sclPic.getFilename(), infoPoint.x, infoPoint.y + ( 2 * lineSpacing ) );
                    g2d.drawString( Settings.jpoResources.getString( "PicturePaneLoadTime" ) + twoDecimalFormatter.format( sclPic.getSourcePicture().loadTime / 1000F ) + Settings.jpoResources.getString( "PicturePaneSeconds" ), infoPoint.x, infoPoint.y + ( 3 * lineSpacing ) );
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
     *  Constant to indicate that no information should be overlaid on the picture
     */
    public static final int DISPLAY_NONE = 0;

    /**
     *  Constant to indicate that photographic information should be displayed on the picture
     */
    public static final int DISPLAY_PHOTOGRAPHIC = DISPLAY_NONE + 1;

    /**
     *  Constant to indicate that Application related information should be displayed on the picture
     */
    public static final int DISPLAY_APPLICATION = DISPLAY_PHOTOGRAPHIC + 1;

    /**
     *  Code that determines what info is to be displayed over the picture.
     */
    private int showInfo = DISPLAY_NONE;


    /**
     *  This function cycles to the next info display. The first is DISPLAY_NONE, DISPLAY_PHOTOGRAPHIC
     *  and DISPLAY_APPLICATION
     **/
    public void cylceInfoDisplay() {
        switch ( showInfo ) {
            case DISPLAY_NONE:
                showInfo = DISPLAY_PHOTOGRAPHIC;
                break;
            case DISPLAY_PHOTOGRAPHIC:
                showInfo = DISPLAY_APPLICATION;
                break;
            case DISPLAY_APPLICATION:
                showInfo = DISPLAY_NONE;
                break;
        }
        repaint();
    }


    /**
     *  method that gets invoked from the ScalablePicture object to notify of status changes.
     *  The ScalablePicture goes through several statuses: UNINITIALISED, GARBAGE_COLLECTION,
     *  LOADING, SCALING, READY, ERROR.<p>
     *  Each status is passed to the listener upon receipt.<p>
     *  When the ScalablePicture signals that it is READY the legend of the picture is sent
     *  to the listener. The method {@link #centerImage} is called and a repaint is requested.
     * @param pictureStatusCode 
     * @param pictureStatusMessage
     */
    public void scalableStatusChange( int pictureStatusCode,
            String pictureStatusMessage ) {
        logger.fine( "PicturePane.scalableStatusChange: got a status change: " + pictureStatusMessage );

        if ( pictureStatusCode == ScalablePicture.READY ) {
            logger.fine( "PicturePane.scalableStatusChange: a READY status" );
            //pictureStatusMessage = legend;
            pictureStatusMessage = Settings.jpoResources.getString( "PicturePaneReadyStatus" );
            if ( centerWhenScaled ) {
                logger.fine( "PicturePane.scalableStatusChange: centering image" );
                centerImage();
            }
            logger.fine( "PicturePane.scalableStatusChange: forcing Panel repaint" );
            repaint();
        }

        for ( ScalablePictureListener scalablePictureListener : picturePaneListeners ) {
            scalablePictureListener.scalableStatusChange( pictureStatusCode, pictureStatusMessage );
        }
    }


    /**
     *  pass messages about progress onto the PictureViewer for updating of the progress bar
     * @param statusCode
     * @param percentage
     */
    public void sourceLoadProgressNotification( int statusCode, int percentage ) {
        for ( ScalablePictureListener scalablePictureListener : picturePaneListeners ) {
            scalablePictureListener.sourceLoadProgressNotification( statusCode, percentage );
        }
    }

    /**
     *  This Vector hold references to objects that would like to
     *  receive notifications about what is going on with the ScalablePicture
     *  being displayed in this PicturePane. These objects
     *  must implement the ScalablePictureListener interface.
     */
    protected Vector<ScalablePictureListener> picturePaneListeners = new Vector<ScalablePictureListener>();


    /**
     *  method to register the listening object of the status events
     * @param listener 
     */
    public void addStatusListener( ScalablePictureListener listener ) {
        picturePaneListeners.add( listener );
    }


    /**
     * deregister the listening object of the status events
     * @param listener the listener to remove
     */
    public void removeStatusListener( ScalablePictureListener listener ) {
        picturePaneListeners.remove( listener );
    }


    /**
     *  method that returns a handle to the scalable picture that this component is displaying
     * @return the scaled image
     */
    public ScalablePicture getScalablePicture() {
        return sclPic;
    }
}
