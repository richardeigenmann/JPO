package jpo.gui;

import java.util.Iterator;
import java.util.concurrent.PriorityBlockingQueue;


/*
ThumbnailCreationQueue.java:  queue that holds requests to create Thumbnails from Highres Images

Copyright (C) 2003-2009  Richard Eigenmann.
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
 *  Queue that holds requests to create Thumbnails from Highres Images.
 * It is deliberately static so that there is only one queue for the entire application.
 **/
public class ThumbnailCreationQueue {

    /**
     * Implemented using a PriorityBlockingQueue
     */
    private static final PriorityBlockingQueue<ThumbnailQueueRequest> queue = new PriorityBlockingQueue<ThumbnailQueueRequest>();


    /**
     *  This method creates {@link ThumbnailQueueRequest} and sticks it
     *  on the {@link ThumbnailCreationQueue}.
     *
     *  @param	thumb	The Thumbnail which is to be loaded
     *  @param	priority	The priority with which the request is to be treated on the queue
     *  @param	force		Set to true if the thumbnail needs to be rebuilt from source, false
     *				if using a cached version is OK.
     */
    public static void requestThumbnailCreation( Thumbnail thumb, int priority, boolean force ) {
        ThumbnailQueueRequest req = findQueueRequest( thumb );
        if ( req == null ) {
            thumb.setPendingIcon();
            add( new ThumbnailQueueRequest( thumb, priority, force ) );
        } else {
            // thumbnail already on queue
            if ( req.getPriority() > priority ) {
                req.setPriority( priority );
            }
            if ( req.getForce() || force ) {
                req.setForce( true );
            }
        }
    }


    /**
     * Adds a request to the queue.
     * @param tqr The request to add
     */
    public static void add( ThumbnailQueueRequest tqr ) {
        queue.add( tqr );
    }


    /**
     * Returns the highest priority request on the queue.
     * @return The highest priority queue request
     */
    public static ThumbnailQueueRequest remove() {
        return queue.poll();
    }


    /**
     * Removes the specified request from the queue
     * @param tqr The request to remove
     */
    public static void remove( ThumbnailQueueRequest tqr ) {
        queue.remove( tqr );
    }


    /**
     * Remove all queue requests from the queue.
     */
    public static void clear() {
        queue.clear();
    }


    /**
     * Returns the number of Requests currently on the queue
     *
     * @return  The number of requests on the queue.
     */
    public static int size() {
        return queue.size();
    }


    /**
     *   removes the request for a specific Thumbnail from the queue.
     *
     *   @param  thumb  The thumbnail to be removed
     */
    public static void removeThumbnailRequest( Thumbnail thumb ) {
        ThumbnailQueueRequest tqr = findQueueRequest( thumb );
        if ( tqr != null ) {
            queue.remove( tqr );
        }
    }


    /**
     *   This method returns the {@link ThumbnailQueueRequest} for the supplied Thumbnail if such
     *   a request exists. Otherwise it returns null.
     *
     *   @param  thumb  The {@link Thumbnail} for which the request is to be found
     *   @return   The ThumbnailQueueRequest if it exists.
     */
    public static ThumbnailQueueRequest findQueueRequest( Thumbnail thumb ) {
        ThumbnailQueueRequest req = null, test = null;
        for ( Iterator i = queue.iterator(); i.hasNext(); ) {
            test = (ThumbnailQueueRequest) i.next();
            if ( test.getThumbnail().equals( thumb ) ) {
                req = test;
                break;
            }
        }
        return req;
    }
}
