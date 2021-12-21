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

/**
 * A controller to manage a map winndow in the info panel. It listens to the eventBus and updates the map
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
    List<GeoPosition> track = Arrays.asList(northernPointOfEurope, falklandIslands, tasmania);
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
     * Handles the ShowGroupRequest and show the markers on the map
     *
     * @param event the ShowGroupRequest
     */
    @Subscribe
    public void handleShowGroupRequest(final ShowGroupRequest event) {
        LOGGER.log(Level.INFO, "handleShowGroupRequest received");
        final var waypoints = new HashSet<Waypoint>();
        final var geoPositions = new HashSet<GeoPosition>();
        for (var node : event.node().getChildPictureNodes(false)) {
            final var latLng = ((PictureInfo) node.getUserObject()).getLatLng();
            LOGGER.log(Level.INFO, "LatLang: {0}", latLng);
            // Create waypoints from the geo-positions
            if (latLng.x != 0.0 && latLng.y != 0.0) {
                final var geoPostion = new GeoPosition(latLng.x, latLng.y);
                waypoints.add(new DefaultWaypoint(geoPostion));
                geoPositions.add(geoPostion);
            }
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
                final var latLng = pictureInfo.getLatLng();
                LOGGER.log(Level.INFO, "LatLang: {0}", latLng);
                // Create waypoints from the geo-positions
                if (latLng.x != 0.0 && latLng.y != 0.0) {
                    final var geoPostion = new GeoPosition(latLng.x, latLng.y);
                    waypoints.add(new DefaultWaypoint(geoPostion));
                    geoPositions.add(geoPostion);
                }
            }
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

}
