package org.jpo.gui;

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

    private ResourceBundle jpoResourcesEN;
    private ResourceBundle jpoResourcesDE;
    private ResourceBundle jpoResourcesCN_ZH;
    private ResourceBundle jpoResourcesCN_TW;

    /**
     * Set up for each test
     */
    @BeforeEach
    void setUp() {
        try {
            jpoResourcesEN = ResourceBundle.getBundle("org.jpo.gui.JpoResources", Locale.ENGLISH);
            jpoResourcesDE = ResourceBundle.getBundle("org.jpo.gui.JpoResources", Locale.GERMAN);
            jpoResourcesCN_ZH = ResourceBundle.getBundle("org.jpo.gui.JpoResources", Locale.SIMPLIFIED_CHINESE);
            jpoResourcesCN_TW = ResourceBundle.getBundle("org.jpo.gui.JpoResources", Locale.TRADITIONAL_CHINESE);

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
        assertNotNull(jpoResourcesEN);
        // Testing German bundle found
        assertNotNull(jpoResourcesDE);
        // Testing SimplifiedChinese bundle found
        assertNotNull(jpoResourcesCN_ZH);
        // Testing TraditionalChinese bundle found
        assertNotNull(jpoResourcesCN_TW);
    }

    /**
     * Test that the English bundle can be found
     */
    @Test
    void testResourceLocale1() {
        // Testing English bundle locale
        assertEquals(Locale.ENGLISH, jpoResourcesEN.getLocale());
    }

    /**
     * test that the German bundle can be found
     */
    @Test
    void testResourceLocale2() {
        // Testing German bundle locale
        assertEquals(Locale.GERMAN, jpoResourcesDE.getLocale());
    }

    /**
     * test that the Simplified Chinese bundle can be found
     */
    @Test
    void testResourceLocale3() {
        // Testing SimplifiedChinese bundle locale
        assertEquals(Locale.SIMPLIFIED_CHINESE, jpoResourcesCN_ZH.getLocale());
    }

    /**
     * test that the Traditional Chinese bundle can be found
     */
    @Test
    void testResourceLocale4() {
        // Testing TraditionalChinese bundle locale
        assertEquals(Locale.TRADITIONAL_CHINESE, jpoResourcesCN_TW.getLocale());
    }

    /**
     * Test retrieval from English bundle
     */
    @Test
    void testRetrieveResourceLocale1() {
        // Testing English bundle locale
        assertEquals("New Collection", jpoResourcesEN.getString("FileNewJMenuItem"));
    }

    /**
     * Test retrieval from German bundle
     */
    @Test
    void testRetrieveResourceLocale2() {
        // Testing German bundle locale
        assertEquals("Neue Sammlung", jpoResourcesDE.getString("FileNewJMenuItem"));
    }

    /**
     * test that the Simplified Chinese bundle can be found
     */
    @Test
    void testRetrieveResourceLocale3() {
        // Testing Simplified Chinese bundle locale
        assertEquals("新建图片集", jpoResourcesCN_ZH.getString("FileNewJMenuItem"));
    }

    /**
     * test that the Traditional Chinese bundle can be found
     */
    @Test
    void testRetrieveResourceLocale4() {
        // Testing Traditional Chinese bundle locale
        assertEquals("新建圖片集", jpoResourcesCN_TW.getString("FileNewJMenuItem"));
    }

    /**
     * Test for change of locale
     */
    @Test
    void testChangeLocale() {
        assertEquals("New Collection", jpoResourcesEN.getString("FileNewJMenuItem"));
        jpoResourcesEN = ResourceBundle.getBundle("org.jpo.gui.JpoResources", Locale.GERMAN);
        assertEquals("Neue Sammlung", jpoResourcesEN.getString("FileNewJMenuItem"));
        // Testing verifying that the changed bundle is now German
        assertEquals(Locale.GERMAN, jpoResourcesEN.getLocale());
    }

}
