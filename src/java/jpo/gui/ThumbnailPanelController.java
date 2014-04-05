package jpo.gui;

import com.google.common.eventbus.Subscribe;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import jpo.dataModel.NodeNavigatorListener;
import jpo.dataModel.NodeNavigatorInterface;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import jpo.EventBus.JpoEventBus;
import jpo.EventBus.ShowGroupRequest;
import jpo.EventBus.ShowQueryRequest;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.GroupInfoChangeEvent;
import jpo.dataModel.GroupInfoChangeListener;
import jpo.dataModel.GroupNavigator;
import jpo.dataModel.QueryNavigator;
import jpo.dataModel.Tools;
import jpo.gui.swing.ThumbnailPanelTitle;

/*
 ThumbnailPanelController.java:  a JScrollPane that shows thumbnailControllers

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
 * The ThumbnailPanelController manages a JPanel in a JScrollPane that displays
 * a group of pictures in a grid of thumbnailControllers or ad hoc search
 * results. Real pictures are shown as a thumbnail of the image whilst groups
 * are shown as a folder icon. Each thumbnail has it's caption under the image.
 * <p>
 * If the size of the component is changed the images are re-laid out and can
 * take advantage of the extra space if there is some.
 *
 */
public class ThumbnailPanelController
        implements NodeNavigatorListener {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( ThumbnailPanelController.class.getName() );

    /**
     * The title above the ThumbnailPanel
     */
    private final ThumbnailPanelTitle titleJPanel = new ThumbnailPanelTitle();

    /**
     * The Layout Manager used by the thumbnail panel
     */
    private final ThumbnailLayoutManager thumbnailLayoutManager = new ThumbnailLayoutManager();

    /**
     * The Panel that shows the Thumbnails
     */
    private final JPanel thumbnailPane = new JPanel( thumbnailLayoutManager );

    /**
     * The Scroll Pane that holds the Thumbnail Panel
     */
    private final JScrollPane thumbnailJScrollPane;

    /**
     * currently displayed page
     *
     */
    private int curPage = 1;

    /**
     * This object refers to the set of Nodes that is being browsed in the
     * ThumbnailPanelController
     */
    public NodeNavigatorInterface mySetOfNodes;

    /**
     * a variable to hold the current starting position of thumbnailControllers
     * being displayed out of a group or search. Range 0..count()-1
     * <p>
     *
     * This was invented to allow the number of thumbnailControllers to be
     * restricted so that <<Out of memory>> errors may be averted on long lists
     * of pictures.
     */
    private int startIndex;

    /**
     * An array that holds the 50 or so ThumbnailComponents that are being
     * displayed
     */
    private ThumbnailController[] thumbnailControllers;

    /**
     * An array that holds the 50 or so ThumbnailDescriptionJPanels that are
     * being displayed
     */
    private ThumbnailDescriptionJPanel[] thumbnailDescriptionJPanels;

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
     * Point where the mouse was pressed so that we can figure out the rectangle
     * that is being selected.
     */
    private Point mousePressedPoint;

    /**
     * Creates a new ThumbnailPanelController which in turn creates the view
     * objects and hooks itself up so that thumbnails can be shown
     *
     * @param thumbnailJScrollPane
     */
    public ThumbnailPanelController( JScrollPane thumbnailJScrollPane ) {
        this.thumbnailJScrollPane = thumbnailJScrollPane;
        Tools.checkEDT();
        initComponents();
        registerListeners();
    }

    /**
     * Registers the controller as a listener
     */
    private void registerListeners() {
        JpoEventBus.getInstance().register( this );
    }

    /**
     * Returns a handle to view widget being controlled by this controller
     *
     * @return The JScollPane widget
     */
    public JScrollPane getView() {
        return thumbnailJScrollPane;
    }

    @Subscribe
    public void handleShowGroupRequest( final ShowGroupRequest event ) {
        final Runnable r = new Runnable() {

            @Override
            public void run() {
                show( new GroupNavigator( event.getNode() ) );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }

    }

    @Subscribe
    public void handleShowQueryRequest( ShowQueryRequest event ) {
        show( new QueryNavigator( event.getQuery() ) );
    }

    /**
     * Remembers the last GroupInfo we picked so that we can attach a listener
     * to update the title if it changes
     */
    private SortableDefaultMutableTreeNode myLastGroupNode = null;

    /**
     * Instructs the ThumbnailPanelController to display the specified set of
     * nodes to be displayed
     *
     * @param newNodeNavigator The Interface with the collection of nodes
     */
    private void show( NodeNavigatorInterface newNodeNavigator ) {
        Tools.checkEDT();

        if ( this.mySetOfNodes != null ) {
            this.mySetOfNodes.removeNodeNavigatorListener( this );
            this.mySetOfNodes.getRid();
        }
        this.mySetOfNodes = newNodeNavigator;
        newNodeNavigator.addNodeNavigatorListener( this );  //Todo: investigate how we unattach these...

        if ( myLastGroupNode != null ) {
            GroupInfo gi = (GroupInfo) myLastGroupNode.getUserObject();
            gi.removeGroupInfoChangeListener( myGroupInfoChangeListener );
        }
        myLastGroupNode = null;
        if ( newNodeNavigator instanceof GroupNavigator ) {
            myLastGroupNode = ( (GroupNavigator) newNodeNavigator ).getGroupNode();
            GroupInfo gi = (GroupInfo) myLastGroupNode.getUserObject();
            gi.addGroupInfoChangeListener( myGroupInfoChangeListener );
        }

        Settings.pictureCollection.clearSelection();
        thumbnailJScrollPane.getVerticalScrollBar().setValue( 0 );
        startIndex = 0;
        curPage = 1;
        nodeLayoutChanged();
    }

    /**
     * Listens for changes in the Group and updates the title if anything
     * changed
     */
    private final GroupInfoChangeListener myGroupInfoChangeListener = new GroupInfoChangeListener() {

        @Override
        public void groupInfoChangeEvent( GroupInfoChangeEvent groupInfoChangeEvent ) {
            LOGGER.info( "change event received." );
            updateTitle();
        }
    };

    /**
     * Request that the ThumbnailPanel show the first page of Thumbnails
     */
    private void goToFirstPage() {
        if ( startIndex == 0 ) {
            return;
        }

        startIndex = 0;
        thumbnailJScrollPane.getVerticalScrollBar().setValue( 0 );
        curPage = 1;
        nodeLayoutChanged();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the previous page of Thumbnails
     */
    private void goToPreviousPage() {
        startIndex = startIndex - Settings.maxThumbnails;
        if ( startIndex < 0 ) {
            startIndex = 0;
        }
        thumbnailJScrollPane.getVerticalScrollBar().setValue( 0 );
        curPage--;
        nodeLayoutChanged();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the next page of Thumbnails
     */
    private void goToNextPage() {
        startIndex = startIndex + Settings.maxThumbnails;
        thumbnailJScrollPane.getVerticalScrollBar().setValue( 0 );
        curPage++;
        nodeLayoutChanged();
        setButtonStatus();
    }

    /**
     * Request that the ThumbnailPanel show the last page of Thumbnails
     */
    private void goToLastPage() {
        int last = mySetOfNodes.getNumberOfNodes();
        int tgtPage = last / Settings.maxThumbnails;
        curPage = tgtPage + 1;
        startIndex = tgtPage * Settings.maxThumbnails;
        thumbnailJScrollPane.getVerticalScrollBar().setValue( 0 );
        nodeLayoutChanged();
        setButtonStatus();
    }

    /**
     * Initialises the components for the ThumbnailController Pane
     */
    private void initComponents() {
        thumbnailJScrollPane.setViewportView( thumbnailPane );
        thumbnailPane.setBackground( Settings.JPO_BACKGROUND_COLOR );

        thumbnailJScrollPane.setMinimumSize( Settings.THUMBNAIL_JSCROLLPANE_MINIMUM_SIZE );
        thumbnailJScrollPane.setPreferredSize( Settings.thumbnailJScrollPanePreferredSize );
        thumbnailJScrollPane.setWheelScrollingEnabled( true );
        thumbnailJScrollPane.setFocusable( true );

        //  set the amount by which the panel scrolls down when the user clicks the
        //  little down or up arrow in the scrollbar
        thumbnailJScrollPane.getVerticalScrollBar().setUnitIncrement( 80 );

        //titleJPanel = new ThumbnailPanelTitle();
        thumbnailJScrollPane.setColumnHeaderView( titleJPanel );

        initThumbnailsArray();

        // Wire up the events
        titleJPanel.firstThumbnailsPageButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                goToFirstPage();
            }
        } );
        titleJPanel.previousThumbnailsPageButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                goToPreviousPage();
            }
        } );
        titleJPanel.nextThumbnailsPageButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                goToNextPage();
            }
        } );
        titleJPanel.lastThumbnailsPageButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent e ) {
                goToLastPage();
            }
        } );

        titleJPanel.resizeJSlider.addChangeListener( new SlideChangeListener() );

        JPanel whiteArea = new JPanel();
        thumbnailJScrollPane.setCorner( JScrollPane.UPPER_RIGHT_CORNER, whiteArea );

        thumbnailPane.addMouseListener( new MouseInputAdapter() {

            @Override
            public void mousePressed( MouseEvent e ) {
                mousePressedPoint = e.getPoint();
            }

            @Override
            public void mouseReleased( MouseEvent e ) {
                thumbnailJScrollPane.requestFocusInWindow();

                Graphics g = thumbnailPane.getGraphics();
                thumbnailPane.paint( g ); //cheap way of undoing old rectancle... TODO: use the glass pane

                Point mouseMovedToPoint = e.getPoint();
                Rectangle r = new Rectangle( mousePressedPoint,
                        new Dimension( mouseMovedToPoint.x - mousePressedPoint.x,
                                mouseMovedToPoint.y - mousePressedPoint.y ) );
                if ( mouseMovedToPoint.x < mousePressedPoint.x ) {
                    r.x = mouseMovedToPoint.x;
                    r.width = mousePressedPoint.x - mouseMovedToPoint.x;
                }
                if ( mouseMovedToPoint.y < mousePressedPoint.y ) {
                    r.y = mouseMovedToPoint.y;
                    r.height = mousePressedPoint.y - mouseMovedToPoint.y;
                }

                // I wonder why they don't put the following two lines into the SWING library but
                // let you work out this binary math on your own from the unhelpful description?
                boolean ctrlpressed = ( e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK ) == MouseEvent.CTRL_DOWN_MASK;
                boolean shiftpressed = ( e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK ) == MouseEvent.SHIFT_DOWN_MASK;

                if ( !( ctrlpressed | shiftpressed ) ) {
                    Settings.pictureCollection.clearSelection();
                }
                Rectangle thumbnailRectangle = new Rectangle();
                SortableDefaultMutableTreeNode n;
                for ( ThumbnailController thumbnailController : thumbnailControllers ) {
                    thumbnailController.getThumbnail().getBounds( thumbnailRectangle );
                    if ( r.intersects( thumbnailRectangle ) ) {
                        n = thumbnailController.getNode();
                        if ( n != null ) {
                            Settings.pictureCollection.addToSelectedNodes( n );
                        }
                    }
                }

            }
        } );

        thumbnailPane.addMouseMotionListener( new MouseInputAdapter() {

            @Override
            public void mouseDragged( MouseEvent e ) {

                Point mouseMovedToPoint = e.getPoint();
                Rectangle r = new Rectangle( mousePressedPoint,
                        new Dimension( mouseMovedToPoint.x - mousePressedPoint.x,
                                mouseMovedToPoint.y - mousePressedPoint.y ) );
                if ( mouseMovedToPoint.x < mousePressedPoint.x ) {
                    r.x = mouseMovedToPoint.x;
                    r.width = mousePressedPoint.x - mouseMovedToPoint.x;
                }
                if ( mouseMovedToPoint.y < mousePressedPoint.y ) {
                    r.y = mouseMovedToPoint.y;
                    r.height = mousePressedPoint.y - mouseMovedToPoint.y;
                }
                Graphics g = thumbnailPane.getGraphics();
                thumbnailPane.paint( g ); //cheap way of undoing old rectancle...
                g.drawRect( r.x, r.y, r.width, r.height );

                // find out if we need to scroll the window
                Rectangle viewRect = thumbnailJScrollPane.getViewport().getViewRect();
                JScrollBar verticalScrollBar = thumbnailJScrollPane.getVerticalScrollBar();
                final int scrolltrigger = 40;
                if ( mouseMovedToPoint.y - viewRect.y - viewRect.height > -scrolltrigger ) {
                    // LOGGER.info("must scroll down");
                    int increment = verticalScrollBar.getUnitIncrement( 1 );
                    int position = verticalScrollBar.getValue();
                    if ( position < verticalScrollBar.getMaximum() ) {
                        verticalScrollBar.setValue( position + increment );
                    }
                } else if ( mouseMovedToPoint.y - viewRect.y < scrolltrigger ) {
                    //logger.info("must scroll up");
                    int increment = verticalScrollBar.getUnitIncrement( 1 );
                    int position = verticalScrollBar.getValue();
                    if ( position > verticalScrollBar.getMinimum() ) {
                        verticalScrollBar.setValue( position - increment );
                    }

                }

            }
        } );

        thumbnailJScrollPane.addKeyListener(
                new KeyAdapter() {

                    @Override
                    public void keyReleased( KeyEvent e ) {
                        //logger.info("thumbnailJScrollPane: Trapped a KeyTyped event for key code: " + Integer.toString( e.getKeyCode() ) );
                        if ( e.getKeyCode() == KeyEvent.VK_A && e.isControlDown() ) {
                            //logger.info("thumbnailJScrollPane: Got a CTRL-A");
                            selectAll();
                        }
                    }
                } );
    }

    /**
     * Listens to the slider and changes the thumbnail size accordingly
     */
    private class SlideChangeListener
            implements ChangeListener {

        /**
         * *
         * Got a slider changed event.
         *
         * @param e
         */
        @Override
        public void stateChanged( ChangeEvent e ) {
            JSlider source = (JSlider) e.getSource();
            thumbnailSizeFactor = (float) source.getValue() / ThumbnailPanelTitle.THUMBNAILSIZE_MAX;
            thumbnailLayoutManager.setThumbnailWidth( (int) ( 350 * thumbnailSizeFactor ) );
            for ( int i = 0; i < Settings.maxThumbnails; i++ ) {
                thumbnailControllers[i].setFactor( thumbnailSizeFactor );
                thumbnailDescriptionJPanels[i].setFactor( thumbnailSizeFactor );
            }
            thumbnailLayoutManager.layoutContainer( thumbnailPane );
        }
    }

    /**
     * creates the arrays for the thumbnailControllers and the descriptions and
     * adds them to the ThubnailPane.
     */
    private void initThumbnailsArray() {
        Tools.checkEDT();
        thumbnailControllers = new ThumbnailController[Settings.maxThumbnails];
        thumbnailDescriptionJPanels = new ThumbnailDescriptionJPanel[Settings.maxThumbnails];
        thumbnailPane.removeAll();
        initialisedMaxThumbnails = Settings.maxThumbnails;
        for ( int i = 0; i < Settings.maxThumbnails; i++ ) {
            thumbnailControllers[i] = new ThumbnailController( Settings.thumbnailSize );
            thumbnailDescriptionJPanels[i] = new ThumbnailDescriptionJPanel();
            thumbnailPane.add( thumbnailControllers[i].getThumbnail() );
            thumbnailPane.add( thumbnailDescriptionJPanels[i] );
        }
    }

    /**
     * Assigns each of the ThumbnailControllers and ThumbnailDescriptionJPanels
     * the appropriate node from the Browser being shown.
     *
     * It also sets the title of the JScrollPane.
     */
    @Override
    public void nodeLayoutChanged() {
        Tools.checkEDT();
        if ( mySetOfNodes == null ) {
            LOGGER.severe( "ThumbnailPanelController nodeLayoutChanged was called with a null set of nodes. This is not right." );
            return;
        }

        updateTitle();

        if ( initialisedMaxThumbnails != Settings.maxThumbnails ) {
            LOGGER.info( String.format( "There are %d initialised thumbnails which is not equal to the defined maximum number of %d. Therefore reinitialising", initialisedMaxThumbnails, Settings.maxThumbnails ) );
            initThumbnailsArray();
        }

        setPageStats();
        setButtonStatus();

        for ( int i = 0; i < Settings.maxThumbnails; i++ ) {
            if ( !thumbnailControllers[i].isSameNode( mySetOfNodes, i + startIndex ) ) {
                thumbnailControllers[i].setNode( mySetOfNodes, i + startIndex );
                thumbnailDescriptionJPanels[i].setNode( mySetOfNodes.getNode( i + startIndex ) );
            } else {
                LOGGER.fine( String.format( "Node %d is unchanged", i ) );
            }
        }
    }

    /**
     * Sets the text in the title for displaying page count information
     */
    private void setPageStats() {
        Tools.checkEDT();
        final int total = mySetOfNodes.getNumberOfNodes();
        final int lastOnPage = Math.min( startIndex + Settings.maxThumbnails, total );
        titleJPanel.lblPage.setText( String.format( "Thumbnails %d to %d of %d", startIndex + 1, lastOnPage, total ) );
    }

    /**
     * Updates the title of the page. (The implementing method takes care that
     * it is on the EDT)
     */
    private void updateTitle() {
        LOGGER.fine( String.format( "setting title to: %s", mySetOfNodes.getTitle() ) );
        titleJPanel.setTitle( mySetOfNodes.getTitle() );
    }

    /**
     * This method sets whether the first, previous, next and last buttons are
     * visible or not
     */
    private void setButtonStatus() {
        Tools.checkEDT();
        if ( startIndex == 0 ) {
            titleJPanel.firstThumbnailsPageButton.setEnabled( false );
            titleJPanel.previousThumbnailsPageButton.setEnabled( false );
        } else {
            titleJPanel.firstThumbnailsPageButton.setEnabled( true );
            titleJPanel.previousThumbnailsPageButton.setEnabled( true );
        }

        int count = mySetOfNodes.getNumberOfNodes();
        if ( ( startIndex + Settings.maxThumbnails ) < count ) {
            titleJPanel.lastThumbnailsPageButton.setEnabled( true );
            titleJPanel.nextThumbnailsPageButton.setEnabled( true );
        } else {
            titleJPanel.lastThumbnailsPageButton.setEnabled( false );
            titleJPanel.nextThumbnailsPageButton.setEnabled( false );
        }
    }

    /**
     * This method select all Thumbnails which are not null
     */
    private void selectAll() {
        SortableDefaultMutableTreeNode node;
        for ( ThumbnailController thumbnailController : thumbnailControllers ) {
            node = thumbnailController.getNode();
            if ( node != null ) {
                Settings.pictureCollection.addToSelectedNodes( node );
            }
        }
    }
}
