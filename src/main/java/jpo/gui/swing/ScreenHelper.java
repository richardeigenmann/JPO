package jpo.gui.swing;

import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;


/*
ScreenHelper.java:  class that helps with screen size logic

Copyright (C) 2006-2010  Richard Eigenmann, Zurich, Switzerland
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
 *  This class helps with screen size logic.
 *
 **/
public class ScreenHelper {

    /**
     *   This method returns the number of screen devices.
     *
     *  @return   The number of screen devices
     */
    public static int getNumberOfScreenDevices() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        return gs.length;
    }


    /**
     *   This method returns whether the environment is a Xinerama environment
     *
     *  @return   True if the environment is a Xinerama environment, False if not
     */
    public static boolean isXinerama() {
        if ( getNumberOfScreenDevices() < 2 ) {
            return false;
        }
        Rectangle r;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        GraphicsDevice gd;
        GraphicsConfiguration gc;
        for ( GraphicsDevice g : gs ) {
            gd = g;
            gc = gd.getDefaultConfiguration();
            r = gc.getBounds();
            if ( ( r.x != 0 ) || ( r.y != 0 ) ) {
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
        Rectangle virtualBounds = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for ( GraphicsDevice gd : gs ) {
            GraphicsConfiguration[] gc = gd.getConfigurations();
            for ( GraphicsConfiguration gc1 : gc ) {
                virtualBounds = virtualBounds.union( gc1.getBounds() );
            }
        }
        return virtualBounds;
    }


    /**
     *  Returns the GraphicsConfiguration of the primary screen
     * @return Graphics Configuration
     */
    public static GraphicsConfiguration getPrimaryScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }


    /**
     *  Returns the graphics Configuration of the secondary screen
     * @return the Graphics Configuration
     */
    public static GraphicsConfiguration getSecondaryScreenGraphicsConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        if ( gd.length > 1 ) {
            return gd[1].getDefaultConfiguration();
        } else {
            return gd[0].getDefaultConfiguration();
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
     *  If there is a secondary device we will use this. Otherwise the primary.
     * @return bounds for the full screen
     */
    public static Rectangle getFullScreenBounds() {
        if ( isXinerama() ) {
            return getXineramaScreenBounds();
        } else if ( getNumberOfScreenDevices() > 1 ) {
            return getSecondaryScreenBounds();
        }
        return getPrimaryScreen().getBounds();
    }


    /**
     *  Returns the bounds for the "left" window. In a Xinerama context this is the screen
     *  device with the lowest x coordinates. In a non Xinerama context this is the primary
     *  screen.
     * @return bounds for the left window
     */
    public static Rectangle getLeftScreenBounds() {
        if ( isXinerama() ) {
            Rectangle xsb = getXineramaScreenBounds();
            return new Rectangle( xsb.x, xsb.y, xsb.width / getNumberOfScreenDevices(), xsb.height );
        } else {
            if ( getNumberOfScreenDevices() > 1 ) {
                return getPrimaryScreen().getBounds();
            } else {
                Rectangle bounds = getPrimaryScreen().getBounds();
                return new Rectangle( bounds.x, bounds.y, ( bounds.width / 2 ), bounds.height );
            }
        }
    }


    /**
     *  Returns the bounds for the "right" window. In a Xinerama context this is the screen
     *  we assume that the screens are same sized so we subtract a screen width from the right edge and
     *  hope this is the correct location In a non Xinerama context this is the secondary
     *  screen.
     * @return bounds of the right screen
     */
    public static Rectangle getRightScreenBounds() {
        if ( isXinerama() ) {
            Rectangle xsb = getXineramaScreenBounds();
            return new Rectangle( xsb.x + ( xsb.width / getNumberOfScreenDevices() * ( getNumberOfScreenDevices() - 1 ) ),
                    xsb.y, xsb.width / getNumberOfScreenDevices(), xsb.height );
        } else {
            if ( getNumberOfScreenDevices() > 1 ) {
                return getSecondaryScreenGraphicsConfiguration().getBounds();
            } else {
                Rectangle bounds = getPrimaryScreen().getBounds();
                return new Rectangle( bounds.x + ( bounds.width / 2 ), bounds.y, ( bounds.width / 2 ), bounds.height );
            }
        }
    }


    /**
     *  Returns the bounds for the "top left" window.
     * @return the bounds for the top left window
     */
    public static Rectangle getTopLeftScreenBounds() {
        Rectangle bounds = getLeftScreenBounds();
        bounds.height = ( bounds.height / 2 );
        return bounds;
    }


    /**
     *  Returns the bounds for the "bottom left" window.
     * @return the bounds for the bottom left window
     */
    public static Rectangle getBottomLeftScreenBounds() {
        Rectangle bounds = getLeftScreenBounds();
        bounds.height = ( bounds.height / 2 );
        bounds.y += bounds.height;
        return bounds;
    }


    /**
     *  Returns the bounds for the "top right" window.
     * @return the bounds for the top right window
     */
    public static Rectangle getTopRightScreenBounds() {
        Rectangle bounds = getRightScreenBounds();
        bounds.height = ( bounds.height / 2 );
        return bounds;
    }


    /**
     *  Returns the bounds for the "bottom right" window.
     * @return the bounds for the bottom right window
     */
    public static Rectangle getBottomRightScreenBounds() {
        Rectangle bounds = getRightScreenBounds();
        bounds.height = ( bounds.height / 2 );
        bounds.y += bounds.height;
        return bounds;
    }

    /**
     * helper variable
     */
    private static StringBuffer b;

    /**
     * concatenates strings with a system dependent newline.
     * @param s String
     */
    private static void sbadd( String s ) {
        b.append( s ).append( System.getProperty( "line.separator" ));
    }


    /**
     *  Explains the graphics configuration to the log file if debug is on:
     * @return A description of the graphics configuration
     */
    public static StringBuffer explainGraphicsEnvironment() {
        b = new StringBuffer();
        boolean headless = GraphicsEnvironment.isHeadless();
        if ( headless ) {
            sbadd( "HEADLESS: The GraphicsEnvironment is reporting no Display, Keyboard and Mound can be supported in this environment" );
        } else {
            sbadd( "HEADLESS: The GraphicsEnvironment is reporting it is not headless. How reassuring." );
        }

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        sbadd( "                  The LocalGraphicsEnvironment is reporting " +
                ( ( ge.isHeadlessInstance() ) ? "no Display, Keyboard and Mound can be supported in this environment"
                : "it is not headless. How reassuring." ) );


        if ( isXinerama() ) {
            sbadd( "XINERAMA: The environment is a Xinerama environment" );
        } else {
            sbadd( "XINERAMA: The environment is not a Xinerama environment" );
        }

        GraphicsDevice[] gd = ge.getScreenDevices();
        sbadd( "There are " + gd.length + " Screen Devices: " );


        sbadd( "The Graphics Environment Maximum Window Bounds are: " + ge.getMaximumWindowBounds().toString() );
        sbadd( "The Graphics Environment Center Point is at : " + ge.getCenterPoint().toString() );

        sbadd( "The left window bounds are: " + getLeftScreenBounds().toString() );
        sbadd( "The top left window bounds are: " + getTopLeftScreenBounds().toString() );
        sbadd( "The bottom left window bounds are: " + getBottomLeftScreenBounds().toString() );
        sbadd( "The right window bounds are: " + getRightScreenBounds().toString() );
        sbadd( "The top right window bounds are: " + getTopRightScreenBounds().toString() );
        sbadd( "The bottom right window bounds are: " + getBottomRightScreenBounds().toString() );


        sbadd( "The Default Screen Device configuration:" );
        GraphicsDevice defaultScreenDevice = ge.getDefaultScreenDevice();
        explainGraphicsDevice( defaultScreenDevice );


        for ( GraphicsDevice device : gd ) {
            explainGraphicsDevice( device );
        }

        return b;
    }


    /**
     *  Explains the graphics device to the log file
     * @param d GraphicsDevice
     */
    public static void explainGraphicsDevice( GraphicsDevice d ) {
        String id = d.getIDstring();
        sbadd( "================== Device: " + id );
        if ( d.isFullScreenSupported() ) {
            sbadd( "FullScreenexclusive mode is supported" );
        } else {
            sbadd( "FullScreenexclusive mode is not supported" );
        }
        if ( d.isDisplayChangeSupported() ) {
            sbadd( "DisplayChangeSupported is supported" );
        } else {
            sbadd( "DisplayChangeSupported is not supported" );
        }
        sbadd( "AvailableAcceleratedMemory: " + d.getAvailableAcceleratedMemory());
        explainDisplayMode( id, d.getDisplayMode() );

        DisplayMode[] dm = d.getDisplayModes();
        for ( int i = 0; i < dm.length; i++ ) {
            explainDisplayMode( id + " possibleMode [" + i + "]", dm[i] );
        }


        sbadd( "**Default Graphics Configuration:" );
        explainGraphicsConfiguration( id, d.getDefaultConfiguration() );
        sbadd( "**Available GraphicsConfigurations:" );
        GraphicsConfiguration[] gc = d.getConfigurations();
        for ( int i = 0; i < gc.length; i++ ) {
            explainGraphicsConfiguration( id + " Available Configuration [" + i + "] ", gc[i] );
        }
    }


    /**
     *  Explains the display mode to the log file
     * @param id Id
     * @param dm DisplayMode
     */
    public static void explainDisplayMode( String id, DisplayMode dm ) {
        sbadd( "DisplayMode for Device: " + id + " Bit Depth: " + dm.getBitDepth() + " RefreshRate: " + dm.getRefreshRate() + " Width: " + dm.getWidth() + " Height: " + dm.getHeight());
    }


    /**
     *  Explains the graphics configuration to the log file
     * @param id Id
     * @param gc GraphicsConfiguration
     */
    public static void explainGraphicsConfiguration( String id,
            GraphicsConfiguration gc ) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        sbadd( "Graphicsconfiguration for Device: " + id + " Bounds: " + gc.getBounds().toString() + " Insets: " + toolkit.getScreenInsets( gc ).toString() );
    }
}
