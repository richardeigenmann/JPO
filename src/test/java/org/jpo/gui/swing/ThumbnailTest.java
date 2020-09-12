package org.jpo.gui.swing;


import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;


/*
 Copyright (C) 2019,  Richard Eigenmann, Zürich
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
 *
 * @author Richard Eigenmann
 */
public class ThumbnailTest {

    @BeforeAll
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    /**
     * Test constructor
     */
    @Test
    public void testImageInitialisation() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final Thumbnail thumbnail = new Thumbnail( );
                assertNotNull( thumbnail );

                assertNotNull( thumbnail.getSelectedIcon());
                assertNotNull( thumbnail.getMailIcon());
                assertNotNull( thumbnail.getOfflineIcon());
                assertNotNull( thumbnail.getLargeFolderIcon());
                assertNotNull( thumbnail.getQueueIcon());
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            fail("Failed to create a PictureFrame");
            Thread.currentThread().interrupt();
        }
    }

}
