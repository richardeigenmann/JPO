package jpo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;
import net.miginfocom.swing.MigLayout;


/*
 IntegrityCheckerJFrame.java:  creates a frame and checks the integrity of the collection

 Copyright (C) 2002-2014  Richard Eigenmann, Zurich, Switzerland
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
 * IntegrityChecker.java: creates a frame and checks the integrity of the
 * collection
 *
 *
 */
public class IntegrityCheckerJFrame
        extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( IntegrityCheckerJFrame.class.getName() );
    private final JTextArea resultJTextArea = new JTextArea( 25, 80 );
    private final JButton okJButton = new JButton( Settings.jpoResources.getString( "genericOKText" ) );
    private final JButton correctChecksumsJButton = new JButton( "Correct picture checksums" );
    private final JButton interruptJButton = new JButton( "Interrupt" );
    /**
     * reference to the node that should be checked
     */
    private final SortableDefaultMutableTreeNode startNode;

    /**
     * Constructor for the window that shows the various checks being performed.
     *
     * @param startNode The node from which to start
     */
    public IntegrityCheckerJFrame( SortableDefaultMutableTreeNode startNode ) {
        this.startNode = startNode;

        // set up widgets
        setTitle( Settings.jpoResources.getString( "IntegrityCheckerTitle" ) );
        JPanel jPanel = new JPanel( new MigLayout( "insets 15" ) );
        jPanel.add( new JLabel( Settings.jpoResources.getString( "integrityCheckerLabel" ) ), "wrap" );
        okJButton.setMaximumSize( Settings.defaultButtonDimension );
        okJButton.setMinimumSize( Settings.defaultButtonDimension );
        okJButton.setPreferredSize( Settings.defaultButtonDimension );
        //jPanel.add( fixThumbnailReferencesJButton, "wrap" );
        jPanel.add( correctChecksumsJButton, "wrap" );
        final JScrollPane resultScrollPane = new JScrollPane( resultJTextArea );
        jPanel.add( resultScrollPane, "wrap" );
        jPanel.add( interruptJButton, "split 2" );
        jPanel.add( okJButton, "wrap, tag ok" );
        getContentPane().add( jPanel );

        // connect behaviour to widgets
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
                getRid();
            }
        } );
        okJButton.addActionListener(( ActionEvent e ) -> {
            getRid();
        });
        correctChecksumsJButton.addActionListener(( ActionEvent e ) -> {
            correctChecksums();
        });
        interruptJButton.addActionListener(( ActionEvent e ) -> {
            interruptWorkers();
        });


        pack();
        setLocationRelativeTo( Settings.anchorFrame );
        setVisible( true );
    }

    /**
     * method that closes the JFrame and gets rid of it
     */
    private void getRid() {
        interruptWorkers();
        setVisible( false );
        dispose();
    }

    private void interruptWorkers() {
        if ( checksumWorker != null ) {
            checksumWorker.cancel( true );
        }
    }

    /**
     * Check the checksums of all the PictureInfo nodes and adds those that are
     * missing
     *
     */
    private void correctChecksums() {
        checksumWorker = new CorrectChecksumSwingWorker();
        checksumWorker.execute();
    }
    
    private CorrectChecksumSwingWorker checksumWorker;

    private class CorrectChecksumSwingWorker extends SwingWorker<Integer, String> {

        @Override
        protected Integer doInBackground() {
            int nodesProcessed = 0;
            int corrections = 0;
            SortableDefaultMutableTreeNode testNode;
            PictureInfo pictureInfo;
            Object nodeObject;
            long oldChecksum;
            long newChecksum;
            for ( Enumeration e = startNode.breadthFirstEnumeration(); e.hasMoreElements() && ( !isCancelled() ); ) {
                nodesProcessed++;
                if ( nodesProcessed % 1000 == 0 ) {
                    publish( String.format( "%d nodes processed%n", nodesProcessed ) );
                }
                testNode = (SortableDefaultMutableTreeNode) e.nextElement();
                nodeObject = testNode.getUserObject();
                if ( ( nodeObject instanceof PictureInfo ) ) {
                    pictureInfo = (PictureInfo) nodeObject;
                    File imageFile = pictureInfo.getImageFile();
                    if ( imageFile != null ) {
                        newChecksum = Tools.calculateChecksum(imageFile );
                        oldChecksum = pictureInfo.getChecksum();
                        if ( oldChecksum != newChecksum ) {
                            corrections++;
                            pictureInfo.setChecksum( newChecksum );
                            String logMessage = String.format( "Corrected checksum of node %s from %d to %d%n", pictureInfo.getDescription(), oldChecksum, newChecksum );
                            publish( logMessage );
                            LOGGER.severe( logMessage );
                        }
                    }
                }
            }
            publish( String.format( "Corrected %d checksums in %d nodes.%n", corrections, nodesProcessed ) );
            return corrections;
        }

        @Override
        protected void process( List<String> chunks ) {
            chunks.stream().forEach(resultJTextArea::append);
        }
    }

    /**
     * This method iterates through all the nodes in the collection and fixes
     * issues where the same thumbnail is being referred to by different nodes
     *
    private void fixThumbnailReferences() {
        thumbnailWorker = new FixThumbnailReferencesSwingWorker();
        thumbnailWorker.execute();
    }
    private FixThumbnailReferencesSwingWorker thumbnailWorker;

    private class FixThumbnailReferencesSwingWorker extends SwingWorker<Integer, String> {

        @Override
        protected Integer doInBackground() {
            HashMap<String, SortableDefaultMutableTreeNode> thumbnailNodeMap = new HashMap<String, SortableDefaultMutableTreeNode>();

            SortableDefaultMutableTreeNode testNode;
            String thumbnailLocation;
            int conflicts = 0;
            for ( Enumeration e = startNode.breadthFirstEnumeration(); e.hasMoreElements() && ( !isCancelled() ); ) {
                testNode = (SortableDefaultMutableTreeNode) e.nextElement();
                thumbnailLocation = testNode.getThumbnailLocation();
                if ( !thumbnailNodeMap.containsKey( thumbnailLocation ) ) {
                    thumbnailNodeMap.put( thumbnailLocation, testNode );
                } else {
                    conflicts++;
                    String newThumbnailFilename = testNode.assignNewThumbnailLocation();
                    SortableDefaultMutableTreeNode conflictNode = thumbnailNodeMap.get( thumbnailLocation );
                    JpoEventBus.getInstance().post( new RefreshThumbnailRequest(conflictNode, ThumbnailQueueRequest.LOWEST_PRIORITY));

                    String logMessage = String.format(
                            "Conflict on the following two nodes:\n%s\n%s\nboth refer to the same thumbnail: %s\nNew thumbnail assigned to second node:%s\n",
                            conflictNode.toString(),
                            testNode.toString(),
                            thumbnailLocation,
                            newThumbnailFilename );
                    publish( logMessage );
                    LOGGER.severe( logMessage );
                }
            }
            publish( String.format( "Number of conflicts: %d\n", conflicts ) );
            return conflicts;
        }

        @Override
        protected void process( List<String> chunks ) {
            for ( String s : chunks ) {
                resultJTextArea.append( s );
            }
        }
    }*/
}
