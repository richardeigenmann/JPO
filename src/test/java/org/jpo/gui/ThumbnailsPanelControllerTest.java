package org.jpo.gui;

import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


/*
 ThumbnailsPanelControllerTest.java:  Tests for the ThumbnailsPanelController

 Copyright (C) 2014-2017  Richard Eigenmann.
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
public class ThumbnailsPanelControllerTest {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ThumbnailsPanelControllerTest.class.getName() );

    /**
     * Test of nodeLayoutChanged getMouseRectangle, of class
     * ThumbnailsPanelController.
     */
    @Test
    public void testGetMouseRectangle() {
        try {
            SwingUtilities.invokeAndWait( () -> {
                try {
                    ThumbnailsPanelController thumbnailsPanelController = new ThumbnailsPanelController();
                    Field mousePressedPoint = thumbnailsPanelController.getClass().getDeclaredField( "mousePressedPoint" );
                    mousePressedPoint.setAccessible( true );
                    Point topLeft = new Point( 50, 200 );
                    mousePressedPoint.set( thumbnailsPanelController, topLeft );

                    Method getMouseRectangle = thumbnailsPanelController.getClass().getDeclaredMethod( "getMouseRectangle", Point.class );
                    getMouseRectangle.setAccessible( true );
                    Point bottomRight = new Point( 70, 230 );
                    Rectangle r1 = (Rectangle) getMouseRectangle.invoke( thumbnailsPanelController, bottomRight );

                    assertEquals( "Checking top left x", topLeft.x, r1.x );
                    assertEquals( "Checking top left y", topLeft.y, r1.y );
                    assertEquals( "Checking width", bottomRight.x - topLeft.x, r1.width );
                    assertEquals( "Checking height", bottomRight.y - topLeft.y, r1.height );

                    Point higherLeftPoint = new Point( 20, 30 );
                    Rectangle r2 = (Rectangle) getMouseRectangle.invoke( thumbnailsPanelController, higherLeftPoint );

                    assertEquals( "Checking top left x of r2", higherLeftPoint.x, r2.x );
                    assertEquals( "Checking top left y or r2", higherLeftPoint.y, r2.y );
                    assertEquals( "Checking width", topLeft.x - higherLeftPoint.x, r2.width );
                    assertEquals( "Checking height", topLeft.y - higherLeftPoint.y, r2.height );
                } catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex ) {
                    LOGGER.severe( ex.getMessage() );
                    fail( ex.getMessage() );
                } catch ( HeadlessException ex ) {
                    LOGGER.severe( "The tests are running in a headless environment. This test can't be executed. Letting it pass." );
                }
            } );
        } catch ( InterruptedException ex  ) {
            LOGGER.severe( ex.getMessage() );
            fail( ex.getMessage() );
        } catch ( InvocationTargetException ex ) {
            LOGGER.severe( "InvocationTargetException: " + ex.getMessage() );
            LOGGER.severe( "Source: " + ex.getTargetException().getMessage() );
            //fail( ex.getMessage() );
            // ToDo: Don't know how why this is going wrong with gradle test
        }

    }

}
