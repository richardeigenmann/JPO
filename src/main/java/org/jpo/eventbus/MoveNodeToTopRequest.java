package org.jpo.eventbus;

import org.jpo.datamodel.SortableDefaultMutableTreeNode;

/**
 * Request to indicate that the user would like move the supplied node to the first position in the group
 * @author Richard Eigenmann
 */
public class MoveNodeToTopRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * Request to indicate that the user would like move the supplied node to the first position in the group
     * @param node The node for which the user would like the dialog to be done
     */
    public MoveNodeToTopRequest( SortableDefaultMutableTreeNode node ) {
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
