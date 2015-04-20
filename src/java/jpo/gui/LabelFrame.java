package jpo.gui;

import jpo.dataModel.Settings;
import java.lang.reflect.InvocationTargetException;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

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
        Runnable r = new Runnable() {

            @Override
            public void run() {
                initComponents( title );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait( r );
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
        Runnable r = new Runnable() {

            @Override
            public void run() {
                progressJLabel.setText( message );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }

    }

    /**
     * method that closes the frame and gets rid of it
     */
    public void getRid() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                jFrame.setVisible( false );
                jFrame.dispose();
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }
}
