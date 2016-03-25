package jpo.EventBus;

import java.util.ArrayList;
import java.util.List;
import jpo.cache.ThumbnailQueueRequest.QUEUE_PRIORITY;
import jpo.dataModel.SortableDefaultMutableTreeNode;



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
