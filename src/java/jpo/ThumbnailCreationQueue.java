package jpo;

import java.util.*;
import javax.swing.ImageIcon;


/*
ThumbnailCreationQueue.java:  queue that holds requests to create Thumbnails from Highres Images

Copyright (C) 2003-2006  Richard Eigenmann.
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
	 *  The thumnail creation Queue.
	 */
	private static Vector thumbQueue = new Vector();
	
	/**
	 *  The first ThumbnailCreationThread.
	 */
	private static ThumbnailCreationThread thubnailFactory1 = new ThumbnailCreationThread();
	
	/**
	 *  The second ThumbnailCreationThread.
	 */
	private static ThumbnailCreationThread thubnailFactory2 = new ThumbnailCreationThread();

		
	/**
	 *  a constant to indicate a high priority
	 */
	public static final int HIGH_PRIORITY = 0;

	/**
	 *  a constant to indicate a medium priority
	 */
	public static final int MEDIUM_PRIORITY = HIGH_PRIORITY + 1;

	/**
	 *  a constant to indicate a low priority
	 */
	public static final int LOW_PRIORITY = MEDIUM_PRIORITY + 1;
	
	/**
	 *  a constant to used in the process of finding the highest priority queue item. 
	 *  It must be 1 more than the Low Priority.
	 */
	protected static final int COMPARISON_PRIORITY = LOW_PRIORITY + 1;
	

	/**
	 *   An icon that indicates an image on the queue
	 */
	private static final ImageIcon queueIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/queued_thumbnail.gif" ) );


	/**
	 *   An icon that indicates an image on the queue
	 */
	private static final ImageIcon largeFolderIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_folder_large.jpg" ) );


	/** 
	 *  This method puts a ThumbnailCreationRequest on the queue. 
	 *  If a suitable cached image exists it will be used instead.
	 *
	 *  @param	thumb	The Thumbnail which is to be loaded
	 *  @param	priority	The priority with which the rquest is to be treated on the queue
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
	 */
	public static void forceThumbnailCreation( Thumbnail thumb, int priority ) {
		requestThumbnailCreation( thumb, priority, true );
	}
	

	/** 
	 *  This method puts a ThumbnailCreationRequest on the queue. 
	 *
	 *  @param	thumb	The Thumbnail which is to be loaded
	 *  @param	priority	The priority with which the rquest is to be treated on the queue
	 *  @param	force		Set to true if the thumbnail needs to be rebuilt from source, false
	 *				if using a cached version is ok.
	 */
	public static void requestThumbnailCreation( Thumbnail thumb, int priority, boolean force ) {
		// prevent concurrent use of Thumbnail:
		synchronized( thumb ) {
			if ( thumb.referringNode == null ) {
				Tools.log( "ThumbnailCreationQueue.requestThumbnailCreation: referring node was null! How did this happen?");
				return;
			} else if ( thumb.referringNode.getUserObject() instanceof PictureInfo ) {
				thumb.setThumbnail( queueIcon );
			} else {
				thumb.setThumbnail( largeFolderIcon );
			}
		}
		thumbQueue.remove( thumb ); // remove it if it was there already
		thumbQueue.add( new ThumbnailQueueRequest ( thumb, priority, force ) );
	}



	/**
	 *   removes the specified Thumbnail from the queue.
	 *
	 *   @param  thumb  The thumbnail to be removed
	 */
	public static void remove( Thumbnail thumb ) {
		boolean notFound = true;
		ThumbnailQueueRequest test;
		Enumeration e = thumbQueue.elements();		
		while ( e.hasMoreElements() && notFound ) {
			test = (ThumbnailQueueRequest) e.nextElement();
			if ( test.getThumbnail() == thumb ) {
				thumbQueue.remove( test );
				notFound = false;
			}
		}
	}


	/**
	 *   removes all queue requests from the queue.
	 */
	public static void removeAll() {
		thumbQueue.removeAllElements();
	}


	/**
	 *  this method returns the highest priority request on the queue. If there
	 *  are no elements it returns null.
	 */
	public static ThumbnailQueueRequest getRequest() {
		ThumbnailQueueRequest req = null;
		ThumbnailQueueRequest test;
		int prio = COMPARISON_PRIORITY;
		Enumeration e = thumbQueue.elements();		
		while ( e.hasMoreElements() && ( prio > HIGH_PRIORITY ) ) {
			test = (ThumbnailQueueRequest) e.nextElement();
			if ( test.getPriority() < prio ) {
				req = test;
				prio = test.getPriority();
			}
		}
		thumbQueue.remove( req );
		/*if ( req == null )
			Tools.log("ThumbnailCreationQueue.getRequest: picking up a null thumbnail request.");
		else
			Tools.log("ThumbnailCreationQueue.getRequest: picking up thumbnail request: " + req.toString());*/
		return req;
	}


	/**
	 * returns the number of Requests currently on the queue
	 */
	public static int countQueueRequests() {
		return thumbQueue.size();
	}
			
}
