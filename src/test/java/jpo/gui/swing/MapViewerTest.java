/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jpo.gui.swing;

import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import static org.junit.Assert.*;
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
    public void testGetJXMapViewer() {
        try {
            SwingUtilities.invokeAndWait( () -> {
                MapViewer instance = new MapViewer();
                JXMapViewer result = instance.getJXMapViewer();
                assertNotNull( result );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( PicturePopupMenuTest.class.getName() ).log( Level.SEVERE, null, ex );
        }
    }

    /**
     * Test of setMarker method, of class MapViewer.
     */
    @Test
    public void testSetMarker() {
        try {
            SwingUtilities.invokeAndWait( () -> {
                Point2D.Double latLng = new Point2D.Double( 47.557306, 7.797439 );
                MapViewer mapViewer = new MapViewer();
                mapViewer.setMarker( latLng );
                GeoPosition mapPosition = mapViewer.getJXMapViewer().getCenterPosition();
                assertEquals( 47.557306, mapPosition.getLatitude(), 0.00000000001 );
                assertEquals( 7.797439, mapPosition.getLongitude(), 0.00000000001 );
            } );
        } catch ( InterruptedException | InvocationTargetException ex ) {
            Logger.getLogger( PicturePopupMenuTest.class.getName() ).log( Level.SEVERE, null, ex );
        }

    }

}
