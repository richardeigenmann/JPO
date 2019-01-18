package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import net.javaprog.ui.wizard.DefaultWizardModel;
import net.javaprog.ui.wizard.Step;
import net.javaprog.ui.wizard.Wizard;
import net.javaprog.ui.wizard.WizardModel;

/*
CameraDownloadWizard.java:  A Wizard based on the JWizz framework by Michael Rudolf
 
 
Copyright (C) 2007 - 2019  Richard Eigenmann.
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
 * A wizard that leads the user through downloading the pictures from his digital camera.
 * It is based on the JWizz software contributed by Michael Rudolf.
 * <a href="http://javaprog.net/jwizz/">http://javaprog.net/jwizz/</a>
 * <p>
 * Before creating a CameraDownloadWizard, create a CameraDownloadWizardData
 * object and set some parameters on it:
 * <pre>CameraDownloadWizardData dm = new CameraDownloadWizardData();
 * dm.setCamera( c );
 * dm.setAnchorFrame( Settings.anchorFrame );
 * new CameraDownloadWizard( dm );
 * </pre>
 * 
 * @author Richard Eigenmann, richard.eigenmann@gmail.com
 * @see CameraDownloadWizard
 * @see CameraDownloadWizardStep1
 * @see CameraDownloadWizardStep2
 * @see CameraDownloadWizardStep3
 * @see CameraDownloadWizardStep4
 * @see CameraDownloadWizardStep6
 * @see CameraDownloadWizardStep7
 */
public class CameraDownloadWizard {
        
    /**
     *  Constructor for a new CameraDownloadWizard
     *  @param dataModel The data model for the wizard. Pre-fill with the camera before calling.
     */
    public CameraDownloadWizard( CameraDownloadWizardData dataModel ) {
        Tools.checkEDT();
        WizardModel model = new DefaultWizardModel( new Step[] {
            //populate wizard model with the steps
            new CameraDownloadWizardStep1( dataModel ),
            new CameraDownloadWizardStep2( dataModel ),
            new CameraDownloadWizardStep3( dataModel ),
            new CameraDownloadWizardStep4( dataModel ),
            new CameraDownloadWizardStep5( dataModel ),
            new CameraDownloadWizardStep6( dataModel ),
            new CameraDownloadWizardStep7( dataModel ),
        });
        //instaniate wizard
        Wizard wizard = new Wizard( model, Settings.jpoResources.getString("CameraDownloadWizard") );
        //show wizard
        wizard.setAlwaysOnTop( true );
        wizard.setModal( false );
        wizard.pack();
        wizard.setLocationRelativeTo( dataModel.getAnchorFrame() );
        wizard.setVisible(true);
    }
}


