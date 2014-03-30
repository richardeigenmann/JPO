package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to sort a group by the specified criteria
 * 
 * @author Richard eigenmann
 */
public class SortGroupRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final int sortCriteria;

    /**
     * A request to sort the group
     * @param node The node to which should be sorted
     * @param sortCriteria
     */
    public SortGroupRequest( SortableDefaultMutableTreeNode node, int sortCriteria ) {
        this.node = node;
        this.sortCriteria = sortCriteria;
    }

    /**
     * Returns the node to which the collection should be added
     * @return the Node with the group
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * Returns the sort criteria
     * @return 
     */
    public int getSortCriteria() {
        return sortCriteria;
    }
    
}
