package jpo.gui;

import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.*;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import java.util.*;
import java.net.*;
import java.util.logging.Logger;


/*
PictureCache.java:  class that manages the cache of pictures
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
 *  class that manages the cache of pictures
 *
 **/
public class PictureCache {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( PictureCache.class.getName() );

    /**
     *  Defines the Hashtable that facilitate the caching of images. 
     *  It is static so that there is just one for the application
     */
    private static Hashtable<String, SourcePicture> pictureCache = new Hashtable<String, SourcePicture>();

    /**
     *  Vector to keep track of what we should remove first in the cache
     */
    private static Vector<String> removalQueue = new Vector<String>();


    /**
     *  this method removes the least popular picture(s) in the cache.
     *  It first removes those pictures which have been suggested for
     *  removal. And then it picks any it can find
     *  As many pictures are removed as necessary until there are less pictures in the 
     *  cache than the Settings.maxCache specifies. (If maxCache is 0 then the
     *  Enumeration finds no elements and we don't get an endless loop.
     */
    public static synchronized void removeLeastPopular() {
        //logger.info("PictureCache.removeLeastPopular:");
        //reportCache();

        Enumeration<String> e = removalQueue.elements();
        while ( ( e.hasMoreElements() ) && ( pictureCache.size() >= Settings.maxCache ) ) {
            String removeElement = e.nextElement();
            //logger.info ("PictureCache.remove: " + removeElement );
            pictureCache.remove( removeElement );
            removalQueue.remove( removeElement );
        }

        e = pictureCache.keys();
        while ( ( pictureCache.size() >= Settings.maxCache ) && ( e.hasMoreElements() ) ) {
            String removeElement = e.nextElement();
            //logger.info ("PictureCache.remove: " + removeElement );
            pictureCache.remove( removeElement );
        }
        System.gc();
        //reportCache();
    }


    /**
     *   Method that can be called when a picture is no longer needed.
     *   These pictures will be removed first from the cache when
     *   we need more space.
     * @param node
     */
    public static void suggestCacheRemoval( SortableDefaultMutableTreeNode node ) {
        if ( node == null ) {
            return;
        }
        Object userObject = node.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            try {
                removalQueue.add( ( (PictureInfo) userObject ).getHighresURL().toString() );
            } catch ( MalformedURLException x ) {
                // ignore
            }
        }
    }


    /**
     *  returns whether an image is in the cache. <p>
     * @param url 
     * @return
     */
    public static synchronized boolean isInCache( URL url ) {
        return isInCache( url.toString() );
    }


    /**
     *  returns whether an image is in the cache. <p>
     * @param urlString 
     * @return
     */
    public static synchronized boolean isInCache( String urlString ) {
        return pictureCache.containsKey( urlString );
    }


    /**
     *  store an image in the cache
     *  @param url	The URL of the picture
     *  @param sp	The picture to be stored
     */
    public static synchronized void add( URL url, SourcePicture sp ) {
        // logger.info("PictureCache.add: " + url.toString() );
        if ( sp.getSourceBufferedImage() == null ) {
            logger.info( "PictureCache.add: invoked with a null picture! Not cached! URL was: " + url.toString() );
            return;
        }

        if ( ( Settings.maxCache < 1 ) ) {
            //logger.info("PictureCache.add: cache is diabled. Not adding picture.");
            return;
        }

        if ( isInCache( url ) ) {
            //logger.info( "PictureCache.add: Picture " + url.toString() + " is already in the cache. Not adding again.");
            return;
        }

        if ( pictureCache.size() >= Settings.maxCache ) {
            removeLeastPopular();
        }

        if ( pictureCache.size() < Settings.maxCache ) {
            pictureCache.put( url.toString(), sp );
        }

        //reportCache();
    }


    /**
     *  remove a picture from the cache
     * @param urlString
     */
    public static synchronized void remove( String urlString ) {
        if ( isInCache( urlString ) ) {
            pictureCache.remove( urlString );
        }
    }


    /**
     *  returns a picture from the cache. Returns null if image is not there
     *  @param url 	The URL of the picture to be retrieved
     * @return
     */
    public static synchronized SourcePicture getSourcePicture( URL url ) {
        return pictureCache.get(url.toString());
    }


    /**
     *  clears out all images in the cache. Important after OutOfMemoryErrors
     */
    public static void clear() {
        // logger.info("PictureCache.clear: Zapping entire cache");
        pictureCache.clear();
        Tools.freeMem();
    }


    /**
     *  method to inspect the cache
     */
    public static void reportCache() {
        logger.info( "cache contains: " + Integer.toString( pictureCache.size() ) + " max: " + Integer.toString( Settings.maxCache ) );
        //Tools.freeMem();
        Enumeration<String> e = pictureCache.keys();
        while ( e.hasMoreElements() ) {
            logger.info( "   Cache contains: " +  e.nextElement() );
        }
        logger.info( "  End of cache contents" );
    }
}
