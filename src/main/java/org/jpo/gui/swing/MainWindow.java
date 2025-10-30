package org.jpo.gui.swing;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.*;
import org.jpo.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * Main Window of the JPO application. It uses the
 * <a href="http://www.docking-frames.org/">Docking Frames</a> framework to handle
 * the internal windows.
 *
 * @author Richard Eigenmann
 */
public class MainWindow extends ResizableJFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    /**
     * The controller for the Docking Frames framework that controls all the
     * internal windows.
     */
    private final transient CControl control = new CControl(this);

    /**
     * The grid to which all components are added
     */
    private final transient CGrid grid = new CGrid(control);

    /**
     * A handle to the Collection Tree so that we can ask it to move to the
     * front.
     */
    private transient DefaultSingleCDockable tree;

    /**
     * A handle to the Searches Tree so that we can ask it to move to the front.
     */
    private transient DefaultSingleCDockable searches;

    /**
     * Creates the JPO window and lays out the components. It registers itself
     * on the {@link JpoEventBus} and handles the below events. If the window close handles
     * are activated it sends an {@link UnsavedUpdatesDialogRequest} with an
     * embedded {@link ShutdownApplicationRequest} to the event handlers.
     *
     * @see JpoEventBus
     * @see ShowGroupRequest
     * @see ShowQueryRequest
     * @see SaveDockablesPositionsRequest
     * @see LoadDockablesPositionsRequest
     * @see RestoreDockablesPositionsRequest
     * @see UpdateApplicationTitleRequest
     */
    public MainWindow() {
        super(JpoResources.getResource("ApplicationTitle"));
        initComponents();
    }

    private void initComponents() {
        Settings.setMainWindow(this);
        Settings.setAnchorFrame(this);
        JpoEventBus.getInstance().register(this);

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                JpoEventBus.getInstance().post(new UnsavedUpdatesDialogRequest(Settings.getPictureCollection(), new ShutdownApplicationRequest()));
            }
        });

        try {
            final var Windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            UIManager.setLookAndFeel(Windows);
        } catch (final ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            LOGGER.fine("Could not set Look and Feel");
        }

        setMinimumSize(Settings.jpoJFrameMinimumSize);
        setPreferredSize(Settings.getLastMainFrameCoordinates().getSize());

        final var menuBar = new ApplicationJMenuBar();
        setJMenuBar(menuBar);

        // Set Tooltipps to snappy mode
        final var ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay(1500);
        ttm.setInitialDelay(100);

        final var infoPanelController = new InfoPanelController();
        final var statsScroller = new JScrollPane(infoPanelController.getInfoPanel());
        statsScroller.setWheelScrollingEnabled(true);
        statsScroller.getVerticalScrollBar().setUnitIncrement(20);

        final var mapWindow = new MapWindow();

        switch (Settings.getStartupSizeChoice()) {
            case 0 -> switchWindowMode(WindowSize.WINDOW_DECORATED_FULLSCREEN);
            case 1 -> switchWindowMode(WindowSize.WINDOW_DECORATED_PRIMARY);
            case 2 -> switchWindowMode(WindowSize.WINDOW_DECORATED_SECONDARY);
            default -> switchWindowMode(WindowSize.WINDOW_CUSTOM_SIZE_MAIN_FRAME);
        }

        final Component thumbnailPanel = (new ThumbnailsPanelController()).getView();

        tree = new DefaultSingleCDockable("TreeId",
                JpoResources.getResource("jpoTabbedPaneCollection"),
                new CollectionJTreeController(Settings.getPictureCollection()).getJScrollPane());
        searches = new DefaultSingleCDockable("SearchId",
                JpoResources.getResource("jpoTabbedPaneSearches"),
                new QueriesJTreeController(Settings.getPictureCollection()).getJComponent());

        final var tagCloudDockable = new DefaultSingleCDockable("TagId", "TagCloud", new TagCloudController(Settings.getPictureCollection()).getTagCloud());
        final var statsDockable = new DefaultSingleCDockable("StatsId", "Stats", statsScroller);
        final var mapsDockable = new DefaultSingleCDockable("MapId", "Map", mapWindow.getJComponent());
        final var eventBusViewerDockable = new DefaultSingleCDockable("EventBusViewerId", "EventBus", new EventBusViewer());
        final var thumbnailsDockable = new DefaultSingleCDockable("ThumbnailsId", "Thumbnails", thumbnailPanel);

        grid.add(0, 0, 0.2, 0.8, tree);
        grid.add(0, 0, 0.2, 0.8, searches);
        grid.add(0, 1, 0.2, 0.2, tagCloudDockable);
        grid.add(0, 1, 0.2, 0.2, statsDockable);
        grid.add(0, 1, 0.2, 0.2, mapsDockable);
        if (Settings.isDebugMode()) {
            grid.add(0, 1, 0.2, 0.2, eventBusViewerDockable);
        }
        grid.add(1, 0, .5, 2, thumbnailsDockable);

        control.getContentArea().deploy(grid);
        getContentPane().add(control.getContentArea());

        pack();
        setVisible(true);
        mapWindow.setDefaultView();
    }

    /**
     * If a ShowGroupRequest is seen we will switch the collection tab to the
     * foreground.
     *
     * @param request The ShowGroupRequest
     */
    @Subscribe
    public void handleShowGroupRequest(final ShowGroupRequest request) {
        tree.toFront();
    }

    /**
     * If a ShowQueryRequest is seen we will switch the query tab to the
     * foreground.
     *
     * @param request The query request
     */
    @Subscribe
    public void handleShowQueryRequest(final ShowQueryRequest request) {
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
    public void handleUpdateApplicationTitleRequest(final UpdateApplicationTitleRequest request) {
        if (SwingUtilities.isEventDispatchThread()) {
            setTitle(request.newTitle());
        } else {
            SwingUtilities.invokeLater(()
                    -> setTitle(request.newTitle())
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
    public void handleSaveDockablesPositionsRequest(final SaveDockablesPositionsRequest request) {
        LOGGER.log(Level.FINE, "Saving Main Window position and size");
        try {
            Settings.setLastMainFrameCoordinates(this.getBounds());
            control.getResources().writePreferences();
        } catch (final IOException ex) {
            LOGGER.severe(ex.getMessage());
        }
    }

    /**
     * Handle the LoadDockablesPositionsRequest by loading the saved dockable
     * windows layout from the Preferences of the JVM
     *
     * @param request The LoadDockablesPositionsRequest
     */
    @Subscribe
    public void handleLoadDockablesPositionsRequest(final LoadDockablesPositionsRequest request) {
        try {
            control.getResources().readPreferences();
        } catch (final IOException ex) {
            LOGGER.severe(ex.getMessage());
        }
    }

    /**
     * Handle the RestoreDockablesPositionsRequest by deploying the default grid
     * to the content.
     *
     * @param request The RestoreDockablesPositionsRequest
     */
    @Subscribe
    public void handleRestoreDockablesPositionsRequest(final RestoreDockablesPositionsRequest request) {
        final CContentArea content = control.getContentArea();
        content.deploy(grid);
    }

}
