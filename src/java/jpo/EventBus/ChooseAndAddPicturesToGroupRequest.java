package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to bring up a GUI to pick pictures 
 * and add them to the group
 * 
 * @author Richard Eigenmann
 */
public class ChooseAndAddPicturesToGroupRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to bring up a GUI to add pictures
     * @param node The node to which the pictures should be added
     */
    public ChooseAndAddPicturesToGroupRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the node to which the pictures should be added
     * @return the Node with the group
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
