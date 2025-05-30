package org.jpo.export;

import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import javax.swing.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * PicasaUploaderWorker.java: service using Google provided code. Copyright (C)
 * 2012-2025 Richard Eigenmann. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the
 * License, or any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details. You should have received a copy of
 * the GNU General Public License along with this program; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 *
 * @author Richard Eigenmann
 */
public class PicasaUploaderWorker extends SwingWorker<Boolean, Integer> {

    private final PicasaUploadRequest myRequest;
    private final JProgressBar progressBar;
    private final PicasaUploaderDoneInterface doneHandler;

    /**
     * Creates a SwingWorker to upload the pictures to Picasa
     * @param myRequest the request
     * @param progressBar the progress bar
     * @param doneHandler the done handler to call afterwards
     */
    public PicasaUploaderWorker( PicasaUploadRequest myRequest, JProgressBar progressBar, PicasaUploaderDoneInterface doneHandler ) {
        this.myRequest = myRequest;
        this.progressBar = progressBar;
        this.doneHandler = doneHandler;
    }
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( PicasaUploaderWorker.class.getName() );

    @Override
    protected Boolean doInBackground() {
        GroupInfo groupInfo;
        try {
            groupInfo = (GroupInfo) myRequest.getNode().getUserObject();
        } catch ( ClassCastException ex ) {
            LOGGER.severe( ex.getMessage() );
            return false;
        }

        if ( !createAlbum( groupInfo ) ) {
            LOGGER.severe( "Could not create Album" );
            return false;
        }

        PictureInfo pi;
        for ( SortableDefaultMutableTreeNode node : myRequest.getNode().getChildPictureNodes( false ) ) {
            publish( 1 );
            pi = (PictureInfo) node.getUserObject();
            postPicture( pi );
            if ( myRequest.isInterrupt() ) {
                break;
            }
        }
        return true;
    }

    @Override
    protected void process(final List<Integer> chunks) {
        chunks.forEach(item -> progressBar.setValue(progressBar.getValue() + 1));
    }

    @Override
    protected void done() {
        doneHandler.uploadDone();
    }

    /**
     * Creates a group on Picasa
     * @param groupInfo the group for which to create the "album"
     * @return  true if success, false if not
     */
    public boolean createAlbum( GroupInfo groupInfo ) {
        LOGGER.log(Level.SEVERE, "Not implemented. No longer working" );
        return true;
    }

    /**
     * Uploads a picture to Picasa
     *
     * @param pictureInfo the picture to upload
     */
    public void postPicture(final PictureInfo pictureInfo) {
        LOGGER.log(Level.SEVERE, "Not implemented. No longer working" );
    }
}
