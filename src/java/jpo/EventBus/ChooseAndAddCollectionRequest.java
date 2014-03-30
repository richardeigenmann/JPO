package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to bring up a GUI to pick a collection and it to the group
 * 
 * @author Richard eigenmann
 */
public class ChooseAndAddCollectionRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to bring up a GUI to add a collection
     * @param node The node to which the collection should be added
     */
    public ChooseAndAddCollectionRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the node to which the collection should be added
     * @return the Node with the group
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
