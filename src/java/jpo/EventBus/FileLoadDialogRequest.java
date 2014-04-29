package jpo.EventBus;

/**
 * This request indicates that the user wants to choose a file for loading
 * <p>
 * It will not check for unsaved updates. To check for those wrap this in a
 * UnsavedUpdatesDialogRequest:
 * <p>
 * {@code JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new FileLoadRequest()) ); }
 *
 * @author Richard eigenmann
 */
public class FileLoadDialogRequest implements Request {

    /**
     * A request to load a file
     */
    public FileLoadDialogRequest() {
    }

}
