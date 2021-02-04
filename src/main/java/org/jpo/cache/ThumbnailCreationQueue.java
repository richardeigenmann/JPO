package org.jpo.cache;

import org.jetbrains.annotations.TestOnly;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import java.awt.*;
import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;


/*
 Copyright (C) 2003-2021  Richard Eigenmann.
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

    // prevent a public constructor from being created by the compiler
    private ThumbnailCreationQueue() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Implemented using a PriorityBlockingQueue
     */
    protected static final PriorityBlockingQueue<ThumbnailQueueRequest> QUEUE = new PriorityBlockingQueue<>();

    /**
     * This method creates a {@link ThumbnailQueueRequest} and sticks it on the
     * {@link ThumbnailCreationQueue} if Thumbnail is not already on the queue.
     * Otherwise the queue priority of the existing request is increased if the
     * request comes in with a higher priority. If the new request forces a
     * rebuild of the image this is also updated in the request.
     *
     *
     * @param    callbackHandler    The ThumbnailQueueRequestCallbackHandler which is
     * to be notified when done
     * @param node The node for which to extract a thumbnail
     * @param priority The priority with which the request is to be treated on
     * the queue
     * @param size the maximum size to extract
     * @return true if the request was added to the queue, false if the request
     * already existed.
     */
    public static ThumbnailQueueRequest requestThumbnailCreation(
            final ThumbnailQueueRequestCallbackHandler callbackHandler,
            final SortableDefaultMutableTreeNode node,
            final QUEUE_PRIORITY priority,
            final Dimension size) {
        final ThumbnailQueueRequest newThumbnailQueueRequest = new ThumbnailQueueRequest(callbackHandler, node, priority, size);

        final ThumbnailQueueRequest requestFoundOnQueue = findThumbnailQueueRequest(callbackHandler);
        if (requestFoundOnQueue == null) {
            QUEUE.add(newThumbnailQueueRequest);
            return newThumbnailQueueRequest;
        } else if ((requestFoundOnQueue.getThumbnailQueueRequestCallbackHandler() != callbackHandler)
                || (requestFoundOnQueue.getNode() != node)
                || (requestFoundOnQueue.getSize() != size)) {
            requestFoundOnQueue.cancel();
            removeFromQueue(requestFoundOnQueue);
            QUEUE.add(newThumbnailQueueRequest);
            return newThumbnailQueueRequest;
        } else {
            requestFoundOnQueue.increasePriorityTo(priority);
            return requestFoundOnQueue;
        }
    }

    /**
     * Remove all queue requests from the queue.
     */
    @TestOnly
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
     * removes the request for a specific ThumbnailController from the queue
     * if it was on the queue in the first place.
     *
     * @param requestToRemove The request to remove
     */
    public static void removeFromQueue(final ThumbnailQueueRequest requestToRemove) {
        QUEUE.remove(requestToRemove);
    }

    /**
     * This method returns the {@link ThumbnailQueueRequest} for the supplied
     * ThumbnailController if such a request exists. Otherwise it returns null.
     *
     * @param callbackHandler The {@link ThumbnailQueueRequestCallbackHandler}
     * for which the request is to be found
     * @return The ThumbnailQueueRequest if it exists.
     */
    @TestOnly
    protected static ThumbnailQueueRequest findThumbnailQueueRequest(
            final ThumbnailQueueRequestCallbackHandler callbackHandler ) {
        for (final Iterator<ThumbnailQueueRequest> i = QUEUE.iterator(); i.hasNext(); ) {
            final ThumbnailQueueRequest test = i.next();
            if ((callbackHandler != null) && (test.getThumbnailQueueRequestCallbackHandler().equals(callbackHandler))) {
                return test;
            }
        }
        return null;
    }
}
