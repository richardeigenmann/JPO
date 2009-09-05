package jpo.gui;

import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.*;
import jpo.dataModel.PictureInfo;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;
import java.util.*;
import java.util.logging.Logger;
import jpo.gui.swing.ThumbnailPanel;

/*
ThumbnailJScrollPane.java:  a JScrollPane that shows thumbnails

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
 *  The ThumbnailJScrollPane manages a JPanel in a JScrollPane that displays a group of pictures
 *  in a grid of thumbnails or ad hoc search results. Real pictures are shown as a thumbnail
 *  of the image whilst sub-groups are shown as a folder icon. Each thumbnail has it's caption
 *  under the image. <p>
 *
 *  If the size of the component is changed the images are re-laid out and can take advantage of the
 *  extra space if there is some.
 *
 */
public class ThumbnailJScrollPane implements RelayoutListener {

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( ThumbnailJScrollPane.class.getName() );


    /**
     * Returns a handle to the JScrollPane widget
     * @return The JScollPane widget
     */
    public JScrollPane getJScrollPane() {
        return thumbnailPanel;
    }


    /**
     *   Instructs the ThumbnailJScrollPane to display the specified set of nodes to be displayed
     *   @param mySetOfNodes 	The Interface with the collection of nodes
     */
    public void show( ThumbnailBrowserInterface mySetOfNodes ) {
        if ( this.mySetOfNodes != null ) {
            this.mySetOfNodes.removeRelayoutListener( this );
            this.mySetOfNodes.cleanup();
        }
        this.mySetOfNodes = mySetOfNodes;
        mySetOfNodes.addRelayoutListener( this );  //Todo, investigate how we unattach these...

        Runnable r = new Runnable() {

            public void run() {
                clearSelection();
                thumbnailPanel.getVerticalScrollBar().setValue( 0 );
                startIndex = 0;
                curPage = 1;
                assignThumbnails();
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }

    /**
     *  color for title
     */
    private static final Color GRAY_BLUE_LIGHT = new Color( 204, 204, 255 );

    /**
     *  color for title
     */
    private static final Color GRAY_BLUE_DARK = new Color( 51, 51, 102 );

    /**
     *  the JLabel that holds the description of the group being shown.
     */
    private JLabel title = new JLabel();

    /**
     * JLabel for holding the thumbnail counts
     * */
    private JLabel lblPage = new JLabel();	// JA

    /**
     * currently displayed page
     * */
    private int curPage = 1;

    /**
     *   A Thread that will load the images.
     *
     * private ThumbnailLoaderThread tl = null; */
    /**
     *  variable that will signal to the thread to stop loading images.
     */
    public boolean stopThread;

    /**
     *  This object refers to the set of Nodes that is being browsed in the ThumbnailJScrollPane
     */
    public ThumbnailBrowserInterface mySetOfNodes;

    /**
     *  a variable to hold the current starting position of thumbnails being
     *  displayed out of a group or search. Range 0..count()-1<p>
     *
     *  This was invented to allow the number of thumbnails to be restricted
     *  so that <<Out of memory>> errors may be averted on long lists of
     *  pictures.
     *
     **/
    private int startIndex;

    /**
     *  a button to nagivate back to the first page
     **/
    private JButton firstThumbnailsPageButton =
            new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_first.gif" ) ) );

    /**
     *  a button to navigate to the next page
     **/
    private JButton nextThumbnailsPageButton =
            new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/Forward24.gif" ) ) );

    /**
     *  a button to naviage to the last page
     **/
    private JButton lastThumbnailsPageButton =
            new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_last.gif" ) ) );

    /**
     *  a button to navigate to the first page
     **/
    private JButton previousThumbnailsPageButton =
            new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/Back24.gif" ) ) );

    /**
     *   An array that holds the 50 or so ThumbnailComponents that are being displayed
     */
    private Thumbnail[] thumbnails;

    /**
     *   An array that holds the 50 or so ThumbnailDescriptionJPanels that are being displayed
     */
    private ThumbnailDescriptionJPanel[] thumbnailDescriptionJPanels;

    /**
     *   The amount of horizontal padding between the thumbnails and descriptions.
     */
    private static final int horizontalPadding = 10;

    /**
     *   The amount of vertical padding between the thumbnails and descriptions.
     */
    private static final int verticalPadding = 10;

    /**
     *  This variable keeps track of how many thumbnails per page the component was initialised
     *  with. If the number changes because the user changed it in the settings then the difference
     *  is recognised and the arrays are recreated.
     */
    private int initialisedMaxThumbnails = Integer.MIN_VALUE;

    /**
     *  The largest size for the thumbnail slider
     */
    private static final int THUMBNAILSIZE_MIN = 5;

    /**
     *  The smallest size for the thumbnail slider
     */
    private static final int THUMBNAILSIZE_MAX = 20;

    /**
     *  The starting position for the thumbnail slider
     */
    private static final int THUMBNAILSIZE_INIT = 20;

    /**
     *   Slider to control the size of the thumbnails
     */
    private JSlider resizeJSlider = new JSlider( JSlider.HORIZONTAL,
            THUMBNAILSIZE_MIN, THUMBNAILSIZE_MAX, THUMBNAILSIZE_INIT );

    /**
     *  Factor for the Thumbnails
     */
    private float thumbnailSizeFactor = 1;

    /**
     *  Point where the mouse was pressed
     */
    private Point mousePressedPoint;


    /**
     *   creates a new JScrollPane with an embedded JPanel and provides a set of
     *   methods that allow thumbnails to be displayed. <p>
     *
     *   The passing in of the caller is obsolete and should be removed when
     *   a better interface type solution has been built.
     *
     */
    public ThumbnailJScrollPane() {
        Runnable r = new Runnable() {

            public void run() {
                initComponents();
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }

    private ThumbnailPanel thumbnailPanel;


    private void initComponents() {
        thumbnailPanel = new ThumbnailPanel();

        initThumbnailsArray();

        firstThumbnailsPageButton.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
        firstThumbnailsPageButton.setPreferredSize( new Dimension( 25, 25 ) );
        firstThumbnailsPageButton.setVerticalAlignment( JLabel.CENTER );
        firstThumbnailsPageButton.setOpaque( false );
        firstThumbnailsPageButton.setVisible( false );
        firstThumbnailsPageButton.setFocusPainted( false );
        firstThumbnailsPageButton.setToolTipText( Settings.jpoResources.getString( "ThumbnailToolTipPrevious" ) );
        firstThumbnailsPageButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                if ( startIndex == 0 ) {
                    return;
                }

                startIndex = 0;
                thumbnailPanel.getVerticalScrollBar().setValue( 0 );
                curPage = 1;
                assignThumbnails();
                setButtonVisibility();
            }
        } );


        previousThumbnailsPageButton.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
        previousThumbnailsPageButton.setPreferredSize( new Dimension( 25, 25 ) );
        previousThumbnailsPageButton.setVerticalAlignment( JLabel.CENTER );
        previousThumbnailsPageButton.setOpaque( false );
        previousThumbnailsPageButton.setVisible( false );
        previousThumbnailsPageButton.setFocusPainted( false );
        previousThumbnailsPageButton.setToolTipText( Settings.jpoResources.getString( "ThumbnailToolTipPrevious" ) );
        previousThumbnailsPageButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                startIndex = startIndex - Settings.maxThumbnails;
                if ( startIndex < 0 ) {
                    startIndex = 0;
                }
                thumbnailPanel.getVerticalScrollBar().setValue( 0 );
                curPage--;
                assignThumbnails();
                setButtonVisibility();
            }
        } );

        nextThumbnailsPageButton.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
        nextThumbnailsPageButton.setPreferredSize( new Dimension( 25, 25 ) );
        nextThumbnailsPageButton.setVerticalAlignment( JLabel.CENTER );
        nextThumbnailsPageButton.setOpaque( false );
        nextThumbnailsPageButton.setVisible( false );
        nextThumbnailsPageButton.setFocusPainted( false );
        nextThumbnailsPageButton.setToolTipText( Settings.jpoResources.getString( "ThumbnailToolTipNext" ) );
        nextThumbnailsPageButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                startIndex = startIndex + Settings.maxThumbnails;
                thumbnailPanel.getVerticalScrollBar().setValue( 0 );
                curPage++;
                assignThumbnails();
                setButtonVisibility();
            }
        } );

        lastThumbnailsPageButton.setBorder( BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
        lastThumbnailsPageButton.setPreferredSize( new Dimension( 25, 25 ) );
        lastThumbnailsPageButton.setVerticalAlignment( JLabel.CENTER );
        lastThumbnailsPageButton.setOpaque( false );
        lastThumbnailsPageButton.setVisible( false );
        lastThumbnailsPageButton.setFocusPainted( false );
        lastThumbnailsPageButton.setToolTipText( Settings.jpoResources.getString( "ThumbnailToolTipNext" ) );
        lastThumbnailsPageButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                int last = mySetOfNodes.getNumberOfNodes();
                int tgtPage = last / Settings.maxThumbnails;
                curPage = tgtPage + 1;
                startIndex = tgtPage * Settings.maxThumbnails;
                thumbnailPanel.getVerticalScrollBar().setValue( 0 );
                assignThumbnails();
                setButtonVisibility();
            }
        } );


        title.setFont( Settings.titleFont );

        JPanel titleJPanel = new JPanel();
        BoxLayout bl = new BoxLayout( titleJPanel, BoxLayout.X_AXIS );
        titleJPanel.setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );	// JA
        titleJPanel.setLayout( bl );
        titleJPanel.setBackground( Color.LIGHT_GRAY );
        titleJPanel.add( Box.createRigidArea( new Dimension( 5, 0 ) ) );
        titleJPanel.add( firstThumbnailsPageButton );
        titleJPanel.add( previousThumbnailsPageButton );
        titleJPanel.add( nextThumbnailsPageButton );
        titleJPanel.add( lastThumbnailsPageButton );
        lblPage.setBorder( BorderFactory.createEmptyBorder( 0, 10, 0, 10 ) );					// JA
        titleJPanel.add( lblPage );
        titleJPanel.add( title );
        resizeJSlider.setSnapToTicks( false );
        resizeJSlider.setMaximumSize( new Dimension( 150, 40 ) );
        resizeJSlider.setMajorTickSpacing( 4 );
        resizeJSlider.setMinorTickSpacing( 2 );
        resizeJSlider.setPaintTicks( true );
        resizeJSlider.setPaintLabels( false );
        titleJPanel.add( resizeJSlider );

        resizeJSlider.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                JSlider source = (JSlider) e.getSource();
                thumbnailSizeFactor = (float) source.getValue() / THUMBNAILSIZE_MAX;
                thumbnailPanel.thumbnailLayout.setThumbnailWidth( (int) ( 350 * thumbnailSizeFactor ) );
                for ( int i = 0; i < Settings.maxThumbnails; i++ ) {
                    thumbnails[i].setFactor( thumbnailSizeFactor );
                    thumbnailDescriptionJPanels[i].setFactor( thumbnailSizeFactor );
                }
                thumbnailPanel.thumbnailLayout.layoutContainer( thumbnailPanel.ThumbnailPane );
            }
        } );



        thumbnailPanel.setColumnHeaderView( titleJPanel );

        JPanel whiteArea = new JPanel();
        thumbnailPanel.setCorner( JScrollPane.UPPER_RIGHT_CORNER, whiteArea );

        thumbnailPanel.ThumbnailPane.addMouseListener( new MouseInputAdapter() {

            @Override
            public void mousePressed( MouseEvent e ) {
                mousePressedPoint = e.getPoint();
            }


            @Override
            public void mouseReleased( MouseEvent e ) {
                thumbnailPanel.requestFocusInWindow();

                Graphics g = thumbnailPanel.ThumbnailPane.getGraphics();
                thumbnailPanel.ThumbnailPane.paint( g ); //cheap way of undoing old rectancle... TODO: use the glass pane

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
                    clearSelection();
                }
                Rectangle thumbnailRectangle = new Rectangle();
                SortableDefaultMutableTreeNode n;
                for ( int i = 0; i < thumbnails.length; i++ ) {
                    thumbnails[i].getBounds( thumbnailRectangle );
                    if ( r.intersects( thumbnailRectangle ) ) {
                        n = thumbnails[i].referringNode;
                        if ( n != null ) {
                            setSelected( n );
                        }
                    }
                }


            }
        } );

        thumbnailPanel.ThumbnailPane.addMouseMotionListener( new MouseInputAdapter() {

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
                Graphics g = thumbnailPanel.ThumbnailPane.getGraphics();
                thumbnailPanel.ThumbnailPane.paint( g ); //cheap way of undoing old rectancle...
                g.drawRect( r.x, r.y, r.width, r.height );

                // find out if we need to scroll the window
                Rectangle viewRect = thumbnailPanel.getViewport().getViewRect();
                JScrollBar verticalScrollBar = thumbnailPanel.getVerticalScrollBar();
                final int scrolltrigger = 40;
                if ( mouseMovedToPoint.y - viewRect.y - viewRect.height > -scrolltrigger ) {
                    // logger.info("must scroll down");
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


        thumbnailPanel.addKeyListener(
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
     *  creates the arrays for the thumbnails and the descriptions and adds them to the ThubnailPane.
     */
    public void initThumbnailsArray() {
        thumbnails = new Thumbnail[Settings.maxThumbnails];
        thumbnailDescriptionJPanels = new ThumbnailDescriptionJPanel[Settings.maxThumbnails];
        thumbnailPanel.ThumbnailPane.removeAll();
        initialisedMaxThumbnails = Settings.maxThumbnails;
        for ( int i = 0; i < Settings.maxThumbnails; i++ ) {
            thumbnails[i] = new Thumbnail( this );
            thumbnailDescriptionJPanels[i] = new ThumbnailDescriptionJPanel();
            thumbnailPanel.ThumbnailPane.add( thumbnails[i] );
            thumbnailPanel.ThumbnailPane.add( thumbnailDescriptionJPanels[i] );
        }
    }


    /**
     *  this method runs through all the thumbnails on the panel and makes sure they are set to the
     *  correct node. It also sets the tile of the JScrollPane.
     */
    public void assignThumbnails() {
        if ( mySetOfNodes == null ) {
            return;
        }

        setTitle( mySetOfNodes.getTitle() );

        if ( initialisedMaxThumbnails != Settings.maxThumbnails ) {
            initThumbnailsArray();
        }

        int groupCount = mySetOfNodes.getNumberOfNodes();

        setPageStats();

        setButtonVisibility();
        // take the thumbnails off the creation queue if they were on it.
        // as setNode is now internally synchronised this can slow down removal
        // from the queue
        for ( int i = 0; i < Settings.maxThumbnails; i++ ) {
            thumbnails[i].unqueue();
        }
        for ( int i = 0; i < Settings.maxThumbnails; i++ ) {
            thumbnails[i].setNode( mySetOfNodes, i + startIndex );
            thumbnailDescriptionJPanels[i].setNode( mySetOfNodes.getNode( i + startIndex ) );
        }
    }

    /**
     *   This is a reference to the assoicated CollectionJTreeController which allows group show requests
     *   to be passed between the Thumbnail pane and the JTree.
     */
    public CollectionJTreeController associatedCollectionJTree;

    /**
     *   This is a reference to the assoicated InfoPanel which shows info from selections
     */
    public InfoPanelController associatedInfoPanel;


    /**
     *   sets the assoiciated JTree
     * @param associatedCollectionJTree
     */
    public void setAssociatedCollectionJTree( CollectionJTreeController associatedCollectionJTree ) {
        this.associatedCollectionJTree = associatedCollectionJTree;
    }


    /**
     *   sets the assoiciated InfoPanel
     * @param associatedInfoPanel
     */
    public void setAssociatedInfoPanel( InfoPanelController associatedInfoPanel ) {
        this.associatedInfoPanel = associatedInfoPanel;
    }


    /**
     *   returns the associated JTree
     * @return the associated the Jtree
     */
    public CollectionJTreeController getAssociatedCollectionJTree() {
        return associatedCollectionJTree;
    }


    /**
     *   returns the associated InfoPanelController
     * @return the associated info panel controller
     */
    public InfoPanelController getAssociatedInfoPanel() {
        return associatedInfoPanel;
    }


    /**
     *   changes the title at the top of the page
     *
     * @param	titleString	The text to be printed across the top
     *				of all columns. Usually this will be
     *				the name of the group
     */
    public void setTitle( final String titleString ) {
        Runnable r = new Runnable() {

            public void run() {
                title.setText( titleString );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }


    /**
     * Sets the text in the title for displaying page count information
     */
    private void setPageStats() {
        final int total = mySetOfNodes.getNumberOfNodes();
        final int lastOnPage = Math.min( startIndex + Settings.maxThumbnails, total );
        Runnable r = new Runnable() {

            public void run() {
                lblPage.setText( String.format( "Thumbnails %d to %d of %d", startIndex + 1, lastOnPage, total ) );
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }


    /**
     *  This method sets whether the first, previous, next and last buttons are visible or not
     */
    public void setButtonVisibility() {
        Runnable r = new Runnable() {

            public void run() {
                if ( startIndex == 0 ) {
                    firstThumbnailsPageButton.setVisible( false );
                    previousThumbnailsPageButton.setVisible( false );
                } else {
                    firstThumbnailsPageButton.setVisible( true );
                    if ( startIndex > Settings.maxThumbnails ) {
                        previousThumbnailsPageButton.setVisible( true );
                    } else {
                        // it's plenty for one back button to be shown if we are on page 1
                        previousThumbnailsPageButton.setVisible( false );
                    }
                }

                int count = mySetOfNodes.getNumberOfNodes();
                lblPage.setVisible( count != 0 );
                if ( ( startIndex + Settings.maxThumbnails ) < count ) {
                    lastThumbnailsPageButton.setVisible( true );
                } else {
                    lastThumbnailsPageButton.setVisible( false );
                }
                if ( ( startIndex + ( 2 * Settings.maxThumbnails ) ) < count ) {
                    nextThumbnailsPageButton.setVisible( true );
                } else {
                    nextThumbnailsPageButton.setVisible( false );
                }
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            r.run();
        } else {
            SwingUtilities.invokeLater( r );
        }
    }

    /**
     *   This Hash Set hold references to the selected nodes.
     */
    public final HashSet<SortableDefaultMutableTreeNode> selection = new HashSet<SortableDefaultMutableTreeNode>();


    /**
     *  This method places the current {@link SortableDefaultMutableTreeNode} into the selection HashSet.
     * @param node
     */
    public void setSelected( SortableDefaultMutableTreeNode node ) {
        selection.add( node );
        Object userObject = node.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            ( (PictureInfo) userObject ).sendWasSelectedEvent();
        }
    }


    /**
     *  This method select all Thumbnails which are not null
     */
    public void selectAll() {
        SortableDefaultMutableTreeNode n;
        for ( int i = 0; i < thumbnails.length; i++ ) {
            n = thumbnails[i].referringNode;
            if ( n != null ) {
                setSelected( n );
            }
        }
    }


    /**
     *  This method clears selection HashSet that refers to the selected highlighted thumbnails.
     */
    public void clearSelection() {
        Iterator i = selection.iterator();
        Object o;
        Object userObject;
        while ( i.hasNext() ) {
            o = i.next();
            i.remove();
            userObject = ( (SortableDefaultMutableTreeNode) o ).getUserObject();
            if ( userObject instanceof PictureInfo ) {
                ( (PictureInfo) userObject ).sendWasUnselectedEvent();

            }
        }
    }


    /**
     *  This method removes the current SDMTN from the selection HashSet.
     * @param node
     */
    public void removeFromSelection( SortableDefaultMutableTreeNode node ) {
        selection.remove( node );
        Object userObject = node.getUserObject();
        if ( userObject instanceof PictureInfo ) {
            ( (PictureInfo) userObject ).sendWasUnselectedEvent();
        }
    }


    /**
     *  This returns whether the SDMTN is part of the selection HashSet.
     * @param node
     * @return true if the node is selected
     */
    public boolean isSelected( SortableDefaultMutableTreeNode node ) {
        try {
            return selection.contains( node );
        } catch ( NullPointerException x ) {
            return false;
        }
    }


    /**
     *  returns an array of the selected nodes.
     * @return an array of the selected nodes
     */
    public Object[] getSelectedNodes() {
        return selection.toArray();
    }


    /**
     *  returns a Vector of the selected nodes.
     * @return a vector of the selected nodes
     */
    public Vector<SortableDefaultMutableTreeNode> getSelectedNodesAsVector() {
        return new Vector<SortableDefaultMutableTreeNode>( selection );
    }


    /**
     *  returns the amount of selected nodes
     * @return
     */
    public int countSelectedNodes() {
        return selection.size();
    }
}








