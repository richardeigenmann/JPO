package org.jpo.cache;

import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.dataModel.SortableDefaultMutableTreeNode;

import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Logger;


/*
 Copyright (C) 2003-2019  Richard Eigenmann.
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
    protected static final PriorityBlockingQueue<ThumbnailQueueRequest> QUEUE = new PriorityBlockingQueue<>();

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
     * @param	callbackHandler	The ThumbnailQueueRequestCallbackHandler which is
     * to be notified when done
     * @param node The node for which to extract a thumbnail
     * @param priority The priority with which the request is to be treated on
     * the queue
     * @param size the maximum size to extract
     * @return true if the request was added to the queue, false if the request
     * already existed.
     */
    public static ThumbnailQueueRequest requestThumbnailCreation(
            ThumbnailQueueRequestCallbackHandler callbackHandler,
            SortableDefaultMutableTreeNode node,
            QUEUE_PRIORITY priority,
            Dimension size ) {
        ThumbnailQueueRequest newThumbnailQueueRequest = new ThumbnailQueueRequest( callbackHandler, node, priority, size );

        ThumbnailQueueRequest requestFoundOnQueue = findThumbnailQueueRequest( callbackHandler );
        if ( requestFoundOnQueue == null ) {
            QUEUE.add( newThumbnailQueueRequest );
            return newThumbnailQueueRequest;
        } else if ( ( requestFoundOnQueue.getThumbnailQueueRequestCallbackHandler() != callbackHandler )
                || ( requestFoundOnQueue.getNode() != node )
                || ( requestFoundOnQueue.getSize().width != size.width )
                || ( requestFoundOnQueue.getSize().height != size.height ) ) {
            requestFoundOnQueue.cancel();
            remove( requestFoundOnQueue );
            QUEUE.add( newThumbnailQueueRequest );
            return newThumbnailQueueRequest;
        } else {
            requestFoundOnQueue.increasePriorityTo( priority );
            return requestFoundOnQueue;
        }
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
     * @param requestToRemove The request to remove
     */
    public static void remove( ThumbnailQueueRequest requestToRemove ) {
       if ( ! QUEUE.remove( requestToRemove ) ) {
           LOGGER.info( "Failed to remove request: " + requestToRemove.toString() + " from QUEUE");
       }
    }

    /**
     * This method returns the {@link ThumbnailQueueRequest} for the supplied
     * ThumbnailController if such a request exists. Otherwise it returns null.
     *
     * @param callbackHandler The {@link ThumbnailQueueRequestCallbackHandler}
     * for which the request is to be found
     * @return The ThumbnailQueueRequest if it exists.
     */
    protected static ThumbnailQueueRequest findThumbnailQueueRequest(
            ThumbnailQueueRequestCallbackHandler callbackHandler ) {
        ThumbnailQueueRequest req = null;
        ThumbnailQueueRequest test;
        for ( Iterator i = QUEUE.iterator(); i.hasNext(); ) {
            test = (ThumbnailQueueRequest) i.next();
            if ( ( callbackHandler != null ) && ( test.getThumbnailQueueRequestCallbackHandler().equals( callbackHandler ) ) ) {
                req = test;
                break;
            }
        }
        return req;
    }
}
