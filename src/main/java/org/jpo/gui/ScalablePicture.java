package org.jpo.gui;

import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;
import org.jpo.gui.SourcePicture.SourcePictureStatus;
import org.jpo.gui.swing.PictureControllerImage;

import javax.imageio.*;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.gui.ScalablePicture.ScalablePictureStatus.*;

/*
 Copyright (C) 2002 - 2023 Richard Eigenmann.
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
    private SourcePicture sourcePicture;

    /**
     * The scaled version of the image
     */
    private BufferedImage scaledPicture;

    /**
     * The scaling factor
     */
    private double scaleFactor;

    /**
     * the File of the picture
     */
    private File imageFile;

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
    private float jpgQuality = 0.8f;

    /**
     * which method to use on scaling, a fast one or a good quality one.
     * Unfortunately the good quality AffineTransformOp converts the image to a
     * colorspace in the output JPEG file which most programs can't read so this
     * flag is currently not considered. RE 7.9.2005
     */
    private boolean fastScale = true;

    /**
     * Variable that indicates how often to scale down the image. Multiple steps
     * (surprisingly) results in better quality!
     */
    private int scaleSteps = 1;

    /**
     * flag that indicates that the image should be scaled after a status
     * message is received from the SourcePicture that the picture was loaded.
     */
    private boolean scaleAfterLoad;  // default is false

    /**
     * Constructor
     */
    public ScalablePicture() {
        setStatus(SCALABLE_PICTURE_UNINITIALISED, Settings.getJpoResources().getString("ScalablePictureUninitialisedStatus"));
        setScaleFactor(1);
    }

    /**
     * method to invoke with a filename or URL of a picture that is to be loaded
     * and scaled in a new thread. This is handy to update the screen while the
     * loading chugs along in the background. Make sure you invoked
     * setScaleFactor or setScaleSize before invoking this method.
     * Step 1: Am I already loading what I need somewhere? If yes -&gt; use it.
     * Has it finished loading? If no -&gt; wait for it If yes -&gt; use it Else
     * -&gt; load it
     * @param file	The URL of the image you want to load
     * @param priority	The Thread priority
     * @param rotation	The rotation 0-360 that the image should be put through
     * after loading.
     */
    public void loadAndScalePictureInThread( final String sha256, final File file, final int priority, final double rotation ) {
        this.imageFile = file;
        if ( sourcePicture != null ) {
            sourcePicture.removeListener( this );
        }
        sourcePicture = new SourcePicture();
        sourcePicture.addListener(this);
        setStatus(SCALABLE_PICTURE_LOADING, Settings.getJpoResources().getString("ScalablePictureLoadingStatus"));
        scaleAfterLoad = true;
        sourcePicture.loadPictureInThread( sha256, file, priority, rotation );
        // when the thread is done it sends a sourceStatusChange message to us
    }

    /**
     * Loads the image on the current thread. Doesn't send status updates.
     * @param imageFile The image File to be loaded
     * @param rotation  The angle by which it is to be rotated upon loading.
     */
    public void loadPictureImd( final String sha256, final File imageFile, final double rotation ) {
        if ( sourcePicture != null ) {
            sourcePicture.removeListener( this );
        }
        LOGGER.log(Level.FINE, "About to load image: {0}", imageFile);
        sourcePicture = new SourcePicture();
        scaleAfterLoad = false;
        sourcePicture.loadPicture(sha256, imageFile, rotation);
        LOGGER.log(Level.FINE, "Finished loading image: {0}", imageFile);
    }

    /**
     * stops all picture loading except if the Url we desire is being loaded
     *
     * @param file	The URL of the image which is to be loaded.
     */
    public void stopLoadingExcept( final File file ) {
        if ( sourcePicture != null ) {
            boolean isCurrentlyLoading = sourcePicture.stopLoadingExcept( file );
            if ( !isCurrentlyLoading ) {
                sourcePicture.removeListener( this );
            }
        }
    }

    /**
     * method that is invoked by the SourcePictureListener interface. Usually
     * this will be called by the SourcePicture telling the ScalablePicture that
     * it has completed loading. The ScalablePicture should then change its own
     * status and tell the ScalableListeners what's up.
     *
     * @param statusCode status code
     * @param statusMessage status message
     * @param sp source picture
     */
    @Override
    public void sourceStatusChange( final SourcePictureStatus statusCode, final String statusMessage,
            final SourcePicture sp ) {

        switch (statusCode) {
            case SOURCE_PICTURE_UNINITIALISED -> setStatus(SCALABLE_PICTURE_UNINITIALISED, statusMessage);
            case SOURCE_PICTURE_ERROR -> {
                setStatus(SCALABLE_PICTURE_ERROR, statusMessage);
                sourcePicture.removeListener(this);
            }
            case SOURCE_PICTURE_LOADING, SOURCE_PICTURE_ROTATING -> setStatus(SCALABLE_PICTURE_LOADING, statusMessage);
            case SOURCE_PICTURE_READY -> {
                setStatus(SCALABLE_PICTURE_LOADED, statusMessage);
                sourcePicture.removeListener(this);
                if (scaleAfterLoad) {
                    createScaledPictureInThread(Thread.MAX_PRIORITY);
                    scaleAfterLoad = false;
                }
            }
            default -> {
            }
        }
    }

    /**
     * method that creates the scaled image in the background in its own
     * thread.
     *
     * @param priority The priority this image takes relative to the others.
     */
    @Override
    public void createScaledPictureInThread( final int priority ) {
        final Thread t = new Thread( "ScalablePicture.createScaledPictureInThread" ) {
            @Override
            public void run() {
                scalePicture();
            }
        };
        t.setPriority( priority );
        t.start();
    }

    /**
     * call this method when the affine transform op is to be executed.
     */
    public void scalePicture() {
        LOGGER.fine( "scaling..." );
        try {
            setStatus( SCALABLE_PICTURE_SCALING, "Scaling picture." );

            if ( ( sourcePicture != null ) && ( sourcePicture.getSourceBufferedImage() != null ) ) {
                if ( scaleToSize ) {
                    scaleFactor = calcScaleSourceToTarget( sourcePicture.getWidth(), sourcePicture.getHeight(), targetSize.width, targetSize.height );

                    if ( Settings.isDontEnlargeSmallImages() && scaleFactor > 1 ) {
                        scaleFactor = 1;
                    }
                }

                scaledPicture = scaleIt(sourcePicture.getSourceBufferedImage());
                setStatus(SCALABLE_PICTURE_READY, "Scaled Picture is ready.");
            } else if (getStatusCode() != SCALABLE_PICTURE_LOADING) {
                setStatus(SCALABLE_PICTURE_ERROR, "Could not scale image as SourceImage is null.");
            }
        } catch (final OutOfMemoryError e) {
            LOGGER.log(Level.SEVERE, "Caught an OutOfMemoryError while scaling an image.\n{0}", e.getMessage());
            setStatus(SCALABLE_PICTURE_ERROR, "Out of Memory Error while scaling " + imageFile.toString());
            scaledPicture = null;
            Tools.dealOutOfMemoryError();
        }
    }

    private BufferedImage scaleIt(final BufferedImage sourcePicture) {
        BufferedImage resultImage = sourcePicture;
        double factor = getFactor();
        final AffineTransform afStep = AffineTransform.getScaleInstance(factor, factor);
        final AffineTransformOp opStep = new AffineTransformOp(afStep, getAffineTransformOp());
        for (int i = 0; i < getScaleSteps(); i++) {
            Point2D pStep = new Point2D.Float(resultImage.getWidth(), resultImage.getHeight());
            pStep = afStep.transform(pStep, null);
            final Dimension size = new Dimension((int) Math.rint(pStep.getX()), (int) Math.rint(pStep.getY()));
            int imageType = sourcePicture.getType();
            LOGGER.log(Level.FINE, "getType from source image returned {0}", imageType);
            if ((imageType == 0) || (imageType == 13)) {
                imageType = BufferedImage.TYPE_3BYTE_BGR;
                LOGGER.log(Level.FINE, "Because we don''t like imageType 0 we are setting the target type to BufferedImage.TYPE_3BYTE_BGR which has code: {0}", BufferedImage.TYPE_3BYTE_BGR);
            }
            ensureMinimumSize(size);
            BufferedImage biStep = new BufferedImage(size.width, size.height, imageType);
            resultImage = opStep.filter(resultImage, biStep);
        }
        return resultImage;
    }

    private void ensureMinimumSize(Dimension size) {
        if (size.width == 0) {
            size.width = 100;
        }
        if (size.height == 0) {
            size.height = 100;
        }
    }

    private int getAffineTransformOp() {
                 /* note that I have tried to use other AffineTransformOps such as TYPE_BILINEAR and
                 TYPE_BICUBIC. Only they don't work as they muck about with the color channels,
                 and we end up with a non JFIF compliant JPEG image. This doesn't display well
                 in most programs which makes this format useless. This is thoroughly explained
                 in the following article. The workaround doesn't work though.
                 http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4503132
                 RE, 7.9.2005  */

        return fastScale ? AffineTransformOp.TYPE_NEAREST_NEIGHBOR : AffineTransformOp.TYPE_BICUBIC;
    }

    private double getFactor() {
        return fastScale ? scaleFactor : Math.pow(scaleFactor, 1f / getScaleSteps());
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
    public static double calcScaleSourceToTarget( final int sourceWidth, final int sourceHeight, final int maxWidth, final int maxHeight ) {
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
    public void setScaleFactor( final double newFactor ) {
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
    public void setScaleSize( final Dimension newSize ) {
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
     * fitted into. Since the area could be wider or taller than the picture will
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
        return imageFile.toString();
    }

    /**
     * This method allows the ScalablePicture's scaled BufferedImage to be
     * written to the desired file.
     *
     * @param	writeFile	The File that shall receive the jpg data
     */
    public void writeScaledJpg( final File writeFile ) {
        writeJpg( writeFile, scaledPicture, jpgQuality );
    }

    /**
     * This method allows the ScalablePicture's scaled BufferedImage to be
     * written to the desired output stream.
     *
     * @param	writeStream	The Stream that shall receive the jpg data
     */
    public void writeScaledJpg( final OutputStream writeStream ) {
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
    public static void writeJpg( final File targetFile, RenderedImage renderedImage,
            float jpgQuality ) {
        if (renderedImage == null ) {
            return;
        }
        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName( "jpg" );
        final ImageWriter writer = writers.next();
        final JPEGImageWriteParam params = new JPEGImageWriteParam( null );
        params.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
        params.setCompressionQuality( jpgQuality );
        params.setProgressiveMode( ImageWriteParam.MODE_DISABLED );
        params.setDestinationType(
                new ImageTypeSpecifier( ColorModel.getRGBdefault(),
                        ColorModel.getRGBdefault().createCompatibleSampleModel( 16, 16 ) ) );

        try ( final ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(
                new FileOutputStream( targetFile ) ) ) {
            writer.setOutput( imageOutputStream );
            writer.write( null, new IIOImage( renderedImage, null, null ), params );
        } catch ( final IOException e ) {
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
    public static void writeJpg( final OutputStream writeStream,
            final RenderedImage renderedImage, final float jpgQuality ) {
        final Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName( "jpg" );
        final ImageWriter writer = writers.next();
        final JPEGImageWriteParam params = new JPEGImageWriteParam( null );
        params.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
        params.setCompressionQuality( jpgQuality );
        params.setProgressiveMode( ImageWriteParam.MODE_DISABLED );
        params.setDestinationType( new ImageTypeSpecifier( ColorModel.getRGBdefault(),
                ColorModel.getRGBdefault().createCompatibleSampleModel( 16, 16 ) ) );

        try ( final ImageOutputStream ios = ImageIO.createImageOutputStream( writeStream ) ) {
            writer.setOutput( ios );
            writer.write( null, new IIOImage( renderedImage, null, null ), params );
        } catch ( final IOException e ) {
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
    public void addStatusListener( final ScalablePictureListener listener ) {
        scalablePictureStatusListeners.add( listener );
    }

    /**
     * method to register the listening object of the status events
     *
     * @param listener Listener
     */
    public void removeStatusListener( final ScalablePictureListener listener ) {
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
    private void setStatus( final ScalablePictureStatus statusCode, String statusMessage ) {
        pictureStatusCode = statusCode;
        pictureStatusMessage = statusMessage;

        synchronized ( scalablePictureStatusListeners ) {
            scalablePictureStatusListeners.forEach(scalablePictureListener -> scalablePictureListener.scalableStatusChange(pictureStatusCode, pictureStatusMessage));
        }
    }

    /**
     * pass on the update on the loading Progress to the listening objects
     *
     * @param statusCode status code
     * @param percentage percentage
     */
    @Override
    public void sourceLoadProgressNotification( final SourcePictureStatus statusCode, final int percentage ) {
        scalablePictureStatusListeners.forEach(scalablePictureListener -> scalablePictureListener.sourceLoadProgressNotification(statusCode, percentage));
    }

    /**
     * Method that returns the status code of the picture loading.
     *
     * @return the status code
     */
    public ScalablePictureStatus getStatusCode() {
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
    public void setJpgQuality( final float quality ) {
        if ( quality >= 0f && quality <= 1f ) {
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
    public void setScaleSteps( final int scaleSteps ) {
        this.scaleSteps = scaleSteps;
    }

}
