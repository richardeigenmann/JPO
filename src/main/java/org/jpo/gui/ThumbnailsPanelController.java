package org.jpo.gui;

import com.google.common.eventbus.Subscribe;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.ShowGroupPopUpMenuRequest;
import org.jpo.eventbus.ShowGroupRequest;
import org.jpo.eventbus.ShowQueryRequest;
import org.jpo.datamodel.*;
import org.jpo.gui.swing.Thumbnail;
import org.jpo.gui.swing.ThumbnailPanelTitle;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.util.logging.Logger;

/*
 Copyright (C) 2002 - 2019  Richard Eigenmann.
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
 * The ThumbnailPanelController manages a JPanel in a JScrollPane that displays
 * a group of pictures in a grid of thumbnailControllers or ad hoc search
 * results. Real pictures are shown as a thumbnail of the image whilst groups
 * are shown as a folder icon. Each thumbnail has it's caption under the image.
 * <p>
 * If the size of the component is changed the images are re-laid out and can
 * take advantage of the extra space if there is some.
 */
public class ThumbnailsPanelController implements NodeNavigatorListener, JpoDropTargetDropEventHandler {

    private static final Logger LOGGER = Logger.getLogger(ThumbnailsPanelController.class.getName());

    private boolean paintOverlay;  // default is false
    private Rectangle overlayRectangle;

    /**
     * The Panel that shows the Thumbnails
     */
    private final JPanel thumbnailsPane;

    /**
     * The Scroll Pane that holds the Thumbnail Panel
     */
    private final JScrollPane thumbnailJScrollPane;

    /**
     * This object refers to the set of Nodes that is being browsed in the
     * ThumbnailPanelController
     */
    private NodeNavigatorInterface mySetOfNodes;

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
     * The title above the ThumbnailPanel
     */
    private final ThumbnailPanelTitle titleJPanel;

    /**
     * An array that holds the 50 or so ThumbnailComponents that are being
     * displayed
     */
    private ThumbnailController[] thumbnailControllers;

    /**
     * An array that holds the 50 or so ThumbnailDescriptionJPanels that are
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
     * Factor for the Thumbnails
     */
    private float thumbnailSizeFactor = 1;

    /**
     * Creates a new ThumbnailPanelController which in turn creates the view
     * objects and hooks itself up so that thumbnails can be shown
     */
    public ThumbnailsPanelController() {
        titleJPanel = new ThumbnailPanelTitle();
        thumbnailsPane = new JPanel();
        thumbnailJScrollPane = new JScrollPane();
        thumbnailLayoutManager = new ThumbnailLayoutManager(thumbnailJScrollPane.getViewport());

        init();
        registerListeners();
    }

    private static final Color DIMMED_COLOR = new Color(45, 45, 45, 180);

    private final ThumbnailLayoutManager thumbnailLayoutManager;

    /**
     * Initialises the components for the ThumbnailController Pane
     */
    private void init() {
        Tools.checkEDT();

        thumbnailsPane.setLayout(thumbnailLayoutManager);

        final JLayeredPane layeredPane = new JLayeredPane();

        layeredPane.setLayout(new OverlayLayout(layeredPane));
        layeredPane.add(thumbnailsPane, Integer.valueOf(1));

        final JPanel overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {

                if (paintOverlay) {
                    super.paintComponent(g);

                    Rectangle outerRect = new Rectangle(0, 0, thumbnailsPane.getWidth(), thumbnailsPane.getHeight());
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
        thumbnailsPane.setBackground(Settings.JPO_BACKGROUND_COLOR);
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
        titleJPanel.firstThumbnailsPageButton.addActionListener((ActionEvent e) -> goToFirstPage());
        titleJPanel.previousThumbnailsPageButton.addActionListener((ActionEvent e) -> goToPreviousPage());
        titleJPanel.nextThumbnailsPageButton.addActionListener((ActionEvent e) -> goToNextPage());
        titleJPanel.lastThumbnailsPageButton.addActionListener((ActionEvent e) -> goToLastPage());

        titleJPanel.addResizeChangeListener((ChangeEvent e) -> {
            JSlider source = (JSlider) e.getSource();
            thumbnailSizeFactor = ((float) source.getValue()) / ThumbnailPanelTitle.THUMBNAILSIZE_SLIDER_MAX;
            resizeAllThumbnails(thumbnailSizeFactor);
        });

        JPanel whiteArea = new JPanel();
        thumbnailJScrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, whiteArea);

        thumbnailsPane.addMouseListener(new MouseInputAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger() && mySetOfNodes instanceof GroupNavigator) {
                    JpoEventBus.getInstance().post(new ShowGroupPopUpMenuRequest(((GroupNavigator) mySetOfNodes).getGroupNode(), e.getComponent(), e.getX(), e.getY()));
                    return;
                }

                mousePressedPoint = e.getPoint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && mySetOfNodes instanceof GroupNavigator) {
                    JpoEventBus.getInstance().post(new ShowGroupPopUpMenuRequest(((GroupNavigator) mySetOfNodes).getGroupNode(), e.getComponent(), e.getX(), e.getY()));
                    return;
                }

                thumbnailJScrollPane.requestFocusInWindow();

                // undo the overlay painting
                paintOverlay = false;
                thumbnailsPane.repaint();

                Rectangle mouseRectangle = getMouseRectangle(e.getPoint());

                // I wonder why they don't put the following two lines into the SWING library but
                // let you work out this binary math on your own from the unhelpful description?
                boolean ctrlpressed = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK;
                boolean shiftpressed = (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK;

                if (!(ctrlpressed || shiftpressed)) {
                    Settings.getPictureCollection().clearSelection();
                }

                Rectangle thumbnailRectangle = new Rectangle();
                SortableDefaultMutableTreeNode node;
                for (ThumbnailController thumbnailController : thumbnailControllers) {
                    node = thumbnailController.getNode();
                    if (node == null) {
                        continue;
                    }
                    thumbnailController.getThumbnail().getBounds(thumbnailRectangle);
                    if (mouseRectangle.intersects(thumbnailRectangle)) {
                        Settings.getPictureCollection().addToSelectedNodes(node);
                    }
                }

            }
        });

        thumbnailsPane.addMouseMotionListener(new MouseInputAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                // do the overlay painting
                paintOverlay = true;
                Point mouseMovedToPoint = e.getPoint();
                overlayRectangle = getMouseRectangle(mouseMovedToPoint);
                thumbnailsPane.repaint();

                Rectangle viewRect = thumbnailJScrollPane.getViewport().getViewRect();
                JScrollBar verticalScrollBar = thumbnailJScrollPane.getVerticalScrollBar();
                final int scrolltrigger = 40;
                if (mouseMovedToPoint.y - viewRect.y - viewRect.height > -scrolltrigger) {
                    int increment = verticalScrollBar.getUnitIncrement(1);
                    int position = verticalScrollBar.getValue();
                    if (position < verticalScrollBar.getMaximum()) {
                        verticalScrollBar.setValue(position + increment);
                    }
                } else if (mouseMovedToPoint.y - viewRect.y < scrolltrigger) {
                    int increment = verticalScrollBar.getUnitIncrement(1);
                    int position = verticalScrollBar.getValue();
                    if (position > verticalScrollBar.getMinimum()) {
                        verticalScrollBar.setValue(position - increment);
                    }
                }
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

    public void resizeAllThumbnails(float thumbnailSizeFactor) {
        thumbnailLayoutManager.setThumbnailWidth((int) (350 * thumbnailSizeFactor));
        for (int i = 0; i < Settings.maxThumbnails; i++) {
            thumbnailControllers[i].setFactor(thumbnailSizeFactor);
            thumbnailDescriptionControllers[i].setFactor(thumbnailSizeFactor);
        }
        thumbnailLayoutManager.layoutContainer(thumbnailsPane);
    }

    /**
     * Point where the mouse was pressed so that we can figure out the rectangle
     * that is being selected.
     */
    private Point mousePressedPoint;

    /**
     * Returns the rectangle marked by the area which the mouse marked by
     * dragging. If the destination is to the left or higher than the
     * mousePressedPoint the rectangle corrects this.
     *
     * @param mousePoint mouse point
     * @return The rectangle in the coordinate space of the parent component
     */
    private Rectangle getMouseRectangle(Point mousePoint) {
        Rectangle rectangle = new Rectangle(mousePressedPoint,
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

        //Netbeans says this is never used, is there a side effect?
//        new DropTarget( thumbnailsPane, new JpoTransferrableDropTargetListener( this ) );

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
        final Runnable runnable = () -> {
            GroupNavigator groupNavigator = new GroupNavigator();
            groupNavigator.setNode(event.getNode());
            show(groupNavigator);
        };
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
    public void handleShowQueryRequest(ShowQueryRequest event) {
        show(new QueryNavigator(event.getQuery()));
    }

    /**
     * Remembers the last GroupInfo we picked so that we can attach a listener
     * to update the title if it changes
     */
    private SortableDefaultMutableTreeNode myLastGroupNode;

    /**
     * Instructs the ThumbnailPanelController to display the specified set of
     * nodes
     *
     * @param newNodeNavigator The Interface with the collection of nodes
     */
    private void show(NodeNavigatorInterface newNodeNavigator) {
        Tools.checkEDT();

        if (this.mySetOfNodes != null) {
            this.mySetOfNodes.removeNodeNavigatorListener(this);
        }
        this.mySetOfNodes = newNodeNavigator;
        newNodeNavigator.addNodeNavigatorListener(this);

        if (myLastGroupNode != null) {
            GroupInfo gi = (GroupInfo) myLastGroupNode.getUserObject();
            gi.removeGroupInfoChangeListener(myGroupInfoChangeListener);
        }
        myLastGroupNode = null;
        if (newNodeNavigator instanceof GroupNavigator) {
            myLastGroupNode = ((GroupNavigator) newNodeNavigator).getGroupNode();
            GroupInfo groupInfo = (GroupInfo) myLastGroupNode.getUserObject();
            groupInfo.addGroupInfoChangeListener(myGroupInfoChangeListener);
        }

        Settings.getPictureCollection().clearSelection();
        thumbnailJScrollPane.getVerticalScrollBar().setValue(0);
        startIndex = 0;
        nodeLayoutChanged();
    }

    /**
     * Listens for changes in the Group and updates the title if anything
     * changed
     */
    private final GroupInfoChangeListener myGroupInfoChangeListener = (GroupInfoChangeEvent groupInfoChangeEvent) -> {
        LOGGER.info("change event received.");
        updateTitle();
    };

    /**
     * Request that the ThumbnailPanel show the first page of Thumbnails
     */
    private void goToFirstPage() {
        startIndex = 0;
        thumbnailJScrollPane.getVerticalScrollBar().setValue(0);
        nodeLayoutChanged();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the previous page of Thumbnails
     */
    private void goToPreviousPage() {
        startIndex -= Settings.maxThumbnails;
        if (startIndex < 0) {
            startIndex = 0;
        }
        thumbnailJScrollPane.getVerticalScrollBar().setValue(0);
        nodeLayoutChanged();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the next page of Thumbnails
     */
    private void goToNextPage() {
        startIndex += Settings.maxThumbnails;
        thumbnailJScrollPane.getVerticalScrollBar().setValue(0);
        nodeLayoutChanged();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the last page of Thumbnails
     */
    private void goToLastPage() {
        int last = mySetOfNodes.getNumberOfNodes();
        int tgtPage = last / Settings.maxThumbnails;
        startIndex = tgtPage * Settings.maxThumbnails;
        thumbnailJScrollPane.getVerticalScrollBar().setValue(0);
        nodeLayoutChanged();
        setButtonStatus();
    }

    /**
     * creates the arrays for the thumbnailControllers and the descriptions and
     * adds them to the ThubnailPane.
     */
    private void initThumbnailsArray() {
        Tools.checkEDT();
        thumbnailControllers = new ThumbnailController[Settings.maxThumbnails];
        thumbnailDescriptionControllers = new ThumbnailDescriptionController[Settings.maxThumbnails];
        thumbnailsPane.removeAll();
        initialisedMaxThumbnails = Settings.maxThumbnails;
        for (int i = 0; i < Settings.maxThumbnails; i++) {
            thumbnailControllers[i] = new ThumbnailController(new Thumbnail(), Settings.thumbnailSize);
            thumbnailDescriptionControllers[i] = new ThumbnailDescriptionController();
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
        Tools.checkEDT();
        updateTitle();

        if (initialisedMaxThumbnails != Settings.maxThumbnails) {
            LOGGER.info(String.format("There are %d initialised thumbnails which is not equal to the defined maximum number of %d. Therefore reinitialising", initialisedMaxThumbnails, Settings.maxThumbnails));
            initThumbnailsArray();
        }

        setPageStats();
        setButtonStatus();

        for (int i = Settings.maxThumbnails - 1; i > -1; i--) {
            if (!thumbnailControllers[i].isSameNode(mySetOfNodes, i + startIndex)) {
                thumbnailControllers[i].setNode(mySetOfNodes, i + startIndex);
                thumbnailDescriptionControllers[i].setNode(mySetOfNodes.getNode(i + startIndex));
            }
        }
    }

    /**
     * Sets the text in the title for displaying page count information
     */
    private void setPageStats() {
        Tools.checkEDT();
        final int total = mySetOfNodes.getNumberOfNodes();
        final int lastOnPage = Math.min(startIndex + Settings.maxThumbnails, total);
        titleJPanel.lblPage.setText(String.format("Thumbnails %d to %d of %d", startIndex + 1, lastOnPage, total));
    }

    /**
     * Updates the title of the page. (The implementing method takes care that
     * it is on the EDT)
     */
    private void updateTitle() {
        LOGGER.fine(String.format("setting title to: %s", mySetOfNodes.getTitle()));
        titleJPanel.setTitle(mySetOfNodes.getTitle());
    }

    /**
     * This method sets whether the first, previous, next and last buttons are
     * visible or not
     */
    private void setButtonStatus() {
        Tools.checkEDT();
        if (startIndex == 0) {
            titleJPanel.firstThumbnailsPageButton.setEnabled(false);
            titleJPanel.previousThumbnailsPageButton.setEnabled(false);
        } else {
            titleJPanel.firstThumbnailsPageButton.setEnabled(true);
            titleJPanel.previousThumbnailsPageButton.setEnabled(true);
        }

        int count = mySetOfNodes.getNumberOfNodes();
        if ((startIndex + Settings.maxThumbnails) < count) {
            titleJPanel.lastThumbnailsPageButton.setEnabled(true);
            titleJPanel.nextThumbnailsPageButton.setEnabled(true);
        } else {
            titleJPanel.lastThumbnailsPageButton.setEnabled(false);
            titleJPanel.nextThumbnailsPageButton.setEnabled(false);
        }
    }

    /**
     * This method select all Thumbnails which are not null
     */
    private void selectAll() {
        SortableDefaultMutableTreeNode node;
        for (ThumbnailController thumbnailController : thumbnailControllers) {
            node = thumbnailController.getNode();
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
