package jpo.EventBus;

/**
 * This request must bring up the unsaved changes dialog
 * and allow the user to save the changes. After a successful save or 
 * Dismiss choice the nextRequest is fired
 * 
 * @author Richard eigenmann
 */
public class UnsavedUpdatesDialogRequest implements Request {

    private Request nextRequest;

    /**
     * A request bring the unsaved changes dialog
     * @param nextRequest the request to fire after saving or dismissing but not cancelling
     */
    public UnsavedUpdatesDialogRequest( Request nextRequest) {
        this.nextRequest = nextRequest;
    }

    public Request getNextRequest() {
        return nextRequest;
    }

}
