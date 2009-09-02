package jpo.gui;

import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.*;
import java.io.File;
import java.util.logging.Logger;
import net.javaprog.ui.wizard.*;
import javax.swing.*;

/*
CameraDownloadWizardStep5.java: the sixth step in the download from Camera Wizard

Copyright (C) 2007-2009  Richard Eigenmann.
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version. This program is distributedin the hope that it will be useful, but WITHOUT ANY WARRANTY;
without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
more details. You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
The license is in gpl.txt.
See http://www.gnu.org/copyleft/gpl.html for the details.
 */
/**
 *  The sixth step in the download from camera actually does the download. When it is finished the Finish button is made
 *  visible and the Cancel button is disabled.
 */
public class CameraDownloadWizardStep6 extends AbstractStep {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( CameraDownloadWizardStep6.class.getName() );


    /**
     *
     * @param dataModel
     */
    public CameraDownloadWizardStep6( CameraDownloadWizardData dataModel ) {
        //pass step title and description
        super( Settings.jpoResources.getString( "DownloadCameraWizardStep6Title" ), Settings.jpoResources.getString( "DownloadCameraWizardStep6Description" ) );
        this.dataModel = dataModel;
    }

    /**
     *  Holds a reference to the data used by the wizard
     */
    private CameraDownloadWizardData dataModel = null;


    /**
     *  Returns the component that visualises the user interactable stuff for this step of the wizard.
     * @return
     */
    protected JComponent createComponent() {
        //return component shown to the user
        JPanel stepComponent = new JPanel();
        stepComponent.setLayout( new BoxLayout( stepComponent, BoxLayout.PAGE_AXIS ) );
        int filesOnCamera = dataModel.getCamera().countFiles();
        int newPictures = dataModel.getNewPictures().size();
        int picsToProcess = dataModel.getCopyMode() ? newPictures + filesOnCamera : filesOnCamera;
        JProgressBar progressBar = new JProgressBar( 0, picsToProcess );
        logger.info( "just created it" );
        progressBar.setStringPainted( true );
        stepComponent.add( progressBar );
        if ( !hasStarted ) {
            hasStarted = true;
            Thread t = new Thread( new PictureDownloader( progressBar ) );
            t.start();
        }
        return stepComponent;
    }


    /**
     *  Required by the AbstractSetp but not used.
     */
    public void prepareRendering() {
        setCanGoNext( false );
        setCanGoBack( false );
        setCanCancel( false );
    }

    /**
     *  This field became necessary as there seems to be a problem with JWIZZ whereby it calls the createComponent repeatedly
     *  when doing a setCanGoBack, thereby starting the thread multiple times.
     */
    private boolean hasStarted = false;

    /**
     *  This inline class calls the relevant download methods. when finished it changes the Finish and Cancel buttons.
     */
    class PictureDownloader implements Runnable, ProgressListener {

        public PictureDownloader( JProgressBar threadProgressBar ) {
            this.threadProgressBar = threadProgressBar;
        }

        private JProgressBar threadProgressBar;


        @Override
        public void run() {
            Settings.memorizeCopyLocation( dataModel.targetDir.getText() );
            if ( dataModel.getShouldCreateNewGroup() ) {
                dataModel.setTargetNode( dataModel.getTargetNode().addGroupNode( dataModel.getNewGroupDescription() ) );
            }
            Settings.memorizeGroupOfDropLocation( dataModel.getTargetNode() );

            dataModel.getTargetNode().copyAddPictures( dataModel.getNewPictures(),
                    new File( dataModel.targetDir.getText() ),
                    dataModel.getCopyMode(),
                    threadProgressBar );
            dataModel.getTargetNode().refreshThumbnail();

            CollectionJTreeController c = dataModel.getCollectionJTreeController();
            if ( c != null ) {
                c.requestShowGroup( dataModel.getTargetNode() );
            }

            InterruptSemaphore interrupter = new InterruptSemaphore();
            dataModel.getCamera().buildOldImage( this, interrupter );// this, interrupter );
            Settings.writeCameraSettings();
            this.threadProgressBar.setValue( this.threadProgressBar.getMaximum() );

            setCanCancel( false );
            setCanFinish( true );
        }


        /**
         * This callback is used by the Camera.buildOldImage to notify that it has
         * added a file to the list of known files on the camera.
         */
        public void progressIncrement() {
            this.threadProgressBar.setValue( this.threadProgressBar.getValue() + 1 );
        }
    }
}

