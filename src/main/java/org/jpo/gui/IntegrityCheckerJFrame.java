package org.jpo.gui;

import net.miginfocom.swing.MigLayout;
import org.jpo.dataModel.PictureInfo;
import org.jpo.dataModel.Settings;
import org.jpo.dataModel.SortableDefaultMutableTreeNode;
import org.jpo.dataModel.Tools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;


/*
 IntegrityCheckerJFrame.java:  creates a frame and checks the integrity of the collection

 Copyright (C) 2002-2019  Richard Eigenmann, Zurich, Switzerland
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
    /**
     * reference to the node that should be checked
     */
    private final SortableDefaultMutableTreeNode startNode;

    /**
     * Constructor for the window that shows the various checks being performed.
     *
     * @param startNode The node from which to start
     */
    IntegrityCheckerJFrame(SortableDefaultMutableTreeNode startNode) {
        this.startNode = startNode;

        // set up widgets
        setTitle( Settings.jpoResources.getString( "IntegrityCheckerTitle" ) );
        JPanel jPanel = new JPanel( new MigLayout( "insets 15" ) );
        jPanel.add( new JLabel( Settings.jpoResources.getString( "integrityCheckerLabel" ) ), "wrap" );
        JButton okJButton = new JButton(Settings.jpoResources.getString("genericOKText"));
        okJButton.setMaximumSize( Settings.defaultButtonDimension );
        okJButton.setMinimumSize( Settings.defaultButtonDimension );
        okJButton.setPreferredSize( Settings.defaultButtonDimension );
        JButton correctChecksumsJButton = new JButton("Correct picture checksums");
        jPanel.add(correctChecksumsJButton, "wrap" );
        final JScrollPane resultScrollPane = new JScrollPane( resultJTextArea );
        jPanel.add( resultScrollPane, "wrap" );
        JButton interruptJButton = new JButton("Interrupt");
        jPanel.add(interruptJButton, "split 2" );
        jPanel.add(okJButton, "wrap, tag ok" );
        getContentPane().add( jPanel );

        // connect behaviour to widgets
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
                getRid();
            }
        } );
        okJButton.addActionListener((ActionEvent e ) -> getRid());
        correctChecksumsJButton.addActionListener((ActionEvent e ) -> correctChecksums());
        interruptJButton.addActionListener((ActionEvent e ) -> interruptWorkers());


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
            for ( final Enumeration e = startNode.breadthFirstEnumeration(); e.hasMoreElements() && ( !isCancelled() ); ) {
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
            chunks.forEach(resultJTextArea::append);
        }
    }

}
