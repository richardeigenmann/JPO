package jpo.EventBus;

import java.util.List;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the supplied picture nodes should be removed
 * from the email selection
 *
 * @author Richard eigenmann
 */
public class RemovePictureNodesFromEmailSelectionRequest implements Request {

    private final List<SortableDefaultMutableTreeNode> nodesList;

    /**
     * A request to remove the nodes from the email selection
     *
     * @param nodesList The nodes to remove
     */
    public RemovePictureNodesFromEmailSelectionRequest( List<SortableDefaultMutableTreeNode> nodesList ) {
        this.nodesList = nodesList;
    }

    /**
     * Returns the nodes whose pictures are to be removed
     *
     * @return the Nodes
     */
    public List<SortableDefaultMutableTreeNode> getNodesList() {
        return nodesList;
    }

}
