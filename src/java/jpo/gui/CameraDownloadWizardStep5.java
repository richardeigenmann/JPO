package jpo.gui;

import java.awt.event.ActionEvent;
import jpo.dataModel.Settings;
import jpo.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Logger;
import net.javaprog.ui.wizard.*;
import javax.swing.*;
import jpo.dataModel.SortOption;

/*
CameraDownloadWizardStep4.java: the fourth step in the download from Camera Wizard

Copyright (C) 2007 - 2009  Richard Eigenmann.
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
 *  The fourth step in the download from camera dialog asks for the storage location on the disk.
 */
public class CameraDownloadWizardStep5
        extends AbstractStep {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( CameraDownloadWizardStep5.class.getName() );


    /**
     * The fourth step in the download from camera dialog asks for the storage location on the disk.
     * @param dataModel The data model where the settings are to be saved
     */
    public CameraDownloadWizardStep5( CameraDownloadWizardData dataModel ) {
        //pass step title and description
        //super( Settings.jpoResources.getString( "DownloadCameraWizardStep4Title" ), Settings.jpoResources.getString( "DownloadCameraWizardStep4Description" ) );
        super( "Sorting", "How to Sort" );
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
        //JLabel label1 = new JLabel( Settings.jpoResources.getString( "DownloadCameraWizardStep4Text1" ) );
        JLabel label1 = new JLabel( "After loading, sort by:" );
        label1.setAlignmentX( Component.LEFT_ALIGNMENT );
        stepComponent.add( label1 );
        stepComponent.add( Box.createVerticalGlue() );


        ArrayList<SortOption> sortOptions = Settings.getSortOptions();
        JComboBox sortChoice = new JComboBox( sortOptions.toArray() );
        for ( int i = 0; i < sortOptions.size(); i++ ) {
            if ( sortOptions.get(i).getSortCode() == dataModel.getSortCode() ) {
                sortChoice.setSelectedIndex( i );
                break;
            }
        }
        sortChoice.setPreferredSize( new Dimension( 120, 25 ) );
        sortChoice.setMaximumSize( new Dimension( 220, 30 ) );
        stepComponent.add( sortChoice );
        sortChoice.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                JComboBox cb = (JComboBox) e.getSource();
                SortOption sortOption = (SortOption) cb.getSelectedItem();
                sortOptionPicked( sortOption );
            }
        } );


        stepComponent.add( Box.createVerticalGlue() );
        //JPanel fillerPanel = new JPanel();
        //stepComponent.add( fillerPanel ); // helps with the layout...

        return stepComponent;
    }


    /**
     *  Required by the AbstractSetp but not used.
     */
    public void prepareRendering() {
    }


    /**
     * Respond to a pick of a sort option
     * @param sortOption the SortOption that was picked
     */
    private void sortOptionPicked( SortOption sortOption ) {
        logger.fine( String.format( "Option %s with sortCode %d picked", sortOption.getDescription(), sortOption.getSortCode() ) );
        dataModel.setSortCode( sortOption.getSortCode() );
        Settings.lastSortChoice = sortOption.getSortCode();
    }
}

