package jpo.EventBus;

import java.util.ArrayList;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to remove specific nodes
 * 
 * @author Richard eigenmann
 */
public class RemoveNodeRequest implements Request {

    private final ArrayList<SortableDefaultMutableTreeNode> nodes;

    /**
     * This request indicates that the user wants to remove specific nodes
     * @param nodes The node to remove from it's parent
     */
    public RemoveNodeRequest( ArrayList<SortableDefaultMutableTreeNode> nodes ) {
        this.nodes = nodes;
    }

    /**
     * The nodes to remove from their parents
     * @return the nodes
     */
    public ArrayList<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }

    
}
