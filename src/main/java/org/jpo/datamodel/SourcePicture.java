package org.jpo.datamodel;

import org.jetbrains.annotations.Nullable;
import org.jpo.cache.ImageBytes;
import org.jpo.cache.JpoCache;
import org.jpo.gui.SourcePictureListener;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.datamodel.SourcePicture.SourcePictureStatus.*;


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
     * the hash code of the image
     */
    private String sha256;

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

    public long getLoadTime() {
        return loadTime;
    }

    /**
     * the time it took to load the image
     */
    private long loadTime;  //default is 0

    /**
     * reference to the inner class that listens to the image loading progress
     */
    private final MyIIOReadProgressListener myIIOReadProgressListener = new MyIIOReadProgressListener();


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
     * Loads the picture indicated by the file to this SourcePicture on the current thread.
     * If the file doesn't exist the getSourceBufferedImage method will return a null.
     *
     * @param file     The file with the image
     * @param rotation Image rotation
     */
    public void loadPicture(final String sha256, final File file, final double rotation) {
        if (pictureStatusCode == SOURCE_PICTURE_LOADING) {
            stopLoadingExcept(file);
        }
        this.sha256 = sha256;
        this.imageFile = file;
        this.rotation = rotation;
        loadPicture();
    }

    /**
     * method to invoke with a file of a picture that is to be loaded in
     * a new thread. This is handy to update the screen while the loading chugs
     * along in the background.
     *
     * @param imageFile The file with the image to be loaded
     * @param priority  The Thread priority for this thread.
     * @param rotation  The rotation 0-360 to be used on this picture
     */
    public void loadPictureInThread(final String sha256, final File imageFile, final int priority, final double rotation) {
        if (pictureStatusCode == SOURCE_PICTURE_LOADING) {
            stopLoadingExcept(imageFile);
        }
        this.sha256 = sha256;
        this.imageFile = imageFile;
        this.rotation = rotation;
        final var thread = new Thread("SourcePicture.loadPictureInThread") {

            @Override
            public void run() {
                loadPicture();
            }
        };
        thread.setPriority(priority);
        thread.start();
    }

    /**
     * loads a picture from the file  imageFile object into the
     * sourcePictureBufferedImage object and updates the status when done or
     * failed.
     */
    private void loadPicture() {
        setStatus(SOURCE_PICTURE_LOADING, Settings.getJpoResources().getString("ScalablePictureLoadingStatus"));
        final var start = System.currentTimeMillis();
        loadTime = 0;
        ImageBytes imageBytes;
        try {
            LOGGER.log(Level.FINE, "Asking highres cache for image {0}", imageFile);
            imageBytes = JpoCache.getHighresImageBytes(sha256, imageFile);
            LOGGER.log(Level.FINE, "Image loaded from cache: {0} Bytes: {1}", new Object[]{imageBytes.isRetrievedFromCache(), imageBytes.getBytes().length});
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "IOException loading {0}: {1}", new Object[]{imageFile, e.getMessage()});
            setStatus(SOURCE_PICTURE_ERROR, "Error while reading " + imageFile.toString());
            sourcePictureBufferedImage = null;
            return;
        }

        sourcePictureBufferedImage = convertImageBytesToBufferedImage(imageBytes);

        if (!abortFlag) {
            if (rotation != 0) {
                rotateImage();
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

    private void rotateImage() {
        setStatus(SOURCE_PICTURE_ROTATING, "Rotating: " + imageFile.toString());
        final var xRot = sourcePictureBufferedImage.getWidth() / 2;
        final var yRot = sourcePictureBufferedImage.getHeight() / 2;
        final var rotateAf = AffineTransform.getRotateInstance(Math.toRadians(rotation), xRot, yRot);
        var op = new AffineTransformOp(rotateAf, AffineTransformOp.TYPE_BILINEAR);
        var newBounds = op.getBounds2D(sourcePictureBufferedImage);
        // a simple AffineTransform would give negative top left coordinates -->
        // do another transform to get 0,0 as top coordinates again.
        final double minX = newBounds.getMinX();
        final double minY = newBounds.getMinY();

        final var translateAf = AffineTransform.getTranslateInstance(minX * (-1), minY * (-1));
        rotateAf.preConcatenate(translateAf);
        op = new AffineTransformOp(rotateAf, AffineTransformOp.TYPE_BILINEAR);
        newBounds = op.getBounds2D(sourcePictureBufferedImage);

        // this piece of code is so essential!!! Otherwise, the internal image format
        // is totally altered and either the AffineTransformOp decides it doesn't
        // want to rotate the image or web browsers can't read the resulting image.
        final var targetImage = new BufferedImage(
                (int) newBounds.getWidth(),
                (int) newBounds.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR);

        sourcePictureBufferedImage = op.filter(sourcePictureBufferedImage, targetImage);
    }

    @Nullable
    private BufferedImage convertImageBytesToBufferedImage(final ImageBytes imageBytes) {
        // We have the bytes from the image that came from the cache or the disk
        // now create a BufferedImage from that
        try (final var bis = imageBytes.getByteArrayInputStream();
             final var iis = ImageIO.createImageInputStream(bis)) {
            final var reader = org.jpo.datamodel.ImageIO.getImageIOReader(iis);
            if (reader == null) {
                LOGGER.log(Level.SEVERE, "No reader found for URL: {0}", imageFile);
                setStatus(SOURCE_PICTURE_ERROR, String.format("No reader found for URL: %s", imageFile.toString()));
                return null;
            }
            return readFromReaderWithProgressListener(iis, reader);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "IOException while converting {0} bytes to a BufferedImage: {1}", new Object[]{imageBytes.getBytes().length,e.getMessage()});
            setStatus(SOURCE_PICTURE_ERROR, "Error while reading " + imageFile.toString());
            return null;
        }
    }

    @Nullable
    private BufferedImage readFromReaderWithProgressListener(final ImageInputStream iis, final ImageReader reader) throws IOException {
        reader.addIIOReadProgressListener(myIIOReadProgressListener);
        reader.setInput(iis);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = fixTypeOfImage(reader.read(0));
        } catch (final OutOfMemoryError e) {
            LOGGER.log(Level.SEVERE, "Caught an OutOfMemoryError while loading an image: {0}", e.getMessage());
            setStatus(SOURCE_PICTURE_ERROR, Settings.getJpoResources().getString("ScalablePictureErrorStatus"));
            Tools.dealOutOfMemoryError();
        } finally {
            reader.removeIIOReadProgressListener(myIIOReadProgressListener);
            reader.dispose();
        }
        return bufferedImage;
    }

    /**
     * Checks that the supplied BufferedImage is of TYPE_3BYTE_BGR. If it isn't it creates a new image of that type
     * and copies the picture to that type.
     *
     * @param bufferedImage The BufferedImage to potentially modify.
     */
    private BufferedImage fixTypeOfImage(final BufferedImage bufferedImage) {
        if (bufferedImage.getType() == BufferedImage.TYPE_3BYTE_BGR) {
            return bufferedImage;
        } else {
            LOGGER.log(Level.INFO, "Got wrong image type: {0} instead of {1}. Trying to convert...", new Object[]{bufferedImage.getType(), BufferedImage.TYPE_3BYTE_BGR});

            final var bgr3ByteImage = new BufferedImage(bufferedImage.getWidth(),
                    bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            final var g = bgr3ByteImage.createGraphics();
            g.drawImage(bufferedImage, 0, 0, null);
            g.dispose();
            return bgr3ByteImage;
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
     * loaded. Otherwise, it returns false.
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
            LOGGER.log(Level.FINE, "called but pointless since image is not LOADING: {0}", imageFile);
            return false;
        }

        if (!exemptionFile.equals(imageFile)) {
            LOGGER.log(Level.FINE, "called with Url {0} --> stopping loading of {1}", new Object[]{exemptionFile, imageFile});
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
        LOGGER.log(Level.FINE, "Sending status code {0} with message {1} to {2} listeners", new Object[]{statusCode, statusMessage, sourcePictureListeners.size()});
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
            sourcePictureListeners.forEach(sourcePictureListener -> sourcePictureListener.sourceLoadProgressNotification(statusCode, percentage));
        }

        @Override
        public void imageComplete(final ImageReader source) {
            notifySourceLoadProgressListeners(SOURCE_PICTURE_LOADING_COMPLETED, 100);
        }

        @Override
        public void imageProgress(final ImageReader source, final float percentageDone) {
            if (abortFlag) {
                source.abort();
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
                source.abort();
            }
        }

        @Override
        public void thumbnailStarted(final ImageReader source, final int imageIndex,
                                     final int thumbnailIndex) {
            // noop
        }
    }
}
