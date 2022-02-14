package org.jpo.gui.swing;

import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.NodeStatisticsBean;
import org.jpo.datamodel.Settings;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 NodeStatisticsController.java: a controller that makes the NodeStatisticsPanel show interesting stats

 Copyright (C) 2002 - 2022  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * A Panel that holds labels and a method to fill in the numbers
 *
 * @author Richard Eigenmann
 */
public class NodeStatisticsPanel extends JPanel {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(NodeStatisticsPanel.class.getName());

    /**
     * Number of Nodes in the tree including the current node
     */
    private final JLabel collectionItemsLabel = new JLabel();
    /**
     * Number of Groups in the tree excluding the current node
     */
    private final JLabel collectionGroupsLabel = new JLabel();
    /**
     * Number of Pictures in the tree
     */
    private final JLabel collectionPicturesLabel = new JLabel();
    /**
     * Number of Jobs on the Thumbnail queue
     */
    private final JLabel collectionSizeJLabel = new JLabel();
    /**
     * Free Application Memory
     */
    private final JLabel freeMemoryJLabel = new JLabel();
    /**
     * Indicates how many jobs are on the thumbnail creation queue.
     */
    private final JLabel queueCountJLabel = new JLabel();
    /**
     * Indicates how many pictures are selected
     */
    private final JLabel selectedCountJLabel = new JLabel();
    /**
     * The number of running ThumbnailCreationFactory objects running
     */
    private final JLabel thumbnailCreationFactoriesJLabel = new JLabel();

    /**
     * Constructor to create a panel that displays statistics about the nodes
     * underneath the supplied node.
     */
    public NodeStatisticsPanel() {
        initComponents();
    }

    /**
     * Initialise the widgets and add them to the panel
     */
    private void initComponents() {
        setLayout( new MigLayout() );
        add( collectionItemsLabel, "wrap" );
        add( collectionGroupsLabel, "wrap" );
        add( collectionPicturesLabel, "wrap" );
        add( collectionSizeJLabel, "wrap" );
        add( freeMemoryJLabel, "wrap" );
        add( queueCountJLabel, "wrap" );
        add(selectedCountJLabel, "wrap");
        add(thumbnailCreationFactoriesJLabel, "wrap");
    }

    /**
     * Updates the statistics
     *
     * @param nodeStatisticsBean bean
     */
    public void updateStats(final NodeStatisticsBean nodeStatisticsBean) {
        collectionItemsLabel.setText(nodeStatisticsBean.getNumberOfNodes());
        collectionGroupsLabel.setText(nodeStatisticsBean.getNumberOfGroups());
        collectionPicturesLabel.setText(nodeStatisticsBean.getNumberOfPictures());
        collectionSizeJLabel.setText(nodeStatisticsBean.getSizeOfPictures());
        LOGGER.log(Level.INFO, "Debug mode: {0}", Settings.isDebugMode());
        if (Settings.isDebugMode()) {
            freeMemoryJLabel.setText(nodeStatisticsBean.getFreeMemory());
            freeMemoryJLabel.setVisible(true);
            queueCountJLabel.setText(nodeStatisticsBean.getQueueCount());
            queueCountJLabel.setVisible(true);
            selectedCountJLabel.setText(nodeStatisticsBean.getSelectedCount());
            selectedCountJLabel.setVisible(true);
            thumbnailCreationFactoriesJLabel.setText(nodeStatisticsBean.getThumbnailCreationFactoryCount());
            thumbnailCreationFactoriesJLabel.setVisible(true);
        } else {
            freeMemoryJLabel.setVisible(false);
            queueCountJLabel.setVisible( false );
            selectedCountJLabel.setVisible(false);
            thumbnailCreationFactoriesJLabel.setVisible(false);
        }

    }

}
