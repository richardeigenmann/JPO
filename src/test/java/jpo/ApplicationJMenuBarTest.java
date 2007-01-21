package jpo;

import java.lang.reflect.Field;
import java.util.Locale;
import javax.swing.JMenuItem;
import junit.framework.*;

/*
 * ApplicationJMenuBarTest.java
 * JUnit based test
 *
 * Created on 20 January 2007, 08:42
 */

/**
 *
 * @author Richard Eigenmann
 */
public class ApplicationJMenuBarTest extends TestCase implements ApplicationMenuInterface{
    
    public ApplicationJMenuBarTest(String testName) {
        super(testName);
    }
    
    /**
     *  A handle to the Object we are testing.
     */
    private ApplicationJMenuBar ajm;
    
    /**
     * A handle to one of the menu items which is private but exposed
     * through the Reflection API.
     **/
    private JMenuItem fnmi;
    
    protected void setUp() throws Exception {
        ajm = new ApplicationJMenuBar( this );
        try {
            Field field = ajm.getClass().getDeclaredField( "FileNewJMenuItem" );
            field.setAccessible( true );
            fnmi = (JMenuItem) field.get(ajm);
        } catch ( Exception x ) {
            fail("Could not test field FileNewJMenuItem");
        }
    }
    
    public void testMenuItemTextInDefaultLanguage() {
        assertNotNull("Testing menu item text in default language", fnmi.getText() );
    }

    public void testMenuItemTextInEnglish() {
        Settings.setLocale( Locale.ENGLISH );
        assertEquals("Testing menu item text in English", fnmi.getText(), "New Collection" );
    }

    public void testMenuItemTextInGerman() {
        //System.out.println("Current language: "+ Settings.getCurrentLanguage() );
        Settings.setLocale( Locale.GERMANY );
        //System.out.println("Current language: "+ Settings.getCurrentLanguage() );
        //System.out.println("Got: " + fnmi.getText() );
        assertEquals("Testing menu item text in German", fnmi.getText(), "Neue Sammlung" );
    }

    
    /**
     *   Signals that the user wants to create a new empty collection.
     */
    public void requestFileNew() {}
    
    
    /**
     *   Signals that the user wants to add pictures to his collection.
     */
    public void requestFileAdd() {}
    
    
    /**
     *   Signals that the user wants to add pictures from the camera to his collection.
     */
    public void requestFileAddFromCamera() {}
    
    
    /**
     *   Signals that the user wants to load a collection file.
     */
    public void requestFileLoad() {}
    
    
    /**
     *  Signals that the user wants to load a recently opened file. The parameter i
     *  indicates which file to open in the Settings.recentCollections array.
     *
     *  @param 	i	the index in the {@link Settings#recentCollections} array
     *			indicating the file to load.
     */
    public void requestOpenRecent( int i ) {}
    
    
    /**
     *   Signals that the user wants to save a collection file.
     */
    public void requestFileSave() {}
    
    
    /**
     *   Signals that the user wants to save a collection file under a new name.
     */
    public void requestFileSaveAs() {}
    
    
    /**
     *   Signals that the user wants to leave the application.
     */
    public void requestExit() {}
    
    
    /**
     *   Signals that the user wants to search the collection.
     */
    public void requestEditFind() {}
    
    
    /**
     *   Signals that the user wants to set up a camera
     */
    public void requestEditCameras() {}
    
    
    /**
     *   Signals that the user wants to reconcile pictures in a
     *   directory with those in his collection.
     */
    public void requestCheckDirectories() {}
    
    
    /**
     *   Signals that the user wants to see the Collection Properties dialog.
     */
    public void requestCollectionProperties() {}
    
    
    /**
     *   Signals that the user wants to have the Collection Integrity checked.
     */
    public void requestCheckIntegrity() {}
    
    
    /**
     *   Signals that the user wants to edit the settings of the application.
     */
    public void requestEditSettings() {}
    
    
    /**
     *   Signals that the user wants to see a random slideshow
     */
    public void performSlideshow() {}
    
}
