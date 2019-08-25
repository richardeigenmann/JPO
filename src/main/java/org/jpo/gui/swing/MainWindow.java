package org.jpo.gui.swing;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import com.google.common.eventbus.Subscribe;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
import org.jpo.EventBus.CloseApplicationRequest;
import org.jpo.EventBus.JpoEventBus;
import org.jpo.EventBus.LoadDockablesPositionsRequest;
import org.jpo.EventBus.RestoreDockablesPositionsRequest;
import org.jpo.EventBus.SaveDockablesPositionsRequest;
import org.jpo.EventBus.ShowGroupRequest;
import org.jpo.EventBus.ShowQueryRequest;
import org.jpo.EventBus.UnsavedUpdatesDialogRequest;
import org.jpo.EventBus.UpdateApplicationTitleRequest;
import org.jpo.dataModel.Settings;
import org.jpo.dataModel.Tools;
import org.jpo.gui.*;

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
 * org.jpo.Main Window of the JPO application. It uses the
 * <a href="http://dock.javaforge.com">Docking Frames</a> framework to handle
 * the internal windows.
 *
 * @author Richard Eigenmann
 */
@SuppressWarnings("UnstableApiUsage")
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
    private final CGrid grid;

    /**
     * Creates the JPO window and lays out the components. It registers itself
     * on the Eventbus and handles the below events. If the window close handles
     * are activated it sends an {@link UnsavedUpdatesDialogRequest} with an
     * embedded {@link CloseApplicationRequest} to the event handlers.
     *
     * @see ShowGroupRequest
     * @see ShowQueryRequest
     * @see SaveDockablesPositionsRequest
     * @see LoadDockablesPositionsRequest
     * @see RestoreDockablesPositionsRequest
     * @see UpdateApplicationTitleRequest
     */
    public MainWindow() {
        // Set up Docking Frames
        control = new CControl( this );
        grid = new CGrid( control );

        Settings.setMainWindow( this );

        Tools.checkEDT();
        initComponents();
        JpoEventBus.getInstance().register( this );

        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );
                JpoEventBus.getInstance().post( new UnsavedUpdatesDialogRequest( new CloseApplicationRequest() ) );
            }
        } );
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
                new CollectionJTreeController(Settings.getPictureCollection()).getJScrollPane() );
        searches = new DefaultSingleCDockable( "SearchId",
                Settings.jpoResources.getString( "jpoTabbedPaneSearches" ),
                new QueriesJTreeController().getJComponent() );

        JButton loadJButton = new JButton( "Properties - Load" );
        loadJButton.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new LoadDockablesPositionsRequest() ));
        JButton saveJbutton = new JButton( "Save" );
        saveJbutton.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new SaveDockablesPositionsRequest() ));
        JButton resetJbutton = new JButton( "Reset" );
        resetJbutton.addActionListener(( ActionEvent e ) -> JpoEventBus.getInstance().post( new RestoreDockablesPositionsRequest() ));

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
        //grid.add( 2, 0, 0.3, .5, properties );
        //grid.add( 2, 1, 0.3, .5, map );

        final CContentArea content = control.getContentArea();
        content.deploy( grid );

        thumbnailsDockable.setVisible( true );
        tree.setVisible( true );
        searches.setVisible( true );
        tagDockable.setVisible( true );
        //properties.setVisible( true );
        //map.setVisible( true );

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
     * When a UpdateApplicationTitleRequest is seen we will update the title of
     * the JFrame. If the request is received on the EDT it is executed
     * immediately else it is packed into a Runnable and
     * SwingUtilities.invokeLater -ed.
     *
     * @param request The new Title Request
     */
    @Subscribe
    public void handleUpdateApplicationTitleRequest( final UpdateApplicationTitleRequest request ) {
        if ( SwingUtilities.isEventDispatchThread() ) {
            setTitle( request.getTitle() );
        } else {
            SwingUtilities.invokeLater( ()
                    -> setTitle( request.getTitle() )
            );
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
