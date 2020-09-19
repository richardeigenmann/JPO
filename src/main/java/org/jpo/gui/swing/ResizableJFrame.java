package org.jpo.gui.swing;

import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;
import org.jpo.gui.ChangeWindowInterface;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_DEFAULT;
import static org.jpo.gui.swing.ResizableJFrame.WindowSize.WINDOW_FULLSCREEN;

/*
 Copyright (C) 2002 - 2020  Richard Eigenmann.
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
    public enum WindowSize {

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
     * @param title Title for the frame
     * @param component The Component to show in the frame
     */
    public ResizableJFrame( String title, Component component ) {
        super( title );
        Tools.checkEDT();

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add("Center", component);
        setBackground(Settings.getPictureviewerBackgroundColor());

        setUndecorated( !decorateWindow );

        if (Settings.isMaximisePictureViewerWindow()) {
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
    public void maximise() {
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
        LOGGER.log(Level.INFO, "old mode: {0} new: {1}", new Object[]{windowMode, newMode});
        windowMode = newMode;
        boolean newDecoration = switch (newMode) {
            case WINDOW_FULLSCREEN, WINDOW_LEFT, WINDOW_RIGHT -> false;
            default -> true;
        };
        // some intelligence as to when to have window decorations and when not.
        showWindowDecorations(newDecoration);
        resizeTo(windowMode);
    }

    /**
     * Request that the window showing the picture be changed be changed.
     *
     * @param newMode {@link WindowSize#WINDOW_FULLSCREEN}, {@link WindowSize#WINDOW_LEFT},
     *		{@link WindowSize#WINDOW_RIGHT},  {@link WindowSize#WINDOW_TOP_LEFT},
     *		{@link WindowSize#WINDOW_TOP_RIGHT}, {@link WindowSize#WINDOW_BOTTOM_LEFT},
     *		{@link WindowSize#WINDOW_BOTTOM_RIGHT} or
     * {@link WindowSize#WINDOW_DEFAULT} need to be indicated.
     *
     */
    public void resizeTo( WindowSize newMode ) {
        switch (newMode) {
            case WINDOW_FULLSCREEN -> maximise();
            case WINDOW_LEFT -> resizeToLeft();
            case WINDOW_RIGHT -> resizeToRight();
            case WINDOW_TOP_LEFT -> resizeToTopLeft();
            case WINDOW_TOP_RIGHT -> resizeToTopRight();
            case WINDOW_BOTTOM_LEFT -> resizeToBottomLeft();
            case WINDOW_BOTTOM_RIGHT -> resizeToBottomRight();
            default -> unMaximise();
        }
    }

    /**
     * This method turns on or turns off the frame around the window. It works
     * by closing the window and creating a new one with the correct
     * decorations. It uses the decorateWindow flag to determine if the
     * decorations are being shown.
     *
     * @param newDecoration Send true if decorations should be shown, false if
     * they should not be shown
     */
    @Override
    public void showWindowDecorations( boolean newDecoration ) {
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
     * Resizes the screen to the specified size after un-maximising it.
     *
     * @param targetSize The dimension you want the Frame to have
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
     * Resizes the window to the left part of the screen after un-maximising it.
     */
    public void resizeToLeft() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getLeftScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the top left quarter of the screen after
     * un-maximising it.
     */
    public void resizeToTopLeft() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getTopLeftScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the bottom left quarter of the screen after
     * un-maximising it.
     */
    public void resizeToBottomLeft() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getBottomLeftScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the right part of the screen after un-maximising it.
     */
    public void resizeToRight() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getRightScreenBounds() );
        validate();
    }

    /**
     * Resizes the window to the top right part of the screen after un-maximising
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
     * un-maximising it.
     */
    public void resizeToBottomRight() {
        Tools.checkEDT();
        unMaximise();
        setBounds( ScreenHelper.getBottomRightScreenBounds() );
        validate();
    }

}
