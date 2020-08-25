package org.jpo.datamodel;

import org.jpo.gui.swing.EdtViolationException;
import org.junit.Test;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Objects;

import static junit.framework.TestCase.*;

/*
 Copyright (C) 2017-2019  Richard Eigenmann.
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
 * Tests for the Tools class
 *
 * @author Richard Eigenmann
 */
public class ToolsTest {

    private boolean notOnEDT_ErrorThrown;

    /**
     * Constructor for the Tools Test class
     */
    @Test
    public void testCheckEDTnotOnEDT() {
        // if not on EDT must throw Error
        notOnEDT_ErrorThrown = false;
        Thread t = new Thread( () -> {
            try {
                Tools.checkEDT();
            } catch ( EdtViolationException ex ) {
                notOnEDT_ErrorThrown = true;
            }
        } );
        t.start();
        try {
            t.join();
            assertTrue( "When not on EDT must throw an error", notOnEDT_ErrorThrown );
        } catch ( InterruptedException ex ) {
            fail( "EDT violation not thrown" );
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Test that an error is thrown when we are on the EDT and call the checkEDT
     * method
     */
    @Test
    public void testCheckEDTOnEDT() {
        // if on EDT must not throw Error
        try {
            SwingUtilities.invokeAndWait( () -> {
                boolean onEDTErrorThrown;
                onEDTErrorThrown = false;
                try {
                    Tools.checkEDT();
                } catch ( EdtViolationException ex ) {
                    onEDTErrorThrown = true;
                }
                assertFalse("When on EDT must not throw an error", onEDTErrorThrown);
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( "Something went wrong with the EDT thread test" );
            Thread.currentThread().interrupt();
        }

    }



    /**
     * Test of parseDate
     */
    @Test
    public void testParseDateTime() {
        String d = "2017:01:28 12:26:04";
        String expected = "2017-01-28 12:26:04";
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String result = format.format( Objects.requireNonNull(Tools.parseDate(d)).getTime() );
        assertEquals( expected, result );
    }

    /**
     * Test of parseDate
     */
    @Test
    public void testParseDate() {
        String d = "2017:01:28";
        String expected = "2017-01-28 00:00:00";
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String result = format.format( Objects.requireNonNull(Tools.parseDate(d)).getTime() );
        assertEquals( expected, result );
    }

    /**
     * Test of parseDate
     */
    @Test
    public void testParseDateGerman() {
        String d = "15.01.2017";
        String expected = "2017-01-15 00:00:00";
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String result = format.format( Objects.requireNonNull(Tools.parseDate(d)).getTime() );
        assertEquals( expected, result );
    }

    /**
     * Test of parseDate
     */
    @Test
    public void testParseDateGermanMinutes() {
        String d = "15.01.2017 18:11";
        String expected = "2017-01-15 18:11:00";
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String result = format.format( Objects.requireNonNull(Tools.parseDate(d)).getTime() );
        assertEquals( expected, result );
    }

    /**
     * Test of parseDate
     */
    @Test
    public void testParseDateGermanSeconds() {
        String d = "15.01.2017 18:11:33";
        String expected = "2017-01-15 18:11:33";
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String result = format.format( Objects.requireNonNull(Tools.parseDate(d)).getTime() );
        assertEquals( expected, result );
    }

    /**
     * Test of parseDate
     */
    @Test
    public void testParseDateAmerican() {
        String d = "9/11/2001";
        String expected = "2001-09-11 00:00:00";
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String result = format.format( Objects.requireNonNull(Tools.parseDate(d)).getTime() );
        assertEquals( expected, result );
    }

    /**
     * Test of parseDate
     */
    @Test
    public void testParseDateAmericanTime() {
        String d = "9/11/2001 08:46";
        String expected = "2001-09-11 08:46:00";
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
        String result = format.format( Objects.requireNonNull(Tools.parseDate(d)).getTime() );
        assertEquals( expected, result );
    }




}
