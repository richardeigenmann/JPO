package jpo.EventBus;

/**
 * This request indicates that the GUI to remove the old lowres thumbnail files
 * should be shown
 *
 * @author Richard Eigenmann
 */
public class RemoveOldLowresThumbnailsRequest implements Request {


    /**
     * A request to bring up the GUI that removes the old lowres thumbnail files
     * that JPO up to version 0.11 used to create
     *
     * @param lowresUrls  The list of lowresUrls to remove
     */
    public RemoveOldLowresThumbnailsRequest( StringBuilder lowresUrls ) {
        this.lowresUrls = lowresUrls;
    }

    
    private final StringBuilder lowresUrls;
    
    /**
     * Returns a StringBuilder of the lowres Urls
     * @return Returns a StringBuilder of the lowres Urls
     */
    public StringBuilder getLowresUrls() {
        return lowresUrls;
    }

}
