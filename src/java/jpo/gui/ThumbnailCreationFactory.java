package jpo.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import jpo.cache.ImageBytes;
import jpo.cache.JpoCache;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;

/*
 ThumbnailCreationFactory.java:  A factory that creates thumbnails

 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
 * Implementations of this class become a Thread that poll the
 * {@link ThumbnailCreationQueue} for new {@link ThumbnailQueueRequest} and
 * process them.
 *
 */
public class ThumbnailCreationFactory implements Runnable {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ThumbnailCreationFactory.class.getName() );
    //{ LOGGER.setLevel( Level.ALL ); }

    /**
     * Flag to indicate that the thread should die.
     */
    public boolean endThread = false;

    /**
     * Constructor that creates the thread. It creates the thread with a
     * Thread.MIN_PRIOTITY priority to ensure good overall response.
     */
    public ThumbnailCreationFactory() {
        Thread thread = new Thread( this );
        thread.setPriority( Thread.MIN_PRIORITY );
        thread.start();
    }

    /**
     * The run method for the thread that keeps checking whether there are any
     * {@link ThumbnailQueueRequest} objects on the queue to be rendered.
     */
    @Override
    public void run() {
        while ( !endThread ) {
            ThumbnailQueueRequest req = ThumbnailCreationQueue.poll();
            if ( req == null ) {
                try {
                    Thread.sleep( Settings.ThumbnailCreationThreadPollingTime );
                } catch ( InterruptedException x ) {
                    // so we got interrupted? Don' care.
                }
            } else {
                processQueueRequest( req );
            }
        }
    }

    /**
     * Handles the queue request by placing a synchronized lock on the Thumbnail
     * Controller and passes the request to the {@link #processPictureRequest}
     * or the {@link #processGroupRequest} method.
     *
     * @param request	the {@link ThumbnailQueueRequest} for which to create the
     * ThumbnailController
     */
    private void processQueueRequest( ThumbnailQueueRequest request ) {
        ThumbnailController thumbnailController = request.getThumbnailController();

        // now block other threads from accessing the ThumbnailController
        synchronized ( thumbnailController ) {
            Object userObject = thumbnailController.getNode().getUserObject();
            if ( userObject instanceof PictureInfo ) {
                processPictureRequest( request );
            } else if ( userObject instanceof GroupInfo ) {
                processGroupRequest( request );
            }
        }
    }

    /**
     * This method tries to find out if a lowres image already exists and loads
     * it if matches some criteria so. If there are problems it loads the broken
     * thumbnail image. If the Highres needs to be loaded and scaled down it
     * calls createNewThumbnail().
     *
     * @param req the ThumbnailQueueRequest for which to create the
     * ThumbnailController
     */
    private void processPictureRequest( ThumbnailQueueRequest req ) {
        LOGGER.fine( String.format( "Processing Queue Request details: %s", req.toString() ) );
        ThumbnailController thumbnailController = req.getThumbnailController();
        if ( thumbnailController == null ) {
            LOGGER.info( "Invoked request with a null Thumbnail. Aborting." );
            return;
        }
        PictureInfo pi = (PictureInfo) thumbnailController.getNode().getUserObject();
        if ( pi == null ) {
            LOGGER.info( "Could not find PictureInfo. Aborting." );
            return;
        }
        
        try {
            ImageBytes imageBytes = JpoCache.getInstance().getThumbnailImageBytes(
                    pi.getHighresURL(),
                    pi.getRotation(),
                    thumbnailController.getMaximumUnscaledSize().width,
                    thumbnailController.getMaximumUnscaledSize().height );
            ImageIcon icon = new ImageIcon( imageBytes.getBytes() );
            thumbnailController.getThumbnail().setImageIcon( icon );
        } catch ( IOException ex ) {
            Logger.getLogger( ThumbnailCreationFactory.class.getName() ).log( Level.SEVERE, null, ex );
            thumbnailController.setBrokenIcon();
        }
        if ( pi != null ) {
            return;
        }
        LOGGER.info( "Didn't want to get here" );
        
        
        
        URL lowresUrl = null;
        
        if ( Settings.keepThumbnails ) {
            try {
                lowresUrl = pi.getLowresURL();
            } catch ( MalformedURLException x ) {
                LOGGER.info( x.getMessage() );
                pi.setLowresLocation( Tools.getNewLowresFilename() );
                try {
                    lowresUrl = pi.getLowresURL();
                } catch ( MalformedURLException x1 ) {
                    LOGGER.info( "The system is generating broken URL's! Aborting Thumbnail creation!" );
                    thumbnailController.setBrokenIcon();
                    return;
                }
                createNewPictureThumbnail( thumbnailController );
                return;
            }
            // if we get here we have a good lowres URL
        }

        // Are we being requested to recreate the ThumbnailController in any case?
        if ( req.getForce() ) {
            createNewPictureThumbnail( thumbnailController );
            return;
        }

        // test if lowres is readable
        if ( Settings.keepThumbnails ) {
            try {
                InputStream lowresStream = lowresUrl.openStream();
                lowresStream.close();
            } catch ( IOException x ) {
                LOGGER.fine( "Requesting the creation of a thumbnail because if we can't open the lowres stream we should re-create the image. Actually an IOException: " + x.getMessage() );
                createNewPictureThumbnail( thumbnailController );
                return;
            }
            
            URL highresUrl;
            try {
                highresUrl = pi.getHighresURL();
            } catch ( MalformedURLException x ) {
                LOGGER.info( x.getLocalizedMessage() );
                thumbnailController.setBrokenIcon();
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
                    thumbnailController.getThumbnail().setImageIcon( icon );
                    return;
                } catch ( IOException x2 ) {
                    // we have nothing to display
                    thumbnailController.setBrokenIcon();
                    return;
                }
            }

            // is lowres up to date?
            try {
                URLConnection lowresUC = lowresUrl.openConnection();
                URLConnection highresUC = highresUrl.openConnection();
                long lowresModDate = lowresUC.getLastModified();
                long highresModDate = highresUC.getLastModified();
                lowresUC.getInputStream().close();
                highresUC.getInputStream().close();

                /* Tom Hoogenboom noticed that we create an infinite loop in cases where the 
                 * highres modification date lies in the future which is why we ignore those
                 * pics with a future modification date. It doesn't seem right that the filesystem
                 * would allow this but since it does we have to deal with it.
                 * TODO: Why is there be a loop? This must mean the request is being thrown on the queue again and again?
                 */
                if ( lowresModDate < highresModDate && highresModDate < System.currentTimeMillis() ) {
                    LOGGER.fine( String.format( "Highres %s is newer then Thumbnail %s Highres modification: %s Lowres modification: %s", highresUrl.toString(), lowresUrl.toString(), getDate( highresModDate, "dd/MM/yyyy hh:mm:ss.SSS" ), getDate( lowresModDate, "dd/MM/yyyy hh:mm:ss.SSS" ) ) );
                    createNewPictureThumbnail( thumbnailController );
                    return;
                }
            } catch ( IOException x ) {
                //if we can't open the stream we should re-create the image
                createNewPictureThumbnail( thumbnailController );
                return;
            }
        } else {
            createNewPictureThumbnail( thumbnailController );
            return;
        }

        // ThumbnailController up to date is size ok?
        LOGGER.fine( "Thumbnail is current. Checking size..." );
        ImageIcon icon = new ImageIcon( lowresUrl );
        if ( isThumbnailSizeOk( new Dimension( icon.getIconWidth(), icon.getIconHeight() ),
                thumbnailController.getMaximumUnscaledSize() ) ) {
            // all ist fine
            thumbnailController.getThumbnail().setImageIcon( icon );
        } else {
            LOGGER.info( "Thumbnail is wrong size: " + icon.getIconWidth() + " x " + icon.getIconHeight() + " therefore thrown on queue" );
            createNewPictureThumbnail( thumbnailController );
        }
    }

    /**
     * Return date in specified format.
     *
     * @param milliSeconds Date in milliseconds
     * @param dateFormat Date format
     * @return String representing date in specified format
     */
    private static String getDate( long milliSeconds, String dateFormat ) {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat( dateFormat );

        // Create a calendar object that will convert the date and time value in milliseconds to date. 
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( milliSeconds );
        return formatter.format( calendar.getTime() );
    }

    /**
     * This method returns whether the dimension of the icon are within the
     * tolerance of the desired dimension.
     *
     * @return true if inside dimension, false if outside.
     */
    private boolean isThumbnailSizeOk( Dimension iconDimension,
            Dimension desiredDimension ) {
        final float tolerance = 1.02f;
        //logger.info("ThumbnailCreationFactory.isThumbnailSizeOk: called with check dimension: " + iconDimension.toString() + " desiredDimension: " + desiredDimension.toString() );
        boolean widthOk = ( iconDimension.width > desiredDimension.width / tolerance ) && ( iconDimension.width < desiredDimension.width * tolerance );
        boolean heightOk = ( iconDimension.height > desiredDimension.height / tolerance ) && ( iconDimension.height < desiredDimension.height * tolerance );
        if ( widthOk || heightOk ) {
            return true;
        }

        //the original could be small. Problem: how to get the orignial size quickly here?
        if ( Settings.dontEnlargeSmallImages && ( ( iconDimension.width < desiredDimension.width * tolerance ) || ( iconDimension.height < desiredDimension.height * tolerance ) ) && ( iconDimension.width > 1 ) && ( iconDimension.height > 1 ) ) {
            return true;
        }
        
        return false;
    }

    /**
     * creates a thumbnail by loading the highres image and scaling it down
     */
    private void createNewPictureThumbnail( ThumbnailController thumbnailController ) {
        
        SortableDefaultMutableTreeNode referringNode = thumbnailController.getNode();
        LOGGER.fine( String.format( "Creating Thumbnail %s from %s", ( (PictureInfo) referringNode.getUserObject() ).getLowresLocation(), ( (PictureInfo) referringNode.getUserObject() ).getHighresLocation() ) );
        
        try {
            PictureInfo pictureInfo = (PictureInfo) referringNode.getUserObject();
            
            URL imageURL = pictureInfo.getHighresURL();
            double rotation = pictureInfo.getRotation();
            Dimension maxDimension = thumbnailController.getThumbnail().getThumbnailDimension();

            // create a new thumbnail from the highres
            ScalablePicture scalablePicture = new ScalablePicture();
            if ( Settings.thumbnailFastScale ) {
                scalablePicture.setFastScale();
            } else {
                scalablePicture.setQualityScale();
            }
            scalablePicture.setScaleSize( maxDimension );
            
            scalablePicture.loadPictureImd( imageURL, rotation );
            
            scalablePicture.scalePicture();
            
            if ( scalablePicture.getScaledPicture() == null ) {
                LOGGER.info( "There was a problem creating the thumbnail for: " + imageURL );
                thumbnailController.setBrokenIcon();
                return;
            }

            // is the thumbnail is not on the local filesystem then change the
            // url to be a local file or the write will fail.
            if ( !Tools.isUrlFile( pictureInfo.getLowresURL() ) ) {
                LOGGER.info( "The URL is not a file:// type. Getting new name. Type was: " + pictureInfo.getLowresURL().getProtocol().equals( "file" ) );
                pictureInfo.setLowresLocation( Tools.getNewLowresFilename() );
                referringNode.getPictureCollection().setUnsavedUpdates();
            }

            //logger.info(" ... writing: " + pi.getLowresLocation());
            if ( Settings.keepThumbnails ) {
                // Test that the file can be written to
                // Note that we are using files here because java doesn't want to let me use output streams on URL's.
                if ( !pictureInfo.getLowresFile().exists() ) {
                    // the file doesn't yet exist. Can we write to it?
                    try {
                        pictureInfo.getLowresFile().createNewFile();
                    } catch ( IOException x ) {
                        LOGGER.info( "Lowres URL is not writable: " + pictureInfo.getLowresLocation() + " " + x.getMessage() + "  Creating a new URL." );
                        pictureInfo.setLowresLocation( Tools.getNewLowresFilename() );
                        referringNode.getPictureCollection().setUnsavedUpdates();
                    }
                } else {
                    // the file does exist, can we write to it?
                    if ( !pictureInfo.getLowresFile().canWrite() ) {
                        LOGGER.info( "Lowres URL is not writable: " + pictureInfo.getLowresLocation() + ".  Creating a new URL." );
                        pictureInfo.setLowresLocation( Tools.getNewLowresFilename() );
                        referringNode.getPictureCollection().setUnsavedUpdates();
                    }
                }
                scalablePicture.writeScaledJpg( pictureInfo.getLowresFile() );

                // clean the cache
                ImageIcon cleanCache = new ImageIcon( pictureInfo.getLowresURLOrNull() );
                cleanCache.getImage().flush();
                
                pictureInfo.sendThumbnailChangedEvent();
            }

            //if ( !Settings.keepThumbnails ) {
            // This branch is nescessary because it sets the thumbnail only when the
            // ThumbnailController is not written to disk. Where it is written to disk the
            // sent ThumbnailChangedEvent ensures that the new image is loaded.
            ImageIcon icon = new ImageIcon( scalablePicture.getScaledPicture() );
            //if ((currentThumb.myNode != null) && (currentThumb.myNode == myNode)) {
            if ( thumbnailController.getNode() != null ) {
                // could have changed in the mean time
                thumbnailController.getThumbnail().setImageIcon( icon );
            }
            //}
        } catch ( MalformedURLException x ) {
            thumbnailController.setBrokenIcon();
        }
    }

    /**
     * This method looks at the supplied ThumbnailQueueRequest and figures out
     * if there is a suitable disk based thumbnail for the group that can be
     * displayed. If there isn't it has a new thumbnail created.
     *
     * @param req The request to be processed
     */
    private void processGroupRequest( ThumbnailQueueRequest req ) {
        //logger.info( String.format( "Request details: %s", req.toString() ) );
        ThumbnailController currentThumb = req.getThumbnailController();
        GroupInfo gi = (GroupInfo) currentThumb.getNode().getUserObject();
        URL lowresUrl = null;
        
        if ( Settings.keepThumbnails ) {
            lowresUrl = gi.getLowresURLOrNull();
            if ( lowresUrl == null ) {
                lowresUrl = Tools.getNewLowresURL();
                gi.setLowresLocation( lowresUrl );
            }
        }

        // Are we being requested to recreate the ThumbnailController in any case?
        if ( req.getForce() ) {
            createNewGroupThumbnail( currentThumb );
            return;
        }

        // test if lowres is readable
        if ( Settings.keepThumbnails ) {
            try {
                InputStream lowresStream = lowresUrl.openStream();
                lowresStream.close();
            } catch ( IOException x ) {
                //logger.info("ThumbnailCreationFactory.loadOrCreateGroupThumbnail: is requesting the creation of a numbnail because if we can't open the lowres stream we should re-create the image.");
                createNewGroupThumbnail( currentThumb );
                return;
            }
        } else {
            createNewGroupThumbnail( currentThumb );
            return;
        }

        // ThumbnailController up to date is size ok?
        ImageIcon icon = new ImageIcon( lowresUrl );
        if ( isThumbnailSizeOk( new Dimension( icon.getIconWidth(), icon.getIconHeight() ),
                currentThumb.getMaximumUnscaledSize() ) ) {
            // all ist fine
            currentThumb.getThumbnail().setImageIcon( icon );
        } else {
            LOGGER.fine( "Thumbnail is wrong size: " + icon.getIconWidth() + " x " + icon.getIconHeight() );
            createNewGroupThumbnail( currentThumb );
        }
    }

    /**
     * Create a Group ThumbnailController by loading the nodes component images
     * and creating a folder icon with embedded images
     */
    private void createNewGroupThumbnail(
            ThumbnailController groupThumbnailController ) {
        
        if ( groupThumbnailController == null ) {
            LOGGER.info( "Called with null parameter! Aborting." );
            return;
        }
        
        SortableDefaultMutableTreeNode referringNode = groupThumbnailController.getNode();
        //logger.info("ThumbnailCreationFactory.createNewGroupThumbnail: Creating ThumbnailController " + ((GroupInfo) myNode.getUserObject()).getLowresLocation() + " from " + ((GroupInfo) myNode.getUserObject()).getLowresLocation());

        try {
            BufferedImage groupThumbnail = ImageIO.read( new BufferedInputStream( Settings.CLASS_LOADER.getResourceAsStream( "jpo/images/icon_folder_large.jpg" ) ) );
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
                    userObject = ( (SortableDefaultMutableTreeNode) referringNode.getChildAt( childIndex ) ).getUserObject();
                    childIndex++;
                } while ( ( !( userObject instanceof PictureInfo ) ) && ( childIndex < numberOfChildNodes ) );
                
                if ( ( userObject instanceof PictureInfo ) && ( childIndex <= numberOfChildNodes ) ) {
                    x = margin + ( ( picsProcessed % horizontalPics ) * ( Settings.miniThumbnailSize.width + margin ) );
                    yPos = (int) Math.round( ( (double) picsProcessed / (double) horizontalPics ) - 0.5f );
                    y = topMargin + ( yPos * ( Settings.miniThumbnailSize.height + margin ) );
                    //logger.info(Integer.toString(picsProcessed) +": " + Integer.toString(x) + "/" +Integer.toString(y)+ " - " + Integer.toString(yPos) );

                    pi = (PictureInfo) userObject;
                    //logger.info("Loading picture: " + pi.getDescription() + " Filename: " + pi.getLowresFilename() );
                    try {
                        lowresUrl = pi.getLowresURL();
                    } catch ( MalformedURLException mue ) {
                        LOGGER.info( "Lowres URL was Malformed: " + pi.getLowresLocation() );
                        continue;
                    }
                    
                    try {
                        //logger.info( "Trying to load ThumbnailController for Miniicon" );
                        lowresUrl.openStream().close();
                        sclPic.loadPictureImd( lowresUrl, 0 );
                    } catch ( IOException ioe ) {
                        // logger.info( "ThumbnailController failed. Loading Highres for Miniicon" );
                        sclPic.loadPictureImd( pi.getHighresURL(), pi.getRotation() );
                    }
                    
                    sclPic.setScaleSize( Settings.miniThumbnailSize );
                    sclPic.scalePicture();
                    x += ( Settings.miniThumbnailSize.width - sclPic.getScaledWidth() ) / 2;
                    y += Settings.miniThumbnailSize.height - sclPic.getScaledHeight();
                    
                    groupThumbnailGraphics.drawImage( sclPic.getScaledPicture(), x, y, null );
                }
            }

            //logger.info(" ... writing: " + pi.getLowresLocation());
            if ( Settings.keepThumbnails ) {
                // Test that the file can be written to
                // Note that we are using files here because java doesn't want to let me use output streams on URL's.
                GroupInfo gi = (GroupInfo) referringNode.getUserObject();
                if ( !gi.getLowresFile().exists() ) {
                    // the file doesn't yet exist. Can we write to it?
                    try {
                        gi.getLowresFile().createNewFile();
                    } catch ( IOException ioe ) {
                        LOGGER.info( "Lowres URL is not writable: " + gi.getLowresLocation() + " " + ioe.getMessage() + "  Creating a new URL." );
                        gi.setLowresLocation( Tools.getNewLowresFilename() );
                        referringNode.getPictureCollection().setUnsavedUpdates();
                    }
                } else {
                    // the file does exist, can we write to it?
                    if ( !gi.getLowresFile().canWrite() ) {
                        LOGGER.info( "Lowres URL is not writable: " + gi.getLowresLocation() + ".  Creating a new URL." );
                        gi.setLowresLocation( Tools.getNewLowresFilename() );
                        referringNode.getPictureCollection().setUnsavedUpdates();
                    }
                }
                ScalablePicture.writeJpg( gi.getLowresFile(), groupThumbnail, 0.8f );

                // clean the cache
                ImageIcon cleanCache = new ImageIcon( gi.getLowresURLOrNull() );
                cleanCache.getImage().flush();
                
                Settings.pictureCollection.sendNodeChanged( referringNode );
            }

            //if ( ( groupThumbnailController.myNode != null ) && ( groupThumbnailController.myNode == myNode ) ) {
            if ( groupThumbnailController.getNode() == referringNode ) {
                // in the meantime it might be displaying something completely else
                groupThumbnailController.getThumbnail().setImageIcon( new ImageIcon( groupThumbnail ) );
            }
        } catch ( IOException x ) {
            LOGGER.info( "Caught an IOException setting broken icon: " + x.getMessage() );
            groupThumbnailController.setBrokenIcon();
        }
    }
}
