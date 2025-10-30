package org.jpo.gui.swing;

import org.jpo.datamodel.ProgressTracker;
import org.jpo.datamodel.Settings;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/*
Copyright (C) 2020-2025 Richard Eigenmann, Zürich, Switzerland
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or any later version. This program is distributed
in the hope that it will be useful, but WITHOUT ANY WARRANTY,
without even the implied warranty of MERCHANTABILITY or FITNESS
FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
more details. You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
The license is in gpl.txt.
See http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * Creates a JFrame that holds a centred JLabel.
 */
public class LabelFrame implements ProgressTracker {

    /**
     * Holds the label
     */
    private JLabel progressJLabel;

    /**
     * Reference for the JFrame
     */
    private JFrame jFrame;

    /**
     * The size for the label
     */
    private static final Dimension LABEL_DIMENSION = new Dimension( 310, 20 );

    /**
     * Constructs the LabelFrame. Is EventDispatchThread safe. I.e. will execute
     * synchronously if on EDT else will invokeAndWait.
     * @param title The title for the JFrame
     */
    public LabelFrame( final String title ) {
        final Runnable runnable = () -> initComponents(title);
        if ( SwingUtilities.isEventDispatchThread() ) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable );
            } catch ( InterruptedException | InvocationTargetException _ ) {
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Creates the components
     * @param title title for the frame
     */
    private void initComponents(final String title) {
        jFrame = new JFrame();
        jFrame.setTitle(title);
        jFrame.setLocationRelativeTo(Settings.getAnchorFrame());
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        progressJLabel = new JLabel();
        progressJLabel.setPreferredSize(LABEL_DIMENSION);
        progressJLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jFrame.getContentPane().add(progressJLabel);

        jFrame.pack();
        jFrame.setVisible( true );
    }

    /**
     * Updates the label with the supplied message and makes sure to do it on the EDT;
     * the caller can be on any thread.
     *
     * @param message the message to show in the label
     */
    public void update( final String message ) {
        Runnable runnable = () -> progressJLabel.setText( message );
        if ( SwingUtilities.isEventDispatchThread() ) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable );
        }

    }

    /**
     * method that closes the frame and gets rid of it. Makes sure to do all Swing actions on the EDT
     */
    public void done() {
        final Runnable runnable = () -> {
            jFrame.setVisible(false);
            jFrame.dispose();
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable );
        }
    }
}
