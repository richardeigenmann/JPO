package org.jpo.gui.swing;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/*
 Copyright (C) 2002 - 2021  Richard Eigenmann.
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
 * <p>
 * {@link WindowSize} and can switch on and off the window decorations (which
 * requires disposing of the window and redrawing itself).
 */
public class ResizableJFrame
        extends JFrame {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(ResizableJFrame.class.getName());

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
         * Switches the window to a custom size and position.
         */
        WINDOW_CUSTOM_SIZE
    }

    /**
     * Creates a new instance of ResizableJFrame
     *
     * @param title     Title for the frame
     * @param component The Component to show in the frame
     */
    public ResizableJFrame(final String title, final Component component, final WindowSize initialSize) {
        super(title);
        getContentPane().add(component);
        pack();
        setVisible(true);
        switchWindowMode(initialSize);
    }

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
     * {@link WindowSize#WINDOW_CUSTOM_SIZE} need to be indicated.
     *
     */
    public void switchWindowMode(final WindowSize newMode) {
        boolean newDecoration = switch (newMode) {
            case WINDOW_FULLSCREEN, WINDOW_LEFT, WINDOW_RIGHT -> false;
            default -> true;
        };
        showWindowDecorations(newDecoration);

        switch (newMode) {
            case WINDOW_FULLSCREEN -> maximise();
            case WINDOW_LEFT -> setBounds(ScreenHelper.getLeftScreenBounds());
            case WINDOW_RIGHT -> setBounds(ScreenHelper.getRightScreenBounds());
            case WINDOW_TOP_LEFT -> setBounds(ScreenHelper.getTopLeftScreenBounds());
            case WINDOW_TOP_RIGHT -> setBounds(ScreenHelper.getTopRightScreenBounds());
            case WINDOW_BOTTOM_LEFT -> setBounds(ScreenHelper.getBottomLeftScreenBounds());
            case WINDOW_BOTTOM_RIGHT -> setBounds(ScreenHelper.getBottomRightScreenBounds());
            // no default is deliberate
        }
    }

    /**
     * Call this method on the EDT to maximise the windows. Don't forget to call
     * validate() afterwards.
     */
    private void maximise() {
        if (this.getToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {
            setExtendedState(Frame.MAXIMIZED_BOTH);
        } else {
            LOGGER.severe("The Window Manager doesn't support Frame.MAXIMIZED_BOTH. Leaving window unchanged.");
        }
    }

    /**
     * Call this method on the EDT to un-maximises the window, restoring the
     * original size. Don't forget to call validate() afterwards.
     */
    private void unMaximise() {
        if (this.getToolkit().isFrameStateSupported(Frame.NORMAL)) {
            setExtendedState(Frame.NORMAL);
        } else {
            LOGGER.severe("The Window Manager doesn't support Frame.NORMAL. Leaving window unchanged.");
        }
    }

    /**
     * Resizes the screen to the specified size after un-maximising it.
     * Unlike the method it is overriding, it calls validate directly so
     * the caller doesn't have to. Also it calls unMaximise().
     *
     * @param targetSize The dimension you want the Frame to have
     */
    @Override
    public void setBounds(final Rectangle targetSize) {
        unMaximise();
        super.setBounds(new Rectangle(targetSize));
        validate();
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
    public void showWindowDecorations( boolean newDecoration ) {
        if ( decorateWindow != newDecoration ) {
            decorateWindow = newDecoration;
            final Rectangle myBounds = getBounds();
            dispose();
            setUndecorated( !decorateWindow );
            myBounds.setBounds( myBounds );
            setVisible( true );
        }
    }



}
