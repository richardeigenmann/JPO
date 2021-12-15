package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.eventbus.GroupSelectionEvent;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.ShowGroupRequest;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;

/*
 InfoPanelController.java:  The Controller for the Info Panel

 Copyright (C) 2009-2021  Richard Eigenmann.
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
 * Manages the Info Panel
 */
public class InfoPanelController {

    /**
     * Constructor for the Info Panel controller
     */
    public InfoPanelController() {
        JpoEventBus.getInstance().register( this );
    }


    private final NodeStatisticsController nodeStatisticsController = new NodeStatisticsController();
    /**
     * Delay between polls.
     */
    private static final int DELAY_MS = 5000; //milliseconds

    /**
     * A timer to fire off the refresh of the info display.
     */
    private final Timer statsUpdateTimer = new Timer(DELAY_MS, (ActionEvent ae) -> nodeStatisticsController.updateStats());

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
     *
     * @param event The Group Selection Event
     */
    @Subscribe
    public void handleGroupSelectionEvent(final GroupSelectionEvent event) {
        showInfo(event.node());
    }

    /**
     * Handles the ShowGroupRequest by updating the display.
     *
     * @param event The Show Group Event
     */
    @Subscribe
    public void handleShowGroupRequest(final ShowGroupRequest event) {
        showInfo(event.node());
    }

    /**
     * Invoked to tell that we should display something
     *
     * @param defaultMutableTreeNode The Group or Picture node to be displayed.
     */
    public void showInfo(final DefaultMutableTreeNode defaultMutableTreeNode) {
        SwingUtilities.invokeLater(
                () -> {
                    if (defaultMutableTreeNode.getUserObject() instanceof PictureInfo) {
                        statsUpdateTimer.stop();
                    } else {
                        nodeStatisticsController.updateStats(defaultMutableTreeNode);
                        statsUpdateTimer.start();
                    }
                }
        );
    }
}
