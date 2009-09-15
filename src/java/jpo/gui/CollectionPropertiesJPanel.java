package jpo.gui;

import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import jpo.*;
import java.awt.*;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import jpo.dataModel.NodeStatistics;

/*
CollectionPropertiesJPanel.java: a panel that shows some counts about the collection

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
 *  a panel that shows some counts about the collection
 */
public class CollectionPropertiesJPanel
        extends JPanel {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( CollectionPropertiesJPanel.class.getName() );

    /**
     *   Number of Nodes in the tree including the current node
     */
    private JLabel collectionItemsLabel = new JLabel();

    /**
     *  Number of Groups in the tree excluding the current node
     */
    private JLabel collectionGroupsLabel = new JLabel();

    /**
     *  Number of Pictures in the tree
     */
    private JLabel collectionPicturesLabel = new JLabel();

    /**
     *  Number of Jobs on the Thumbnail queue
     */
    private JLabel collectionSizeJLabel = new JLabel();

    /**
     *  Free Application Memory
     */
    private JLabel freeMemoryJLabel = new JLabel();

    /**
     *  Indicates how many jobs are on the thumbnail creation queue.
     */
    private JLabel queueCountJLabel = new JLabel();

    /**
     *  Indicates how many pictures are selected
     */
    private JLabel selectedCountJLabel = new JLabel();


    /**
     *   Constructor to create a JFrame that displays statistics about the nodes underneath
     *   the supplied node.
     */
    public CollectionPropertiesJPanel() {
        Tools.checkEDT();
        initComponents();
    }


    private void initComponents() {
        setMinimumSize( new Dimension( 100, 100 ) );
        setLayout( new GridBagLayout() );

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets( 0, 4, 0, 4 );
        add( collectionItemsLabel, constraints );

        constraints.gridy++;
        add( collectionGroupsLabel, constraints );

        constraints.gridy++;
        add( collectionPicturesLabel, constraints );

        constraints.gridy++;
        add( collectionSizeJLabel, constraints );

        constraints.gridy++;
        add( freeMemoryJLabel, constraints );

        constraints.gridy++;
        add( queueCountJLabel, constraints );

        constraints.gridy++;
        add( selectedCountJLabel, constraints );
    }

    private NodeStatistics ns;


    /**
     *  This method will update the statistics based on the supplied input node.
     *  @param  statisticsNode   The node that is being analysed.
     */
    public void updateStats( DefaultMutableTreeNode statisticsNode ) {
        logger.fine( "update stats for node: " + statisticsNode.toString() );
        Tools.checkEDT();
        if ( Settings.pictureCollection.fileLoading ) {
            logger.info( "Still busy loading the file. Aborting" );
            return;
        }

        // initialise or update the NodeStatistics object
        if ( ns == null ) {
            ns = new NodeStatistics( statisticsNode );
        } else {
            if ( ns.getNode() != statisticsNode ) {
                ns.setNode( statisticsNode );
            }
        }


        collectionItemsLabel.setText( ns.getNumberOfNodesString() );
        collectionGroupsLabel.setText( ns.getNumberOfGroupsString() );
        collectionPicturesLabel.setText( ns.getNumberOfPicturesString() );
        collectionSizeJLabel.setText( ns.getSizeOfPicturesString() );
        logger.info(String.format("Updating stats: ",Settings.pictureCollection.getSelectedNodesAsVector().size()));
        selectedCountJLabel.setText( String.format( "Selected: %d", Settings.pictureCollection.getSelectedNodesAsVector().size() ) ); //TODO: put this into translations

        updateQueueCount();

    }


    /**
     *  This method updates the label showing the entries on the queue. This was split so that costly operations could be slowed down but it doesn't
     *  Seem to be a problem. The memory usage and the Thumbnails on queue are only shown when the log file is being written as normal users will
     *  hardly be interested in this detail.
     */
    public void updateQueueCount() {
        Tools.checkEDT();
        if ( Settings.writeLog ) {
            freeMemoryJLabel.setVisible( true );
            freeMemoryJLabel.setText( Tools.freeMemory() );
            queueCountJLabel.setVisible( true );
            queueCountJLabel.setText( Settings.jpoResources.getString( "queCountJLabel" ) + ThumbnailCreationQueue.size() );
        } else {
            freeMemoryJLabel.setVisible( false );
            queueCountJLabel.setVisible( false );
        }
    }
}
