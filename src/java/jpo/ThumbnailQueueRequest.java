package jpo;

/*
ThumbnailQueueRequest.java: Element on the Thumbnail Queue

Copyright (C) 2002-2006  Richard Eigenmann.
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
 *  {@link ThumbnailCreationQueue} with a references to the {@link Thumbnail}, the queue priority 
 *  and an indicator whether the thumbnail creation must be forced.
 */
public class ThumbnailQueueRequest {

	/**
	 *  a reference to the Thumbnail for which the request is to be performed.
	 */
	protected Thumbnail thumb;
	
	/**
	 *  the prioriy the request has on the queue.
	 */
	protected int priority;
	
	/**
	 *  indicates that the thumbnail must be recreated regardless of any pre-existing thumbnail
	 */
	protected boolean force;


	/**
	 *  Constructs a ThumbnailQueueRequest object
	 *  @param	thumb	The Thumbnail object for which the thumbnail is to be created		
	 *  @param	priority	The priority with which the thumbnail is to be created
	 *				Possible values are {@link ThumbnailCreationQueue#HIGH_PRIORITY}
	 *				{@link ThumbnailCreationQueue#MEDIUM_PRIORITY} and 
	 *                              {@link ThumbnailCreationQueue#LOW_PRIORITY}.
	 *  @param	force	set to true if the Thumbnail must be read from source if set to false 
	 *			it is permissible to just reload the cached Thumbnail.
	 */					
	ThumbnailQueueRequest( Thumbnail thumb, int priority, boolean force ) {
		this.thumb = thumb;
		this.priority = priority;
		this.force = force;
	}

	/**
	 *  returns the {@link Thumbnail} which is to be created.
	 */		
	public Thumbnail getThumbnail() {
		return thumb;
	}

	
	/**
	 * returns the priority in which the {@link Thumbnail} is to be created. The values returned are 
	 * {@link ThumbnailCreationQueue#LOW_PRIORITY}, {@link ThumbnailCreationQueue#MEDIUM_PRIORITY}
	 * or {@link ThumbnailCreationQueue#HIGH_PRIORITY}. A high numeric value means less priority.
	 *
	 * @return {@link ThumbnailCreationQueue#LOW_PRIORITY}, {@link ThumbnailCreationQueue#MEDIUM_PRIORITY}
	 * or {@link ThumbnailCreationQueue#HIGH_PRIORITY}
	 */	
	public int getPriority() {
		return priority;
	}
	

	/**
	 *  sets the priority in which the {@link Thumbnail} is to be created. The possible values are 
	 *  {@link ThumbnailCreationQueue#LOW_PRIORITY}, {@link ThumbnailCreationQueue#MEDIUM_PRIORITY}
	 *  or {@link ThumbnailCreationQueue#HIGH_PRIORITY}. A high numeric value means less priority.
	 *
	 *  @param  newPriority  The priority of the request: {@link ThumbnailCreationQueue#LOW_PRIORITY}, {@link ThumbnailCreationQueue#MEDIUM_PRIORITY}
	 *  or {@link ThumbnailCreationQueue#HIGH_PRIORITY}
	 */	
	public void setPriority( int newPriority ) {
		priority = newPriority;
	}

	
	/**
	 *   returns whether the rebuilding of the {@link Thumbnail} must be forced or
	 *   whether an available cached thumbnail will suffice.
	 *
	 *   @return  true if the thumbnail creation must be forced, false if not.
	 */
	public boolean getForce() {
		return force;
	}

	/**
	 *   sets whether the rebuilding of the {@link Thumbnail} must be forced or
	 *   whether an available cached thumbnail will suffice.
	 *
	 *   @param newForce   true if the thumbnail creation must be forced, false if not. 
	 */
	public void setForce( boolean newForce ) {
		force = newForce;
	}

}
