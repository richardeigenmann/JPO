package jpo.gui;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import static jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_ERROR;
import static jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_LOADED;
import static jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_LOADING;
import static jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_READY;
import static jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_SCALING;
import static jpo.gui.ScalablePicture.ScalablePictureStatus.SCALABLE_PICTURE_UNINITIALISED;
import jpo.gui.SourcePicture.SourcePictureStatus;
import jpo.gui.swing.PictureControllerImage;

/*
 ScalablePicture.java:  class that can load and save images

 Copyright (C) 2002 - 2017  Richard Eigenmann.
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
 * a class to load and scale an image either immediately or in a separate
 * thread.
 */
public class ScalablePicture
        implements SourcePictureListener, PictureControllerImage {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ScalablePicture.class.getName() );

    /**
     * the source picture for the scalable picture
     */
    public SourcePicture sourcePicture;

    /**
     * The scaled version of the image
     */
    public BufferedImage scaledPicture;

    /**
     * The scaling factor
     */
    private double scaleFactor;

    /**
     * the URL of the picture
     */
    public URL imageUrl;

    /**
     * variable to compose the status message
     */
    private String pictureStatusMessage;

    /**
     * if true means that the image should be scaled so that it fits inside a
     * given dimension (TargetSize). If false the ScaleFactor should be used.
     */
    private boolean scaleToSize;

    /**
     * variable to record the size of the box that the scaled image must fit
     * into.
     */
    private Dimension targetSize;

    /**
     * Status that the Scalable Picture could have
     */
    public enum ScalablePictureStatus {

        /**
         * It can be uninitialized
         */
        SCALABLE_PICTURE_UNINITIALISED,
        /**
         * It could be garbage collecting
         */
        SCALABLE_PICTURE_GARBAGE_COLLECTION,
        /**
         * It could be loading
         */
        SCALABLE_PICTURE_LOADING,
        /**
         * It could have loaded the picture
         */
        SCALABLE_PICTURE_LOADED,
        /**
         * It could be scaling
         */
        SCALABLE_PICTURE_SCALING,
        /**
         * It could be ready
         */
        SCALABLE_PICTURE_READY,
        /**
         * It could be in error state
         */
        SCALABLE_PICTURE_ERROR
    }

    /**
     * the quality with which the JPG pictures shall be written. A value of 0
     * means poor 1 means great.
     */
    public float jpgQuality = 0.8f;

    /**
     * which method to use on scaling, a fast one or a good quality one.
     * Unfortunately the good quality AffineTransformOp converts the image to a
     * colorspace in the output JPEG file which most programs can't read so this
     * flag is currently not considered. RE 7.9.2005
     */
    public boolean fastScale = true;

    /**
     * Variable that indicates how often to scale down the image. Multiple steps
     * (surprisingly) results in better quality!
     */
    private int scaleSteps = 1;

    /**
     * flag that indicates that the image should be scaled after a status
     * message is received from the SourcePicture that the picture was loaded.
     */
    public boolean scaleAfterLoad;  // default is false

    /**
     * Constructor
     */
    public ScalablePicture() {
        setStatus( SCALABLE_PICTURE_UNINITIALISED, Settings.jpoResources.getString( "ScalablePictureUninitialisedStatus" ) );
        setScaleFactor( 1 );
    }

    /**
     * method to invoke with a filename or URL of a picture that is to be loaded
     * and scaled in a new thread. This is handy to update the screen while the
     * loading chuggs along in the background. Make sure you invoked
     * setScaleFactor or setScaleSize before invoking this method.
     *
     * Step 1: Am I already loading what I need somewhere? If yes -&gt; use it.
     * Has it finished loading? If no -&gt; wait for it If yes -&gt; use it Else
     * -&gt; load it
     *
     * @param imageUrl	The URL of the image you want to load
     * @param priority	The Thread priority
     * @param rotation	The rotation 0-360 that the image should be put through
     * after loading.
     */
    public void loadAndScalePictureInThread( URL imageUrl, int priority, double rotation ) {
        this.imageUrl = imageUrl;
        if ( sourcePicture != null ) {
            sourcePicture.removeListener( this );
        }
        sourcePicture = new SourcePicture();
        sourcePicture.addListener( this );
        setStatus( SCALABLE_PICTURE_LOADING, Settings.jpoResources.getString( "ScalablePictureLoadingStatus" ) );
        scaleAfterLoad = true;
        sourcePicture.loadPictureInThread( imageUrl, priority, rotation );
        // when the thread is done it sends a sourceStatusChange message to us
    }

    /**
     * Synchronous method to load the image. It should only be called by
     * something which is a thread itself such as the HtmlDistillerThread. Since
     * this intended for large batch operations this bypasses the cache. There
     * are no status updates
     *
     * @param imageUrl The Url of the image to be loaded
     * @param rotation The angle by which it is to be rotated upon loading.
     */
    public void loadPictureImd( URL imageUrl, double rotation ) {
        if ( sourcePicture != null ) {
            sourcePicture.removeListener( this );
        }
        sourcePicture = new SourcePicture();
        scaleAfterLoad = false;
        sourcePicture.loadPicture( imageUrl, rotation );
    }

    /**
     * stops all picture loading except if the Url we desire is being loaded
     *
     * @param url	The URL of the image which is to be loaded.
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
     * method that is invoked by the SourcePictureListener interface. Usually
     * this will be called by the SourcePicture telling the ScalablePicture that
     * it has completed loading. The ScalablePicture should then change it's own
     * status and tell the ScalableListeners what's up.
     *
     * @param statusCode status code
     * @param statusMessage status message
     * @param sp source picture
     */
    @Override
    public void sourceStatusChange( SourcePictureStatus statusCode, String statusMessage,
            SourcePicture sp ) {
        //logger.info("ScalablePicture.sourceStatusChange: status received from SourceImage: " + statusMessage);

        switch ( statusCode ) {
            case SOURCE_PICTURE_UNINITIALISED:
                // logger.info("ScalablePicture.sourceStatusChange: pictureStatus was: UNINITIALISED message: " + statusMessage );
                setStatus( SCALABLE_PICTURE_UNINITIALISED, statusMessage );
                break;
            case SOURCE_PICTURE_ERROR:
                // logger.info("ScalablePicture.sourceStatusChange: pictureStatus was: ERROR message: " + statusMessage );
                setStatus( SCALABLE_PICTURE_ERROR, statusMessage );
                sourcePicture.removeListener( this );
                break;
            case SOURCE_PICTURE_LOADING:
                // logger.info("ScalablePicture.sourceStatusChange: pictureStatus was: LOADING message: " + statusMessage );
                setStatus( SCALABLE_PICTURE_LOADING, statusMessage );
                break;
            case SOURCE_PICTURE_ROTATING:
                // logger.info("ScalablePicture.sourceStatusChange: pictureStatus was: ROTATING message: " + statusMessage );
                setStatus( SCALABLE_PICTURE_LOADING, statusMessage );
                break;
            case SOURCE_PICTURE_READY:
                // logger.info("ScalablePicture.sourceStatusChange: pictureStatus was: READY message: " + statusMessage );
                setStatus( SCALABLE_PICTURE_LOADED, statusMessage );
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
     * method that creates the scaled image in the background in it's own
     * thread.
     *
     * @param priority The priority this image takes relative to the others.
     */
    @Override
    public void createScaledPictureInThread( int priority ) {
        Thread t = new Thread( "ScalablePicture.createScaledPictureInThread" ) {
            @Override
            public void run() {
                scalePicture();
            }
        };
        t.setPriority( priority );
        t.start();
    }

    /* Taking these variables here to be friendlier on the heap */
    private AffineTransform afStep;

    private AffineTransformOp opStep;

    private Point2D pStep;

    private BufferedImage biStep;

    /**
     * call this method when the affine transform op is to be executed.
     *
     *
     */
    public void scalePicture() {
        LOGGER.fine( "scaling..." );
        try {
            setStatus( SCALABLE_PICTURE_SCALING, "Scaling picture." );

            if ( ( sourcePicture != null ) && ( sourcePicture.getSourceBufferedImage() != null ) ) {
                if ( scaleToSize ) {
                    scaleFactor = calcScaleSourceToTarget( sourcePicture.getWidth(), sourcePicture.getHeight(), targetSize.width, targetSize.height );

                    if ( Settings.dontEnlargeSmallImages && scaleFactor > 1 ) {
                        scaleFactor = 1;
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
                    factor = scaleFactor;
                } else {
                    affineTransformType = AffineTransformOp.TYPE_BICUBIC;
                    factor = Math.pow( scaleFactor, 1f / getScaleSteps() );
                }

                afStep = AffineTransform.getScaleInstance( factor, factor );
                opStep = new AffineTransformOp( afStep, affineTransformType );
                scaledPicture = sourcePicture.getSourceBufferedImage();
                for ( int i = 0; i < getScaleSteps(); i++ ) {
                    pStep = new Point2D.Float( scaledPicture.getWidth(), scaledPicture.getHeight() );
                    pStep = afStep.transform( pStep, null );
                    int x = (int) Math.rint( pStep.getX() );
                    int y = (int) Math.rint( pStep.getY() );
                    int imageType = sourcePicture.getSourceBufferedImage().getType();
                    LOGGER.fine( String.format( "getType from source image returned %d", imageType ) );
                    if ( x == 0 ) {
                        x = 100;
                    }
                    if ( y == 0 ) {
                        y = 100;
                    }
                    if ( ( imageType == 0 ) || ( imageType == 13 ) ) {
                        imageType = BufferedImage.TYPE_3BYTE_BGR;
                        LOGGER.fine( String.format( "Becuase we don't like imageType 0 we are setting the target type to BufferedImage.TYPE_3BYTE_BGR which has code: %d", BufferedImage.TYPE_3BYTE_BGR ) );
                    }
                    biStep = new BufferedImage( x, y, imageType );
                    scaledPicture = opStep.filter( scaledPicture, biStep );
                }
                setStatus( SCALABLE_PICTURE_READY, "Scaled Picture is ready." );
            } else if ( getStatusCode() != SCALABLE_PICTURE_LOADING ) {
                setStatus( SCALABLE_PICTURE_ERROR, "Could not scale image as SourceImage is null." );
            }
        } catch ( OutOfMemoryError e ) {
            LOGGER.log( Level.SEVERE, "Caught an OutOfMemoryError while scaling an image.\n{0}", e.getMessage() );
            setStatus( SCALABLE_PICTURE_ERROR, "Out of Memory Error while scaling " + imageUrl.toString() );
            scaledPicture = null;
            Tools.dealOutOfMemoryError();
        }
    }

    /**
     * Returns the scale factor maintaining aspect ratio to fit the source image
     * into the target dimension..
     *
     * @param sourceWidth the width of the original dimension
     * @param sourceHeight the height of the original dimension
     * @param maxWidth the maximum width of the output dimension
     * @param maxHeight the maximum height of the output dimension
     * @return The scale factor by which to multiply the source dimension
     */
    public static double calcScaleSourceToTarget( int sourceWidth, int sourceHeight, int maxWidth, int maxHeight ) {
        // Scale so that the entire picture fits in the component.
        if ( ( (double) sourceHeight / maxHeight ) > ( (double) sourceWidth / maxWidth ) ) {
            // Vertical scaling
            return ( (double) maxHeight / sourceHeight );
        } else {
            // Horizontal scaling
            return ( (double) maxWidth / sourceWidth );
        }
    }

    /**
     * set the scale factor to the new desired value. The scale factor is a
     * multiplier by which the original picture needs to be multiplied to get
     * the size of the picture on the screen. You must call
     * {@link #createScaledPictureInThread(int)} to make anything happen.<p>
     *
     * Example: Original is 3000 x 2000 --&gt; Scale Factor 0.10 --&gt; Target
     * Picture is 300 x 200
     *
     * @param newFactor new factor
     */
    @Override
    public void setScaleFactor( double newFactor ) {
        scaleToSize = false;
        targetSize = null;
        scaleFactor = newFactor;
    }

    /**
     * invoke this method to tell the scale process to figure out the scale
     * factor so that the image fits either by height or by width into the
     * indicated dimension.
     *
     * @param newSize new size
     */
    @Override
    public void setScaleSize( Dimension newSize ) {
        scaleToSize = true;
        if ( ( newSize.height < 1 ) || ( newSize.width < 1 ) ) {
            // to prevent the affine transform from failing on a 0 size.
            targetSize = new Dimension( 100, 100 );
        } else {
            targetSize = newSize;
        }
    }

    /**
     * return the current scale factor
     *
     * @return the scale factor
     */
    @Override
    public double getScaleFactor() {
        return scaleFactor;
    }

    /**
     * return the current scale size. This is the area that the picture is
     * fitted into. Since the are could be wider or taller than the picture will
     * be scaled to there is a different method <code>getScaledSize</code> that
     * will return the size of the picture.
     *
     * @return the current scale size
     */
    public Dimension getScaleSize() {
        return targetSize;
    }

    /**
     * return the scaled image
     *
     * @return the scaled image
     */
    @Override
    public BufferedImage getScaledPicture() {
        return scaledPicture;
    }

    /**
     * return the scaled image
     *
     * @return the scaled image
     */
    public ImageIcon getScaledImageIcon() {
        return new ImageIcon( scaledPicture );
    }

    /**
     * return the size of the scaled image or Zero if there is none
     *
     * @return the scaled size
     */
    public Dimension getScaledSize() {
        if ( scaledPicture != null ) {
            return new Dimension( scaledPicture.getWidth(), scaledPicture.getHeight() );
        } else {
            return new Dimension( 0, 0 );
        }
    }

    /**
     * return the size of the scaled image as a neatly formatted text or Zero if
     * there is none
     *
     * @return a string of the scaled size
     */
    public String getScaledSizeString() {
        if ( scaledPicture != null ) {
            return scaledPicture.getWidth() + " x " + scaledPicture.getHeight();
        } else {
            return "0 x 0";
        }
    }

    /**
     * return the height of the scaled image or Zero if there is none
     *
     * @return the scaled height or 0
     */
    public int getScaledHeight() {
        if ( scaledPicture != null ) {
            return scaledPicture.getHeight();
        } else {
            return 0;
        }
    }

    /**
     * return the width of the scaled image or Zero if there is none
     *
     * @return the scaled width or 0
     */
    public int getScaledWidth() {
        if ( scaledPicture != null ) {
            return scaledPicture.getWidth();
        } else {
            return 0;
        }
    }

    /**
     * return the image in the original size
     *
     * @return image in the original size
     */
    public BufferedImage getOriginalImage() {
        return sourcePicture.getSourceBufferedImage();
    }

    /**
     * return the image in the original size
     *
     * @return the original picture
     */
    public SourcePicture getSourcePicture() {
        return sourcePicture;
    }

    /**
     * return the size of the original image or Zero if there is none
     *
     * @return The original size
     */
    public Dimension getOriginalSize() {
        return sourcePicture.getSize();
    }

    /**
     * return the height of the original image or Zero if there is none
     *
     * @return the original height of the image
     */
    @Override
    public int getOriginalHeight() {
        return sourcePicture.getHeight();
    }

    /**
     * return the width of the original image or Zero if there is none
     *
     * @return the original width of the image
     */
    @Override
    public int getOriginalWidth() {
        return sourcePicture.getWidth();
    }

    /**
     * return the filename of the original image
     *
     * @return the filename of the original image
     */
    public String getFilename() {
        return imageUrl.toString();
    }

    /**
     * This method allows the ScalablePicture's scaled BufferedImage to be
     * written to the desired file.
     *
     * @param	writeFile	The File that shall receive the jpg data
     */
    public void writeScaledJpg( File writeFile ) {
        writeJpg( writeFile, scaledPicture, jpgQuality );
    }

    /**
     * This method allows the ScalablePicture's scaled BufferedImage to be
     * written to the desired output stream.
     *
     * @param	writeStream	The Stream that shall receive the jpg data
     */
    public void writeScaledJpg( OutputStream writeStream ) {
        writeJpg( writeStream, scaledPicture, jpgQuality );
    }

    /**
     * This static method writes the indicated renderedImage (BufferedImage) to
     * the indicated file.
     *
     * @param	targetFile	The File that shall receive the jpg data
     * @param	renderedImage	The RenderedImage (BufferedImage) to be written
     * @param	jpgQuality	The quality with which to compress to jpg
     */
    public static void writeJpg( File targetFile, RenderedImage renderedImage,
            float jpgQuality ) {
        if (renderedImage == null ) {
            return;
        }
        Iterator writers = ImageIO.getImageWritersByFormatName( "jpg" );
        ImageWriter writer = (ImageWriter) writers.next();
        JPEGImageWriteParam params = new JPEGImageWriteParam( null );
        params.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
        params.setCompressionQuality( jpgQuality );
        params.setProgressiveMode( ImageWriteParam.MODE_DISABLED );
        params.setDestinationType(
                new ImageTypeSpecifier( IndexColorModel.getRGBdefault(),
                        IndexColorModel.getRGBdefault().createCompatibleSampleModel( 16, 16 ) ) );

        try ( ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(
                new FileOutputStream( targetFile ) ) ) {
            writer.setOutput( imageOutputStream );
            writer.write( null, new IIOImage( renderedImage, null, null ), params );
        } catch ( IOException e ) {
            LOGGER.severe( e.getMessage() );
        }
        writer.dispose();
    }

    /**
     * This static method writes the indicated renderedImage (BufferedImage) to
     * the indicated file.
     *
     * @param	writeStream	The File that shall receive the jpg data
     * @param	renderedImage	The RenderedImage (BufferedImage) to be written
     * @param	jpgQuality	The quality with which to compress to jpg
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

        try ( ImageOutputStream ios = ImageIO.createImageOutputStream( writeStream ) ) {
            writer.setOutput( ios );
            writer.write( null, new IIOImage( renderedImage, null, null ), params );
        } catch ( IOException e ) {
            LOGGER.info( "Caught IOException: " + e.getMessage() );
        }
        writer.dispose();
    }

    /**
     * The listeners to notify when the image operation changes the status.
     */
    private final Set<ScalablePictureListener> scalablePictureStatusListeners = Collections.synchronizedSet(new HashSet<>() );

    /**
     * method to register the listening object of the status events
     *
     * @param listener Listener
     */
    public void addStatusListener( ScalablePictureListener listener ) {
        scalablePictureStatusListeners.add( listener );
    }

    /**
     * method to register the listening object of the status events
     *
     * @param listener Listener
     */
    public void removeStatusListener( ScalablePictureListener listener ) {
        scalablePictureStatusListeners.remove( listener );
    }

    /**
     * variable to track the status of the picture
     */
    private ScalablePictureStatus pictureStatusCode;

    /**
     * Method that sets the status of the ScalablePicture object and notifies
     * interested objects of a change in status (not built yet).
     *
     * @param statusCode status code
     * @param statusMessage status message
     */
    private void setStatus( ScalablePictureStatus statusCode, String statusMessage ) {
        pictureStatusCode = statusCode;
        pictureStatusMessage = statusMessage;

        synchronized ( scalablePictureStatusListeners ) {
            scalablePictureStatusListeners.stream().forEach( ( scalablePictureListener ) -> {
                scalablePictureListener.scalableStatusChange( pictureStatusCode, pictureStatusMessage );
            } );
        }
    }

    /**
     * pass on the update on the loading Progress to the listening objects
     *
     * @param statusCode status code
     * @param percentage percentage
     */
    @Override
    public void sourceLoadProgressNotification( SourcePictureStatus statusCode, int percentage ) {
        scalablePictureStatusListeners.stream().forEach( ( scalablePictureListener ) -> {
            scalablePictureListener.sourceLoadProgressNotification( statusCode, percentage );
        } );
    }

    /**
     * Method that returns the status code of the picture loading.
     *
     * @return the status code
     */
    public ScalablePictureStatus getStatusCode() {
        //logger.info(String.format( "Returning status code %d which corresponds to message %s", pictureStatusCode, pictureStatusMessage ));
        return pictureStatusCode;
    }

    /**
     * Method that returns the status message of the picture loading.
     *
     * @return the status message
     */
    public String getStatusMessage() {
        return pictureStatusMessage;
    }

    /**
     * accessor method to set the quality that should be used on jpg write
     * operations.
     *
     * @param quality the quality to use
     */
    public void setJpgQuality( float quality ) {
        //logger.info( "setJpgQuality requested with " + Float.toString( quality ) );
        if ( quality >= 0f && quality <= 1f ) {
            //logger.info( "Quality set." );
            jpgQuality = quality;
        }
    }

    /**
     * sets the picture into fast scaling mode
     */
    public void setFastScale() {
        fastScale = true;
    }

    /**
     * sets the picture into quality scaling mode
     */
    public void setQualityScale() {
        fastScale = false;
    }

    /**
     * The number of steps to use in scaling
     *
     * @return the scaleSteps
     */
    public int getScaleSteps() {
        return scaleSteps;
    }

    /**
     * The number of steps to use in scaling
     *
     * @param scaleSteps the scaleSteps to set
     */
    public void setScaleSteps( int scaleSteps ) {
        this.scaleSteps = scaleSteps;
    }

}
