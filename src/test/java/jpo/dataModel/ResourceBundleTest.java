package jpo.dataModel;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Tests for the resource bundles
 *
 * @author Richard Eigenmann
 */
public class ResourceBundleTest {

    private ResourceBundle jpoResources1;
    private ResourceBundle jpoResources2;
    private ResourceBundle jpoResources3;
    private ResourceBundle jpoResources4;

    /**
     * Set up for each test
     *
     * @throws Exception Can throw an Exception
     */
    @Before
    public void setUp() throws Exception {
        try {
            jpoResources1 = ResourceBundle.getBundle( "jpo.gui.JpoResources", Locale.ENGLISH );
            jpoResources2 = ResourceBundle.getBundle( "jpo.gui.JpoResources", Locale.GERMAN );
            jpoResources3 = ResourceBundle.getBundle( "jpo.gui.JpoResources", Locale.SIMPLIFIED_CHINESE );
            jpoResources4 = ResourceBundle.getBundle( "jpo.gui.JpoResources", Locale.TRADITIONAL_CHINESE );

        } catch ( MissingResourceException x ) {
            fail( "Resource Bundles could not be found." );
        }
    }

    /**
     * test that the resource bundles can be found
     */
    @Test
    public void testResourceBundlesFound() {
        assertNotNull( "Testing English bundle found", jpoResources1 );
        assertNotNull( "Testing German bundle found", jpoResources2 );
        assertNotNull( "Testing SimplifiedChinese bundle found", jpoResources3 );
        assertNotNull( "Testing TraditionalChinese bundle found", jpoResources4 );
    }

    /**
     * Test that the English bundle can be found
     */
    @Test
    public void testResourceLocale1() {
        assertEquals( "Testing English bundle locale", Locale.ENGLISH, jpoResources1.getLocale() );
    }

    /**
     * test that the German bundle can be found
     */
    @Test
    public void testResourceLocale2() {
        assertEquals( "Testing German bundle locale", Locale.GERMAN, jpoResources2.getLocale() );
    }

    /**
     * test that the Simplified Chinese bundle can be found
     */
    @Test
    public void testResourceLocale3() {
        assertEquals( "Testing SimplifiedChinese bundle locale", Locale.SIMPLIFIED_CHINESE, jpoResources3.getLocale() );
    }

    /**
     * test that the Traditional Chinese bundle can be found
     */
    @Test
    public void testResourceLocale4() {
        assertEquals( "Testing TraditionalChinese bundle locale", Locale.TRADITIONAL_CHINESE, jpoResources4.getLocale() );
    }

    /**
     * Test retrieval from English bundle
     */
    @Test
    public void testRetrieveResourceLocale1() {
        assertEquals( "Testing English bundle locale", "New Collection", jpoResources1.getString( "FileNewJMenuItem" ) );
    }

    /**
     * Test retrieval from German bundle
     */
    @Test
    public void testRetrieveResourceLocale2() {
        assertEquals( "Testing German bundle locale", "Neue Sammlung", jpoResources2.getString( "FileNewJMenuItem" ) );
    }

    /**
     * test that the Simplified Chinese bundle can be found
     */
    @Test
    public void testRetrieveResourceLocale3() {
        assertEquals( "Testing Simplified Chinese bundle locale", "新建图片集", jpoResources3.getString( "FileNewJMenuItem" ) );
    }

    /**
     * test that the Traditional Chinese bundle can be found
     */
    @Test
    public void testRetrieveResourceLocale4() {
        assertEquals( "Testing Traditional Chinese bundle locale", "新建圖片集", jpoResources4.getString( "FileNewJMenuItem" ) );
    }

    /**
     * Test for change of locale
     */
    @Test
    public void testChangeLocale() {
        assertEquals( "Testing English bundle locale", "New Collection", jpoResources1.getString( "FileNewJMenuItem" ) );
        jpoResources1 = ResourceBundle.getBundle( "jpo.gui.JpoResources", Locale.GERMAN );
        assertEquals( "Testing getString from the changed bundle", "Neue Sammlung", jpoResources1.getString( "FileNewJMenuItem" ) );
        assertEquals( "Testing verifying that the changed bundle is now German", Locale.GERMAN, jpoResources1.getLocale() );
    }

}
