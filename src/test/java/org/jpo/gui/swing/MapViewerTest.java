package org.jpo.gui.swing;

import org.junit.Test;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;


/*
 Copyright (C) 2017-2019,  Richard Eigenmann, Zürich
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
public class MapViewerTest {

    /**
     * Test of getJXMapViewer method, of class MapViewer.
     */
    @Test
    public void testGetJXMapViewer() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final MapViewer instance = new MapViewer();
                final JXMapViewer result = instance.getJXMapViewer();
                assertNotNull( result );
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( MapViewerTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail("Was not supposed to throw exception: " + ex.getMessage());
        }
    }

    /**
     * Test of setMarker method, of class MapViewer.
     */
    @Test
    public void testSetMarker() {
        assumeFalse( GraphicsEnvironment.isHeadless() );
        try {
            SwingUtilities.invokeAndWait( () -> {
                final Point2D.Double latLng = new Point2D.Double( 47.557306, 7.797439 );
                final MapViewer mapViewer = new MapViewer();
                mapViewer.setMarker( latLng );
                final GeoPosition mapPosition = mapViewer.getJXMapViewer().getCenterPosition();
                assertEquals( 47.557306, mapPosition.getLatitude(), 0.00000000001 );
                assertEquals( 7.797439, mapPosition.getLongitude(), 0.00000000001 );
            } );
        } catch ( final InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( MapViewerTest.class.getName() ).log( Level.SEVERE, null, ex );
            fail("Was not supposed to throw exception: " + ex.getMessage());
        }

    }

}
