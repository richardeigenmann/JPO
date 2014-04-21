package jpo.gui;

import com.google.common.eventbus.Subscribe;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.PictureInfo;
import javax.swing.Timer;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import jpo.EventBus.GroupSelectionEvent;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.ShowGroupRequest;

/*
 InfoPanelController.java:  The Controller for the Info Panel

 Copyright (C) 2009-2014  Richard Eigenmann.
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
 * Manages the Info Panel
 */
public class InfoPanelController {

    /**
     * Constructor for the Info Panel controller
     */
    public InfoPanelController() {
        JpoEventBus.getInstance().register( this );
    }

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( InfoPanelController.class.getName() );

    private final NodeStatisticsController nodeStatisticsController = new NodeStatisticsController();
    /**
     * A millisecond delay for the polling of the thumbnailController queue and
     * memory status
     */
    private static final int DELAY = 5000; //milliseconds

    /**
     * A timer to fire off the refresh of the Thumbnail Queue display. Is only
     * alive if the InfoPanel is showing the statistics panel.
     */
    private final Timer statUpdateTimer = new Timer( DELAY, new ActionListener() {

        @Override
        public void actionPerformed( ActionEvent ae ) {
            nodeStatisticsController.updateStats();
        }
    } );

    /**
     * Returns the InfoPanel Widget
     *
     * @return The InfoPanel widget as a generic JComponent
     */
    public JComponent getInfoPanel() {
        return nodeStatisticsController.getJComponent();
    }

    /**
     * Handles group selection events by refreshing the display.
     * @param event The Group Selection Event
     */
    @Subscribe
    public void handleGroupSelectionEvent( GroupSelectionEvent event ) {
        showInfo( event.getNode() );
    }

    /**
     * Handles the ShowGroupRequest by updating the display.
     *
     * @param event The Show Group Event
     */
    @Subscribe
    public void handleShowGroupRequest( ShowGroupRequest event ) {
        showInfo( event.getNode() );
    }

    /**
     * Invoked to tell that we should display something
     *
     * @param defaultMutableTreeNode The Group or Picture node to be displayed.
     */
    public void showInfo( DefaultMutableTreeNode defaultMutableTreeNode ) {
        if ( !( defaultMutableTreeNode instanceof SortableDefaultMutableTreeNode ) ) {
            LOGGER.info( "The node is not a SortableDefaultMutableTreeNode. Don't know what to do. Skipping" );
        }
        final SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) defaultMutableTreeNode;
        SwingUtilities.invokeLater( new Runnable() {

            @Override
            public void run() {
                if ( node == null ) {
                    statUpdateTimer.stop();
                    statUpdateTimer.start();  // updates the queue-count
                } else if ( node.getUserObject() instanceof PictureInfo ) {
                    statUpdateTimer.stop();
                } else {
                    nodeStatisticsController.updateStats( node );
                    statUpdateTimer.start();  // updates the queue-count
                }
            }
        } );
    }
}
