package jpo.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import jpo.cache.ImageBytes;
import jpo.cache.JpoCache;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import static jpo.gui.SourcePicture.SourcePictureStatus.SOURCE_PICTURE_ERROR;
import static jpo.gui.SourcePicture.SourcePictureStatus.SOURCE_PICTURE_LOADING;
import static jpo.gui.SourcePicture.SourcePictureStatus.SOURCE_PICTURE_LOADING_COMPLETED;
import static jpo.gui.SourcePicture.SourcePictureStatus.SOURCE_PICTURE_LOADING_PROGRESS;
import static jpo.gui.SourcePicture.SourcePictureStatus.SOURCE_PICTURE_LOADING_STARTED;
import static jpo.gui.SourcePicture.SourcePictureStatus.SOURCE_PICTURE_READY;
import static jpo.gui.SourcePicture.SourcePictureStatus.SOURCE_PICTURE_ROTATING;
import static jpo.gui.SourcePicture.SourcePictureStatus.SOURCE_PICTURE_UNINITIALISED;


/*
 SourcePicture.java:  class that can load a picture from a URL

 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
 * This class can load and rotate a digital picture either immediately or in a
 * separate thread from a URL
 */
public class SourcePicture {

    /**
     * the Buffered Image that this class protects and provides features for.
     */
    public BufferedImage sourcePictureBufferedImage;

    /**
     * the URL of the picture
     */
    private URL imageUrl;

    /**
     * States of the source picture
     */
    public enum SourcePictureStatus {
        /**
         * The picture could be uninitialised
         */
        SOURCE_PICTURE_UNINITIALISED,
        /**
         * The picture could be loading
         */
        SOURCE_PICTURE_LOADING,
        /**
         * The picture could be rotating
         */
        SOURCE_PICTURE_ROTATING,
        /**
         * The picture could be ready
         */
        SOURCE_PICTURE_READY,
        /**
         * The picture could be in error
         */
        SOURCE_PICTURE_ERROR,
        /**
         * The picture could have stared loading
         */
        SOURCE_PICTURE_LOADING_STARTED,
        /**
         * The picture could be making progress while loading
         */
        SOURCE_PICTURE_LOADING_PROGRESS,
        /**
         * The picture could have finished loading
         */
        SOURCE_PICTURE_LOADING_COMPLETED
    }

    /**
     * the time it took to load the image
     */
    public long loadTime;  //default is 0

    /**
     * reference to the inner class that listens to the image loading progress
     */
    private final MyIIOReadProgressListener myIIOReadProgressListener = new MyIIOReadProgressListener();

    /**
     * the reader object that will read the image
     */
    private ImageReader reader;

    /**
     * Indicator to tell us if the loading was aborted.
     */
    private boolean abortFlag;  // default is false

    /**
     * Rotation 0-360 that the image is subjected to after loading
     */
    private double rotation;  // default is 0

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( SourcePicture.class.getName() );

    /**
     * method to invoke with a filename or URL of a picture that is to be loaded
     * in the main thread.
     *
     * @param imageUrl
     * @param rotation
     */
    public void loadPicture( URL imageUrl, double rotation ) {
        if ( pictureStatusCode == SOURCE_PICTURE_LOADING ) {
            stopLoadingExcept( imageUrl );
        }
        this.imageUrl = imageUrl;
        this.rotation = rotation;
        loadPicture();
    }

    /**
     * method to invoke with a filename or URL of a picture that is to be loaded
     * a new thread. This is handy to update the screen while the loading chugs
     * along in the background.
     *
     * @param	imageUrl	The URL of the image to be loaded
     * @param	priority	The Thread priority for this thread.
     * @param	rotation	The rotation 0-360 to be used on this picture
     */
    public void loadPictureInThread( URL imageUrl, int priority, double rotation ) {
        if ( pictureStatusCode == SOURCE_PICTURE_LOADING ) {
            stopLoadingExcept( imageUrl );
        }

        this.imageUrl = imageUrl;
        this.rotation = rotation;
        Thread t = new Thread( "SourcePicture.loadPictureInThread" ) {

            @Override
            public void run() {
                loadPicture();
            }
        };
        t.setPriority( priority );
        t.start();
    }

    /**
     * loads a picture from the URL in the imageUrl object into the
     * sourcePictureBufferedImage object and updates the status when done or
     * failed.
     */
    public void loadPicture() {
        setStatus( SOURCE_PICTURE_LOADING, Settings.jpoResources.getString( "ScalablePictureLoadingStatus" ) );
        abortFlag = false;
        long start = System.currentTimeMillis();
        loadTime = 0;

        try {
            ImageBytes imageBytes = JpoCache.getInstance().getHighresImageBytes( imageUrl );
            ByteArrayInputStream bis = imageBytes.getByteArrayInputStream();

            ImageInputStream iis = ImageIO.createImageInputStream( bis );
            LOGGER.fine( "Searching for ImageIO readers..." );
            Iterator readerIterator = ImageIO.getImageReaders( iis );
            while ( readerIterator.hasNext() ) {
                reader = (ImageReader) readerIterator.next();
                LOGGER.fine( String.format( "Found reader: %s", reader.toString() ) );
            }
            Iterator i = ImageIO.getImageReaders( iis );
            if ( !i.hasNext() ) {
                throw new IOException( "No Readers Available!" );
            }
            reader = (ImageReader) i.next();  // grab the first one

            reader.addIIOReadProgressListener( myIIOReadProgressListener );
            reader.setInput( iis );
            sourcePictureBufferedImage = null;
            try {
                sourcePictureBufferedImage = reader.read( 0 ); // just get the first image

                if ( sourcePictureBufferedImage.getType() != BufferedImage.TYPE_3BYTE_BGR ) {
                    LOGGER.fine( String.format( "Got wrong image type: %d instead of %d. Trying to convert...", sourcePictureBufferedImage.getType(), BufferedImage.TYPE_3BYTE_BGR ) );

                    BufferedImage newImage = new BufferedImage( sourcePictureBufferedImage.getWidth(),
                            sourcePictureBufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR );
                    Graphics2D g = newImage.createGraphics();
                    g.drawImage( sourcePictureBufferedImage, 0, 0, null );
                    g.dispose();
                    sourcePictureBufferedImage = newImage;
                }

            } catch ( OutOfMemoryError e ) {
                LOGGER.severe( "SourcePicture caught an OutOfMemoryError while loading an image." );
                iis.close();
                reader.removeIIOReadProgressListener( myIIOReadProgressListener );
                reader.dispose();

                setStatus( SOURCE_PICTURE_ERROR, Settings.jpoResources.getString( "ScalablePictureErrorStatus" ) );
                sourcePictureBufferedImage = null;

                Tools.dealOutOfMemoryError();
                return;
            }
            reader.removeIIOReadProgressListener( myIIOReadProgressListener );
            reader.dispose();
            iis.close();

            if ( !abortFlag ) {
                if ( rotation != 0 ) {
                    setStatus( SOURCE_PICTURE_ROTATING, "Rotating: " + imageUrl.toString() );
                    int xRot = sourcePictureBufferedImage.getWidth() / 2;
                    int yRot = sourcePictureBufferedImage.getHeight() / 2;
                    AffineTransform rotateAf = AffineTransform.getRotateInstance( Math.toRadians( rotation ), xRot, yRot );
                    AffineTransformOp op = new AffineTransformOp( rotateAf, AffineTransformOp.TYPE_BILINEAR );
                    Rectangle2D newBounds = op.getBounds2D( sourcePictureBufferedImage );
                    // a simple AffineTransform would give negative top left coordinates -->
                    // do another transform to get 0,0 as top coordinates again.
                    double minX = newBounds.getMinX();
                    double minY = newBounds.getMinY();

                    AffineTransform translateAf = AffineTransform.getTranslateInstance( minX * ( -1 ), minY * ( -1 ) );
                    rotateAf.preConcatenate( translateAf );
                    op = new AffineTransformOp( rotateAf, AffineTransformOp.TYPE_BILINEAR );
                    newBounds = op.getBounds2D( sourcePictureBufferedImage );

                    // this piece of code is so essential!!! Otherwise the internal image format
                    // is totally altered and either the AffineTransformOp decides it doesn't
                    // want to rotate the image or web browsers can't read the resulting image.
                    BufferedImage targetImage = new BufferedImage(
                            (int) newBounds.getWidth(),
                            (int) newBounds.getHeight(),
                            BufferedImage.TYPE_3BYTE_BGR );

                    sourcePictureBufferedImage = op.filter( sourcePictureBufferedImage, targetImage );
                }

                setStatus( SOURCE_PICTURE_READY, "Loaded: " + imageUrl.toString() );
                long end = System.currentTimeMillis();
                loadTime = end - start;
            } else {
                loadTime = 0;
                setStatus( SOURCE_PICTURE_ERROR, "Aborted!" );
                sourcePictureBufferedImage = null;
            }
        } catch ( IOException e ) {
            setStatus( SOURCE_PICTURE_ERROR, "Error while reading " + imageUrl.toString() );
            sourcePictureBufferedImage = null;
        }

    }

    /**
     * this method can be invoked to flag the current reader to stop at a convenient moment
     */
    public void stopLoading() {
        abortFlag = true;
    }

    /**
     * this method can be invoked to stop the current reader except if it is
     * reading the desired file. It returns true is the desired file is being
     * loaded. Otherwise it returns false.
     *
     * @param exemptionURL The exemption URL
     * @return True if loading in progress, false if not
     */
    public boolean stopLoadingExcept( URL exemptionURL ) {
        if ( ( imageUrl == null ) || ( exemptionURL == null ) ) {
            LOGGER.fine( "exiting on a null parameter. How did this happen?" );
            return false; // has never been used yet
        }

        if ( pictureStatusCode != SOURCE_PICTURE_LOADING ) {
            LOGGER.log( Level.FINE, "called but pointless since image is not LOADING: {0}", imageUrl.toString());
            return false;
        }

        if ( !exemptionURL.equals( imageUrl ) ) {
            LOGGER.log( Level.FINE, "called with Url {0} --> stopping loading of {1}", new Object[]{ exemptionURL.toString(), imageUrl.toString() });
            stopLoading();
            return true;
        } else {
            return false;
        }
    }


    /**
     * return the size of the image or Zero if there is none
     *
     * @return the Dimension of the sourceBufferedImage
     */
    public Dimension getSize() {
        if ( sourcePictureBufferedImage != null ) {
            return new Dimension( sourcePictureBufferedImage.getWidth(), sourcePictureBufferedImage.getHeight() );
        } else {
            return new Dimension( 0, 0 );
        }

    }

    /**
     * return the height of the image or Zero if there is none
     *
     * @return the height of the image
     */
    public int getHeight() {
        if ( sourcePictureBufferedImage != null ) {
            return sourcePictureBufferedImage.getHeight();
        } else {
            return 0;
        }
    }

    /**
     * return the width of the image or Zero if there is none
     *
     * @return the width of the image
     */
    public int getWidth() {
        if ( sourcePictureBufferedImage != null ) {
            return sourcePictureBufferedImage.getWidth();
        } else {
            return 0;
        }
    }

    /**
     * return the URL of the original image as a string
     *
     * @return the url of the name
     */
    public String getUrlString() {
        return imageUrl.toString();
    }

    /**
     * return the URL of the original image
     *
     * @return the url of the name
     */
    public URL getUrl() {
        return imageUrl;
    }

    /**
     * return the rotation of the image
     *
     * @return the rotation angle
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * The listeners to notify about changes on this SourcePicture.
     */
    private final Set<SourcePictureListener> sourcePictureListeners = Collections.synchronizedSet( new HashSet<SourcePictureListener>() );

    /**
     * Adds a listener
     *
     * @param listener
     */
    public void addListener( SourcePictureListener listener ) {
        sourcePictureListeners.add( listener );
    }

    /**
     * method to register the listening object of the status events
     *
     * @param listener the listener to remove
     */
    public void removeListener( SourcePictureListener listener ) {
        sourcePictureListeners.remove( listener );
    }

    /**
     * This variable track the status of the picture. It can be queried many
     * times or listeners can be attached to wait for a sourceStatusChange
     * event.
     */
    private SourcePictureStatus pictureStatusCode = SOURCE_PICTURE_UNINITIALISED;

    /**
     * This variable track the status message of the picture. It can be queried
     * many times or listeners can be attached to wait for a sourceStatusChange
     * event.
     */
    private String pictureStatusMessage = "Uninitialised SourcePicture object";

    /**
     * Method that sets the status of the ScalablePicture object and notifies
     * interested objects of a change in status (not built yet).
     */
    private void setStatus( SourcePictureStatus statusCode, String statusMessage ) {
        LOGGER.fine( String.format( "Sending status code %s with message %s to %d listeners", statusCode, statusMessage, sourcePictureListeners.size() ) );
        pictureStatusCode = statusCode;
        pictureStatusMessage = statusMessage;
        synchronized ( sourcePictureListeners ) {
            for ( SourcePictureListener sourcePictureListener : sourcePictureListeners ) {
                sourcePictureListener.sourceStatusChange( statusCode, statusMessage, this );
            }
        }
    }

    /**
     * Method that returns the status code of the picture loading.
     *
     * @return the status value
     */
    public SourcePictureStatus getStatusCode() {
        return pictureStatusCode;
    }

    /**
     * Method that returns the status code of the picture loading.
     *
     * @return the message of the status
     */
    public String getStatusMessage() {
        return pictureStatusMessage;
    }

    /**
     * variable that records how much has been loaded
     */
    private int percentLoaded;  // default is 0

    /**
     * Returns how much of the image has been loaded
     *
     * @return the percentage loaded
     */
    public int getPercentLoaded() {
        return percentLoaded;
    }

    /**
     * returns the buffered image that was loaded or null if there is no image.
     *
     * @return	the <code>BufferedImage</code> that was loaded or null if there
     * is no image.
     */
    public BufferedImage getSourceBufferedImage() {
        return sourcePictureBufferedImage;
    }

    /**
     * Special class that allows to catch notifications about how the image
     * reading is getting along
     */
    private class MyIIOReadProgressListener
            implements IIOReadProgressListener {

        private void notifySourceLoadProgressListeners( SourcePictureStatus statusCode,
                int percentage ) {
            percentLoaded = percentage;
            for ( SourcePictureListener sourcePictureListener : sourcePictureListeners ) {
                sourcePictureListener.sourceLoadProgressNotification( statusCode, percentage );
            }
        }

        @Override
        public void imageComplete( ImageReader source ) {
            notifySourceLoadProgressListeners( SOURCE_PICTURE_LOADING_COMPLETED, 100 );
        }

        @Override
        public void imageProgress( ImageReader source, float percentageDone ) {
            if ( abortFlag ) {
                reader.abort();
            }
            notifySourceLoadProgressListeners( SOURCE_PICTURE_LOADING_PROGRESS, (  Float.valueOf( percentageDone ) ).intValue() );
        }

        @Override
        public void imageStarted( ImageReader source, int imageIndex ) {
            notifySourceLoadProgressListeners( SOURCE_PICTURE_LOADING_STARTED, 0 );
        }

        @Override
        public void readAborted( ImageReader source ) {
        }

        @Override
        public void sequenceComplete( ImageReader source ) {
        }

        @Override
        public void sequenceStarted( ImageReader source, int minIndex ) {
        }

        @Override
        public void thumbnailComplete( ImageReader source ) {
        }

        @Override
        public void thumbnailProgress( ImageReader source, float percentageDone ) {
            if ( abortFlag ) {
                reader.abort();
            }
        }

        @Override
        public void thumbnailStarted( ImageReader source, int imageIndex,
                int thumbnailIndex ) {
        }
    }
}
