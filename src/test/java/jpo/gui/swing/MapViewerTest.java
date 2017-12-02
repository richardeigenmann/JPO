/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpo.gui.swing;

import java.awt.geom.Point2D;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

/**
 *
 * @author Richard Eigenmann
 */
public class MapViewerTest {

    /**
     * Test of getJXMapViewer method, of class MapViewer.
     */
    @Test
    @Ignore
    public void testGetJXMapViewer() {
        MapViewer instance = new MapViewer();
        JXMapViewer result = instance.getJXMapViewer();
        assertNotNull( result );
    }

    /**
     * Test of setMarker method, of class MapViewer.
     */
    @Test
    @Ignore
    public void testSetMarker() {
        Point2D.Double latLng = new Point2D.Double(47.557306, 7.797439);
        MapViewer mapViewer = new MapViewer();
        mapViewer.setMarker( latLng );
        GeoPosition mapPosition = mapViewer.getJXMapViewer().getCenterPosition();
        assertEquals( 47.557306, mapPosition.getLatitude(), 0.00000000001 );
        assertEquals( 7.797439, mapPosition.getLongitude(), 0.00000000001 );
    }
    
}
