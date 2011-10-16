package jpo.gui;

import jpo.dataModel.Tools;
import jpo.dataModel.Settings;
import java.awt.*;
import java.util.logging.Level;
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
public class NodeStatisticsPanel
        extends JPanel {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( NodeStatisticsPanel.class.getName() );
    
    /**
     *   Number of Nodes in the tree including the current node
     */
    private final JLabel collectionItemsLabel = new JLabel();
    /**
     *  Number of Groups in the tree excluding the current node
     */
    private final JLabel collectionGroupsLabel = new JLabel();
    /**
     *  Number of Pictures in the tree
     */
    private final JLabel collectionPicturesLabel = new JLabel();
    /**
     *  Number of Jobs on the Thumbnail queue
     */
    private final JLabel collectionSizeJLabel = new JLabel();
    /**
     *  Free Application Memory
     */
    private final JLabel freeMemoryJLabel = new JLabel();
    /**
     *  Indicates how many jobs are on the thumbnail creation queue.
     */
    private final JLabel queueCountJLabel = new JLabel();
    /**
     *  Indicates how many pictures are selected
     */
    private final JLabel selectedCountJLabel = new JLabel();

    /**
     *   Constructor to create a JFrame that displays statistics about the nodes underneath
     *   the supplied node.
     */
    public NodeStatisticsPanel() {
        initComponents();
    }

    private void initComponents() {
        Tools.checkEDT();
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
    
    
    /**
     * The NodeStatistics object that gives the results
     */
    private final NodeStatistics ns = new NodeStatistics( null );

    /**
     *  This method will update the statistics based on the supplied input node.
     *  @param  statisticsNode   The node that is being analysed.
     */
    public void updateStats( DefaultMutableTreeNode statisticsNode ) {
        LOGGER.log( Level.FINE, "update stats for node: {0}", statisticsNode.toString());
        Tools.checkEDT();
        if ( Settings.pictureCollection.fileLoading ) {
            LOGGER.info( "Still busy loading the file. Aborting" );
            return;
        }

        if ( ns.getNode() != statisticsNode ) {
            ns.setNode( statisticsNode );
        }
        updateStats();

    }

    /**
     *  This method will update the statistics based on the current Node Statistics object.
     */
    public void updateStats() {
        class updateStatsWorker extends SwingWorker<Object, Object> {

            String numberOfNodes;
            String numberOfGroups;
            String numberOfPictures;
            String sizeOfPictures;
            boolean debugMode = Settings.writeLog;
            String freeMemory;
            String queueCount;
            String selectedCount;

            @Override
            public String doInBackground() {
                LOGGER.fine( "Counting nodes on Background Thread" );
                Tools.warnOnEDT();
                numberOfNodes = ns.getNumberOfNodesString();
                numberOfGroups = ns.getNumberOfGroupsString();
                numberOfPictures = ns.getNumberOfPicturesString();
                sizeOfPictures = ns.getSizeOfPicturesString();

                if ( debugMode ) {
                    freeMemory = Tools.freeMemory();
                    queueCount = Settings.jpoResources.getString( "queCountJLabel" ) + ThumbnailCreationQueue.size();
                    selectedCount = String.format( "Selected: %d", Settings.pictureCollection.getSelectedNodes().length ); //TODO: put this into translations
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    Tools.checkEDT();
                    collectionItemsLabel.setText( numberOfNodes );
                    collectionGroupsLabel.setText( numberOfGroups );
                    collectionPicturesLabel.setText( numberOfPictures );
                    collectionSizeJLabel.setText( sizeOfPictures );
                    if ( debugMode ) {
                        freeMemoryJLabel.setText( freeMemory );
                        freeMemoryJLabel.setVisible( true );
                        queueCountJLabel.setText( queueCount );
                        queueCountJLabel.setVisible( true );
                        selectedCountJLabel.setText( selectedCount );
                        selectedCountJLabel.setVisible( true );
                    } else {
                        freeMemoryJLabel.setVisible( false );
                        queueCountJLabel.setVisible( false );
                        selectedCountJLabel.setVisible( false );
                    }
                } catch ( Exception ignore ) {
                }
            }
        }

        ( new updateStatsWorker() ).execute();
    }
}
