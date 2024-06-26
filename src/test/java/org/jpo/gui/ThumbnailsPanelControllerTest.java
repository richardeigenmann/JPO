package org.jpo.gui;

import org.jpo.datamodel.Settings;
import org.jpo.gui.swing.ThumbnailPanelTitle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;


/*
 Copyright (C) 2014-2024 Richard Eigenmann.
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
 * ThumbnailsPanelControllerTest.java:  Tests for the ThumbnailsPanelController
 *
 * @author Richard Eigenmann
 */
class ThumbnailsPanelControllerTest {

    @BeforeAll
    public static void beforeAll() {
        Settings.loadSettings(); // We need to start the cache
    }

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ThumbnailsPanelControllerTest.class.getName() );

    @Test
    void testConstructor() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                final ThumbnailsPanelController thumbnailsPanelController = new ThumbnailsPanelController();
                assertNotNull(thumbnailsPanelController);
                assertInstanceOf(JScrollPane.class, thumbnailsPanelController.getView());
                assertInstanceOf(JLayeredPane.class, ((JScrollPane) thumbnailsPanelController.getView()).getViewport().getView());
                assertInstanceOf(ThumbnailPanelTitle.class, ((JScrollPane) thumbnailsPanelController.getView()).getColumnHeader().getView());
            });
        } catch (final InterruptedException | InvocationTargetException ex) {
            fail(ex.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    @Test
    void testGetMouseRectangle() {
        assumeFalse(GraphicsEnvironment.isHeadless());
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    final ThumbnailsPanelController thumbnailsPanelController = new ThumbnailsPanelController();
                    final Field mousePressedPoint = thumbnailsPanelController.getClass().getDeclaredField("mousePressedPoint");
                    mousePressedPoint.setAccessible(true);
                    final Point topLeft = new Point(50, 200);
                    mousePressedPoint.set(thumbnailsPanelController, topLeft );

                    final Method getMouseRectangle = thumbnailsPanelController.getClass().getDeclaredMethod( "getMouseRectangle", Point.class );
                    getMouseRectangle.setAccessible( true );
                    final Point bottomRight = new Point( 70, 230 );
                    Rectangle r1 = (Rectangle) getMouseRectangle.invoke( thumbnailsPanelController, bottomRight );

                    assertEquals( topLeft.x, r1.x );
                    assertEquals(  topLeft.y, r1.y );
                    assertEquals(  bottomRight.x - topLeft.x, r1.width );
                    assertEquals(  bottomRight.y - topLeft.y, r1.height );

                    final Point higherLeftPoint = new Point( 20, 30 );
                    final Rectangle r2 = (Rectangle) getMouseRectangle.invoke( thumbnailsPanelController, higherLeftPoint );

                    assertEquals(  higherLeftPoint.x, r2.x );
                    assertEquals(  higherLeftPoint.y, r2.y );
                    assertEquals(  topLeft.x - higherLeftPoint.x, r2.width );
                    assertEquals(  topLeft.y - higherLeftPoint.y, r2.height );
                } catch ( final NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex ) {
                    fail( ex.getMessage() );
                }
            } );
        } catch ( final InterruptedException ex  ) {
            fail( ex.getMessage() );
            Thread.currentThread().interrupt();
        } catch ( final InvocationTargetException ex ) {
            LOGGER.severe( "InvocationTargetException: " + ex.getMessage() );
            LOGGER.severe( "Source: " + ex.getTargetException().getMessage() );
            fail( ex.getMessage() );
        }

    }

}
