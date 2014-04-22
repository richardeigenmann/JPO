package jpo.EventBus;

/**
 * This request indicates that the user wants to save the collection under a new
 * name
 *
 * @author Richard eigenmann
 */
public class FileSaveAsRequest implements Request {

    /**
     * A request to save the collection under a new name
     */
    public FileSaveAsRequest() {
    }

    private Request onSucccessNextRequest;

    /**
     * Optional next request to call after successfully saving the file.
     *
     * @param onSuccessNextRequest
     */
    public void setOnSuccessNextRequest( Request onSuccessNextRequest ) {
        this.onSucccessNextRequest = onSuccessNextRequest;
    }

    /**
     * Returns the next event to submit only if the file was successfully saved
     * @return The next event to execute on a successful save
     */
    public Request getOnSuccessNextRequest() {
        return onSucccessNextRequest;
    }

}
