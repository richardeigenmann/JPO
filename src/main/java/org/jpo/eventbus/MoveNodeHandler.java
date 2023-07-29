package org.jpo.eventbus;

import com.google.common.eventbus.Subscribe;
import org.jpo.cache.QUEUE_PRIORITY;

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
@EventHandler
public class MoveNodeHandler {
    /**
     * Moves the node to the first position in the group
     *
     * @param request The node on which the request was made
     */
    @Subscribe
    public void handleEvent(final MoveNodeToTopRequest request) {
        for( final var node : request.node()) {
            node.moveNodeToTop();
        }
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(request.node(), true, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Moves the node up one position
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeUpRequest request) {
        for( final var node : request.node()) {
            node.moveNodeUp();
        }
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(request.node(), true, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }


    /**
     * Moves the node down one position
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeDownRequest request) {
        for( final var node : request.node()) {
            node.moveNodeDown();
        }
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(request.node(), true, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Moves the node to the last position
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeToBottomRequest request) {
        for( final var node : request.nodes()) {
            node.moveNodeToBottom();
        }
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(request.nodes(), true, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Indents the nodes
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveIndentRequest request) {
        for( final var node : request.nodes()) {
            node.indentNode();
        }
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(request.nodes(), true, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Outdents the nodes
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveOutdentRequest request) {
        for( final var node : request.nodes()) {
            node.outdentNode();
        }
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(request.nodes(), true, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

    /**
     * Moves the movingNode into the last child position of the target node
     *
     * @param request the request
     */
    @Subscribe
    public void handleEvent(final MoveNodeToNodeRequest request) {
        for( final var node : request.nodes()) {
            node.moveToLastChild(request.targetNode());
        }
        JpoEventBus.getInstance().post(new RefreshThumbnailRequest(request.nodes(), true, QUEUE_PRIORITY.MEDIUM_PRIORITY));
    }

}
