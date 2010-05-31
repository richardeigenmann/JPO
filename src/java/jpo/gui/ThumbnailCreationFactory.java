package jpo.gui;

import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import javax.swing.*;
import java.net.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;
import java.util.logging.Logger;
import javax.imageio.*;

/*
ThumbnailCreationFactory.java:  A factory that creates thumbnails

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
 *  A thread that polls the static {@link ThumbnailCreationQueue} and then 
 *  creates thumbnails for the {@link ThumbnailQueueRequest} on the queue.
 **/
public class ThumbnailCreationFactory
        implements Runnable {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( ThumbnailCreationFactory.class.getName() );

    /**
     *   Flag to indicate that the thread should die.
     */
    public boolean endThread = false;


    /**
     *  Constructor that creates the thread. It creates the thread with a Thread.MIN_PRIOTITY priority
     *  to ensure good overall response.
     */
    public ThumbnailCreationFactory() {
        Thread t = new Thread( this );
        t.setPriority( Thread.MIN_PRIORITY );
        t.start();
    }


    /**
     *  The run method for the thread that keeps checking whether there are any {@link ThumbnailQueueRequest} objects
     *  on the queue to be rendered.
     */
    public void run() {
        while ( !endThread ) {
            ThumbnailQueueRequest req = ThumbnailCreationQueue.poll();
            if ( req == null ) {
                try {
                    Thread.sleep( Settings.ThumbnailCreationThreadPollingTime );
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
     *  request to the {@link #loadOrCreatePictureThumbnail} or the {@link #loadOrCreateGroupThumbnail} method.
     *
     *  @param  req		the {@link ThumbnailQueueRequest} for which to create the ThumbnailController
     */
    private void createThumbnail( ThumbnailQueueRequest req ) {
        //logger.info( String.format( "Processing QueueRequest %s", req.toString() ) );
        ThumbnailController currentThumb = req.getThumbnailController();
        // now block other threads from accessing the ThumbnailController
        synchronized ( currentThumb ) {
            SortableDefaultMutableTreeNode referringNode = currentThumb.referringNode;
            if ( referringNode == null ) {
                logger.severe( "referringNode was null! Setting Broken Image.\nThis happened on ThumbnailQueueRequest: " + req.toString() + " which refers to Thumbnail: " + currentThumb.toString() );
                Thread.dumpStack();
                currentThumb.setBrokenIcon();
                return;
            }

            // validate we were called on the right type of node
            if ( referringNode.getUserObject() instanceof PictureInfo ) {
                //logger.info("ThumbnailCreationFactory.createThumbnail: Throwing ThumbnailController " + Integer.toString(groupThumbnailController.myIndex) + " on queue");
                loadOrCreatePictureThumbnail( req );
            } else if ( referringNode.getUserObject() instanceof GroupInfo ) {
                loadOrCreateGroupThumbnail( req );
            } else {
                currentThumb.setBrokenIcon();
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
     *   @param req 	the ThumbnailQueueRequest for which to create the ThumbnailController
     */
    private void loadOrCreatePictureThumbnail( ThumbnailQueueRequest req ) {
        if ( req == null ) {
            logger.info( "Invoked with a null request. Aborting." );
            return;
        }
        logger.fine( String.format( "Processing Queue Request details: %s", req.toString() ) );
        ThumbnailController currentThumb = req.getThumbnailController();
        if ( currentThumb == null ) {
            logger.info( "Invoked request with a null Thumbnail. Aborting." );
            return;
        }
        PictureInfo pi = (PictureInfo) currentThumb.referringNode.getUserObject();
        if ( pi == null ) {
            logger.info( "Could not find PictureInfo. Aborting." );
            return;
        }
        URL lowresUrl = null;

        if ( Settings.keepThumbnails ) {
            try {
                lowresUrl = pi.getLowresURL();
            } catch ( MalformedURLException x ) {
                logger.info( "Lowres URL was Malformed: " + pi.getLowresLocation() + "  Creating a new URL." );
                pi.setLowresLocation( Tools.lowresFilename() );
                try {
                    lowresUrl = pi.getLowresURL();
                } catch ( MalformedURLException x1 ) {
                    logger.info( "The system is generating broken URL's! Aborting Thumbnail creation!" );
                    currentThumb.setBrokenIcon();
                    return;
                }
                createNewThumbnail( currentThumb );
                return;
            }
            // if we get here we have a good lowres URL
        }


        // Are we being requested to recreate the ThumbnailController in any case?
        if ( req.getForce() ) {
            createNewThumbnail( currentThumb );
            return;
        }


        // test if lowres is readable
        if ( Settings.keepThumbnails ) {
            try {
                InputStream lowresStream = lowresUrl.openStream();
                lowresStream.close();
            } catch ( IOException x ) {
                //logger.info("ThumbnailCreationFactory.createThumbnail: is requesting the creation of a numbnail because if we can't open the lowres stream we should re-create the image.");
                createNewThumbnail( currentThumb );
                return;
            }


            URL highresUrl = null;
            try {
                highresUrl = pi.getHighresURL();
            } catch ( MalformedURLException x ) {
                logger.info( "Highres URL was Malformed: " + pi.getHighresLocation() + "  Loading \"broken\" icon." );
                currentThumb.setBrokenIcon();
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
                    currentThumb.getThumbnail().setThumbnail( icon );
                    return;
                } catch ( IOException ioe ) {
                    // we have nothing to display
                    currentThumb.setBrokenIcon();
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

                if ( lowresModDate < highresModDate ) {
                    logger.fine( "Requesting the creation of a Thumbnail because Thumbnail is out of date: " + pi.getLowresLocation() );
                    createNewThumbnail( currentThumb );
                    return;
                }
            } catch ( IOException x ) {
                //if we can't open the stream we should re-create the image
                createNewThumbnail( currentThumb );
                return;
            }
        } else {
            createNewThumbnail( currentThumb );
            return;
        }


        // ThumbnailController up to date is size ok?
        logger.fine( "Thumbnail is more recent than Highres picture. Loading and checking size..." );
        ImageIcon icon = new ImageIcon( lowresUrl );
        if ( isThumbnailSizeOk( new Dimension( icon.getIconWidth(), icon.getIconHeight() ),
                currentThumb.getMaximumUnscaledSize() ) ) {
            // all ist fine
            currentThumb.getThumbnail().setThumbnail( icon );
        } else {
            logger.info( "ThumbnailCreationThread.createPictureThumbnail: Thumbnail is wrong size: " + icon.getIconWidth() + " x " + icon.getIconHeight() + " therefore thrown on queue" );
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
     *  creates a thumbnail by loading the highres image and scaling it down
     */
    private void createNewThumbnail( ThumbnailController currentThumb ) {
        SortableDefaultMutableTreeNode referringNode = null;
        if ( currentThumb == null ) {
            logger.info( "ThumbnailCreationThread.createNewThumbnail called with null parameter! Aborting." );
            return;
        }

        referringNode = currentThumb.referringNode;
        logger.fine( String.format( "Creating Thumbnail %s from %s", ( (PictureInfo) referringNode.getUserObject() ).getLowresLocation(), ( (PictureInfo) referringNode.getUserObject() ).getHighresLocation() ) );

        try {
            // create a new thumbnail from the highres
            ScalablePicture currentPicture = new ScalablePicture();
            currentPicture.setScaleSize( new Dimension( currentThumb.getThumbnail().thumbnailSize, currentThumb.getThumbnail().thumbnailSize ) );
            if ( Settings.thumbnailFastScale ) {
                currentPicture.setFastScale();
            } else {
                currentPicture.setQualityScale();
            }


            PictureInfo pi = (PictureInfo) referringNode.getUserObject();
            currentPicture.loadPictureImd( pi.getHighresURL(), pi.getRotation() );


            //logger.info(" ... scaling");
            currentPicture.scalePicture();


            if ( currentPicture.getScaledPicture() == null ) {
                logger.info( "There was a problem creating the thumbnail for: " + pi.getHighresURL() );
                currentThumb.setBrokenIcon();
                return;
            }


            // is the thumbnail is not on the local filesystem then change the
            // url to be a local file or the write will fail.
            if ( !Tools.isUrlFile( pi.getLowresURL() ) ) {
                logger.info( "The URL is not a file:// type. Getting new name. Type was: " + pi.getLowresURL().getProtocol().equals( "file" ) );
                pi.setLowresLocation( Tools.lowresFilename() );
                referringNode.getPictureCollection().setUnsavedUpdates();
            }


            //logger.info(" ... writing: " + pi.getLowresLocation());
            if ( Settings.keepThumbnails ) {
                // Test that the file can be written to
                // Note that we are using files here because java doesn't want to let me use output streams on URL's.
                if ( !pi.getLowresFile().exists() ) {
                    // the file doesn't yet exist. Can we write to it?
                    try {
                        pi.getLowresFile().createNewFile();
                    } catch ( IOException x ) {
                        logger.info( "Lowres URL is not writable: " + pi.getLowresLocation() + " " + x.getMessage() + "  Creating a new URL." );
                        pi.setLowresLocation( Tools.lowresFilename() );
                        referringNode.getPictureCollection().setUnsavedUpdates();
                    }
                } else {
                    // the file does exist, can we write to it?
                    if ( !pi.getLowresFile().canWrite() ) {
                        logger.info( "Lowres URL is not writable: " + pi.getLowresLocation() + ".  Creating a new URL." );
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

            //if ( !Settings.keepThumbnails ) {
            // This branch is nescessary because it sets the thumbnail only when the
            // ThumbnailController is not written to disk. Where it is written to disk the
            // sent ThumbnailChangedEvent ensures that the new image is loaded.
            ImageIcon icon = new ImageIcon( currentPicture.getScaledPicture() );
            //if ((currentThumb.referringNode != null) && (currentThumb.referringNode == referringNode)) {
            if ( currentThumb.referringNode != null ) {
                // could have changed in the mean time
                currentThumb.getThumbnail().setThumbnail( icon );
            }
            //}
        } catch ( IOException x ) {
            currentThumb.setBrokenIcon();
        }
    }


    /**
     * This method looks at the supplied ThumbnailQueueRequest and figures out if there is a
     * suitable disk based thumbnail for the group that can be displayed. If there isn't it
     * has a new thumbnail created.
     * @param req The request to be processed
     */
    private void loadOrCreateGroupThumbnail( ThumbnailQueueRequest req ) {
        logger.fine( String.format( "Request details: %s", req.toString() ) );
        ThumbnailController currentThumb = req.getThumbnailController();
        GroupInfo gi = (GroupInfo) currentThumb.referringNode.getUserObject();
        URL lowresUrl = null;

        if ( Settings.keepThumbnails ) {
            try {
                lowresUrl = gi.getLowresURL();
            } catch ( MalformedURLException x ) {
                logger.info( String.format( "Caught MalformedURLException: %s\nThe bad URL read: %s\nRequesting a new URL.", x.getMessage(), gi.getLowresLocation() ) );
                gi.setLowresLocation( Tools.lowresFilename() );
                try {
                    lowresUrl = gi.getLowresURL();
                } catch ( MalformedURLException x1 ) {
                    logger.severe( String.format( "The system is generating broken URL's! Setting broken-thumbnail image.\nError: %s", x.getMessage() ) );
                    currentThumb.setBrokenIcon();
                    return;
                }
                createNewGroupThumbnail( currentThumb );
                return;
            }
            // if we get here we have a good lowres URL
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
            currentThumb.getThumbnail().setThumbnail( icon );
        } else {
            //logger.info( "ThumbnailCreationFactory.loadOrCreateGroupThumbnail: ThumbnailController is wrong size: " + icon.getIconWidth()  + " x " +  icon.getIconHeight() );
            createNewGroupThumbnail( currentThumb );
        }

        return;
    }


    /**
     *  Create a Group ThumbnailController by loading the nodes component images and creating a folder icon with embeded images
     */
    private void createNewGroupThumbnail(
            ThumbnailController groupThumbnailController ) {
        SortableDefaultMutableTreeNode referringNode = null;
        if ( groupThumbnailController == null ) {
            logger.info( "Called with null parameter! Aborting." );
            return;
        }

        referringNode = groupThumbnailController.referringNode;
        //logger.info("ThumbnailCreationFactory.createNewGroupThumbnail: Creating ThumbnailController " + ((GroupInfo) referringNode.getUserObject()).getLowresLocation() + " from " + ((GroupInfo) referringNode.getUserObject()).getLowresLocation());

        try {
            BufferedImage groupThumbnail = ImageIO.read( new BufferedInputStream( Settings.cl.getResourceAsStream( "jpo/images/icon_folder_large.jpg" ) ) );
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
                        logger.info( "Lowres URL was Malformed: " + pi.getLowresLocation() );
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
                        logger.info( "Lowres URL is not writable: " + gi.getLowresLocation() + " " + ioe.getMessage() + "  Creating a new URL." );
                        gi.setLowresLocation( Tools.lowresFilename() );
                        referringNode.getPictureCollection().setUnsavedUpdates();
                    }
                } else {
                    // the file does exist, can we write to it?
                    if ( !gi.getLowresFile().canWrite() ) {
                        logger.info( "Lowres URL is not writable: " + gi.getLowresLocation() + ".  Creating a new URL." );
                        gi.setLowresLocation( Tools.lowresFilename() );
                        referringNode.getPictureCollection().setUnsavedUpdates();
                    }
                }
                ScalablePicture.writeJpg( gi.getLowresFile(), groupThumbnail, 0.8f );

                // clean the cache
                ImageIcon cleanCache = new ImageIcon( gi.getLowresURLOrNull() );
                cleanCache.getImage().flush();

                Settings.pictureCollection.sendNodeChanged( referringNode );
            }


            //if ( ( groupThumbnailController.referringNode != null ) && ( groupThumbnailController.referringNode == referringNode ) ) {
            if ( groupThumbnailController.referringNode == referringNode ) {
                // in the meantime it might be displaying something completely else
                groupThumbnailController.getThumbnail().setThumbnail( new ImageIcon( groupThumbnail ) );
            }
        } catch ( IOException x ) {
            logger.info( "ThumbnailCreationThread.createNewGroupThumbnail: caught an IOException: " + x.getMessage() );
            groupThumbnailController.setBrokenIcon();
        }
    }
}
