package jpo.EventBus;

import jpo.dataModel.Settings.FieldCodes;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to sort a group by the specified criteria
 * 
 * @author Richard Eigenmann
 */
public class SortGroupRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final FieldCodes sortCriteria;

    /**
     * A request to sort the group
     * @param node The node to which should be sorted
     * @param sortCriteria The sort criteria
     */
    public SortGroupRequest( SortableDefaultMutableTreeNode node, FieldCodes sortCriteria ) {
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
     * @return the sort criteria index
     */
    public FieldCodes getSortCriteria() {
        return sortCriteria;
    }
    
}
