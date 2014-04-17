package jpo.gui.swing;

import com.google.common.eventbus.Subscribe;
import java.awt.BorderLayout;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import jpo.EventBus.CloseApplicationRequest;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.ShowGroupRequest;
import jpo.EventBus.ShowQueryRequest;
import jpo.EventBus.UnsavedUpdatesDialogRequest;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import jpo.gui.ApplicationJMenuBar;
import jpo.gui.CollectionJTreeController;
import jpo.gui.InfoPanelController;
import jpo.gui.TagCloudController;
import jpo.gui.ThumbnailsPanelController;

/*
 MainWindow.java:  main window of the JPO application

 Copyright (C) 2002 - 2014  Richard Eigenmann.
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
 * Main Window of the JPO application.
 *
 *
 * @author Richard Eigenmann, richard.eigenmann@gmail.com
 */
public class MainWindow extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( MainWindow.class.getName() );

    /**
     * Creates the JPO window and lays our the components
     */
    public MainWindow() {
        this.collectionTab = new CollectionJTreeController().getJScrollPane();
        this.searchesTab = new QueriesJTree().getJComponent();
        Tools.checkEDT();
        initComponents();
        registerOnEventBus();
        Settings.pictureCollection.getTreeModel().addTreeModelListener( new MainAppModelListener() );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
                JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new CloseApplicationRequest() ) );
            }
        } );

    }

    private void registerOnEventBus() {
        JpoEventBus.getInstance().register( this );
    }

    private void initComponents() {
        Settings.anchorFrame = this;
        try {
            final String Windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            UIManager.setLookAndFeel( Windows );
        } catch ( ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e ) {
            LOGGER.fine( "Could not set Look and Feel" );
        }
        setTitle( Settings.jpoResources.getString( "ApplicationTitle" ) );

        setMinimumSize( Settings.jpoJFrameMinimumSize );
        setPreferredSize( Settings.mainFrameDimensions );

        ApplicationJMenuBar menuBar = new ApplicationJMenuBar();
        setJMenuBar( menuBar );

        // Set Tooltipps to snappy mode
        ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay( 1500 );
        ttm.setInitialDelay( 100 );

        final JSplitPane leftSplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
        leftSplitPane.setDividerSize( Settings.dividerWidth );
        leftSplitPane.setOneTouchExpandable( true );
        leftSplitPane.setContinuousLayout( true );

        jpoNavigatorJTabbedPane.setMinimumSize( Settings.JPO_NAVIGATOR_JTABBEDPANE_MINIMUM_SIZE );
        jpoNavigatorJTabbedPane.setPreferredSize( Settings.jpoNavigatorJTabbedPanePreferredSize );

        InfoPanelController infoPanelController = new InfoPanelController();
        infoPanelController.getInfoPanel().addComponentListener( new ComponentAdapter() {

            @Override
            public void componentResized( ComponentEvent event ) {
                int leftDividerSpot = leftSplitPane.getDividerLocation();
                if ( leftDividerSpot != Settings.preferredLeftDividerSpot ) {
                    //LOGGER.info( String.format( "infoPanel was resized. Updating preferredLeftDividerSpot to: %d", leftDividerSpot ) );
                    Settings.preferredLeftDividerSpot = leftDividerSpot;
                    Settings.unsavedSettingChanges = true;
                }
            }
        } );

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab( "Word Cloud", new TagCloudController().getTagCloud() );
     

        final JScrollPane statsScroller = new JScrollPane( infoPanelController.getInfoPanel() );
        statsScroller.setWheelScrollingEnabled( true );
        statsScroller.getVerticalScrollBar().setUnitIncrement( 20 );
        tabbedPane.addTab( "Stats", statsScroller );

        leftSplitPane.setBottomComponent( tabbedPane );

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

        jpoNavigatorJTabbedPane.add( Settings.jpoResources.getString( "jpoTabbedPaneCollection" ), collectionTab );
        jpoNavigatorJTabbedPane.add( Settings.jpoResources.getString( "jpoTabbedPaneSearches" ), searchesTab );
        leftSplitPane.setTopComponent( jpoNavigatorJTabbedPane );

        // Set up the Thumbnail Pane
        masterSplitPane.setLeftComponent( leftSplitPane );
        masterSplitPane.setRightComponent( (new ThumbnailsPanelController()).getView() );

        leftSplitPane.setDividerLocation( Settings.preferredLeftDividerSpot );

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
     * Reference to the collection tab
     */
    private final JComponent collectionTab;

    /**
     * Reference to the searches tab
     */
    private final JComponent searchesTab;

    /**
     * The multi tab panel top left that allows the collection to be shown and
     * then the searches etc.
     */
    private final JTabbedPane jpoNavigatorJTabbedPane = new JTabbedPane();

    /**
     * If a ShowGroupRequest is seen we will switch the collection tab to the
     * foreground.
     *
     * @param request The ShowGroupRequest
     */
    @Subscribe
    public void handleShowGroupRequest( ShowGroupRequest request ) {
        tabToCollection();
    }

    /**
     * Instructs the MainWindow to show the collection in the left panel
     */
    private void tabToCollection() {
        jpoNavigatorJTabbedPane.setSelectedComponent( collectionTab );
    }

    /**
     * If a ShowQueryRequest is seen we will switch the query tab to the
     * foreground.
     *
     * @param request The query request
     */
    @Subscribe
    public void handleShowQueryRequest( ShowQueryRequest request ) {
        tabToSearches();
    }

    /**
     * Instructs the MainWindow to show the searches in the left panel
     */
    private void tabToSearches() {
        jpoNavigatorJTabbedPane.setSelectedComponent( searchesTab );
    }

    /**
     * This method updates the title of the MainWindow. In most operating
     * systems this is shown on the top of the window and in the taskbar. Note:
     * you must be on the EDT when calling this method.
     *
     * @param newTitle The new title of the Frame
     */
    public void updateApplicationTitle( final String newTitle ) {
        Tools.checkEDT();
        setTitle( newTitle );
    }

    /**
     * This method calls the {@link #updateApplicationTitle} method but can be
     * called if you don't know whether you are on the EDT or not.
     *
     * @param newTitle The new title of the Frame
     */
    public void updateApplicationTitleEDT( final String newTitle ) {
        if ( SwingUtilities.isEventDispatchThread() ) {
            updateApplicationTitle( newTitle );
        } else {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    updateApplicationTitle( newTitle );
                }
            };
            SwingUtilities.invokeLater( r );

        }
    }

    private class MainAppModelListener
            implements TreeModelListener {

        @Override
        public void treeNodesChanged( TreeModelEvent e ) {
            TreePath tp = e.getTreePath();
            LOGGER.fine( String.format( "The main app model listener trapped a tree node change event on the tree path: %s", tp.toString() ) );
            if ( tp.getPathCount() == 1 ) { //if the root node sent the event
                LOGGER.fine( "Since this is the root node we will update the ApplicationTitle" );

                updateApplicationTitle();
            }
        }

        @Override
        public void treeNodesInserted( TreeModelEvent e ) {
            // ignore
        }

        @Override
        public void treeNodesRemoved( TreeModelEvent e ) {
            // ignore, the root can't be removed ... Really?
        }

        @Override
        public void treeStructureChanged( TreeModelEvent e ) {
            TreePath tp = e.getTreePath();
            if ( tp.getPathCount() == 1 ) { //if the root node sent the event
                updateApplicationTitle();
            }
        }
    }

    /**
     * Sets the application title to the default tile based on the
     * Resourcebundle string ApplicationTitle and the file name of the loaded
     * xml file if any.
     */
    private void updateApplicationTitle() {
        final File xmlFile = Settings.pictureCollection.getXmlFile();
        if ( xmlFile != null ) {
            updateApplicationTitleEDT( Settings.jpoResources.getString( "ApplicationTitle" ) + ":  " + xmlFile.toString() );
        } else {
            updateApplicationTitleEDT( Settings.jpoResources.getString( "ApplicationTitle" ) );
        }
    }

}
