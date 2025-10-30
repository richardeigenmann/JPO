package org.jpo.gui;

import net.javaprog.ui.wizard.AbstractStep;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.logging.Logger;

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
 * The third step in the download from camera dialog asks whether to create a
 * new Group, the description for the new Group and the node for which the
 * operation is to run. If presents a checkbox whether to create a new group. If
 * this is ticked then the textfield for the new group name is made visible,
 * otherwise it is hidden.
 */
public class CameraDownloadWizardStep3
        extends AbstractStep {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CameraDownloadWizardStep3.class.getName() );

    /**
     * @param dataModel The data model
     */
    public CameraDownloadWizardStep3(final CameraDownloadWizardData dataModel) {
        super(JpoResources.getResource("DownloadCameraWizardStep3Title"), JpoResources.getResource("DownloadCameraWizardStep3Description"));
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
        stepComponent.setLayout( new BoxLayout( stepComponent, BoxLayout.PAGE_AXIS ) );
        final JCheckBox createSubGroupCheckBox = new JCheckBox(JpoResources.getResource("DownloadCameraWizardStep3Text0"));
        createSubGroupCheckBox.setAlignmentX( Component.LEFT_ALIGNMENT );
        final JLabel titleLabel = new JLabel(JpoResources.getResource("DownloadCameraWizardStep3Text1"));
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
            selectNodeLabel.setText(JpoResources.getResource("DownloadCameraWizardStep3Text2a"));
        } else {
            selectNodeLabel.setText(JpoResources.getResource("DownloadCameraWizardStep3Text2b"));
        }

        createSubGroupCheckBox.addChangeListener(( ChangeEvent e ) -> {
            dataModel.setShouldCreateNewGroup( createSubGroupCheckBox.isSelected() );
            titleLabel.setVisible( createSubGroupCheckBox.isSelected() );
            newGroupName.setVisible( createSubGroupCheckBox.isSelected() );
            secondStrut.setVisible( createSubGroupCheckBox.isSelected() );
            if ( createSubGroupCheckBox.isSelected() ) {
                selectNodeLabel.setText(JpoResources.getResource("DownloadCameraWizardStep3Text2a"));
            } else {
                selectNodeLabel.setText(JpoResources.getResource("DownloadCameraWizardStep3Text2b"));
            }
        });
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

        final JTree collectionJTree = new JTree();
        collectionJTree.setModel( dataModel.getTreeModel() );
        collectionJTree.setEditable( false );
        collectionJTree.addTreeSelectionListener(( TreeSelectionEvent e ) -> {
            LOGGER.fine( String.format( "listening to a value changed event e: %s", e.toString() ) );
            // Are we trying to get the last clicked node?
            final SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) collectionJTree.getLastSelectedPathComponent();
            try {
                if ( node.getUserObject() instanceof GroupInfo ) {
                    dataModel.setTargetNode( node );
                    setCanGoNext( true );
                } else {
                    dataModel.setTargetNode( null );
                    setCanGoNext( false );
                }
            } catch ( NullPointerException x ) {
                LOGGER.fine( String.format( "The listener on the Download Wizard picked up a node change event on the node tree but got a NPE: %s", x.getMessage() ) );
                setCanGoNext( false );
            }
        });

        // if there is only a root node in the collection, select it by default
        final Object root = dataModel.getTreeModel().getRoot();
        if ( dataModel.getTreeModel().isLeaf( root ) ) {
            final TreePath rp = new TreePath( root );
            collectionJTree.setSelectionPath( rp );
        }

        // if no node is selected, select the root node
        if ( collectionJTree.getSelectionCount() < 1 ) {
            final TreePath rp = new TreePath( root );
            collectionJTree.setSelectionPath( rp );
        }

        final JScrollPane jsp = new JScrollPane( collectionJTree );
        stepComponent.add( jsp );
        jsp.setAlignmentX( Component.LEFT_ALIGNMENT );
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
