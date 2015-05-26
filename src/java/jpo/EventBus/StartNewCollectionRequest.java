package jpo.EventBus;

/**
 * This request indicates that the user wants to start a new collection
 *
 * It will not check for unsaved updates. To check for those wrap this in a
 * UnsavedUpdatesDialogRequest:
 *
 * JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new
 * StartNewCollectionRequest()) );
 *
 *
 * @author Richard Eigenmann
 */
public class StartNewCollectionRequest implements Request {

    /**
     * A request to start a new collection
     */
    public StartNewCollectionRequest() {
    }

}
