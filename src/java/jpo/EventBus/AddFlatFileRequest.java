package jpo.EventBus;

import java.io.File;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This request indicates that the user wants to add a flat file to the supplied node
 * 
 * @author Richard eigenmann
 */
public class AddFlatFileRequest implements Request {

    private final SortableDefaultMutableTreeNode node;
    private final File flatfile;

    /**
     * A request to add the pictures in the flatfile to the supplied node
     * @param node The node to which the empty group should be added
     * @param flatfile the flat file to add
     */
    public AddFlatFileRequest( SortableDefaultMutableTreeNode node, File flatfile ) {
        this.node = node;
        this.flatfile = flatfile;
    }

    /**
     * Returns the node to which the flat file is to be added
     * @return the Node with the group
     */
    public SortableDefaultMutableTreeNode getNode() {
        return node;
    }

    /**
     * Returns the file with the pictures to add
     * @return the flat file to add
     */
    public File getFile() {
        return flatfile;
    }

    
}
