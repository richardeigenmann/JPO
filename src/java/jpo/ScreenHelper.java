package jpo;

import java.awt.*;

/*
ScreenHelper.java:  class that helps with screen size logic
 
Copyright (C) 2006-2007  Richard Eigenmann, Zurich, Switzerland
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
 *  This class helps with screen size logicese settings.
 *
 **/
public class ScreenHelper {
    
    
    /**
     *   This method returns the bounds of the multihead desktop that a user may have.
     *  This method uses some code I got off the web to dtermine the rectangle of the
     *  screen size. This is more complicated than it appears on the face of it because
     *  some users have multiple screens making up a large virtual desktop so the top left corner
     *  is not neccesarily (0,0).
     *
     *  @return   A rectangle with the coordinates of the desktop.
     */
    public static Rectangle getXineramaScreenBounds() {
        Rectangle virtualBounds = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs =  ge.getScreenDevices();
        for (int j = 0; j < gs.length; j++) {
            GraphicsDevice gd = gs[j];
            GraphicsConfiguration[] gc = gd.getConfigurations();
            for (int i=0; i < gc.length; i++) {
                virtualBounds = virtualBounds.union(gc[i].getBounds());
            }
        }
        return virtualBounds;
    }
    
    
    
    /**
     *  Returns the GraphicsConfiguration of the primary screen
     */
    public static GraphicsConfiguration getPrimaryScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd =  ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }
    
    
    
    /**
     *  Returns the graphics Configuration of the secondary screen
     */
    public static GraphicsConfiguration getSecondaryScreenGraphicsConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        int i = 0;
        if ( gd.length > 1 ) {
            i = 1;
        }
        return gd[i].getDefaultConfiguration();
    }
    
    
    /**
     *  Returns the graphics Configuration of the secondary screen
     */
    public static Rectangle getSecondaryScreenBounds() {
        if ( ! isXinerama() ) {
            return getSecondaryScreenGraphicsConfiguration().getBounds();
        } else {
            return getRightScreenBounds();
        }
    }

    /**
     *  Returns the bounds for the full screen. In a Xinerama context this is the full Xinerama area.
     *  If there is a secondary device we will use this. Otherwise the primary.
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
     */
    public static Rectangle getLeftScreenBounds() {
        if ( isXinerama() ) {
            Rectangle xsb = getXineramaScreenBounds();
            return new Rectangle( xsb.x, xsb.y, (int) xsb.width / getNumberOfScreenDevices(), xsb.height );
        } else {
            return getPrimaryScreen().getBounds();
        }
    }
    
    
    /**
     *  Returns the bounds for the "right" window. In a Xinerama context this is the screen
     *  we assume that the screens are same sized so we subtract a screen width from the right edge and
     *  hope this is the correct location In a non Xinerama context this is the secondary
     *  screen.
     */
    public static Rectangle getRightScreenBounds() {
        if ( isXinerama() ) {
            Rectangle xsb = getXineramaScreenBounds();
            return new Rectangle( xsb.x + ( (int) xsb.width / getNumberOfScreenDevices() * ( getNumberOfScreenDevices() - 1 ) ),
                    xsb.y, (int) xsb.width / getNumberOfScreenDevices(), xsb.height );
        } else {
            return getSecondaryScreenGraphicsConfiguration().getBounds();
        }
    }
    
    
    /**
     *  Returns the bounds for the "top left" window.
     */
    public static Rectangle getTopLeftScreenBounds() {
        Rectangle bounds;
        if ( isXinerama() ) {
            bounds = getXineramaScreenBounds();
        } else {
            bounds = getPrimaryScreen().getBounds();
        }
        return new Rectangle( bounds.x, bounds.y, (int) bounds.width / getNumberOfScreenDevices(), (int) bounds.height / 2 );
    }
    
    
    /**
     *  Returns the bounds for the "top left" window.
     */
    public static Rectangle getBottomLeftScreenBounds() {
        Rectangle bounds;
        if ( isXinerama() ) {
            bounds = getXineramaScreenBounds();
        } else {
            bounds = getPrimaryScreen().getBounds();
        }
        return new Rectangle( bounds.x, bounds.y + ( (int) bounds.height / 2 ), (int) bounds.width / getNumberOfScreenDevices(), (int) bounds.height / 2 );
    }
    
    
    /**
     *  Returns the bounds for the "top right" window.
     */
    public static Rectangle getTopRightScreenBounds() {
        Rectangle bounds;
        if ( isXinerama() ) {
            bounds = getXineramaScreenBounds();
        } else {
            bounds = getPrimaryScreen().getBounds();
        }
        return new Rectangle( bounds.x + (int) bounds.width / getNumberOfScreenDevices(), bounds.y, (int) bounds.width / getNumberOfScreenDevices() , (int)bounds.height / 2 );
    }
    
    
    /**
     *  Returns the bounds for the "bottom right" window.
     */
    public static Rectangle getBottomRightScreenBounds() {
        Rectangle bounds;
        if ( isXinerama() ) {
            bounds = getXineramaScreenBounds();
        } else {
            bounds = getPrimaryScreen().getBounds();
        }
        return new Rectangle( bounds.x + (int) bounds.width / getNumberOfScreenDevices(), bounds.y + (int) bounds.height / 2, (int) bounds.width / getNumberOfScreenDevices() , (int)bounds.height / 2 );
    }
    
    
    /**
     *   This method returns the number of screen devices.
     *
     *  @return   The number of screen devices
     */
    public static int getNumberOfScreenDevices() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs =  ge.getScreenDevices();
        return gs.length;
    }
    
    
    
    /**
     *   This method returns whether the environment is a Xinerama environment
     *
     *  @return   True if the environment is a Xinerama environment, False if not
     */
    public static boolean isXinerama() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs =  ge.getScreenDevices();
        if ( gs.length < 2 ) {
            return false;
        }
        Rectangle r;
        GraphicsDevice gd;
        GraphicsConfiguration gc;
        for (int j = 0; j < gs.length; j++) {
            gd = gs[j];
            gc = gd.getDefaultConfiguration();
            r = gc.getBounds();
            if ( ( r.x != 0 ) || ( r.y != 0 ) ) {
                return true;
            }
        }
        return false;
    }
    
    
    
    
    /**
     *  Explains the graphics configuration to the log file if debug is on:
     */
    public static void explainGraphicsEnvironment() {
        if ( Settings.writeLog ) {
            boolean headless = GraphicsEnvironment.isHeadless();
            if ( headless ) {
                Tools.log( "ScreenHelper.explainGraphicsEnvironment: The GraphicsEnvironment is reporting no Display, Keyboard and Mound can be supported in this environment");
            } else {
                Tools.log( "ScreenHelper.explainGraphicsEnvironment: The GraphicsEnvironment is reporting it is not headless. How reassuring.");
            }
            
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            Tools.log( "ScreenHelper.explainGraphicsEnvironment: The LocalGraphicsEnvironment is reporting " +
                    ( ( ge.isHeadlessInstance() ) ? "no Display, Keyboard and Mound can be supported in this environment"
                    : "it is not headless. How reassuring.") );
            
            
            Tools.log( "ScreenHelper.explainGraphicsEnvironment: The Graphics Environment Center Point is at : " + ge.getCenterPoint().toString() );
            Tools.log( "ScreenHelper.explainGraphicsEnvironment: The Graphics Environment Maximum Window Bounds are: " + ge.getMaximumWindowBounds().toString() );
            
            GraphicsDevice[] gd = ge.getScreenDevices();
            Tools.log( "ScreenHelper.explainGraphicsEnvironment: There are "+Integer.toString(gd.length) +" Screen Devices: ");
            Tools.log( "ScreenHelper.explainGraphicsEnvironment: This " + ( ( isXinerama() ) ? "is" : "is not" ) + " a Xinerama environment." );
            for ( int i = 0; i< gd.length; i++ ) {
                explainGraphicsDevice( gd[i] );
            }
            
            Tools.log( "ScreenHelper.explainGraphisConfiguration: The Default Screen Device configuration:");
            GraphicsDevice defaultScreenDevice = ge.getDefaultScreenDevice();
            explainGraphicsDevice( defaultScreenDevice );
            
            
            
        }
    }
    
    
    
    /**
     *  Explains the graphics device to the log file
     */
    public static void explainGraphicsDevice( GraphicsDevice d ) {
        String id = d.getIDstring();
        if ( d.isFullScreenSupported() ) {
            Tools.log( "ScreenHelper.explainGraphisDevice: Device: " + id + " reports FullScreenexclusive mode is supported");
        } else {
            Tools.log( "ScreenHelper.explainGraphisDevice: Device: " + id + " reports FullScreenexclusive mode is not supported");
        }
        if ( d.isDisplayChangeSupported() ) {
            Tools.log( "ScreenHelper.explainGraphisDevice: Device: " + id + " reports DisplayChangeSupported is supported");
        } else {
            Tools.log( "ScreenHelper.explainGraphisDevice: Device: " + id + " reports DisplayChangeSupported is not supported");
        }
        Tools.log( "ScreenHelper.explainGraphisDevice: Device: " + id + " AvailableAcceleratedMemory: " + Integer.toString( d.getAvailableAcceleratedMemory() ) );
        explainDisplayMode( id, d.getDisplayMode() );
        
        DisplayMode[] dm = d.getDisplayModes();
        for ( int i = 0; i< dm.length; i++ ) {
            explainDisplayMode( id + " possibleMode [" + Integer.toString(i) + "]", dm[i] );
        }
        
        
        Tools.log( "ScreenHelper.explainGraphisDevice: Device: " + id + " Default Graphics Configuration:" );
        explainGraphicsConfiguration( id, d.getDefaultConfiguration() );
        Tools.log( "ScreenHelper.explainGraphisDevice: Device: " + id + " Available GraphicsConfigurations:" );
        GraphicsConfiguration[] gc = d.getConfigurations();
        for ( int i = 0; i< gc.length; i++ ) {
            explainGraphicsConfiguration( id + " Available Configuration [" + Integer.toString(i) + "] ", gc[i] );
        }
    }
    
    
    
    /**
     *  Explains the display mode to the log file
     */
    public static void explainDisplayMode( String id, DisplayMode dm ) {
        Tools.log( "ScreenHelper.explainDisplayMode: Device: " + id
                + " Bit Depth: " + Integer.toString( dm.getBitDepth() )
                + " RefreshRate: " + Integer.toString( dm.getRefreshRate() )
                + " Width: " + Integer.toString( dm.getWidth() )
                + " Height: " + Integer.toString( dm.getHeight() )
                );
    }
    
    
    
    /**
     *  Explains the graphics configuration to the log file
     */
    public static void explainGraphicsConfiguration( String id, GraphicsConfiguration gc ) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Tools.log( "ScreenHelper.explainGraphicsConfiguration: Device: " + id
                + " Bounds: " + gc.getBounds().toString()
                + " Insets: " + toolkit.getScreenInsets( gc ).toString()
                );
    }
    
    
}





