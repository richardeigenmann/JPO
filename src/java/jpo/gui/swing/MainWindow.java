package jpo.gui.swing;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import com.google.common.eventbus.Subscribe;
import java.awt.Component;
import static java.awt.Frame.MAXIMIZED_BOTH;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
import jpo.EventBus.LoadDockablesPositionsRequest;
import jpo.EventBus.RestoreDockablesPositionsRequest;
import jpo.EventBus.SaveDockablesPositionsRequest;
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

 Copyright (C) 2002 - 2015  Richard Eigenmann.
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
 * Main Window of the JPO application. It uses the
 * {@link http://dock.javaforge.com Docking Frames} framework to handle the
 * internal windows. * @author Richard Eigenmann
 */
public class MainWindow extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( MainWindow.class.getName() );

    /**
     * The controller for the Docking Frames framework that controls all the
     * internal windows.
     */
    private final CControl control;

    /**
     * The grid to which all components are added
     */
    final CGrid grid;

    /**
     * Creates the JPO window and lays our the components
     */
    public MainWindow() {
        // Set up Docking Frames
        control = new CControl( this );
        grid = new CGrid( control );

        Settings.setMainWindow( this );

        Tools.checkEDT();
        initComponents();
        registerOnEventBus();
        Settings.getPictureCollection().getTreeModel().addTreeModelListener( new MainAppModelListener() );
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
                JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new CloseApplicationRequest() ) );
            }
        } );

    }

    /**
     * Registers the MainWindow on the event bus. Main Window handles the
     * following requests:
     *
     * @see ShowGroupRequest
     * @see ShowQueryRequest
     * @see SaveDockablesPositionsRequest
     * @see LoadDockablesPositionsRequest
     * @see RestoreDockablesPositionsRequest
     *
     */
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

        InfoPanelController infoPanelController = new InfoPanelController();
        final JScrollPane statsScroller = new JScrollPane( infoPanelController.getInfoPanel() );
        statsScroller.setWheelScrollingEnabled( true );
        statsScroller.getVerticalScrollBar().setUnitIncrement( 20 );
        pack();

        if ( Settings.maximiseJpoOnStartup ) {
            setExtendedState( MAXIMIZED_BOTH );
        }

        Component thumbnailPanel = ( new ThumbnailsPanelController() ).getView();

        tree = new DefaultSingleCDockable( "TreeId",
                Settings.jpoResources.getString( "jpoTabbedPaneCollection" ),
                new CollectionJTreeController().getJScrollPane() );
        searches = new DefaultSingleCDockable( "SearchId",
                Settings.jpoResources.getString( "jpoTabbedPaneSearches" ),
                new QueriesJTree().getJComponent() );

        JButton loadJButton = new JButton( "Properties - Load" );
        loadJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new LoadDockablesPositionsRequest() );
            }
        } );
        JButton saveJbutton = new JButton( "Save" );
        saveJbutton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new SaveDockablesPositionsRequest() );
            }
        } );
        JButton resetJbutton = new JButton( "Reset" );
        resetJbutton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                JpoEventBus.getInstance().post( new RestoreDockablesPositionsRequest() );
            }
        } );

        JPanel propertiesJPanel = new JPanel();
        propertiesJPanel.setLayout( new BoxLayout( propertiesJPanel, BoxLayout.Y_AXIS ) );
        propertiesJPanel.add( loadJButton );
        propertiesJPanel.add( saveJbutton );
        propertiesJPanel.add( resetJbutton );

        SingleCDockable properties = new DefaultSingleCDockable( "PropertiesId", "Properties", propertiesJPanel );

        SingleCDockable map = new DefaultSingleCDockable( "MapId", "Map", new JLabel( "a map would go here" ) );
        SingleCDockable tagDockable = new DefaultSingleCDockable( "TagId", "TagCloud", new TagCloudController().getTagCloud() );
        SingleCDockable statsDockable = new DefaultSingleCDockable( "StatsId", "Stats", statsScroller );
        SingleCDockable thumbnailsDockable = new DefaultSingleCDockable( "ThumbnailsId", "Thumbnails", thumbnailPanel );

        grid.add( 0, 0, 0.2, 0.8, tree );
        grid.add( 0, 0, 0.2, 0.8, searches );
        grid.add( 0, 1, 0.2, 0.2, tagDockable );
        grid.add( 0, 1, 0.2, 0.2, statsDockable );
        grid.add( 1, 0, .5, 2, thumbnailsDockable );
        grid.add( 2, 0, 0.3, .5, properties );
        grid.add( 2, 1, 0.3, .5, map );

        final CContentArea content = control.getContentArea();
        content.deploy( grid );

        thumbnailsDockable.setVisible( true );
        tree.setVisible( true );
        searches.setVisible( true );
        tagDockable.setVisible( true );
        properties.setVisible( true );
        map.setVisible( true );

        getContentPane().add( control.getContentArea() );

        setVisible( true );

    }

    /**
     * A handle to the Collection Tree so that we can ask it to move to the
     * front.
     */
    private DefaultSingleCDockable tree;

    /**
     * A handle to the Searches Tree so that we can ask it to move to the front.
     */
    private DefaultSingleCDockable searches;

    /**
     * If a ShowGroupRequest is seen we will switch the collection tab to the
     * foreground.
     *
     * @param request The ShowGroupRequest
     */
    @Subscribe
    public void handleShowGroupRequest( ShowGroupRequest request ) {
        tree.toFront();
    }

    /**
     * If a ShowQueryRequest is seen we will switch the query tab to the
     * foreground.
     *
     * @param request The query request
     */
    @Subscribe
    public void handleShowQueryRequest( ShowQueryRequest request ) {
        searches.toFront();
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
     * Sets the application title to the default title based on the
     * Resourcebundle string ApplicationTitle and the file name of the loaded
     * xml file if any.
     */
    private void updateApplicationTitle() {
        final File xmlFile = Settings.getPictureCollection().getXmlFile();
        if ( xmlFile != null ) {
            updateApplicationTitleEDT( Settings.jpoResources.getString( "ApplicationTitle" ) + ":  " + xmlFile.toString() );
        } else {
            updateApplicationTitleEDT( Settings.jpoResources.getString( "ApplicationTitle" ) );
        }
    }

    /**
     * Handle the SaveDockablesPositionsRequest by saving the dockable windows
     * layout to the Preferences of the JVM
     *
     * @param request The SaveDockablesPositionsRequest
     */
    @Subscribe
    public void handleSaveDockablesPositionsRequest( SaveDockablesPositionsRequest request ) {
        try {
            control.getResources().writePreferences();
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
        }
    }

    /**
     * Handle the LoadDockablesPositionsRequest by loading the saved dockable
     * windows layout from the Preferences of the JVM
     *
     * @param request The LoadDockablesPositionsRequest
     */
    @Subscribe
    public void handleLoadDockablesPositionsRequest( LoadDockablesPositionsRequest request ) {
        try {
            control.getResources().readPreferences();
        } catch ( IOException ex ) {
            LOGGER.severe( ex.getMessage() );
        }
    }

    /**
     * Handle the RestoreDockablesPositionsRequest by deploying the default grid
     * to the content.
     *
     * @param request The RestoreDockablesPositionsRequest
     */
    @Subscribe
    public void handleRestoreDockablesPositionsRequest( RestoreDockablesPositionsRequest request ) {
        CContentArea content = control.getContentArea();
        content.deploy( grid );
    }

}
