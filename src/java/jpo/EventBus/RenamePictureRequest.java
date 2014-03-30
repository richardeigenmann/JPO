package jpo.EventBus;

import jpo.dataModel.SortableDefaultMutableTreeNode;


/**
 * The receiver of this request is supposed to bring up the file rename dialog for the selected node
 * 
 * @author Richard eigenmann
 */
public class RenamePictureRequest implements Request {

    private final SortableDefaultMutableTreeNode node;

    /**
     * A request to rename the supplied node 
     * @param node The node to rename
     */
    public RenamePictureRequest( SortableDefaultMutableTreeNode node ) {
        this.node = node;
    }

    /**
     * Returns the node to be renamed
     * @return the Node with the picture
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

}
