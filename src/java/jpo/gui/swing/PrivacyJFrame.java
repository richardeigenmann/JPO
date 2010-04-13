package jpo.gui.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import net.miginfocom.swing.MigLayout;

/*
PrivacyJFrame.java:  a dialog to clear private data from JPO

Copyright (C) 2002 - 2010 Richard Eigenmann.
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
 * @author Richard Eigenmann
 */
public class PrivacyJFrame
        extends JFrame {

    /**
     * Constructs a Frame with the privacy options
     */
    public PrivacyJFrame() {
        super( "Privacy Window" );
        Tools.checkEDT();
        initGui();
        pack();
        setVisible( true );
        setLocationRelativeTo( Settings.anchorFrame );
    }

    /**
     * Handler for the clicks
     */
    private PrivacyController privacyController = new PrivacyController();

    /**
     * Defines a logger for this class
     */
    private static final Logger logger = Logger.getLogger( PrivacyJFrame.class.getName() );


    /**
     * Creates the GUI widgets.
     */
    private void initGui() {
        final MigLayout layout = new MigLayout( "insets 10" );
        final JPanel privacyPanel = new JPanel( layout );


        final JLabel hello = new JLabel( "Privacy Settings" );
        final Font titleFont = new Font( "SansSerif", Font.BOLD, 14 );
        hello.setFont( titleFont );
        hello.setForeground( Color.blue );
        privacyPanel.add( hello, "wrap" );

        final JCheckBox clearRecentFiles = new JCheckBox( "Clear Recent Files" );
        privacyPanel.add( clearRecentFiles );
        final JButton clearRecentFilesButton = new JButton( "clear" );
        clearRecentFilesButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                privacyController.clearRecentFiles();
            }
        } );
        privacyPanel.add( clearRecentFilesButton, "wrap" );

        final JCheckBox clearThumbnails = new JCheckBox( "Clear Thumbnails" );
        privacyPanel.add( clearThumbnails );
        final JButton clearThumbnailsButton = new JButton( "clear" );
        clearThumbnailsButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                privacyController.clearThumbnails();
            }
        } );
        privacyPanel.add( clearThumbnailsButton, "wrap" );

        final JCheckBox clearAutoload = new JCheckBox( "Clear Autoload" );
        privacyPanel.add( clearAutoload );
        final JButton clearAutoloadButton = new JButton( "clear" );
        clearAutoloadButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                privacyController.clearAutoload();
            }
        } );
        privacyPanel.add( clearAutoloadButton, "wrap" );

        final JCheckBox clearMemorisedDirs = new JCheckBox( "Clear Memorised Directories" );
        privacyPanel.add( clearMemorisedDirs );
        final JButton clearMemorisedDirsButton = new JButton( "clear" );
        clearMemorisedDirsButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                privacyController.clearMemorisedDirs();
            }
        } );
        privacyPanel.add( clearMemorisedDirsButton, "wrap" );

        final JButton selected = new JButton( "Selected" );
        selected.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                privacyController.clearSelected( clearRecentFiles.isSelected(), clearThumbnails.isSelected(), clearAutoload.isSelected(), clearMemorisedDirs.isSelected() );
            }
        } );
        privacyPanel.add( selected, "split 2" );

        final JButton cancel = new JButton( "Cancel" );
        cancel.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                getRid();
            }
        } );
        privacyPanel.add( cancel );

        final JButton all = new JButton( "All" );
        all.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                privacyController.clearAll();
            }
        } );
        privacyPanel.add( all );




        getContentPane().add( privacyPanel );

        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                getRid();
            }
        } );
    }


    /**
     * closes the window and releases the resources.
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }

    /**
     * A Controller that handles the clicks on the buttons of the Privacy JFrame.
     * Implemented as an inner class as it is quite specific to the GUI but
     * I wanted to maintain a semblance of MVC.
     */
    private class PrivacyController {

        /**
         * Defines a logger for this class
         */
        private Logger logger = Logger.getLogger( getClass().getName() );


        /**
         * Handles a click on the clear All button
         */
        public void clearAll() {
            logger.info( "Click all" );
            clearRecentFiles();
            clearThumbnails();
            clearAutoload();
            clearMemorisedDirs();
        }


        /**
         * Handles a click on the clear selected button.
         * @param clearRecentFiles  Wether to clear the recent files of not
         * @param clearThumbnails  Wether to clear the thumbnails
         * @param clearAutoload  Wether to clear the Autoload 
         * @param clearMemorisedDirs  Wether to clear the memorised locations
         */
        public void clearSelected( final boolean clearRecentFiles,
                final boolean clearThumbnails, final boolean clearAutoload,
                final boolean clearMemorisedDirs ) {
            if ( clearRecentFiles ) {
                clearRecentFiles();
            }
            if ( clearThumbnails ) {
                clearThumbnails();
            }
            if ( clearAutoload ) {
                clearAutoload();
            }
            if ( clearMemorisedDirs ) {
                clearMemorisedDirs();
            }
        }


        /**
         * Handles a click on the clear Recent Files button
         */
        public void clearRecentFiles() {
            logger.info( "Should Clear all recent files" );
        }


        /**
         * Handles a click on the clear Thumbnails button
         */
        public void clearThumbnails() {
            logger.info( "Should Clear Thumbnails" );
        }


        /**
         * Handles a click on the clear Autoload button
         */
        public void clearAutoload() {
            logger.info( "Should Clear Autoload" );
        }


        /**
         * Handles a click on the clear Memorised Dirs button
         */
        public void clearMemorisedDirs() {
            logger.info( "Should Clear Memorised Directories" );
        }
    }
}
