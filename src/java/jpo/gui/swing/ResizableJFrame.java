package jpo.gui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.util.logging.Logger;
import javax.swing.JFrame;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import jpo.gui.ChangeWindowInterface;
import jpo.gui.swing.ResizableJFrame.WindowSize;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_DEFAULT;
import static jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_FULLSCREEN;

/*
 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
 * This is basically a JFrame which understands how to resize itself to a
 * specific {
 *
 * {@link WindowSize} and can switch on and off the window decorations (which
 * requires disposing of the window and redrawing itself).
 */
public class ResizableJFrame
        extends JFrame implements ChangeWindowInterface {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ResizableJFrame.class.getName() );

    /**
     * Window size options
     */
    public static enum WindowSize {

        /**
         * Switches the Window to fullscreen
         */
        WINDOW_FULLSCREEN,
        /**
         * Switches the window to occupy the left side
         */
        WINDOW_LEFT,
        /**
         * Switches the window to occupy the right side
         */
        WINDOW_RIGHT,
        /**
         * Switches the window to occupy the top left of the screen
         */
        WINDOW_TOP_LEFT,
        /**
         * Switches the window to occupy the top right of the screen
         */
        WINDOW_TOP_RIGHT,
        /**
         * Switches the window to occupy the bottom left of the screen
         */
        WINDOW_BOTTOM_LEFT,
        /**
         * Switches the window to occupy the bottom right of the screen
         */
        WINDOW_BOTTOM_RIGHT,
        /**
         * Switches the window to the default size and location
         */
        WINDOW_DEFAULT
    }

    /**
     * Creates a new instance of ResizableJFrame
     *
     * @param component The Component to show in the frame
     */
    public ResizableJFrame( Component component ) {
        super( Settings.jpoResources.getString( "PictureViewerTitle" ) );
        Tools.checkEDT();

        //ToDo: Review this...!
        Dimension initialDimension = (Dimension) Settings.pictureViewerDefaultDimensions.clone();
        if ( ( initialDimension.width == 0 ) || ( initialDimension.height == 0 ) ) {
            // this gets us around the problem that the Affine Transform crashes if the window size is 0,0
            initialDimension = Settings.windowSizes[1];
        }

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( "Center", component );
        setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );

        setUndecorated( !decorateWindow );
        setSize( initialDimension );

        if ( Settings.maximisePictureViewerWindow ) {
            maximise();
            windowMode = WINDOW_FULLSCREEN;
        }
        pack();
        setVisible( true );
    }

    /**
     * Call this method on the EDT to maximise the windows. Don't forget to call
     * validate() afterwards.
     */
    public final void maximise() {
        Tools.checkEDT();
        if ( this.getToolkit().isFrameStateSupported( Frame.MAXIMIZED_BOTH ) ) {
            setExtendedState( Frame.MAXIMIZED_BOTH );
        } else {
            LOGGER.severe( "The Window Manager doesn't support Frame.MAXIMIZED_BOTH. Leaving window unchanged." );
        }
    }

    /**
     * Call this method on the EDT to un-maximises the window, restoring the
     * original size. Don't forget to call validate() afterwards.
     */
    public void unMaximise() {
        Tools.checkEDT();
        if ( this.getToolkit().isFrameStateSupported( Frame.NORMAL ) ) {
            setExtendedState( Frame.NORMAL );
        } else {
            LOGGER.severe( "The Window Manager doesn't support Frame.NORMAL. Leaving window unchanged." );
        }
    }
    /**
     * indicator that specifies what sort of window should be created
     */
    private WindowSize windowMode = WINDOW_DEFAULT;

    /**
     * Flag that specifies whether the window should be drawn with decoration or
     * not.
     */
    private boolean decorateWindow = true;

    /**
     * request that the window showing the picture be changed be changed.
     *
     * @param newMode {@link WindowSize#WINDOW_FULLSCREEN}, {@link WindowSize#WINDOW_LEFT},
     * {@link WindowSize#WINDOW_RIGHT},  {@link WindowSize#WINDOW_TOP_LEFT}, 
     * {@link WindowSize#WINDOW_TOP_RIGHT}, {@link WindowSize#WINDOW_BOTTOM_LEFT},
     * {@link WindowSize#WINDOW_BOTTOM_RIGHT} or
     * {@link WindowSize#WINDOW_DEFAULT} need to be indicated.
     *
     */
    @Override
    public void switchWindowMode( final WindowSize newMode ) {
        LOGGER.info( String.format( "old mode: %s new: %s", windowMode, newMode ) );
        windowMode = newMode;
        boolean newDecoration = decorateWindow;
        // some intelligence as to when to have window decorations and when not.
        switch ( newMode ) {
            case WINDOW_FULLSCREEN:
                newDecoration = false;
                break;
            case WINDOW_LEFT:
                newDecoration = false;
                break;
            case WINDOW_RIGHT:
                newDecoration = false;
                break;
            case WINDOW_TOP_LEFT:
                newDecoration = true;
                break;
            case WINDOW_TOP_RIGHT:
                newDecoration = true;
                break;
            case WINDOW_BOTTOM_LEFT:
                newDecoration = true;
                break;
            case WINDOW_BOTTOM_RIGHT:
                newDecoration = true;
                break;
            case WINDOW_DEFAULT:
                newDecoration = true;
                break;
        }
        switchDecorations( newDecoration );
        resizeTo( windowMode );
    }

    /**
     * Request that the window showing the picture be changed be changed.
     *
     * @param newMode {@link WindowSize#WINDOW_FULLSCREEN}, {@link WindowSize#WINDOW_LEFT},
     *		{@link WindowSize#WINDOW_RIGHT},  {@link WindowSize#WINDOW_TOP_LEFT},
     *		{@link WindowSize#WINDOW_TOP_RIGHT}, {@link WindowSize#WINDOW_BOTTOM_LEFT},
     *		{@link WindowSize#WINDOW_BOTTOM_RIGHT} or {@link WindowSize#WINDOW_DEFAULT} need to be
     * indicated.
     *
     */
    public void resizeTo( WindowSize newMode ) {
        switch ( newMode ) {
            case WINDOW_FULLSCREEN:
                maximise();
                break;
            case WINDOW_LEFT:
                resizeToLeft();
                break;
            case WINDOW_RIGHT:
                resizeToRight();
                break;
            case WINDOW_TOP_LEFT:
                resizeToTopLeft();
                break;
            case WINDOW_TOP_RIGHT:
                resizeToTopRight();
                break;
            case WINDOW_BOTTOM_LEFT:
                resizeToBottomLeft();
                break;
            case WINDOW_BOTTOM_RIGHT:
                resizeToBottomRight();
                break;
            case WINDOW_DEFAULT:
                unMaximise();
                break;
        }
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
            Rectangle myBounds = getBounds();
            dispose();
            setUndecorated( !decorateWindow );
            myBounds.setBounds( myBounds );
            setVisible( true );
        }
    }

    /**
     * Resizes the screen to the specified size after unmaximising it.
     *
     * @param targetSize The dimension you want the Frame to have
     * 
     */
    @SuppressWarnings( "deprecation" )
    @Override
    public void resize( final Dimension targetSize ) {
        Tools.checkEDT();
        unMaximise();
        setBounds( new Rectangle( targetSize ) );
        validate();
    }

    /**
     * Resizes the window to the left part of the screen after unmaximising it.
     */
    public void resizeToLeft() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getLeftScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the top left quarter of the screen after
     * unmaximising it.
     */
    public void resizeToTopLeft() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getTopLeftScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the bottom left quarter of the screen after
     * unmaximising it.
     */
    public void resizeToBottomLeft() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getBottomLeftScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the right part of the screen after unmaximising it.
     */
    public void resizeToRight() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getRightScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the top right part of the screen after unmaximising
     * it.
     */
    public void resizeToTopRight() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getTopRightScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the bottom right part of the screen after
     * unmaximising it.
     */
    public void resizeToBottomRight() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getBottomRightScreenBounds() );
        validate();
    }

}
