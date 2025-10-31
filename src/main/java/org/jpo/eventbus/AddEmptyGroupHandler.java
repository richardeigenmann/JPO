package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.GroupInfo;
import org.jpo.gui.Settings;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2023-2025 Richard Eigenmann.
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
 * Adds an empty group to the indicated group node
 */
@EventHandler
public class AddEmptyGroupHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(AddEmptyGroupHandler.class.getName());

    /**
     * Fulfil an AddEmptyGroup request by adding a "New Group" to the supplied node. Memorises this new group .
     * TODO: why does this check for GroupInfo, moan and then proceed anyway?
     *
     * @param request The node on which the request was made
     */
    @Subscribe
    public void handleEvent(final AddEmptyGroupRequest request) {
        final var parentNode = request.nodeWhichReceivesChild();
        if (!(parentNode.getUserObject() instanceof GroupInfo)) {
            LOGGER.log(Level.WARNING, "node {0} is of type {1} instead of GroupInfo. Proceeding anyway.", new Object[]{parentNode.getUserObject(), parentNode.getUserObject().getClass()});
        }
        final var newNode = parentNode.addGroupNode("New Group");
        Settings.memorizeGroupOfDropLocation(newNode);
        JpoEventBus.getInstance().post(new RecentDropNodesChangedEvent());
    }

}
