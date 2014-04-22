package jpo.gui;

import jpo.dataModel.Settings;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import jpo.dataModel.Tools;

/**
 * Constructor for a progress GUI for the XML reader
 *
 */
public class LoadProgressGui {

    /**
     * variable to indicate that the process should be terminated
     */
    private boolean interrupt;  // default is false

    /**
     * Holds the label for the progress dialog
     */
    private JLabel progressJLabel;

    /**
     * Reference for the frame
     */
    private JFrame frame;

    /**
     * Constructor for the LoadProgressGui
     */
    public LoadProgressGui() {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                initComponents();
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait( r );
            } catch ( InterruptedException | InvocationTargetException ex ) {
                Logger.getLogger( LoadProgressGui.class.getName() ).log( Level.SEVERE, null, ex );
                Logger.getLogger( LoadProgressGui.class.getName() ).log( Level.SEVERE, null, "no idea what to do here" );
            }
        }
    }

    /**
     * Creates the components
     */
    private void initComponents() {
        Tools.checkEDT();

        frame = new JFrame();

        frame.setTitle( "Loading file" );
        frame.setLocationRelativeTo( Settings.anchorFrame );
        frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        frame.addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                interrupt = true;
            }
        } );

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets( 4, 4, 4, 4 );

        JPanel contentJPanel = new JPanel();
        contentJPanel.setLayout( new GridBagLayout() );
        contentJPanel.setPreferredSize( new Dimension( 260, 30 ) );
        frame.getContentPane().add( contentJPanel );

        progressJLabel = new JLabel();
        progressJLabel.setPreferredSize( new Dimension( 250, 20 ) );
        progressJLabel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        progressJLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );

        constraints.gridy++;
        contentJPanel.add( progressJLabel, constraints );

        frame.pack();
        frame.setVisible( true );
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
                frame.setVisible( false );
                frame.dispose();
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }

    }
}
