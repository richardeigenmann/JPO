package jpo;

import java.util.Locale;
import javax.swing.event.ChangeListener;
import junit.framework.*;

/*
 * ApplicationJMenuBarTest.java
 * JUnit based test
 *
 */

/**
 *
 * @author Richard Eigenmann
 */
public class SettingsTest extends TestCase implements LocaleChangeListener{
    
    public SettingsTest(String testName) {
        super(testName);
    }
    
    private int countLocaleChanged;
    
    protected void setUp() throws Exception {
        countLocaleChanged = 0;
    }
    
    protected void tearDown() throws Exception {
    }
    
    
    /**
     *  As soon as a Settings Object exists there should always be a current locale available.
     */
    public void testCurrentLocale() {
        assertNotNull("Testing that current locale exists", Settings.getCurrentLocale() );
        //System.out.println("The currentLocale at the beginning of the unit test is: " + Settings.getCurrentLocale().toString() );
        //System.out.println("The jpoResources locale at the beginning of the unit test is: " + Settings.jpoResources.getLocale().toString() );
    }
    
    
    public void testSetLocale() {
        //System.out.println("The currentLocale before the change is: " + Settings.getCurrentLocale().toString() );
        //System.out.println("The jpoResources locale before the change is: " + Settings.jpoResources.getLocale().toString() );
        //System.out.println("Changing to German");
        Settings.setLocale( Locale.GERMAN );
        //System.out.println("The currentLocale after the change is: " + Settings.getCurrentLocale().toString() );
        //System.out.println("The jpoResources locale after the change is: " + Settings.jpoResources.getLocale().toString() );
        assertEquals("Testing the locale change to German", Locale.GERMAN, Settings.getCurrentLocale() );
        //System.out.println("Changing to Simplified Chinese");
        Settings.setLocale( Locale.SIMPLIFIED_CHINESE );
        //System.out.println("The currentLocale after the second change is: " + Settings.getCurrentLocale().toString() );
        //System.out.println("The jpoResources locale after the change is: " + Settings.jpoResources.getLocale().toString() );
        assertEquals("Testing the locale change to Simplified Chinese", Locale.SIMPLIFIED_CHINESE, Settings.getCurrentLocale() );
    }
    
    public void testSetLocaleResourceBundleEffect() {
        //System.out.println("The currentLocale before the change is: " + Settings.getCurrentLocale().toString() );
        //System.out.println("The jpoResources locale before the change is: " + Settings.jpoResources.getLocale().toString() );
        //System.out.println("Changing to German");
        Settings.setLocale( Locale.GERMAN );
        //System.out.println("The currentLocale after the change is: " + Settings.getCurrentLocale().toString() );
        //System.out.println("The jpoResources locale after the change is: " + Settings.jpoResources.getLocale().toString() );
        assertEquals("Testing the ResourceBundle change to German", Locale.GERMAN, Settings.jpoResources.getLocale() );
        //System.out.println("Changing to Simplified Chinese");
        Settings.setLocale( Locale.SIMPLIFIED_CHINESE );
        //System.out.println("The currentLocale after the second change is: " + Settings.getCurrentLocale().toString() );
        //System.out.println("The jpoResources locale after the change is: " + Settings.jpoResources.getLocale().toString() );
        assertEquals("Testing the ResourceBundle change to Simplified Chinese", Locale.SIMPLIFIED_CHINESE, Settings.jpoResources.getLocale() );
    }
    
    
    public void testSetLocaleResourceBundleStrings() {
        Settings.setLocale( Locale.GERMAN );
        //System.out.println("After a change to German locale the jpo ResourceBundle returns: " + Settings.jpoResources.getString("FileNewJMenuItem") );
        assertEquals("Testing the German string", "Neue Sammlung", Settings.jpoResources.getString("FileNewJMenuItem") );
        Settings.setLocale( Locale.SIMPLIFIED_CHINESE );
        //System.out.println("After a change to Simplified Chinese the jpo ResourceBundle returns: " + Settings.jpoResources.getString("FileNewJMenuItem") );
        assertEquals("Testing the Simplified Chinese string", "新建图片集", Settings.jpoResources.getString("FileNewJMenuItem") );
    }
    
    
    public void testChangeListener() {
        // ensure that we will have a notifyable change
        Settings.setLocale( Locale.ENGLISH );
        //System.out.println("testChangeListener.Adding ChangeListener");
        Settings.addLocaleChangeListener( this );
        //System.out.println("testChangeListener.Changing to Chinese");
        Settings.setLocale( Locale.SIMPLIFIED_CHINESE );
        //System.out.println("testChangeListener.Removing ChangeListener");
        Settings.removeLocaleChangeListener( this );
        //System.out.println("testChangeListener.Changing to Traditional Chinese");
        Settings.setLocale( Locale.TRADITIONAL_CHINESE );
        assertEquals("Checking that the LocaleChangeListener fired off the correct number of times", 1, countLocaleChanged );
    }
    
    public void localeChanged() {
        //System.out.println("localeChanged: got notified");
        countLocaleChanged++;
    }
    
    /**
     *  Test the saving and reading back of the settings and whether the locale gets messed up along the way
     */
    public void testReadWriteSettingsLocale() {
        // load the settings first or we have unititialised objects which crash the writing
        Settings.loadSettings();
        // memorise the locale so that we can write it back in the end
        Locale saveLocale = Settings.getCurrentLocale();
        
        // make sure we change the locale to a defined starting point
        //System.out.println("testReadWriteSettingsLocale: Setting Locale to English and writing settings");
        Settings.setLocale( Locale.ENGLISH );
        Settings.writeSettings();
        assertEquals("Locale should not have changed after wrtiting settings!", Locale.ENGLISH, Settings.getCurrentLocale() );
        //System.out.println("testReadWriteSettingsLocale: now changing the Locale to German and loading the settings");
        Settings.setLocale( Locale.GERMAN );
        Settings.loadSettings();
        assertEquals("Locale should be back to English after the load!", Locale.ENGLISH, Settings.getCurrentLocale() );

        // and do it all again to be sure that the saved Settings aren't tricking us here:
        Settings.setLocale( Locale.GERMAN );
        Settings.writeSettings();
        assertEquals("Locale should not have changed after wrtiting settings #2", Locale.GERMAN, Settings.getCurrentLocale() );
        Settings.setLocale( Locale.SIMPLIFIED_CHINESE );
        Settings.loadSettings();
        assertEquals("Locale should be back to German after the second load!", Locale.GERMAN, Settings.getCurrentLocale() );

        // write the original settings back to prevent developer frustration
        Settings.setLocale( saveLocale );
        Settings.writeSettings();
    }
 

    /**
     *  Test the saving and reading back of the maxThumbnails setting
     */
    public void testReadWriteMaxThumbnails() {
        int saveMaxThumbnails = Settings.maxThumbnails;
        Settings.maxThumbnails = -1; //a value that it never should have
        Settings.loadSettings();
        assertTrue("testReadWriteMaxThumbnails: After loading the settings the maxThumbnails should not be -1 any more!", (-1 != Settings.maxThumbnails) );

        Settings.maxThumbnails = -2; //another value that it never should never have
        Settings.writeSettings();
        assertEquals( "After saving the settings the maxThumbnails should still be -2", -2, Settings.maxThumbnails );

        Settings.loadSettings();
        assertEquals( "After loading the negative value should have been replaced with" + Integer.toString(Settings.defaultMaxThumbnails), Settings.defaultMaxThumbnails, Settings.maxThumbnails );
        
        Settings.maxThumbnails = 53;
        Settings.writeSettings();
        Settings.maxThumbnails = 54;
        Settings.loadSettings();
        assertEquals( "After reloading the settings the maxThumbnails should be back at 53 not 54", 53, Settings.maxThumbnails );
        
        // write the original settings back to prevent developer frustration
        Settings.maxThumbnails = saveMaxThumbnails;
        Settings.writeSettings();
    }
            
            
}