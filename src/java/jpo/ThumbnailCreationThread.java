package jpo;

import javax.swing.*;
import java.awt.Dimension;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;

/*
ThumbnailCreationThread.java:  A factory that creates thumbnails

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
 *  A thread that polls the static {@link ThumbnailCreationQueue} and then 
 *  creates thumbnails for the {@link ThumbnailQueueRequests} on the queue.
 **/
public class ThumbnailCreationThread extends Thread {


	/**
	 *   Flag to indicate that the thread should die.
	 */
	public boolean endThread = false;


	/**
	 *   An icon that indicates that the image is being loaded
	 */
	private static final ImageIcon loadingIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/loading_thumbnail.gif" ) );


	/**
	 *  Constructor that creates the thread. It creates the thread with a Thread.MIN_PRIOTITY priority
	 *  to ensure good overall response.
	 */
	ThumbnailCreationThread() {
		setPriority( Thread.MIN_PRIORITY );
		start();
	}
	
	
	/**
	 *  The run method for the thread that keeps checking whether there are any {@link ThumbnailQueueRequest} objects
	 *  on the queue to be rendered.
	 */
	public void run() {
		while ( ! endThread ) {
			ThumbnailQueueRequest req = ThumbnailCreationQueue.getRequest();
			if ( req == null ) {
				try {
					sleep( Settings.ThumbnailCreationThreadPollingTime );
				} catch ( InterruptedException x ) {
					// so we got interrupted?
				}
			} else {
				createThumbnail( req );
			}
		}
	}
	
	
	/**
	 *  This method picks up the thumbnail creation request, sets a loadingIcon and passes the 
	 *  request to the {@link #createPictureThumbnail} or the {@link #loadOrCreateGroupThumbnail} method.
	 *
	 *  @param  req		the {@link ThumbnailQueueRequest} for which to create the Thumbnail
	 */
	private void createThumbnail ( ThumbnailQueueRequest req ) {
		Thumbnail currentThumb = req.getThumbnail();
		// now block other threads from accessing the Thumbnail
		synchronized ( currentThumb ) {
			currentThumb.setThumbnail( loadingIcon );
			SortableDefaultMutableTreeNode referringNode = currentThumb.referringNode;
			if ( referringNode == null ) {
				Tools.log("ThumbnailCreationThread.createThumbnail: referringNode was null! Setting Broken Image.\nThis happened on ThumbnailQueueRequest: " + req.toString() + " which refers to Thumbnail: " + currentThumb.toString() );
				loadBrokenThumbnailImage( currentThumb );
				return;
			}
			
			// validate we were called on the right type of node
			if ( referringNode.getUserObject() instanceof PictureInfo ) {
				loadOrCreatePictureThumbnail( req );
			} else if ( referringNode.getUserObject() instanceof GroupInfo ) {
				loadOrCreateGroupThumbnail( req );
			} else {
				loadBrokenThumbnailImage( currentThumb );
				return;
			}
		}
	}



	/**
	 *   This method tries to find out if a lowres image already exists and 
	 *   loads it if matches some criteria so. If there are problems it loads 
	 *   the broken thumbnail image. If the Highres needs to be loaded and 
	 *   scaled down it calls createNewThumbnail().
	 *
	 *   @param req 	the ThumbnailQueueRequest for which to create the Thumbnail
	 */
	private void loadOrCreatePictureThumbnail (  ThumbnailQueueRequest req ) {
		if ( req == null ) { Tools.log("ThumbnailCreationThread.createPictureThumbnail: invoked with a null request. Aborting."); return; }
		Thumbnail currentThumb = req.getThumbnail();
		if ( currentThumb == null ) { Tools.log("ThumbnailCreationThread.createPictureThumbnail: invoked request with a null Thumbnail. Aborting."); return; }
		PictureInfo pi = (PictureInfo) currentThumb.referringNode.getUserObject();
		if ( pi == null ) { Tools.log("ThumbnailCreationThread.createPictureThumbnail: could not find PictureInfo. Aborting."); return; }
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
			highresUrl.openStream().close();
		} catch ( IOException x ) {
			// highres could not be opened
			// can we read the lowres instead?
			try { 
				lowresUrl.openStream().close();
				ImageIcon icon = new ImageIcon( lowresUrl );
				currentThumb.setThumbnail( icon );
				return;
			} catch ( IOException ioe ) {
				// we have nothing to display				
				loadBrokenThumbnailImage( currentThumb );
				return;
			}
		}


		// Are we being requested to recreate the Thumbnail in any case?
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
		Tools.log("ThubnailCreationThread.loadOrCreatePictureThumbnail: Thumbnail is up to date. Checking size");
		ImageIcon icon = new ImageIcon( lowresUrl );
		if ( isThumbnailSizeOk( new Dimension( icon.getIconWidth(), icon.getIconHeight() ),
	  	      currentThumb.getPreferredSize() ) ) {
			// all ist fine
			currentThumb.setThumbnail( icon );
			currentThumb.validate();
		} else {
			Tools.log( "ThumbnailCreationThread.createPictureThumbnail: Thumbnail is wrong size: " + icon.getIconWidth()  + " x " +  icon.getIconHeight() + " therefore thrown on queue");
			createNewThumbnail( currentThumb );
		}

		return;
	}


	/**
	 *  This method returns whether the dimension of the icon are within the tolerance of the 
	 *  desired dimension.
	 *
	 *  @return   	true if inside dimension, false if outside.
	 */
	private boolean isThumbnailSizeOk( Dimension iconDimension, Dimension desiredDimension ) {
		final float tolerance = 1.02f; 
		return ( //the thumbnail is within the tolerance
  		     (  ( iconDimension.width > desiredDimension.width / tolerance ) 
		     && ( iconDimension.width < desiredDimension.width * tolerance ) )
		   || ( ( iconDimension.height > desiredDimension.height / tolerance ) 
		     && ( iconDimension.height < desiredDimension.height * tolerance ) ) 
		   || //the original could be small. Problem: how to get the orignial size quickly here?
		     (	Settings.dontEnlargeSmallImages	
		     && ( ( iconDimension.width < desiredDimension.width * tolerance )
		       || ( iconDimension.height < desiredDimension.height * tolerance ) )
		     && (  iconDimension.width > 1 )
		     && (  iconDimension.height > 1 )
		     )
		) ;
	}


	/**
	 *  creates a thumbnail by loading the highres image and scaling it down
	 */
	private void createNewThumbnail ( Thumbnail currentThumb ) {
		SortableDefaultMutableTreeNode referringNode = null;
		if ( currentThumb == null ) {
			Tools.log( "ThumbnailCreationThread.createNewThumbnail called with null parameter! Aborting.");
			return;
		}
	
		referringNode = currentThumb.referringNode;
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
			if ( ! Tools.isUrlFile ( pi.getLowresURL() ) ) {
				Tools.log("The URL is not a file:// type. Getting new name. Type was: " + pi.getLowresURL().getProtocol().equals("file"));
				pi.setLowresLocation( Tools.lowresFilename() );
				referringNode.getPictureCollection().setUnsavedUpdates();
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
						referringNode.getPictureCollection().setUnsavedUpdates();
					}
				} else  {
					// the file does exist, can we write to it?
					if ( ! pi.getLowresFile().canWrite() ) {
						Tools.log("Lowres URL is not writable: " + pi.getLowresLocation() + ".  Creating a new URL." );
						pi.setLowresLocation( Tools.lowresFilename() );
						referringNode.getPictureCollection().setUnsavedUpdates();
					}
				}
				currentPicture.writeScaledJpg( pi.getLowresFile() );

				// clean the cache
				ImageIcon cleanCache = new ImageIcon( pi.getLowresURLOrNull() );
				cleanCache.getImage().flush(); 

				pi.sendThumbnailChangedEvent();
			}

			if ( ! Settings.keepThumbnails ) {
				// This branch is nescessary because it sets the thumbnail only when the
				// Thumbnail is not written to disk. Where it is written to disk the
				// sent ThumbnailChangedEvent ensures that the new image is loaded.
				ImageIcon icon = new ImageIcon( currentPicture.getScaledPicture() );
				if ( ( currentThumb.referringNode != null )
				  && ( currentThumb.referringNode == referringNode ) ) {
				  	// could have changed in the mean time
					currentThumb.setThumbnail( icon );
				}
			}
		} catch ( IOException x ) {
			loadBrokenThumbnailImage( currentThumb );
		}
	}



	/**
	 *   This method looks at the ThumbnailQueueRequest and figures out if there is a 
	 *   suitable disk based thumbnail for the group that can be displayed. If there isn't it
	 *   has a new thumbnail created.
	 */
	private void loadOrCreateGroupThumbnail (  ThumbnailQueueRequest req ) {
		Thumbnail currentThumb = req.getThumbnail();
		GroupInfo gi = (GroupInfo) currentThumb.referringNode.getUserObject();
		URL lowresUrl = null;
		
		if ( Settings.keepThumbnails ) {
			try {
				lowresUrl = gi.getLowresURL();
			} catch ( MalformedURLException x ) {
				Tools.log("Lowres URL was Malformed: " + gi.getLowresLocation() + "  Creating a new URL." );
				gi.setLowresLocation( Tools.lowresFilename() );
				try {
					lowresUrl = gi.getLowresURL();
				} catch ( MalformedURLException x1 ) {
					Tools.log( "The system is generating broken URL's! Aborting Thumbnail creation!");
					loadBrokenThumbnailImage( currentThumb );
					return;
				}
				createNewGroupThumbnail( currentThumb );
				return;
			}
			// if we get here we have a good lowres URL
		}

		// Are we being requested to recreate the Thumbnail in any case?
		if ( req.getForce() ) {
			createNewGroupThumbnail( currentThumb );
			return;
		}


		// test if lowres is readable
		if ( Settings.keepThumbnails ) {
			try {
				InputStream lowresStream = lowresUrl.openStream();
				lowresStream.close();
			} catch  ( IOException x ) {
				Tools.log("ThumbnailCreationThread.loadOrCreateGroupThumbnail: is requesting the creation of a numbnail because if we can't open the lowres stream we should re-create the image.");
				createNewGroupThumbnail( currentThumb );
				return;
			}
		} else {
			createNewGroupThumbnail( currentThumb );
			return;
		}

		// Thumbnail up to date is size ok?
		ImageIcon icon = new ImageIcon( lowresUrl );
		if ( isThumbnailSizeOk( new Dimension( icon.getIconWidth(), icon.getIconHeight() ),
		     currentThumb.getPreferredSize() ) ) {
			// all ist fine
			currentThumb.setThumbnail( icon );
		} else {
			Tools.log( "ThumbnailCreationThread.loadOrCreateGroupThumbnail: Thumbnail is wrong size: " + icon.getIconWidth()  + " x " +  icon.getIconHeight() );
			createNewGroupThumbnail( currentThumb );
		}

		return;
	}



	/**
	 *  Create a Group Thumbnail by loading the nodes component images and creating a folder icon with embeded images
	 */
	private void createNewGroupThumbnail ( Thumbnail currentThumb ) {
		SortableDefaultMutableTreeNode referringNode = null;
		if ( currentThumb == null ) {
			Tools.log( "ThumbnailCreationThread.createNewGroupThumbnail called with null parameter! Aborting.");
			return;
		}
		
		referringNode = currentThumb.referringNode;
		Tools.log("ThumbnailCreationThread.createNewGroupThumbnail: Creating Thumbnail " + ((GroupInfo) referringNode.getUserObject()).getLowresLocation() + " from " + ((GroupInfo) referringNode.getUserObject()).getLowresLocation());

		try{
			BufferedImage groupThumbnail = ImageIO.read( new BufferedInputStream( ThumbnailCreationThread.class.getResourceAsStream( "images/icon_folder_large.jpg" ) ) );
			Graphics2D groupThumbnailGraphics = groupThumbnail.createGraphics();
		
			int leftMargin = 15;
			int margin = 10;
			int topMargin = 65;
			int horizontalPics = ( groupThumbnail.getWidth() - leftMargin ) / ( Settings.miniThumbnailSize.width + margin );
			int verticalPics = ( groupThumbnail.getHeight() - topMargin ) / ( Settings.miniThumbnailSize.height + margin );
			int numberOfPics = horizontalPics * verticalPics;
			
			Object userObject;
			int numberOfChildNodes = referringNode.getChildCount();
			int x, y;
			int yPos;
			ScalablePicture sclPic = new ScalablePicture();
			PictureInfo pi;
			URL lowresUrl;
			int childIndex = 0;
			for ( int picsProcessed = 0; ( picsProcessed < numberOfPics ) && ( childIndex < numberOfChildNodes ); picsProcessed++ ) {
				do {
					userObject = ((SortableDefaultMutableTreeNode) referringNode.getChildAt( childIndex )).getUserObject();
					childIndex++;
				} while  ( ( ! ( userObject instanceof PictureInfo ) ) && ( childIndex < numberOfChildNodes ) );
				
				if ( ( userObject instanceof PictureInfo ) && ( childIndex <= numberOfChildNodes ) ) {
					x = margin + ( ( picsProcessed % horizontalPics ) * ( Settings.miniThumbnailSize.width + margin ) );
					yPos =  (int) Math.round( ((double) picsProcessed / (double) horizontalPics) - 0.5f );
					y = topMargin + ( yPos * ( Settings.miniThumbnailSize.height + margin ) );
					//Tools.log(Integer.toString(picsProcessed) +": " + Integer.toString(x) + "/" +Integer.toString(y)+ " - " + Integer.toString(yPos) );
	
					pi = (PictureInfo) userObject;
					Tools.log("Loading picture: " + pi.getDescription() + " Filename: " + pi.getLowresFilename() );
					try {
						lowresUrl = pi.getLowresURL();
					} catch ( MalformedURLException mue ) {
						Tools.log("Lowres URL was Malformed: " + pi.getLowresLocation() );
						continue;
					}

					try {
						//Tools.log( "Trying to load Thumbnail for Miniicon" );
						lowresUrl.openStream().close();
						sclPic.loadPictureImd( lowresUrl, 0 );
					} catch ( IOException ioe ) {
						// Tools.log( "Thumbnail failed. Loading Highres for Miniicon" );
						sclPic.loadPictureImd( pi.getHighresURL(), pi.getRotation() );
					}
					
					
					sclPic.setScaleSize( Settings.miniThumbnailSize );
					sclPic.scalePicture();
					x += ( Settings.miniThumbnailSize.width - sclPic.getScaledWidth() ) / 2;
					y += Settings.miniThumbnailSize.height - sclPic.getScaledHeight();
					
					groupThumbnailGraphics.drawImage( sclPic.getScaledPicture(),  x,  y, null );
				}
			}


			//Tools.log(" ... writing: " + pi.getLowresLocation());
			if ( Settings.keepThumbnails ) {
				// Test that the file can be written to
				// Note that we are using files here because java doesn't want to let me use output streams on URL's.
				GroupInfo gi = (GroupInfo) referringNode.getUserObject();
				if ( ! gi.getLowresFile().exists() ) {
					// the file doesn't yet exist. Can we write to it?
					try {
						gi.getLowresFile().createNewFile();
					} catch (IOException ioe) {
						Tools.log("Lowres URL is not writable: " + gi.getLowresLocation() + " " + ioe.getMessage() + "  Creating a new URL." );
						gi.setLowresLocation( Tools.lowresFilename() );
						referringNode.getPictureCollection().setUnsavedUpdates();
					}
				} else  {
					// the file does exist, can we write to it?
					if ( ! gi.getLowresFile().canWrite() ) {
						Tools.log("Lowres URL is not writable: " + gi.getLowresLocation() + ".  Creating a new URL." );
						gi.setLowresLocation( Tools.lowresFilename() );
						referringNode.getPictureCollection().setUnsavedUpdates();
					}
				}
				ScalablePicture.writeJpg( gi.getLowresFile(), groupThumbnail, 0.8f );

				// clean the cache
				ImageIcon cleanCache = new ImageIcon( gi.getLowresURLOrNull() );
				cleanCache.getImage().flush(); 

				referringNode.getPictureCollection().getTreeModel().nodeChanged( referringNode );
				//pi.sendThumbnailChangedEvent();
			}


			if ( ( currentThumb.referringNode != null )
			  && ( currentThumb.referringNode == referringNode ) ) {
			  	// in the meantime it might be displaying something completely else
				currentThumb.setThumbnail( new ImageIcon( groupThumbnail ) );
			}
		} catch ( IOException x ) {
			Tools.log ("ThumbnailCreationThread.createNewGroupThumbnail: caught an IOException: " + x.getMessage());
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
	 *  image is available. Use the other method as this checks if the node was updated in the meantime.
	 *  @deprecated
	 */
	private static void loadBrokenThumbnailImage( Thumbnail targetThumbnail ) {
		targetThumbnail.setThumbnail( brokenThumbnailPicture );
	}


	/**
	 *  method that loads the brokenThumbnail image. Intended for use when the URLs are all messed up and no 
	 *  image is available
	 */
	private static void loadBrokenThumbnailImage( Thumbnail targetThumbnail, SortableDefaultMutableTreeNode intendedTarget ) {
		synchronized ( targetThumbnail ) {
			if ( ( targetThumbnail.referringNode != null )
			  && ( targetThumbnail.referringNode == intendedTarget ) ) {
				targetThumbnail.setThumbnail( brokenThumbnailPicture );
			}
		}
	}

	
	
}
