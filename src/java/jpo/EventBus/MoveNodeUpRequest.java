package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to indicate that the user would like move the supplied node up in the group
 * @author Richard eigenmann
 */
public class MoveNodeUpRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * Request to indicate that the user would like move the supplied node up in the group
     * @param node The node for which the user would like the dialog to be done
     */
    public MoveNodeUpRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * The node which should be moved
     * @return 
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
