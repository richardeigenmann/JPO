package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;

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

/**
 * Adds the pictures in the supplied group to the email selection
 */
public class AddGroupToEmailSelectionHandler {
    /**
     * Adds the pictures in the supplied group to the email selection
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final AddGroupToEmailSelectionRequest request) {
        final SortableDefaultMutableTreeNode groupNode = request.node();
        for (final Enumeration<TreeNode> e = groupNode.breadthFirstEnumeration(); e.hasMoreElements(); ) {
            final SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) e.nextElement();
            if (n.getUserObject() instanceof PictureInfo) {
                Settings.getPictureCollection().addToMailSelection(n);
            }
        }
    }

}
