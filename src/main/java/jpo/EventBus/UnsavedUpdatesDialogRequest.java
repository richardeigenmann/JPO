package jpo.EventBus;

/**
 * This request must bring up the unsaved changes dialog
 * and allow the user to save the changes. After a successful save or 
 * Dismiss choice the nextRequest is fired.
 * 
 * <p>
 * 
 * <img src="doc-files/UnsavedChangesLogic.png" alt="Unsaved Changes Logic">
 * 
 * @author Richard Eigenmann
 */
public class UnsavedUpdatesDialogRequest implements Request {

    private final Request nextRequest;

    /**
     * A request bring the unsaved changes dialog
     * @param nextRequest the request to fire after saving or dismissing but not cancelling
     */
    public UnsavedUpdatesDialogRequest( Request nextRequest) {
        this.nextRequest = nextRequest;
    }

    /**
     * Returns the request to fire after saving or dismissing but not cancelling
     * @return the next request
     */
    public Request getNextRequest() {
        return nextRequest;
    }

}
