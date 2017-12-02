package jpo.EventBus;

/**
 * This request indicates that the user wants open a recent collection
 * <p>
 * <strong>Note:</strong> It will not check for unsaved updates. To check for those wrap this in a UnsavedUpdatesDialogRequest:
 * <p>
 * {@code JpoEventBus.getInstance().post( new }{@link jpo.EventBus.UnsavedUpdatesDialogRequest UnsavedUpdatesDialogRequest}{@code ( new OpenRecentCollectionRequest())  ); }
 
 *
 * @author Richard Eigenmann
 */
public class OpenRecentCollectionRequest implements Request {

    private final int index;

    /**
     * A request to load a file
     * @param index the index in the {@link jpo.dataModel.Settings#recentCollections} array
     *			indicating the file to load.
     */
    public OpenRecentCollectionRequest( int index ) {
        this.index = index;
    }
    
    /**
     * Returns the Index number of the recent collection that is to be opened
     * @return  the index number
     */
    public int getIndex() {
        return index;
    }

}
