package jpo.gui;

/*
 ThumbnailsPanelControllerTest.java:  Tests for the ThumbnailsPanelController

 Copyright (C) 2014  Richard Eigenmann.
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


import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.SwingUtilities;
import jpo.dataModel.Tools;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Richard Eigenmann
 */
public class ThumbnailsPanelControllerTest  {

    /**
     * Test of nodeLayoutChanged getMouseRectangle, of class ThumbnailsPanelController.
     */
    @Test
    public void testGetMouseRectangle() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                System.out.println( "Inside the testGetMouseRectangle Runnable." );
                System.out.println( "Fetching a ThumbnailsPanelController.." );
                ThumbnailsPanelController thumbnailsPanelController = new ThumbnailsPanelController();
                System.out.println( "got one" );
                try {
                    System.out.println( "fetching the mousePressedPoint" );
                    Field mousePressedPoint = thumbnailsPanelController.getClass().getDeclaredField( "mousePressedPoint" );
                    System.out.println( "got it. Now making it accessible" );
                    mousePressedPoint.setAccessible( true );
                    System.out.println( "done that. Now creating some coordinates" );
                    Point topLeft = new Point( 50, 200 );
                    System.out.println( "done that. Now setting the coordinates" );
                    mousePressedPoint.set( thumbnailsPanelController, topLeft );
                    System.out.println( "Checkpoint 1" );

                    Method getMouseRectangle = thumbnailsPanelController.getClass().getDeclaredMethod( "getMouseRectangle", Point.class );
                    getMouseRectangle.setAccessible( true );
                    Point bottomRight = new Point( 70, 230 );
                    Rectangle r1 = (Rectangle) getMouseRectangle.invoke( thumbnailsPanelController, bottomRight );
                    System.out.println( "Checkpoint 2" );

                    assertEquals( "Checking top left x", topLeft.x, r1.x );
                    assertEquals( "Checking top left y", topLeft.y, r1.y );
                    assertEquals( "Checking width", bottomRight.x - topLeft.x, r1.width );
                    assertEquals( "Checking height", bottomRight.y - topLeft.y, r1.height );
                    System.out.println( "Checkpoint 3" );

                    Point higherLeftPoint = new Point( 20, 30 );
                    Rectangle r2 = (Rectangle) getMouseRectangle.invoke( thumbnailsPanelController, higherLeftPoint );

                    assertEquals( "Checking top left x of r2", higherLeftPoint.x, r2.x );
                    assertEquals( "Checking top left y or r2", higherLeftPoint.y, r2.y );
                    assertEquals( "Checking width", topLeft.x - higherLeftPoint.x, r2.width );
                    assertEquals( "Checking height", topLeft.y - higherLeftPoint.y, r2.height );
                } catch ( NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex ) {
                    System.out.println( "We hit the catch" );
                    System.out.println( ex.getMessage() );
                    fail( ex.getMessage() );
                    
                }
            }
        };
        try {
            SwingUtilities.invokeAndWait( r );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            fail( ex.getMessage() );
        }

    }

}
