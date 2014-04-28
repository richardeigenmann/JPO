package jpo.EventBus;

import java.util.List;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to remove specific nodes
 *
 * @author Richard eigenmann
 */
public class RemoveNodeRequest implements Request {

    private final List<SortableDefaultMutableTreeNode> nodes;

    /**
     * This request indicates that the user wants to remove specific nodes
     *
     * @param nodes The node to remove from it's parent
     */
    public RemoveNodeRequest( List<SortableDefaultMutableTreeNode> nodes ) {
        this.nodes = nodes;
    }

    /**
     * The nodes to remove from their parents
     *
     * @return the nodes
     */
    public List<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }

}
