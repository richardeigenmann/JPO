package org.jpo.gui;

import org.jpo.dataModel.Settings;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Creates a JFrame that holds a centred JLabel.
 *
 */
public class LabelFrame {

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
        Runnable runnable = () -> initComponents( title );
        if ( SwingUtilities.isEventDispatchThread() ) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable );
            } catch ( InterruptedException | InvocationTargetException ex ) {
                // don't care
            }
        }
    }

    /**
     * Creates the components
     * @param title title for the frame
     */
    private void initComponents( String title ) {
        jFrame = new JFrame();
        jFrame.setTitle( title );
        jFrame.setLocationRelativeTo( Settings.anchorFrame );
        jFrame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );

        progressJLabel = new JLabel();
        progressJLabel.setPreferredSize( LABEL_DIMENSION );
        progressJLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
        jFrame.getContentPane().add( progressJLabel );

        jFrame.pack();
        jFrame.setVisible( true );
    }

    /**
     * Updates the label with the supplied message
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
     * method that closes the frame and gets rid of it
     */
    public void getRid() {
        Runnable runnable = () -> {
            jFrame.setVisible( false );
            jFrame.dispose();
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable );
        }
    }
}
