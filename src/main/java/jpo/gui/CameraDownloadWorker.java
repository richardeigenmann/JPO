package jpo.gui;

import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import jpo.EventBus.CopyLocationsChangedEvent;
import jpo.EventBus.GroupSelectionEvent;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.RecentDropNodesChangedEvent;
import jpo.EventBus.RefreshThumbnailRequest;
import jpo.cache.ThumbnailQueueRequest.QUEUE_PRIORITY;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;


/*
 Copyright (C) 2002 - 2017  Richard Eigenmann.
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
 * Downloads the pictures on a background thread and updates the Progress bar
 *
 * @author Richard Eigenmann richard.eigenmann@gmail.com
 */
public class CameraDownloadWorker
        extends SwingWorker<String, String>
        implements ProgressListener {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CameraDownloadWorker.class.getName() );

    /**
     * A SwingWorker to download pictures from the camera
     * @param dataModel the data model
     * @param progressBar The progress bar
     * @param step7 step 7
     */
    public CameraDownloadWorker( CameraDownloadWizardData dataModel,
            JProgressBar progressBar, CameraDownloadWizardStep7 step7 ) {
        this.dataModel = dataModel;
        this.progressBar = progressBar;
        this.step7 = step7;
    }

    private final CameraDownloadWizardData dataModel;

    private final JProgressBar progressBar;

    private CameraDownloadWizardStep7 step7;

    @Override
    protected String doInBackground() throws Exception {
        Settings.memorizeCopyLocation( dataModel.targetDir.toString() );
        JpoEventBus.getInstance().post( new CopyLocationsChangedEvent() );
        if ( dataModel.getShouldCreateNewGroup() ) {
            LOGGER.fine( String.format( "Adding a new group %s to node %s", dataModel.getNewGroupDescription(), dataModel.getTargetNode().toString() ) );
            SortableDefaultMutableTreeNode newGroupNode = dataModel.getTargetNode().addGroupNode( dataModel.getNewGroupDescription() );
            dataModel.setTargetNode( newGroupNode );
        }
        Settings.memorizeGroupOfDropLocation( dataModel.getTargetNode() );
        JpoEventBus.getInstance().post( new RecentDropNodesChangedEvent() );

        LOGGER.fine( String.format( "About to copyAddPictures to node %s", dataModel.getTargetNode().toString() ) );
        dataModel.getTargetNode().copyAddPictures( dataModel.getNewPictures(),
                dataModel.targetDir,
                dataModel.getCopyMode(),
                progressBar );
            LOGGER.fine( String.format( "Sorting node %s by code %s", dataModel.getTargetNode().toString(), dataModel.getSortCode() ) );
            dataModel.getTargetNode().sortChildren( dataModel.getSortCode() );
            JpoEventBus.getInstance().post( new RefreshThumbnailRequest( dataModel.getTargetNode(), QUEUE_PRIORITY.LOWEST_PRIORITY ) );

        InterruptSemaphore interrupter = new InterruptSemaphore();
        dataModel.getCamera().buildOldImage( this, interrupter );// this, interrupter );
        Settings.writeCameraSettings();
        return "Done";
    }

    /**
     * The Swing Worked calls this method when done.
     */
    @Override
    protected void done() {
        progressBar.setValue( progressBar.getMaximum() );
        JpoEventBus.getInstance().post( new GroupSelectionEvent( dataModel.getTargetNode() ) );
        step7.done();

    }

    @Override
    public void progressIncrement() {
        SwingUtilities.invokeLater( 
                () -> progressBar.setValue( progressBar.getValue() + 1 )
        );
    }
}
