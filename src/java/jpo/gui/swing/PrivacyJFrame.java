package jpo.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.RecentCollectionsChangedEvent;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import jpo.gui.ProgressGui;
import net.miginfocom.swing.MigLayout;

/*
PrivacyJFrame.java:  a dialog to clear private data from JPO

Copyright (C) 2002 - 2014 Richard Eigenmann.
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
    private final PrivacyController privacyController = new PrivacyController();

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( PrivacyJFrame.class.getName() );


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

            @Override
            public void actionPerformed( ActionEvent e ) {
                privacyController.clearRecentFiles();
            }
        } );
        privacyPanel.add( clearRecentFilesButton, "wrap" );

        final JCheckBox clearThumbnails = new JCheckBox( Settings.jpoResources.getString( "PrivacyClearThumbnails" ) );
        privacyPanel.add( clearThumbnails );
        final JButton clearThumbnailsButton = new JButton( Settings.jpoResources.getString( "PrivacyClear" ) );
        clearThumbnailsButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                privacyController.clearThumbnails();
            }
        } );
        privacyPanel.add( clearThumbnailsButton, "wrap" );

        final JCheckBox clearAutoload = new JCheckBox( Settings.jpoResources.getString( "PrivacyClearAutoload" ) );
        privacyPanel.add( clearAutoload );
        final JButton clearAutoloadButton = new JButton( Settings.jpoResources.getString( "PrivacyClear" ) );
        clearAutoloadButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                privacyController.clearAutoload();
            }
        } );
        privacyPanel.add( clearAutoloadButton, "wrap" );

        final JCheckBox clearMemorisedDirs = new JCheckBox( Settings.jpoResources.getString( "PrivacyClearMemorisedDirs" ) );
        privacyPanel.add( clearMemorisedDirs );
        final JButton clearMemorisedDirsButton = new JButton( Settings.jpoResources.getString( "PrivacyClear" ) );
        clearMemorisedDirsButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                privacyController.clearMemorisedDirs();
            }
        } );
        privacyPanel.add( clearMemorisedDirsButton, "wrap" );

        final JButton selected = new JButton( Settings.jpoResources.getString( "PrivacySelected" ) );
        selected.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                privacyController.clearSelected( clearRecentFiles.isSelected(), clearThumbnails.isSelected(), clearAutoload.isSelected(), clearMemorisedDirs.isSelected() );
            }
        } );
        privacyPanel.add( selected, "split 2" );

        final JButton cancel = new JButton( Settings.jpoResources.getString( "PrivacyClose" ) );
        cancel.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                getRid();
            }
        } );
        privacyPanel.add( cancel );

        final JButton all = new JButton( Settings.jpoResources.getString( "PrivacyAll" ) );
        all.addActionListener( new ActionListener() {

            @Override
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
        private final Logger LOGGER = Logger.getLogger( getClass().getName() );


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
         * @param clearRecentFiles  Whether to clear the recent files of not
         * @param clearThumbnails  Whether to clear the thumbnailFilter
         * @param clearAutoload  Whether to clear the Autoload 
         * @param clearMemorisedDirs  Whether to clear the memorised locations
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
            JpoEventBus.getInstance().post( new RecentCollectionsChangedEvent() );
        }


        /**
         * Handles a click on the clear Thumbnails button
         */
        public void clearThumbnails() {
            new ThumbnailDeleter();
        }

        /**
         * This class extends a SwingWorker to provide a progress bar while deleting the thumbnailFilter.
         */
        private class ThumbnailDeleter
                extends SwingWorker<String, String> {

            /**
             *   This object holds a reference to the progress GUI for the user.
             */
            private final ProgressGui progGui;

            /**
             * An array of the files to delete.
             */
            File[] deleteableThumbnails;


            /**
             * Constructs the Thumbnail Deleter. Builds an array of the thumbnail
             * files and then uses the doInBackground() method to do the actual deletion.
             */
            public ThumbnailDeleter() {
                File thumbnailDir = Settings.thumbnailPath;
                FilenameFilter thumbnailFilter = new FilenameFilter() {

                    @Override
                    public boolean accept( File dir, String name ) {
                        boolean matches = name.matches( "^" + Settings.thumbnailPrefix + "[0-9]+[.]jpg$" );
                        //logger.info( String.format( "Considering: %s matches: %b", name, matches ) );
                        return matches;
                    }
                };
                deleteableThumbnails = thumbnailDir.listFiles( thumbnailFilter );

                int filesToDelete = deleteableThumbnails.length;
                progGui = new ProgressGui( filesToDelete,
                        Settings.jpoResources.getString( "PrivacyTumbProgBarTitle" ),
                        String.format( Settings.jpoResources.getString( "PrivacyTumbProgBarDone" ), filesToDelete ) );
                execute();
            }


            /**
             * This method deletes each file in the deleteableThumbnails
             * array and updates the progress bar while doing so.
             * It can be interrupted by clicking the cancel button.
             * @return The string "Done"
             * @throws Exception
             */
            @Override
            protected String doInBackground() throws Exception {

                for ( File f : deleteableThumbnails ) {
                    boolean success = f.delete();
                    LOGGER.fine( String.format( "Success: %b for deleting %s ", success, f.toString() ) );
                    publish( String.format( "Success: %b for deleting %s ", success, f.toString() ) );
                    if ( progGui.getInterruptor().getShouldInterrupt() ) {
                        progGui.setDoneString( Settings.jpoResources.getString( "htmlDistillerInterrupt" ) );
                        break;
                    }
                }
                return "Done";
            }


            /**
             * This method is called by SwingWorker when the background process
             * sends a publish.
             * @param messages A message that will be written to the logfile.
             */
            @Override
            protected void process( List<String> messages ) {
                for ( String message : messages ) {
                    //logger.info( String.format( "messge: %s", message ) );
                    progGui.progressIncrement();
                }
            }


            /**
             * SwingWorker calls here when the background task is done.
             */
            @Override
            protected void done() {
                progGui.switchToDoneMode();
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
