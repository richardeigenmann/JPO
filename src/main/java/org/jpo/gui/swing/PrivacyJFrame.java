package org.jpo.gui.swing;

import net.miginfocom.swing.MigLayout;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.RecentCollectionsChangedEvent;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/*
Copyright (C) 2002 - 2019 Richard Eigenmann.
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
 * A dialog to clear private data from JPO
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
     * Creates the GUI widgets.
     */
    private void initGui() {
        final MigLayout layout = new MigLayout( "insets 10" );
        final JPanel privacyPanel = new JPanel( layout );

        final JCheckBox clearRecentFiles = new JCheckBox( Settings.jpoResources.getString( "PrivacyClearRecentFiles" ) );
        privacyPanel.add( clearRecentFiles );
        final JButton clearRecentFilesButton = new JButton( Settings.jpoResources.getString( "PrivacyClear" ) );
        clearRecentFilesButton.addActionListener(( ActionEvent e ) -> privacyController.clearRecentFiles());
        privacyPanel.add( clearRecentFilesButton, "wrap" );

        final JCheckBox clearAutoload = new JCheckBox( Settings.jpoResources.getString( "PrivacyClearAutoload" ) );
        privacyPanel.add( clearAutoload );
        final JButton clearAutoloadButton = new JButton( Settings.jpoResources.getString( "PrivacyClear" ) );
        clearAutoloadButton.addActionListener(( ActionEvent e ) -> privacyController.clearAutoload());
        privacyPanel.add( clearAutoloadButton, "wrap" );

        final JCheckBox clearMemorisedDirs = new JCheckBox( Settings.jpoResources.getString( "PrivacyClearMemorisedDirs" ) );
        privacyPanel.add( clearMemorisedDirs );
        final JButton clearMemorisedDirsButton = new JButton( Settings.jpoResources.getString( "PrivacyClear" ) );
        clearMemorisedDirsButton.addActionListener(( ActionEvent e ) -> privacyController.clearMemorisedDirs());
        privacyPanel.add( clearMemorisedDirsButton, "wrap" );

        final JButton selected = new JButton( Settings.jpoResources.getString( "PrivacySelected" ) );
        selected.addActionListener(( ActionEvent e ) -> privacyController.clearSelected( clearRecentFiles.isSelected(), clearAutoload.isSelected(), clearMemorisedDirs.isSelected() ));
        privacyPanel.add( selected, "split 2" );

        /*final JButton cancel = new JButton( Settings.jpoResources.getString( "PrivacyClose" ) );
        cancel.addActionListener(( ActionEvent e ) -> {
            getRid();
        });
        privacyPanel.add( cancel );*/

        final JButton all = new JButton( Settings.jpoResources.getString( "PrivacyAll" ) );
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
     * Implemented as an inner class as it is quite specific to the GUI but
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
         * @param clearAutoload  Whether to clear the Autoload 
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
