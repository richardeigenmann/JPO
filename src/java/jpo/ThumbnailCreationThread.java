package jpo;

import javax.swing.*;
import java.awt.Dimension;
import java.net.*;
import java.io.*;

/*
ThumbnailCreationThread.java:  A factory that creates thumbnails

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
 *  Queue that holds requests to create Thumbnails from Highres Images
 **/
public class ThumbnailCreationThread extends Thread {

	public boolean endThread = false;


	/**
	 *   An icon that indicates that the image is being loaded
	 */
	private static final ImageIcon loadingIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/loading_thumbnail.gif" ) );


	/**
	 *  Constructor
	 */
	ThumbnailCreationThread() {
		setPriority( Thread.MIN_PRIORITY );
		start();
	}
	
	
	
	public void run() {
		while ( ! endThread ) {
			ThumbnailQueueRequest req = ThumbnailCreationQueue.getRequest();
			if ( req == null ) {
				try {
					sleep( 500 );
				} catch ( InterruptedException x ) {
					// so we got interrupted?
				}
			} else {
				createThumbnail( req );
			}
		}
	}
	
	
	/**
	 * the method that does the dirty work.
	 *  @param  req		a reference to the Thumbnail for which we are creating the Thumbnail
	 */
	private static void createThumbnail ( ThumbnailQueueRequest req ) {
		Thumbnail currentThumb = req.getThumbnail();
		currentThumb.setThumbnail( loadingIcon );
		SortableDefaultMutableTreeNode referringNode = currentThumb.referringNode;
		if ( referringNode == null ) {
			Tools.log("ThumbnailCreationThread invoked on a null image!");
			loadBrokenThumbnailImage( currentThumb );
			return;
		}
			
		// validate we were called on the right type of node
		if ( ! ( referringNode.getUserObject() instanceof PictureInfo ) ) {
			loadBrokenThumbnailImage( currentThumb );
			return;
		}

		PictureInfo pi = (PictureInfo) referringNode.getUserObject();
		URL lowresUrl = null;
		
		if ( Settings.keepThumbnails ) {
			try {
				lowresUrl = pi.getLowresURL();
			} catch ( MalformedURLException x ) {
				Tools.log("Lowres URL was Malformed: " + pi.getLowresLocation() + "  Creating a new URL." );
				pi.setLowresLocation( Tools.lowresFilename() );
				try {
					lowresUrl = pi.getLowresURL();
				} catch ( MalformedURLException x1 ) {
					Tools.log( "The system is generating broken URL's! Aborting Thumbnail creation!");
					loadBrokenThumbnailImage( currentThumb );
					return;
				}
				createNewThumbnail( currentThumb );
				return;
			}
			// if we get here we have a good lowres URL
		}


		URL highresUrl = null;
		try {
			highresUrl = pi.getHighresURL();
		} catch ( MalformedURLException x ) {
			Tools.log("Highres URL was Malformed: " + pi.getHighresLocation() + "  Loading \"broken\" icon." );
			loadBrokenThumbnailImage( currentThumb );
			return;
		}

		// test if highres is readable
		try {
			InputStream highresStream = highresUrl.openStream();
			highresStream.close();
		} catch ( IOException x ) {
			// highres could not be opened
			loadBrokenThumbnailImage( currentThumb );
			return;
		}


		// Are be being requested to recreate the Thumbnail in any case?
		if ( req.getForce() ) {
			createNewThumbnail( currentThumb );
			return;
		}


		// test if lowres is readable
		if ( Settings.keepThumbnails ) {
			try {
				InputStream lowresStream = lowresUrl.openStream();
				lowresStream.close();
			} catch  ( IOException x ) {
				Tools.log("ThumbnailCreationThread.createThumbnail: is requesting the creation of a numbnail because if we can't open the lowres stream we should re-create the image.");
				createNewThumbnail( currentThumb );
				return;
			}
		
			// is lowres up to date?		
			try {
				URLConnection lowresUC = lowresUrl.openConnection();
				URLConnection highresUC = highresUrl.openConnection();
				long lowresModDate = lowresUC.getLastModified();
				long highresModDate = highresUC.getLastModified();
				lowresUC.getInputStream().close();
				highresUC.getInputStream().close();
			
				if ( lowresModDate < highresModDate) {
					Tools.log( "ThumbnailCreationThread.createThumbnail: is requesting the creation of a numbnail because Thumbnail is out of date: " + pi.getLowresLocation() );
					createNewThumbnail( currentThumb );
					return;
				}
			} catch  ( IOException x ) {
				//if we can't open the stream we should re-create the image
				createNewThumbnail( currentThumb );
				return;
			}
		} else {
			createNewThumbnail( currentThumb );
			return;
		}

		// Thumbnail up to date is size ok?
		ImageIcon icon = new ImageIcon( lowresUrl );
		if ((icon.getIconWidth() == currentThumb.thumbnailSize) || (icon.getIconHeight() == currentThumb.thumbnailSize)) {
			// all ist fine
			currentThumb.setThumbnail( icon );
		} else {
			Tools.log( "Thumbnail is wrong size: " + icon.getIconWidth()  + " x " +  icon.getIconHeight() );
			createNewThumbnail( currentThumb );
		}

		return;
	}


	/**
	 *  creates a thumbnail by loading the highres image and scaling it down
	 */
	public static void createNewThumbnail ( Thumbnail currentThumb ) {
		if ( currentThumb == null ) {
			Tools.log( "ThumbnailCreationThread.createNewThumbnail called with null parameter! Aborting.");
			return;
		}
		
		SortableDefaultMutableTreeNode referringNode = currentThumb.referringNode;
		Tools.log("ThumbnailCreationThread.createNewThumbnail: Creating Thumbnail " + ((PictureInfo) referringNode.getUserObject()).getLowresLocation() + " from " + ((PictureInfo) referringNode.getUserObject()).getHighresLocation());
		try {
			// create a new thumbnail from the highres
			ScalablePicture currentPicture = new ScalablePicture();
			currentPicture.setScaleSize(new Dimension( currentThumb.thumbnailSize, currentThumb.thumbnailSize));
			if ( Settings.thumbnailFastScale ) 
				currentPicture.setFastScale();
			else
				currentPicture.setQualityScale();


			PictureInfo pi = (PictureInfo) referringNode.getUserObject();
			currentPicture.loadPictureImd( pi.getHighresURL(), pi.getRotation() );
			
						
			//Tools.log(" ... scaling");
			currentPicture.scalePicture();


			if ( currentPicture.getScaledPicture() == null ) {
				Tools.log("There was a problem creating the thumbnail for: " + pi.getHighresURL() );
				loadBrokenThumbnailImage( currentThumb );
				return;
			}
			

			// is the thumbnail is not on the local filesystem then change the
			// url to be a local file or the write will fail.
			//Tools.log("Protocol: >" + pi.getLowresURL().getProtocol() + "<");
			//if ( ! pi.getLowresURL().getProtocol().equals("file") ) {
			if ( ! Tools.isUrlFile ( pi.getLowresURL() ) ) {
				Tools.log("The URL is not a file:// type. Getting new name. Type was: " + pi.getLowresURL().getProtocol().equals("file"));
				pi.setLowresLocation( Tools.lowresFilename() );
				referringNode.setUnsavedUpdates();
			} 
				
			
			
			//Tools.log(" ... writing: " + pi.getLowresLocation());
			if ( Settings.keepThumbnails ) {
				// Test that the file can be written to
				// Note that we are using files here because java doesn't want to let me use output streams on URL's.
				if ( ! pi.getLowresFile().exists() ) {
					// the file doesn't yet exist. Can we write to it?
					try {
						pi.getLowresFile().createNewFile();
					} catch (IOException x) {
						Tools.log("Lowres URL is not writable: " + pi.getLowresLocation() + " " + x.getMessage() + "  Creating a new URL." );
						pi.setLowresLocation( Tools.lowresFilename() );
						referringNode.setUnsavedUpdates();
					}
				} else  {
					// the file does exist, can we write to it?
					if ( ! pi.getLowresFile().canWrite() ) {
						Tools.log("Lowres URL is not writable: " + pi.getLowresLocation() + ".  Creating a new URL." );
						pi.setLowresLocation( Tools.lowresFilename() );
						referringNode.setUnsavedUpdates();
					}
				}
				currentPicture.writeScaledJpg( pi.getLowresFile() );
				pi.sendThumbnailChangedEvent();
			}

			ImageIcon icon = new ImageIcon( currentPicture.getScaledPicture() );
			currentThumb.setThumbnail( icon );
			
			// clean the cache
			ImageIcon cleanCache = new ImageIcon( pi.getLowresURLOrNull() );
			cleanCache.getImage().flush(); 
			
			//currentPicture = null;
		} catch ( IOException x ) {
			loadBrokenThumbnailImage( currentThumb );
		}
	}


	/**
	 *   An icon that indicates a broken image used when there is a 
	 *   problem rendering the correct thumbnail.
	 */
	private static final ImageIcon brokenThumbnailPicture = new ImageIcon( Settings.cl.getResource( "jpo/images/broken_thumbnail.gif" ) );

	
	/**
	 *  method that loads the brokenThumbnail image. Intended for use when the URLs are all messed up and no 
	 *  image is available
	 */
	private static void loadBrokenThumbnailImage( Thumbnail targetThumbnail ) {
		targetThumbnail.setThumbnail( brokenThumbnailPicture );
	}

	
	
}
