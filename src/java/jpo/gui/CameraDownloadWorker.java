package jpo.gui;

import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;


/*
CameraDownloadWorker.java: Downloads the pictures on a background thread and updates the Progress bar

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
 * Downloads the pictures on a background thread and updates the Progress bar
 * @author Richard Eigenmann  richard.eigenmann@gmail.com
 */
public class CameraDownloadWorker
        extends SwingWorker<String, String>
        implements ProgressListener {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CameraDownloadWorker.class.getName() );
    //{logger.setLevel( Level.ALL ); }


    public CameraDownloadWorker( CameraDownloadWizardData dataModel,
            JProgressBar progressBar, CameraDownloadWizardStep7 step7 ) {
        this.dataModel = dataModel;
        this.progressBar = progressBar;
        this.step7 = step7;
    }

    private final CameraDownloadWizardData dataModel;

    private final JProgressBar progressBar;

    CameraDownloadWizardStep7 step7;


    @Override
    protected String doInBackground() throws Exception {
        Settings.memorizeCopyLocation( dataModel.targetDir.toString() );
        if ( dataModel.getShouldCreateNewGroup() ) {
            LOGGER.fine( String.format( "Adding a new group %s to node %s", dataModel.getNewGroupDescription(), dataModel.getTargetNode().toString() ) );
            SortableDefaultMutableTreeNode newGroupNode =dataModel.getTargetNode().addGroupNode( dataModel.getNewGroupDescription() );
            dataModel.setTargetNode( newGroupNode );
        }
        Settings.memorizeGroupOfDropLocation( dataModel.getTargetNode() );

        LOGGER.fine( String.format( "About to copyAddPictures to node %s", dataModel.getTargetNode().toString() ) );
        dataModel.getTargetNode().copyAddPictures( dataModel.getNewPictures(),
                dataModel.targetDir,
                dataModel.getCopyMode(),
                progressBar );
        if ( dataModel.getSortCode() > 1 ) {
            LOGGER.fine( String.format( "Sorting node %s by code %d", dataModel.getTargetNode().toString(), dataModel.getSortCode() ) );
            dataModel.getTargetNode().sortChildren( dataModel.getSortCode() );
        }


        InterruptSemaphore interrupter = new InterruptSemaphore();
        dataModel.getCamera().buildOldImage( this, interrupter );// this, interrupter );
        Settings.writeCameraSettings();
        return "Done";
    }


    /**
     * The Swing Worked calles this method when done.
     */
    @Override
    protected void done() {
        progressBar.setValue( progressBar.getMaximum() );
        Jpo collectionController = dataModel.getCollectionController();
        if ( collectionController != null ) {
            LOGGER.fine( String.format( "Position to node %s", dataModel.getTargetNode() ) );
            Jpo.positionToNode( dataModel.getTargetNode() );
        }
        step7.done();

    }


    @Override
    public void progressIncrement() {
        LOGGER.fine( "Got a progress Increment message" );
        Runnable r = new Runnable() {

            @Override
            public void run() {
                progressBar.setValue( progressBar.getValue() + 1 );
            }
        };
        SwingUtilities.invokeLater( r );
    }
}
