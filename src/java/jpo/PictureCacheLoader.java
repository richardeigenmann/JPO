package jpo;

import java.util.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import javax.jnlp.*;
import javax.swing.tree.*;

/*
PictureCacheLoader.java:  class that manages the loading of pictures to the cache

Copyright (C) 2002-2008  Richard Eigenmann.
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
 *  The purpose for this class is to have a tool that will load SourcePictures 
 *  into the PictureCache.  
 */
public class PictureCacheLoader implements SourcePictureListener {



	/** 
	 *  Create a PictureCacheLoader for the node supplied
	 *
	 *  @param	node	The node which holds the picture to be cached
	 *
	 */
	public PictureCacheLoader ( SortableDefaultMutableTreeNode node ) {
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
	 *  loads a picture into the cache if the cache size is equal or more to 1.
	 *  if the picture is already in the cache it is not loaded again.
	 */
	private void load( URL url, double rotation ) {	
		//Tools.log("PictureCacheLoader.load: " + url.toString() );

		if ( ( Settings.maxCache < 1 )
		  || ( PictureCache.isInCache( url.toString() ) ) ) {
			//Tools.log("PictureCache.load: picture not loaded because either maxCache < 1 or image already there");
			// PictureCache.reportCache();
			return;
		}
			
		SourcePicture cachedPicture = new SourcePicture();
		cachedPicture.addListener( this );
		cachedPicture.loadPictureInThread( url, Thread.MIN_PRIORITY, rotation );
		PictureCache.add( url, cachedPicture );
		cacheLoadsInProgress.add( cachedPicture );
	}


        /**
     *  This Vector keeps track of which pictures the PictureCache has been
     *  requested to load in the background. They may have to be stopped in 
     *  a hurry.
     */
    public static Vector cacheLoadsInProgress = new Vector();

    /** 
     * method to stop all background loading
     */
    public static void stopBackgroundLoading() {
        Enumeration e = cacheLoadsInProgress.elements();
        while ( e.hasMoreElements() ) {
            ( (SourcePicture) e.nextElement() ).stopLoading();
        }
        //cacheLoadsInProgress.clear();  // handled by the PictureCacheLoader which gets an ERROR status from the SourcePicture.
    }

    /** 
     *  method to stop all background loading except the indicated file. Returns whether the
     *  image is already being loaded. True = loading in progress, False = not in progress.
     */
    public static boolean stopBackgroundLoadingExcept( URL exemptionURL ) {
        SourcePicture sp;
        String exemptionURLString = exemptionURL.toString();
        Enumeration e = cacheLoadsInProgress.elements();
        boolean inProgress = false;
        while ( e.hasMoreElements() ) {
            sp = ( (SourcePicture) e.nextElement() );
            if ( !sp.getUrlString().equals( exemptionURLString ) ) {
                sp.stopLoading();
            } else {
                Tools.log( "PictureCache.stopBackgroundLoading: picture was already loading" );
                inProgress = true;
            }
        }
        return inProgress;
    }
        
        
	/**
	 *  in this method we get the information back from the background loading
	 *  pictures as to what they have achieved.
	 */
	public synchronized void sourceStatusChange( int statusCode, String statusMessage, SourcePicture sp ) {
		//Tools.log("PictureCacheLoader.sourceStatusChange: " + statusMessage);
		if ( statusCode == SourcePicture.ERROR ) {
			PictureCache.remove( sp.getUrlString() );
			cacheLoadsInProgress.remove( sp );
			sp.removeListener( this );
		} else if ( statusCode == SourcePicture.READY )  {
			cacheLoadsInProgress.remove( sp );
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
