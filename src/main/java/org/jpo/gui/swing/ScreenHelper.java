package org.jpo.gui.swing;

import java.awt.*;


/*
Copyright (C) 2006-2023 Richard Eigenmann, Zurich, Switzerland
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
 * This class helps with screen size logic.
 **/
public class ScreenHelper {

    private ScreenHelper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     *   This method returns the number of screen devices.
     *
     *  @return The number of screen devices
     */
    public static int getNumberOfScreenDevices() {
        final var localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final var localGraphicsEnvironmentScreenDevices = localGraphicsEnvironment.getScreenDevices();
        return localGraphicsEnvironmentScreenDevices.length;
    }


    /**
     *   This method returns whether the environment is a Xinerama environment
     *
     *  @return   True if the environment is a Xinerama environment, False if not
     */
    public static boolean isXinerama() {
        if (getNumberOfScreenDevices() < 2) {
            return false;
        }
        final var localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final var localGraphicsEnvironmentScreenDevices = localGraphicsEnvironment.getScreenDevices();
        for (final var graphicsDevice : localGraphicsEnvironmentScreenDevices) {
            final var graphicsDeviceDefaultConfiguration = graphicsDevice.getDefaultConfiguration();
            final var bounds = graphicsDeviceDefaultConfiguration.getBounds();
            if ((bounds.x != 0) || (bounds.y != 0)) {
                return true;
            }
        }
        return false;
    }


    /**
     *  This method returns the bounds of the multihead desktop that a user may have.
     *  This method uses some code I got off the web to determine the rectangle of the
     *  screen size. This is more complicated than it appears on the face of it because
     *  some users have multiple screens making up a large virtual desktop so the top left corner
     *  is not necessarily (0,0).
     *
     *  @return   A rectangle with the coordinates of the desktop.
     */
    public static Rectangle getXineramaScreenBounds() {
        var virtualBounds = new Rectangle();
        final var localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final var localGraphicsEnvironmentScreenDevices = localGraphicsEnvironment.getScreenDevices();
        for (final var graphicsDevice : localGraphicsEnvironmentScreenDevices) {
            final var configurations = graphicsDevice.getConfigurations();
            for (final var graphicsConfiguration : configurations) {
                virtualBounds = virtualBounds.union(graphicsConfiguration.getBounds());
            }
        }
        return virtualBounds;
    }


    /**
     * Returns the bounds of the primary screen
     *
     * @return The bounds of the primary screen
     */
    public static Rectangle getPrimaryScreenBounds() {
        final var localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final var defaultScreenDevice = localGraphicsEnvironment.getDefaultScreenDevice();
        return defaultScreenDevice.getDefaultConfiguration().getBounds();
    }

    /**
     * Returns the graphics Configuration of the secondary screen
     *
     * @return the Graphics Configuration of the secondary screen
     */
    private static GraphicsConfiguration getSecondaryScreenGraphicsConfiguration() {
        final var localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final var localGraphicsEnvironmentScreenDevices = localGraphicsEnvironment.getScreenDevices();
        if (localGraphicsEnvironmentScreenDevices.length > 1) {
            return localGraphicsEnvironmentScreenDevices[1].getDefaultConfiguration();
        } else {
            return localGraphicsEnvironmentScreenDevices[0].getDefaultConfiguration();
        }
    }


    /**
     *  Returns the graphics Configuration of the secondary screen
     * @return GraphicsConfiguration
     */
    public static Rectangle getSecondaryScreenBounds() {
        if ( !isXinerama() ) {
            return getSecondaryScreenGraphicsConfiguration().getBounds();
        } else {
            return getRightScreenBounds();
        }
    }


    /**
     *  Returns the bounds for the full screen. In a Xinerama context this is the full Xinerama area.
     *  If there is a secondary device we will use this. Otherwise, the primary.
     * @return bounds for the full screen
     */
    public static Rectangle getFullScreenBounds() {
        if ( isXinerama() ) {
            return getXineramaScreenBounds();
        } else if ( getNumberOfScreenDevices() > 1 ) {
            return getSecondaryScreenBounds();
        }
        return getPrimaryScreenBounds();
    }


    /**
     *  Returns the bounds for the "left" window. In a Xinerama context this is the screen
     *  device with the lowest x coordinates. In a non Xinerama context this is the primary
     *  screen.
     * @return bounds for the left window
     */
    public static Rectangle getLeftScreenBounds() {
        if ( isXinerama() ) {
            final var xineramaScreenBounds = getXineramaScreenBounds();
            return new Rectangle( xineramaScreenBounds.x, xineramaScreenBounds.y, xineramaScreenBounds.width / getNumberOfScreenDevices(), xineramaScreenBounds.height );
        } else {
            if ( getNumberOfScreenDevices() > 1 ) {
                return getPrimaryScreenBounds();
            } else {
                final var bounds = getPrimaryScreenBounds();
                return new Rectangle( bounds.x, bounds.y, ( bounds.width / 2 ), bounds.height );
            }
        }
    }


    /**
     *  Returns the bounds for the "right" window. In a Xinerama context this is the screen
     *  we assume that the screens are same sized, so we subtract a screen width from the right edge and
     *  hope this is the correct location In a non Xinerama context this is the secondary
     *  screen.
     * @return bounds of the right screen
     */
    public static Rectangle getRightScreenBounds() {
        if ( isXinerama() ) {
            final var xineramaScreenBounds = getXineramaScreenBounds();
            return new Rectangle( xineramaScreenBounds.x + ( xineramaScreenBounds.width / getNumberOfScreenDevices() * ( getNumberOfScreenDevices() - 1 ) ),
                    xineramaScreenBounds.y, xineramaScreenBounds.width / getNumberOfScreenDevices(), xineramaScreenBounds.height );
        } else {
            if ( getNumberOfScreenDevices() > 1 ) {
                return getSecondaryScreenGraphicsConfiguration().getBounds();
            } else {
                final var bounds = getPrimaryScreenBounds();
                return new Rectangle( bounds.x + ( bounds.width / 2 ), bounds.y, ( bounds.width / 2 ), bounds.height );
            }
        }
    }


    /**
     *  Returns the bounds for the "top left" window.
     * @return the bounds for the top left window
     */
    public static Rectangle getTopLeftScreenBounds() {
        final var bounds = getLeftScreenBounds();
        bounds.height = ( bounds.height / 2 );
        return bounds;
    }


    /**
     *  Returns the bounds for the "bottom left" window.
     * @return the bounds for the bottom left window
     */
    public static Rectangle getBottomLeftScreenBounds() {
        final var bounds = getLeftScreenBounds();
        bounds.height = ( bounds.height / 2 );
        bounds.y += bounds.height;
        return bounds;
    }


    /**
     *  Returns the bounds for the "top right" window.
     * @return the bounds for the top right window
     */
    public static Rectangle getTopRightScreenBounds() {
        final var bounds = getRightScreenBounds();
        bounds.height = ( bounds.height / 2 );
        return bounds;
    }


    /**
     *  Returns the bounds for the "bottom right" window.
     * @return the bounds for the bottom right window
     */
    public static Rectangle getBottomRightScreenBounds() {
        final var bounds = getRightScreenBounds();
        bounds.height = ( bounds.height / 2 );
        bounds.y += bounds.height;
        return bounds;
    }

    /**
     * helper variable
     */
    private static StringBuilder stringBuilder;

    /**
     * concatenates strings with a system dependent newline.
     * @param s String
     */
    private static void sbadd(final String s) {
        stringBuilder.append(s).append(System.getProperty("line.separator"));
    }


    /**
     *  Explains the graphics configuration to the log file if debug is on:
     * @return A description of the graphics configuration
     */
    public static StringBuilder explainGraphicsEnvironment() {
        stringBuilder = new StringBuilder();
        if (GraphicsEnvironment.isHeadless()) {
            sbadd("HEADLESS: The GraphicsEnvironment is reporting no Display, Keyboard and Mound can be supported in this environment");
        } else {
            sbadd("HEADLESS: The GraphicsEnvironment is reporting it is not headless. How reassuring.");
        }

        final var localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        sbadd("                  The LocalGraphicsEnvironment is reporting " +
                ((localGraphicsEnvironment.isHeadlessInstance()) ? "no Display, Keyboard and Mound can be supported in this environment"
                        : "it is not headless. How reassuring."));


        if ( isXinerama() ) {
            sbadd( "XINERAMA: The environment is a Xinerama environment" );
        } else {
            sbadd( "XINERAMA: The environment is not a Xinerama environment" );
        }

        final var screenDevices = localGraphicsEnvironment.getScreenDevices();
        sbadd( "There are " + screenDevices.length + " Screen Devices: " );


        sbadd( "The Graphics Environment Maximum Window Bounds are: " + localGraphicsEnvironment.getMaximumWindowBounds().toString() );
        sbadd( "The Graphics Environment Center Point is at : " + localGraphicsEnvironment.getCenterPoint().toString() );

        sbadd( "The left window bounds are: " + getLeftScreenBounds().toString() );
        sbadd( "The top left window bounds are: " + getTopLeftScreenBounds());
        sbadd( "The bottom left window bounds are: " + getBottomLeftScreenBounds());
        sbadd( "The right window bounds are: " + getRightScreenBounds().toString() );
        sbadd( "The top right window bounds are: " + getTopRightScreenBounds());
        sbadd( "The bottom right window bounds are: " + getBottomRightScreenBounds());


        sbadd("The Default Screen Device configuration:");
        explainGraphicsDevice(localGraphicsEnvironment.getDefaultScreenDevice() );


        for (final GraphicsDevice device : screenDevices) {
            explainGraphicsDevice(device);
        }

        return stringBuilder;
    }


    /**
     * Explains the graphics device to the log file
     *
     * @param graphicsDevice GraphicsDevice
     */
    private static void explainGraphicsDevice(final GraphicsDevice graphicsDevice) {
        final var id = graphicsDevice.getIDstring();
        sbadd("================== Device: " + id);
        if (graphicsDevice.isFullScreenSupported()) {
            sbadd("FullScreenexclusive mode is supported");
        } else {
            sbadd("FullScreenexclusive mode is not supported");
        }
        if (graphicsDevice.isDisplayChangeSupported()) {
            sbadd("DisplayChangeSupported is supported");
        } else {
            sbadd("DisplayChangeSupported is not supported");
        }
        sbadd("AvailableAcceleratedMemory: " + graphicsDevice.getAvailableAcceleratedMemory());
        explainDisplayMode(id, graphicsDevice.getDisplayMode());

        final var dm = graphicsDevice.getDisplayModes();
        for (var i = 0; i < dm.length; i++) {
            explainDisplayMode(id + " possibleMode [" + i + "]", dm[i]);
        }


        sbadd("**Default Graphics Configuration:");
        explainGraphicsConfiguration(id, graphicsDevice.getDefaultConfiguration());
        sbadd("**Available GraphicsConfigurations:");
        GraphicsConfiguration[] gc = graphicsDevice.getConfigurations();
        for ( int i = 0; i < gc.length; i++ ) {
            explainGraphicsConfiguration( id + " Available Configuration [" + i + "] ", gc[i] );
        }
    }


    /**
     *  Explains the display mode to the log file
     * @param id Id
     * @param displayMode DisplayMode
     */
    public static void explainDisplayMode(final String id, final DisplayMode displayMode) {
        sbadd("DisplayMode for Device: " + id + " Bit Depth: " + displayMode.getBitDepth() + " RefreshRate: " + displayMode.getRefreshRate() + " Width: " + displayMode.getWidth() + " Height: " + displayMode.getHeight());
    }


    /**
     *  Explains the graphics configuration to the log file
     * @param id Id
     * @param graphicsConfiguration GraphicsConfiguration
     */
    public static void explainGraphicsConfiguration(final String id,
                                                    final GraphicsConfiguration graphicsConfiguration) {
        final var toolkit = Toolkit.getDefaultToolkit();
        sbadd("Graphicsconfiguration for Device: " + id + " Bounds: " + graphicsConfiguration.getBounds() + " Insets: " + toolkit.getScreenInsets(graphicsConfiguration));
    }
}
