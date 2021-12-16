package org.jpo.gui.swing;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.awt.Cursor.CROSSHAIR_CURSOR;

/*
Copyright (C) 2017-2021  Richard Eigenmann.
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
 * A controller for the Map Component which can be retrieved by @see getJXMapViewer
 * @author Richard Eigenmann
 */
public class MapViewer {

    /**
     * Use 8 threads in parallel to load the tiles
     */
    private static final int THREAD_POOL_SIZE = 8;

    /**
     * Constructs the controller which creates the Component and wires up the
     * mouse listeners to it.
     */
    public MapViewer() {
        // Create a TileFactoryInfo for OpenStreetMap
        final TileFactoryInfo info = new OSMTileFactoryInfo();
        final DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        jxMapViewer.setTileFactory(tileFactory);

        // Setup local file cache
        final File cacheDir = new File(System.getProperty("java.io.tmpdir") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));


        tileFactory.setThreadPoolSize(THREAD_POOL_SIZE);

        // Add interactions
        final MouseInputListener mouseInputListener = new PanMouseInputListener(jxMapViewer);
        jxMapViewer.addMouseListener( mouseInputListener );
        jxMapViewer.addMouseMotionListener( mouseInputListener );
        jxMapViewer.addMouseListener( new CenterMapListener( jxMapViewer ) );
        jxMapViewer.addMouseWheelListener( new ZoomMouseWheelListenerCursor( jxMapViewer ) );
        jxMapViewer.addKeyListener( new PanKeyListener( jxMapViewer ) );
        jxMapViewer.setCursor( new Cursor( CROSSHAIR_CURSOR ) );

        // Create waypoints from the geo-positions
        final Set<Waypoint> waypoints = new HashSet<>();
        waypoints.add( defaultWaypoint );

        // Create a waypoint painter that takes all the waypoints
        final WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
        waypointPainter.setWaypoints( waypoints );

        final List<Painter<JXMapViewer>> painters = new ArrayList<>();
        painters.add( waypointPainter );

        final CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painters);
        jxMapViewer.setOverlayPainter( painter );
    }

    private final JXMapViewer jxMapViewer = new JXMapViewer();
    private final DefaultWaypoint defaultWaypoint = new DefaultWaypoint();

    /**
     * Returns the Swing Component showing the map
     * @return the Swing Component showing the map
     */
    public JXMapViewer getJXMapViewer() {
        return jxMapViewer;
    }

    /**
     * Places a marker on the map at the X and Y latitude and longitude and moves the
     * map to show it in the middle.
     *
     * @param latLng the latitude as X and longitude as Y
     */
    public void setMarker(final Point2D.Double latLng) {
        final GeoPosition location = new GeoPosition(latLng.getX(), latLng.getY());
        defaultWaypoint.setPosition(location);
        jxMapViewer.setAddressLocation(location);
    }

}
