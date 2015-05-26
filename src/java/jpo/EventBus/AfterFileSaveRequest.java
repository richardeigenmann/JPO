package jpo.EventBus;

/**
 * This request opens the dialog after a file save
 *
 * @author Richard Eigenmann
 */
public class AfterFileSaveRequest implements Request {

    /**
     * A request opens the dialog after a file save
     * @param autoLoadCollectionFile The file to automatically load if the checkbox is ticked
     */
    public AfterFileSaveRequest( String autoLoadCollectionFile ) {
        this.autoLoadCollectionFile = autoLoadCollectionFile;
    }

    private final String autoLoadCollectionFile;


    /**
     * Returns the filename of the file to autoload if the checkbox is ticked
     *
     * @return The file to autoload 
     */
    public String getAutoLoadCollectionFile() {
        return autoLoadCollectionFile;
    }

}
