package jpo.dataModel;

import junit.framework.TestCase;

/*
 SizeCalculatorTest.java:  tests for the SizeCalculator
 *
 Copyright (C) 2002-2014  Richard Eigenmann.
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
 * Tests for the SizeCalculator
 *
 * @author Richard Eigenmann
 */
public class SizeCalculatorTest extends TestCase {

    /**
     * Constructor for the SizeCalculator tests
     *
     * @param testName test name
     */
    public SizeCalculatorTest( String testName ) {
        super( testName );
    }

    /**
     * test for the scaling up of a zoom
     */
    public void testScaleUp() {
        SizeCalculator sc = new SizeCalculator( 100, 100, 200, 200 );
        double two = 2;
        assertEquals( "Expecting a scale factor of 2", two, sc.scaleFactor );
        assertEquals( "Expecting scaled width of 200", 200, sc.getScaledSize().width );
        assertEquals( "Expecting scaled height of 200", 200, sc.getScaledSize().height );
    }

    /**
     * test the scale down of a zoom
     */
    public void testScaleDown() {
        SizeCalculator sc = new SizeCalculator( 200, 200, 100, 100 );
        double half = 0.5;
        assertEquals( "Expecting a scale factor of 0.5", half, sc.scaleFactor );
        assertEquals( "Expecting scaled width of 100", 100, sc.getScaledSize().width );
        assertEquals( "Expecting scaled height of 100", 100, sc.getScaledSize().height );
    }

    /**
     * Test a scale where the bounds are horizontal
     */
    public void testScaleHorizontally() {
        SizeCalculator sc = new SizeCalculator( 200, 100, 400, 400 );
        double two = 2;
        assertEquals( "Expecting a scale factor of 2", two, sc.scaleFactor );
        assertEquals( "Expecting scaled width of 400", 400, sc.getScaledSize().width );
        assertEquals( "Expecting scaled height of 200", 200, sc.getScaledSize().height );
    }

    /**
     * Test a scale where the bounds are vertical
     */
    public void testScaleVertically() {
        SizeCalculator sc = new SizeCalculator( 100, 200, 400, 400 );
        double two = 2;
        assertEquals( "Expecting a scale factor of 2", two, sc.scaleFactor );
        assertEquals( "Expecting scaled width of 200", 200, sc.getScaledSize().width );
        assertEquals( "Expecting scaled height of 400", 400, sc.getScaledSize().height );
    }
}
