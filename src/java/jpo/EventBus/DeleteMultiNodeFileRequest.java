package jpo.EventBus;

import java.util.List;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to delete the files of a specific 
 * nodes and remove the nodes
 *
 * @author Richard Eigenmann
 */
public class DeleteMultiNodeFileRequest implements Request {

    private final List<SortableDefaultMutableTreeNode> nodes;

    /**
     * This request indicates that the user wants to delete the file of a specific node
     *
     * @param nodes The node for which the file and the nodes are to be deleted
     */
    public DeleteMultiNodeFileRequest( List<SortableDefaultMutableTreeNode> nodes ) {
        this.nodes = nodes;
    }

    /**
     * The nodes to delete
     *
     * @return the nodes
     */
    public List<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }

}
