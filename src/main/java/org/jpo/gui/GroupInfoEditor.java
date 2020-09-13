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
 Copyright (C) 2002 - 2020  Richard Eigenmann.
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

        final JPanel jPanel = new JPanel();
        jPanel.setLayout( new MigLayout() );

        final JLabel descriptionJLabel = new JLabel(Settings.jpoResources.getString("groupDescriptionLabel"));
        jPanel.add(descriptionJLabel);

        descriptionJTextArea.setPreferredSize(new Dimension(400, 150));
        descriptionJTextArea.setWrapStyleWord(true);
        descriptionJTextArea.setLineWrap(true);
        descriptionJTextArea.setEditable(true);
        jPanel.add(descriptionJTextArea, "wrap");

        final JButton okJButton = new JButton(Settings.jpoResources.getString("genericOKText"));
        okJButton.setPreferredSize(Settings.getDefaultButtonDimension());
        okJButton.setMinimumSize(Settings.getDefaultButtonDimension());
        okJButton.setMaximumSize(Settings.getDefaultButtonDimension());
        okJButton.setBorder(BorderFactory.createRaisedBevelBorder());
        okJButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        okJButton.addActionListener((ActionEvent e) -> handleOkButtonClick());
        okJButton.setDefaultCapable(true);
        jFrame.getRootPane().setDefaultButton(okJButton);
        jPanel.add(okJButton, "tag ok, span, split 2");

        final JButton cancelButton = new JButton(Settings.jpoResources.getString("genericCancelText"));
        cancelButton.setPreferredSize(Settings.getDefaultButtonDimension());
        cancelButton.setMinimumSize(Settings.getDefaultButtonDimension());
        cancelButton.setMaximumSize(Settings.getDefaultButtonDimension());
        cancelButton.setBorder(BorderFactory.createRaisedBevelBorder());
        cancelButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
        cancelButton.addActionListener((ActionEvent e) -> getRid());
        jPanel.add(cancelButton, "tag cancel");

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Properties", jPanel);

        final NodeStatisticsController nodeStatisticsController = new NodeStatisticsController();
        nodeStatisticsController.updateStats(editNode);
        tabbedPane.addTab("Statistics", nodeStatisticsController.getJComponent());

        final ThumbnailController thumbnailController = new ThumbnailController(new Thumbnail(), Settings.getThumbnailSize());
        thumbnailController.setNode(new SingleNodeNavigator(editNode), 0);
        tabbedPane.addTab( "Thumbnail", thumbnailController.getThumbnail() );

        jFrame.getContentPane().add( tabbedPane );
        jFrame.pack();
        jFrame.setLocationRelativeTo(Settings.getAnchorFrame());
        jFrame.setVisible(true);
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
