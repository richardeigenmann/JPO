package org.jpo.gui;

import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SingleNodeNavigator;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.gui.swing.Thumbnail;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
 GroupInfoEditor.java:  Controller and Vie for editing group properties
 Copyright (C) 2002 - 2015  Richard Eigenmann.
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
 * Creates a Frame and allows the field(s) of a group to be edited.
 */
public class GroupInfoEditor {

    /**
     * JFrame that holds all the dialog components for editing the window.
     */
    private final JFrame jFrame = new JFrame( Settings.jpoResources.getString( "GroupInfoEditorHeading" ) );

    /**
     * the node being edited
     */
    private final SortableDefaultMutableTreeNode editNode;

    /**
     * Constructor that creates the JFrame and objects.
     *
     * @param editNode	The node being edited.
     */
    public GroupInfoEditor( final SortableDefaultMutableTreeNode editNode ) {
        this.editNode = editNode;

        jFrame.addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );

        initComponents();
        populateFields();
    }

    /**
     * TextArea to edit the description
     */
    private final JTextArea descriptionJTextArea = new JTextArea();

    private void initComponents() {

        JPanel jPanel = new JPanel();
        jPanel.setLayout( new MigLayout() );

        JLabel descriptionJLabel = new JLabel( Settings.jpoResources.getString( "groupDescriptionLabel" ) );
        jPanel.add( descriptionJLabel );

        descriptionJTextArea.setPreferredSize( new Dimension( 400, 150 ) );
        descriptionJTextArea.setWrapStyleWord( true );
        descriptionJTextArea.setLineWrap( true );
        descriptionJTextArea.setEditable( true );
        jPanel.add( descriptionJTextArea, "wrap" );

        JButton OkJButton = new JButton( Settings.jpoResources.getString( "genericOKText" ) );
        OkJButton.setPreferredSize( Settings.defaultButtonDimension );
        OkJButton.setMinimumSize( Settings.defaultButtonDimension );
        OkJButton.setMaximumSize( Settings.defaultButtonDimension );
        OkJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        OkJButton.setAlignmentX( Component.LEFT_ALIGNMENT );
        OkJButton.addActionListener(( ActionEvent e ) -> handleOkButtonClick());
        OkJButton.setDefaultCapable( true );
        jFrame.getRootPane().setDefaultButton( OkJButton );
        jPanel.add( OkJButton, "tag ok, span, split 2" );

        JButton CancelButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        CancelButton.setPreferredSize( Settings.defaultButtonDimension );
        CancelButton.setMinimumSize( Settings.defaultButtonDimension );
        CancelButton.setMaximumSize( Settings.defaultButtonDimension );
        CancelButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        CancelButton.setAlignmentX( Component.RIGHT_ALIGNMENT );
        CancelButton.addActionListener(( ActionEvent e ) -> getRid());
        jPanel.add( CancelButton, "tag cancel" );

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab( "Properties", jPanel );

        NodeStatisticsController nodeStatisticsController = new NodeStatisticsController();
        nodeStatisticsController.updateStats( editNode );
        tabbedPane.addTab( "Statistics", nodeStatisticsController.getJComponent() );

        ThumbnailController thumbnailController = new ThumbnailController(new Thumbnail(), Settings.thumbnailSize );
        thumbnailController.setNode( new SingleNodeNavigator( editNode ), 0 );
        tabbedPane.addTab( "Thumbnail", thumbnailController.getThumbnail() );

        jFrame.getContentPane().add( tabbedPane );
        jFrame.pack();
        jFrame.setLocationRelativeTo( Settings.anchorFrame );
        jFrame.setVisible( true );
    }

    private void populateFields() {
        final GroupInfo gi = ( (GroupInfo) editNode.getUserObject() );
        descriptionJTextArea.setText( gi.getGroupName() );

    }

    private void handleOkButtonClick() {
        final GroupInfo gi = ( (GroupInfo) editNode.getUserObject() );
        gi.setGroupName( descriptionJTextArea.getText() );
        editNode.getPictureCollection().sendNodeChanged( editNode );
        getRid();
    }

    /**
     * method that closes the window.
     */
    private void getRid() {
        jFrame.setVisible( false );
        jFrame.dispose();
    }
}
