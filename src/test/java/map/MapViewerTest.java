/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package map;

import com.google.gdata.model.gd.PoBox;
import java.awt.geom.Point2D;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

/**
 *
 * @author Richard Eigenmann
 */
public class MapViewerTest {
    
    public MapViewerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getJXMapViewer method, of class MapViewer.
     */
    @Test
    public void testGetJXMapViewer() {
        MapViewer instance = new MapViewer();
        JXMapViewer result = instance.getJXMapViewer();
        assertNotNull( result );
    }

    /**
     * Test of setMarker method, of class MapViewer.
     */
    @Test
    public void testSetMarker() {
        Point2D.Double latLng = new Point2D.Double(47.557306, 7.797439);
        MapViewer mapViewer = new MapViewer();
        mapViewer.setMarker( latLng );
        GeoPosition mapPosition = mapViewer.getJXMapViewer().getCenterPosition();
        assertEquals( 47.557306, mapPosition.getLatitude(), 0.00000000001 );
        assertEquals( 7.797439, mapPosition.getLongitude(), 0.00000000001 );
    }
    
}
