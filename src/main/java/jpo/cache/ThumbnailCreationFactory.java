package jpo.cache;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/*
 ThumbnailCreationFactory.java:  A factory that creates thumbnails

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
 * Implementations of this class become a Thread that poll the
 * {@link ThumbnailCreationQueue} for new {@link ThumbnailQueueRequest} and
 * process them.
 *
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
        //final String BROKEN_THUMBNAIL_PICTURE_FILE = "jpo/images/broken_thumbnail.gif";
        final String BROKEN_THUMBNAIL_PICTURE_FILE = "broken_thumbnail.gif";
        URL resource = ThumbnailCreationFactory.class.getClassLoader().getResource( BROKEN_THUMBNAIL_PICTURE_FILE );
        if ( resource == null ) {
            LOGGER.severe( "Classloader could not find the file: " + BROKEN_THUMBNAIL_PICTURE_FILE );
            BROKEN_THUMBNAIL_PICTURE = null;
        } else {
            BROKEN_THUMBNAIL_PICTURE = new ImageIcon( resource );
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
     * Thread.MIN_PRIOTITY priority to ensure good overall response.
     *
     * @param pollingInterval The polling interval in milliseconds
     */
    public ThumbnailCreationFactory( int pollingInterval ) {
        this.pollingInterval = pollingInterval;
        Thread thread = new Thread( this, "ThumbnailCreationFactory" );
        thread.setPriority( Thread.MIN_PRIORITY );
        thread.start();
    }

    /**
     * The run method for the thread that keeps checking whether there are any
     * {@link ThumbnailQueueRequest} objects on the queue to be rendered.
     */
    @Override
    public void run() {
        while ( !endThread ) {
            ThumbnailQueueRequest request = ThumbnailCreationQueue.poll();
            if ( request == null ) {
                try {
                    Thread.sleep( pollingInterval );
                } catch ( InterruptedException x ) {
                    // Restore interrupted state
                    Thread.currentThread().interrupt();
                }
            } else {
                processQueueRequest( request );
            }
        }
    }

    /**
     * A requestor can ask for the thread to be shut down.
     */
    public void endThread() {
        endThread = true;
    }
    
    /**
     * Handles the queue request by placing a synchronized lock on the Thumbnail
     * Controller and passes the request to the Factory.
     *
     * @param request	the {@link ThumbnailQueueRequest} for which to create the
     * ThumbnailController
     */
    private static void processQueueRequest( ThumbnailQueueRequest request ) {
        Object userObject = request.getNode().getUserObject();
        if ( userObject == null ) {
            LOGGER.severe( "Queue request for a null Could not find PictureInfo. Aborting." );
            return;
        }

        try {
            if ( userObject instanceof PictureInfo ) {
                PictureInfo pictureinfo = (PictureInfo) userObject;

                ImageBytes imageBytes = JpoCache.getInstance().getThumbnailImageBytes( pictureinfo.getImageURL(),
                        pictureinfo.getRotation(),
                        request.getSize() );
                if ( imageBytes == null ) {
                    request.setIcon( BROKEN_THUMBNAIL_PICTURE );
                } else {
                    request.setIcon( new ImageIcon( imageBytes.getBytes() ) );
                }
            } else if ( userObject instanceof GroupInfo ) {
                List<SortableDefaultMutableTreeNode> childPictureNodes = request.getNode().getChildPictureNodes( false );

                ImageBytes imageBytes = JpoCache.getInstance().getGroupThumbnailImageBytes( childPictureNodes );
                if ( imageBytes == null ) {
                    request.setIcon( BROKEN_THUMBNAIL_PICTURE );
                } else {
                    request.setIcon( new ImageIcon( imageBytes.getBytes() ) );
                }
            } else {
                request.setIcon( BROKEN_THUMBNAIL_PICTURE );
            }
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
            request.setIcon( BROKEN_THUMBNAIL_PICTURE );
        }
        request.notifyCallbackHandler();
    }

}
