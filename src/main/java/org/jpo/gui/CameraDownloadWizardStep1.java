package org.jpo.gui;

import net.javaprog.ui.wizard.AbstractStep;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 CameraDownloadWizardStep1.java: the first step in the download from Camera Wizard

 Copyright (C) 2007 - 2016  Richard Eigenmann.
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
 * The first step in the download from camera dialog that pops up and informs
 * the user that on the camera she just plugged in x new pictures were
 * discovered. If no new pictures were found the Next button remains disabled
 * and the user can only click the Cancel button.
 */
public class CameraDownloadWizardStep1
        extends AbstractStep {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CameraDownloadWizardStep1.class.getName() );

    /**
     * Constructor for the first step
     *
     * @param dataModel The data model for this wizard
     */
    public CameraDownloadWizardStep1( CameraDownloadWizardData dataModel ) {
        //pass step title and description
        super(Settings.getJpoResources().getString("DownloadCameraWizardStep1Title"), Settings.getJpoResources().getString("DownloadCameraWizardStep1Description"));
        this.dataModel = dataModel;
    }

    /**
     * Holds a reference to the data used by the wizard
     */
    private final CameraDownloadWizardData dataModel;

    /**
     * Returns the component that visualises the user interactable stuff for
     * this step of the wizard.
     *
     * @return the component
     */
    @Override
    protected JComponent createComponent() {
        Tools.checkEDT();
        //return component shown to the user
        JPanel stepComponent = new JPanel();
        stepComponent.setLayout( new BoxLayout( stepComponent, BoxLayout.PAGE_AXIS ) );
        // say Camera xxx detected
        stepComponent.add(new JLabel(Settings.getJpoResources().getString("DownloadCameraWizardStep1Text1") + dataModel.getCamera().getDescription() + Settings.getJpoResources().getString("DownloadCameraWizardStep1Text2")));
        stepComponent.add(Box.createVerticalStrut(8));

        JLabel analysisLabel = new JLabel(Settings.getJpoResources().getString("DownloadCameraWizardStep1Text4"));
        stepComponent.add( analysisLabel );
        Thread t = new Thread( new SearchForPicturesThread( analysisLabel ), "CameraDownloadWizard" );
        t.start();

        return stepComponent;
    }

    /**
     * Required by the AbstractStep but not used.
     */
    @Override
    public void prepareRendering() {
        setCanGoNext( false );
    }

    /**
     * This inner class will search for the new pictures. It is implemented as a
     * thread. When the new pictures have been found it changes the label to say
     * that who many pictures were found. Only if there are more than 0 pictures
     * then the setCanGoNext method is called which will allow the user to move
     * forward. If there are no new pictures then the user can only cancel.
     */
    private class SearchForPicturesThread
            implements Runnable {

        private final JLabel progressJLabel;

        SearchForPicturesThread( JLabel progressJLabel ) {
            this.progressJLabel = progressJLabel;
        }

        @Override
        public void run() {
            LOGGER.log(Level.INFO, "{0}.run: searching for the new pictures on the camera {1}", new Object[]{getClass(), dataModel.getCamera().getDescription()});
            dataModel.setNewPictures(dataModel.getCamera().getNewPictures());

            final Runnable r = () -> {
                setCanGoNext(dataModel.getNewPictures().size() > 0);
                progressJLabel.setText(dataModel.getNewPictures().size() + Settings.getJpoResources().getString("DownloadCameraWizardStep1Text3"));
            };
            if (SwingUtilities.isEventDispatchThread()) {
                r.run();
            } else {
                SwingUtilities.invokeLater(r);
            }
        }
    }
}
