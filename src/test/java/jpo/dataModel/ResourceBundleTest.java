package jpo.dataModel;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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
public class ResourceBundleTest extends TestCase {
    
    public ResourceBundleTest(String testName) {
        super(testName);
    }
    
    ResourceBundle jpoResources1;
    ResourceBundle jpoResources2;
    ResourceBundle jpoResources3;
    ResourceBundle jpoResources4;
    
    protected void setUp() throws Exception {
        try {
            jpoResources1 = ResourceBundle.getBundle("jpo.JpoResources", Locale.ENGLISH);
            jpoResources2 = ResourceBundle.getBundle("jpo.JpoResources", Locale.GERMAN);
            jpoResources3 = ResourceBundle.getBundle("jpo.JpoResources", Locale.SIMPLIFIED_CHINESE);
            jpoResources4 = ResourceBundle.getBundle("jpo.JpoResources", Locale.TRADITIONAL_CHINESE);
            
        } catch ( MissingResourceException x ) {
            fail( "Resource Bundles could not be found." );
        }
    }
    
    public void testResourceBundlesFound() {
        assertNotNull("Testing English bundle found", jpoResources1 );
        assertNotNull("Testing German bundle found", jpoResources2 );
        assertNotNull("Testing SimplifiedChinese bundle found", jpoResources3 );
        assertNotNull("Testing TraditionalChinese bundle found", jpoResources4 );
    }
    
    public void testResourceLocale1() {
        //System.out.println("English bundle is reporting locale: " + jpoResources1.getLocale());
        assertEquals("Testing English bundle locale", Locale.ENGLISH, jpoResources1.getLocale() );
    }
    
    public void testResourceLocale2() {
        //System.out.println("German bundle is reporting locale: " + jpoResources2.getLocale());
        assertEquals("Testing German bundle locale", Locale.GERMAN, jpoResources2.getLocale() );
    }
    
    public void testResourceLocale3() {
        //System.out.println("SimplifiedChinese bundle is reporting locale: " + jpoResources3.getLocale());
        assertEquals("Testing SimplifiedChinese bundle locale", Locale.SIMPLIFIED_CHINESE, jpoResources3.getLocale() );
    }
    
    public void testResourceLocale4() {
        //System.out.println("TraditionalChinese bundle is reporting locale: " + jpoResources4.getLocale());
        assertEquals("Testing TraditionalChinese bundle locale", Locale.TRADITIONAL_CHINESE, jpoResources4.getLocale() );
    }
    
    public void testRetrieveResourceLocale1() {
        assertEquals("Testing English bundle locale", "New Collection", jpoResources1.getString("FileNewJMenuItem") );
    }
    
    public void testRetrieveResourceLocale2() {
        assertEquals("Testing English bundle locale", "Neue Sammlung", jpoResources2.getString("FileNewJMenuItem") );
    }
    
    public void testRetrieveResourceLocale3() {
        assertEquals("Testing English bundle locale", "新建图片集", jpoResources3.getString("FileNewJMenuItem") );
    }
    
    public void testRetrieveResourceLocale4() {
        assertEquals("Testing English bundle locale", "新建圖片集", jpoResources4.getString("FileNewJMenuItem") );
    }
    
    public void testChangeLocale() {
        assertEquals("Testing English bundle locale", "New Collection", jpoResources1.getString("FileNewJMenuItem") );
        jpoResources1 = ResourceBundle.getBundle("jpo.JpoResources", Locale.GERMAN);
        assertEquals("Testing getString from the changed bundle", "Neue Sammlung", jpoResources1.getString("FileNewJMenuItem") );
        assertEquals("Testing verifying that the changed bundle is now German", Locale.GERMAN, jpoResources1.getLocale() );
    }
    
}