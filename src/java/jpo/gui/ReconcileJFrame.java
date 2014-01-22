package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.*;
import jpo.dataModel.GroupInfo;

/*
ReconcileJFrame.java:  
a class that creates a GUI, asks for a directory and then tells you if the files are in your collection.

Copyright (C) 2002 - 2010  Richard Eigenmann.
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
 *  TODO: Make this a Swing Worker if it servers a purpose
 *
 */
public class ReconcileJFrame
        extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ReconcileJFrame.class.getName() );

    /**
     *  tickbox that indicates whether subdirectories are to be reconciled too
     **/
    private final JCheckBox recurseSubdirectoriesJCheckBox = new JCheckBox( Settings.jpoResources.getString( "ReconcileSubdirectories" ) );

    /**
     *  tickbox that indicates whether subdirectories are to be reconciled too
     **/
    private final JCheckBox listPositivesJCheckBox = new JCheckBox( Settings.jpoResources.getString( "ReconcileListPositives" ) );

    /**
     *  the log window with the results of the reconciliation
     */
    private final JTextArea logJTextArea = new JTextArea( 15, 60 );

    private final JScrollPane logJScrollPane = new JScrollPane( logJTextArea );

    /**
     *  flag to tell the thread to end in a controlled manner. If the thread is not running it is true
     */
    private boolean stopThread = true;

    /**
     *  a reference to the root object which shall be reconciled.
     */
    private final SortableDefaultMutableTreeNode rootNode;


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


        final DirectoryChooser directoryChooser =
                new DirectoryChooser( Settings.jpoResources.getString( "directoryCheckerChooserTitle" ),
                DirectoryChooser.DIR_MUST_EXIST );

        constraints.gridx = 3;
        constraints.fill = GridBagConstraints.NONE;
        JButton okJButton = new JButton( Settings.jpoResources.getString( "ReconcileOkButtonLabel" ) );
        okJButton.setPreferredSize( Settings.defaultButtonDimension );
        okJButton.setMinimumSize( Settings.defaultButtonDimension );
        okJButton.setMaximumSize( Settings.defaultButtonDimension );
        okJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        okJButton.setDefaultCapable( true );
        getRootPane().setDefaultButton( okJButton );
        okJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                scanDir = directoryChooser.getDirectory();
                runReconciliationEDT();
            }
        } );
        controlJPanel.add( okJButton, constraints );


        // create the JTextField that holds the reference to the targetDirJTextField
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.insets = new Insets( 4, 4, 4, 4 );
        controlJPanel.add( directoryChooser, constraints );


        constraints.gridx = 3;
        JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
        cancelJButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                if ( stopThread ) {
                    getRid(); // thread hasn't started
                } else {
                    stopThread = true; // thread will end when it wants to
                }
            }
        } );
        cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
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
        setLocationRelativeTo( Settings.anchorFrame );
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
     * This method does some validation and then fires the Reconciler
     */
    private void runReconciliationEDT() {
        stopThread = false;
        if ( validateDir( scanDir ) ) {
            logJTextArea.setText( null );
            ( new Reconciler() ).execute();
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
        if ( scanDir == null ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "ReconcileNullFileError" ),
                    "Error",
                    JOptionPane.ERROR_MESSAGE );
            return false;
        }

        //logger.info( "validateDir invoked with " + scanDir.getPath() );
        // is it a directory?
        if ( !scanDir.isDirectory() ) {
            scanDir = scanDir.getParentFile();
            if ( scanDir == null ) {
                JOptionPane.showMessageDialog(
                        Settings.anchorFrame,
                        Settings.jpoResources.getString( "ReconcileNullFileError" ),
                        "Error",
                        JOptionPane.ERROR_MESSAGE );
                return false;
            }
            LOGGER.info( "File is not a directory. Using it's parent: " + scanDir.getPath() );
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

    private File scanDir;

    /**
     * Do the reconciliation in a SwingWorker
     */
    class Reconciler
            extends SwingWorker<String, String> {

        private HashSet<URI> collectionUris = new HashSet<URI>();


        @Override
        protected String doInBackground() throws Exception {
            //Build HashSet of all of the URIs know to the collection
            SortableDefaultMutableTreeNode node;
            Object nodeObject;
            Enumeration e = rootNode.preorderEnumeration();
            while ( e.hasMoreElements() ) {
                node = (SortableDefaultMutableTreeNode) e.nextElement();
                nodeObject = node.getUserObject();
                if ( nodeObject instanceof PictureInfo ) {
                    PictureInfo pi = (PictureInfo) nodeObject;
                    collectionUris.add( pi.getHighresURIOrNull() );
                    collectionUris.add( pi.getLowresURIOrNull() );
                } else if ( nodeObject instanceof GroupInfo ) {
                    GroupInfo gi = (GroupInfo) nodeObject;
                    collectionUris.add( gi.getLowresURIOrNull() );
                }
            }

            reconcileDir( scanDir );
            stopThread = true;
            return "Done.";
        }


        public void reconcileDir( File scanDir ) {
            if ( listPositivesJCheckBox.isSelected() ) {
                publish( Settings.jpoResources.getString( "ReconcileStart" ) + scanDir.getPath() + "\n" );
            }
            File[] fileArray = scanDir.listFiles();
            if ( fileArray == null ) {
                publish( Settings.jpoResources.getString( "ReconcileNoFiles" ) );
                return;
            }


            for ( int i = 0; ( ( i < fileArray.length ) && ( !stopThread ) ); i++ ) {
                if ( fileArray[i].isDirectory() ) {
                    reconcileDir( fileArray[i] );
                } else {
                    URI testFile = fileArray[i].toURI();
                    if ( collectionUris.contains( testFile ) ) {
                        if ( listPositivesJCheckBox.isSelected() ) {
                            publish( fileArray[i].getPath() + Settings.jpoResources.getString( "ReconcileFound" ) + "\n" );
                        }
                    } else {
                        publish( Settings.jpoResources.getString( "ReconcileNotFound" ) + fileArray[i].toString() + "\n" );
                    }
                }
            }
        }


        @Override
        protected void process( List<String> chunks ) {
            for ( String s : chunks ) {
                logJTextArea.append( s );
            }
        }
    }
}
