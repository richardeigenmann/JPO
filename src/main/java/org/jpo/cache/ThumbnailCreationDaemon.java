package org.jpo.cache;

import org.jpo.datamodel.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 ThumbnailCreationFactory.java:  A factory that creates thumbnails

 Copyright (C) 2002-2022 Richard Eigenmann.
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
 * Instantiated objects of this class sit on their thread, polling the
 * ThumbnailCreationQueue for new thumbnail creation requests. If a new
 * request is found it is pulled off the queue and the daemon goes to work
 * creating the thumbnail image. Using notification the GUI can then
 * learn about a created Thumbnail and can redraw the picture.
 * {@link ThumbnailCreationQueue} for new {@link ThumbnailQueueRequest} and
 * process them.
 */
public class ThumbnailCreationDaemon implements Runnable {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ThumbnailCreationDaemon.class.getName());

    /**
     * An icon that indicates a broken image used when there is a problem
     * rendering the correct thumbnail.
     */
    private static final ImageIcon BROKEN_THUMBNAIL_PICTURE;

    static {
        BROKEN_THUMBNAIL_PICTURE = getResource( "broken_thumbnail.gif" );
    }

    /**
     * An icon that indicates a broken image used when there is a problem
     * rendering the correct thumbnail.
     */
    private static final ImageIcon MOVIE_ICON;

    static {
        MOVIE_ICON = getResource("icon_movie.png");
    }

    /**
     * An icon that indicates a broken image used when there is a problem
     * rendering the correct thumbnail.
     */
    private static final ImageIcon DOCUMENT_ICON;

    static {
        DOCUMENT_ICON = getResource("icon_document.png");
    }

    static ImageIcon getResource(final String resource) {
        final var resourceURL = ThumbnailCreationDaemon.class.getClassLoader().getResource(resource);
        if (resourceURL == null) {
            LOGGER.log(Level.SEVERE, "Classloader could not find the file: {}", resource);
            return null;
        } else {
            return new ImageIcon(resourceURL);
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
    public ThumbnailCreationDaemon(final int pollingInterval) {
        this.pollingInterval = pollingInterval;
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
                processPictureInfoRequest(request, pictureInfo);
            } else if (userObject instanceof GroupInfo) {
                processGroupInfoRequest(request);
            } else {
                request.setIcon(BROKEN_THUMBNAIL_PICTURE);
            }
        } catch (final IOException ex) {
            LOGGER.severe(ex.getMessage());
            request.setIcon(BROKEN_THUMBNAIL_PICTURE);
        }
        request.notifyCallbackHandler();
    }

    private static void processGroupInfoRequest(final ThumbnailQueueRequest request) throws IOException {
        final List<SortableDefaultMutableTreeNode> childPictureNodes = request.getNode().getChildPictureNodes(false);

        final var imageBytes = JpoCache.getGroupThumbnailImageBytes(childPictureNodes);
        if (imageBytes == null) {
            request.setIcon(BROKEN_THUMBNAIL_PICTURE);
        } else {
            request.setIcon(new ImageIcon(imageBytes.getBytes()));
        }
    }

    private static void processPictureInfoRequest(final ThumbnailQueueRequest request, final PictureInfo pictureInfo) {
        final File imageFile = pictureInfo.getImageFile();
        if (! imageFile.exists() || ! imageFile.canRead() ) {
            request.setIcon(BROKEN_THUMBNAIL_PICTURE);
        } else if (MimeTypes.isAMovie(imageFile)) {
            request.setIcon(MOVIE_ICON);
        } else if (MimeTypes.isADocument(imageFile)) {
            request.setIcon(DOCUMENT_ICON);
        } else if (!JpoImageIO.jvmHasReader(imageFile)) {
            request.setIcon(BROKEN_THUMBNAIL_PICTURE);
        } else {
            final var imageBytes = JpoCache.getThumbnailImageBytes(imageFile,
                    pictureInfo.getRotation(),
                    request.getSize());
            if (imageBytes == null) {
                LOGGER.log(Level.INFO, "Cache returned a null instead of image bytes for request {0}. Setting BROKEN_THUMBNAIL_PICTURE", pictureInfo);
                request.setIcon(BROKEN_THUMBNAIL_PICTURE);
            } else {
                request.setIcon(new ImageIcon(imageBytes.getBytes()));
            }
        }
    }

}
