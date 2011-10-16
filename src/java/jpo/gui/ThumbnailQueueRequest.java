package jpo.gui;

/*
ThumbnailQueueRequest.java: Element on the ThumbnailController Queue

Copyright (C) 2002 - 2009  Richard Eigenmann.
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
 *  The ThumbnailQueueRequest is the type of object that will sit on the 
 *  {@link ThumbnailCreationQueue} with a references to the {@link ThumbnailController}, the queue priority
 *  and an indicator whether the thumbnail creation must be forced.
 * TODO: Analyse how often a new picture is thrown on the queue. Could be a bit too often...
 * TODO: Perhaps we should not throw thumbnails on the queue but lowresimages as these are not exactly the same as a GUI component which is what a ThumbnailController is.
 */
public class ThumbnailQueueRequest implements Comparable<ThumbnailQueueRequest> {

    /**
     *  a constant to indicate a high queue priority. It is implemented as the value 0.
     */
    public static final int HIGH_PRIORITY = 0;
    /**
     *  a constant to indicate a medium queue priority. It is implemented as the value 1.
     */
    public static final int MEDIUM_PRIORITY = HIGH_PRIORITY + 1;
    /**
     *  a constant to indicate a low queue priority. It is implemented as the value 2.
     */
    public static final int LOW_PRIORITY = MEDIUM_PRIORITY + 1;
    /**
     *  a constant to used in the process of finding the highest priority queue item.
     *  It must be 1 more than the Low Priority. Do not use this in the assignment.
     */
    protected static final int LOWEST_PRIORITY = LOW_PRIORITY + 1;
    /**
     *  a reference to the ThumbnailController for which the request is to be performed.
     */
    protected ThumbnailController thumbnailController;
    /**
     *  the priority the request has on the queue.
     */
    protected int priority;
    /**
     *  indicates that the thumbnail must be recreated regardless of any pre-existing thumbnail
     */
    protected boolean force;

    /**
     *  Constructs a ThumbnailQueueRequest object
     *  @param	thumb	The ThumbnailController object for which the thumbnail is to be created
     *  @param	priority	The priority with which the thumbnail is to be created
     *	Possible values are {@link #HIGH_PRIORITY}
     *	{@link #MEDIUM_PRIORITY} and
     *  {@link #LOW_PRIORITY}.
     *  @param	force	set to true if the ThumbnailController must be read from source if set to false
     *  it is permissible to just reload the cached ThumbnailController.
     */
    ThumbnailQueueRequest(ThumbnailController thumb, int priority, boolean force) {
        this.thumbnailController = thumb;
        this.priority = priority;
        this.force = force;
    }

    /**
     *  returns the {@link ThumbnailController} which is to be created.
     *
     * @return the thumbnail
     */
    public ThumbnailController getThumbnailController() {
        return thumbnailController;
    }

    /**
     * returns the priority in which the {@link ThumbnailController} is to be created. The values returned are
     * {@link #LOW_PRIORITY}, {@link #MEDIUM_PRIORITY}
     * or {@link #HIGH_PRIORITY}. A high numeric value means less priority.
     *
     * @return {@link #LOW_PRIORITY}, {@link #MEDIUM_PRIORITY}
     * or {@link #HIGH_PRIORITY}
     */
    public int getPriority() {
        return priority;
    }

    /**
     *  sets the priority in which the {@link ThumbnailController} is to be created. The possible values are
     *  {@link #LOW_PRIORITY}, {@link #MEDIUM_PRIORITY}
     *  or {@link #HIGH_PRIORITY}. A high numeric value means less priority.
     *
     *  @param  newPriority  The priority of the request: {@link #LOW_PRIORITY}, {@link #MEDIUM_PRIORITY}
     *  or {@link #HIGH_PRIORITY}
     */
    public void setPriority(int newPriority) {
        priority = newPriority;
    }

    /**
     *   returns whether the rebuilding of the {@link ThumbnailController} must be forced or
     *   whether an available cached thumbnail will suffice.
     *
     *   @return  true if the thumbnail creation must be forced, false if not.
     */
    public boolean getForce() {
        return force;
    }

    /**
     *   sets whether the rebuilding of the {@link ThumbnailController} must be forced or
     *   whether an available cached thumbnail will suffice.
     *
     *   @param newForce   true if the thumbnail creation must be forced, false if not.
     */
    public void setForce(boolean newForce) {
        force = newForce;
    }

    /**
     * Compares to another request based on priority.
     * @param o The request to compare against
     * @return a negative 0 or positive number as defined by the compareTo interface
     */
    public int compareTo(ThumbnailQueueRequest o) {
        return getPriority() - o.getPriority();
    }

    /**
     * Inform about the request
     * @return information about the request
     */
    @Override
    public String toString() {
        return String.format("ThumbnailQueueRequest: Hash: %d, Priority: %d, Force: %b, Thumbnail: %s", this.hashCode(), priority, force, thumbnailController.toString());

    }
}
