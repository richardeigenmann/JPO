package org.jpo.gui;

import net.miginfocom.swing.MigLayout;
import org.jpo.dataModel.Settings;
import org.jpo.dataModel.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/*
 ProgressGui.java:  a class that shows the progress in adding pictures

 Copyright (C) 2002-2019  Richard Eigenmann.
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
 * a private class that allows the PictureAdder to show what it is doing.
 */
public class ProgressGui extends JFrame implements ProgressListener {

    /**
     * button to start the export
     *
     */
    private final JButton okJButton = new JButton( Settings.jpoResources.getString( "genericOKText" ) );
    /**
     * button to cancel the dialog
     *
     */
    private final JButton cancelJButton = new JButton( Settings.jpoResources.getString( "genericCancelText" ) );
    /**
     * Progress Indicator
     */
    private JProgressBar progressBar;
    /**
     * Label
     */
    private JLabel progressLabel;
    /**
     * variable that is checked periodically that stops the addDirectory loop in
     * a controlled way
     */
    private final InterruptSemaphore interruptSemaphore = new InterruptSemaphore();
    
    /**
     * how long the gui should show after it has finished.
     */
    private static final int TIMEOUT = 5 * 60 * 1000;
    /**
     * The minimum size for a ProgressGui
     */
    private static final Dimension MINIMUM_FRAME_SIZE = new Dimension( 350, 100 );

    /**
     * Constructor for a progress GUI
     *
     * @param max The total to count to
     * @param title The Title to be shown in the dialog
     * @param doneString The text to show when done.
     *
     */
    public ProgressGui( final int max, final String title, String doneString ) {
        Tools.checkEDT();
        initComponents( doneString, max, title );
    }
    /**
     * The string that should be shown after completion. Something like "12
     * pictures added". Default is "Done."
     */
    private String doneString = "Done.";

    /**
     * Sets the text that will be shown when the processing is over.
     *
     * @param newDoneString The text to be shown when the processing is over
     */
    public void setDoneString( String newDoneString ) {
        doneString = newDoneString;

    }

    private void initComponents( String doneString, int max, String title ) {
        setDoneString( doneString );

        setTitle( title );
        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                if ( okJButton.isVisible() ) {
                    getRid();
                } else {
                    interruptSemaphore.setShouldInterrupt( true );
                }
            }
        } );

        JPanel contentJPanel = new JPanel();
        contentJPanel.setLayout( new MigLayout( "insets 10", // Layout Constraints
                "[center]", // Column constraints with default align
                "[top]" ) ); // Row constraints with default align

        getContentPane().add( contentJPanel );

        progressBar = new JProgressBar( 0, max );
        progressBar.setBorder( BorderFactory.createLineBorder( Color.gray, 1 ) );
        progressBar.setStringPainted( true );
        progressBar.setValue( 0 );
        contentJPanel.add(progressBar, "push, wrap" );

        progressLabel = new JLabel();
        progressLabel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        progressLabel.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
        contentJPanel.add(progressLabel, "wrap" );

        okJButton.setPreferredSize( Settings.defaultButtonDimension );
        okJButton.setMinimumSize( Settings.defaultButtonDimension );
        okJButton.setMaximumSize( Settings.defaultButtonDimension );
        okJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        okJButton.setDefaultCapable( true );
        okJButton.addActionListener(( ActionEvent e ) -> getRid());
        contentJPanel.add( okJButton, "wrap" );

        cancelJButton.setPreferredSize( Settings.defaultButtonDimension );
        cancelJButton.setMinimumSize( Settings.defaultButtonDimension );
        cancelJButton.setMaximumSize( Settings.defaultButtonDimension );
        cancelJButton.setBorder( BorderFactory.createRaisedBevelBorder() );
        cancelJButton.setDefaultCapable( true );
        cancelJButton.addActionListener(( ActionEvent e ) -> interruptSemaphore.setShouldInterrupt( true ));
        contentJPanel.add( cancelJButton, "wrap" );

        okJButton.setVisible( false );
        this.getRootPane().setDefaultButton( cancelJButton );
        setMinimumSize( MINIMUM_FRAME_SIZE );
        pack();
        setLocationRelativeTo( Settings.anchorFrame );
        setVisible( true );
    }

    /**
     * method that closes the frame and gets rid of it
     */
    private void getRid() {
        Runnable r = () -> {
            setVisible( false );
            dispose();
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }

    /**
     * Adds 1 to the progress counter
     */
    @Override
    public void progressIncrement() {
        Tools.checkEDT();
        progressBar.setValue( progressBar.getValue() + 1 );
        progressLabel.setText(progressBar.getValue() + " / " + progressBar.getMaximum());
    }

    /**
     * decreases the total by 1
     */
    public void decrementTotal() {
        progressBar.setMaximum( progressBar.getMaximum() - 1 );
        progressLabel.setText(progressBar.getValue() + " / " + progressBar.getMaximum());
    }

    /**
     * set the maximum
     *
     * @param max maximum
     */
    public void setMaximum(int max ) {
        progressBar.setMaximum( max );
        progressLabel.setText(progressBar.getValue() + " / " + progressBar.getMaximum());
    }

    /**
     * removes the Cancel Button and adds an OK button
     */
    public void switchToDoneMode() {
        Tools.checkEDT();
        okJButton.setVisible( true );
        cancelJButton.setVisible( false );
        progressLabel.setText( String.format( doneString, progressBar.getValue() ) );
        validate();
        Timer timer = new Timer( TIMEOUT, ( ActionEvent evt ) -> getRid());
        timer.setRepeats( false );
        timer.start();
    }

    /**
     * returns the interrupt semaphore object
     *
     * @return true if the thread should be interrupted
     */
    public InterruptSemaphore getInterruptSemaphore() {
        return interruptSemaphore;
    }
}
