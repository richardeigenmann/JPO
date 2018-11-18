package jpo.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import net.miginfocom.swing.MigLayout;

/*
 ReconcileJFrame.java:  
 a class that creates a GUI, asks for a directory and then tells you if the files are in your collection.

 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
 * This class creates a GUI where the user can specify a directory against which
 * he wants to reconcile the pictures in the current collection. When the
 * reconciliation is started the results are displayed in a JTextArea.<p>
 *
 * The user can choose whether only missing files are to be shown or whether the
 * reconciliation should also show the matched files.
 *
 */
public class ReconcileJFrame extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ReconcileJFrame.class.getName() );

    /**
     * tickbox that indicates whether subdirectories are to be reconciled too
     *
     */
    private final JCheckBox recurseSubdirectoriesJCheckBox = new JCheckBox( Settings.jpoResources.getString( "ReconcileSubdirectories" ) );

    /**
     * tickbox that indicates whether subdirectories are to be reconciled too
     *
     */
    private final JCheckBox listPositivesJCheckBox = new JCheckBox( Settings.jpoResources.getString( "ReconcileListPositives" ) );

    /**
     * the log window with the results of the reconciliation
     */
    private final JTextArea logJTextArea = new JTextArea( 15, 60 );

    /**
     * a reference to the node which shall be reconciled.
     */
    private final SortableDefaultMutableTreeNode startNode;

    /**
     * Creates a JFrame with the GUI elements and buttons that can start and
     * stop the reconciliation. The reconciliation itself runs in it's own
     * SwingWorker.
     *
     * @param	startNode	The node which should be used as a starting point for
     * the reconciliation.
     */
    public ReconcileJFrame( SortableDefaultMutableTreeNode startNode ) {
        this.startNode = startNode;

        initComponents();
    }

    /**
     * Creates the GUI
     */
    private void initComponents() {
        setTitle( Settings.jpoResources.getString( "ReconcileJFrameTitle" ) );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );

        JPanel controlJPanel = new JPanel();
        controlJPanel.setLayout( new MigLayout() );

        controlJPanel.add( new JLabel( Settings.jpoResources.getString( "ReconcileBlaBlaLabel" ) ), "spanx 3, wrap" );

        controlJPanel.add( new JLabel( Settings.jpoResources.getString( "directoryJLabelLabel" ) ) );

        final DirectoryChooser directoryChooser
                = new DirectoryChooser( Settings.jpoResources.getString( "directoryCheckerChooserTitle" ),
                        DirectoryChooser.DIR_MUST_EXIST );

        controlJPanel.add( directoryChooser );

        JButton okJButton = new JButton( Settings.jpoResources.getString( "ReconcileOkButtonLabel" ) );
        okJButton.setPreferredSize( Settings.defaultButtonDimension );
        okJButton.setMinimumSize( Settings.defaultButtonDimension );
        okJButton.setMaximumSize( Settings.defaultButtonDimension );
        okJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        okJButton.setDefaultCapable( true );
        getRootPane().setDefaultButton( okJButton );
        okJButton.addActionListener(( ActionEvent e ) -> {
            File scanDir = directoryChooser.getDirectory();
            runReconciliation( scanDir );
        });
        controlJPanel.add( okJButton, "wrap" );

        controlJPanel.add( recurseSubdirectoriesJCheckBox, "spanx 2" );

        JButton cancelJButton = new JButton( Settings.jpoResources.getString( "closeJButton" ) );
        cancelJButton.addActionListener(( ActionEvent e ) -> {
            if ( ( reconciler == null ) || ( reconciler.isDone() ) ) {
                getRid();
            } else {
                reconciler.cancel( false );
            }
        });
        cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        controlJPanel.add( cancelJButton, "wrap" );

        recurseSubdirectoriesJCheckBox.setSelected( true );

        controlJPanel.add( listPositivesJCheckBox, "spanx 3, wrap" );
        listPositivesJCheckBox.setSelected( false );

        logJTextArea.setLineWrap( false );
        JScrollPane logJScrollPane = new JScrollPane( logJTextArea );
        logJScrollPane.setMinimumSize( new Dimension( 400, 250 ) );
        logJScrollPane.setMaximumSize( new Dimension( 2000, 1000 ) );

        controlJPanel.add( logJScrollPane, "spanx 3, wrap" );

        getContentPane().add( controlJPanel );

        pack();
        setLocationRelativeTo( Settings.anchorFrame );
        setVisible( true );
    }

    /**
     * method that closes the frame and gets rid of it
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }

    /**
     * Reference to the reconciler
     */
    private Reconciler reconciler = null;

    /**
     * This method does some validation and then fires the Reconciler
     * @param reconcileDir the directory to reconcile
     */
    private void runReconciliation( File reconcileDir ) {
        if ( validateDir( reconcileDir ) ) {
            logJTextArea.setText( null );
            reconciler = new Reconciler( startNode, reconcileDir, recurseSubdirectoriesJCheckBox.isSelected(), logJTextArea, listPositivesJCheckBox.isSelected() );
            reconciler.execute();
        }
    }

    /**
     * Validates that the directory is a readable valid directory. If it is not
     * it returns false. It pops up JOptionPanes to explain what is wrong.
     *
     * @param reconcileDir The directory to validate
     * @return true if good, false if not
     */
    private boolean validateDir( File reconcileDir ) {
        if ( reconcileDir == null ) {
            JOptionPane.showMessageDialog(
                    Settings.anchorFrame,
                    Settings.jpoResources.getString( "ReconcileNullFileError" ),
                    "Error",
                    JOptionPane.ERROR_MESSAGE );
            return false;
        }

        if ( !reconcileDir.isDirectory() ) {
            reconcileDir = reconcileDir.getParentFile();
            if ( reconcileDir == null ) {
                JOptionPane.showMessageDialog(
                        Settings.anchorFrame,
                        Settings.jpoResources.getString( "ReconcileNullFileError" ),
                        "Error",
                        JOptionPane.ERROR_MESSAGE );
                return false;
            }
            LOGGER.log( Level.INFO, "File is not a directory. Using it''s parent: {0}", reconcileDir.getPath() );
        }

        if ( !reconcileDir.canRead() ) {
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
     * Do the reconciliation in a SwingWorker
     */
    private class Reconciler
            extends SwingWorker<String, String> {

        /**
         * Start Node for the reconciliation
         */
        private final SortableDefaultMutableTreeNode startNode;

        /**
         * Directory to reconcile against
         */
        private final File reconcileDir;

        /**
         * Flag whether to recursively check subdirectories
         */
        private final boolean recurseSubdirectories;

        /**
         * The text area to append the output to
         */
        private final JTextArea outputTextArea;

        /**
         * Flag to indicate whether to show only breaks or show positives too
         */
        private final boolean listPositives;

        /**
         * Constructor for the Reconciler that checks the directories and
         * reports the missing pictures
         *
         * @param startNode The node from which to start the reconciliation
         * @param reconcileDir The directory to reconcile against
         * @param recurseSubdirectories True if subdirectories should be
         * recursively checked, false if not
         * @param logJTextArea The TextArea to receive the output
         * @param listPositives true to list the positives, false if only breaks
         * should be shown
         */
        Reconciler( SortableDefaultMutableTreeNode startNode, File reconcileDir, boolean recurseSubdirectories, JTextArea logJTextArea, boolean listPositives ) {
            this.startNode = startNode;
            this.reconcileDir = reconcileDir;
            this.recurseSubdirectories = recurseSubdirectories;
            this.outputTextArea = logJTextArea;
            this.listPositives = listPositives;
        }

        private final HashSet<URI> collectionUris = new HashSet<>();

        @Override
        protected String doInBackground() throws Exception {
            //Build HashSet of all of the URIs know to the collection
            SortableDefaultMutableTreeNode node;
            Object nodeObject;
            Enumeration e = startNode.preorderEnumeration();
            while ( e.hasMoreElements() ) {
                node = (SortableDefaultMutableTreeNode) e.nextElement();
                nodeObject = node.getUserObject();
                if ( nodeObject instanceof PictureInfo ) {
                    PictureInfo pictureInfo = (PictureInfo) nodeObject;
                    collectionUris.add( pictureInfo.getImageURIOrNull() );
                }
            }

            reconcileDir( reconcileDir );
            return ( "" );
        }

        /**
         * Reconciles the directory files against the URIs in the
         * collectionUris. Note this method calls itself recursively.
         *
         * @param reconcileDir The directory to reconcile
         */
        private void reconcileDir( File reconcileDir ) {
            if ( listPositivesJCheckBox.isSelected() ) {
                publish( Settings.jpoResources.getString( "ReconcileStart" ) + reconcileDir.getPath() + "\n" );
            }
            File[] fileArray = reconcileDir.listFiles();
            if ( fileArray == null ) {
                publish( Settings.jpoResources.getString( "ReconcileNoFiles" ) );
                return;
            }

            for ( int i = 0; ( ( i < fileArray.length ) && ( !isCancelled() ) ); i++ ) {
                if ( fileArray[i].isDirectory() ) {
                    if ( recurseSubdirectories ) {
                        reconcileDir( fileArray[i] );
                    }
                } else {
                    URI testFile = fileArray[i].toURI();
                    if ( collectionUris.contains( testFile ) ) {
                        if ( listPositivesJCheckBox.isSelected() ) {
                            publish( String.format( Settings.jpoResources.getString( "jpo.gui.ReconcileFound" ), fileArray[i].getPath() ) );
                        }
                    } else {
                        publish( Settings.jpoResources.getString( "ReconcileNotFound" ) + fileArray[i].toString() + "\n" );
                    }
                }
            }
        }

        @Override
        protected void process( List<String> chunks ) {
            chunks.stream().forEach(outputTextArea::append);
        }

        @Override
        protected void done() {
            if ( isCancelled() ) {
                outputTextArea.append( Settings.jpoResources.getString( "ReconcileInterrupted" ) );
            } else {
                outputTextArea.append( Settings.jpoResources.getString( "ReconcileDone" ) );

            }
        }

    }
}
