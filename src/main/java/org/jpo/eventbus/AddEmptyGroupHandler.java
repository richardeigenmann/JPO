package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.GroupInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2022  Richard Eigenmann.
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

public class AddEmptyGroupHandler {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(AddEmptyGroupHandler.class.getName());

    /**
     * when the App sees an AddEmptyGroup request it will sort the group by the
     * criteria
     *
     * @param request The node on which the request was made
     */
    @Subscribe
    public void handleEvent(final AddEmptyGroupRequest request) {
        final SortableDefaultMutableTreeNode node = request.node();
        if (!(node.getUserObject() instanceof GroupInfo)) {
            LOGGER.log(Level.WARNING, "node {0} is of type {1} instead of GroupInfo. Proceeding anyway.", new Object[]{node.getUserObject(), node.getUserObject().getClass()});
        }
        final SortableDefaultMutableTreeNode newNode = node.addGroupNode("New Group");
        Settings.memorizeGroupOfDropLocation(newNode);
        JpoEventBus.getInstance().post(new RecentDropNodesChangedEvent());
        JpoEventBus.getInstance().post(new ShowGroupRequest(newNode));
    }

}
