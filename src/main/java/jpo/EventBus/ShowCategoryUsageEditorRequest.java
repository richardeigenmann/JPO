package jpo.EventBus;

import java.util.Set;
import jpo.dataModel.SortableDefaultMutableTreeNode;



/**
 * The receiver of this request is supposed to spawn the CategoryUsageEditor
 * for the supplied node.
 * 
 * @author Richard Eigenmann
 */
public class ShowCategoryUsageEditorRequest implements Request {

    private final Set<SortableDefaultMutableTreeNode> nodes;

    /**
     * A request to bring up the CategoryUsageEditor for the supplied nodes
     * @param nodes The nodes
     */
    public ShowCategoryUsageEditorRequest( Set<SortableDefaultMutableTreeNode> nodes ) {
        this.nodes = nodes;
    }

    /**
     * Returns the node for which the CategoryUsageEditor is to be shown.
     * @return the Node with the picture
     */
    public Set<SortableDefaultMutableTreeNode> getNodes() {
        return nodes;
    }

}
