package jpo;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;

/*
Copyright (C) 2002-2007  Richard Eigenmann.
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
 * Class to create a JFrame which can be resized. This can be a bit hasslesome so I have put the code in
 * it's own class.
 */
public class ResizableJFrame extends JFrame {
    
    /**
     * Creates a new instance of ResizableJFrame
     * @param title  The title of the window
     * @param drawframe  Whether to draw the decorations or not
     * @param defaultSize Default size for the window
     */
    public ResizableJFrame( String title, boolean drawframe, Dimension defaultSize ) {
        super( title );
        this.defaultSize = defaultSize;
        Tools.log("ResizeableJFrame.constructor: defaultSize = " + defaultSize.toString() );
        setUndecorated( ! drawframe );
        setSize( defaultSize );
            EventQueue.invokeLater( new Runnable() {
                public void run() {
                    setVisible( true );
                }
            } );
    }
    
    /**
     * Creates a new instance of ResizableJFrame
     * @param title  The title of the window
     * @param drawframe  Whether to draw the decorations or not
     */
    public ResizableJFrame( String title, boolean drawframe ) {
        this( title, drawframe, new Dimension( 800, 600 ) );
    }
    
    /**
     *  tracks the default Size of this window
     */
    private Dimension defaultSize;
    
    
    /**
     *  maximises the window
     */
    public void maximise() {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                setExtendedState( Frame.MAXIMIZED_BOTH );
                validate();
            }
        } );
    }
    
    /**
     *  un-maximises the window, restoring the original size
     */
    public void unMaximise() {
        setExtendedState( Frame.NORMAL );
    }
    
    
    /**
     * Resizes the screen to the specified size after unmaximising it.
     */
    public void rezise( final Dimension targetSize ) {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                unMaximise();
                setBounds( new Rectangle( targetSize ) );
                validate();
            }
        } );
    }
    
    
    
    
    /**
     * Resizes the window to the left part of the screen
     */
    public void reziseToLeft() {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                //unMaximise();
                setBounds( ScreenHelper.getLeftScreenBounds() );
                setExtendedState( Frame.MAXIMIZED_VERT ); // because the above ignores menubars and stuff
                validate();
            }
        } );
    }
    
    
    /**
     *
     */
    public void reziseToTopLeft() {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                unMaximise();
                setBounds( ScreenHelper.getTopLeftScreenBounds() );
                validate();
            }
        } );
    }
    
    /**
     *
     */
    public void reziseToBottomLeft() {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                unMaximise();
                setBounds( ScreenHelper.getBottomLeftScreenBounds() );
                validate();
            }
        } );
    }
    
    
    /**
     *
     */
    public void reziseToRight() {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                //unMaximise();
                setBounds( ScreenHelper.getRightScreenBounds() );
                setExtendedState( Frame.MAXIMIZED_VERT ); // because the above ignores menubars and stuff
                validate();
            }
        } );
    }
    
    /**
     *
     */
    public void reziseToTopRight() {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                unMaximise();
                setBounds( ScreenHelper.getTopRightScreenBounds() );
                validate();
            }
        } );
    }
    
    /**
     *
     */
    public void reziseToBottomRight() {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                unMaximise();
                setBounds( ScreenHelper.getBottomRightScreenBounds() );
                validate();
            }
        } );
    }
    
    
    /**
     *  constant to indicate that a Fullscreen window should be created.
     */
    public static final int WINDOW_FULLSCREEN = 1;
    
    /**
     *  constant to indicate that the window should be created on the LEFT half of the display
     */
    public static final int WINDOW_LEFT = WINDOW_FULLSCREEN + 1;
    
    /**
     *  constant to indicate that the window should be created on the LEFT half of the display
     */
    public static final int WINDOW_RIGHT = WINDOW_LEFT + 1;
    
    /**
     *  constant to indicate that the window should be created on the TOP LEFT quarter of the display
     */
    public static final int WINDOW_TOP_LEFT = WINDOW_RIGHT + 1;
    
    /**
     *  constant to indicate that the window should be created on the TOP RIGHT quarter of the display
     */
    public static final int WINDOW_TOP_RIGHT = WINDOW_TOP_LEFT + 1;
    
    /**
     *  constant to indicate that the window should be created on the BOTTOM LEFT quarter of the display
     */
    public static final int WINDOW_BOTTOM_LEFT = WINDOW_TOP_RIGHT + 1;
    
    /**
     *  constant to indicate that the window should be created on the BOTTOM RIGHT quarter of the display
     */
    public static final int WINDOW_BOTTOM_RIGHT = WINDOW_BOTTOM_LEFT + 1;
    
    /**
     *  constant to indicate that the window should be created on the Default area
     */
    public static final int WINDOW_DEFAULT = WINDOW_BOTTOM_RIGHT + 1;
    
    
    /**
     *  request that the window showing the picture be changed be changed.
     *  @param  newMode  {@link #WINDOW_FULLSCREEN}, {@link #WINDOW_LEFT},
     *		{@link #WINDOW_RIGHT},  {@link #WINDOW_TOP_LEFT},
     *		{@link #WINDOW_TOP_RIGHT}, {@link #WINDOW_BOTTOM_LEFT},
     *		{@link #WINDOW_BOTTOM_RIGHT} or {@link #WINDOW_DEFAULT}
     *		need to be indicated.
     *
     */
    public void resizeTo( int newMode ) {
        switch ( newMode ) {
            case WINDOW_FULLSCREEN:
                maximise();
                break;
            case WINDOW_LEFT:
                reziseToLeft();
                break;
            case WINDOW_RIGHT:
                reziseToRight();
                break;
            case WINDOW_TOP_LEFT:
                reziseToTopLeft();
                break;
            case WINDOW_TOP_RIGHT:
                reziseToTopRight();
                break;
            case WINDOW_BOTTOM_LEFT:
                reziseToBottomLeft();
                break;
            case WINDOW_BOTTOM_RIGHT:
                reziseToBottomRight();
                break;
            case WINDOW_DEFAULT:
                unMaximise();
                break;
        }
    }
    
    
    
}
