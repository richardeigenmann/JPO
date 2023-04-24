package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.PictureInfo;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.ShowGroupRequest;
import org.jpo.eventbus.ShowQueryRequest;
import org.jpo.gui.swing.MapViewer;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2022  Richard Eigenmann.
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
 * A controller to manage a map window in the info panel. It listens to the eventBus and updates the map
 * and the Waypoints autonomously when it hears about a new Group or a new Query being selected.
 */
public class MapWindow {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(MapWindow.class.getName());

    private final GeoPosition northernPointOfEurope = new GeoPosition(71, 18, 55, 25, 67, 64);
    private final GeoPosition falklandIslands = new GeoPosition(-51, 79, -63, 59, 52, 36);
    private final GeoPosition tasmania = new GeoPosition(-42, 4, 9, +146, 80, 87);
    final List<GeoPosition> track = Arrays.asList(northernPointOfEurope, falklandIslands, tasmania);
    final MapViewer mapViewer = new MapViewer();
    final JPanel mapPanel = new JPanel();

    /**
     * Constructs the new MapWindow component.
     */
    public MapWindow() {
        mapPanel.setLayout(new BorderLayout());
        mapPanel.add(mapViewer.getJXMapViewer(), BorderLayout.CENTER);
        JpoEventBus.getInstance().register(this);
    }

    /**
     * Sets the map back to the default view.
     */
    public void setDefaultView() {
        mapViewer.getJXMapViewer().zoomToBestFit(new HashSet<>(track), 0.8);
    }

    /**
     * Returns the JComponent to attach to SWING components
     *
     * @return the JComponent showing the map
     */
    public JComponent getJComponent() {
        return mapPanel;
    }

    /**
     * Handles the ShowGroupRequest and shows the markers on the map
     *
     * @param event the ShowGroupRequest
     */
    @Subscribe
    public void handleShowGroupRequest(final ShowGroupRequest event) {
        LOGGER.log(Level.FINE, "handleShowGroupRequest received");
        final var waypoints = new HashSet<Waypoint>();
        final var geoPositions = new HashSet<GeoPosition>();
        for (var node : event.node().getChildPictureNodes(false)) {
            addWaypointForPicture(((PictureInfo) node.getUserObject()), waypoints, geoPositions);
        }

        final Runnable runnable = () -> {
            WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(waypoints);
            mapViewer.getJXMapViewer().setOverlayPainter(waypointPainter);
            if (geoPositions.size() > 1) {
                mapViewer.getJXMapViewer().zoomToBestFit(geoPositions, 0.7);
                mapViewer.getJXMapViewer().setCenterPosition(geoPositions.stream().findFirst().get());
            } else if (geoPositions.size() == 1) {
                mapViewer.getJXMapViewer().setCenterPosition(geoPositions.stream().findFirst().get());
                mapViewer.getJXMapViewer().setZoom(7);
            } else {
                setDefaultView();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }
    }

    private static void addWaypointForPicture(final PictureInfo node, final HashSet<Waypoint> waypoints, final HashSet<GeoPosition> geoPositions) {
        final var latLng = node.getLatLng();
        LOGGER.log(Level.FINE, "LatLang: {0}", latLng);
        // Create waypoints from the geo-positions
        if (latLng.x != 0.0 && latLng.y != 0.0) {
            final var geoPosition = new GeoPosition(latLng.x, latLng.y);
            waypoints.add(new DefaultWaypoint(geoPosition));
            geoPositions.add(geoPosition);
        }
    }

    /**
     * Handles the ShowQueryRequest by showing the query results
     *
     * @param event the ShowQueryRequest
     */
    @Subscribe
    public void handleShowQueryRequest(final ShowQueryRequest event) {
        LOGGER.log(Level.INFO, "handleShowQueryRequest received");
        final var waypoints = new HashSet<Waypoint>();
        final var geoPositions = new HashSet<GeoPosition>();
        for (var i = 0; i < event.query().getNumberOfResults(); i++) {
            if (event.query().getIndex(i).getUserObject() instanceof PictureInfo pictureInfo) {
                addWaypointForPicture(pictureInfo, waypoints, geoPositions);
            }
        }

        final Runnable runnable = () -> {
            final var waypointPainter = new WaypointPainter<>();
            waypointPainter.setWaypoints(waypoints);
            mapViewer.getJXMapViewer().setOverlayPainter(waypointPainter);
            if (geoPositions.size() > 1) {
                mapViewer.getJXMapViewer().zoomToBestFit(geoPositions, 0.7);
                mapViewer.getJXMapViewer().setCenterPosition(geoPositions.stream().findFirst().get());
            } else if (geoPositions.size() == 1) {
                mapViewer.getJXMapViewer().setCenterPosition(geoPositions.stream().findFirst().get());
                mapViewer.getJXMapViewer().setZoom(7);
            } else {
                setDefaultView();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }


    }

}
