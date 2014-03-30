package jpo.EventBus;

import java.io.File;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/**
 * This event indicates that the collection has finished loading
 * 
 * @author Richard eigenmann
 */
public class FinishedLoadingCollectionEvent implements  NotificationEvent {

    private final SortableDefaultMutableTreeNode rootNode;
    private final File loadedFile;

    /**
     * This event indicates that the collection has finished loading
     * @param rootNode  The root node of the loaded collection
     * @param loadedFile The file that was loaded
     */
    public FinishedLoadingCollectionEvent( SortableDefaultMutableTreeNode rootNode, File loadedFile ) {
        this.rootNode = rootNode;
        this.loadedFile = loadedFile;
    }

    /**
     * Returns the root node of the loaded collection
     * @return the root node
     */
    public SortableDefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    /**
     * Returns the file that was loaded
     * @return the file that was loaded
     */
    public File getLoadedFile() {
        return loadedFile;
    }

    
}
