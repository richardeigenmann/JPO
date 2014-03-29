package jpo.gui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import jpo.gui.ChangeWindowInterface;

/*
 Copyright (C) 2002 - 2011  Richard Eigenmann.
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
 * This is an extended JFrame which has a few useful methods for it to be
 * resized. The resizing doesn't always work very well which is why I
 * encapsulated this into this class.
 */
public class ResizableJFrame
        extends JFrame implements ChangeWindowInterface {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ResizableJFrame.class.getName() );

    /**
     * Creates a new instance of ResizableJFrame
     *
     * @param viewer The Component that the JFrame is the show
     */
    public ResizableJFrame( Component viewer ) {
        super( Settings.jpoResources.getString( "PictureViewerTitle" ) );
        Tools.checkEDT();
        Dimension initialDimension = (Dimension) Settings.pictureViewerDefaultDimensions.clone();
        if ( ( initialDimension.width == 0 ) || ( initialDimension.height == 0 ) ) {
            // this gets us around the problem that the Affine Transform crashes if the window size is 0,0
            initialDimension = Settings.windowSizes[1];
        }

        setUndecorated( !decorateWindow );
        setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        getContentPane().setLayout( new BorderLayout() );

        setSize( initialDimension );
        getContentPane().add( "Center", viewer );
        setVisible( true );

        if ( Settings.maximisePictureViewerWindow ) {
            maximise();
            windowMode = ResizableJFrame.WINDOW_FULLSCREEN;
        }
    }

    /**
     * Call this method on the EDT to maximise the windows
     */
    public void maximise() {
        Tools.checkEDT();
        if ( this.getToolkit().isFrameStateSupported( Frame.MAXIMIZED_BOTH ) ) {
            setExtendedState( Frame.MAXIMIZED_BOTH );
            validate();
        } else {
            LOGGER.severe( "The Window Manager doesn't support Frame.MAXIMIZED_BOTH" );
        }
    }

    /**
     * Call this method on the EDT to un-maximises the window, restoring the
     * original size
     */
    public void unMaximise() {
        Tools.checkEDT();
        if ( this.getToolkit().isFrameStateSupported( Frame.NORMAL ) ) {
            setExtendedState( Frame.NORMAL );
        } else {
            LOGGER.severe( "The Window Manager doesn't support Frame.NORMAL" );
        }
    }
    /**
     * indicator that specifies what sort of window should be created
     */
    private int windowMode = ResizableJFrame.WINDOW_DEFAULT;
    /**
     * Flag that specifies whether the window should be drawn with decoration or
     * not.
     */
    private transient boolean decorateWindow = true;

    /**
     * request that the window showing the picture be changed be changed.
     *
     * @param newMode null     {@link ResizableJFrame#WINDOW_FULLSCREEN}, {@link ResizableJFrame#WINDOW_LEFT},
     *		{@link ResizableJFrame#WINDOW_RIGHT},  {@link ResizableJFrame#WINDOW_TOP_LEFT},
     *		{@link ResizableJFrame#WINDOW_TOP_RIGHT}, {@link ResizableJFrame#WINDOW_BOTTOM_LEFT},
     *		{@link ResizableJFrame#WINDOW_BOTTOM_RIGHT} or
     * {@link ResizableJFrame#WINDOW_DEFAULT} need to be indicated.
     *
     */
    @Override
    public void switchWindowMode( final int newMode ) {
        LOGGER.log( Level.FINE, "old mode: {0} new: {1}", new Object[]{ Integer.toString( windowMode ), Integer.toString( newMode ) } );
        windowMode = newMode;
        boolean newDecoration = decorateWindow;
        // some intelligence as to when to have window decorations and when not.
        switch ( newMode ) {
            case ResizableJFrame.WINDOW_FULLSCREEN:
                newDecoration = false;
                break;
            case ResizableJFrame.WINDOW_LEFT:
                newDecoration = false;
                break;
            case ResizableJFrame.WINDOW_RIGHT:
                newDecoration = false;
                break;
            case ResizableJFrame.WINDOW_TOP_LEFT:
                newDecoration = true;
                break;
            case ResizableJFrame.WINDOW_TOP_RIGHT:
                newDecoration = true;
                break;
            case ResizableJFrame.WINDOW_BOTTOM_LEFT:
                newDecoration = true;
                break;
            case ResizableJFrame.WINDOW_BOTTOM_RIGHT:
                newDecoration = true;
                break;
            case ResizableJFrame.WINDOW_DEFAULT:
                newDecoration = true;
                break;
        }
        switchDecorations( newDecoration );
        resizeTo( windowMode );
    }

    /**
     * This method turns on or turns off the frame around the window. It works
     * by closing the window and creating a new one with the correct
     * decorations. It uses the decorateWindow flag to determine if the
     * decorations are being shown.
     *
     * @param newDecoration
     */
    @Override
    public void switchDecorations( boolean newDecoration ) {
        if ( decorateWindow != newDecoration ) {
            decorateWindow = newDecoration;
            LOGGER.fine( "Switching decoration" );
            dispose();
            setUndecorated( !decorateWindow );
            pack();
            setVisible( true );
        }
    }

    /**
     * Resizes the screen to the specified size after unmaximising it.
     *
     * @param targetSize The dimension you want the Frame to have
     */
    public void rezise( final Dimension targetSize ) {
        Tools.checkEDT();
        unMaximise();
        setBounds( new Rectangle( targetSize ) );
        validate();
    }

    /**
     * Resizes the window to the left part of the screen after unmaximising it.
     */
    public void reziseToLeft() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getLeftScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the top left quarter of the screen after
     * unmaximising it.
     */
    public void reziseToTopLeft() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getTopLeftScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the bottom left quarter of the screen after
     * unmaximising it.
     */
    public void reziseToBottomLeft() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getBottomLeftScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the right part of the screen after unmaximising it.
     */
    public void reziseToRight() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getRightScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the top right part of the screen after unmaximising
     * it.
     */
    public void reziseToTopRight() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getTopRightScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the bottom right part of the screen after
     * unmaximising it.
     */
    public void reziseToBottomRight() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getBottomRightScreenBounds() );
        validate();
    }
    /**
     * constant to indicate that a Fullscreen window should be created.
     */
    public static final int WINDOW_FULLSCREEN = 1;
    /**
     * constant to indicate that the window should be created on the LEFT half
     * of the display
     */
    public static final int WINDOW_LEFT = WINDOW_FULLSCREEN + 1;
    /**
     * constant to indicate that the window should be created on the RIGHT half
     * of the display
     */
    public static final int WINDOW_RIGHT = WINDOW_LEFT + 1;
    /**
     * constant to indicate that the window should be created on the TOP LEFT
     * quarter of the display
     */
    public static final int WINDOW_TOP_LEFT = WINDOW_RIGHT + 1;
    /**
     * constant to indicate that the window should be created on the TOP RIGHT
     * quarter of the display
     */
    public static final int WINDOW_TOP_RIGHT = WINDOW_TOP_LEFT + 1;
    /**
     * constant to indicate that the window should be created on the BOTTOM LEFT
     * quarter of the display
     */
    public static final int WINDOW_BOTTOM_LEFT = WINDOW_TOP_RIGHT + 1;
    /**
     * constant to indicate that the window should be created on the BOTTOM
     * RIGHT quarter of the display
     */
    public static final int WINDOW_BOTTOM_RIGHT = WINDOW_BOTTOM_LEFT + 1;
    /**
     * constant to indicate that the window should be created on the Default
     * area
     */
    public static final int WINDOW_DEFAULT = WINDOW_BOTTOM_RIGHT + 1;

    /**
     * Request that the window showing the picture be changed be changed.
     *
     * @param newMode null     {@link #WINDOW_FULLSCREEN}, {@link #WINDOW_LEFT},
     *		{@link #WINDOW_RIGHT},  {@link #WINDOW_TOP_LEFT},
     *		{@link #WINDOW_TOP_RIGHT}, {@link #WINDOW_BOTTOM_LEFT},
     *		{@link #WINDOW_BOTTOM_RIGHT} or {@link #WINDOW_DEFAULT} need to be
     * indicated.
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
