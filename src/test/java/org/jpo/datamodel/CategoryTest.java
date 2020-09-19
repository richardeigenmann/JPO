package org.jpo.datamodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the Category class
 *
 * @author Richard Eigenmann
 */
public class CategoryTest {

    /**
     * Test of getKey method, of class org.jpo.Category.
     */
    @Test
    public void testGetKey() {
        final Category cat = new Category(1, "Houses");
        assertEquals( "1", cat.getKey().toString() );
    }

    /**
     * Test of setKey method, of class org.jpo.Category.
     */
    @Test
    public void testSetKey() {
        final Category cat = new Category(1, "Houses");
        cat.setKey( 2 );
        assertEquals( "2", cat.getKey().toString() );
    }

    /**
     * Test of getValue method, of class org.jpo.Category.
     */
    @Test
    public void testGetValue() {
        final Category cat = new Category(1, "Houses");
        assertEquals( "Houses", cat.getValue() );
    }

    /**
     * Test of setValueKey method, of class org.jpo.Category.
     */
    @Test
    public void testSetValue() {
        final Category cat = new Category(1, "Houses");
        cat.setValue( "Landscapes" );
        assertEquals( "Landscapes", cat.getValue() );
    }

    /**
     * Test of toString method, of class org.jpo.Category following Jon Allen's bug
     * report in May 2007. The problem was that toString was returning the
     * Object internals instead of the value. It seems I lost a toString
     * override somewhere. To prevent this stupid thing happening ever again I
     * have added this test.
     */
    @Test
    public void testToString() {
        final Category cat = new Category(1, "Houses");
        assertEquals("Houses", cat.toString());
    }

    @Test
    public void testStatus() {
        final Category cat = new Category(1, "Houses");
        assertEquals(Category.UNDEFINED, cat.getStatus());
        cat.setStatus(Category.SELECTED);
        assertEquals(Category.SELECTED, cat.getStatus());
        cat.setStatus(Category.BOTH);
        assertEquals(Category.BOTH, cat.getStatus());
        cat.setStatus(Category.UN_SELECTED);
        assertEquals(Category.UN_SELECTED, cat.getStatus());
        cat.setStatus(Category.UNDEFINED);
        assertEquals(Category.UNDEFINED, cat.getStatus());
    }


}
