package jpo.gui.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
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
        super( Settings.jpoResources.getString( "PrivacyTitle" ) );
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

        final JCheckBox clearRecentFiles = new JCheckBox( Settings.jpoResources.getString( "PrivacyClearRecentFiles" ) );
        privacyPanel.add( clearRecentFiles );
        final JButton clearRecentFilesButton = new JButton( Settings.jpoResources.getString( "PrivacyClear" ) );
        clearRecentFilesButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                privacyController.clearRecentFiles();
            }
        } );
        privacyPanel.add( clearRecentFilesButton, "wrap" );

        final JCheckBox clearThumbnails = new JCheckBox( Settings.jpoResources.getString( "PrivacyClearThumbnails" ) );
        privacyPanel.add( clearThumbnails );
        final JButton clearThumbnailsButton = new JButton( Settings.jpoResources.getString( "PrivacyClear" ) );
        clearThumbnailsButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                privacyController.clearThumbnails();
            }
        } );
        privacyPanel.add( clearThumbnailsButton, "wrap" );

        final JCheckBox clearAutoload = new JCheckBox( Settings.jpoResources.getString( "PrivacyClearAutoload" ) );
        privacyPanel.add( clearAutoload );
        final JButton clearAutoloadButton = new JButton( Settings.jpoResources.getString( "PrivacyClear" ) );
        clearAutoloadButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                privacyController.clearAutoload();
            }
        } );
        privacyPanel.add( clearAutoloadButton, "wrap" );

        final JCheckBox clearMemorisedDirs = new JCheckBox( Settings.jpoResources.getString( "PrivacyClearMemorisedDirs" ) );
        privacyPanel.add( clearMemorisedDirs );
        final JButton clearMemorisedDirsButton = new JButton( Settings.jpoResources.getString( "PrivacyClear" ) );
        clearMemorisedDirsButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                privacyController.clearMemorisedDirs();
            }
        } );
        privacyPanel.add( clearMemorisedDirsButton, "wrap" );

        final JButton selected = new JButton( Settings.jpoResources.getString( "PrivacySelected" ) );
        selected.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                privacyController.clearSelected( clearRecentFiles.isSelected(), clearThumbnails.isSelected(), clearAutoload.isSelected(), clearMemorisedDirs.isSelected() );
            }
        } );
        privacyPanel.add( selected, "split 2" );

        final JButton cancel = new JButton( Settings.jpoResources.getString( "PrivacyClose" ) );
        cancel.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                getRid();
            }
        } );
        privacyPanel.add( cancel );

        final JButton all = new JButton( Settings.jpoResources.getString( "PrivacyAll" ) );
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
            Settings.clearRecentCollection();
        }


        /**
         * Handles a click on the clear Thumbnails button
         * TODO: make this a SwingWorker and have a progress bar
         */
        public void clearThumbnails() {
            File thumbnailDir = Settings.thumbnailPath;
            FilenameFilter thumbnails = new FilenameFilter() {

                public boolean accept( File dir, String name ) {
                    boolean matches = name.matches( "^" + Settings.thumbnailPrefix + "[0-9]+[.]jpg$" );
                    //logger.info( String.format( "Considering: %s matches: %b", name, matches ) );
                    return matches;
                }
            };
            File[] deleteableThumbnails = thumbnailDir.listFiles( thumbnails );
            for ( File f : deleteableThumbnails ) {
                boolean success = f.delete();
                logger.info( String.format( "Success: %b for deleting %s ", success, f.toString() ) );
            }
        }


        /**
         * Handles a click on the clear Autoload button
         */
        public void clearAutoload() {
            Settings.clearAutoLoad();
        }


        /**
         * Handles a click on the clear Memorised Dirs button
         */
        public void clearMemorisedDirs() {
            Settings.clearCopyLocations();
        }
    }
}
