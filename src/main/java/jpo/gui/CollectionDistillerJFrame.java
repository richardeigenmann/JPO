package jpo.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
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
import javax.swing.text.JTextComponent;
import jpo.EventBus.CopyLocationsChangedEvent;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.RecentCollectionsChangedEvent;
import jpo.dataModel.JpoWriter;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import net.miginfocom.swing.MigLayout;

/*
 Copyright (C) 2002 - 2018  Richard Eigenmann.
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
 * Frame to capture the details of the collection export
 */
public class CollectionDistillerJFrame extends JFrame {

    /**
     * Size for this frame
     */
    private final static Dimension FRAME_SIZE = new Dimension( 460, 300 );

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
     * JCheckBox that indicates whether the pictures are to be copied to the
     * target directory structure.
     *
     */
    private final JCheckBox exportPicsJCheckBox = new JCheckBox( Settings.jpoResources.getString( "collectionExportPicturesText" ) );

    /**
     * Constructor for the Export Dialog window.
     *
     * @param startNode The group node that the user wants the export to be done
     * on.
     */
    CollectionDistillerJFrame(SortableDefaultMutableTreeNode startNode) {
        super( Settings.jpoResources.getString( "CollectionDistillerJFrameFrameHeading" ) );
        this.startNode = startNode;
        initComponents();
    }

    public void initComponents() {
        setSize( FRAME_SIZE );
        setLocationRelativeTo( Settings.anchorFrame );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );

        JPanel contentJPanel = new javax.swing.JPanel();
        contentJPanel.setLayout( new MigLayout() );

        contentJPanel.add( new JLabel( Settings.jpoResources.getString( "genericTargetDirText" ) ) );
        contentJPanel.add( targetDirChooser, "wrap" );

        contentJPanel.add( new JLabel( Settings.jpoResources.getString( "xmlFileNameLabel" ) ) );

        xmlFileNameJTextField.setPreferredSize( new Dimension( 240, 20 ) );
        xmlFileNameJTextField.setMinimumSize( new Dimension( 240, 20 ) );
        xmlFileNameJTextField.setMaximumSize( new Dimension( 400, 20 ) );
        xmlFileNameJTextField.setText( "PictureList.xml" );
        xmlFileNameJTextField.setInputVerifier( new InputVerifier() {

            @Override
            public boolean shouldYieldFocus( JComponent input ) {
                String validationFile = ( (JTextComponent) input ).getText();
                if ( !validationFile.toUpperCase().endsWith( ".XML" ) ) {
                    ( (JTextComponent) input ).setText( validationFile + ".xml" );
                }
                return true;
            }

            @Override
            public boolean verify( JComponent input ) {
                return true;
            }
        } );
        contentJPanel.add( xmlFileNameJTextField, "wrap" );

        exportPicsJCheckBox.setSelected( true );
        contentJPanel.add( exportPicsJCheckBox, "spanx 2, wrap" );

        JPanel buttonJPanel = new JPanel();

        final JButton exportJButton = new JButton( Settings.jpoResources.getString( "genericExportButtonText" ) );
        exportJButton.setPreferredSize( Settings.defaultButtonDimension );
        exportJButton.setMinimumSize( Settings.defaultButtonDimension );
        exportJButton.setMaximumSize( Settings.defaultButtonDimension );
        exportJButton.setDefaultCapable( true );
        this.getRootPane().setDefaultButton( exportJButton );
        exportJButton.addActionListener( ( ActionEvent e ) -> {
            exportToDirectory();
            getRid();
        } );
        buttonJPanel.add( exportJButton );

        final JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelJButton.addActionListener( ( ActionEvent e ) -> getRid());
        buttonJPanel.add( cancelJButton );

        contentJPanel.add( buttonJPanel, "spanx 2, wrap" );

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
        Thread t = new Thread( ()
                -> new JpoWriter( targetFile, startNode, exportPicsJCheckBox.isSelected() )
        );
        t.start();

        Settings.memorizeCopyLocation( targetFile.getParent() );
        JpoEventBus.getInstance().post( new CopyLocationsChangedEvent() );
        Settings.pushRecentCollection( targetFile.toString() );
        JpoEventBus.getInstance().post( new RecentCollectionsChangedEvent() );
        JOptionPane.showMessageDialog( Settings.anchorFrame,
                Settings.jpoResources.getString( "collectionSaveBody" ) + targetFile.toString(),
                Settings.jpoResources.getString( "collectionSaveTitle" ),
                JOptionPane.INFORMATION_MESSAGE );

    }

}
