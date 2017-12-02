package jpo.dataModel;

import static junit.framework.TestCase.assertEquals;
import org.junit.Test;




/*
 * CategoryTest.java
 * JUnit based test
 *
 */
/**
 * Tests for the Category class
 *
 * @author Richard Eigenmann
 */
public class CategoryTest {

    /**
     * Test of getKey method, of class jpo.Category.
     */
    @Test
    public void testGetKey() {
        Category cat = new Category( 1, "Houses" );
        assertEquals( "1", cat.getKey().toString() );
    }

    /**
     * Test of setKey method, of class jpo.Category.
     */
    @Test
    public void testSetKey() {
        Category cat = new Category( 1, "Houses" );
        cat.setKey( 2 );
        assertEquals( "2", cat.getKey().toString() );
    }

    /**
     * Test of getValue method, of class jpo.Category.
     */
    @Test
    public void testGetValue() {
        Category cat = new Category( 1, "Houses" );
        assertEquals( "Houses", cat.getValue() );
    }

    /**
     * Test of setValueKey method, of class jpo.Category.
     */
    @Test
    public void testSetValue() {
        Category cat = new Category( 1, "Houses" );
        cat.setValue( "Landscapes" );
        assertEquals( "Landscapes", cat.getValue() );
    }

    /**
     * Test of toString method, of class jpo.Category following Jon Allen's bug
     * report in May 2007. The problem was that toString was returning the
     * Object internals instead of the value. It seems I lost a toString
     * override somewhere. To prevent this stupid thing happening ever again I
     * have added this test.
     */
    @Test
    public void testToString() {
        Category cat = new Category( 1, "Houses" );
        assertEquals( "Houses", cat.toString() );
    }

}
