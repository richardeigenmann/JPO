package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.*;

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
     */
    private final JMenu fileJMenu = new JMenu();

    /**
     * The Edit menu which is part of the JMenuBar for the Jpo application.
     *
     *
     */
    private final JMenu editJMenu = new JMenu();

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
    private final JMenu helpJMenu = new JMenu();

    /**
     * Menu item that will request a File|New operation.
     *
     */
    private final JMenuItem fileNewJMenuItem = new JMenuItem();

    /**
     * Menu item that will request a File|Add operation.
     *
     */
    private final JMenuItem fileAddJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to load a collection.
     *
     */
    private final JMenuItem fileLoadJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to load a collection recently used.
     *
     */
    private final JMenu fileOpenRecentJMenu = new JMenu();

    /**
     * An array of recently opened collections.
     */
    private final JMenuItem[] recentOpenedFileJMenuItem = new JMenuItem[Settings.MAX_MEMORISE];

    /**
     * Menu item that allows the user to save the picture list.
     *
     */
    private final JMenuItem fileSaveJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to save the picture list to a new file.
     *
     */
    private final JMenuItem fileSaveAsJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to close the application.
     *
     */
    private final JMenuItem fileExitJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to search for pictures.
     *
     */
    private final JMenuItem editFindJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to set up his cameras.
     *
     */
    private final JMenuItem editCamerasJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to change the application settings.
     *
     */
    private final JMenuItem editSettingsJMenuItem = new JMenuItem();

    /**
     * Menu item that pops up an automatic slide show.
     */
    private final JMenuItem randomSlideshowJMenuItem = new JMenuItem();

    /**
     * Menu item that calls the Check Directories item
     *
     */
    private final JMenuItem editCheckDirectoriesJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to have the collection integrity checked.
     *
     */
    private final JMenuItem editCheckIntegrityJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to find Duplicates
     */
    private final JMenuItem findDuplicatesJMenuItem = new JMenuItem();


    /**
     * Menu item that allows the user to change the categories.
     *
     */
    private final JMenuItem editCategoriesJMenuItem = new JMenuItem();

    /**
     * Menu item that allows the user to start a new ThumbnailCreationThread
     *
     */
    private final JMenuItem startThumbnailCreationThreadJMenuItem = new JMenuItem();

    /**
     * Menu item that brings up the Help About screen.
     */
    private final JMenuItem helpAboutJMenuItem = new JMenuItem();

    /**
     * Menu item to bring up the license.
     */
    private final JMenuItem helpLicenseJMenuItem = new JMenuItem();

    /**
     * Menu item to bring up the privacy dialog.
     */
    private final JMenuItem helpPrivacyJMenuItem = new JMenuItem();

    /**
     * Menu item to reset the windows
     */
    private final JMenuItem helpResetWindowsJMenuItem = new JMenuItem();

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
        fileJMenu.setText(Settings.jpoResources.getString("FileMenuText"));
        fileNewJMenuItem.setText(Settings.jpoResources.getString("FileNewJMenuItem"));
        fileOpenRecentJMenu.setText(Settings.jpoResources.getString("FileOpenRecentItemText"));
        fileLoadJMenuItem.setText(Settings.jpoResources.getString("FileLoadMenuItemText"));
        fileAddJMenuItem.setText(Settings.jpoResources.getString("FileAddMenuItemText"));
        fileSaveJMenuItem.setText(Settings.jpoResources.getString("FileSaveMenuItemText"));
        fileSaveAsJMenuItem.setText(Settings.jpoResources.getString("FileSaveAsMenuItemText"));
        fileExitJMenuItem.setText(Settings.jpoResources.getString("FileExitMenuItemText"));

        editJMenu.setText(Settings.jpoResources.getString("EditJMenuText"));
        editFindJMenuItem.setText(Settings.jpoResources.getString("EditFindJMenuItemText"));
        editCamerasJMenuItem.setText(Settings.jpoResources.getString("EditCamerasJMenuItem"));
        editSettingsJMenuItem.setText(Settings.jpoResources.getString("EditSettingsMenuItemText"));

        actionJMenu.setText(Settings.jpoResources.getString("actionJMenu"));
        emailJMenuItem.setText(Settings.jpoResources.getString("emailJMenuItem"));
        randomSlideshowJMenuItem.setText(Settings.jpoResources.getString("RandomSlideshowJMenuItem"));

        ExtrasJMenu.setText(Settings.jpoResources.getString("ExtrasJMenu"));
        editCheckDirectoriesJMenuItem.setText(Settings.jpoResources.getString("EditCheckDirectoriesJMenuItemText"));
        editCheckIntegrityJMenuItem.setText(Settings.jpoResources.getString("EditCheckIntegrityJMenuItem"));
        findDuplicatesJMenuItem.setText(Settings.jpoResources.getString("FindDuplicatesJMenuItem"));
        editCategoriesJMenuItem.setText(Settings.jpoResources.getString("EditCategoriesJMenuItem"));
        startThumbnailCreationThreadJMenuItem.setText(Settings.jpoResources.getString("StartThumbnailCreationThreadJMenuItem"));

        helpJMenu.setText(Settings.jpoResources.getString("HelpJMenuText"));
        helpAboutJMenuItem.setText(Settings.jpoResources.getString("HelpAboutMenuItemText"));
        helpLicenseJMenuItem.setText(Settings.jpoResources.getString("HelpLicenseMenuItemText"));
        helpPrivacyJMenuItem.setText(Settings.jpoResources.getString("HelpPrivacyMenuItemText"));
        helpResetWindowsJMenuItem.setText(Settings.jpoResources.getString("HelpResetWindowsJMenuItem"));
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
            for (int i = 0; i < Settings.getRecentCollections().length; i++ ) {
                if ( Settings.getRecentCollections()[i] != null ) {
                    recentOpenedFileJMenuItem[i].setText((i + 1) + ": " + Settings.getRecentCollections()[i] );
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
        fileJMenu.setMnemonic(KeyEvent.VK_F);
        add(fileJMenu);

        fileNewJMenuItem.setMnemonic(KeyEvent.VK_N);
        fileNewJMenuItem.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
        fileNewJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new UnsavedUpdatesDialogRequest(new StartNewCollectionRequest())));
        fileJMenu.add(fileNewJMenuItem);

        fileAddJMenuItem.setMnemonic(KeyEvent.VK_A);
        fileAddJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ChooseAndAddPicturesToGroupRequest(Settings.getPictureCollection().getRootNode())));
        fileJMenu.add(fileAddJMenuItem);

        fileLoadJMenuItem.setMnemonic(KeyEvent.VK_O);
        fileLoadJMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
        fileLoadJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new UnsavedUpdatesDialogRequest(new FileLoadDialogRequest())));
        fileJMenu.add(fileLoadJMenuItem);

        fileOpenRecentJMenu.setMnemonic(KeyEvent.VK_R);
        fileJMenu.add(fileOpenRecentJMenu);

        for (int i = 0; i < Settings.MAX_MEMORISE; i++) {
            recentOpenedFileJMenuItem[i] = new JMenuItem();
            final int index = i;  // the anonymous inner class needs a final variable
            recentOpenedFileJMenuItem[i].addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new UnsavedUpdatesDialogRequest(new OpenRecentCollectionRequest(index))));
            recentOpenedFileJMenuItem[i].setVisible(false);
            recentOpenedFileJMenuItem[i].setAccelerator(KeyStroke.getKeyStroke("control " + Integer.toString(i).substring(1, 1)));
            fileOpenRecentJMenu.add(recentOpenedFileJMenuItem[i]);
        }

        fileSaveJMenuItem.setMnemonic(KeyEvent.VK_S);
        fileSaveJMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
        fileSaveJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new FileSaveRequest()));
        fileJMenu.add(fileSaveJMenuItem);

        fileSaveAsJMenuItem.setMnemonic(KeyEvent.VK_A);
        fileSaveAsJMenuItem.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.ALT_DOWN_MASK));
        fileSaveAsJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new FileSaveAsRequest()));
        fileJMenu.add(fileSaveAsJMenuItem);

        fileExitJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        fileExitJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new UnsavedUpdatesDialogRequest(new CloseApplicationRequest())));
        fileJMenu.add(fileExitJMenuItem);

        //Build the Edit menu.
        editJMenu.setMnemonic(KeyEvent.VK_E);
        add(editJMenu);

        editFindJMenuItem.setMnemonic(KeyEvent.VK_F);
        editFindJMenuItem.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_DOWN_MASK));
        editFindJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenSearchDialogRequest(Settings.getPictureCollection().getRootNode())));
        editJMenu.add(editFindJMenuItem);

        editCamerasJMenuItem.setMnemonic(KeyEvent.VK_D);
        editCamerasJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new EditCamerasRequest()));
        editJMenu.add(editCamerasJMenuItem);

        editSettingsJMenuItem.setMnemonic(KeyEvent.VK_S);
        editSettingsJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new EditSettingsRequest()));
        editJMenu.add(editSettingsJMenuItem);

        // Build the Action menu
        emailJMenuItem.setMnemonic(KeyEvent.VK_E);
        emailJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new SendEmailRequest()));
        actionJMenu.add(emailJMenuItem);

        randomSlideshowJMenuItem.setMnemonic(KeyEvent.VK_S);
        randomSlideshowJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new StartDoublePanelSlideshowRequest(Settings.getPictureCollection().getRootNode())));
        actionJMenu.add(randomSlideshowJMenuItem);

        actionJMenu.setMnemonic(KeyEvent.VK_A);
        add(actionJMenu);

        // Build the Extras menu.
        ExtrasJMenu.setMnemonic(KeyEvent.VK_X);
        add(ExtrasJMenu);
        editCheckDirectoriesJMenuItem.setMnemonic(KeyEvent.VK_D);
        editCheckDirectoriesJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new CheckDirectoriesRequest()));
        ExtrasJMenu.add(editCheckDirectoriesJMenuItem);

        editCheckIntegrityJMenuItem.setMnemonic(KeyEvent.VK_C);
        editCheckIntegrityJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new CheckIntegrityRequest()));
        ExtrasJMenu.add(editCheckIntegrityJMenuItem);

        findDuplicatesJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new FindDuplicatesRequest()));
        ExtrasJMenu.add(findDuplicatesJMenuItem);

        editCategoriesJMenuItem.setMnemonic(KeyEvent.VK_D);
        editCategoriesJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenCategoryEditorRequest()));
        ExtrasJMenu.add(editCategoriesJMenuItem);


        startThumbnailCreationThreadJMenuItem.setMnemonic(KeyEvent.VK_T);
        startThumbnailCreationThreadJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new StartThumbnailCreationFactoryRequest()));
        ExtrasJMenu.add(startThumbnailCreationThreadJMenuItem);


        // Build the Help menu.
        helpJMenu.setMnemonic(KeyEvent.VK_H);
        add(helpJMenu);

        helpAboutJMenuItem.setMnemonic(KeyEvent.VK_A);
        helpAboutJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenHelpAboutFrameRequest()));
        helpJMenu.add(helpAboutJMenuItem);

        helpLicenseJMenuItem.setMnemonic(KeyEvent.VK_L);
        helpLicenseJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenLicenceFrameRequest()));
        helpJMenu.add(helpLicenseJMenuItem);

        helpPrivacyJMenuItem.setMnemonic(KeyEvent.VK_P);
        helpPrivacyJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenPrivacyFrameRequest()));
        helpJMenu.add(helpPrivacyJMenuItem);

        helpResetWindowsJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RestoreDockablesPositionsRequest()));
        helpJMenu.add(helpResetWindowsJMenuItem);

    }

}
