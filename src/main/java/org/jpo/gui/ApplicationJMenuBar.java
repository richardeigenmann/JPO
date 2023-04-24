package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;

/*
 Copyright (C) 2002-2023 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
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
     * Creates a menu object for use in the main frame of the application.
     */
    public ApplicationJMenuBar() {
        add(new FileJMenu());
        add(new EditJMenu());
        add(new ActionJMenu());
        add(new ExtrasJMenu());
        add(new HelpJMenu());
    }


    private static class FileJMenu extends JMenu {

        /**
         * Menu item that will request a File|New operation.
         */
        private final JMenuItem fileNewJMenuItem = new JMenuItem();

        /**
         * Menu item that will request a File|Add operation.
         */
        private final JMenuItem fileAddJMenuItem = new JMenuItem();

        /**
         * Menu item that allows the user to load a collection.
         */
        private final JMenuItem fileLoadJMenuItem = new JMenuItem();

        /**
         * Menu item that allows the user to load a collection recently used.
         */
        private final JMenu fileOpenRecentJMenu = new JMenu();

        /**
         * An array of recently opened collections.
         */
        private final JMenuItem[] recentOpenedFileJMenuItem = new JMenuItem[Settings.MAX_MEMORISE];

        /**
         * Menu item that allows the user to save the picture list.
         */
        private final JMenuItem fileSaveJMenuItem = new JMenuItem();

        /**
         * Menu item that allows the user to save the picture list to a new file.
         */
        private final JMenuItem fileSaveAsJMenuItem = new JMenuItem();

        /**
         * Menu item that allows the user to close the application.
         */
        private final JMenuItem fileExitJMenuItem = new JMenuItem();


        @Subscribe
        public void handleLocaleChangedEvent(final LocaleChangedEvent event) {
            setMenuTexts();
        }

        @Subscribe
        public void handleRecentCollectionsChangedEvent(RecentCollectionsChangedEvent event) {
            recentFilesChanged();
        }

        @Subscribe
        public void handleCollectionLockNotification(final CollectionLockNotification event) {
            setMenuVisibility();
        }

        private void setMenuVisibility() {
            final var pictureCollection = Settings.getPictureCollection();
            if (pictureCollection == null) {
                return;
            }
            fileAddJMenuItem.setVisible(pictureCollection.getAllowEdits());

        }

        public FileJMenu() {
            setMnemonic(KeyEvent.VK_F);

            fileNewJMenuItem.setMnemonic(KeyEvent.VK_N);
            fileNewJMenuItem.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.CTRL_DOWN_MASK));
            fileNewJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new UnsavedUpdatesDialogRequest(new StartNewCollectionRequest())));
            add(fileNewJMenuItem);

            fileAddJMenuItem.setMnemonic(KeyEvent.VK_A);
            fileAddJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new ChooseAndAddPicturesToGroupRequest(Settings.getPictureCollection().getRootNode())));
            add(fileAddJMenuItem);

            fileLoadJMenuItem.setMnemonic(KeyEvent.VK_O);
            fileLoadJMenuItem.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));
            fileLoadJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new UnsavedUpdatesDialogRequest(new FileLoadDialogRequest())));
            add(fileLoadJMenuItem);

            fileOpenRecentJMenu.setMnemonic(KeyEvent.VK_R);
            add(fileOpenRecentJMenu);

            for (var i = 0; i < Settings.MAX_MEMORISE; i++) {
                recentOpenedFileJMenuItem[i] = new JMenuItem();
                final int index = i;  // the anonymous inner class needs a final variable
                recentOpenedFileJMenuItem[i].addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new UnsavedUpdatesDialogRequest(new FileLoadRequest(new File(Settings.getRecentCollections()[index])))));
                recentOpenedFileJMenuItem[i].setVisible(false);
                recentOpenedFileJMenuItem[i].setAccelerator(KeyStroke.getKeyStroke("control " + Integer.toString(i).substring(1, 1)));
                fileOpenRecentJMenu.add(recentOpenedFileJMenuItem[i]);
            }

            fileSaveJMenuItem.setMnemonic(KeyEvent.VK_S);
            fileSaveJMenuItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));
            fileSaveJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new FileSaveRequest(null)));
            add(fileSaveJMenuItem);

            fileSaveAsJMenuItem.setMnemonic(KeyEvent.VK_A);
            fileSaveAsJMenuItem.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.ALT_DOWN_MASK));
            fileSaveAsJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new FileSaveAsRequest(null)));
            add(fileSaveAsJMenuItem);

            fileExitJMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
            fileExitJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new UnsavedUpdatesDialogRequest(new ShutdownApplicationRequest())));
            add(fileExitJMenuItem);

            setMenuTexts();
            recentFilesChanged();
            setMenuVisibility();
            JpoEventBus.getInstance().register(this);
        }

        /**
         * This menu sets the texts of the menu in the language defined by the
         * locale. The application needs to call this method when the user changes
         * the Locale in the Settings editor.
         */
        private void setMenuTexts() {
            setText(Settings.getJpoResources().getString("FileMenuText"));
            fileNewJMenuItem.setText(Settings.getJpoResources().getString("FileNewJMenuItem"));
            fileOpenRecentJMenu.setText(Settings.getJpoResources().getString("FileOpenRecentItemText"));
            fileLoadJMenuItem.setText(Settings.getJpoResources().getString("FileLoadMenuItemText"));
            fileAddJMenuItem.setText(Settings.getJpoResources().getString("FileAddMenuItemText"));
            fileSaveJMenuItem.setText(Settings.getJpoResources().getString("FileSaveMenuItemText"));
            fileSaveAsJMenuItem.setText(Settings.getJpoResources().getString("FileSaveAsMenuItemText"));
            fileExitJMenuItem.setText(Settings.getJpoResources().getString("FileExitMenuItemText"));
        }

        /**
         * Sets up the menu entries in the File|OpenRecent sub menu from the
         * recentCollections in Settings. Can be called by the interface from the
         * listener on the Settings object.
         */
        private void recentFilesChanged() {
            final Runnable runnable = () -> {
                for (var i = 0; i < Settings.getRecentCollections().length; i++) {
                    if (Settings.getRecentCollections()[i] != null) {
                        recentOpenedFileJMenuItem[i].setText((i + 1) + ": " + Settings.getRecentCollections()[i]);
                        recentOpenedFileJMenuItem[i].setVisible(true);
                    } else {
                        recentOpenedFileJMenuItem[i].setVisible(false);
                    }
                }
            };
            if (SwingUtilities.isEventDispatchThread()) {
                runnable.run();
            } else {
                SwingUtilities.invokeLater(runnable);
            }
        }
    }

    private static class EditJMenu extends JMenu {
        /**
         * Menu item that allows the user to set up his cameras.
         */
        private final JMenuItem editCamerasJMenuItem = new JMenuItem();

        /**
         * Menu item that allows the user to turn on or off the edit Mode
         */
        private final JMenuItem editModeJMenuItem = new JMenuItem();

        /**
         * Menu item that allows the user to change the application settings.
         */
        private final JMenuItem editSettingsJMenuItem = new JMenuItem();

        @Subscribe
        public void handleLocaleChangedEvent(final LocaleChangedEvent event) {
            setMenuTexts();
        }

        @Subscribe
        public void handleCollectionLockNotification(final CollectionLockNotification event) {
            setMenuVisibility();

            final var pictureCollection = Settings.getPictureCollection();
            if (pictureCollection == null) { return; }

            if (pictureCollection.getAllowEdits() ) {
                editModeJMenuItem.setText("Disable Editing");
            } else {
                editModeJMenuItem.setText("Enable Editing");
            }
        }

        private void setMenuVisibility() {
            final var pictureCollection = Settings.getPictureCollection();
            if (pictureCollection == null) {
                return;
            }
            editCamerasJMenuItem.setVisible(pictureCollection.getAllowEdits());
        }

        public EditJMenu() {
            setMnemonic(KeyEvent.VK_E);
            editCamerasJMenuItem.setMnemonic(KeyEvent.VK_D);
            editCamerasJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new EditCamerasRequest()));
            add(editCamerasJMenuItem);

            editModeJMenuItem.addActionListener((ActionEvent e) -> Settings.getPictureCollection().setAllowEdits(! Settings.getPictureCollection().getAllowEdits()));
            add(editModeJMenuItem);

            editSettingsJMenuItem.setMnemonic(KeyEvent.VK_S);
            editSettingsJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new EditSettingsRequest()));
            add(editSettingsJMenuItem);
            setMenuTexts();
            setMenuVisibility();

            JpoEventBus.getInstance().register(this);
        }

        /**
         * This menu sets the texts of the menu in the language defined by the
         * locale. The application needs to call this method when the user changes
         * the Locale in the Settings editor.
         */
        private void setMenuTexts() {
            setText(Settings.getJpoResources().getString("EditJMenuText"));
            editCamerasJMenuItem.setText(Settings.getJpoResources().getString("EditCamerasJMenuItem"));
            editSettingsJMenuItem.setText(Settings.getJpoResources().getString("EditSettingsMenuItemText"));
        }


    }

    private static class ActionJMenu extends JMenu {
        /**
         * Menu item that will request an Action | Send Email
         */
        private final JMenuItem emailJMenuItem = new JMenuItem();

        /**
         * Menu item that pops up an automatic slide show.
         */
        private final JMenuItem randomSlideshowJMenuItem = new JMenuItem();

        @Subscribe
        public void handleLocaleChangedEvent(final LocaleChangedEvent event) {
            setMenuTexts();
        }

        public ActionJMenu() {
            setMnemonic(KeyEvent.VK_A);
            emailJMenuItem.setMnemonic(KeyEvent.VK_E);
            emailJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new SendEmailRequest()));
            add(emailJMenuItem);

            randomSlideshowJMenuItem.setMnemonic(KeyEvent.VK_S);
            randomSlideshowJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new StartDoublePanelSlideshowRequest(Settings.getPictureCollection().getRootNode())));
            add(randomSlideshowJMenuItem);
            setMenuTexts();
            JpoEventBus.getInstance().register(this);

        }


        /**
         * This menu sets the texts of the menu in the language defined by the
         * locale. The application needs to call this method when the user changes
         * the Locale in the Settings editor.
         */
        private void setMenuTexts() {
            setText(Settings.getJpoResources().getString("actionJMenu"));
            emailJMenuItem.setText(Settings.getJpoResources().getString("emailJMenuItem"));
            randomSlideshowJMenuItem.setText(Settings.getJpoResources().getString("RandomSlideshowJMenuItem"));
        }
    }


    private static class ExtrasJMenu extends JMenu {
        /**
         * Menu item that calls the Check Directories item
         */
        private final JMenuItem editCheckDirectoriesJMenuItem = new JMenuItem();

        /**
         * Menu item that allows the user to have the collection integrity checked.
         */
        private final JMenuItem editCheckIntegrityJMenuItem = new JMenuItem();

        /**
         * Menu item that allows the user to find Duplicates
         */
        private final JMenuItem findDuplicatesJMenuItem = new JMenuItem();


        /**
         * Menu item that allows the user to change the categories.
         */
        private final JMenuItem editCategoriesJMenuItem = new JMenuItem();

        /**
         * Menu item that allows the user to start a new ThumbnailCreationThread
         */
        private final JMenuItem startThumbnailCreationThreadJMenuItem = new JMenuItem();

        @Subscribe
        public void handleLocaleChangedEvent(final LocaleChangedEvent event) {
            setMenuTexts();
        }

        @Subscribe
        public void handleCollectionLockNotification(final CollectionLockNotification event) {
            setVisible( Settings.getPictureCollection().getAllowEdits() );
        }


        public ExtrasJMenu() {
            setMnemonic(KeyEvent.VK_X);
            editCheckDirectoriesJMenuItem.setMnemonic(KeyEvent.VK_D);
            editCheckDirectoriesJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new CheckDirectoriesRequest()));
            add(editCheckDirectoriesJMenuItem);

            editCheckIntegrityJMenuItem.setMnemonic(KeyEvent.VK_C);
            editCheckIntegrityJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new CheckIntegrityRequest()));
            add(editCheckIntegrityJMenuItem);

            findDuplicatesJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new FindDuplicatesRequest()));
            add(findDuplicatesJMenuItem);

            editCategoriesJMenuItem.setMnemonic(KeyEvent.VK_D);
            editCategoriesJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenCategoryEditorRequest(Settings.getPictureCollection())));
            add(editCategoriesJMenuItem);

            startThumbnailCreationThreadJMenuItem.setMnemonic(KeyEvent.VK_T);
            startThumbnailCreationThreadJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new StartThumbnailCreationDaemonRequest()));
            add(startThumbnailCreationThreadJMenuItem);

            final var findBaseDirJMenuItem = new JMenuItem("Find Basedir");
            findBaseDirJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post((new FindBasedirRequest())));
            add(findBaseDirJMenuItem);

            final var scanHashCodeJMenuItem = new JMenuItem("Scan for checksums");
            scanHashCodeJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new StartHashCodeScannerRequest(Settings.getPictureCollection().getRootNode())));
            add(scanHashCodeJMenuItem);

            setMenuTexts();
            JpoEventBus.getInstance().register(this);
        }


        /**
         * This menu sets the texts of the menu in the language defined by the
         * locale. The application needs to call this method when the user changes
         * the Locale in the Settings editor.
         */
        private void setMenuTexts() {
            setText(Settings.getJpoResources().getString("ExtrasJMenu"));
            editCheckDirectoriesJMenuItem.setText(Settings.getJpoResources().getString("EditCheckDirectoriesJMenuItemText"));
            editCheckIntegrityJMenuItem.setText(Settings.getJpoResources().getString("EditCheckIntegrityJMenuItem"));
            findDuplicatesJMenuItem.setText(Settings.getJpoResources().getString("FindDuplicatesJMenuItem"));
            editCategoriesJMenuItem.setText(Settings.getJpoResources().getString("EditCategoriesJMenuItem"));
            startThumbnailCreationThreadJMenuItem.setText(Settings.getJpoResources().getString("StartThumbnailCreationThreadJMenuItem"));
        }

    }

    private static class HelpJMenu extends JMenu {


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
         * Menu item to check for updates
         */
        private final JMenuItem helpCheckForUpdatesJMenuItem = new JMenuItem();

        @Subscribe
        public void handleLocaleChangedEvent(final LocaleChangedEvent event) {
            setMenuTexts();
        }

        public HelpJMenu() {
            setMnemonic(KeyEvent.VK_H);
            helpAboutJMenuItem.setMnemonic(KeyEvent.VK_A);
            helpAboutJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenHelpAboutFrameRequest()));
            add(helpAboutJMenuItem);

            helpLicenseJMenuItem.setMnemonic(KeyEvent.VK_L);
            helpLicenseJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenLicenceFrameRequest()));
            add(helpLicenseJMenuItem);

            helpPrivacyJMenuItem.setMnemonic(KeyEvent.VK_P);
            helpPrivacyJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new OpenPrivacyFrameRequest()));
            add(helpPrivacyJMenuItem);

            helpResetWindowsJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RestoreDockablesPositionsRequest()));
            add(helpResetWindowsJMenuItem);

            helpCheckForUpdatesJMenuItem.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new CheckForUpdatesRequest(true)));
            add(helpCheckForUpdatesJMenuItem);
            setMenuTexts();
            JpoEventBus.getInstance().register(this);
        }

        /**
         * This menu sets the texts of the menu in the language defined by the
         * locale. The application needs to call this method when the user changes
         * the Locale in the Settings editor.
         */
        private void setMenuTexts() {
            setText(Settings.getJpoResources().getString("HelpJMenuText"));
            helpAboutJMenuItem.setText(Settings.getJpoResources().getString("HelpAboutMenuItemText"));
            helpLicenseJMenuItem.setText(Settings.getJpoResources().getString("HelpLicenseMenuItemText"));
            helpPrivacyJMenuItem.setText(Settings.getJpoResources().getString("HelpPrivacyMenuItemText"));
            helpResetWindowsJMenuItem.setText(Settings.getJpoResources().getString("HelpResetWindowsJMenuItem"));
            helpCheckForUpdatesJMenuItem.setText(Settings.getJpoResources().getString("HelpCheckForUpdates"));
        }

    }


}
