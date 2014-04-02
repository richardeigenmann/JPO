package jpo.EventBus;

/**
 * This request indicates that the user wants to load a file.
 * 
 * It will not check for
 * unsaved updates. To check for those wrap this in a UnsavedUpdatesDialogRequest:
 * 
 * JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new FileLoadRequest())  );
 * 
 * @author Richard eigenmann
 */
public class FileLoadRequest implements Request {


    /**
     * A request to load a file
     */
    public FileLoadRequest() {
    }


}
