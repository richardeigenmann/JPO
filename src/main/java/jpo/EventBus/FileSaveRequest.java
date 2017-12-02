package jpo.EventBus;

/**
 * This request indicates that the user wants to save the collection
 *
 * @author Richard Eigenmann
 */
public class FileSaveRequest implements Request {


    private Request onSucccessNextRequest;
    /**
     * A request to save the collection
     */
    public FileSaveRequest() {
    }

    /**
     * Optional next request to call after successfully saving the file.
     *
     * @param onSuccessNextRequest the next request
     */
    public void setOnSuccessNextRequest( Request onSuccessNextRequest ) {
        this.onSucccessNextRequest = onSuccessNextRequest;
    }

    /**
     * Returns the next event to submit only if the file was successfully saved
     *
     * @return The next event to execute on a successful save
     */
    public Request getOnSuccessNextRequest() {
        return onSucccessNextRequest;
    }

}
