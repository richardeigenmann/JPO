package jpo.gui;

import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;
import jpo.gui.ThumbnailQueueRequest.QUEUE_PRIORITY;


/*
 ThumbnailCreationQueue.java:  queue that holds requests to create Thumbnails from Highres Images

 Copyright (C) 2003-2015  Richard Eigenmann.
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
 * Queue that holds requests to create Thumbnails from Images. 
 *
 */
public class ThumbnailCreationQueue {

    /**
     * Implemented using a PriorityBlockingQueue
     */
    private static final PriorityBlockingQueue<ThumbnailQueueRequest> QUEUE = new PriorityBlockingQueue<>();
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ThumbnailCreationQueue.class.getName() );

    /**
     * This method creates a {@link ThumbnailQueueRequest} and sticks it on the
     * {@link ThumbnailCreationQueue} if Thumbnail is not already on the queue.
     * Otherwise the queue priority of the existing request is increased if the
     * request comes in with a higher priority. If the new request forces a
     * rebuild of the image this is also updated in the request.
     *
     *
     * @param	thumbnailController	The ThumbnailController which is to be loaded
     * @param	priority	The priority with which the request is to be treated on
     * the queue
     * @param	force	Set to true if the thumbnail needs to be rebuilt from
     * source, false if using a cached version is OK.
     * @return true if the request was added to the queue, false if the request
     * already existed.
     */
    public static boolean requestThumbnailCreation(
            ThumbnailController thumbnailController,
            QUEUE_PRIORITY priority) {
        //LOGGER.fine( "Chucking a request on the queue for ThumbnailController: " + thumbnailController.toString() );
        ThumbnailQueueRequest requestFoundOnQueue = findThumbnailQueueRequest( thumbnailController );
        if ( requestFoundOnQueue == null ) {
            ThumbnailQueueRequest thumbnailQueueRequest = new ThumbnailQueueRequest( thumbnailController, thumbnailController.getNode(), priority, thumbnailController.getMaximumUnscaledSize() );
            //logger.info( String.format( "There is no prior request on the queue, we add a new one: %s", tqr.toString() ) );
            add( thumbnailQueueRequest );
            return true;
        } else {
            //logger.info( "Such a request is already on the queue" );
            // thumbnail already on queue, should we up the priority?
            //if ( requestFoundOnQueue.getPriority() > priority ) {
            //    requestFoundOnQueue.setPriority( priority );
            //}
            requestFoundOnQueue.increasePriorityTo( priority );
            // must we now rebuild the image?
            //if ( force && ( requestFoundOnQueue.getForce() != force ) ) {
            //    requestFoundOnQueue.setForce( true );
            //}
            return false;
        }
    }

    /**
     * Adds a request to the queue.
     *
     * @param thumbnailQueueRequest The request to add
     */
    public static void add( ThumbnailQueueRequest thumbnailQueueRequest ) {
        QUEUE.add( thumbnailQueueRequest );
    }

    /**
     * Returns the highest priority request on the queue.
     *
     * @return The highest priority queue request
     */
    public static ThumbnailQueueRequest poll() {
        ThumbnailQueueRequest thumbnailQueueRequest = QUEUE.poll();
        return thumbnailQueueRequest;
    }

    /**
     * Retrieves and removes the head of this queue, or returns null if this
     * queue is empty.
     *
     * @param tqr The request to poll
     */
    public static void remove( ThumbnailQueueRequest tqr ) {
        QUEUE.remove( tqr );
    }

    /**
     * Remove all queue requests from the queue.
     */
    public static void clear() {
        QUEUE.clear();
    }

    /**
     * Returns the number of requests currently on the queue
     *
     * @return The number of requests on the queue.
     */
    public static int size() {
        return QUEUE.size();
    }

    /**
     * removes the request for a specific ThumbnailController from the queue.
     *
     * @param thumbnailController The thumbnail to be removed
     */
    public static void removeThumbnailQueueRequest( ThumbnailController thumbnailController ) {
        ThumbnailQueueRequest thumbnailQueueRequest = findThumbnailQueueRequest( thumbnailController );
        if ( thumbnailQueueRequest != null ) {
            remove( thumbnailQueueRequest );
        }
    }

    /**
     * This method returns the {@link ThumbnailQueueRequest} for the supplied
     * ThumbnailController if such a request exists. Otherwise it returns null.
     *
     * @param thumbnailController The {@link ThumbnailController} for which the
     * request is to be found
     * @return The ThumbnailQueueRequest if it exists.
     */
    public static ThumbnailQueueRequest findThumbnailQueueRequest(
            ThumbnailController thumbnailController ) {
        ThumbnailQueueRequest req = null, test;
        for ( Iterator i = QUEUE.iterator(); i.hasNext(); ) {
            test = (ThumbnailQueueRequest) i.next();
            if ( ( thumbnailController != null ) && ( test.getThumbnailQueueRequestCallbackHandler().equals( thumbnailController ) ) ) {
                req = test;
                break;
            }
        }
        return req;
    }
}
