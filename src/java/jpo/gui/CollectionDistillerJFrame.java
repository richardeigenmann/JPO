package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.XmlDistiller;
import jpo.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.logging.Logger;

/*
CollectionDistillerJFrame.java:  creates a GUI for the export

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
 * class that generates a GUI for the export of a collection
 */
class CollectionDistillerJFrame extends JFrame implements ActionListener {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( CollectionDistillerJFrame.class.getName() );

    /**
     *  the node from which to start the export
     */
    private SortableDefaultMutableTreeNode startNode;

    /**
     *  text field that holds the directory that the group is to be exported to
     **/
    private DirectoryChooser targetDirChooser =
            new DirectoryChooser( Settings.jpoResources.getString( "collectionExportChooserTitle" ),
            DirectoryChooser.DIR_MUST_BE_WRITABLE );

    /**
     *  text field that holds the filename of the target XML file
     **/
    private JTextField xmlFileNameJTextField = new JTextField();

    /**
     *  tickbox that indicates whether the pictures are to be copied to the
     *  target directory structure.
     **/
    private JCheckBox exportPicsJCheckBox = new JCheckBox( Settings.jpoResources.getString( "collectionExportPicturesText" ) );

    /**
     *  button to start the export
     **/
    private JButton exportJButton = new JButton( Settings.jpoResources.getString( "genericExportButtonText" ) );

    /**
     *  button to cancel the dialog
     **/
    private JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );


    /**
     *  Constructor for the Export Dialog windod.
     *
     *  @param startNode  The group node that the user wants the export to be done on.
     */
    public CollectionDistillerJFrame( SortableDefaultMutableTreeNode startNode ) {
        super( Settings.jpoResources.getString( "CollectionDistillerJFrameFrameHeading" ) );
        this.startNode = startNode;

        setSize( 460, 300 );
        setLocationRelativeTo( Settings.anchorFrame );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );

        JPanel contentJPanel = new javax.swing.JPanel();
        contentJPanel.setLayout( new GridBagLayout() );

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;

        JLabel targetDirJLabel = new JLabel( Settings.jpoResources.getString( "genericTargetDirText" ) );
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.insets = new Insets( 4, 4, 4, 4 );
        contentJPanel.add( targetDirJLabel, constraints );

        // create the JTextField that holds the reference to the targetDirChooser
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.weightx = 0.8;
        constraints.insets = new Insets( 4, 4, 4, 4 );
        contentJPanel.add( targetDirChooser, constraints );


        JLabel xmlFileNameJLabel = new JLabel( Settings.jpoResources.getString( "xmlFileNameLabel" ) );
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.insets = new Insets( 4, 4, 4, 4 );
        contentJPanel.add( xmlFileNameJLabel, constraints );

        xmlFileNameJTextField.setPreferredSize( new Dimension( 240, 20 ) );
        xmlFileNameJTextField.setMinimumSize( new Dimension( 240, 20 ) );
        xmlFileNameJTextField.setMaximumSize( new Dimension( 400, 20 ) );
        xmlFileNameJTextField.setText( "PictureList.xml" );
        xmlFileNameJTextField.setInputVerifier( new InputVerifier() {

            @Override
            public boolean shouldYieldFocus( JComponent input ) {
                logger.info( "CollectionDistillerJFrame:xmlFileNameJTestField.shouldYieldFocus was triggered" );
                String validationFile = ( (JTextField) input ).getText();
                if ( !validationFile.toUpperCase().endsWith( ".XML" ) ) {
                    ( (JTextField) input ).setText( validationFile + ".xml" );
                }
                return true;
            }


            public boolean verify( JComponent input ) {
                return true;
            }
        } );

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.weightx = 0.8;
        constraints.insets = new Insets( 4, 4, 4, 4 );
        contentJPanel.add( xmlFileNameJTextField, constraints );

        exportPicsJCheckBox.setSelected( true );
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 2;
        constraints.weightx = 1.0;
        constraints.insets = new Insets( 4, 4, 4, 4 );
        contentJPanel.add( exportPicsJCheckBox, constraints );

        // crate a JPanel for the buttons
        JPanel buttonJPanel = new JPanel();

        // add the export button
        exportJButton.setPreferredSize( Settings.defaultButtonDimension );
        exportJButton.setMinimumSize( Settings.defaultButtonDimension );
        exportJButton.setMaximumSize( Settings.defaultButtonDimension );
        exportJButton.setDefaultCapable( true );
        this.getRootPane().setDefaultButton( exportJButton );
        exportJButton.addActionListener( this );
        buttonJPanel.add( exportJButton );

        // add the cancel button
        cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelJButton.addActionListener( this );
        buttonJPanel.add( cancelJButton );

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 2;
        constraints.weightx = 0.5;
        constraints.insets = new Insets( 4, 4, 4, 4 );
        contentJPanel.add( buttonJPanel, constraints );

        setContentPane( contentJPanel );

        pack();
        setVisible( true );
    }


    /**
     *  method that get's rid of this JFrame
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }


    /**
     *  method that outputs the selected group to a directory
     */
    private void exportToDirectory() {
        File exportDirectory = targetDirChooser.getDirectory();

        if ( !exportDirectory.exists() ) {
            try {
                exportDirectory.mkdirs();
            } catch ( SecurityException e ) {
                e.printStackTrace();
                JOptionPane.showMessageDialog( this, "Could not create directory " + exportDirectory,
                        "SecurityException",
                        JOptionPane.ERROR_MESSAGE );
                return;
            }
        }

        File targetFile = new File( exportDirectory, xmlFileNameJTextField.getText() );

        if ( targetFile.exists() ) {
            int answer = JOptionPane.showConfirmDialog( Settings.anchorFrame,
                    Settings.jpoResources.getString( "confirmSaveAs" ),
                    Settings.jpoResources.getString( "genericWarning" ),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE );
            if ( answer == JOptionPane.CANCEL_OPTION ) {
                return;
            }
        }
        new XmlDistiller( targetFile, startNode, exportPicsJCheckBox.isSelected(), true );

        Settings.memorizeCopyLocation( targetFile.getParent().toString() );
        Settings.pushRecentCollection( targetFile.toString() );
        JOptionPane.showMessageDialog( Settings.anchorFrame,
                Settings.jpoResources.getString( "collectionSaveBody" ) + targetFile.toString(),
                Settings.jpoResources.getString( "collectionSaveTitle" ),
                JOptionPane.INFORMATION_MESSAGE );

    }


    /**
     *  method that analyses the user initiated action and performs what the user requested
     **/
    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == cancelJButton ) {
            getRid();
        }
        if ( e.getSource() == exportJButton ) {
            exportToDirectory();
            getRid();
        }
    }


    ;
}
