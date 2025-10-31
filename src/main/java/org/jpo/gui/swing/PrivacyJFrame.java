package org.jpo.gui.swing;

import net.miginfocom.swing.MigLayout;
import org.jpo.gui.Settings;
import org.jpo.datamodel.Tools;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.RecentCollectionsChangedEvent;
import org.jpo.gui.JpoResources;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
Copyright (C) 2002-2025 Richard Eigenmann.
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
 * A dialog to clear private data from JPO
 * @author Richard Eigenmann
 */
public class PrivacyJFrame
        extends JFrame {


    /**
     * Constructs a Frame with the privacy options
     */
    public PrivacyJFrame() {
        super(JpoResources.getResource("PrivacyTitle"));
        Tools.checkEDT();
        initGui();
        pack();
        setVisible(true);
        setLocationRelativeTo(Settings.getAnchorFrame());
    }

    /**
     * Handler for the clicks
     */
    private final transient PrivacyController privacyController = new PrivacyController();


    /**
     * Creates the GUI widgets.
     */
    private void initGui() {
        final var layout = new MigLayout("insets 10");
        final var privacyPanel = new JPanel(layout);

        final var clearRecentFiles = new JCheckBox(JpoResources.getResource("PrivacyClearRecentFiles"));
        privacyPanel.add(clearRecentFiles);
        final var PRIVACY_CLEAR = JpoResources.getResource("PrivacyClear");
        final var clearRecentFilesButton = new JButton(PRIVACY_CLEAR);
        clearRecentFilesButton.addActionListener((ActionEvent e) -> privacyController.clearRecentFiles());
        privacyPanel.add(clearRecentFilesButton, "wrap");

        final var clearAutoload = new JCheckBox(JpoResources.getResource("PrivacyClearAutoload"));
        privacyPanel.add(clearAutoload);
        final var clearAutoloadButton = new JButton(PRIVACY_CLEAR);
        clearAutoloadButton.addActionListener((ActionEvent e) -> privacyController.clearAutoload());
        privacyPanel.add(clearAutoloadButton, "wrap");

        final var clearMemorisedDirs = new JCheckBox(JpoResources.getResource("PrivacyClearMemorisedDirs"));
        privacyPanel.add( clearMemorisedDirs );
        final var clearMemorisedDirsButton = new JButton(PRIVACY_CLEAR);
        clearMemorisedDirsButton.addActionListener(( ActionEvent e ) -> privacyController.clearMemorisedDirs());
        privacyPanel.add( clearMemorisedDirsButton, "wrap" );

        final var selected = new JButton(JpoResources.getResource("PrivacySelected"));
        selected.addActionListener(( ActionEvent e ) -> privacyController.clearSelected( clearRecentFiles.isSelected(), clearAutoload.isSelected(), clearMemorisedDirs.isSelected() ));
        privacyPanel.add( selected, "split 2" );

        final var all = new JButton(JpoResources.getResource("PrivacyAll"));
        all.addActionListener(( ActionEvent e ) -> privacyController.clearAll());
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
     * Implemented as an inner class as it is quite specific to the GUI, but
     * I wanted to maintain a semblance of MVC.
     */
    private static class PrivacyController {


        /**
         * Handles a click on the clear All button
         */
        public void clearAll() {
            clearRecentFiles();
            clearAutoload();
            clearMemorisedDirs();
        }


        /**
         * Handles a click on the clear selected button.
         * @param clearRecentFiles  Whether to clear the recent files of not
         * @param clearAutoload  Whether to clear Autoload
         * @param clearMemorisedDirs  Whether to clear the memorised locations
         */
        public void clearSelected( final boolean clearRecentFiles,
                final boolean clearAutoload,
                final boolean clearMemorisedDirs ) {
            if ( clearRecentFiles ) {
                clearRecentFiles();
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
