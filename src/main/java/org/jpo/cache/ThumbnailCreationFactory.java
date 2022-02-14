package org.jpo.cache;

import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.MimeTypes;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import javax.swing.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 ThumbnailCreationFactory.java:  A factory that creates thumbnails

 Copyright (C) 2002 - 2022  Richard Eigenmann.
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
 * Implementations of this class should run on a thread which polls the
 * {@link ThumbnailCreationQueue} for new {@link ThumbnailQueueRequest} and
 * process them.
 */
public class ThumbnailCreationFactory implements Runnable {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ThumbnailCreationFactory.class.getName() );

    /**
     * An icon that indicates a broken image used when there is a problem
     * rendering the correct thumbnail.
     */
    private static final ImageIcon BROKEN_THUMBNAIL_PICTURE;

    static {
        final var BROKEN_THUMBNAIL_PICTURE_FILE = "broken_thumbnail.gif";
        final var resource = ThumbnailCreationFactory.class.getClassLoader().getResource(BROKEN_THUMBNAIL_PICTURE_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, "Classloader could not find the file: {}", BROKEN_THUMBNAIL_PICTURE_FILE);
            BROKEN_THUMBNAIL_PICTURE = null;
        } else {
            BROKEN_THUMBNAIL_PICTURE = new ImageIcon(resource);
        }
    }

    /**
     * An icon that indicates a broken image used when there is a problem
     * rendering the correct thumbnail.
     */
    private static final ImageIcon MOVIE_ICON;

    static {
        final var MOVIE_ICON_FILE = "icon_movie.png";
        final var resource = ThumbnailCreationFactory.class.getClassLoader().getResource(MOVIE_ICON_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, "Classloader could not find the file: {}", MOVIE_ICON_FILE);
            MOVIE_ICON = null;
        } else {
            MOVIE_ICON = new ImageIcon(resource);
        }
    }

    /**
     * An icon that indicates a broken image used when there is a problem
     * rendering the correct thumbnail.
     */
    private static final ImageIcon DOCUMENT_ICON;

    static {
        final var DOCUMENT_ICON_FILE = "icon_document.png";
        final var resource = ThumbnailCreationFactory.class.getClassLoader().getResource(DOCUMENT_ICON_FILE);
        if (resource == null) {
            LOGGER.log(Level.SEVERE, "Classloader could not find the file: {}", DOCUMENT_ICON_FILE);
            DOCUMENT_ICON = null;
        } else {
            DOCUMENT_ICON = new ImageIcon(resource);
        }
    }


    /**
     * Flag to indicate that the thread should die.
     */
    private boolean endThread;  // default is false

    /**
     * The polling interval in milliseconds
     */
    protected final int pollingInterval;

    /**
     * Constructor that creates the thread. It creates the thread with a
     * Thread.MIN_PRIORITY priority to ensure good overall response.
     *
     * @param pollingInterval The polling interval in milliseconds
     */
    public ThumbnailCreationFactory(final int pollingInterval) {
        this.pollingInterval = pollingInterval;
        final var thread = new Thread(this, "ThumbnailCreationFactory");
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /**
     * The run method for the thread that keeps checking whether there are any
     * {@link ThumbnailQueueRequest} objects on the queue to be rendered.
     */
    @Override
    public void run() {
        while ( !endThread ) {
            final var request = ThumbnailCreationQueue.QUEUE.poll();
            if ( request == null ) {
                try {
                    Thread.sleep(pollingInterval);
                } catch (final InterruptedException x) {
                    // Restore interrupted state
                    Thread.currentThread().interrupt();
                }
            } else {
                processQueueRequest( request );
            }
        }
    }

    /**
     * A requester can ask for the thread to be shut down.
     */
    public void endThread() {
        endThread = true;
    }

    /**
     * Handles the queue request by placing a synchronized lock on the Thumbnail
     * Controller and passes the request to the Factory.
     *
     * @param request the {@link ThumbnailQueueRequest} for which to create the
     *                ThumbnailController
     */
    private static void processQueueRequest(final ThumbnailQueueRequest request) {
        final var userObject = request.getNode().getUserObject();
        if (userObject == null) {
            LOGGER.log(Level.SEVERE, "Queue request for a null Could not find PictureInfo. Aborting. {0}", request);
            return;
        }

        try {
            if (userObject instanceof PictureInfo pictureInfo) {
                if (MimeTypes.isAMovie(pictureInfo.getImageFile())) {
                    request.setIcon(MOVIE_ICON);
                } else if (MimeTypes.isADocument(pictureInfo.getImageFile())) {
                    request.setIcon(DOCUMENT_ICON);
                } else {
                    final var imageBytes = JpoCache.getThumbnailImageBytes(pictureInfo.getImageFile(),
                            pictureInfo.getRotation(),
                            request.getSize());
                    if (imageBytes == null) {
                        LOGGER.log(Level.INFO, "Cache returned a null instead of image bytes for request {0} Setting BROKEN_THUMBNAIL_PICTURE", pictureInfo);
                        request.setIcon(BROKEN_THUMBNAIL_PICTURE);
                    } else {
                        request.setIcon(new ImageIcon(imageBytes.getBytes()));
                    }
                }
            } else if (userObject instanceof GroupInfo) {
                final List<SortableDefaultMutableTreeNode> childPictureNodes = request.getNode().getChildPictureNodes(false);

                final var imageBytes = JpoCache.getGroupThumbnailImageBytes(childPictureNodes);
                if (imageBytes == null) {
                    request.setIcon(BROKEN_THUMBNAIL_PICTURE);
                } else {
                    request.setIcon(new ImageIcon(imageBytes.getBytes()));
                }
            } else {
                request.setIcon(BROKEN_THUMBNAIL_PICTURE);
            }
        } catch (final IOException ex) {
            LOGGER.severe(ex.getMessage());
            request.setIcon(BROKEN_THUMBNAIL_PICTURE);
        }
        request.notifyCallbackHandler();
    }

}
