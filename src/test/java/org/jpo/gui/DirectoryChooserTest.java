package org.jpo.gui;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2022  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */


/**
 * Tests for the Directory Chooser
 *
 * @author Richard Eigenmann
 */
public class DirectoryChooserTest {

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    private int changesReceived;
    private File result;

    /**
     * Test the listener
     */
    @Test
    void testListener() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final DirectoryChooser dc = new DirectoryChooser("Title", DirectoryChooser.DIR_MUST_EXIST);
                dc.addChangeListener((ChangeEvent e) -> changesReceived++);
                dc.setText("/");
                result = dc.getDirectory();
                // Checking that what went in is what comes out
                assertEquals(new File("/"), result);
                // Checking that the changeEvent was fired
                assertEquals( 1, changesReceived );
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            fail("This was not supposed to land in the catch clause: " + ex.getMessage());
            Thread.currentThread().interrupt();
        }

    }
}
