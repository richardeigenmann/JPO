package jpo.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import jpo.cache.ImageBytes;
import jpo.cache.JpoCache;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;

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

    /**
     * Flag to indicate that the thread should die.
     */
    public boolean endThread;  // default is false

    /**
     * Constructor that creates the thread. It creates the thread with a
     * Thread.MIN_PRIOTITY priority to ensure good overall response.
     */
    public ThumbnailCreationFactory() {
        Thread thread = new Thread( this, "ThumbnailCreationFactory" );
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
     * Controller and passes the request to the Factory.
     *
     * @param request	the {@link ThumbnailQueueRequest} for which to create the
     * ThumbnailController
     */
    private void processQueueRequest( ThumbnailQueueRequest request ) {
        ThumbnailController thumbnailController = request.getThumbnailController();
        if ( thumbnailController == null ) {
            LOGGER.info( "Invoked request with a null Thumbnail. Aborting." );
            return;
        }
        LOGGER.fine( String.format( "Processing Queue Request details: %s", request.toString() ) );
        Object userObject = thumbnailController.getNode().getUserObject();
        if ( userObject == null ) {
            LOGGER.info( "Could not find PictureInfo. Aborting." );
            return;
        }

        // now block other threads from accessing the ThumbnailController
        synchronized ( thumbnailController ) {
            try {
                if ( userObject instanceof PictureInfo ) {
                    PictureInfo pi = (PictureInfo) userObject;

                    ImageBytes imageBytes = JpoCache.getInstance().getThumbnailImageBytes(
                            pi.getHighresURL(),
                            pi.getRotation(),
                            thumbnailController.getMaximumUnscaledSize().width,
                            thumbnailController.getMaximumUnscaledSize().height );
                    ImageIcon icon = new ImageIcon( imageBytes.getBytes() );
                    thumbnailController.getThumbnail().setImageIcon( icon );

                } else if ( userObject instanceof GroupInfo ) {
                    List<SortableDefaultMutableTreeNode> childPictureNodes = thumbnailController.getNode().getChildPictureNodes( false );

                    ImageBytes imageBytes = JpoCache.getInstance().getGroupThumbnailImageBytes( childPictureNodes );
                    ImageIcon icon = new ImageIcon( imageBytes.getBytes() );
                    thumbnailController.getThumbnail().setImageIcon( icon );

                } else {
                    thumbnailController.setBrokenIcon();
                }
            } catch ( IOException ex ) {
                Logger.getLogger( ThumbnailCreationFactory.class.getName() ).log( Level.SEVERE, null, ex );
                thumbnailController.setBrokenIcon();
            }

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

    
}
