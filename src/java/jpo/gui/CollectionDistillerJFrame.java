package jpo.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.XmlDistiller;
import java.util.logging.Logger;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import jpo.EventBus.CopyLocationsChangedEvent;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.RecentCollectionsChangedEvent;

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
    private static final Logger LOGGER = Logger.getLogger( CollectionDistillerJFrame.class.getName() );

    /**
     * the node from which to start the export
     */
    private final SortableDefaultMutableTreeNode startNode;

    /**
     * text field that holds the directory that the group is to be exported to
     *
     */
    private final DirectoryChooser targetDirChooser
            = new DirectoryChooser( Settings.jpoResources.getString( "collectionExportChooserTitle" ),
                    DirectoryChooser.DIR_MUST_BE_WRITABLE );

    /**
     * text field that holds the filename of the target XML file
     *
     */
    private final JTextField xmlFileNameJTextField = new JTextField();

    /**
     * Tickbox that indicates whether the pictures are to be copied to the
     * target directory structure.
     *
     */
    private final JCheckBox exportPicsJCheckBox = new JCheckBox( Settings.jpoResources.getString( "collectionExportPicturesText" ) );

    /**
     * button to start the export
     *
     */
    private final JButton exportJButton = new JButton( Settings.jpoResources.getString( "genericExportButtonText" ) );

    /**
     * button to cancel the dialog
     *
     */
    private final JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );

    /**
     * Constructor for the Export Dialog window.
     *
     * @param startNode The group node that the user wants the export to be done
     * on.
     */
    public CollectionDistillerJFrame( SortableDefaultMutableTreeNode startNode ) {
        super( Settings.jpoResources.getString( "CollectionDistillerJFrameFrameHeading" ) );
        this.startNode = startNode;
        initComponents();
    }

    public final void initComponents() {

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
                //logger.info( "CollectionDistillerJFrame:xmlFileNameJTestField.shouldYieldFocus was triggered" );
                String validationFile = ( (JTextField) input ).getText();
                if ( !validationFile.toUpperCase().endsWith( ".XML" ) ) {
                    ( (JTextField) input ).setText( validationFile + ".xml" );
                }
                return true;
            }

            @Override
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
     * method that gets rid of this JFrame
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }

    /**
     * method that outputs the selected group to a directory
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

        Settings.memorizeCopyLocation( targetFile.getParent() );
        JpoEventBus.getInstance().post( new CopyLocationsChangedEvent() );
        Settings.pushRecentCollection( targetFile.toString() );
        JpoEventBus.getInstance().post( new RecentCollectionsChangedEvent() );
        JOptionPane.showMessageDialog( Settings.anchorFrame,
                Settings.jpoResources.getString( "collectionSaveBody" ) + targetFile.toString(),
                Settings.jpoResources.getString( "collectionSaveTitle" ),
                JOptionPane.INFORMATION_MESSAGE );

    }

    /**
     * method that analyses the user initiated action and performs what the user
     * requested
     *
     */
    @Override
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
