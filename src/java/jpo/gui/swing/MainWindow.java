package jpo.gui.swing;

import jpo.gui.*;
import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import jpo.dataModel.Settings;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Tools;

/*
MainWindow.java:  main window of the JPO application

Copyright (C) 2002 - 2009  Richard Eigenmann.
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
 * MainWindow is the main window of the JPO application. Based on the MCV pattern
 * this is just the View object. The Jpo object is the controller.
 *
 * <p><img src=../Overview.png border=0><p>
 *
 * It uses a PictureCollection to organise a hierarchical model of
 * <code>SortableDefaultMutableTreeNode</code>s that represent the structure of the collection.
 * Each node has an associated object of {@link GroupInfo} or {@link PictureInfo} type.
 *
 *
 * @author Richard Eigenmann, richard.eigenmann@gmail.com
 */
public class MainWindow
        extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( MainWindow.class.getName() );


    public MainWindow( ApplicationMenuInterface applicationController,
            JComponent navigationPanel, JComponent searchesJScrollPane,
            ThumbnailPanelController thumbnailJScrollPane, JComponent infoPanel ) {
        this.applicationController = applicationController;
        this.collectionTab = navigationPanel;
        this.searchesTab = searchesJScrollPane;
        this.thumbnailJScrollPane = thumbnailJScrollPane;
        Tools.checkEDT();
        try {
            //final String GTK = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            final String Windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            //final String Metal = "javax.swing.plaf.metal.MetalLookAndFeel";
            //final String CDE = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

            UIManager.setLookAndFeel( Windows );
        } catch ( Exception e ) {
            logger.fine( "Jpo.main: Could not set Look and Feel" );
        }
        //ScreenHelper.explainGraphicsEnvironment();

        Settings.anchorFrame = this;
        setTitle( Settings.jpoResources.getString( "ApplicationTitle" ) );


        setMinimumSize( Settings.jpoJFrameMinimumSize );
        setPreferredSize( Settings.mainFrameDimensions );

        //Create the menu bar.
        ApplicationJMenuBar menuBar = new ApplicationJMenuBar( applicationController );
        setJMenuBar( menuBar );

        // Set Tooltipps to snappy mode
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay( 600 );
        ttm.setInitialDelay( 100 );



        // Set up the Info Panel

        /**
         *  The pane that holds the main window. On the left will go the tree, on the
         *  right will go the thumbnails
         **/
        final JSplitPane leftSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        leftSplitPane.setDividerSize( Settings.dividerWidth );
        leftSplitPane.setOneTouchExpandable( true );
        leftSplitPane.setContinuousLayout( true );


        jpoNavigatorJTabbedPane.setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
        jpoNavigatorJTabbedPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        //leftSplitPane.setTopComponent( jpoNavigatorJTabbedPane );
        leftSplitPane.setBottomComponent( infoPanel );
        leftSplitPane.setDividerLocation( Settings.preferredLeftDividerSpot );
        /**
         *  The pane that holds the main window. On the left will go the tree, on the
         *  right will go the thumbnails
         **/
        final JSplitPane masterSplitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT );
        masterSplitPane.setDividerSize( Settings.dividerWidth );
        masterSplitPane.setOneTouchExpandable( true );
        masterSplitPane.setContinuousLayout( true );
        masterSplitPane.setDividerLocation( Settings.preferredMasterDividerSpot );


        //Add the split pane to this frame.
        getContentPane().add( masterSplitPane, BorderLayout.CENTER );
        pack();

        if ( Settings.maximiseJpoOnStartup ) {
            setExtendedState( MAXIMIZED_BOTH );
        }

        searchesJScrollPane.setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
        searchesJScrollPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        jpoNavigatorJTabbedPane.add( Settings.jpoResources.getString( "jpoTabbedPaneCollection" ), navigationPanel );
        jpoNavigatorJTabbedPane.add( Settings.jpoResources.getString( "jpoTabbedPaneSearches" ), searchesJScrollPane );
        leftSplitPane.setTopComponent( jpoNavigatorJTabbedPane );

        // Set up the Thumbnail Pane
        masterSplitPane.setLeftComponent( leftSplitPane );
        masterSplitPane.setRightComponent( thumbnailJScrollPane.getJScrollPane() );


        infoPanel.addComponentListener( new ComponentAdapter() {

            @Override
            public void componentResized( ComponentEvent event ) {
                int leftDividerSpot = leftSplitPane.getDividerLocation();
                if ( leftDividerSpot != Settings.preferredLeftDividerSpot ) {
                    Settings.preferredLeftDividerSpot = leftDividerSpot;
                    Settings.unsavedSettingChanges = true;
                }
            }
        } );

        jpoNavigatorJTabbedPane.addComponentListener( new ComponentAdapter() {

            @Override
            public void componentResized( ComponentEvent event ) {
                int dividerSpot = masterSplitPane.getDividerLocation();
                if ( dividerSpot != Settings.preferredMasterDividerSpot ) {
                    Settings.preferredMasterDividerSpot = dividerSpot;
                    Settings.unsavedSettingChanges = true;
                }
            }
        } );
        setVisible( true );
    }

    private ApplicationMenuInterface applicationController;

    /**
     * Reference to the collection tab
     */
    private JComponent collectionTab;

    /**
     * Reference to the searches tab
     */
    private JComponent searchesTab;

    private ThumbnailPanelController thumbnailJScrollPane;

    private JTabbedPane jpoNavigatorJTabbedPane = new JTabbedPane();


    /**
     * Instructs the MainWindow to show the collection in the left panel
     */
    public void tabToCollection() {
        jpoNavigatorJTabbedPane.setSelectedComponent( collectionTab );
    }


    /**
     * Instructs the MainWindow to show the searchesn in the left panel
     */
    public void tabToSearches() {
        jpoNavigatorJTabbedPane.setSelectedComponent( searchesTab );
    }


    /**
     * Swing EDT invoking method that sets the title of the Frame to the new name
     * @param newTitle The new title of the Frame
     */
    public void updateApplicationTitle( final String newTitle ) {
        Tools.checkEDT();
        setTitle( newTitle );
    }
}
