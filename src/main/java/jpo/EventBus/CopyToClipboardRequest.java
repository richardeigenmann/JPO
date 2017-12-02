package jpo.EventBus;

import java.util.List;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to copy the picture nodes to the system clipboard
 *
 * @author Richard Eigenmann
 */
public class CopyToClipboardRequest implements Request {

    private final List<SortableDefaultMutableTreeNode>nodes;

    /**
     * Request to copy the picture modes to the clipboard
     *
     * @param nodes The nodes
     */
    public CopyToClipboardRequest( List<SortableDefaultMutableTreeNode>nodes ) {
        this.nodes = nodes;
    }

    /**
     * The nodes for which the operation should be done
     *
     * @return the nodes
     */
    public List<SortableDefaultMutableTreeNode>getNodes() {
        return nodes;
    }

    
    
}
