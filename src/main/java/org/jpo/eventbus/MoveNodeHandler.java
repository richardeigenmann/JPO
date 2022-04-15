package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.cache.QUEUE_PRIORITY;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import java.util.ArrayList;
import java.util.List;

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

public class MoveNodeHandler {
    /**
     * Moves the node to the first position in the group
     *
     * @param request The node on which the request was made
     */
    @Subscribe
    public void handleEvent(final MoveNodeToTopRequest request) {
        final SortableDefaultMutableTreeNode popupNode = request.node();
        popupNode.moveNodeToTop();
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(popupNode.getParent());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Moves the node up one position
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeUpRequest request) {
        final SortableDefaultMutableTreeNode popupNode = request.node();
        popupNode.moveNodeUp();
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(popupNode.getParent());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }


    /**
     * Moves the node down one position
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeDownRequest request) {
        final SortableDefaultMutableTreeNode popupNode = request.node();
        popupNode.moveNodeDown();
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(popupNode.getParent());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Moves the node to the last position
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeToBottomRequest request) {
        final SortableDefaultMutableTreeNode popupNode = request.node();
        popupNode.moveNodeToBottom();
        final List<SortableDefaultMutableTreeNode> nodes = new ArrayList<>();
        nodes.add(popupNode.getParent());
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(nodes, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Indents the nodes
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveIndentRequest request) {
        final List<SortableDefaultMutableTreeNode> nodes = request.nodes();
        for (SortableDefaultMutableTreeNode node : nodes) {
            node.indentNode();
        }
    }

    /**
     * Outdents the nodes
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveOutdentRequest request) {
        final List<SortableDefaultMutableTreeNode> nodes = request.nodes();
        for (SortableDefaultMutableTreeNode node : nodes) {
            node.outdentNode();
        }
    }

    /**
     * Moves the movingNode into the last child position of the target node
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeToNodeRequest request) {
        final List<SortableDefaultMutableTreeNode> movingNodes = request.movingNodes();
        final SortableDefaultMutableTreeNode targetGroup = request.targetNode();
        for (final SortableDefaultMutableTreeNode movingNode : movingNodes) {
            movingNode.moveToLastChild(targetGroup);
        }
    }

}
