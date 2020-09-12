package org.jpo.gui;

import org.jpo.datamodel.*;
import org.jpo.eventbus.ShowAutoAdvanceDialogRequest;
import org.jpo.gui.swing.WholeNumberField;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;


/*
 Copyright (C) 2017-2017,  Richard Eigenmann, Zürich
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
 * Brings up an Auto Advance Dialog and sends off an new request if successful.
 * 
 * Note that if the request's parentComponent is a ComponentMock then the actual
 * modal dialog is skipped.
 *
 * @author Richard Eigenmann
 */
public class AutoAdvanceDialog {

    private final ShowAutoAdvanceDialogRequest request;

    public AutoAdvanceDialog( ShowAutoAdvanceDialogRequest request ) {
        this.request = request;
        doAutoAdvanceDialog();
    }

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( AutoAdvanceDialog.class.getName() );

    /**
     * method that brings up a dialog box and asks the user how he would like
     * auto advance to work
     */
    private void doAutoAdvanceDialog() {
        JRadioButton randomAdvanceJRadioButton = new JRadioButton( Settings.jpoResources.getString( "randomAdvanceJRadioButtonLabel" ) );
        JRadioButton sequentialAdvanceJRadioButton = new JRadioButton( Settings.jpoResources.getString( "sequentialAdvanceJRadioButtonLabel" ) );
        ButtonGroup advanceButtonGroup = new ButtonGroup();
        advanceButtonGroup.add( randomAdvanceJRadioButton );
        advanceButtonGroup.add( sequentialAdvanceJRadioButton );
        randomAdvanceJRadioButton.setSelected( true );

        JRadioButton restrictToGroupJRadioButton = new JRadioButton( Settings.jpoResources.getString( "restrictToGroupJRadioButtonLabel" ) );
        JRadioButton useAllPicturesJRadioButton = new JRadioButton( Settings.jpoResources.getString( "useAllPicturesJRadioButtonLabel" ) );
        ButtonGroup cycleButtonGroup = new ButtonGroup();
        cycleButtonGroup.add( restrictToGroupJRadioButton );
        cycleButtonGroup.add( useAllPicturesJRadioButton );
        useAllPicturesJRadioButton.setSelected( true );

        JLabel timerSecondsJLabel = new JLabel( Settings.jpoResources.getString( "timerSecondsJLabelLabel" ) );
        WholeNumberField timerSecondsField = new WholeNumberField( 4, 3 );
        timerSecondsField.setPreferredSize( new Dimension( 50, 20 ) );
        timerSecondsField.setMaximumSize( new Dimension( 50, 20 ) );
        Object[] objects = { randomAdvanceJRadioButton,
            sequentialAdvanceJRadioButton,
            restrictToGroupJRadioButton,
            useAllPicturesJRadioButton,
            timerSecondsJLabel,
            timerSecondsField
        };

        Component parentComponent = request.parentComponent;
        int selectedValue = showDialog( parentComponent, objects );

        try {
            NodeNavigator mySetOfNodes;
            int myIndex;
            if ( selectedValue == 0 ) {
                if ( randomAdvanceJRadioButton.isSelected() ) {
                    if ( useAllPicturesJRadioButton.isSelected() ) {
                        SortableDefaultMutableTreeNode rootNode = Settings.getPictureCollection().getRootNode();
                        mySetOfNodes
                                = new RandomNavigator(
                                        rootNode.getChildPictureNodes( true ),
                                        String.format( "Randomised pictures from %s",
                                                Settings.getPictureCollection().getRootNode().toString() ) );
                    } else {
                        mySetOfNodes = new RandomNavigator(
                                request.currentNode.getParent().getChildPictureNodes( true ),
                                String.format( "Randomised pictures from %s",
                                        ( request.currentNode.getParent() ).toString() ) );
                    }
                } else {
                    if ( useAllPicturesJRadioButton.isSelected() ) {
                        mySetOfNodes = new FlatGroupNavigator( request.currentNode.getRoot() );
                    } else {
                        mySetOfNodes = new FlatGroupNavigator( request.currentNode.getParent() );
                    }

                    myIndex = 0;
                    request.autoAdvanceTarget.showNode( mySetOfNodes, myIndex );
                }

                myIndex = 0;
                request.autoAdvanceTarget.showNode( mySetOfNodes, myIndex );
                request.autoAdvanceTarget.startAdvanceTimer( timerSecondsField.getValue() );
            }
        } catch ( NullPointerException ex ) {
            LOGGER.severe( "NPE!" );
        }
    }

    private int showDialog( Component parentComponent, Object message ) {
        // hack to facilitate unit testing
        if ( parentComponent instanceof ComponentMock ) {
            return 0;
        }
        return JOptionPane.showOptionDialog(
                parentComponent,
                message,
                Settings.jpoResources.getString( "autoAdvanceDialogTitle" ),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null );
    }

}
