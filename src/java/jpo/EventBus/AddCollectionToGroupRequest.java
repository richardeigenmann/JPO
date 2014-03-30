package jpo.EventBus;

import java.io.File;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to add the supplied collection to the supplied group
 * 
 * @author Richard eigenmann
 */
public class AddCollectionToGroupRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final File collectionFile;

    /**
     * A request to add the supplied collection file to the node
     * @param node The node to which the collection should be added
     * @param collectionFile
     */
    public AddCollectionToGroupRequest( SortableDefaultMutableTreeNode node, File collectionFile ) {
        this.node = node;
        this.collectionFile = collectionFile;
    }

    /**
     * Returns the node to which the collection should be added
     * @return the Node with the group
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * Returns the collectionFile that should be added.
     * @return 
     */
    public File getCollectionFile() {
        return collectionFile;
    }
    
}
