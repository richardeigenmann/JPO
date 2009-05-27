package jpo.gui;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import jpo.dataModel.Settings;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;

/*
MainWindow.java:  main window of the JPO application

Copyright (C) 2002-2009  Richard Eigenmann.
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
 * Jpo is the the window of the JPO application.
 *
 * >>TODO: out of date RE,25.5.2009 << The Jpo class creates the following main objects:
 *
 * <p><img src=../Overview.png border=0><p>
 *
 * It uses a list of pictures (PictureList file) to create a hierarchical model of
 * <code>SortableDefaultMutableTreeNode</code>s that represent the structure of the collection.
 * Each node has an associated object of {@link GroupInfo} or {@link PictureInfo} type.
 *
 * The {@link CollectionJTreeController} visualises the model and allows the user to
 * expand and collapse branches of the tree with the mouse. If a node is clicked this generates
 * a <code>valueChanged</code> event from the model which is sent to all listening objects.<p>
 *
 * Listening objects are the thumbnail pane which displays the group if a node of type
 * <code>GroupInfo</code> has been selected.<p>
 *
 * This listener architecture allows fairly easy expansion of the application
 * since all that is required is that any additional objects that need to be change the picture
 * or need to be informed of a change can connect to the model in this manner and
 * need no other controls.
 *
 * @author Richard Eigenmann, richard.eigenmann@gmail.com
 */
public class MainWindow extends JFrame {

    public MainWindow( ApplicationMenuInterface applicationController, CollectionJTreeController collectionJTreeController, ThumbnailJScrollPane thumbnailJScrollPane ) {
        if ( !SwingUtilities.isEventDispatchThread() ) {
            System.out.println( "MainWindow creation is not on EDT!" );
            Thread.dumpStack();
        }
        this.applicationController = applicationController;
        this.collectionJTreeController = collectionJTreeController;
        this.thumbnailJScrollPane = thumbnailJScrollPane;
        initComponents();
    }

    private ApplicationMenuInterface applicationController;

    private CollectionJTreeController collectionJTreeController;

    private ThumbnailJScrollPane thumbnailJScrollPane;


    /**
     *  This method initialises the GUI components of the main window.
     */
    private void initComponents() {
        try {
            //final String GTK = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
            final String Windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            //final String Metal = "javax.swing.plaf.metal.MetalLookAndFeel";
            //final String CDE = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";

            UIManager.setLookAndFeel( Windows );
        } catch ( Exception e ) {
            // System.out.println( "Jpo.main: Could not set Look and Feel");
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
        InfoPanel infoPanel = new InfoPanel();

        /**
         *  The pane that holds the main window. On the left will go the tree, on the
         *  right will go the thumbnails
         **/
        final JSplitPane leftSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        leftSplitPane.setDividerSize( Settings.dividerWidth );
        leftSplitPane.setOneTouchExpandable( true );

        final JTabbedPane jpoNavigatorJTabbedPane = new JTabbedPane();
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
        masterSplitPane.setDividerLocation( Settings.preferredMasterDividerSpot );


        //Add the split pane to this frame.
        getContentPane().add( masterSplitPane, BorderLayout.CENTER );
        pack();

        if ( Settings.maximiseJpoOnStartup ) {
            setExtendedState( MAXIMIZED_BOTH );
        }

        JScrollPane collectionJScrollPane = collectionJTreeController.getJScrollPane();
        collectionJScrollPane.setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
        collectionJScrollPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        QueriesJTree searchesJTree = new QueriesJTree();
        JScrollPane searchesJScrollPane = new JScrollPane( searchesJTree );
        searchesJScrollPane.setMinimumSize( Settings.jpoNavigatorJTabbedPaneMinimumSize );
        searchesJScrollPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        jpoNavigatorJTabbedPane.add( Settings.jpoResources.getString( "jpoTabbedPaneCollection" ), collectionJScrollPane );
        jpoNavigatorJTabbedPane.add( Settings.jpoResources.getString( "jpoTabbedPaneSearches" ), searchesJScrollPane );
        leftSplitPane.setTopComponent( jpoNavigatorJTabbedPane );

        // Set up the Thumbnail Pane
        masterSplitPane.setLeftComponent( leftSplitPane );
        masterSplitPane.setRightComponent( thumbnailJScrollPane.getJScrollPane() );


        // Set up the communication between the JTree and the Thumbnail Pane
        collectionJTreeController.setAssociatedThumbnailJScrollpane( thumbnailJScrollPane );
        collectionJTreeController.setAssociatedInfoPanel( infoPanel );
        searchesJTree.setAssociatedThumbnailJScrollpane( thumbnailJScrollPane );
        searchesJTree.setAssociatedInfoPanel( infoPanel );
        thumbnailJScrollPane.setAssociatedCollectionJTree( collectionJTreeController );
        thumbnailJScrollPane.setAssociatedInfoPanel( infoPanel );
        Settings.mainCollectionJTreeController = collectionJTreeController;

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


    /**
     * Swing EDT invoking method that sets the title of the Frame to the new name
     * @param newTitle The new title of the Frame
     */
    public void updateApplicationTitle( final String newTitle ) {
        Runnable r = new Runnable() {

            public void run() {
                setTitle( newTitle );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            new Thread( r ).start();
        }
    }
}
