package org.jpo.cache;

import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import javax.swing.*;
import java.awt.*;

/*
 Copyright (C) 2002-2024 Richard Eigenmann.
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
 * The ThumbnailQueueRequest is the type of object that will sit on the
 * {@link ThumbnailCreationQueue} with a references to the
 * {@link org.jpo.cache.ThumbnailQueueRequestCallbackHandler} and the queue priority.
 * When the Thumbnail has been created the
 * {@link ThumbnailQueueRequestCallbackHandler#callbackThumbnailCreated}
 * method will be fired and the caller can process the new Thumbnail.
 */
public class ThumbnailQueueRequest implements Comparable<ThumbnailQueueRequest> {

    /**
     * The target to notify that the Thumbnail is now ready
     */
    protected final ThumbnailQueueRequestCallbackHandler callbackHandler;

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
     * A flag to indicate that the request was canceled.
     */
    protected Boolean isCanceled = false;

    /**
     * Constructs a ThumbnailQueueRequest object
     *
     * @param callbackHandler The callback handler that will be notified when
     *                        the image icon is ready
     * @param node            the node for which the image is to be created
     * @param priority        The queue priority with which the thumbnail is to be
     *                        created Possible values are
     *                        {@link QUEUE_PRIORITY#HIGH_PRIORITY}, {@link QUEUE_PRIORITY#MEDIUM_PRIORITY},
     *                        {@link QUEUE_PRIORITY#LOW_PRIORITY}
     *                        and {@link QUEUE_PRIORITY#LOWEST_PRIORITY}.
     * @param size            the maximum size of the thumbnail
     */
    public ThumbnailQueueRequest(
            final ThumbnailQueueRequestCallbackHandler callbackHandler,
            final SortableDefaultMutableTreeNode node,
            final QUEUE_PRIORITY priority,
            final Dimension size) {
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
     * if the request was not canceled in the meantime.
     */
    public void notifyCallbackHandler() {
        if (!isCanceled()) {
            callbackHandler.callbackThumbnailCreated(this);
        }
    }

    /**
     * sets the priority in which the {@link org.jpo.gui.ThumbnailController} is to be
     * created. The possible values are
     * {@link QUEUE_PRIORITY#LOWEST_PRIORITY}, {@link QUEUE_PRIORITY#LOW_PRIORITY},
     * {@link QUEUE_PRIORITY#MEDIUM_PRIORITY}
     * or {@link QUEUE_PRIORITY#HIGH_PRIORITY}. A high numeric value means less priority.
     *
     * @param newPriority The priority of the request:
     *                    {@link QUEUE_PRIORITY#LOW_PRIORITY}, {@link QUEUE_PRIORITY#MEDIUM_PRIORITY} or {@link QUEUE_PRIORITY#HIGH_PRIORITY}
     */
    public void setPriority(final QUEUE_PRIORITY newPriority) {
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
    public int compareTo(final ThumbnailQueueRequest thumbnailQueueRequest) {
        return priority.compareTo(thumbnailQueueRequest.priority);
    }

    /**
     * Compares to another request based on priority.
     *
     * @param obj The request to compare against
     * @return if the priority is the same
     * interface
     */
    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }


    /**
     * Increases the priority of the request to the supplied higher priority if
     * the supplied priority is higher.
     *
     * @param newPriority the new, possibly higher priority
     */
    public void increasePriorityTo(final QUEUE_PRIORITY newPriority) {
        if (priority.compareTo(newPriority) > 0) {
            setPriority(newPriority);
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
    public void setIcon(final ImageIcon icon) {
        this.icon = icon;
    }

    /**
     * <a href="https://www.securecoding.cert.org/confluence/display/java/LCK01-J.+Do+not+synchronize+on+objects+that+may+be+reused">...</a>
     */
    private final Object lock = new Object();

    /**
     * @return true is the request is canceled
     */
    public boolean isCanceled() {
        synchronized (lock) {
            return isCanceled;
        }
    }

    /**
     * Cancel the request
     */
    public void cancel() {
        synchronized (lock) {
            this.isCanceled = true;
        }
    }



}
