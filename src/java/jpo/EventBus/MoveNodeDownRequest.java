package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * Request to indicate that the user would like move the supplied node down in the group
 * @author Richard eigenmann
 */
public class MoveNodeDownRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * Request to indicate that the user would like move the supplied node down in the group
     * @param node The node for which the user would like the dialog to be done
     */
    public MoveNodeDownRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * The node which should be moved
     * @return the node
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
