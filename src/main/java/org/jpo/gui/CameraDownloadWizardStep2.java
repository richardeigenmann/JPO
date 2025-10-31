package org.jpo.gui;

import net.javaprog.ui.wizard.AbstractStep;

import javax.swing.*;
import java.awt.event.ActionEvent;

/*
 Copyright (C) 2007-2025 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */


/**
 * The second step in the download from camera dialog asks whether the user
 * wants to copy or move the pictures to his computer.
 */
public class CameraDownloadWizardStep2 extends AbstractStep {

    /**
     * Second step constructor
     * @param dataModel The data model
     */
    public CameraDownloadWizardStep2( CameraDownloadWizardData dataModel ) {
        //pass step title and description
        super(JpoResources.getResource("DownloadCameraWizardStep2Title"), JpoResources.getResource("DownloadCameraWizardStep2Description"));
        this.dataModel = dataModel;
    }

    /**
     * Holds a reference to the data used by the wizard
     */
    private final CameraDownloadWizardData dataModel;

    /**
     * Returns the component that visualises the user widgets for
     * this step of the wizard.
     *
     * @return the component
     */
    @Override
    protected JComponent createComponent() {
        final JPanel stepComponent = new JPanel();
        stepComponent.setLayout(new BoxLayout(stepComponent, BoxLayout.PAGE_AXIS));
        stepComponent.add(new JLabel(JpoResources.getResource("DownloadCameraWizardStep2Text1")
                + dataModel.getNewPictures().size()
                + JpoResources.getResource("DownloadCameraWizardStep2Text2")));
        stepComponent.add(Box.createVerticalStrut(8));

        final JRadioButton moveButton = new JRadioButton(JpoResources.getResource("DownloadCameraWizardStep2Text3"));
        moveButton.addActionListener(( ActionEvent e ) -> {
            dataModel.setCopyMode(false);
            Settings.setLastCameraWizardCopyMode(false);
        });
        stepComponent.add( moveButton );
        final JRadioButton copyButton = new JRadioButton(JpoResources.getResource("DownloadCameraWizardStep2Text4"));
        copyButton.addActionListener(( ActionEvent e ) -> {
            dataModel.setCopyMode(true);
            Settings.setLastCameraWizardCopyMode(true);
        });
        stepComponent.add( copyButton );
        final ButtonGroup group = new ButtonGroup();
        group.add( moveButton );
        group.add( copyButton );
        moveButton.setSelected( !dataModel.getCopyMode() );
        copyButton.setSelected( dataModel.getCopyMode() );
        setCanGoNext( true );
        setCanGoBack( false );
        return stepComponent;
    }

    /**
     * Required by the AbstractStep but not used.
     */
    @Override
    public void prepareRendering() {
        // noop
    }
}
