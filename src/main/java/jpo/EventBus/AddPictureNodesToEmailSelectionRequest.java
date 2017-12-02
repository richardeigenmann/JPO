package jpo.EventBus;

import java.util.List;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the supplied picture nodes should be added
 * to the email selection
 *
 * @author Richard Eigenmann
 */
public class AddPictureNodesToEmailSelectionRequest implements Request {

    private final List<SortableDefaultMutableTreeNode> nodesList;

    /**
     * A request to add the nodes to the email selection
     *
     * @param nodesList The nodes to add
     */
    public AddPictureNodesToEmailSelectionRequest( List<SortableDefaultMutableTreeNode> nodesList ) {
        this.nodesList = nodesList;
    }

    /**
     * Returns the nodes whose pictures are to be added
     *
     * @return the Nodes
     */
    public List<SortableDefaultMutableTreeNode> getNodesList() {
        return nodesList;
    }

}
