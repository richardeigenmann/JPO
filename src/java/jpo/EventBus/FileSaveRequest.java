package jpo.EventBus;

/**
 * This request indicates that the user wants to save the collection
 *
 * @author Richard eigenmann
 */
public class FileSaveRequest implements Request {

    /**
     * A request to save the collection
     */
    public FileSaveRequest() {
    }

    private Request onSucccessNextRequest = null;

    /**
     * Optional next request to call after successfully saving the file.
     *
     * @param onSuccessNextRequest
     */
    public void setOnSuccessNextRequest( Request onSuccessNextRequest ) {
        this.onSucccessNextRequest = onSuccessNextRequest;
    }

    public Request getOnSuccessNextRequest() {
        return onSucccessNextRequest;
    }

}
