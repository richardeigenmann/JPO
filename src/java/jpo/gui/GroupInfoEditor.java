package jpo.gui;

import jpo.dataModel.SingleNodeBrowser;
import jpo.dataModel.Settings;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;

/*
GroupInfoEditor.java:  Controller and Vie for editing group properties
Copyright (C) 2002 - 2009  Richard Eigenmann.
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
 *   Creates a Frame and allows the field(s) of a group to be edited.
 */
public class GroupInfoEditor {

    /**
     *   JFrame that holds all the dialog components for editing the window.
     */
    private JFrame jFrame = new JFrame( Settings.jpoResources.getString( "GroupInfoEditorHeading" ) );

    /**
     *  the node being edited
     */
    private SortableDefaultMutableTreeNode editNode;


    /**
     *   Constructor that creates the JFrame and objects.
     *
     *   @param   editNode	The node being edited.
     */
    public GroupInfoEditor( final SortableDefaultMutableTreeNode editNode ) {
        this.editNode = editNode;

        jFrame.addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );

        JPanel jPanel = new JPanel();
        jPanel.setLayout( new MigLayout() );

        JLabel descriptionJLabel = new JLabel( Settings.jpoResources.getString( "groupDescriptionLabel" ) );
        jPanel.add( descriptionJLabel );


        final GroupInfo gi = ( (GroupInfo) editNode.getUserObject() );

        final JTextArea descriptionJTextArea = new JTextArea();
        descriptionJTextArea.setText( gi.getGroupName() );
        descriptionJTextArea.setPreferredSize( new Dimension( 400, 150 ) );
        descriptionJTextArea.setWrapStyleWord( true );
        descriptionJTextArea.setLineWrap( true );
        descriptionJTextArea.setEditable( true );
        jPanel.add( descriptionJTextArea, "wrap" );



        JLabel lowresLocationJLabel = new JLabel( Settings.jpoResources.getString( "lowresLocationLabel" ) );
        jPanel.add( lowresLocationJLabel );

        final JTextField lowresLocationJTextField = new JTextField();
        Dimension inputDimension = new Dimension( 400, 20 );
        lowresLocationJTextField.setPreferredSize( inputDimension );
        lowresLocationJTextField.setText( gi.getLowresLocation() );
        jPanel.add( lowresLocationJTextField, "wrap" );

        JPanel buttonJPanel = new JPanel();

        JButton OkJButton = new JButton( Settings.jpoResources.getString( "genericOKText" ) );
        OkJButton.setPreferredSize( Settings.defaultButtonDimension );
        OkJButton.setMinimumSize( Settings.defaultButtonDimension );
        OkJButton.setMaximumSize( Settings.defaultButtonDimension );
        OkJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        OkJButton.setAlignmentX( Component.LEFT_ALIGNMENT );
        OkJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                gi.setGroupName( descriptionJTextArea.getText() );
                gi.setLowresLocation( lowresLocationJTextField.getText() );
                editNode.getPictureCollection().getTreeModel().nodeChanged( editNode );
                getRid();
            }
        } );
        OkJButton.setDefaultCapable( true );
        jFrame.getRootPane().setDefaultButton( OkJButton );
        jPanel.add( OkJButton, "tag ok, span, split 2" );

        JButton CancelButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        CancelButton.setPreferredSize( Settings.defaultButtonDimension );
        CancelButton.setMinimumSize( Settings.defaultButtonDimension );
        CancelButton.setMaximumSize( Settings.defaultButtonDimension );
        CancelButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        CancelButton.setAlignmentX( Component.RIGHT_ALIGNMENT );
        CancelButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                getRid();
            }
        } );
        jPanel.add( CancelButton, "tag cancel" );

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab( "Properties", jPanel );

        CollectionPropertiesJPanel statsJPanel = new CollectionPropertiesJPanel();
        statsJPanel.updateStats( editNode );
        tabbedPane.addTab( "Statistics", statsJPanel );

        ThumbnailController thumbnailController = new ThumbnailController();
        thumbnailController.setNode( new SingleNodeBrowser( editNode), 0 );
        tabbedPane.addTab( "Thumbnail", thumbnailController.getThumbnail() );


        jFrame.getContentPane().add( tabbedPane );
        jFrame.pack();
        jFrame.setLocationRelativeTo( Settings.anchorFrame );
        jFrame.setVisible( true );
    }


    /**
     *  method that closes the window.
     */
    private void getRid() {
        jFrame.setVisible( false );
        jFrame.dispose();
    }
}
