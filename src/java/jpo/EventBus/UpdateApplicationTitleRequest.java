package jpo.EventBus;

/**
 * This request indicates that the application should change its title
 * 
 * @author Richard Eigenmann
 */
public class UpdateApplicationTitleRequest implements Request {

    private final String title;

    /**
     * This request indicates that the application should change its title
     * @param newTitle The new title for the application
     */
    public UpdateApplicationTitleRequest( String newTitle ) {
        this.title = newTitle;
    }

    /**
     * Returns the new title that the application should have
     * @return the new title
     */
    public String getTitle() {
        return title;
    }


    
}
