package jpo.EventBus;

import java.util.ArrayList;
import java.util.List;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.cache.ThumbnailQueueRequest.QUEUE_PRIORITY;

/*
 RefreshThumbnailRequest.java: Request to refresh thumbnails

 Copyright (C) 2015  Richard Eigenmann.
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
 * This request indicates that the thumbnails of the specified nodes are
 * supposed to be refreshed
 *
 * @author Richard Eigenmann
 */
public class RefreshThumbnailRequest implements Request {

    private final List<SortableDefaultMutableTreeNode> nodes;
    private final QUEUE_PRIORITY priority;

    /**
     * A request to indicate that the specified thumbnail is supposed to be
     * refreshed
     *
     * @param node The node with the thumbnails to show
     * @param priority The priority for the creation queue
     */
    public RefreshThumbnailRequest( SortableDefaultMutableTreeNode node, QUEUE_PRIORITY priority ) {
        nodes = new ArrayList<>();
        nodes.add( node );
        this.priority = priority;
    }

    /**
     * A request to indicate that the specified thumbnails are supposed to be
     * refreshed
     *
     * @param nodes The nodes to be refreshed
     * @param priority The priority for the creation queue
     */
    public RefreshThumbnailRequest( List<SortableDefaultMutableTreeNode> nodes, QUEUE_PRIORITY priority ) {
        this.nodes = nodes;
        this.priority = priority;
    }

    /**
     * Returns the nodes to be refreshed
     *
     * @return the Nodes to refresh
     */
    public List<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }

    /**
     * Return the queue priority
     * @return The priority for the queue
     */
    public QUEUE_PRIORITY getPriority() {
        return priority;
    }

}
