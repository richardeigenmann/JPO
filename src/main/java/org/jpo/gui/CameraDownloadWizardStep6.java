package org.jpo.gui;

import net.javaprog.ui.wizard.AbstractStep;
import org.jpo.dataModel.Settings;
import org.jpo.dataModel.SortOption;

import javax.swing.*;
import java.util.List;

/*
 CameraDownloadWizardStep6.java: the fifth step in the download from Camera Wizard

 Copyright (C) 2007 - 2014  Richard Eigenmann.
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
 * The fifth step in the download from camera download wizard summarises the
 * action that will be performed.
 */
public class CameraDownloadWizardStep6 extends AbstractStep {

    /**
     *
     * @param dataModel The data model
     */
    public CameraDownloadWizardStep6( CameraDownloadWizardData dataModel ) {
        //pass step title and description
        super( Settings.jpoResources.getString( "DownloadCameraWizardStep5Title" ), Settings.jpoResources.getString( "DownloadCameraWizardStep5Description" ) );
        this.dataModel = dataModel;
    }
    /**
     * Holds a reference to the data used by the wizard
     */
    private final CameraDownloadWizardData dataModel;
    private final JLabel label1 = new JLabel();
    private final JLabel label2 = new JLabel();
    private final JLabel label3 = new JLabel();
    private final JLabel label3a = new JLabel(); // Sorting by
    private final JLabel label4 = new JLabel();

    /**
     * Returns the component that visualises the user intractable stuff for this
     * step of the wizard.
     *
     * @return the component
     */
    @Override
    protected JComponent createComponent() {
        JPanel stepComponent = new JPanel();
        stepComponent.setLayout( new BoxLayout( stepComponent, BoxLayout.PAGE_AXIS ) );
        stepComponent.add( label1 );
        stepComponent.add( Box.createVerticalStrut( 8 ) );
        stepComponent.add( label2 );
        stepComponent.add( Box.createVerticalStrut( 8 ) );
        stepComponent.add( label3 );
        stepComponent.add( Box.createVerticalStrut( 8 ) );
        stepComponent.add( label3a );
        stepComponent.add( Box.createVerticalStrut( 8 ) );
        stepComponent.add( label4 );

        return stepComponent;
    }

    /**
     * Required by the AbstractStep but not used.
     */
    @Override
    public void prepareRendering() {

        // Move|Copy xx pictures from
        if ( dataModel.getCopyMode() ) {
            label1.setText( Settings.jpoResources.getString( "DownloadCameraWizardStep5Text1" ) + dataModel.getNewPictures().size() + Settings.jpoResources.getString( "DownloadCameraWizardStep5Text3" ) );
        } else {
            label1.setText( Settings.jpoResources.getString( "DownloadCameraWizardStep5Text2" ) + dataModel.getNewPictures().size() + Settings.jpoResources.getString( "DownloadCameraWizardStep5Text3" ) );
        }

        // Camera xxxx
        label2.setText( Settings.jpoResources.getString( "DownloadCameraWizardStep5Text4" ) + dataModel.getCamera().getDescription() );

        // Adding to [new] folder xxx
        if ( dataModel.getShouldCreateNewGroup() ) {
            label3.setText( Settings.jpoResources.getString( "DownloadCameraWizardStep5Text5" ) + dataModel.getNewGroupDescription() );
        } else {
            label3.setText( Settings.jpoResources.getString( "DownloadCameraWizardStep5Text6" ) + dataModel.getTargetNode().toString() );
        }

        // Sorting by
        String sortingDescription = "not found!";
        List<SortOption> sortOptions = Settings.getSortOptions();
        for ( SortOption sortOption : sortOptions ) {
            if ( sortOption.getSortCode() == dataModel.getSortCode() ) {
                sortingDescription = sortOption.getDescription();
            }
        }
        label3a.setText( "Sorting by: " + sortingDescription );

        // Storing in xxx
        label4.setText( Settings.jpoResources.getString( "DownloadCameraWizardStep5Text7" ) + dataModel.targetDir.toString() );
        if ( !dataModel.targetDir.exists() ) {
            dataModel.targetDir.mkdirs();
        }
        if ( !dataModel.targetDir.exists() ) {
            label4.setText( String.format( "Error: %s  doesn't exist.", dataModel.targetDir.toString() ) );
            setCanGoNext( false );
        } else if ( !dataModel.targetDir.isDirectory() ) {
            label4.setText( String.format( "Error: %s  must be a directory.", dataModel.targetDir.toString() ) );
            setCanGoNext( false );
        } else if ( !dataModel.targetDir.canWrite() ) {
            label4.setText( String.format( "Error: Can't write to %s", dataModel.targetDir.toString() ) );
            setCanGoNext( false );
        } else {
            setCanGoNext( true );
        }

    }
}
