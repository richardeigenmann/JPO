package jpo;

import java.io.File;
import net.javaprog.ui.wizard.*;
import javax.swing.*;

/*
CameraDownloadWizardStep5.java: the sixth step in the download from Camera Wizard
 
Copyright (C) 2007  Richard Eigenmann.
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
 *  The sixth step in the download from camera actually does the download. When it is finished the Finish button is made
 *  visible and the Cancel button is disabled.
 */
public class CameraDownloadWizardStep6 extends AbstractStep {
    public CameraDownloadWizardStep6( CameraDownloadWizardData dataModel ) {
        //pass step title and description
        super( Settings.jpoResources.getString("DownloadCameraWizardStep6Title"), Settings.jpoResources.getString("DownloadCameraWizardStep6Description") );
        this.dataModel = dataModel;
    }
    
    /**
     *  Holds a reference to the data used by the wizard
     */
    private CameraDownloadWizardData dataModel = null;
    
    
    /**
     *  Returns the component that visualises the user interactable stuff for this step of the wizard.
     */
    protected JComponent createComponent() {
        //return component shown to the user
        JPanel stepComponent = new JPanel();
        stepComponent.setLayout( new BoxLayout( stepComponent, BoxLayout.PAGE_AXIS ) );
        JProgressBar progressBar = new JProgressBar( 0, dataModel.getNewPictures().size() );
        Tools.log("just created it");
        progressBar.setStringPainted( true );
        stepComponent.add( progressBar );
        if ( ! hasStarted ) {
            hasStarted = true;
            Thread t = new DownloadPicturesThread( progressBar );
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
    class DownloadPicturesThread extends Thread {
        
        
        public DownloadPicturesThread( JProgressBar threadProgressBar ) {
            this.threadProgressBar = threadProgressBar;
        }
        
        private JProgressBar threadProgressBar;
        
        public void run() {
            Settings.memorizeCopyLocation( dataModel.targetDir.getText() );
            if ( dataModel.getCreateNewGroup() ) {
                dataModel.setTargetNode( dataModel.getTargetNode().addGroupNode( dataModel.getNewGroupDescription() ) );
            }
            
            dataModel.getTargetNode().copyAddPictures( dataModel.getNewPictures(),
                    new File( dataModel.targetDir.getText() ),
                    dataModel.getCopyMode(),
                    threadProgressBar );
            if ( dataModel.getCreateNewGroup() ) {
                dataModel.getTargetNode().sortChildren( Settings.CREATION_TIME );
                // fix the group thumbnail
                SingleNodeBrowser snb = new SingleNodeBrowser( dataModel.getTargetNode() );
                Thumbnail thumb = new Thumbnail( snb, 0, Settings.thumbnailSize, ThumbnailCreationQueue.LOW_PRIORITY );
                thumb.requestThumbnailCreation( ThumbnailCreationQueue.LOW_PRIORITY, true );
            }
            
            CollectionJTreeController c = dataModel.getCollectionJTreeController();
            if ( c!= null ) {
                c.requestShowGroup( dataModel.getTargetNode() );
            }
            
            InterruptSemaphore interrupter = new InterruptSemaphore();
            dataModel.getCamera().buildOldImage( null, interrupter );
            Settings.writeCameraSettings();
            
            setCanCancel( false );
            setCanFinish( true );
        }
    }
    
    
}

