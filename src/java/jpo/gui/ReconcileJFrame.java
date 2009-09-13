package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import jpo.dataModel.PictureInfo;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;
import javax.swing.*;

/*
ReconcileJFrame.java:  
a class that creates a GUI, asks for a directory and then tells you if the files are in your collection.

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
 *  This class creates a JFrame where the user can specify a directory against which
 *  he wants to reconcile the current collection. When the reconciliation is started
 *  the results are displayed in a JTextArea.<p>
 *
 *  The user can choose whether only missing files are to be shown or whether the
 *  reconciliation should also show the matched files.
 *
 */
public class ReconcileJFrame
        extends JFrame
        implements ActionListener,
        Runnable {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( ReconcileJFrame.class.getName() );

    /** 
     *   holds the directory to use.
     */
    private DirectoryChooser directoryChooser =
            new DirectoryChooser( Settings.jpoResources.getString( "directoryCheckerChooserTitle" ),
            DirectoryChooser.DIR_MUST_EXIST );

    /**
     *  button to start the export
     **/
    private JButton okJButton = new JButton( Settings.jpoResources.getString( "ReconcileOkButtonLabel" ) );

    /**
     *  button to cancel the dialog
     **/
    private JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );

    /**
     *  tickbox that indicates whether subdirectories are to be reconciled too
     **/
    private JCheckBox recurseSubdirectoriesJCheckBox = new JCheckBox( Settings.jpoResources.getString( "ReconcileSubdirectories" ) );

    /**
     *  tickbox that indicates whether subdirectories are to be reconciled too
     **/
    private JCheckBox listPositivesJCheckBox = new JCheckBox( Settings.jpoResources.getString( "ReconcileListPositives" ) );

    /**
     *  the log window with the results of the reconciliation
     */
    private JTextArea logJTextArea = new JTextArea( 15, 60 );

    private JScrollPane logJScrollPane = new JScrollPane( logJTextArea );

    /**
     *  flag to tell the thread to end in a controlled manner. If the thread is not running it is true
     */
    private boolean stopThread = true;

    /**
     *  a reference to the root object which shall be reconciled.
     */
    private SortableDefaultMutableTreeNode rootNode;


    /** 
     *   Creates a JFrame with the GUI elements and buttons that can
     *   start and stop the reconciliation. The reconciliation itself
     *   runs in it's own Thread.
     *
     *   @param	rootNode	The node which should be used as
     *				a starting point for the reconciliation.
     *				Will probably always be the root node of
     *				the tree.
     */
    public ReconcileJFrame( SortableDefaultMutableTreeNode rootNode ) {
        this.rootNode = rootNode;

        setSize( 460, 300 );
        setLocationRelativeTo( Settings.anchorFrame );
        setTitle( Settings.jpoResources.getString( "ReconcileJFrameTitle" ) );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );


        JPanel controlJPanel = new JPanel();
        controlJPanel.setLayout( new GridBagLayout() );

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;

        JLabel reconcileBlaBlaJLabel = new JLabel( Settings.jpoResources.getString( "ReconcileBlaBlaLabel" ) );
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets( 4, 4, 4, 4 );
        controlJPanel.add( reconcileBlaBlaJLabel, constraints );



        JLabel directoryJLabel = new JLabel( Settings.jpoResources.getString( "directoryJLabelLabel" ) );
        constraints.gridy++;
        constraints.gridwidth = 2;
        constraints.insets = new Insets( 4, 4, 4, 4 );
        controlJPanel.add( directoryJLabel, constraints );


        constraints.gridx = 3;
        constraints.fill = GridBagConstraints.NONE;
        okJButton.setPreferredSize( Settings.defaultButtonDimension );
        okJButton.setMinimumSize( Settings.defaultButtonDimension );
        okJButton.setMaximumSize( Settings.defaultButtonDimension );
        okJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        okJButton.setDefaultCapable( true );
        getRootPane().setDefaultButton( okJButton );
        okJButton.addActionListener( this );
        controlJPanel.add( okJButton, constraints );


        // create the JTextField that holds the reference to the targetDirJTextField
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.insets = new Insets( 4, 4, 4, 4 );
        controlJPanel.add( directoryChooser, constraints );


        constraints.gridx = 3;
        cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        cancelJButton.addActionListener( this );
        controlJPanel.add( cancelJButton, constraints );

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 2;
        controlJPanel.add( recurseSubdirectoriesJCheckBox, constraints );
        recurseSubdirectoriesJCheckBox.setSelected( true );

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 2;
        controlJPanel.add( listPositivesJCheckBox, constraints );
        listPositivesJCheckBox.setSelected( false );


        logJTextArea.setLineWrap( false );
        logJScrollPane.setMinimumSize( new Dimension( 400, 250 ) );
        logJScrollPane.setMaximumSize( new Dimension( 2000, 1000 ) );


        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 4;
        constraints.fill = GridBagConstraints.BOTH;
        controlJPanel.add( logJScrollPane, constraints );

        getContentPane().add( controlJPanel );

        pack();
        setVisible( true );
    }


    /**
     *  method that closes te frame and gets rid of it
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }


    /** 
     *  method that analyses the user initiated action and performs what the user requested
     *
     * @param e
     */
    public void actionPerformed( ActionEvent e ) {
        logger.fine("actionperformed!!");
        if ( e.getSource() == cancelJButton ) {
            logger.info( "cancel buttoon" );
            if ( stopThread ) {
                getRid(); // thread hasn't started
            } else {
                stopThread = true; // thread will end when it wants to
            }
        } else if ( e.getSource() == okJButton ) {
            new Thread( this ).start();
        }
    }


    /**
     *   the run method that gets inkoked in it's own thread and can then do the directory checking.
     */
    public void run() {
        stopThread = false;
        File scanDir = directoryChooser.getDirectory();
        if ( validateDir( scanDir ) ) {
            logJTextArea.setText( null );
            reconcileDir( scanDir );
            if ( stopThread ) {
                logJTextArea.append( Settings.jpoResources.getString( "ReconcileInterrupted" ) );
            } else {
                logJTextArea.append( Settings.jpoResources.getString( "ReconcileDone" ) );
            }
        }
    }


    /**
     *  helper method that returns true if the directory is a readable valid directory.
     *  If it is not it returns false. It pops up JOptionPanes to explain what is wrong
     */
    private boolean validateDir( File scanDir ) {
        logger.info( "validateDir invoked with " + scanDir.getPath() );
        // is it a directory?
        if ( !scanDir.isDirectory() ) {
            scanDir = scanDir.getParentFile();
            logger.info( "File is not a directory. Using it's parent: " + scanDir.getPath() );
        }
        if ( scanDir == null ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "ReconcileNullFileError" ),
                    "Error",
                    JOptionPane.ERROR_MESSAGE );
            return false;
        }


        // is the File object readable?
        if ( !scanDir.canRead() ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "ReconcileCantReadError" ),
                    "Error",
                    JOptionPane.ERROR_MESSAGE );
            return false;
        }
        // we can read the file

        return true;
    }


    /**
     *  reconciles the directory
     */
    private void reconcileDir( File scanDir ) {
        logJTextArea.append( Settings.jpoResources.getString( "ReconcileStart" ) + scanDir.getPath() + "\n" );
        File[] fileArray = scanDir.listFiles();
        if ( fileArray == null ) {
            logJTextArea.append( Settings.jpoResources.getString( "ReconcileNoFiles" ) );
            return;
        }

        for ( int i = 0; ( ( i < fileArray.length ) && ( !stopThread ) ); i++ ) {
            if ( fileArray[i].isDirectory() ) {
                logger.fine("Branching into subdir");
                reconcileDir( fileArray[i] );
            } else {
                logger.fine ("should reconciling File: " + fileArray[i].getPath());
                SortableDefaultMutableTreeNode node;
                Object nodeObject;
                Enumeration e = rootNode.preorderEnumeration();
                boolean found = false;
                while ( e.hasMoreElements() && ( !found ) ) {
                    node = (SortableDefaultMutableTreeNode) e.nextElement();
                    nodeObject = node.getUserObject();
                    if ( nodeObject instanceof PictureInfo ) {
//						logJTextArea.append("Comparing: " + fileArray[i].toURI().toString() + " against: " + ((PictureInfo) nodeObject ).getHighresURIOrNull().toString() + "\n");
                        // if ( ((PictureInfo) nodeObject ).getHighresURL().getDirectory().compareTo( fileArray[i].getPath() ) == 0)  {
                        if ( ( (PictureInfo) nodeObject ).getHighresURIOrNull().equals( fileArray[i].toURI() ) ) {
                            if ( listPositivesJCheckBox.isSelected() ) {
                                logJTextArea.append( fileArray[i].getPath() + Settings.jpoResources.getString( "ReconcileFound" ) + ( (PictureInfo) nodeObject ).getDescription() + "\n" );
                            }
                            found = true;
                        }
                    }
                }
                if ( !found ) {
                    logJTextArea.append( Settings.jpoResources.getString( "ReconcileNotFound" ) + fileArray[i].toString() + "\n" );
                }

            }
        }

    }
}
