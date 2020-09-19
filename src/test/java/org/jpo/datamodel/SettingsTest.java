package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Locale;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2017-2020  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 without even the implied warranty of MERCHANTABILITY or FITNESS 
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for 
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * Tests for the Settings class
 *
 * @author Richard Eigenmann
 */
public class SettingsTest {

    /**
     * As soon as a Settings Object exists there should always be a current
     * locale available.
     */
    @Test
    public void testCurrentLocale() {
        // Testing that current locale exists
        assertNotNull( Settings.getCurrentLocale());
    }

    /**
     * Tests setting the locale
     */
    @Test
    public void testSetLocale() {
        Settings.setLocale(Locale.GERMAN);
        // Testing the locale change to German
        assertEquals( Locale.GERMAN, Settings.getCurrentLocale());
        Settings.setLocale(Locale.SIMPLIFIED_CHINESE);
        // Testing the locale change to Simplified Chinese
        assertEquals( Locale.SIMPLIFIED_CHINESE, Settings.getCurrentLocale());
    }

    /**
     * test the switching of resource bundles
     */
    @Test
    public void testSetLocaleResourceBundleEffect() {
        Settings.setLocale(Locale.GERMAN);
        // Testing the ResourceBundle change to German
        assertEquals(Locale.GERMAN, Settings.getJpoResources().getLocale());
        Settings.setLocale(Locale.SIMPLIFIED_CHINESE);
        // Testing the ResourceBundle change to Simplified Chinese
        assertEquals(Locale.SIMPLIFIED_CHINESE, Settings.getJpoResources().getLocale());
    }

    /**
     * test that different languages are returned after switching the locale
     */
    @Test
    public void testSetLocaleResourceBundleStrings() {
        Settings.setLocale(Locale.GERMAN);
        // Testing the German string
        assertEquals("Neue Sammlung", Settings.getJpoResources().getString("FileNewJMenuItem"));
        Settings.setLocale(Locale.SIMPLIFIED_CHINESE);
        // Testing the Simplified Chinese string
        assertEquals("新建图片集", Settings.getJpoResources().getString("FileNewJMenuItem"));
    }

    /**
     * Test the saving and reading back of the settings and whether the locale
     * gets messed up along the way
     */
    @Test
    public void testReadWriteSettingsLocale() {
        // load the settings first or we have uninitialised objects which crash the writing
        Settings.loadSettings();
        // memorise the locale so that we can write it back in the end
        Locale saveLocale = Settings.getCurrentLocale();

        // make sure we change the locale to a defined starting point
        // Setting Locale to English and writing settings");
        Settings.setLocale(Locale.ENGLISH);
        Settings.writeSettings();
        // Locale should not have changed after writing settings!
        assertEquals( Locale.ENGLISH, Settings.getCurrentLocale());
        // now changing the Locale to German and loading the settings");
        Settings.setLocale(Locale.GERMAN);
        Settings.loadSettings();
        // Locale should be back to English after the load!
        assertEquals( Locale.ENGLISH, Settings.getCurrentLocale());

        // and do it all again to be sure that the saved Settings aren't tricking us here:
        Settings.setLocale(Locale.GERMAN);
        Settings.writeSettings();
        // Locale should not have changed after writing settings #2
        assertEquals( Locale.GERMAN, Settings.getCurrentLocale());
        Settings.setLocale(Locale.SIMPLIFIED_CHINESE);
        Settings.loadSettings();
        // Locale should be back to German after the second load!
        assertEquals( Locale.GERMAN, Settings.getCurrentLocale());

        // write the original settings back to prevent developer frustration
        Settings.setLocale(saveLocale);
        Settings.writeSettings();
    }

    /**
     * Test the saving and reading back of the maxThumbnails setting
     */
    @Test
    public void testReadWriteMaxThumbnails() {
        int saveMaxThumbnails = Settings.getMaxThumbnails();
        Settings.setMaxThumbnails(-1); //a value that it never should have
        Settings.loadSettings();
        // After loading the settings the maxThumbnails should not be -1 any more!
        assertTrue( (-1 != Settings.getMaxThumbnails()));

        Settings.setMaxThumbnails(-2); //another value that it never should never have
        Settings.writeSettings();
        // After saving the settings the maxThumbnails should still be -2
        assertEquals( -2, Settings.getMaxThumbnails());

        Settings.loadSettings();
        // After loading the negative value should have been replaced with" + Settings.DEFAULT_MAX_THUMBNAILS
        assertEquals(Settings.DEFAULT_MAX_THUMBNAILS, Settings.getMaxThumbnails());

        Settings.setMaxThumbnails(53);
        Settings.writeSettings();
        Settings.setMaxThumbnails(54);
        Settings.loadSettings();
        // After reloading the settings the maxThumbnails should be back at 53 not 54
        assertEquals( 53, Settings.getMaxThumbnails());

        // write the original settings back to prevent developer frustration
        Settings.setMaxThumbnails(saveMaxThumbnails);
        Settings.writeSettings();
    }


    @Test
    public void testMemorizeGroupOfDropLocation() {
        Settings.getRecentDropNodes().clear();
        final SortableDefaultMutableTreeNode n = new SortableDefaultMutableTreeNode();
        assertEquals(0, Settings.getRecentDropNodes().size());
        Settings.memorizeGroupOfDropLocation(n);
        assertEquals(1, Settings.getRecentDropNodes().size());
    }

    @Test
    public void testMemorizeGroupOfDropLocationPushDown() {
        Settings.getRecentDropNodes().clear();
        final SortableDefaultMutableTreeNode n1 = new SortableDefaultMutableTreeNode();
        assertEquals(0, Settings.getRecentDropNodes().size());
        Settings.memorizeGroupOfDropLocation(n1);
        // First Element should now be our new node
        assertEquals( n1, Settings.getRecentDropNodes().element());

        final SortableDefaultMutableTreeNode n2 = new SortableDefaultMutableTreeNode();
        Settings.memorizeGroupOfDropLocation(n2);
        // First Element should now be our new node n2
        assertEquals(n1, Settings.getRecentDropNodes().poll());
        // Second Element should now be our new node n1
        assertEquals(n2, Settings.getRecentDropNodes().element());
    }

    @Test
    public void testMemorizeGroupOfDropLocationOverfill() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SortableDefaultMutableTreeNode n1 = new SortableDefaultMutableTreeNode(new GroupInfo("N1"));
        // First Element should not be our new node
        assertFalse(Settings.getRecentDropNodes().contains(n1));
        Settings.memorizeGroupOfDropLocation(n1);
        // First Element should now be our new node
        assertEquals(n1, Settings.getRecentDropNodes().element());

        range(1, Settings.getMaxDropnodes()).forEach(
                itr -> Settings.memorizeGroupOfDropLocation(new SortableDefaultMutableTreeNode(new GroupInfo("Any other node"))));
        // The n1 node should still be on the queue
        assertTrue( Settings.getRecentDropNodes().contains(n1));

        Settings.memorizeGroupOfDropLocation(new SortableDefaultMutableTreeNode(new GroupInfo("One more node")));
        assertFalse(Settings.getRecentDropNodes().contains(n1));
    }

    @Test
    public void testRemoveRecentDropNode() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        Settings.getRecentDropNodes().clear();
        final SortableDefaultMutableTreeNode n1 = new SortableDefaultMutableTreeNode(new GroupInfo("N1"));
        // First Element should not be our new node
        assertFalse( Settings.getRecentDropNodes().contains(n1));
        Settings.memorizeGroupOfDropLocation(n1);
        // First Element should now be our new node
        assertEquals( n1, Settings.getRecentDropNodes().element());
        Settings.getRecentDropNodes().remove(n1);
        // First Element should not be our new node
        assertFalse( Settings.getRecentDropNodes().contains(n1));
    }

    @Test
    public void testRemoveRecentDropNodeAndCompress() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        Settings.getRecentDropNodes().clear();
        final SortableDefaultMutableTreeNode n1 = new SortableDefaultMutableTreeNode(new GroupInfo("N1"));
        Settings.memorizeGroupOfDropLocation(n1);
        Settings.getRecentDropNodes().remove(n1);
        final SortableDefaultMutableTreeNode n2 = new SortableDefaultMutableTreeNode(new GroupInfo("N2"));
        Settings.memorizeGroupOfDropLocation(n2);
        // First Element should be our new node
        assertEquals( n2, Settings.getRecentDropNodes().element());
    }

    @Test
    public void testClearRecentDropNodes() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        final SortableDefaultMutableTreeNode n1 = new SortableDefaultMutableTreeNode(new GroupInfo("N1"));
        Settings.memorizeGroupOfDropLocation(n1);
        // First Element should be our new node
        assertTrue(Settings.getRecentDropNodes().contains(n1));
        Settings.getRecentDropNodes().clear();
        // First Element should no longer be our node
        assertFalse( Settings.getRecentDropNodes().contains(n1));
    }

}
