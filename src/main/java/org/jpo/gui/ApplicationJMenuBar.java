package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.eventbus.*;
import org.jpo.datamodel.Settings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/*
 ApplicationJMenuBar.java:  main menu for the application

 Copyright (C) 2002 -2019 Richard Eigenmann.
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
 * Creates the menu for the JPO application. Listens to
 * RecentCollectionsChangedEvent and LocaleChangedEvent events and updates
 * itself accordingly. It fires events into the JpoEventBus as per the user
 * selections.
 *
 * @author Richard Eigenmann
 */
public class ApplicationJMenuBar extends JMenuBar {

    /**
     * The File menu which is part of the JMenuBar for the Jpo application.
     *
     */
    private final JMenu FileJMenu = new JMenu();

    /**
     * The Edit menu which is part of the JMenuBar for the Jpo application.
     *
     *
     */
    private final JMenu EditJMenu = new JMenu();

    /**
     * The Action menu which is part of the JMenuBar for the Jpo application.
     *
     */
    private final JMenu actionJMenu = new JMenu();

    /**
     * Menu item that will request a Action | Send Email
     *
     */
    private final JMenuItem emailJMenuItem = new JMenuItem();

    /**
     * The extras menu which is part of the JMenuBar for the Jpo application.
     *
     */
    private final JMenu ExtrasJMenu = new JMenu();

    /**
     * The help menu which is part of the JMenuBar for the Jpo application.
     *
     */
    private final JMenu HelpJMenu = new JMenu();

    /**
     * Menu item that will request a File|New operation.
     *
     */
    private final JMenuItem FileNewJMenuItem = new JMenuItem();

    /**
     * Menu item that will request a File|Add operation.
     *
     */
    private final JMenuItem FileAddJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to load a collection.
     *
     */
    private final JMenuItem FileLoadJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to load a collection recently used.
     *
     */
    private final JMenu FileOpenRecentJMenu = new JMenu();

    /**
     * An array of recently opened collections.
     */
    private final JMenuItem[] recentOpenedFileJMenuItem = new JMenuItem[Settings.MAX_MEMORISE];

    /**
     * Menu item that allows the user to save the picture list.
     *
     */
    private final JMenuItem FileSaveJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to save the picture list to a new file.
     *
     */
    private final JMenuItem FileSaveAsJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to close the application.
     *
     */
    private final JMenuItem FileExitJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to search for pictures.
     *
     */
    private final JMenuItem EditFindJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to set up his cameras.
     *
     */
    private final JMenuItem EditCamerasJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to change the application settings.
     *
     */
    private final JMenuItem EditSettingsJMenuItem = new JMenuItem();

    /**
     * Menu item that pops up an automatic slide show.
     */
    private final JMenuItem RandomSlideshowJMenuItem = new JMenuItem();

    /**
     * Menu item that calls the Check Directories item
     *
     */
    private final JMenuItem EditCheckDirectoriesJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to have the collection integrity checked.
     *
     */
    private final JMenuItem EditCheckIntegrityJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to find Duplicates
     */
    private final JMenuItem FindDuplicatesJMenuItem = new JMenuItem();


    /**
     * Menu item that allows the user to change the categories.
     *
     */
    private final JMenuItem EditCategoriesJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to start a new ThumbnailCreationThread
     *
     */
    private final JMenuItem StartThumbnailCreationThreadJMenuItem = new JMenuItem();

    /**
     * Menu item that brings up the Help About screen.
     */
    private final JMenuItem HelpAboutJMenuItem = new JMenuItem();

    /**
     * Menu item to bring up the license.
     */
    private final JMenuItem HelpLicenseJMenuItem = new JMenuItem();

    /**
     * Menu item to bring up the privacy dialog.
     */
    private final JMenuItem HelpPrivacyJMenuItem = new JMenuItem();

    /**
     * Menu item to reset the windows
     */
    private final JMenuItem HelpResetWindowsJMenuItem = new JMenuItem();

    /**
     * Creates a menu object for use in the main frame of the application.
     *
     */
    public ApplicationJMenuBar() {
        init();

        setMenuTexts();
        recentFilesChanged();

        JpoEventBus.getInstance().register( new LocaleChangedEventHandler() );
        JpoEventBus.getInstance().register( new RecentCollectionsChangedEventHandler() );
    }

    /**
     * Handler for the LocationChangedEvent.
     */
    private class LocaleChangedEventHandler {

        /**
         * Handle the event by updating the menu texts
         *
         * @param event The event
         */
        @Subscribe
        public void handleLocaleChangedEvent( LocaleChangedEvent event ) {
            setMenuTexts();
        }
    }

    /**
     * This menu sets the texts of the menu in the language defined by the
     * locale. The application needs to call this method when the user changes
     * the Locale in the Settings editor.
     */
    private void setMenuTexts() {
        FileJMenu.setText( Settings.jpoResources.getString( "FileMenuText" ) );
        FileNewJMenuItem.setText( Settings.jpoResources.getString( "FileNewJMenuItem" ) );
        FileOpenRecentJMenu.setText( Settings.jpoResources.getString( "FileOpenRecentItemText" ) );
        FileLoadJMenuItem.setText( Settings.jpoResources.getString( "FileLoadMenuItemText" ) );
        FileAddJMenuItem.setText( Settings.jpoResources.getString( "FileAddMenuItemText" ) );
        FileSaveJMenuItem.setText( Settings.jpoResources.getString( "FileSaveMenuItemText" ) );
        FileSaveAsJMenuItem.setText( Settings.jpoResources.getString( "FileSaveAsMenuItemText" ) );
        FileExitJMenuItem.setText( Settings.jpoResources.getString( "FileExitMenuItemText" ) );

        EditJMenu.setText( Settings.jpoResources.getString( "EditJMenuText" ) );
        EditFindJMenuItem.setText( Settings.jpoResources.getString( "EditFindJMenuItemText" ) );
        EditCamerasJMenuItem.setText( Settings.jpoResources.getString( "EditCamerasJMenuItem" ) );
        EditSettingsJMenuItem.setText( Settings.jpoResources.getString( "EditSettingsMenuItemText" ) );

        actionJMenu.setText( Settings.jpoResources.getString( "actionJMenu" ) );
        emailJMenuItem.setText( Settings.jpoResources.getString( "emailJMenuItem" ) );
        RandomSlideshowJMenuItem.setText( Settings.jpoResources.getString( "RandomSlideshowJMenuItem" ) );

        ExtrasJMenu.setText( Settings.jpoResources.getString("ExtrasJMenu" ));
        EditCheckDirectoriesJMenuItem.setText( Settings.jpoResources.getString( "EditCheckDirectoriesJMenuItemText" ) );
        EditCheckIntegrityJMenuItem.setText( Settings.jpoResources.getString( "EditCheckIntegrityJMenuItem" ) );
        FindDuplicatesJMenuItem.setText(Settings.jpoResources.getString( "FindDuplicatesJMenuItem" ) );
        EditCategoriesJMenuItem.setText( Settings.jpoResources.getString( "EditCategoriesJMenuItem" ) );
        StartThumbnailCreationThreadJMenuItem.setText( Settings.jpoResources.getString( "StartThumbnailCreationThreadJMenuItem" ));

        HelpJMenu.setText( Settings.jpoResources.getString( "HelpJMenuText" ) );
        HelpAboutJMenuItem.setText( Settings.jpoResources.getString( "HelpAboutMenuItemText" ) );
        HelpLicenseJMenuItem.setText( Settings.jpoResources.getString( "HelpLicenseMenuItemText" ) );
        HelpPrivacyJMenuItem.setText( Settings.jpoResources.getString( "HelpPrivacyMenuItemText" ) );
        HelpResetWindowsJMenuItem.setText( Settings.jpoResources.getString("HelpResetWindowsJMenuItem" ));
    }

    /**
     * Handler for the RecentCollectionsChangedEvent
     */
    private class RecentCollectionsChangedEventHandler {

        /**
         * Handle the event by updating the submenu items
         *
         * @param event The event
         */
        @Subscribe
        public void handleRecentCollectionsChangedEvent( RecentCollectionsChangedEvent event ) {
            recentFilesChanged();
        }
    }

    /**
     * Sets up the menu entries in the File|OpenRecent sub menu from the
     * recentCollections in Settings. Can be called by the interface from the
     * listener on the Settings object.
     */
    private void recentFilesChanged() {
        Runnable runnable = () -> {
            for ( int i = 0; i < Settings.recentCollections.length; i++ ) {
                if ( Settings.recentCollections[i] != null ) {
                    recentOpenedFileJMenuItem[i].setText((i + 1) + ": " + Settings.recentCollections[i] );
                    recentOpenedFileJMenuItem[i].setVisible( true );
                } else {
                    recentOpenedFileJMenuItem[i].setVisible( false );
                }
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable );
        }
    }

    private void init() {
        //Build the file menu.
        FileJMenu.setMnemonic( KeyEvent.VK_F );
        add( FileJMenu );

        FileNewJMenuItem.setMnemonic( KeyEvent.VK_N );
        FileNewJMenuItem.setAccelerator( KeyStroke.getKeyStroke( 'N', InputEvent.CTRL_DOWN_MASK ) );
        FileNewJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new StartNewCollectionRequest() ) ));
        FileJMenu.add( FileNewJMenuItem );

        FileAddJMenuItem.setMnemonic( KeyEvent.VK_A );
        FileAddJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new ChooseAndAddPicturesToGroupRequest( Settings.getPictureCollection().getRootNode() ) ));
        FileJMenu.add( FileAddJMenuItem );

        FileLoadJMenuItem.setMnemonic( KeyEvent.VK_O );
        FileLoadJMenuItem.setAccelerator( KeyStroke.getKeyStroke( 'O', InputEvent.CTRL_DOWN_MASK ) );
        FileLoadJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new FileLoadDialogRequest() ) ));
        FileJMenu.add( FileLoadJMenuItem );

        FileOpenRecentJMenu.setMnemonic( KeyEvent.VK_R );
        FileJMenu.add( FileOpenRecentJMenu );

        for ( int i = 0; i < Settings.MAX_MEMORISE; i++ ) {
            recentOpenedFileJMenuItem[i] = new JMenuItem();
            final int index = i;  // the anonymous inner class needs a final variable
            recentOpenedFileJMenuItem[i].addActionListener((ActionEvent e ) -> JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new OpenRecentCollectionRequest( index ) ) ));
            recentOpenedFileJMenuItem[i].setVisible( false );
            recentOpenedFileJMenuItem[i].setAccelerator( KeyStroke.getKeyStroke( "control " + Integer.toString( i ).substring( 1, 1 ) ) );
            FileOpenRecentJMenu.add( recentOpenedFileJMenuItem[i] );
        }

        FileSaveJMenuItem.setMnemonic( KeyEvent.VK_S );
        FileSaveJMenuItem.setAccelerator( KeyStroke.getKeyStroke( 'S', InputEvent.CTRL_DOWN_MASK ) );
        FileSaveJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new FileSaveRequest() ));
        FileJMenu.add( FileSaveJMenuItem );

        FileSaveAsJMenuItem.setMnemonic( KeyEvent.VK_A );
        FileSaveAsJMenuItem.setAccelerator( KeyStroke.getKeyStroke( 'A', InputEvent.ALT_DOWN_MASK ) );
        FileSaveAsJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new FileSaveAsRequest() ));
        FileJMenu.add( FileSaveAsJMenuItem );

        FileExitJMenuItem.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK ) );
        FileExitJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new CloseApplicationRequest() ) ));
        FileJMenu.add( FileExitJMenuItem );

        //Build the Edit menu.
        EditJMenu.setMnemonic( KeyEvent.VK_E );
        add( EditJMenu );

        EditFindJMenuItem.setMnemonic( KeyEvent.VK_F );
        EditFindJMenuItem.setAccelerator( KeyStroke.getKeyStroke( 'F', InputEvent.CTRL_DOWN_MASK ) );
        EditFindJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new OpenSearchDialogRequest( Settings.getPictureCollection().getRootNode() ) ));
        EditJMenu.add( EditFindJMenuItem );

        EditCamerasJMenuItem.setMnemonic( KeyEvent.VK_D );
        EditCamerasJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new EditCamerasRequest() ));
        EditJMenu.add( EditCamerasJMenuItem );

        EditSettingsJMenuItem.setMnemonic( KeyEvent.VK_S );
        EditSettingsJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new EditSettingsRequest() ));
        EditJMenu.add( EditSettingsJMenuItem );

        // Build the Action menu
        emailJMenuItem.setMnemonic( KeyEvent.VK_E );
        emailJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new SendEmailRequest() ));
        actionJMenu.add( emailJMenuItem );

        RandomSlideshowJMenuItem.setMnemonic( KeyEvent.VK_S );
        RandomSlideshowJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new StartDoublePanelSlideshowRequest( Settings.getPictureCollection().getRootNode() ) ));
        actionJMenu.add( RandomSlideshowJMenuItem );

        actionJMenu.setMnemonic( KeyEvent.VK_A );
        add( actionJMenu );

        // Build the Extras menu.
        ExtrasJMenu.setMnemonic( KeyEvent.VK_X );
        add( ExtrasJMenu );
        EditCheckDirectoriesJMenuItem.setMnemonic( KeyEvent.VK_D );
        EditCheckDirectoriesJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new CheckDirectoriesRequest() ));
        ExtrasJMenu.add( EditCheckDirectoriesJMenuItem );

        EditCheckIntegrityJMenuItem.setMnemonic( KeyEvent.VK_C );
        EditCheckIntegrityJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new CheckIntegrityRequest() ));
        ExtrasJMenu.add( EditCheckIntegrityJMenuItem );

        FindDuplicatesJMenuItem.addActionListener((ActionEvent e ) -> JpoEventBus.getInstance().post( new FindDuplicatesRequest() ));
        ExtrasJMenu.add(FindDuplicatesJMenuItem);

        EditCategoriesJMenuItem.setMnemonic( KeyEvent.VK_D );
        EditCategoriesJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new OpenCategoryEditorRequest() ));
        ExtrasJMenu.add( EditCategoriesJMenuItem );


        StartThumbnailCreationThreadJMenuItem.setMnemonic( KeyEvent.VK_T );
        StartThumbnailCreationThreadJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new StartThumbnailCreationFactoryRequest() ));
        ExtrasJMenu.add( StartThumbnailCreationThreadJMenuItem );


        // Build the Help menu.
        HelpJMenu.setMnemonic( KeyEvent.VK_H );
        add( HelpJMenu );

        HelpAboutJMenuItem.setMnemonic( KeyEvent.VK_A );
        HelpAboutJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new OpenHelpAboutFrameRequest() ));
        HelpJMenu.add( HelpAboutJMenuItem );

        HelpLicenseJMenuItem.setMnemonic( KeyEvent.VK_L );
        HelpLicenseJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new OpenLicenceFrameRequest() ));
        HelpJMenu.add( HelpLicenseJMenuItem );

        HelpPrivacyJMenuItem.setMnemonic( KeyEvent.VK_P );
        HelpPrivacyJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new OpenPrivacyFrameRequest() ));
        HelpJMenu.add( HelpPrivacyJMenuItem );

        HelpResetWindowsJMenuItem.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new RestoreDockablesPositionsRequest() ));
        HelpJMenu.add( HelpResetWindowsJMenuItem );

    }

}
