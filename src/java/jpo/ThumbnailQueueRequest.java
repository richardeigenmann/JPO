package jpo;

/*
ThumbnailQueueRequest.java: Element on the Thumbnail Queue

Copyright (C) 2002  Richard Eigenmann.
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
 *  Object that holds the references to the Thumbnail and the priority 
 *  with which the request was submitted
 */
public class ThumbnailQueueRequest {
	private Thumbnail thumb;
	private int priority;
	private boolean force;

	/**
	 *  Constructs a ThumbnailQueueRequest object
	 *  @param	thumb	The Thumbnail object for which the thumbnail is to be created		
	 *  @param	priority	The priority with which the thumbnail is to be created
	 *				Possible values are ThumbnailCreationQueue.HIGH_PRIORITY
	 *				ThumbnailCreationQueue.MEDIUM_PRIORITY and ThumbnailCreationQueue.LOW_PRIORITY
	 *  @param	force	set to true if the Thubnail must be read from source if set to false 
	 *			it is permissible to just reload the cached Thumbnail.
	 */					
	ThumbnailQueueRequest( Thumbnail thumb, int priority, boolean force ) {
		this.thumb = thumb;
		this.priority = priority;
		this.force = force;
	}
		
	public Thumbnail getThumbnail() {
		return thumb;
	}
		
	public int getPriority() {
		return priority;
	}
	
	
	/**
	 *   returns whether the rebuilding of the thumbnail must be forced or
	 *   whether an available cached thumbnail will suffice.
	 */
	public boolean getForce() {
		return force;
	}
}
