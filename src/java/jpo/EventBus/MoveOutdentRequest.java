package jpo.EventBus;

import java.util.List;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the nodes should be outdented
 *
 * @author Richard Eigenmann
 */
public class MoveOutdentRequest implements Request {

    private final List<SortableDefaultMutableTreeNode> nodes;

    /**
     * This request indicates that the nodes should be outdented
     *
     * @param nodes The nodes to indent
     */
    public MoveOutdentRequest( List<SortableDefaultMutableTreeNode> nodes ) {
        this.nodes = nodes;
    }

    /**
     * The nodes to outdent
     *
     * @return the nodes
     */
    public List<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }

}
