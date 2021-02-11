package org.jpo.gui.swing;

import bibliothek.gui.dock.common.*;
import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.Settings;
import org.jpo.eventbus.*;
import org.jpo.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 MainWindow.java:  main window of the JPO application

 Copyright (C) 2002 - 2021  Richard Eigenmann.
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
@SuppressWarnings("UnstableApiUsage")
public class MainWindow extends ResizableJFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    /**
     * The controller for the Docking Frames framework that controls all the
     * internal windows.
     */
    private final transient CControl control;

    /**
     * The grid to which all components are added
     */
    private final transient CGrid grid;
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
        super(Settings.getJpoResources().getString("ApplicationTitle"));
        // Set up Docking Frames
        control = new CControl(this);
        grid = new CGrid(control);
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
                JpoEventBus.getInstance().post(new UnsavedUpdatesDialogRequest(new ShutdownApplicationRequest()));
            }
        });


        try {
            final String Windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            UIManager.setLookAndFeel(Windows);
        } catch (final ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            LOGGER.fine("Could not set Look and Feel");
        }

        setMinimumSize(Settings.jpoJFrameMinimumSize);
        setPreferredSize(Settings.getLastMainFrameCoordinates().getSize());

        final ApplicationJMenuBar menuBar = new ApplicationJMenuBar();
        setJMenuBar(menuBar);

        // Set Tooltipps to snappy mode
        final ToolTipManager ttm = ToolTipManager.sharedInstance();
        ttm.setDismissDelay(1500);
        ttm.setInitialDelay(100);

        final InfoPanelController infoPanelController = new InfoPanelController();
        final JScrollPane statsScroller = new JScrollPane(infoPanelController.getInfoPanel());
        statsScroller.setWheelScrollingEnabled(true);
        statsScroller.getVerticalScrollBar().setUnitIncrement(20);

        pack();
        setVisible(true);

        switch (Settings.getStartupSizeChoice()) {
            case 0 -> switchWindowMode(WindowSize.WINDOW_DECORATED_FULLSCREEN);
            case 1 -> switchWindowMode(WindowSize.WINDOW_DECORATED_PRIMARY);
            case 2 -> switchWindowMode(WindowSize.WINDOW_DECORATED_SECONDARY);
            default -> switchWindowMode(WindowSize.WINDOW_CUSTOM_SIZE_MAIN_FRAME);
        }

        final Component thumbnailPanel = (new ThumbnailsPanelController()).getView();

        tree = new DefaultSingleCDockable("TreeId",
                Settings.getJpoResources().getString("jpoTabbedPaneCollection"),
                new CollectionJTreeController(Settings.getPictureCollection()).getJScrollPane());
        searches = new DefaultSingleCDockable("SearchId",
                Settings.getJpoResources().getString("jpoTabbedPaneSearches"),
                new QueriesJTreeController().getJComponent());

        final JButton loadJButton = new JButton("Properties - Load");
        loadJButton.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new LoadDockablesPositionsRequest()));
        final JButton saveJbutton = new JButton("Save");
        saveJbutton.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new SaveDockablesPositionsRequest()));
        final JButton resetJbutton = new JButton("Reset");
        resetJbutton.addActionListener((ActionEvent e) -> JpoEventBus.getInstance().post(new RestoreDockablesPositionsRequest()));

        final JPanel propertiesJPanel = new JPanel();
        propertiesJPanel.setLayout(new BoxLayout(propertiesJPanel, BoxLayout.Y_AXIS));
        propertiesJPanel.add(loadJButton);
        propertiesJPanel.add(saveJbutton);
        propertiesJPanel.add(resetJbutton);

        SingleCDockable properties = new DefaultSingleCDockable("PropertiesId", "Properties", propertiesJPanel);
        SingleCDockable tagDockable = new DefaultSingleCDockable("TagId", "TagCloud", new TagCloudController().getTagCloud());
        SingleCDockable statsDockable = new DefaultSingleCDockable("StatsId", "Stats", statsScroller);
        SingleCDockable thumbnailsDockable = new DefaultSingleCDockable("ThumbnailsId", "Thumbnails", thumbnailPanel);

        grid.add(0, 0, 0.2, 0.8, tree);
        grid.add(0, 0, 0.2, 0.8, searches);
        grid.add(0, 1, 0.2, 0.2, tagDockable);
        grid.add(0, 1, 0.2, 0.2, properties);
        grid.add(0, 1, 0.2, 0.2, statsDockable);
        grid.add(1, 0, .5, 2, thumbnailsDockable);

        final CContentArea content = control.getContentArea();
        content.deploy(grid);

        thumbnailsDockable.setVisible(true);
        tree.setVisible(true);
        searches.setVisible(true);
        tagDockable.setVisible(true);

        getContentPane().add(control.getContentArea());

        setVisible(true);
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
        LOGGER.log(Level.INFO, "Saving Main Window position and size");
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
        } catch (IOException ex) {
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
