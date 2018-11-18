package jpo.gui;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import jpo.dataModel.Settings;
import net.javaprog.ui.wizard.AbstractStep;

/*
Copyright (C) 2007-2017  Richard Eigenmann.
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
public class CameraDownloadWizardStep7
        extends AbstractStep {

    /**
     *
     * @param dataModel Data Model
     */
    public CameraDownloadWizardStep7( CameraDownloadWizardData dataModel ) {
        //pass step title and description
        super( Settings.jpoResources.getString( "DownloadCameraWizardStep6Title" ), Settings.jpoResources.getString( "DownloadCameraWizardStep6Description" ) );
        this.dataModel = dataModel;
    }

    /**
     *  Holds a reference to the data used by the wizard
     */
    private final CameraDownloadWizardData dataModel;

    private final JProgressBar progressBar = new JProgressBar();


    /**
     *  Returns the component that visualises the user interactable stuff for this step of the wizard.
     * @return the component
     */
    @Override
    protected JComponent createComponent() {
        JPanel stepComponent = new JPanel();
        stepComponent.setLayout( new BoxLayout( stepComponent, BoxLayout.PAGE_AXIS ) );
        int filesOnCamera = dataModel.getCamera().countFiles();
        int newPictures = dataModel.getNewPictures().size();
        int picsToProcess = dataModel.getCopyMode() ? newPictures + filesOnCamera : filesOnCamera;
        progressBar.setMinimum( 0 );
        progressBar.setMaximum( picsToProcess );
        progressBar.setStringPainted( true );
        stepComponent.add( progressBar );
        if ( !hasStarted ) {
            hasStarted = true;
            new CameraDownloadWorker( dataModel, progressBar, this ).execute();
        }
        return stepComponent;
    }


    /**
     *  Required by the AbstractSetp but not used.
     */
    @Override
    public void prepareRendering() {
        setCanGoNext( false );
        setCanGoBack( false );
        setCanCancel( false );
    }


    /**
     * Call back when done.
     */
    public void done() {
        setCanCancel( false );
        setCanFinish( true );
        progressBar.setValue( progressBar.getMaximum() );
    }

    /**
     *  This field became necessary as there seems to be a problem with JWIZZ whereby it calls the createComponent repeatedly
     *  when doing a setCanGoBack, thereby starting the thread multiple times.
     */
    private boolean hasStarted;  // default is false
}

