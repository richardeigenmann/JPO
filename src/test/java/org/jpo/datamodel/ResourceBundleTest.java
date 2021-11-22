package org.jpo.datamodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the resource bundles
 *
 * @author Richard Eigenmann
 */
class ResourceBundleTest {

    private ResourceBundle jpoResources1;
    private ResourceBundle jpoResources2;
    private ResourceBundle jpoResources3;
    private ResourceBundle jpoResources4;

    /**
     * Set up for each test
     */
    @BeforeEach
    void setUp() {
        try {
            jpoResources1 = ResourceBundle.getBundle("org.jpo.gui.JpoResources", Locale.ENGLISH);
            jpoResources2 = ResourceBundle.getBundle("org.jpo.gui.JpoResources", Locale.GERMAN);
            jpoResources3 = ResourceBundle.getBundle("org.jpo.gui.JpoResources", Locale.SIMPLIFIED_CHINESE);
            jpoResources4 = ResourceBundle.getBundle("org.jpo.gui.JpoResources", Locale.TRADITIONAL_CHINESE);

        } catch (MissingResourceException x) {
            fail("Resource Bundles could not be found.");
        }
    }

    /**
     * test that the resource bundles can be found
     */
    @Test
    void testResourceBundlesFound() {
        // Testing English bundle found
        assertNotNull(jpoResources1);
        // Testing German bundle found
        assertNotNull(jpoResources2);
        // Testing SimplifiedChinese bundle found
        assertNotNull(jpoResources3);
        // Testing TraditionalChinese bundle found
        assertNotNull(jpoResources4);
    }

    /**
     * Test that the English bundle can be found
     */
    @Test
    void testResourceLocale1() {
        // Testing English bundle locale
        assertEquals(Locale.ENGLISH, jpoResources1.getLocale());
    }

    /**
     * test that the German bundle can be found
     */
    @Test
    void testResourceLocale2() {
        // Testing German bundle locale
        assertEquals(Locale.GERMAN, jpoResources2.getLocale());
    }

    /**
     * test that the Simplified Chinese bundle can be found
     */
    @Test
    void testResourceLocale3() {
        // Testing SimplifiedChinese bundle locale
        assertEquals(Locale.SIMPLIFIED_CHINESE, jpoResources3.getLocale());
    }

    /**
     * test that the Traditional Chinese bundle can be found
     */
    @Test
    void testResourceLocale4() {
        // Testing TraditionalChinese bundle locale
        assertEquals(Locale.TRADITIONAL_CHINESE, jpoResources4.getLocale());
    }

    /**
     * Test retrieval from English bundle
     */
    @Test
    void testRetrieveResourceLocale1() {
        // Testing English bundle locale
        assertEquals("New Collection", jpoResources1.getString("FileNewJMenuItem"));
    }

    /**
     * Test retrieval from German bundle
     */
    @Test
    void testRetrieveResourceLocale2() {
        // Testing German bundle locale
        assertEquals("Neue Sammlung", jpoResources2.getString("FileNewJMenuItem"));
    }

    /**
     * test that the Simplified Chinese bundle can be found
     */
    @Test
    void testRetrieveResourceLocale3() {
        // Testing Simplified Chinese bundle locale
        assertEquals("新建图片集", jpoResources3.getString("FileNewJMenuItem"));
    }

    /**
     * test that the Traditional Chinese bundle can be found
     */
    @Test
    void testRetrieveResourceLocale4() {
        // Testing Traditional Chinese bundle locale
        assertEquals("新建圖片集", jpoResources4.getString("FileNewJMenuItem"));
    }

    /**
     * Test for change of locale
     */
    @Test
    void testChangeLocale() {
        assertEquals("New Collection", jpoResources1.getString("FileNewJMenuItem"));
        jpoResources1 = ResourceBundle.getBundle("org.jpo.gui.JpoResources", Locale.GERMAN);
        assertEquals("Neue Sammlung", jpoResources1.getString("FileNewJMenuItem"));
        // Testing verifying that the changed bundle is now German
        assertEquals(Locale.GERMAN, jpoResources1.getLocale());
    }

}
