package jpo.gui;

import jpo.dataModel.Settings;
import jpo.gui.swing.CollectionJTree;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import net.javaprog.ui.wizard.*;
import javax.swing.*;
import java.awt.Dimension;
import javax.swing.tree.TreePath;


/* CameraDownloadWizardStep3.java: the third step in the download from Camera Wizard

Copyright (C) 2007-2008  Richard Eigenmann.
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
 *  The third step in the download from camera dialog asks whether to create a new Group, the description for the new Group and
 *  the node for which the operation is to run. If presents a checkbox whether to create a new group. If this is ticked then
 *  the textfield for the new group name is made visible, otherwise it is hidden.
 */
public class CameraDownloadWizardStep3 extends AbstractStep {

    /**
     *
     * @param dataModel
     */
    public CameraDownloadWizardStep3( CameraDownloadWizardData dataModel ) {
        //pass step title and description
        super( Settings.jpoResources.getString( "DownloadCameraWizardStep3Title" ), Settings.jpoResources.getString( "DownloadCameraWizardStep3Description" ) );
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
        final JCheckBox createSubGroupCheckBox = new JCheckBox( Settings.jpoResources.getString( "DownloadCameraWizardStep3Text0" ) );
        createSubGroupCheckBox.setAlignmentX( Component.LEFT_ALIGNMENT );
        final JLabel titleLabel = new JLabel( Settings.jpoResources.getString( "DownloadCameraWizardStep3Text1" ) );
        titleLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
        final JTextField newGroupName = new JTextField();
        newGroupName.setAlignmentX( Component.LEFT_ALIGNMENT );
        final JLabel selectNodeLabel = new JLabel();
        final Component secondStrut = Box.createVerticalStrut( 8 );

        // set the initial visibility
        createSubGroupCheckBox.setSelected( dataModel.getShouldCreateNewGroup() );
        dataModel.setShouldCreateNewGroup( createSubGroupCheckBox.isSelected() );
        titleLabel.setVisible( createSubGroupCheckBox.isSelected() );
        newGroupName.setVisible( createSubGroupCheckBox.isSelected() );
        secondStrut.setVisible( createSubGroupCheckBox.isSelected() );
        if ( createSubGroupCheckBox.isSelected() ) {
            selectNodeLabel.setText( Settings.jpoResources.getString( "DownloadCameraWizardStep3Text2a" ) );
        } else {
            selectNodeLabel.setText( Settings.jpoResources.getString( "DownloadCameraWizardStep3Text2b" ) );
        }

        createSubGroupCheckBox.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                dataModel.setShouldCreateNewGroup( createSubGroupCheckBox.isSelected() );
                titleLabel.setVisible( createSubGroupCheckBox.isSelected() );
                newGroupName.setVisible( createSubGroupCheckBox.isSelected() );
                secondStrut.setVisible( createSubGroupCheckBox.isSelected() );
                if ( createSubGroupCheckBox.isSelected() ) {
                    selectNodeLabel.setText( Settings.jpoResources.getString( "DownloadCameraWizardStep3Text2a" ) );
                } else {
                    selectNodeLabel.setText( Settings.jpoResources.getString( "DownloadCameraWizardStep3Text2b" ) );
                }
            }
        } );
        stepComponent.add( createSubGroupCheckBox );
        stepComponent.add( Box.createVerticalStrut( 8 ) );
        stepComponent.add( titleLabel );
        stepComponent.add( newGroupName );
        newGroupName.setText( dataModel.getNewGroupDescription() );
        newGroupName.setMaximumSize( new Dimension( 800, 20 ) );
        newGroupName.addFocusListener( new FocusAdapter() {

            @Override
            public void focusLost( FocusEvent e ) {
                dataModel.setNewGroupDescription( newGroupName.getText() );
            }
        } );
        stepComponent.add( newGroupName );
        stepComponent.add( secondStrut );
        stepComponent.add( selectNodeLabel );

        final JTree collectionJTree = new CollectionJTree();
        collectionJTree.setModel( dataModel.getTreeModel() );
        collectionJTree.setEditable( false );
        collectionJTree.addTreeSelectionListener( new TreeSelectionListener() {

            public void valueChanged( TreeSelectionEvent e ) {
                SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) collectionJTree.getLastSelectedPathComponent();
                try {
                    if ( node.getUserObject() instanceof GroupInfo ) {
                        dataModel.setTargetNode( node );
                        setCanGoNext( true );
                    } else {
                        dataModel.setTargetNode( null );
                        setCanGoNext( false );
                    }
                } catch ( NullPointerException x ) {
                    dataModel.setTargetNode( null );
                    setCanGoNext( false );
                }
            }
        } );

        // if there is only a root node in the collection, select it by default
        Object root = dataModel.getTreeModel().getRoot();
        if ( dataModel.getTreeModel().isLeaf( root ) ) {
            TreePath rp = new TreePath( root );
            collectionJTree.setSelectionPath( rp );
        }

        // if no node is selected, select the root node
        if ( collectionJTree.getSelectionCount() < 1 ) {
            TreePath rp = new TreePath( root );
            collectionJTree.setSelectionPath( rp );
        }


        JScrollPane jsp = new JScrollPane( collectionJTree );
        stepComponent.add( jsp );
        jsp.setAlignmentX( Component.LEFT_ALIGNMENT );
        return stepComponent;
    }

    /**
     *  Required by the AbstractSetp but not used.
     */
    public void prepareRendering() {
    }
}