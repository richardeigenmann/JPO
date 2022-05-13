package org.jpo.gui;

import net.javaprog.ui.wizard.AbstractStep;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortOption;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
CameraDownloadWizardStep5.java: the fourth step in the download from Camera Wizard

Copyright (C) 2007 - 2022  Richard Eigenmann.
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
 *  The fourth step in the download from camera dialog asks for the storage location on the disk.
 */
public class CameraDownloadWizardStep5
        extends AbstractStep {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CameraDownloadWizardStep5.class.getName() );


    /**
     * The fourth step in the download from camera dialog asks for the storage location on the disk.
     * @param dataModel The data model where the settings are to be saved
     */
    public CameraDownloadWizardStep5( CameraDownloadWizardData dataModel ) {
        super( "Sorting", "How to Sort" );
        this.dataModel = dataModel;
    }

    /**
     *  Holds a reference to the data used by the wizard
     */
    private final CameraDownloadWizardData dataModel;


    /**
     *  Returns the component that visualises the user interactable stuff for this step of the wizard.
     * @return the component
     */
    @Override
    protected JComponent createComponent() {
        final JPanel stepComponent = new JPanel();
        stepComponent.setLayout( new BoxLayout( stepComponent, BoxLayout.PAGE_AXIS ) );
        final JLabel label1 = new JLabel( "After loading, sort by:" );
        label1.setAlignmentX( Component.LEFT_ALIGNMENT );
        stepComponent.add( label1 );
        stepComponent.add( Box.createVerticalGlue() );


        final List<SortOption> sortOptions = Settings.getSortOptions();
        final JComboBox <SortOption> sortChoice = new JComboBox<>( sortOptions.toArray( new SortOption[0]) );
        for ( int i = 0; i < sortOptions.size(); i++ ) {
            if ( sortOptions.get(i).getSortCode() == dataModel.getSortCode() ) {
                sortChoice.setSelectedIndex( i );
                break;
            }
        }
        sortChoice.setPreferredSize( new Dimension( 120, 25 ) );
        sortChoice.setMaximumSize( new Dimension( 220, 30 ) );
        stepComponent.add( sortChoice );
        sortChoice.addActionListener(( ActionEvent e ) -> {
            final JComboBox<SortOption> cb = (JComboBox<SortOption>) e.getSource();
            SortOption sortOption = (SortOption) cb.getSelectedItem();
            if ( sortOption == null ) {
                sortOption = sortOptions.get(3); // by creation time
            }
            sortOptionPicked( sortOption );
        });


        stepComponent.add( Box.createVerticalGlue() );

        return stepComponent;
    }


    /**
     *  Required by the AbstractStep but not used.
     */
    @Override
    public void prepareRendering() {
        // noop
    }


    /**
     * Respond to a pick of a sort option
     * @param sortOption the SortOption that was picked
     */
    private void sortOptionPicked( final SortOption sortOption ) {
        LOGGER.log(Level.FINE, "Option {0} with sortCode {1} picked", new Object[]{sortOption.getDescription(), sortOption.getSortCode()});
        dataModel.setSortCode(sortOption.getSortCode());
        Settings.setLastSortChoice(sortOption.getSortCode());
    }
}

