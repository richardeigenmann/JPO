package jpo.dataModel;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import org.junit.Test;

/*
 Copyright (C) 2017  Richard Eigenmann.
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

    boolean notOnEDT_ErrorThrown;

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
            } catch ( Error ex ) {
                notOnEDT_ErrorThrown = true;
            }
        } );
        t.start();
        try {
            t.join();
            assertEquals( "When not on EDT must throw an error", true, notOnEDT_ErrorThrown );
        } catch ( InterruptedException ex ) {
            Logger.getLogger( ToolsTest.class.getName() ).log( Level.SEVERE, null, ex );
            assertTrue( "Something went wrong", false );
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
                } catch ( Error ex ) {
                    onEDTErrorThrown = true;
                }
                assertEquals( "When on EDT must not throw an error", false, onEDTErrorThrown );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( "Something went wrong");
        }

    }

    /**
     * Test of cleanupFilename method, of class HtmlDistiller.
     */
    @Test
    public void testCleanupFilename() {
        String filename = "directory\\file.xml";  // actually contains directoy\file.xml
        String wanted = "directory_file.xml";  // actually contains directoy\file.xml
        String got = Tools.cleanupFilename( filename );
        assertEquals( "A backslash could be made into an underscore", wanted, got );
    }

}
