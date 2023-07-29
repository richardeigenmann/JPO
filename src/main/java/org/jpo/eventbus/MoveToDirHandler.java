package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.gui.ConsolidateGroupWorker;

import javax.swing.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.datamodel.SortableDefaultMutableTreeNode.GENERIC_ERROR;
import static org.jpo.gui.ApplicationStartupHandler.GENERIC_INFO;

/*
 Copyright (C) 2022-2023 Richard Eigenmann.
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
 * Moves the pictures of the supplied nodes to the target directory
 */
@EventHandler
public class MoveToDirHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(MoveToDirHandler.class.getName());

    /**
     * Moves the pictures of the supplied nodes to the target directory
     *
     * @param request The request
     */
    @Subscribe
    public void handleEvent(final MoveToDirRequest request) {
        if (!request.targetLocation().isDirectory()) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("htmlDistIsDirError"),
                    GENERIC_ERROR,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!request.targetLocation().canWrite()) {
            JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                    Settings.getJpoResources().getString("htmlDistCanWriteError"),
                    GENERIC_ERROR,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        var picsMoved = 0;
        for (SortableDefaultMutableTreeNode node : request.nodes()) {
            if (node.getUserObject() instanceof PictureInfo pi) {
                if (ConsolidateGroupWorker.movePicture(pi, request.targetLocation())) {
                    picsMoved++;
                }
            } else {
                LOGGER.log(Level.INFO, "Node {0} is not a picture. Skipping the move for this node.", node);
            }
        }
        JOptionPane.showMessageDialog(Settings.getAnchorFrame(),
                String.format(Settings.getJpoResources().getString("moveToNewLocationSuccess"), picsMoved, request.nodes().size()),
                GENERIC_INFO,
                JOptionPane.INFORMATION_MESSAGE);
    }
}
