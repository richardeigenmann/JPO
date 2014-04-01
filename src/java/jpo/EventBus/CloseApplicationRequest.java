package jpo.EventBus;

/**
 * This request indicates that the user wants to close the application.
 * It doesn't check for unsaved changes. You might want to consider calling
 * the request UnsavedUpdatesDialogRequest first with this as a nextRequest
 * 
 * e.g. JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new CloseApplicationRequest())  );
 * 
 * @author Richard eigenmann
 */
public class CloseApplicationRequest implements Request {


    /**
     * A request to close the application
     */
    public CloseApplicationRequest() {
    }


}
