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
		if ( referringNode.getUserObject() instanceof PictureInfo ) {
			createPictureThumbnail( req );
		} else if ( referringNode.getUserObject() instanceof GroupInfo ) {
			loadOrCreateGroupThumbnail( req );
		} else {
			loadBrokenThumbnailImage( currentThumb );
			return;
		}		
	}


	private static void createPictureThumbnail (  ThumbnailQueueRequest req ) {
		Thumbnail currentThumb = req.getThumbnail();
		PictureInfo pi = (PictureInfo) currentThumb.referringNode.getUserObject();
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
		ImageIcon icon = new ImageIcon( lowresUrl );
		// bug when doesn't scale to exact size --> tolerance
		final float tolerance = 1.02f; 
		if ( //the thumbnail is within the tolerance
  		     (  ( icon.getIconWidth() > currentThumb.thumbnailSize / tolerance ) 
		     && ( icon.getIconWidth() < currentThumb.thumbnailSize * tolerance ) )
		   || ( ( icon.getIconHeight() > currentThumb.thumbnailSize / tolerance ) 
		     && ( icon.getIconHeight() < currentThumb.thumbnailSize * tolerance ) ) 
		   || //the original could be small. Problem: how to get the orignial size quickly here?
		     (	Settings.dontEnlargeSmallImages	
		     && ( ( icon.getIconWidth() < currentThumb.thumbnailSize * tolerance )
		       || ( icon.getIconHeight() < currentThumb.thumbnailSize * tolerance ) )
		     && (  icon.getIconWidth() > 1 )
		     && (  icon.getIconHeight() > 1 )
		     )
		 ) {
			// all ist fine
			currentThumb.setThumbnail( icon );
		} else {
			Tools.log( "ThumbnailCreationThread.createPictureThumbnail: Thumbnail is wrong size: " + icon.getIconWidth()  + " x " +  icon.getIconHeight() + " therefore thrown on queue");
			createNewThumbnail( currentThumb );
		}

		return;
	}



	/**
	 *  creates a thumbnail by loading the highres image and scaling it down
	 */
	public static void createNewThumbnail ( Thumbnail currentThumb ) {
		SortableDefaultMutableTreeNode referringNode = null;
		synchronized ( currentThumb ) {
			if ( currentThumb == null ) {
				Tools.log( "ThumbnailCreationThread.createNewThumbnail called with null parameter! Aborting.");
				return;
			}
		
			referringNode = currentThumb.referringNode;
			Tools.log("ThumbnailCreationThread.createNewThumbnail: Creating Thumbnail " + ((PictureInfo) referringNode.getUserObject()).getLowresLocation() + " from " + ((PictureInfo) referringNode.getUserObject()).getHighresLocation());
		}
		
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
				synchronized ( currentThumb ) {
					if ( ( currentThumb.referringNode != null )
					  && ( currentThumb.referringNode == referringNode ) ) {
					  	// could have changed in the mean time
						currentThumb.setThumbnail( icon );
					}
				}
			}
			
			//currentPicture = null;
		} catch ( IOException x ) {
			loadBrokenThumbnailImage( currentThumb );
		}
	}



	/**
	 *   This method looks at the ThumbnailQueueRequest and figures out if there is a 
	 *   suitable disk based thumbnail for the group that can be displayed. If there isn't it
	 *   has a new thumbnail created.
	 */
	private static void loadOrCreateGroupThumbnail (  ThumbnailQueueRequest req ) {
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
		if ((icon.getIconWidth() == currentThumb.thumbnailSize) || (icon.getIconHeight() == currentThumb.thumbnailSize)) {
			// all ist fine
			currentThumb.setThumbnail( icon );
		} else {
			Tools.log( "Thumbnail is wrong size: " + icon.getIconWidth()  + " x " +  icon.getIconHeight() );
			createNewGroupThumbnail( currentThumb );
		}

		return;
	}



	/**
	 *  creates a thumbnail by loading the highres image and scaling it down
	 */
	public static void createNewGroupThumbnail ( Thumbnail currentThumb ) {
		SortableDefaultMutableTreeNode referringNode = null;
		synchronized ( currentThumb ) {
			if ( currentThumb == null ) {
				Tools.log( "ThumbnailCreationThread.createNewGroupThumbnail called with null parameter! Aborting.");
				return;
			}
		
			referringNode = currentThumb.referringNode;
			Tools.log("ThumbnailCreationThread.createNewGroupThumbnail: Creating Thumbnail " + ((GroupInfo) referringNode.getUserObject()).getLowresLocation() + " from " + ((GroupInfo) referringNode.getUserObject()).getLowresLocation());
		}

		try{
			//ImageIO.setUseCache( false );
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
						referringNode.setUnsavedUpdates();
					}
				} else  {
					// the file does exist, can we write to it?
					if ( ! gi.getLowresFile().canWrite() ) {
						Tools.log("Lowres URL is not writable: " + gi.getLowresLocation() + ".  Creating a new URL." );
						gi.setLowresLocation( Tools.lowresFilename() );
						referringNode.setUnsavedUpdates();
					}
				}
				ScalablePicture.writeJpg( gi.getLowresFile(), groupThumbnail, 0.8f );

				// clean the cache
				ImageIcon cleanCache = new ImageIcon( gi.getLowresURLOrNull() );
				cleanCache.getImage().flush(); 

				referringNode.getTreeModel().nodeChanged( referringNode );
				//pi.sendThumbnailChangedEvent();
			}


			synchronized ( currentThumb ) {
				if ( ( currentThumb.referringNode != null )
				  && ( currentThumb.referringNode == referringNode ) ) {
				  	// in the meantime it might be displaying something completely else
					currentThumb.setThumbnail( new ImageIcon( groupThumbnail ) );
				}
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
