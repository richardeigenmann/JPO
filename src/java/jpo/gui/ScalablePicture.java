package jpo.gui;

import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.*;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/*
ScalablePicture.java:  class that can load and save images

Copyright (C) 2002 - 2009  Richard Eigenmann.
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
 *  a class to load and scale an image either immediately or in a seperate thread.
 */
public class ScalablePicture
        implements SourcePictureListener {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( ScalablePicture.class.getName() );

    /**
     *   the source picture for the scalable picture
     */
    public SourcePicture sourcePicture;// = new SourcePicture();

    /**
     *  The scaled version of the image
     */
    public BufferedImage scaledPicture = null;

    /**
     *  The scaling factor
     */
    private double ScaleFactor;

    /**
     *  the URL of the picture
     */
    public URL imageUrl = null;

    /**
     *  variable to compose te status message
     */
    private String pictureStatusMessage;

    /**
     *  if true means that the image should be scaled so that it fits inside
     *  a given dimension (TargetSize). If false the ScaleFactor should be used.
     */
    private boolean scaleToSize;

    /**
     *  variable to record the size of the box that the scaled image must fit into.
     */
    private Dimension TargetSize;

    /**
     *  status code used to signal that the picture is not loaded
     */
    public static final int UNINITIALISED = SourcePicture.LOADING_COMPLETED + 1;

    /**
     *  status code used to signal that the picture is cleaning up memory
     */
    public static final int GARBAGE_COLLECTION = UNINITIALISED + 1;

    /**
     * status code used to signal that the thread is loading the image
     */
    public static final int LOADING = GARBAGE_COLLECTION + 1;

    /**
     * status code used to signal that the thread has finished loading the image
     */
    public static final int LOADED = LOADING + 1;

    /**
     * status code used to signal that the thread has loaded the tread is scaling the image
     */
    public static final int SCALING = LOADED + 1;

    /**
     *  status code used to signal that the image is available.
     */
    public static final int READY = SCALING + 1;

    /**
     *  status code used to signal that there was an error
     */
    public static final int ERROR = READY + 1;

    /**
     *   thingy to scale the image
     */
    private AffineTransformOp op;

    /**
     *   the quality with which the JPG pictures shall be written. A value of 0 means poor 1 means great.
     */
    public float jpgQuality = 0.8f;

    /**
     *   which method to use on scaling, a fast one or a good quality one. Unfortunately the
     *   good quality AffineTransformOp converts the image to a colorspace in the output JPEG file
     *   which most programs can't read so this flag is currently not considered. RE 7.9.2005
     */
    public boolean fastScale = true;

    /**
     * Variable that indicates how often to scale down the image. Multiple steps (surprisingly)
     * results in better quality!
     */
    private int scaleSteps = 1;

    /**
     *  flag that indicates that the image should be scaled after
     *  a status message is received from the SourcePicture that the
     *  picture was loaded.
     */
    public boolean scaleAfterLoad = false;


    /**
     *   Constructor
     */
    public ScalablePicture() {
        setStatus( UNINITIALISED, Settings.jpoResources.getString( "ScalablePictureUninitialisedStatus" ) );
        setScaleFactor( (double) 1 );
    }


    /**
     *  method to invoke with a filename or URL of a picture that is to be loaded and scaled in
     *  a new thread. This is handy to update the screen while the loading chuggs along in the background.
     *  Make sure you invoked setScaleFactor or setScaleSize before
     *  invoking this method.
     *
     *  Step 1: Am I already loading what I need somewhere?
     *          If yes -> use it.
     *	              Has it finished loading?
     *                    If no -> wait for it
     *                    If yes -> use it
     *          Else -> load it
     *
     *  @param	imageUrl	The URL of the image you want to load
     *  @param 	priority	The Thread priority
     *  @param	rotation	The rotation 0-360 that the image should be put through
     *				after loading.
     */
    public void loadAndScalePictureInThread( URL imageUrl, int priority,
            double rotation ) {
        this.imageUrl = imageUrl;

        boolean alreadyLoading = false;
        logger.fine( "Checking if picture " + imageUrl + " is already being loaded." );
        if ( ( sourcePicture != null ) && ( sourcePicture.getUrl() != null ) && ( sourcePicture.getUrl().equals( imageUrl ) ) ) {
            logger.fine( "the SourcePicture is already loading the sourcePicture image" );
            if ( sourcePicture.getRotation() == rotation ) {
                alreadyLoading = true;
                logger.fine( "Picture was even rotated to the correct angle!" );
            } else {
                alreadyLoading = false;
                logger.fine( "Picture was in cache but with wrong rotation. Forcing reload." );
            }
        } else if ( PictureCache.isInCache( imageUrl ) ) {
            // in case the old image has a listener connected remove it
            //  fist time round the sourcePicture is still null therefore the if.
            if ( sourcePicture != null ) {
                sourcePicture.removeListener( this );
            }

            sourcePicture = PictureCache.getSourcePicture( imageUrl );
            String status = sourcePicture.getStatusMessage();
            if ( status == null ) {
                status = "";
            }
            logger.fine( "Picture in cache! Status: " + status );

            if ( sourcePicture.getRotation() == rotation ) {
                alreadyLoading = true;
                logger.fine( "Picture was even rotated to the correct angle!" );
            } else {
                alreadyLoading = false;
                logger.fine( "Picture was in cache but with wrong rotation. Forcing reload." );
            }
        }


        if ( alreadyLoading ) {
            switch ( sourcePicture.getStatusCode() ) {
                case SourcePicture.UNINITIALISED:
                    alreadyLoading = false;
                    // logger.info("ScalablePicture.loadAndScalePictureInThread: pictureStatus was: UNINITIALISED");
                    break;
                case SourcePicture.ERROR:
                    alreadyLoading = false;
                    // logger.info("ScalablePicture.loadAndScalePictureInThread: pictureStatus was: ERROR");
                    break;
                case SourcePicture.LOADING:
                    // logger.info("ScalablePicture.loadAndScalePictureInThread: pictureStatus was: LOADING");
                    sourcePicture.addListener( this );
                    setStatus( LOADING, Settings.jpoResources.getString( "ScalablePictureLoadingStatus" ) );
                    sourceLoadProgressNotification( SourcePicture.LOADING_PROGRESS, sourcePicture.getPercentLoaded() );
                    scaleAfterLoad = true;
                    break;
                case SourcePicture.ROTATING:
                    // logger.info("ScalablePicture.loadAndScalePictureInThread: pictureStatus was: ROTATING");
                    setStatus( LOADING, Settings.jpoResources.getString( "ScalablePictureRotatingStatus" ) );
                    sourceLoadProgressNotification( SourcePicture.LOADING_PROGRESS, sourcePicture.getPercentLoaded() );
                    scaleAfterLoad = true;
                    break;
                case SourcePicture.READY:
                    //logger.info("ScalablePicture.loadAndScalePictureInThread: pictureStatus was: READY. Sending SCALING status.");
                    setStatus( SCALING, Settings.jpoResources.getString( "ScalablePictureScalingStatus" ) );
                    createScaledPictureInThread( priority );
                    break;
                default:
                    // logger.info("ScalablePicture.loadAndScalePictureInThread: Don't know what status this is:" + Integer.toString(sourcePicture.getStatusCode()) );
                    break;

            }
        }

        // if the image is not already there then load it.
        if ( !alreadyLoading ) {
            if ( sourcePicture != null ) {
                sourcePicture.removeListener( this );
            }
            sourcePicture = new SourcePicture();
            sourcePicture.addListener( this );
            setStatus( LOADING, Settings.jpoResources.getString( "ScalablePictureLoadingStatus" ) );
            scaleAfterLoad = true;
            sourcePicture.loadPictureInThread( imageUrl, priority, rotation );
            // when the thread is done it sends a sourceStatusChange message to us
        }
    }


    /**
     *  Synchroneous method to load the image.
     *  It should only be called by something which is a thread itself such as the HtmlDistillerThread.
     *  Since this intended for large batch operations this bypasses the cache.
     *  There are no status updates
     *  @param  imageUrl  The Url of the image to be loaded
     *  @param  rotation  The angle by which it is to be roated upon loading.
     */
    public void loadPictureImd( URL imageUrl, double rotation ) {
        logger.fine( "Invoked on URL: " + imageUrl.toString() );
        if ( sourcePicture != null ) {
            sourcePicture.removeListener( this );
        }
        sourcePicture = new SourcePicture();
        //sourcePicture.addListener( this );
        //setStatus( LOADING, Settings.jpoResources.getString( "ScalablePictureLoadingStatus" ) );
        scaleAfterLoad = false;
        sourcePicture.loadPicture( imageUrl, rotation );
    }


    /**
     *  stops all picture loading except if the Url we desire is being loaded
     *  @param  url	The URL of the image which is to be loaded.
     */
    public void stopLoadingExcept( URL url ) {
        if ( sourcePicture != null ) {
            boolean isCurrentlyLoading = sourcePicture.stopLoadingExcept( url );
            if ( !isCurrentlyLoading ) {
                sourcePicture.removeListener( this );
            }
            //PictureCache.stopBackgroundLoadingExcept( url );
        }
    }


    /**
     *  method that is invoked by the SourcePictureListener interface. Usually this
     *  will be called by the SourcePicture telling the ScalablePicture that
     *  it has completed loading. The ScalablePicture should then change it's own
     *  status and tell the ScalableListeners what's up.
     * @param statusCode
     * @param statusMessage
     * @param sp
     */
    public void sourceStatusChange( int statusCode, String statusMessage,
            SourcePicture sp ) {
        //logger.info("ScalablePicture.sourceStatusChange: status received from SourceImage: " + statusMessage);

        switch ( statusCode ) {
            case SourcePicture.UNINITIALISED:
                // logger.info("ScalablePicture.sourceStatusChange: pictureStatus was: UNINITIALISED message: " + statusMessage );
                setStatus( UNINITIALISED, statusMessage );
                break;
            case SourcePicture.ERROR:
                // logger.info("ScalablePicture.sourceStatusChange: pictureStatus was: ERROR message: " + statusMessage );
                setStatus( ERROR, statusMessage );
                sourcePicture.removeListener( this );
                break;
            case SourcePicture.LOADING:
                // logger.info("ScalablePicture.sourceStatusChange: pictureStatus was: LOADING message: " + statusMessage );
                setStatus( LOADING, statusMessage );
                break;
            case SourcePicture.ROTATING:
                // logger.info("ScalablePicture.sourceStatusChange: pictureStatus was: ROTATING message: " + statusMessage );
                setStatus( LOADING, statusMessage );
                break;
            case SourcePicture.READY:
                // logger.info("ScalablePicture.sourceStatusChange: pictureStatus was: READY message: " + statusMessage );
                setStatus( LOADED, statusMessage );
                sourcePicture.removeListener( this );
                if ( scaleAfterLoad ) {
                    createScaledPictureInThread( Thread.MAX_PRIORITY );
                    scaleAfterLoad = false;
                }
                break;
            default:
                // logger.info("ScalablePicture.sourceStatusChange: Don't recognize this status: " + statusMessage );
                break;

        }
    }


    /**
     *  method that creates the scaled image in the background in it's own thread.
     *  @param priority  The priority this image takes relative to the others.
     */
    public void createScaledPictureInThread( int priority ) {
        Runnable r = new Runnable() {

            public void run() {
                scalePicture();
            }
        };

        Thread t = new Thread( r );
        t.setPriority( priority );
        t.start();
    }

    /* Taking these variables here to be friendlier on the heap */
    private AffineTransform afStep;

    private AffineTransformOp opStep;

    private Point2D pStep;

    private BufferedImage biStep;


    /**
     *  call this method when the affine transform op is to be executed.
     *
     **/
    public void scalePicture() {
        logger.fine( "scaling..." );
        try {
            setStatus( SCALING, "Scaling picture." );

            if ( ( sourcePicture != null ) && ( sourcePicture.getSourceBufferedImage() != null ) ) {
                if ( scaleToSize ) {
                    int WindowWidth = TargetSize.width;
                    int WindowHeight = TargetSize.height;
                    //logger.info("ScalablePicture.scalePicture: Size of window is: " + Integer.toString(WindowWidth) + " x " + Integer.toString(WindowHeight));


                    int PictureWidth = sourcePicture.getWidth();
                    int PictureHeight = sourcePicture.getHeight();

                    // Scale so that the enire picture fits in the component.
                    if ( ( (double) PictureHeight / WindowHeight ) > ( (double) PictureWidth / WindowWidth ) ) {
                        // Vertical scaling
                        ScaleFactor = ( (double) WindowHeight / PictureHeight );
                    } else {
                        // Horizontal scaling
                        ScaleFactor = ( (double) WindowWidth / PictureWidth );
                    }

                    if ( Settings.dontEnlargeSmallImages && ScaleFactor > 1 ) {
                        ScaleFactor = 1;
                    }
                }

                /* note that I have tried to use other AffineTransformOps such as TYPE_BILINEAR and
                TYPE_BICUBIC. Only they don't work as they muck about with the color channels
                and we end up with a non JFIF compliant JPEG image. This doesn't display well
                in most programs which makes this format useless. This is thoroughly explained
                in the following article. The workaround doesn't work though.
                http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4503132
                RE, 7.9.2005  */

                double factor;
                int affineTransformType;
                if ( fastScale ) {
                    affineTransformType = AffineTransformOp.TYPE_NEAREST_NEIGHBOR;
                    factor = ScaleFactor;
                } else {
                    affineTransformType = AffineTransformOp.TYPE_BICUBIC;
                    factor = Math.pow( ScaleFactor, 1f / getScaleSteps() );
                }

                afStep = AffineTransform.getScaleInstance( factor, factor );
                opStep = new AffineTransformOp( afStep, affineTransformType );
                scaledPicture = sourcePicture.getSourceBufferedImage();
                for ( int i = 0; i < getScaleSteps(); i++ ) {
                    pStep = new Point2D.Float( (float) scaledPicture.getWidth(), (float) ( scaledPicture.getHeight() ) );
                    pStep = afStep.transform( pStep, null );
                    int x = (int) Math.rint( pStep.getX() );
                    int y = (int) Math.rint( pStep.getY() );
                    int imageType = sourcePicture.getSourceBufferedImage().getType();
                    logger.fine( String.format( "getType from source image returned %d", imageType ) );
                    if ( x == 0 ) {
                        x = 100;
                    }
                    if ( y == 0 ) {
                        y = 100;
                    }
                    if ( ( imageType == 0 ) || ( imageType == 13 ) ) {
                        imageType = BufferedImage.TYPE_3BYTE_BGR;
                        logger.fine( String.format( "Becuase we don't like imageType 0 we are setting the target type to BufferedImage.TYPE_3BYTE_BGR which has code: %d", BufferedImage.TYPE_3BYTE_BGR ) );
                    }
                    biStep = new BufferedImage( x, y, imageType );
                    scaledPicture = opStep.filter( scaledPicture, biStep );
                }

                int PictureWidth = scaledPicture.getWidth();
                int PictureHeight = scaledPicture.getHeight();

                setStatus( READY, "Scaled Picture is ready." );
            } else {
                if ( getStatusCode() == LOADING ) {
                    // logger.info ("ScalablePicture.scalePicture invoked while image is still loading. I wonder why?");
                    return;
                } else {
                    setStatus( ERROR, "Could not scale image as SourceImage is null." );
                }
            }
        } catch ( OutOfMemoryError e ) {
            logger.severe( "ScalablePicture.scalePicture caught an OutOfMemoryError while scaling an image.\n" + e.getMessage() );
            Tools.freeMem();

            setStatus( ERROR, "Out of Memory Error while scaling " + imageUrl.toString() );
            scaledPicture = null;
            PictureCache.clear();

            JOptionPane.showMessageDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "outOfMemoryError" ),
                    Settings.jpoResources.getString( "genericError" ),
                    JOptionPane.ERROR_MESSAGE );

            System.gc();
            System.runFinalization();

            logger.info( "ScalablePicture.scalePicture: JPO has now run a garbage collection and finalization." );
            Tools.freeMem();
        }
    }


    /**
     *  set the scale factor to the new desired value. The scale factor is a multiplier by which the original picture
     *  needs to be multiplied to get the size of the picture on the screen. You must
     *  call {@link #createScaledPictureInThread(int)} to
     *  make anything happen.<p>
     *
     *  Example: Original is 3000 x 2000 --> Scale Factor 0.10  --> Target Picutre is 300 x 200
     *
     * @param newFactor
     */
    public void setScaleFactor( double newFactor ) {
        scaleToSize = false;
        TargetSize = null;
        ScaleFactor = newFactor;
    }


    /**
     *  invoke this method to tell the scale process to figure out the scale factor
     *  so that the image fits either by height or by width into the indicated dimension.
     * @param newSize
     */
    public void setScaleSize( Dimension newSize ) {
        scaleToSize = true;
        if ( ( newSize.height < 1 ) || ( newSize.width < 1 ) ) {
            // to prevent the affine transform from failing on a 0 size.
            TargetSize = new Dimension( 100, 100 );
        } else {
            TargetSize = newSize;
        }
    }


    /**
     *   return the current scale factor
     * @return
     */
    public double getScaleFactor() {
        return ScaleFactor;
    }


    /**
     *   return the current scale size. This is the area that the picture
     *   ist fitted into. Since the are could be wider or taller than the picture
     *   will be scaled to there is a different mehtod <code>getScaledSize</code>
     *   that will return the size of the picture.
     * @return
     */
    public Dimension getScaleSize() {
        return TargetSize;
    }


    /**
     *   return the scaled image
     * @return
     */
    public BufferedImage getScaledPicture() {
        return scaledPicture;
    }


    /**
     *   return the scaled image
     * @return
     */
    public ImageIcon getScaledImageIcon() {
        return new ImageIcon( scaledPicture );
    }


    /**
     *   return the size of the scaled image or Zero if there is none
     * @return
     */
    public Dimension getScaledSize() {
        if ( scaledPicture != null ) {
            return new Dimension( scaledPicture.getWidth(), scaledPicture.getHeight() );
        } else {
            return new Dimension( 0, 0 );
        }
    }


    /**
     *   return the size of the scaled image as a neatly formatted text or Zero if there is none
     * @return
     */
    public String getScaledSizeString() {
        if ( scaledPicture != null ) {
            return Integer.toString( scaledPicture.getWidth() ) + " x " + Integer.toString( scaledPicture.getHeight() );
        } else {
            return "0 x 0";
        }
    }


    /**
     *   return the height of the scaled image or Zero if there is none
     * @return
     */
    public int getScaledHeight() {
        if ( scaledPicture != null ) {
            return scaledPicture.getHeight();
        } else {
            return 0;
        }
    }


    /**
     *   return the width of the scaled image or Zero if there is none
     * @return
     */
    public int getScaledWidth() {
        if ( scaledPicture != null ) {
            return scaledPicture.getWidth();
        } else {
            return 0;
        }
    }


    /**
     *   return the image in the original size
     * @return
     */
    public BufferedImage getOriginalImage() {
        return sourcePicture.getSourceBufferedImage();
    }


    /**
     *   return the image in the original size
     * @return
     */
    public SourcePicture getSourcePicture() {
        return sourcePicture;
    }


    /**
     *   return the size of the original image or Zero if there is none
     * @return
     */
    public Dimension getOriginalSize() {
        return sourcePicture.getSize();
    }


    /**
     *   return the height of the original image or Zero if there is none
     * @return
     */
    public int getOriginalHeight() {
        return sourcePicture.getHeight();
    }


    /**
     *   return the width of the original image or Zero if there is none
     * @return
     */
    public int getOriginalWidth() {
        return sourcePicture.getWidth();
    }


    /**
     *   return the filename of the original image
     * @return
     */
    public String getFilename() {
        return imageUrl.toString();
    }


    /**
     *  This method allows the ScalablePicture's scaled BufferedImage to be written
     *  to the desired file.
     *
     *  @param	writeFile	The File that shall receive the jpg data
     */
    public void writeScaledJpg( File writeFile ) {
        writeJpg( writeFile, scaledPicture, jpgQuality );
    }


    /**
     *  This method allows the ScalablePicture's scaled BufferedImage to be written
     *  to the desired output stream.
     *
     *  @param	writeStream	The Stream that shall receive the jpg data
     */
    public void writeScaledJpg( OutputStream writeStream ) {
        writeJpg( writeStream, scaledPicture, jpgQuality );
    }


    /**
     *  This static method writes the indicated renderedImage (BufferedImage)
     *  to the indicated file.
     *
     *  @param	writeFile	The File that shall receive the jpg data
     *  @param	renderedImage	The RenderedImage (BufferedImage) to be written
     *  @param	jpgQuality	The quality with which to compress to jpg
     */
    public static void writeJpg( File writeFile, RenderedImage renderedImage,
            float jpgQuality ) {
        // should be rewritten to use the method below after creating the FileOutputstream, RE 9.1.2005
        Iterator writers = ImageIO.getImageWritersByFormatName( "jpg" );
        ImageWriter writer = (ImageWriter) writers.next();
        JPEGImageWriteParam params = new JPEGImageWriteParam( null );
        params.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
        params.setCompressionQuality( jpgQuality );
        params.setProgressiveMode( ImageWriteParam.MODE_DISABLED );
        params.setDestinationType(
                new ImageTypeSpecifier( IndexColorModel.getRGBdefault(),
                IndexColorModel.getRGBdefault().createCompatibleSampleModel( 16, 16 ) ) );

        try {
            ImageOutputStream ios = ImageIO.createImageOutputStream(
                    new FileOutputStream( writeFile ) );
            writer.setOutput( ios );
            writer.write( null, new IIOImage( renderedImage, null, null ), params );
            ios.close();

        } catch ( IOException e ) {
            logger.info( "ScalablePicture.writeJpg caught IOException: " + e.getMessage() + "\nwhile writing " + writeFile.toString() );
            e.printStackTrace();
        }
        //writer = null;
        writer.dispose(); //1.4.1 documentation says to do this.
    }


    /**
     *  This static method writes the indicated renderedImage (BufferedImage)
     *  to the indicated file.
     *
     *  @param	writeStream	The File that shall receive the jpg data
     *  @param	renderedImage	The RenderedImage (BufferedImage) to be written
     *  @param	jpgQuality	The quality with which to compress to jpg
     */
    public static void writeJpg( OutputStream writeStream,
            RenderedImage renderedImage, float jpgQuality ) {
        Iterator writers = ImageIO.getImageWritersByFormatName( "jpg" );
        ImageWriter writer = (ImageWriter) writers.next();
        JPEGImageWriteParam params = new JPEGImageWriteParam( null );
        params.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
        params.setCompressionQuality( jpgQuality );
        params.setProgressiveMode( ImageWriteParam.MODE_DISABLED );
        params.setDestinationType( new ImageTypeSpecifier( java.awt.image.IndexColorModel.getRGBdefault(),
                IndexColorModel.getRGBdefault().createCompatibleSampleModel( 16, 16 ) ) );

        try {
            ImageOutputStream ios = ImageIO.createImageOutputStream( writeStream );
            writer.setOutput( ios );
            writer.write( null, new IIOImage( renderedImage, null, null ), params );
            ios.close();

        } catch ( IOException e ) {
            logger.info( "ScalablePicture.writeJpg caught IOException: " + e.getMessage() );
            e.printStackTrace();
        }
        //writer = null;
        writer.dispose(); //1.4.1 documentation says to do this.
    }

    /**
     *  The listeners to notify when the image operation changes the status.
     */
    private Vector<ScalablePictureListener> scalablePictureStatusListeners = new Vector<ScalablePictureListener>();


    /**
     *  method to register the listening object of the status events
     * @param listener
     */
    public void addStatusListener( ScalablePictureListener listener ) {
        scalablePictureStatusListeners.add( listener );
    }


    /**
     *  method to register the listening object of the status events
     * @param listener
     */
    public void removeStatusListener( ScalablePictureListener listener ) {
        scalablePictureStatusListeners.remove( listener );
    }

    /**
     *  variable to track the status of the picture
     */
    private int pictureStatusCode;


    /**
     * Method that sets the status of the ScalablePicture object and notifies
     * intereasted objects of a change in status (not built yet).
     */
    private void setStatus( int statusCode, String statusMessage ) {
        String filename = ( imageUrl == null ) ? "" : imageUrl.toString();
        //logger.info("ScalablePicture.setStatus: sending: " + statusMessage + " to all Listeners from Image: " + filename );

        pictureStatusCode = statusCode;
        pictureStatusMessage = statusMessage;

        for ( ScalablePictureListener scalablePictureListener : scalablePictureStatusListeners ) {
            scalablePictureListener.scalableStatusChange( pictureStatusCode, pictureStatusMessage );
        }
    }


    /**
     * pass on the update on the loading Progress to the listening objects
     * @param statusCode
     * @param percentage
     */
    public void sourceLoadProgressNotification( int statusCode, int percentage ) {
        for ( ScalablePictureListener scalablePictureListener : scalablePictureStatusListeners ) {
            scalablePictureListener.sourceLoadProgressNotification( statusCode, percentage );
        }
    }


    /**
     * Method that returns the status code of the picture loading.
     * @return
     */
    public int getStatusCode() {
        //logger.info(String.format( "Returning status code %d which corresponds to message %s", pictureStatusCode, pictureStatusMessage ));
        return pictureStatusCode;
    }


    /**
     * Method that returns the status code of the picture loading.
     * @return
     */
    public String getStatusMessage() {
        return pictureStatusMessage;
    }


    /**
     *  accessor method to set the quality that should be used on jpg write operations.
     * @param quality
     */
    public void setJpgQuality( float quality ) {
        //logger.info( "setJpgQuality requested with " + Float.toString( quality ) );
        if ( quality >= 0f && quality <= 1f ) {
            //logger.info( "Quality set." );
            jpgQuality = quality;
        }
    }


    /**
     *  sets the picture into fast scaling mode
     */
    public void setFastScale() {
        fastScale = true;
    }


    /**
     *  sets the picture into quality sacling mode
     */
    public void setQualityScale() {
        fastScale = false;
    }


   
    /**
     * @return the scaleSteps
     */
    public int getScaleSteps() {
        return scaleSteps;
    }


    /**
     * @param scaleSteps the scaleSteps to set
     */
    public void setScaleSteps( int scaleSteps ) {
        this.scaleSteps = scaleSteps;
    }
}
