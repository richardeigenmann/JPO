package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.datamodel.*;
import org.jpo.eventbus.*;
import org.jpo.gui.swing.ResizeSlider;
import org.jpo.gui.swing.ThumbnailPanelTitle;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.awt.Component.TOP_ALIGNMENT;

/*
 Copyright (C) 2002-2022 Richard Eigenmann.
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
 * The ThumbnailPanelController manages a JPanel in a JScrollPane that displays
 * a group of pictures in a grid of thumbnailControllers or ad hoc search
 * results. Real pictures are shown as a thumbnail of the image whilst groups
 * are shown as a folder icon. Each thumbnail has it's caption under the image.
 * <p>
 * If the size of the component is changed the images are re-laid out and can
 * take advantage of the extra space if there is some.
 */
public class ThumbnailsPanelController implements NodeNavigatorListener, JpoDropTargetDropEventHandler {
    /**
     * The logger for the class
     */
    private static final Logger LOGGER = Logger.getLogger(ThumbnailsPanelController.class.getName());

    /**
     * The color to use for dimming the rectangle overlaying the images during selection
     */
    private static final Color DIMMED_COLOR = new Color(45, 45, 45, 180);

    /**
     * The panel that shows the Thumbnails
     */
    private final JPanel thumbnailsPane;

    /**
     * The scroll pane that holds the Thumbnail Panel
     */
    private final JScrollPane thumbnailJScrollPane = new JScrollPane();

    /**
     * The title above the ThumbnailPanel
     */
    private final ThumbnailPanelTitle titleJPanel;

    /**
     * The layout manager to lay out the thumbnails
     */
    private final ThumbnailLayoutManager thumbnailLayoutManager;

    /**
     * Whether to paint an overlay
     */
    private boolean paintOverlay;  // default is false

    /**
     * The overlay rectangle
     */
    private Rectangle overlayRectangle;

    /**
     * This object refers to the set of Nodes that is being browsed in the
     * ThumbnailPanelController
     */
    private NodeNavigatorInterface mySetOfNodes;

    /**
     * Listens for changes in the Group and updates the title if anything
     * changed
     */
    private final GroupInfoChangeListener myGroupInfoChangeListener = (GroupInfoChangeEvent groupInfoChangeEvent) -> {
        LOGGER.info("change event received.");
        updateTitle();
    };

    /**
     * a variable to hold the current starting position of thumbnailControllers
     * being displayed out of a group or search. Range 0..count()-1
     * <p>
     * <p>
     * This was invented to allow the number of thumbnailControllers to be
     * restricted so that "Out of memory" errors may be averted on long lists
     * of pictures.
     */
    private int startIndex;

    /**
     * An array that holds the ThumbnailControllers that are being
     * displayed
     */
    private ThumbnailController[] thumbnailControllers;

    /**
     * An array that holds ThumbnailDescriptionControllers that are
     * being displayed
     */
    private ThumbnailDescriptionController[] thumbnailDescriptionControllers;

    /**
     * This variable keeps track of how many thumbnailControllers per page the
     * component was initialised with. If the number changes because the user
     * changed it in the settings then the difference is recognised and the
     * arrays are recreated.
     */
    private int initialisedMaxThumbnails = Integer.MIN_VALUE;

    /**
     * Scaling factor for the Thumbnails
     */
    private float thumbnailSizeFactor = 1;

    /**
     * Whether to show filenames or not.
     * defining this a Boolean instead of boolean to create an object so that it can be passed by reference to the ThumbnailDescriptionPanels
     */
    private Boolean showFilenamesState = Settings.isShowFilenamesOnThumbnailPanel();

    /**
     * Whether to show timestamps or not.
     * defining this a Boolean instead of boolean to create an object so that it can be passed by reference to the Thumbnails
     */
    private Boolean showTimestampState = Settings.isShowTimestampsOnThumbnailPanel();

    /**
     * Point where the mouse was pressed so that we can figure out the rectangle
     * that is being selected.
     */
    private Point mousePressedPoint;

    /**
     * Remembers the last GroupInfo we picked so that we can attach a listener
     * to update the title if it changes
     */
    private SortableDefaultMutableTreeNode myLastGroupNode;

    /**
     * Creates a new ThumbnailPanelController which in turn creates the view
     * objects and hooks itself up so that thumbnails can be shown
     */
    public ThumbnailsPanelController() {
        titleJPanel = new ThumbnailPanelTitle();
        thumbnailsPane = new JPanel();
        thumbnailLayoutManager = new ThumbnailLayoutManager(thumbnailJScrollPane.getViewport());

        init();
        registerListeners();
    }

    /**
     * Initialises the components
     */
    private void init() {
        thumbnailsPane.setLayout(thumbnailLayoutManager);
        thumbnailsPane.setAlignmentY(TOP_ALIGNMENT);

        final var layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        layeredPane.add(thumbnailsPane, Integer.valueOf(1));

        final var overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                if (paintOverlay) {
                    super.paintComponent(g);
                    final var outerRect = new Rectangle(0, 0, thumbnailsPane.getWidth(), thumbnailsPane.getHeight());
                    g.setColor(DIMMED_COLOR);
                    g.fillRect(outerRect.x, outerRect.y, outerRect.width, overlayRectangle.y);
                    g.fillRect(outerRect.x, overlayRectangle.y, overlayRectangle.x, outerRect.height);
                    g.fillRect(overlayRectangle.x, overlayRectangle.y + overlayRectangle.height, outerRect.width, outerRect.height);
                    g.fillRect(overlayRectangle.x + overlayRectangle.width, overlayRectangle.y, outerRect.width - overlayRectangle.x - overlayRectangle.width, overlayRectangle.height);
                }
            }
        };
        overlayPanel.setOpaque(false);

        layeredPane.add(overlayPanel, Integer.valueOf(2));

        thumbnailJScrollPane.setViewportView(layeredPane);
        thumbnailsPane.setBackground(Settings.getJpoBackgroundColor());
        thumbnailJScrollPane.setMinimumSize(Settings.THUMBNAIL_JSCROLLPANE_MINIMUM_SIZE);
        thumbnailJScrollPane.setPreferredSize(Settings.thumbnailJScrollPanePreferredSize);
        thumbnailJScrollPane.setWheelScrollingEnabled(true);
        thumbnailJScrollPane.setFocusable(true);
        //  set the amount by which the panel scrolls down when the user clicks the
        //  little down or up arrow in the scrollbar
        thumbnailJScrollPane.getVerticalScrollBar().setUnitIncrement(80);

        thumbnailJScrollPane.setColumnHeaderView(titleJPanel);
        initThumbnailsArray();

        // Wire up the events
        titleJPanel.getNavigationButtonPanel().getFirstThumbnailsPageButton().addActionListener((ActionEvent e) -> goToFirstPage());
        titleJPanel.getNavigationButtonPanel().getPreviousThumbnailsPageButton().addActionListener((ActionEvent e) -> goToPreviousPage());
        titleJPanel.getNavigationButtonPanel().getNextThumbnailsPageButton().addActionListener((ActionEvent e) -> goToNextPage());
        titleJPanel.getNavigationButtonPanel().getLastThumbnailsPageButton().addActionListener((ActionEvent e) -> goToLastPage());
        titleJPanel.getShowFilenamesButton().addActionListener((ActionEvent e) -> showFilenamesButtonClicked());
        titleJPanel.getShowTimestampButton().addActionListener((ActionEvent e) -> showTimestampButtonClicked());
        titleJPanel.getPadlockButton().addActionListener((ActionEvent e) -> padlockButtonClicked());
        titleJPanel.getSearchButton().addActionListener(e -> searchButtonClicked());
        titleJPanel.getSearchField().addActionListener((ActionEvent e) -> doSearch(titleJPanel.getSearchField().getText()));

        titleJPanel.addResizeChangeListener((ChangeEvent e) -> {
            final var source = (JSlider) e.getSource();
            thumbnailSizeFactor = ((float) source.getValue()) / ResizeSlider.THUMBNAILSIZE_SLIDER_MAX;
            resizeAllThumbnails(thumbnailSizeFactor);
        });

        final var whiteArea = new JPanel();
        thumbnailJScrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, whiteArea);

        thumbnailsPane.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                if (e.isPopupTrigger() && mySetOfNodes instanceof GroupNavigator groupNavigator) {
                    JpoEventBus.getInstance().post(new ShowGroupPopUpMenuRequest(groupNavigator.getGroupNode(), e.getComponent(), e.getX(), e.getY()));
                    return;
                }
                mousePressedPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                handleMouseReleased(e);
            }
        });

        thumbnailsPane.addMouseMotionListener(new MouseInputAdapter() {

            @Override
            public void mouseDragged(final MouseEvent e) {
                handleMouseDragged(e);
            }

        });

        thumbnailJScrollPane.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_A && e.isControlDown()) {
                            selectAll();
                        }
                    }
                });
    }

    private void handleMouseDragged(final MouseEvent e) {
        // do the overlay painting
        paintOverlay = true;
        final var mouseMovedToPoint = e.getPoint();
        overlayRectangle = getMouseRectangle(mouseMovedToPoint);
        thumbnailsPane.repaint();

        final var viewRect = thumbnailJScrollPane.getViewport().getViewRect();
        final var verticalScrollBar = thumbnailJScrollPane.getVerticalScrollBar();
        final var scrolltrigger = 40;
        if (mouseMovedToPoint.y - viewRect.y - viewRect.height > -scrolltrigger) {
            final var increment = verticalScrollBar.getUnitIncrement(1);
            final var position = verticalScrollBar.getValue();
            if (position < verticalScrollBar.getMaximum()) {
                verticalScrollBar.setValue(position + increment);
            }
        } else if (mouseMovedToPoint.y - viewRect.y < scrolltrigger) {
            final var increment = verticalScrollBar.getUnitIncrement(1);
            final var position = verticalScrollBar.getValue();
            if (position > verticalScrollBar.getMinimum()) {
                verticalScrollBar.setValue(position - increment);
            }
        }
    }

    private void handleMouseReleased(final MouseEvent e) {
        if (e.isPopupTrigger() && mySetOfNodes instanceof GroupNavigator gn) {
            JpoEventBus.getInstance().post(new ShowGroupPopUpMenuRequest(gn.getGroupNode(), e.getComponent(), e.getX(), e.getY()));
            return;
        }

        thumbnailJScrollPane.requestFocusInWindow();

        // undo the overlay painting
        paintOverlay = false;
        thumbnailsPane.repaint();

        final var mouseRectangle = getMouseRectangle(e.getPoint());

        // I wonder why they don't put the following two lines into the SWING library but
        // let you work out this binary math on your own from the unhelpful description?
        final var ctrlpressed = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK;
        final var shiftpressed = (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK;

        if (!(ctrlpressed || shiftpressed)) {
            Settings.getPictureCollection().clearSelection();
        }

        final var thumbnailRectangle = new Rectangle();
        for (final var thumbnailController : thumbnailControllers) {
            final var node = thumbnailController.getNode();
            if (node == null) {
                continue;
            }
            thumbnailController.getThumbnail().getBounds(thumbnailRectangle);
            if (mouseRectangle.intersects(thumbnailRectangle)) {
                Settings.getPictureCollection().addToSelectedNodes(node);
            }
        }
    }

    private void doSearch(final String searchString) {
        final var textQuery = new TextQuery(searchString);
        textQuery.setStartNode(Settings.getPictureCollection().getRootNode());
        Settings.getPictureCollection().addQueryToTreeModel(textQuery);
        titleJPanel.hideSearchField();
        JpoEventBus.getInstance().post(new ShowQueryRequest(textQuery));
    }

    private void searchButtonClicked() {
        if ( ! titleJPanel.getSearchField().isVisible()) {
            titleJPanel.getSearchField().setVisible(true);
            titleJPanel.getSearchButton().getParent().validate();
            titleJPanel.getSearchField().requestFocus();
            titleJPanel.getSearchField().selectAll();
        } else {
            if (titleJPanel.getSearchField().getText().length() > 0) {
                doSearch(titleJPanel.getSearchField().getText());
            } else {
                titleJPanel.getSearchField().setVisible(false);
                titleJPanel.getSearchButton().getParent().validate();
            }
        }
    }

    /**
     * If the show filenames button was clicked, flip the state and show or hide the filenames.
     */
    private void showFilenamesButtonClicked() {
        showFilenamesState = !showFilenamesState;
        Settings.setShowFilenamesOnThumbnailPanel(showFilenamesState);
        for (var i = 0; i < Settings.getMaxThumbnails(); i++) {
            thumbnailDescriptionControllers[i].showFilename(showFilenamesState);
        }
    }

    /**
     * If the show filenames button was clicked, flip the state and show or hide the filenames.
     */
    private void showTimestampButtonClicked() {
        showTimestampState = !showTimestampState;
        Settings.setShowTimestampsOnThumbnailPanel(showTimestampState);
        for (var i = 0; i < Settings.getMaxThumbnails(); i++) {
            thumbnailControllers[i].setShowTimestamp(showTimestampState);
        }
    }

    private void padlockButtonClicked() {
        LOGGER.log(Level.INFO,"padlock button was clicked");
        final var allowEdits = Settings.getPictureCollection().getAllowEdits();
        final var newState = ! allowEdits;
        Settings.getPictureCollection().setAllowEdits( newState );
    }

    private void updatePadlockButton() {
        titleJPanel.setPadlockButtonState( ! Settings.getPictureCollection().getAllowEdits() );
    }

    public void resizeAllThumbnails(final float thumbnailSizeFactor) {
        thumbnailLayoutManager.setThumbnailWidth((int) (350 * thumbnailSizeFactor));
        for (var i = 0; i < Settings.getMaxThumbnails(); i++) {
            thumbnailControllers[i].setFactor(thumbnailSizeFactor);
            thumbnailDescriptionControllers[i].setFactor(thumbnailSizeFactor);
        }
        thumbnailLayoutManager.layoutContainer(thumbnailsPane);
    }

    /**
     * Returns the rectangle marked by the area which the mouse marked by
     * dragging. If the destination is to the left or higher than the
     * mousePressedPoint the rectangle corrects this.
     *
     * @param mousePoint mouse point
     * @return The rectangle in the coordinate space of the parent component
     */
    private Rectangle getMouseRectangle(final Point mousePoint) {
        final var rectangle = new Rectangle(mousePressedPoint,
                new Dimension(mousePoint.x - mousePressedPoint.x,
                        mousePoint.y - mousePressedPoint.y));
        if (mousePoint.x < mousePressedPoint.x) {
            rectangle.x = mousePoint.x;
            rectangle.width = mousePressedPoint.x - mousePoint.x;
        }
        if (mousePoint.y < mousePressedPoint.y) {
            rectangle.y = mousePoint.y;
            rectangle.height = mousePressedPoint.y - mousePoint.y;
        }

        return rectangle;
    }

    /**
     * Registers the controller as a listener
     */
    private void registerListeners() {
        JpoEventBus.getInstance().register(this);

        thumbnailJScrollPane.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                thumbnailsPane.doLayout();
            }

        });
    }

    /**
     * Returns a component to be displayed
     *
     * @return The JScollPane widget
     */
    public Component getView() {
        return thumbnailJScrollPane;
    }

    /**
     * Handles the ShowGroupRequest by showing the group
     *
     * @param event the ShowGroupRequest
     */
    @Subscribe
    public void handleShowGroupRequest(final ShowGroupRequest event) {
        final Runnable runnable = () -> show(new GroupNavigator(event.node()));
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeLater(runnable);
        }

    }

    /**
     * Handles the ShowQueryRequest by showing the query results
     *
     * @param event the ShowQueryRequest
     */
    @Subscribe
    public void handleShowQueryRequest(final ShowQueryRequest event) {
        show(new QueryNavigator(event.query()));
    }

    /**
     * Instructs the ThumbnailPanelController to display the specified set of
     * nodes
     *
     * @param newNodeNavigator The Interface with the collection of nodes
     */
    private void show(final NodeNavigatorInterface newNodeNavigator) {
        Tools.checkEDT();
        if (this.mySetOfNodes != null) {
            this.mySetOfNodes.removeNodeNavigatorListener(this);
        }
        this.mySetOfNodes = newNodeNavigator;
        newNodeNavigator.addNodeNavigatorListener(this);

        if (myLastGroupNode != null) {
            final var groupInfo = (GroupInfo) myLastGroupNode.getUserObject();
            groupInfo.removeGroupInfoChangeListener(myGroupInfoChangeListener);
        }
        myLastGroupNode = null;
        if (newNodeNavigator instanceof GroupNavigator groupNavigator) {
            myLastGroupNode = groupNavigator.getGroupNode();
            final var groupInfo = (GroupInfo) myLastGroupNode.getUserObject();
            groupInfo.addGroupInfoChangeListener(myGroupInfoChangeListener);
        }

        Settings.getPictureCollection().clearSelection();
        goToFirstPage();
    }

    @Subscribe
    public void handleCollectionLockNotification(CollectionLockNotification event) {
        updatePadlockButton();
    }

    /**
     * Request that the ThumbnailPanel show the first page of Thumbnails
     */
    private void goToFirstPage() {
        startIndex = 0;
        scrollToTop();
        nodeLayoutChanged();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the previous page of Thumbnails
     */
    private void goToPreviousPage() {
        startIndex -= Settings.getMaxThumbnails();
        if (startIndex < 0) {
            startIndex = 0;
        }
        scrollToTop();
        nodeLayoutChanged();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the next page of Thumbnails
     */
    private void goToNextPage() {
        startIndex += Settings.getMaxThumbnails();
        scrollToTop();
        nodeLayoutChanged();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the last page of Thumbnails
     */
    private void goToLastPage() {
        final var last = mySetOfNodes.getNumberOfNodes();
        final var tgtPage = last / Settings.getMaxThumbnails();
        startIndex = tgtPage * Settings.getMaxThumbnails();
        scrollToTop();
        nodeLayoutChanged();
        setButtonStatus();
    }

    private void scrollToTop() {
        thumbnailJScrollPane.getVerticalScrollBar().setValue(0);
    }

    /**
     * creates the arrays for the thumbnailControllers and the descriptions and
     * adds them to the ThumbnailPane.
     */
    private void initThumbnailsArray() {
        Tools.checkEDT();
        thumbnailControllers = new ThumbnailController[Settings.getMaxThumbnails()];
        thumbnailDescriptionControllers = new ThumbnailDescriptionController[Settings.getMaxThumbnails()];
        thumbnailsPane.removeAll();
        initialisedMaxThumbnails = Settings.getMaxThumbnails();
        for (var i = 0; i < Settings.getMaxThumbnails(); i++) {
            thumbnailControllers[i] = new ThumbnailController(Settings.getThumbnailSize());
            thumbnailControllers[i].setShowTimestamp(showTimestampState);
            thumbnailDescriptionControllers[i] = new ThumbnailDescriptionController();
            thumbnailDescriptionControllers[i].showFilename(showFilenamesState);
            thumbnailsPane.add(thumbnailControllers[i].getThumbnail());
            thumbnailsPane.add(thumbnailDescriptionControllers[i].getPanel());
        }
    }

    /**
     * Assigns each of the ThumbnailControllers and ThumbnailDescriptionJPanels
     * the appropriate node from the Browser being shown.
     * <p>
     * It also sets the title of the JScrollPane.
     */
    @Override
    public void nodeLayoutChanged() {
        updateTitle();

        if (initialisedMaxThumbnails != Settings.getMaxThumbnails()) {
            LOGGER.log(Level.INFO, "There are {0} initialised thumbnails which is not equal to the defined maximum number of {1}. Therefore reinitialising", new Object[]{initialisedMaxThumbnails, Settings.getMaxThumbnails()});
            initThumbnailsArray();
        }

        setPageStats();
        setButtonStatus();

        for (var i = Settings.getMaxThumbnails() - 1; i > -1; i--) {
            if (!thumbnailControllers[i].isAlreadyDisplayingNode(mySetOfNodes, i + startIndex)) {
                thumbnailControllers[i].setNode(mySetOfNodes, i + startIndex);
                thumbnailDescriptionControllers[i].setNode(mySetOfNodes.getNode(i + startIndex));
                thumbnailDescriptionControllers[i].showFilename(showFilenamesState);
            }
        }
    }

    /**
     * Sets the text in the title for displaying page count information
     */
    private void setPageStats() {
        final var total = mySetOfNodes.getNumberOfNodes();
        final var lastOnPage = Math.min(startIndex + Settings.getMaxThumbnails(), total);
        titleJPanel.lblPage.setText(String.format("Thumbnails %d to %d of %d", startIndex + 1, lastOnPage, total));
    }

    /**
     * Updates the title of the page. (The implementing method takes care that
     * it is on the EDT)
     */
    private void updateTitle() {
        titleJPanel.setTitle(mySetOfNodes.getTitle());
    }

    /**
     * This method sets whether the first, previous, next and last buttons are
     * visible or not
     */
    private void setButtonStatus() {
        if (startIndex == 0) {
            titleJPanel.getNavigationButtonPanel().getFirstThumbnailsPageButton().setEnabled(false);
            titleJPanel.getNavigationButtonPanel().getPreviousThumbnailsPageButton().setEnabled(false);
        } else {
            titleJPanel.getNavigationButtonPanel().getFirstThumbnailsPageButton().setEnabled(true);
            titleJPanel.getNavigationButtonPanel().getPreviousThumbnailsPageButton().setEnabled(true);
        }

        int count = mySetOfNodes.getNumberOfNodes();
        if ((startIndex + Settings.getMaxThumbnails()) < count) {
            titleJPanel.getNavigationButtonPanel().getLastThumbnailsPageButton().setEnabled(true);
            titleJPanel.getNavigationButtonPanel().getNextThumbnailsPageButton().setEnabled(true);
        } else {
            titleJPanel.getNavigationButtonPanel().getLastThumbnailsPageButton().setEnabled(false);
            titleJPanel.getNavigationButtonPanel().getNextThumbnailsPageButton().setEnabled(false);
        }

        titleJPanel.getNavigationButtonPanel().setVisible(mySetOfNodes.getNumberOfNodes() >= Settings.getMaxThumbnails());
    }

    /**
     * This method select all Thumbnails which are not null
     */
    private void selectAll() {
        for (ThumbnailController thumbnailController : thumbnailControllers) {
            final SortableDefaultMutableTreeNode node = thumbnailController.getNode();
            if (node != null) {
                Settings.getPictureCollection().addToSelectedNodes(node);
            }
        }
    }

    @Override
    public void handleJpoDropTargetDropEvent(DropTargetDropEvent event) {
        if (myLastGroupNode != null) {
            myLastGroupNode.executeDrop(event);
        }
    }
}
