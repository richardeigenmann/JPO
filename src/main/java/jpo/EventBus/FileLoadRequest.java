package jpo.EventBus;

import java.io.File;

/**
 * This request indicates that a file is to be loaded and shown
 * <p>
 * It will not check for unsaved updates. To check for those wrap this in a
 * UnsavedUpdatesDialogRequest:
 * <p>
 * {@code JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new FileLoadRequest()) ); }
 *
 * @author Richard Eigenmann
 */
public class FileLoadRequest implements Request {

    
    /**
     * The file to load
     */
    private final File fileToLoad;

    /**
     * A request to load a file
     * @param fileToLoad the file to load
     */
    public FileLoadRequest( File fileToLoad) {
        this.fileToLoad = fileToLoad;
    }

    /**
     * Returns the file to be loaded.
     *
     * @return The file to load
     */
    public File getFileToLoad() {
        return fileToLoad;
    }

}
