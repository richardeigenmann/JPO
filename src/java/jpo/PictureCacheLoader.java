package jpo;

import java.util.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.jnlp.*;
import javax.swing.tree.*;

/*
PictureCacheLoader.java:  class that manages the loading of pictures to the cache

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
 *  This is a class that is non static that can load imaged to the cache
 */
public class PictureCacheLoader implements SourcePictureListener {



	/** 
	 *  Create a PictureCacheLoader for the node supplied
	 *
	 *  @param	node	The node which holds the picture to be cached
	 *
	 */
	PictureCacheLoader ( SortableDefaultMutableTreeNode node ) {
		Object nodeUserObject = node.getUserObject();
		if ( nodeUserObject instanceof PictureInfo) {
			try {
				PictureInfo pi = (PictureInfo) nodeUserObject;
				load( pi.getHighresURL(), pi.getRotation() );
			} catch ( MalformedURLException x ) {
				Tools.log ("PictureCacheLoader.constructor: Caught a MalformedURLException when trying to cache the next image. Reason is: " + x.getMessage() );
			}
		}
	}

	/**
	 *  Create a PictureCacheLoader and supply the URL to be loaded.
	 */
	PictureCacheLoader ( URL url, double rotation ) {
		load ( url, rotation );
	}

	
	/**
	 *  loads a picture into the cache if the cache Size is equal or more to 1.
	 *  if the picture is already in the cache it is not loaded again.
	 */
	private void load( URL url, double rotation ) {	
		Tools.log("PictureCacheLoader.load: " + url.toString() );

		if ( ( Settings.maxCache < 1 )
		  || ( PictureCache.isInCache( url.toString() ) ) ) {
			Tools.log("PictureCache.load: picture not loaded because either maxCache < 1 or image already there");
			PictureCache.reportCache();
			return;
		}
			
		SourcePicture cachedPicture = new SourcePicture();
		cachedPicture.addListener( this );
		cachedPicture.loadPictureInThread( url, Thread.MIN_PRIORITY, rotation );
		PictureCache.add( url, cachedPicture );
		PictureCache.cacheLoadsInProgress.add( cachedPicture );
	}


	/**
	 *  in this method we get the information back from the backgroundloading
	 *  pictures as to what they have achieved.
	 */
	public synchronized void sourceStatusChange( int statusCode, String statusMessage, SourcePicture sp ) {
		Tools.log("PictureCacheLoader.sourceStatusChange: " + statusMessage);
		if ( statusCode == SourcePicture.ERROR ) {
			PictureCache.remove( sp.getUrlString() );
			PictureCache.cacheLoadsInProgress.remove( sp );
			sp.removeListener( this );
		} else if ( statusCode == SourcePicture.READY )  {
			PictureCache.cacheLoadsInProgress.remove( sp );
			Tools.log("PictureCacheLoader.sourceStatusChange: removing listener because of READY status on " + sp.getUrlString() );
			sp.removeListener( this );
		}
	}

	
	/**
	 *  defined to satisfy the interface
	 */
	public void sourceLoadProgressNotification( int statusCode, int percentage ) {
	}
	
}
