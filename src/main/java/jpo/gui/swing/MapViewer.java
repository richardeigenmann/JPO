package jpo.gui.swing;

import java.awt.Cursor;
import static java.awt.Cursor.CROSSHAIR_CURSOR;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.event.MouseInputListener;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.LocalResponseCache;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

/*
Copyright (C) 2017  Richard Eigenmann.
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
public class MapViewer {

    public MapViewer() {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory( info );
        jxMapViewer.setTileFactory( tileFactory );

        // Setup local file cache
        File cacheDir = new File( System.getProperty( "java.io.tmpdir" ) + File.separator + ".jxmapviewer2" );
        LocalResponseCache.installResponseCache( info.getBaseURL(), cacheDir, false );

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize( 8 );

        jxMapViewer.setZoom( 7 );

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener( jxMapViewer );
        jxMapViewer.addMouseListener( mia );
        jxMapViewer.addMouseMotionListener( mia );

        jxMapViewer.addMouseListener( new CenterMapListener( jxMapViewer ) );

        jxMapViewer.addMouseWheelListener( new ZoomMouseWheelListenerCursor( jxMapViewer ) );

        jxMapViewer.addKeyListener( new PanKeyListener( jxMapViewer ) );

        jxMapViewer.setCursor( new Cursor( CROSSHAIR_CURSOR ) );

        // Create waypoints from the geo-positions
        //Set<Waypoint> waypoints = new HashSet<Waypoint>( Arrays.asList( new DefaultWaypoint( location ) ) );
        Set<Waypoint> waypoints = new HashSet<>();
        waypoints.add( defaultWaypoint );

        // Create a waypoint painter that takes all the waypoints
        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints( waypoints );

        // Create a compound painter that uses both the route-painter and the waypoint-painter
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        //painters.add( routePainter );
        painters.add( waypointPainter );

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>( painters );
        jxMapViewer.setOverlayPainter( painter );

    }

    /**
     * Defines a LOGGER for this class
     */
    //private static final Logger LOGGER = Logger.getLogger( MapViewer.class.getName() );

    private final JXMapViewer jxMapViewer = new JXMapViewer();
    private final DefaultWaypoint defaultWaypoint = new DefaultWaypoint();

    public JXMapViewer getJXMapViewer() {
        return jxMapViewer;
    }

    public void setMarker( Point2D.Double latLng ) {
        // Set the focus
        GeoPosition location = new GeoPosition( latLng.getX(), latLng.getY() );
        defaultWaypoint.setPosition( location );
        jxMapViewer.setAddressLocation( location );

    }

}
