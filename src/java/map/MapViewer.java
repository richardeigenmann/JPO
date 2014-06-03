package map;

import java.awt.Cursor;
import static java.awt.Cursor.CROSSHAIR_CURSOR;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.event.MouseInputListener;
import org.jdesktop.swingx.JXMapViewer;
import org.jdesktop.swingx.OSMTileFactoryInfo;
import org.jdesktop.swingx.input.CenterMapListener;
import org.jdesktop.swingx.input.PanKeyListener;
import org.jdesktop.swingx.input.PanMouseInputListener;
import org.jdesktop.swingx.input.ZoomMouseWheelListenerCursor;
import org.jdesktop.swingx.mapviewer.DefaultTileFactory;
import org.jdesktop.swingx.mapviewer.DefaultWaypoint;
import org.jdesktop.swingx.mapviewer.GeoPosition;
import org.jdesktop.swingx.mapviewer.LocalResponseCache;
import org.jdesktop.swingx.mapviewer.TileFactoryInfo;
import org.jdesktop.swingx.mapviewer.Waypoint;
import org.jdesktop.swingx.mapviewer.WaypointPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.Painter;

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
    private static final Logger LOGGER = Logger.getLogger( MapViewer.class.getName() );

    private final JXMapViewer jxMapViewer = new JXMapViewer();
    DefaultWaypoint defaultWaypoint = new DefaultWaypoint();

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
