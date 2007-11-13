package jpo;

import java.util.*;
import javax.swing.ImageIcon;


/*
ThumbnailCreationQueue.java:  queue that holds requests to create Thumbnails from Highres Images

Copyright (C) 2003-2007  Richard Eigenmann.
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
 *  Queue that holds requests to create Thumbnails from Highres Images
 **/
public class ThumbnailCreationQueue {

	/**
	 *  The thumnail creation Queue. It is implemented as a Vector.
	 */
	private static Vector thumbQueue = new Vector();
	
	/**
	 *  This Vector allows us to keep track of the number of ThumbnailCreationThreads 
	 *  we have fired off. Could be enhanced to dynamically start more or less.
	 */
	private static Vector thumbnailFactories = new Vector();


	/**
	 *  static initializer for the ThumbnailCreationThreads
	 */
	static {
		for ( int i = 1; i <= Settings.numberOfThumbnailCreationThreads; i++ ) {
			thumbnailFactories.add( new ThumbnailCreationThread() );
		}
	}
	

		
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
	 *   This icon indicates that the thumbnail creation is sitting on the queue.
	 */
	private static final ImageIcon queueIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/queued_thumbnail.gif" ) );


	/**
	 *   This icon shows a large yellow folder.
	 */
	private static final ImageIcon largeFolderIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_folder_large.jpg" ) );


	/** 
	 *  This method puts a ThumbnailCreationRequest on the queue. 
	 *  If a suitable cached image exists it will be used instead.
	 *
	 *  @param	thumb	The Thumbnail which is to be loaded
	 *  @param	priority	The priority with which the request is to be treated on the queue
	 *  @deprecated  Use requestThumbnailCreation Thumbnail, priority, force instead.
	 */
	public static void requestThumbnailCreation( Thumbnail thumb, int priority ) {
		requestThumbnailCreation( thumb, priority, false );
	}
	

	/** 
	 *  This method puts a ThumbnailCreationRequest on the queue. The thumbnail will be rebuilt from
	 *  the source image.
	 *
	 *  @param	thumb	The Thumbnail which is to be loaded
	 *  @param	priority	The priority with which the rquest is to be treated on the queue
	 *  @deprecated  Use requestThumbnailCreation Thumbnail, priority, force instead.
	 */
	public static void forceThumbnailCreation( Thumbnail thumb, int priority ) {
		requestThumbnailCreation( thumb, priority, true );
	}
	

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
		// prevent concurrent use of Thumbnail:
		//Tools.log("ThumbnailCreationQueue.requestThumbnailCreation: Thumbnail: " + thumb.toString() + " Priority: " + Integer.toString(priority) + " Force: " + Boolean.toString(force));
		synchronized( thumbQueue ) {
			//Tools.log("   got past thumbQueue synchronization.");
			ThumbnailQueueRequest req = findQueueRequest( thumb );
			if ( req == null ) {
				//Tools.log("   trying to synchronize on Thumbnail.");
				//synchronized( thumb ) {
					//Tools.log("   got past thumb synchronization.");
					if ( thumb.referringNode == null ) {
						Tools.log( "ThumbnailCreationQueue.requestThumbnailCreation: referring node was null! How did this happen?");
						return;
					} else if ( thumb.referringNode.getUserObject() instanceof PictureInfo ) {
						thumb.setThumbnail( queueIcon );
					} else {
						thumb.setThumbnail( largeFolderIcon );
					}
				//}
				thumbQueue.add( new ThumbnailQueueRequest ( thumb, priority, force ) );
			} else {
				// thumbnail already on queue
				//Tools.log("ThumbnailCreationQueue.requestThumbnailCreation: Thumbnail already on queue: " + thumb.toString());
				if ( req.getPriority() > priority ) {
					req.setPriority( priority );
				}
				if ( req.getForce() || force ) {
					req.setForce( true );
				}
			}
		}
	}



	/**
	 *   removes the specified Thumbnail from the queue.
	 *
	 *   @param  thumb  The thumbnail to be removed
	 */
	public static void remove( Thumbnail thumb ) {
		synchronized( thumbQueue ) {
			ThumbnailQueueRequest req = findQueueRequest( thumb );
			if ( req != null ) {
				thumbQueue.remove( req );
			}
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
		synchronized( thumbQueue ) {
			boolean notFound = true;
			ThumbnailQueueRequest test = null;
			Enumeration e = thumbQueue.elements();		
			while ( e.hasMoreElements() && notFound ) {
				test = (ThumbnailQueueRequest) e.nextElement();
				if ( test.getThumbnail() == thumb ) {
					notFound = false;
				}
			}
			if ( notFound ) {
				return null;
			} else {
				return test;
			}
		}
	}




	/**
	 *   Remove all queue requests from the queue.
	 */
	public static void removeAll() {
		synchronized( thumbQueue ) {
			thumbQueue.removeAllElements();
		}
	}


	/**
	 *  Returns the highest priority request on the queue. If there
	 *  are no elements it returns null. The way it finds the highest 
	 *  priority request is by enumerating the elements on the queue.
	 *  It then loops through the elements and checks whether the element 
	 *  has a higher priority than initially a priority lower than the lowest assignable
	 *  priority. If this is the case this element is marked unless the next 
	 *  element has a higher priority. This is repeated till there are no more 
	 *  elements or a request with the highest priority is found.
	 *
	 *  @return  The highest priority request on the queue.
	 */
	public static ThumbnailQueueRequest getRequest() {
		synchronized ( thumbQueue ) { // prevent any other thread from messing with the queue until we are done.
			ThumbnailQueueRequest req = null;
			ThumbnailQueueRequest test;
			int prio = LOWEST_PRIORITY;
			Enumeration e = thumbQueue.elements();		
			while ( e.hasMoreElements() && ( prio > HIGH_PRIORITY ) ) {
				test = (ThumbnailQueueRequest) e.nextElement();
				if ( test.getPriority() < prio ) {
					req = test;
					prio = test.getPriority();
				}
			}
			thumbQueue.remove( req );
			return req;
		}
	}


	/**
	 * returns the number of Requests currently on the queue
	 */
	public static int getQueueRequestCount() {
		return thumbQueue.size();
	}
			
}
