package org.jpo.gui;

import org.jetbrains.annotations.TestOnly;
import org.jpo.cache.ImageBytes;
import org.jpo.cache.JpoCache;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.gui.SourcePicture.SourcePictureStatus.*;


/*
 SourcePicture.java:  class that can load a picture from a URL

 Copyright (C) 2002 - 2020  Richard Eigenmann.
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
    private BufferedImage sourcePictureBufferedImage;

    /**
     * the File of the picture
     */
    private File imageFile;


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
    private static final Logger LOGGER = Logger.getLogger(SourcePicture.class.getName());

    /**
     * method to invoke with a filename or URL of a picture that is to be loaded
     * in the main thread.
     *
     * @param file Image URL
     * @param rotation Image rotation
     */
    public void loadPicture(final File file, final double rotation) {
        if (pictureStatusCode == SOURCE_PICTURE_LOADING) {
            stopLoadingExcept(file);
        }
        this.imageFile = file;
        this.rotation = rotation;
        loadPicture();
    }

    /**
     * method to invoke with a filename or URL of a picture that is to be loaded
     * a new thread. This is handy to update the screen while the loading chugs
     * along in the background.
     *
     * @param imageFile The URL of the image to be loaded
     * @param priority The Thread priority for this thread.
     * @param rotation The rotation 0-360 to be used on this picture
     */
    public void loadPictureInThread(final File imageFile, final int priority, final double rotation) {
        if (pictureStatusCode == SOURCE_PICTURE_LOADING) {
            stopLoadingExcept(imageFile);
        }

        this.imageFile = imageFile;
        this.rotation = rotation;
        final Thread t = new Thread("SourcePicture.loadPictureInThread") {

            @Override
            public void run() {
                loadPicture();
            }
        };
        t.setPriority(priority);
        t.start();
    }

    /**
     * loads a picture from the URL in the imageUrl object into the
     * sourcePictureBufferedImage object and updates the status when done or
     * failed.
     */
    private void loadPicture() {
        setStatus(SOURCE_PICTURE_LOADING, Settings.jpoResources.getString("ScalablePictureLoadingStatus"));
        final long start = System.currentTimeMillis();
        loadTime = 0;
        ImageBytes imageBytes;
        try {
            imageBytes = JpoCache.getInstance().getHighresImageBytes(imageFile);
        } catch (final IOException e) {
            setStatus(SOURCE_PICTURE_ERROR, "Error while reading " + imageFile.toString());
            sourcePictureBufferedImage = null;
            return;
        }
        try (final ByteArrayInputStream bis = imageBytes.getByteArrayInputStream(); final ImageInputStream iis = ImageIO.createImageInputStream(bis)) {
            reader = getImageIOReader(iis);
            if (reader == null) {
                LOGGER.severe(String.format("No reader found for URL: %s", imageFile.toString()));
                setStatus(SOURCE_PICTURE_ERROR, String.format("No reader found for URL: %s", imageFile.toString()));
                sourcePictureBufferedImage = null;
                return;
            }

            reader.addIIOReadProgressListener(myIIOReadProgressListener);
            reader.setInput(iis);
            sourcePictureBufferedImage = null;
            try {
                sourcePictureBufferedImage = reader.read(0);

                if (sourcePictureBufferedImage.getType() != BufferedImage.TYPE_3BYTE_BGR) {
                    LOGGER.fine(String.format("Got wrong image type: %d instead of %d. Trying to convert...", sourcePictureBufferedImage.getType(), BufferedImage.TYPE_3BYTE_BGR));

                    final BufferedImage newImage = new BufferedImage(sourcePictureBufferedImage.getWidth(),
                            sourcePictureBufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                    final Graphics2D g = newImage.createGraphics();
                    g.drawImage(sourcePictureBufferedImage, 0, 0, null);
                    g.dispose();
                    sourcePictureBufferedImage = newImage;
                }

            } catch (final OutOfMemoryError e) {
                LOGGER.severe("Caught an OutOfMemoryError while loading an image: " + e.getMessage());
                iis.close();
                reader.removeIIOReadProgressListener(myIIOReadProgressListener);
                reader.dispose();

                setStatus(SOURCE_PICTURE_ERROR, Settings.jpoResources.getString("ScalablePictureErrorStatus"));
                sourcePictureBufferedImage = null;

                Tools.dealOutOfMemoryError();
                return;
            }
            reader.removeIIOReadProgressListener(myIIOReadProgressListener);
            reader.dispose();
        } catch (final IOException e) {
            setStatus(SOURCE_PICTURE_ERROR, "Error while reading " + imageFile.toString());
            sourcePictureBufferedImage = null;
        }

        if (!abortFlag) {
            if (rotation != 0) {
                setStatus(SOURCE_PICTURE_ROTATING, "Rotating: " + imageFile.toString());
                final int xRot = sourcePictureBufferedImage.getWidth() / 2;
                final int yRot = sourcePictureBufferedImage.getHeight() / 2;
                final AffineTransform rotateAf = AffineTransform.getRotateInstance(Math.toRadians(rotation), xRot, yRot);
                AffineTransformOp op = new AffineTransformOp(rotateAf, AffineTransformOp.TYPE_BILINEAR);
                Rectangle2D newBounds = op.getBounds2D(sourcePictureBufferedImage);
                // a simple AffineTransform would give negative top left coordinates -->
                // do another transform to get 0,0 as top coordinates again.
                final double minX = newBounds.getMinX();
                final double minY = newBounds.getMinY();

                final AffineTransform translateAf = AffineTransform.getTranslateInstance(minX * (-1), minY * (-1));
                rotateAf.preConcatenate(translateAf);
                op = new AffineTransformOp(rotateAf, AffineTransformOp.TYPE_BILINEAR);
                newBounds = op.getBounds2D(sourcePictureBufferedImage);

                // this piece of code is so essential!!! Otherwise the internal image format
                // is totally altered and either the AffineTransformOp decides it doesn't
                // want to rotate the image or web browsers can't read the resulting image.
                final BufferedImage targetImage = new BufferedImage(
                        (int) newBounds.getWidth(),
                        (int) newBounds.getHeight(),
                        BufferedImage.TYPE_3BYTE_BGR);

                sourcePictureBufferedImage = op.filter(sourcePictureBufferedImage, targetImage);
            }

            setStatus(SOURCE_PICTURE_READY, "Loaded: " + imageFile.toString());
            long end = System.currentTimeMillis();
            loadTime = end - start;
        } else {
            loadTime = 0;
            setStatus(SOURCE_PICTURE_ERROR, "Aborted!");
            sourcePictureBufferedImage = null;
        }
    }

    /**
     * This method checks whether the JVM has an image reader for the supplied
     * File.
     *
     * @param file The file to be checked
     * @return true if the JVM has a reader false if not.
     */
    public static boolean jvmHasReader(final File file) {
        try (final FileImageInputStream testStream = new FileImageInputStream(file)) {
            return ImageIO.getImageReaders(testStream).hasNext();
        } catch (final IOException x) {
            LOGGER.log(Level.INFO, x.getLocalizedMessage());
            return false;
        }
    }


    @TestOnly
    public static ImageReader getImageIOReader(final ImageInputStream iis) {
        final Iterator<ImageReader> readerIterator = ImageIO.getImageReaders(iis);
        return readerIterator.next();
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
     * @param exemptionFile The exemption URL
     * @return True if loading in progress, false if not
     */
    public boolean stopLoadingExcept(final File exemptionFile) {
        if ((imageFile == null) || (exemptionFile == null)) {
            LOGGER.fine("exiting on a null parameter. How did this happen?");
            return false; // has never been used yet
        }

        if (pictureStatusCode != SOURCE_PICTURE_LOADING) {
            LOGGER.log(Level.FINE, "called but pointless since image is not LOADING: {0}", imageFile.toString());
            return false;
        }

        if (!exemptionFile.equals(imageFile)) {
            LOGGER.log(Level.FINE, "called with Url {0} --> stopping loading of {1}", new Object[]{exemptionFile.toString(), imageFile.toString()});
            stopLoading();
            return true;
        } else {
            return false;
        }
    }


    /**
     * return the size of the image or Dimension(0,0) if there is none
     *
     * @return the Dimension of the sourceBufferedImage
     */
    public Dimension getSize() {
        return new Dimension(getWidth(), getHeight());

    }

    /**
     * return the height of the image or Zero if there is none
     *
     * @return the height of the image
     */
    public int getHeight() {
        if (sourcePictureBufferedImage != null) {
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
        if (sourcePictureBufferedImage != null) {
            return sourcePictureBufferedImage.getWidth();
        } else {
            return 0;
        }
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
    private final Set<SourcePictureListener> sourcePictureListeners = Collections.synchronizedSet(new HashSet<>());

    /**
     * Adds a listener
     *
     * @param listener Listener
     */
    public void addListener(SourcePictureListener listener) {
        sourcePictureListeners.add(listener);
    }

    /**
     * method to register the listening object of the status events
     *
     * @param listener the listener to remove
     */
    public void removeListener(SourcePictureListener listener) {
        sourcePictureListeners.remove(listener);
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
     *
     * @param statusCode    status code
     * @param statusMessage status message
     */
    private void setStatus(final SourcePictureStatus statusCode, final String statusMessage) {
        LOGGER.fine(String.format("Sending status code %s with message %s to %d listeners", statusCode, statusMessage, sourcePictureListeners.size()));
        pictureStatusCode = statusCode;
        pictureStatusMessage = statusMessage;
        synchronized (sourcePictureListeners) {
            sourcePictureListeners.forEach(sourcePictureListener ->
                    sourcePictureListener.sourceStatusChange(statusCode, statusMessage, this)
            );
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
     * @return the <code>BufferedImage</code> that was loaded or null if there
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

        private void notifySourceLoadProgressListeners(final SourcePictureStatus statusCode,
                                                       final int percentage) {
            percentLoaded = percentage;
            sourcePictureListeners.forEach((sourcePictureListener) -> sourcePictureListener.sourceLoadProgressNotification(statusCode, percentage));
        }

        @Override
        public void imageComplete(final ImageReader source) {
            notifySourceLoadProgressListeners(SOURCE_PICTURE_LOADING_COMPLETED, 100);
        }

        @Override
        public void imageProgress(final ImageReader source, final float percentageDone) {
            if (abortFlag) {
                reader.abort();
            }
            notifySourceLoadProgressListeners(SOURCE_PICTURE_LOADING_PROGRESS, (Float.valueOf(percentageDone)).intValue());
        }

        @Override
        public void imageStarted(final ImageReader source, final int imageIndex) {
            notifySourceLoadProgressListeners(SOURCE_PICTURE_LOADING_STARTED, 0);
        }

        @Override
        public void readAborted(final ImageReader source) {
            // noop
        }

        @Override
        public void sequenceComplete(final ImageReader source) {
            // noop
        }

        @Override
        public void sequenceStarted(final ImageReader source, final int minIndex) {
            // noop
        }

        @Override
        public void thumbnailComplete(final ImageReader source) {
            // noop
        }

        @Override
        public void thumbnailProgress(final ImageReader source, final float percentageDone) {
            if (abortFlag) {
                reader.abort();
            }
        }

        @Override
        public void thumbnailStarted(final ImageReader source, final int imageIndex,
                                     final int thumbnailIndex) {
            // noop
        }
    }
}
