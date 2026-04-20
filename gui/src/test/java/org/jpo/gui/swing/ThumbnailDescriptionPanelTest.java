package org.jpo.gui.swing;

import org.assertj.swing.edt.FailOnThreadViolationRepaintManager;
import org.jpo.gui.Settings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/*
 Copyright (C) 2025 Richard Eigenmann.
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

class ThumbnailDescriptionPanelTest {

    @BeforeAll
    static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }

    @Test
    void testConstructor() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionPanel panel = new ThumbnailDescriptionPanel();
                assertNotNull( panel );
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( ThumbnailDescriptionPanelTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail();
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSelected() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionPanel panel = new ThumbnailDescriptionPanel();
                assertNotNull( panel );

                panel.showAsSelected();
                assertEquals(Settings.getSelectedColorText(), panel.getPictureDescriptionJTA().getBackground());

                panel.showAsUnselected();
                assertEquals(Settings.getUnselectedColor(), panel.getPictureDescriptionJTA().getBackground());

                panel.showAsSelected(true);
                assertEquals(Settings.getSelectedColorText(), panel.getPictureDescriptionJTA().getBackground());

                panel.showAsSelected(false);
                assertEquals(Settings.getUnselectedColor(), panel.getPictureDescriptionJTA().getBackground());
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( ThumbnailDescriptionPanelTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail();
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testSetDescription() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final ThumbnailDescriptionPanel panel = new ThumbnailDescriptionPanel();
                assertNotNull( panel );

                final String TEST_TEXT_1 = "This is a test description";
                panel.setDescription(TEST_TEXT_1);
                assertEquals(TEST_TEXT_1,panel.getDescription());

                final String TEST_TEXT_2 = "This is a different description";
                panel.setDescription(TEST_TEXT_2);
                assertEquals(TEST_TEXT_2,panel.getDescription());
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( ThumbnailDescriptionPanelTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail();
            Thread.currentThread().interrupt();
        }
    }


}