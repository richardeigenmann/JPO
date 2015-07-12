package jpo.cache;

import java.awt.Dimension;
import javax.swing.ImageIcon;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/*
 ThumbnailQueueRequest.java: Element on the ThumbnailCreationQueue Queue

 Copyright (C) 2002 - 2015  Richard Eigenmann.
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
 * The ThumbnailQueueRequest is the type of object that will sit on the
 * {@link ThumbnailCreationQueue} with a references to the
 * {@link ThumbnailController}, the queue priority and an indicator whether the
 * thumbnail creation must be forced. TODO: Analyse how often a new picture is
 * thrown on the queue. Could be a bit too often... TODO: Perhaps we should not
 * throw thumbnails on the queue but lowresimages as these are not exactly the
 * same as a GUI component which is what a ThumbnailController is.
 */
public class ThumbnailQueueRequest implements Comparable<ThumbnailQueueRequest> {

    public enum QUEUE_PRIORITY {

        HIGH_PRIORITY( 0 ),
        MEDIUM_PRIORITY( 1 ),
        LOW_PRIORITY( 2 ),
        LOWEST_PRIORITY( 3 );

        private Integer priority;

        private QUEUE_PRIORITY( int priority ) {
            this.priority = priority;
        }
    }

    protected ThumbnailQueueRequestCallbackHandler callbackHandler;
    /**
     * the priority the request has on the queue.
     */
    protected QUEUE_PRIORITY priority;

    /**
     * The size we should scale the thumbnail to
     */
    protected final Dimension size;

    /**
     * The node for which we are to create a thumbnail
     */
    protected final SortableDefaultMutableTreeNode node;

    /**
     * The resulting image icon
     */
    private ImageIcon icon;

    /**
     * Constructs a ThumbnailQueueRequest object
     *
     * @param callbackHandler	The callback handler that will be notified when
     * the image icon is ready
     * @param node the node for which the image is to be created
     * @param priority	The queue priority with which the thumbnail is to be
     * created Possible values are
     * {@link #HIGH_PRIORITY}, {@link #MEDIUM_PRIORITY}, {@link #LOW_PRIORITY}
     * and {@link #LOWEST_PRIORITY}. //* @param force	set to true if the
     * ThumbnailController must be read from source if set to false it is
     * permissible to just reload the cached ThumbnailController.
     * @param size the maximum size of the thumbnail
     */
    public ThumbnailQueueRequest(
            ThumbnailQueueRequestCallbackHandler callbackHandler,
            SortableDefaultMutableTreeNode node,
            QUEUE_PRIORITY priority,
            Dimension size ) {
        this.callbackHandler = callbackHandler;
        this.node = node;
        this.priority = priority;
        this.size = size;
    }

    /**
     * returns the {@link ThumbnailQueueRequestCallbackHandler} that should be
     * notified when the ImageIcon is ready
     *
     * @return the thumbnail
     */
    public ThumbnailQueueRequestCallbackHandler getThumbnailQueueRequestCallbackHandler() {
        return callbackHandler;
    }

    /**
     * sends the notification that the Icon is available to the callback handler
     * if the request was not canceled in the mean time.
     */
    public void notifyCallbackHandler() {
        if ( !isCancelled() ) {
            callbackHandler.callbackThumbnailCreated( this );
        } else {
            System.out.println( "Suppressing notification" );
        }
    }

    /**
     * sets the priority in which the {@link ThumbnailController} is to be
     * created. The possible values are
     * {@link #LOWEST_PRIORITY}, {@link #LOW_PRIORITY}, {@link #MEDIUM_PRIORITY}
     * or {@link #HIGH_PRIORITY}. A high numeric value means less priority.
     *
     * @param newPriority The priority of the request:
     * {@link #LOW_PRIORITY}, {@link #MEDIUM_PRIORITY} or {@link #HIGH_PRIORITY}
     */
    public void setPriority( QUEUE_PRIORITY newPriority ) {
        priority = newPriority;
    }

    /**
     * Compares to another request based on priority.
     *
     * @param thumbnailQueueRequest The request to compare against
     * @return a negative 0 or positive number as defined by the compareTo
     * interface
     */
    @Override
    public int compareTo( ThumbnailQueueRequest thumbnailQueueRequest ) {
        return priority.compareTo( thumbnailQueueRequest.priority );
    }

    /**
     * Increases the priority of the request to the supplied higher priority if
     * the supplied priority is higher.
     *
     * @param newPriority the new, possibly higher priority
     */
    public void increasePriorityTo( QUEUE_PRIORITY newPriority ) {
        if ( priority.compareTo( newPriority ) > 0 ) {
            setPriority( newPriority );
        }

    }

    /**
     * The size the thumbnail should be scaled to
     *
     * @return the size
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * Returns the node for the request
     *
     * @return the node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * @return the icon that was created
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Sets the icon after the creation queue
     *
     * @param icon the new icon
     */
    public void setIcon( ImageIcon icon ) {
        this.icon = icon;
    }

    /**
     * A flag to indicate that the request was cancelled.
     */
    protected Boolean isCancelled = false;

    /**
     *
     * @return true is the request is cancelled
     */
    public boolean isCancelled() {
        synchronized ( isCancelled ) {
            return isCancelled;
        }
    }

    public void cancel() {
        synchronized ( isCancelled ) {
            this.isCancelled = true;
        }
    }

}
