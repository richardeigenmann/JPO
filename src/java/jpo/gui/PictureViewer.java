package jpo.gui;

import jpo.dataModel.FlatGroupNavigator;
import jpo.gui.swing.ResizableJFrame;
import jpo.gui.swing.PicturePane;
import java.awt.event.FocusEvent;
import jpo.dataModel.Settings;
import jpo.*;
import jpo.dataModel.PictureInfo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import jpo.dataModel.ExifInfo;
import jpo.dataModel.NodeNavigator;
import jpo.dataModel.PictureInfoChangeEvent;
import jpo.dataModel.PictureInfoChangeListener;
import jpo.dataModel.RandomNavigator;
import jpo.dataModel.RelayoutListener;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;


/*
PictureViewer.java:  Controller and Viewer class that browses a set of pictures.

Copyright (C) 2002 - 2010  Richard Eigenmann, Zürich, Switzerland
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
 *   PictureViewer is a Controller that manages a window which displays a picture.
 *   It provides navigation control over
 *   the collection as well as mouse and keyboard control over the zooming. 
 *
 *   The user can zoom in on a picture coordinate by clicking the left mouse button. The middle
 *   button scales the picture so that it fits in the available space and centres it there.
 *   The right mouse button zooms out.<p>
 *
 *
 *   <img src="../PictureViewer.png" border=0>
 **/
public class PictureViewer
        implements ScalablePictureListener,
        AdvanceTimerInterface,
        ChangeWindowInterface,
        PictureInfoChangeListener,
        //TreeModelListener,
        RelayoutListener {

    /**
     *   The pane that handles the image drawing aspects.
     **/
    private PicturePane pictureJPanel = new PicturePane();


    /**
     *  Brings up a window in which a picture node is displayed. This class
     *  handles all the user interaction such as zoom in / out, drag, navigation,
     *  information display and keyboard keys.
     **/
    public PictureViewer() {
        initGui();
        //Settings.pictureCollection.getTreeModel().addTreeModelListener( this );
        pictureJPanel.addStatusListener( this );

        // register an interest in mouse events
        Listener MouseListener = new Listener();
        pictureJPanel.addMouseListener( MouseListener );
        pictureJPanel.addMouseMotionListener( MouseListener );

    }

    /**
     * Defines a logger for this class
     */
    private static Logger logger = Logger.getLogger( PictureViewer.class.getName() );

    /**
     *  indicator that specifies what sort of window should be created
     */
    private int windowMode = ResizableJFrame.WINDOW_DEFAULT;


    /**
     *   method to close the PictureViewer and all dangling references.
     */
    public void closeViewer() {
        //getRid the old navigator
        mySetOfNodes.removeRelayoutListener( this );
        mySetOfNodes.getRid(); // help Grabage collection remove the listener
        stopTimer();
        closeMyWindow();
    }
    // GUI Widgets

    /**
     *  The Window in which the viewer will place it's components.
     **/
    private ResizableJFrame myJFrame;

    /**
     *  The root JPanel
     */
    private JPanel viewerPanel = new JPanel();

    /**
     *   progress bar to track the pictures loaded so far
     */
    private JProgressBar loadJProgressBar = new JProgressBar();

    /**
     *   This textarea shows the description of the picture being shown
     **/
    private JTextArea descriptionJTextField = new JTextArea();

    /**
     *  Navigation Panel
     */
    private PictureViewerNavBar navBar;


    /**
     *  This method creates all the GUI widgets and connects them for the
     *  PictureViewer.
     */
    private void initGui() {
        Tools.checkEDT();
        createWindow();

        viewerPanel.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        viewerPanel.setOpaque( true );
        viewerPanel.setFocusable( false );

        GridBagConstraints c = new GridBagConstraints();
        viewerPanel.setLayout( new GridBagLayout() );

        // Picture Painter Pane
        pictureJPanel.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        pictureJPanel.setVisible( true );
        pictureJPanel.setOpaque( true );
        pictureJPanel.setFocusable( true );
        pictureJPanel.addKeyListener( myViewerKeyAdapter );
        c.weightx = 1;
        c.weighty = 0.99f;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.NORTH;
        viewerPanel.add( pictureJPanel, c );

        loadJProgressBar.setPreferredSize( new Dimension( 120, 20 ) );
        loadJProgressBar.setMaximumSize( new Dimension( 140, 20 ) );
        loadJProgressBar.setMinimumSize( new Dimension( 80, 20 ) );
        loadJProgressBar.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        loadJProgressBar.setBorderPainted( true );
        loadJProgressBar.setBorder( BorderFactory.createLineBorder( Color.gray, 1 ) );

        loadJProgressBar.setMinimum( 0 );
        loadJProgressBar.setMaximum( 100 );
        loadJProgressBar.setStringPainted( true );
        loadJProgressBar.setVisible( false );

        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.EAST;
        viewerPanel.add( loadJProgressBar, c );

        // The Description_Panel
        descriptionJTextField.setFont( Font.decode( Settings.jpoResources.getString( "PictureViewerDescriptionFont" ) ) );
        descriptionJTextField.setWrapStyleWord( true );
        descriptionJTextField.setLineWrap( true );
        descriptionJTextField.setEditable( true );
        descriptionJTextField.setForeground( Settings.PICTUREVIEWER_TEXT_COLOR );
        descriptionJTextField.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        descriptionJTextField.setOpaque( true );
        descriptionJTextField.setBorder( new EmptyBorder( 2, 12, 0, 0 ) );
        descriptionJTextField.setMinimumSize( new Dimension( 80, 26 ) );
        descriptionJTextField.addFocusListener( new FocusAdapter() {

            @Override
            public void focusLost( FocusEvent e ) {
                super.focusLost( e );
                updateDescription();
            }
        } );

        JScrollPane descriptionJScrollPane = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        descriptionJScrollPane.setViewportView( descriptionJTextField );
        descriptionJScrollPane.setBorder( new EmptyBorder( 0, 0, 0, 0 ) );
        descriptionJScrollPane.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        descriptionJScrollPane.setOpaque( true );
        c.weightx = 1;
        c.weighty = 0.01;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        viewerPanel.add( descriptionJScrollPane, c );


        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        navBar = new PictureViewerNavBar( this );
        viewerPanel.add( navBar, c );
    }


    /**
     *  Method that creates the JFrame and attaches the viewerPanel to it.
     **/
    private void createWindow() {
        Dimension initialDimension = (Dimension) Settings.pictureViewerDefaultDimensions.clone();
        if ( ( initialDimension.width == 0 ) || ( initialDimension.height == 0 ) ) {
            // this gets us around the problem that the Affine Transform crashes if the window size is 0,0
            initialDimension = Settings.windowSizes[1];
        }
        myJFrame = new ResizableJFrame( Settings.jpoResources.getString( "PictureViewerTitle" ), decorateWindow, initialDimension );
        myJFrame.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );


        if ( Settings.maximisePictureViewerWindow ) {
            windowMode = ResizableJFrame.WINDOW_FULLSCREEN;
        }
        myJFrame.resizeTo( windowMode );
        myJFrame.addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent we ) {
                closeViewer();
            }
        } );

        // set layout manager and add the PictureViewer Panel
        myJFrame.getContentPane().setLayout( new BorderLayout() );
        myJFrame.getContentPane().add( "Center", viewerPanel );
    }


    /**
     *   method to close the active window.
     */
    private void closeMyWindow() {
        if ( myJFrame != null ) {
            myJFrame.dispose();
            myJFrame = null;
        }
    }

    /**
     * Flag that specifies whether the window should be drawn with decoration
     * or not.
     */
    private transient boolean decorateWindow = true;


    /**
     *  request that the window showing the picture be changed be changed.
     *  @param  newMode  {@link ResizableJFrame#WINDOW_FULLSCREEN}, {@link ResizableJFrame#WINDOW_LEFT},
     *		{@link ResizableJFrame#WINDOW_RIGHT},  {@link ResizableJFrame#WINDOW_TOP_LEFT},
     *		{@link ResizableJFrame#WINDOW_TOP_RIGHT}, {@link ResizableJFrame#WINDOW_BOTTOM_LEFT},
     *		{@link ResizableJFrame#WINDOW_BOTTOM_RIGHT} or {@link ResizableJFrame#WINDOW_DEFAULT}
     *		need to be indicated.
     *
     */
    public void switchWindowMode( final int newMode ) {
        logger.fine( "PictureViewer.switchWindowMode: old mode: " + Integer.toString( windowMode ) + " new: " + Integer.toString( newMode ) );
        windowMode = newMode;
        boolean newDecoration = decorateWindow;
        // some intelligence as to when to have window decorations and when not.
        switch ( newMode ) {
            case ResizableJFrame.WINDOW_FULLSCREEN:
                newDecoration = false;
                break;
            case ResizableJFrame.WINDOW_LEFT:
                newDecoration = false;
                break;
            case ResizableJFrame.WINDOW_RIGHT:
                newDecoration = false;
                break;
            case ResizableJFrame.WINDOW_TOP_LEFT:
                newDecoration = true;
                break;
            case ResizableJFrame.WINDOW_TOP_RIGHT:
                newDecoration = true;
                break;
            case ResizableJFrame.WINDOW_BOTTOM_LEFT:
                newDecoration = true;
                break;
            case ResizableJFrame.WINDOW_BOTTOM_RIGHT:
                newDecoration = true;
                break;
            case ResizableJFrame.WINDOW_DEFAULT:
                newDecoration = true;
                break;
        }
        //switchDecorations( newDecoration );
        myJFrame.resizeTo( windowMode );
    }


    /**
     *  This method turns on or turns off the frame around the window. It works by closing
     *  the window and creating a new one with the correct decorations. It uses the decorateWindow
     *  flag to determine if the decorations are being shown.
     * @param newDecoration
     */
    public void switchDecorations( boolean newDecoration ) {
        if ( decorateWindow != newDecoration ) {
            decorateWindow = newDecoration;
            myJFrame.getContentPane().remove( viewerPanel );
            closeMyWindow();
            createWindow();
        }
    }
// End of GUI Widgets

    /**
     *  the context of the browsing
     */
    private NodeNavigator mySetOfNodes = null;

    /**
     *  the position in the context being shown
     */
    private int myIndex = 0;


    /**
     * Returns the current Node.
     * @return The current node as defined by the mySetOfNodes 
     * NodeNavigatorInterface and the myIndex. If the set of nodes has not
     * been initialised or there is some other error null shall be returned.
     */
    public SortableDefaultMutableTreeNode getCurrentNode() {
        try {
            return mySetOfNodes.getNode( myIndex );
        } catch ( NullPointerException npe ) {
            logger.warning( String.format( "Got a npe on node %d. Message: %s", myIndex, npe.getMessage() ) );
            return null;
        }

    }

    /**
     *  variable which controls whether the autoadvance cycles through the current
     *  group only or whether it is allowed to cycle through images in the collection
     */
    private boolean cycleAll = true;

    /**
     *  the timer that can call back into the object with the instruction to
     *  load the next image
     */
    private AdvanceTimer advanceTimer = null;

    /**
     *  popup menu for window mode changing
     */
    private ChangeWindowPopupMenu changeWindowPopupMenu = new ChangeWindowPopupMenu( this );

    private ViewerKeyAdapter myViewerKeyAdapter = new ViewerKeyAdapter();

    private class ViewerKeyAdapter
            extends KeyAdapter {

        /**
         *  method that analysed the key that was pressed
         */
        @Override
        public void keyPressed( KeyEvent e ) {
            int k = e.getKeyCode();
            if ( ( k == KeyEvent.VK_I ) ) {
                pictureJPanel.cylceInfoDisplay();
            } else if ( ( k == KeyEvent.VK_N ) ) {
                requestNextPicture();
            } else if ( ( k == KeyEvent.VK_M ) ) {
                requestPopupMenu();
            } else if ( ( k == KeyEvent.VK_P ) ) {
                requestPriorPicture();
            } else if ( ( k == KeyEvent.VK_F ) ) {
                requestScreenSizeMenu();
            } else if ( ( k == KeyEvent.VK_SPACE ) || ( k == KeyEvent.VK_HOME ) ) {
                resetPicture();
            } else if ( ( k == KeyEvent.VK_PAGE_UP ) ) {
                pictureJPanel.zoomIn();
            } else if ( ( k == KeyEvent.VK_PAGE_DOWN ) ) {
                pictureJPanel.zoomOut();
            } else if ( ( k == KeyEvent.VK_1 ) ) {
                pictureJPanel.zoomFull();
            } else if ( ( k == KeyEvent.VK_UP ) || ( k == KeyEvent.VK_KP_UP ) ) {
                pictureJPanel.scrollDown();
            } else if ( ( k == KeyEvent.VK_DOWN ) || ( k == KeyEvent.VK_KP_DOWN ) ) {
                pictureJPanel.scrollUp();
            } else if ( ( k == KeyEvent.VK_LEFT ) || ( k == KeyEvent.VK_KP_LEFT ) ) {
                pictureJPanel.scrollRight();
            } else if ( ( k == KeyEvent.VK_RIGHT ) || ( k == KeyEvent.VK_KP_RIGHT ) ) {
                pictureJPanel.scrollLeft();
            } else {
                JOptionPane.showMessageDialog( myJFrame,
                        Settings.jpoResources.getString( "PictureViewerKeycodes" ),
                        Settings.jpoResources.getString( "PictureViewerKeycodesTitle" ),
                        JOptionPane.INFORMATION_MESSAGE );
            }
        }
    }


    /**
     *  method to toggle to a frameless window.
     **/
    public void requestScreenSizeMenu() {
        changeWindowPopupMenu.show( navBar.fullScreenJButton, 0, (int) ( 0 - changeWindowPopupMenu.getSize().getHeight() ) );
        pictureJPanel.requestFocusInWindow();
    }


    /**
     *  method to toggle to a frameless window.
     **/
    public void requestPopupMenu() {
        PicturePopupMenu pm = new PicturePopupMenu( mySetOfNodes, myIndex );
        pm.show( navBar.fullScreenJButton, 0, (int) ( 0 - pm.getSize().getHeight() ) );
        pictureJPanel.requestFocusInWindow();
    }


    /**
     *  Puts the picture of the indicated node onto the viewer panel
     *
     *  @param mySetOfNodes  The set of nodes from which one picture is to be shown
     *  @param myIndex  The index of the set of nodes to be shown.
     */
    public void show( NodeNavigator mySetOfNodes,
            int myIndex ) {
        logger.fine( String.format( "Navigator: %s Nodes: %d Index: %d", mySetOfNodes.toString(), mySetOfNodes.getNumberOfNodes(), myIndex ) );
        Tools.checkEDT();

        // Validate the inputs
        SortableDefaultMutableTreeNode node = mySetOfNodes.getNode( myIndex );
        if ( node == null ) {
            logger.severe( String.format( "The new node is null. Aborting. mySetOfNodes: %s, index: %d", mySetOfNodes.toString(), myIndex ) );
            closeViewer();
            return;
        }

        Object uo = node.getUserObject();
        if ( !( uo instanceof PictureInfo ) ) {
            logger.severe( String.format( "The new node is not for a PictureInfo object. Aborting. userObject class: %s, mySetOfNodes: %s, index: %d", node.getUserObject().getClass().toString(), mySetOfNodes.toString(), myIndex ) );
            closeViewer();
            return;
        }


        // remove the pictureinfo change listener if present
        if ( this.mySetOfNodes != null ) {
            ( (PictureInfo) this.mySetOfNodes.getNode( this.myIndex ).getUserObject() ).removePictureInfoChangeListener( this );
        }
        // attach the pictureinfo change listener
        PictureInfo pictureInfo = (PictureInfo) uo;
        pictureInfo.addPictureInfoChangeListener( this );


        if ( this.mySetOfNodes == null ) {
            // add viewer to the new one
            this.mySetOfNodes = mySetOfNodes;
            mySetOfNodes.addRelayoutListener( this );
        } else {
            //did we get a new navigator?
            if ( !this.mySetOfNodes.equals( mySetOfNodes ) ) {
                logger.info( String.format( "Got a new navigator: old: %s new: %s", this.mySetOfNodes.toString(), mySetOfNodes.toString() ) );
                //get rid of the old navigator
                this.mySetOfNodes.removeRelayoutListener( this );
                this.mySetOfNodes.getRid(); // help Grabage collection remove the listener
                // add viewer to the new one
                this.mySetOfNodes = mySetOfNodes;
                mySetOfNodes.addRelayoutListener( this );
            }
        }

        this.myIndex = myIndex;


        if ( myJFrame == null ) {
            createWindow();
        }

        descriptionJTextField.setText( pictureInfo.getDescription() );
        setPicture( pictureInfo );


        navBar.setIconDecorations();
        pictureJPanel.requestFocusInWindow();
    }


    /**
     *  brings up the indicated picture on the display.
     *  @param pi  The PicutreInfo object that should be displayed
     */
    private void setPicture( PictureInfo pi ) {
        logger.fine( "Set picture to PictureInfo: " + pi.toString() );
        URL pictureURL;

        String description;

        double rotation = 0;
        try {
            pictureURL = pi.getHighresURL();
            description =
                    pi.getDescription();
            rotation =
                    pi.getRotation();
        } catch ( MalformedURLException x ) {
            logger.severe( "MarformedURLException trapped on: " + pi.getHighresLocation() + "\nReason: " + x.getMessage() );
            return;

        }
        setPicture( pictureURL, description, rotation );
    }


    /**
     *  brings up the indicated picture on the display.
     *  @param filenameURL  The URL of the picture to display
     *  @param legendParam	The description of the picture
     *  @param rotation  The rotation that should be applied
     */
    private void setPicture( URL filenameURL, String legendParam,
            double rotation ) {
        pictureJPanel.legend = legendParam;
        pictureJPanel.centerWhenScaled = true;
        pictureJPanel.sclPic.setScaleSize( pictureJPanel.getSize() );

        pictureJPanel.sclPic.stopLoadingExcept( filenameURL );
        pictureJPanel.sclPic.loadAndScalePictureInThread( filenameURL, Thread.MAX_PRIORITY, rotation );
        pictureJPanel.ei = new ExifInfo( filenameURL );
        pictureJPanel.ei.decodeExifTags();
    }


    /**
     * Requests that the shown picture be rotated
     * @param angle
     */
    public void rotate( int angle ) {
        PictureInfo pi = (PictureInfo) getCurrentNode().getUserObject();
        pi.rotate( angle );
        pictureJPanel.requestFocusInWindow();

    }


    /**
     *  here we get notified by the PictureInfo object that something has
     *  changed.
     */
    public void pictureInfoChangeEvent( PictureInfoChangeEvent e ) {
        if ( e.getDescriptionChanged() ) {
            String s = e.getPictureInfo().getDescription();
            if ( s == null ) {
                logger.warning( "PictureViewer.pictureInfoChangeEvent: got called without a description in the picture. " + e.toString() );
            } else {
                descriptionJTextField.setText( s );
            }

        }
        if ( e.getHighresLocationChanged() ) {
            SortableDefaultMutableTreeNode node = mySetOfNodes.getNode( myIndex );
            if ( node == null ) {
                logger.warning( "PictureViewer.pictureInfoChangeEvent: highres chnage got called without a node. Index: " + Integer.toString( myIndex ) + mySetOfNodes.toString() );
                return;

            }

            PictureInfo pi = (PictureInfo) node.getUserObject();
            if ( pi == null ) {
                logger.warning( "PictureViewer.pictureInfoChangeEvent: highres location change got called without a PictureInfor user object. " + e.toString() );
            } else {
                setPicture( pi );
            }

        }
        if ( e.getRotationChanged() ) {
            PictureInfo pi = (PictureInfo) mySetOfNodes.getNode( myIndex ).getUserObject();
            if ( pi == null ) {
                logger.warning( "PictureViewer.pictureInfoChangeEvent: rotation change got called without a PictureInfor user object. " + e.toString() );
            } else {
                setPicture( pi );
            }

        }
        /*		if ( e.getLowresLocationChanged() ) {
        lowresLocationJTextField.setText( e.getPictureInfo().getLowresLocation () );
        }
        if ( e.getChecksumChanged() ) {
        checksumJLabel.setText( Settings.jpoResources.getString("checksumJLabel") + pi.getChecksumAsString () );
        }
        if ( e.getCreationTimeChanged() ) {
        creationTimeJTextField.setText( pi.getCreationTime () );
        parsedCreationTimeJLabel.setText( pi.getFormattedCreationTime() );
        }
        if ( e.getFilmReferenceChanged() ) {
        filmReferenceJTextField.setText( pi.getFilmReference() );
        }
        if ( e.getCommentChanged() ) {
        commentJTextField.setText( pi.getComment() );
        }
        if ( e.getPhotographerChanged() ) {
        photographerJTextField.setText( pi.getPhotographer() );
        }
        if ( e.getCopyrightHolderChanged() ) {
        copyrightHolderJTextField.setText( pi.getCopyrightHolder() );
        } */
        //closeViewer();
    }

    // Here we are not that interested in TreeModel change events other than to find out if our
    // current node was removed in which case we close the Window.

    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     *
    public void treeNodesChanged( TreeModelEvent e ) {
    navBar.setIconDecorations();
    }*/
    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     *
    public void treeNodesInserted( TreeModelEvent e ) {
    navBar.setIconDecorations();
    }*/
    /**
     *  The TreeModelListener interface tells us of tree node removal events.
     *  If we receive a removal event we need to find out if the PictureViewer is
     *  displaying node being removed. If so it must switch to the next node or
     *  close if this is not possible.
     *  TODO: This model is flawed. It's not the PictureViewer that must listen to
     *  nodes disappearing but the browsers. The Browser should update with a new set of nodes
     *  and give itself to the PictureVieer and
     * @param e The Notification element
     *
    public void treeNodesRemoved( TreeModelEvent e ) {
    logger.info( String.format( "Investigating a remove event: %s", e.toString() ) );

    // Problem here is that if the current node was removed we are no longer on the node that was removed
    TreePath currentNodeTreePath = new TreePath( currentNode.getPath() );
    logger.info( String.format( "The current node hat this path: %s", currentNodeTreePath.toString() ) );

    // step through the array of removed nodes
    Object[] children = e.getChildren();
    TreePath removedChild;
    for ( int i = 0; i < children.length; i++ ) {
    removedChild = new TreePath( children[i] );
    logger.info( String.format( "Deleted child[%d] has path: %s", i, removedChild.toString() ) );
    if ( removedChild.isDescendant( currentNodeTreePath ) ) {
    logger.info( String.format( "Type of browser is: %s", mySetOfNodes.getClass().toString() ) );
    logger.info( String.format( "The current node was removed. Let's try to go to the next picture if we can" ) );
    // because of the removal the picture we actually are pointing at the next picture
    show( mySetOfNodes, myIndex );
    //requestNextPicture();
    /*int[] childIndices = e.getChildIndices();
    SortableDefaultMutableTreeNode parentNode =
    (SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent();
    try {
    SortableDefaultMutableTreeNode nextChild =
    (SortableDefaultMutableTreeNode) parentNode.getChildAt(childIndices[i]);
    if (nextChild.getUserObject() instanceof PictureInfo) {
    show(new SingleNodeBrowser(nextChild), 0);
    } else {
    closeViewer();
    }
    } catch (ArrayIndexOutOfBoundsException x) {
    closeViewer();
    }*
    }
    }
    navBar.setIconDecorations();
    }*/
    /**
     * This method gets called when the nodes of the set have changed.
     * It moves to the new positon in the navigator if there is one.
     * If the current node has been removed it tries to go to the next node and
     * failing to find one looks for a prior node. If that is also unsuccessful
     * the window is closed.
     * @param mappingIndex The mapping index 
     *
    public void nodesChanged( int[] mappingIndex ) {
    logger.info( "Got a nodeChanged event" );
    int newIndex = mappingIndex[myIndex];
    if ( newIndex > -1 ) {
    show( mySetOfNodes, newIndex );
    } else {
    logger.info( String.format( "The current index position %d has been deleted!", myIndex ) );
    // if the next position has a node let's jump there.
    if ( ( myIndex + 1 < mappingIndex.length ) && ( mappingIndex[myIndex + 1] > -1 ) ) {
    show( mySetOfNodes, mappingIndex[myIndex + 1] );
    } else if ( ( myIndex > 0 ) && ( mappingIndex[myIndex - 1] > -1 ) ) {
    // lets go to the prior node if there is something to show there
    show( mySetOfNodes, mappingIndex[myIndex - 1] );
    } else {
    closeViewer();
    }

    }
    }*/
    /**
     * gets called when the Navigator notices a change
     */
    public void relayout() {
        logger.info( String.format( "Got notified to relayout" ) );
        show( mySetOfNodes, myIndex );

    }


    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     * @param e
     *
    public void treeStructureChanged( TreeModelEvent e ) {
    navBar.setIconDecorations();
    }*/
    /**
     *   This method is invoked by the GUI button or keyboard shortcut to
     *   advance the picture. It calls {@link SortableDefaultMutableTreeNode#getNextPicture} to find
     *   the image. If the call returned a non null node {@link #show}
     *   is called to request the loading and display of the new picture.
     *
     *  @return  true if the next picture was located, false if none available
     *
     * @see #requestPriorPicture()
     */
    public boolean requestNextPicture() {
        logger.fine( String.format( "Using the context aware step forward. The browser contains: %d pictures and we are on picture %d", mySetOfNodes.getNumberOfNodes(), myIndex ) );
        if ( mySetOfNodes.getNumberOfNodes() > myIndex + 1 ) {
            logger.fine( "PictureViewer.requestNextPicture: requesting node: " + Integer.toString( myIndex + 1 ) );
            Runnable r = new Runnable() {

                public void run() {
                    show( mySetOfNodes, myIndex + 1 );
                }
            };
            SwingUtilities.invokeLater( r );
            return true;
        } else {
            return false;
        }

    }


    /**
     *  if a request comes in to show the previous picture the data model is asked for the prior image
     *  and if one is returned it is displayed.
     *
     * @see #requestNextPicture()
     */
    public void requestPriorPicture() {
        /*if ( mySetOfNodes == null ) {
        logger.fine( "PictureViewer.requestPriorPicture: using non context aware step backward" );
        if ( getCurrentNode() != null ) {
        SortableDefaultMutableTreeNode prevNode = getCurrentNode().getPreviousPicture();
        if ( prevNode != null ) {
        show( new SingleNodeBrowser( prevNode ), 0 );
        }
        }
        } else {*/
        // use context aware step forward
        logger.fine( "PictureViewer.requestPriorPicture: using the context aware step backward" );
        if ( myIndex > 0 ) {
            Runnable r = new Runnable() {

                public void run() {
                    show( mySetOfNodes, myIndex - 1 );
                }
            };
            SwingUtilities.invokeLater( r );

        }

    }


    /**
     *  method that cancels a timer if one is running or calls the method to
     *  bring up the dialog.
     */
    @SuppressWarnings( "static-access" )
    public void requestAutoAdvance() {
        if ( advanceTimer != null ) {
            stopTimer();
            navBar.clockJButton.setIcon( PictureViewerNavBar.iconClockOff );
        } else {
            doAutoAdvanceDialog();
        }

        pictureJPanel.requestFocusInWindow();
    }


    /**
     *  method that brings up a dialog box and asks the user how he would
     *  like autoadvance to work
     */
    private void doAutoAdvanceDialog() {
        JRadioButton randomAdvanceJRadioButton = new JRadioButton( Settings.jpoResources.getString( "randomAdvanceJRadioButtonLabel" ) );
        JRadioButton sequentialAdvanceJRadioButton = new JRadioButton( Settings.jpoResources.getString( "sequentialAdvanceJRadioButtonLabel" ) );
        ButtonGroup advanceButtonGroup = new ButtonGroup();
        advanceButtonGroup.add( randomAdvanceJRadioButton );
        advanceButtonGroup.add( sequentialAdvanceJRadioButton );
        //if ( randomAdvance )
        randomAdvanceJRadioButton.setSelected( true );
        //else
        //sequentialAdvanceJRadioButton.setSelected( true );

        JRadioButton restrictToGroupJRadioButton = new JRadioButton( Settings.jpoResources.getString( "restrictToGroupJRadioButtonLabel" ) );
        JRadioButton useAllPicturesJRadioButton = new JRadioButton( Settings.jpoResources.getString( "useAllPicturesJRadioButtonLabel" ) );
        ButtonGroup cycleButtonGroup = new ButtonGroup();
        cycleButtonGroup.add( restrictToGroupJRadioButton );
        cycleButtonGroup.add( useAllPicturesJRadioButton );
        //if ( cycleAll )
        useAllPicturesJRadioButton.setSelected( true );
        //else
        //restrictToGroupJRadioButton.setSelected( true );

        JLabel timerSecondsJLabel = new JLabel( Settings.jpoResources.getString( "timerSecondsJLabelLabel" ) );
        //JTextField timerSecondsJTextField = new JTextField();
        WholeNumberField timerSecondsField = new WholeNumberField( 4, 3 );
        timerSecondsField.setPreferredSize( new Dimension( 50, 20 ) );
        timerSecondsField.setMaximumSize( new Dimension( 50, 20 ) );
        Object[] objects = { randomAdvanceJRadioButton,
            sequentialAdvanceJRadioButton,
            restrictToGroupJRadioButton,
            useAllPicturesJRadioButton,
            timerSecondsJLabel,
            timerSecondsField
        };

        int selectedValue = JOptionPane.showOptionDialog(
                myJFrame,
                objects,
                Settings.jpoResources.getString( "autoAdvanceDialogTitle" ),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null );

        if ( selectedValue == 0 ) {
            //randomAdvance = randomAdvanceJRadioButton.isSelected();
            //cycleAll = useAllPicturesJRadioButton.isSelected();

            if ( randomAdvanceJRadioButton.isSelected() ) {
                if ( useAllPicturesJRadioButton.isSelected() ) //addAllPictureNodes( pictureNodesArrayList, (SortableDefaultMutableTreeNode) currentNode.getRoot()  );
                {
                    mySetOfNodes = new RandomNavigator( Settings.pictureCollection.getRootNode().getChildPictureNodes( true ), String.format( "Randomised pictures from %s", Settings.pictureCollection.getRootNode().toString() ) );
                } else //addAllPictureNodes( pictureNodesArrayList, (SortableDefaultMutableTreeNode) currentNode.getParent() );
                {
                    mySetOfNodes = new RandomNavigator( ( (SortableDefaultMutableTreeNode) getCurrentNode().getParent() ).getChildPictureNodes( true ),
                            String.format( "Randomised pictures from %s", ( (SortableDefaultMutableTreeNode) getCurrentNode().getParent() ).toString() ) );
                }
            } else {
                if ( useAllPicturesJRadioButton.isSelected() ) {
                    mySetOfNodes = new FlatGroupNavigator( (SortableDefaultMutableTreeNode) getCurrentNode().getRoot() );
                } else {
                    mySetOfNodes = new FlatGroupNavigator( (SortableDefaultMutableTreeNode) getCurrentNode().getParent() );
                }

                myIndex = 0;
                show(
                        mySetOfNodes, myIndex );
            }

            myIndex = 0;
            show(
                    mySetOfNodes, myIndex );
            startAdvanceTimer(
                    timerSecondsField.getValue() );
        }

    }


    /**
     * This method sets up the Advance Timer
     * @param seconds 
     */
    @SuppressWarnings( "static-access" )
    public void startAdvanceTimer( int seconds ) {
        advanceTimer = new AdvanceTimer( this, seconds );
        navBar.clockJButton.setIcon( PictureViewerNavBar.iconClockOn );
    }


    /**
     *  method to stop any timer that might be running
     */
    public void stopTimer() {
        if ( advanceTimer != null ) {
            advanceTimer.stopThread();
        }

        advanceTimer = null;
        //pictureNodesArrayList = null;
    }


    /**
     * method that tells the AdvanceTimer whether it is OK to advance the picture or not
     * This important to avoid the submission of new picture requests before the old
     * ones have been met.
     */
    public boolean readyToAdvance() {
        int status = pictureJPanel.getScalablePicture().getStatusCode();
        //logger.info( String.format( "Retrieved status %d; ready would be %d", status, ScalablePicture.READY ));
        if ( ( status == ScalablePicture.READY ) || ( status == ScalablePicture.ERROR ) ) {
            return true;
        } else {
            return false;
        }

    }


    /**
     *   this method is invoked from the timer thread that notifies
     *   our object that it is time to advance to the next picture.
     */
    public void requestAdvance() {
        //logger.info( "Advance requested");
        requestNextPicture();
    }


    /**
     *   this method gets invoked from the PicturePane object
     *   to notifying of status changes. It updates the description
     *   panel at the bottom of the screen with the status. If the
     *   status was a notification of the image starting to load
     *   the progress bar is made le. Any other status hides
     *   the progress bar.
     *
     * @param pictureStatusCode
     * @param pictureStatusMessage
     */
    public void scalableStatusChange( final int pictureStatusCode,
            final String pictureStatusMessage ) {
        Runnable r = new Runnable() {

            public void run() {
                switch ( pictureStatusCode ) {
                    case ScalablePicture.UNINITIALISED:
                        loadJProgressBar.setVisible( false );
                        break;

                    case ScalablePicture.GARBAGE_COLLECTION:
                        loadJProgressBar.setVisible( false );
                        break;

                    case ScalablePicture.LOADING:
                        if ( myJFrame != null ) {
                            loadJProgressBar.setVisible( true );
                        }

                        break;
                    case ScalablePicture.LOADED:
                        loadJProgressBar.setVisible( false );
                        //descriptionJTextField.setText( getDescription() );
                        break;

                    case ScalablePicture.SCALING:
                        loadJProgressBar.setVisible( false );
                        break;

                    case ScalablePicture.READY:
                        loadJProgressBar.setVisible( false );
                        if ( myJFrame != null ) {
                            myJFrame.toFront();
                        }
//descriptionJTextField.setText( getDescription() );

                        break;
                    case ScalablePicture.ERROR:
                        loadJProgressBar.setVisible( false );
                        ;
                        break;

                    default:

                        logger.warning( "PictureViewer.scalableStatusChange: get called with a code that is not understood: " + Integer.toString( pictureStatusCode ) + " " + pictureStatusMessage );
                        break;

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
     * This method sends the text of the textbox to the pictureinfo.
     * This updates the description if it has changed.
     */
    private void updateDescription() {
        Object uo = getCurrentNode().getUserObject();
        if ( uo != null ) {
            if ( uo instanceof PictureInfo ) {
                logger.fine( "Sending description update to " + descriptionJTextField.getText() );
                ( (PictureInfo) uo ).setDescription(
                        descriptionJTextField.getText() );
            }

        }
    }


    /**
     *  method that gets invoked from the PicturePane object to notify of status changes
     *
     * @param statusCode
     * @param percentage
     */
    public void sourceLoadProgressNotification( final int statusCode,
            final int percentage ) {
        Runnable r = new Runnable() {

            public void run() {
                switch ( statusCode ) {
                    case SourcePicture.LOADING_STARTED:
                        loadJProgressBar.setValue( 0 );
                        loadJProgressBar.setVisible( true );
                        break;

                    case SourcePicture.LOADING_PROGRESS:
                        loadJProgressBar.setValue( percentage );
                        loadJProgressBar.setVisible( true );
                        break;

                    case SourcePicture.LOADING_COMPLETED:
                        loadJProgressBar.setVisible( false );
                        loadJProgressBar.setValue( 0 ); // prepare for the next load
                        break;

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
     *  method to scale the picture to the current screen size and to center it there.
     */
    public void resetPicture() {
        pictureJPanel.zoomToFit();
        pictureJPanel.centerImage();
        pictureJPanel.requestFocusInWindow();
    }


    /**
     *  This function cycles to the next info display. The first is DISPLAY_NONE, DISPLAY_PHOTOGRAPHIC
     *  and DISPLAY_APPLICATION
     **/
    public void cylceInfoDisplay() {
        pictureJPanel.cylceInfoDisplay();
        pictureJPanel.requestFocusInWindow();
    }

    /**
     *  This class deals with the mouse events. Is built so that the picture can be dragged if
     *  the mouse button is pressed and the mouse moved. If the left button is clicked the picture is
     *  zoomed in, middle resets to full screen, right zooms out.
     */
    class Listener
            extends MouseInputAdapter {

        /**
         *  used in dragging to find out how much the mouse has moved from the last time
         */
        private int last_x, last_y;


        /**
         *   This method traps the mouse events and changes the scale and position of the displayed
         *   picture.
         */
        @Override
        public void mouseClicked( MouseEvent e ) {
            logger.fine( "PicturePane.mouseClicked" );
            if ( e.getButton() == 3 ) {
                // Right Mousebutton zooms out
                pictureJPanel.centerWhenScaled = false;
                pictureJPanel.zoomOut();
            } else if ( e.getButton() == 2 ) {
                // Middle Mousebutton resets
                pictureJPanel.zoomToFit();
                pictureJPanel.centerWhenScaled = true;
            } else if ( e.getButton() == 1 ) {
                // Left Mousebutton zooms in on selected spot
                // Convert screen coordinates of the mouse click into true
                // coordinates on the picture:

                int WindowWidth = pictureJPanel.getSize().width;
                int WindowHeight = pictureJPanel.getSize().height;

                int X_Offset = e.getX() - ( WindowWidth / 2 );
                int Y_Offset = e.getY() - ( WindowHeight / 2 );

                pictureJPanel.setCenterLocation(
                        pictureJPanel.focusPoint.x + (int) ( X_Offset / pictureJPanel.sclPic.getScaleFactor() ),
                        pictureJPanel.focusPoint.y + (int) ( Y_Offset / pictureJPanel.sclPic.getScaleFactor() ) );
                pictureJPanel.centerWhenScaled = false;
                pictureJPanel.zoomIn();
            }
        }


        /**
         * method that is invoked when the
         * user drags the mouse with a button pressed. Moves the picture around
         */
        @Override
        public void mouseDragged( MouseEvent e ) {
            if ( !Dragging ) {
                // Switch into dragging mode and record current coordinates
                logger.fine( "PicturePane.mouseDragged: Switching to drag mode." );
                last_x = e.getX();
                last_y = e.getY();

                setDragging( true );

            } else {
                // was already dragging
                int x = e.getX(), y = e.getY();

                pictureJPanel.focusPoint.setLocation( (int) ( (double) pictureJPanel.focusPoint.x + ( ( last_x - x ) / pictureJPanel.sclPic.getScaleFactor() ) ),
                        (int) ( (double) pictureJPanel.focusPoint.y + ( ( last_y - y ) / pictureJPanel.sclPic.getScaleFactor() ) ) );
                last_x = x;
                last_y = y;

                setDragging( true );
                pictureJPanel.repaint();
            }
            pictureJPanel.centerWhenScaled = false;
        }


        /**
         *
         * @param parameter
         */
        public void setDragging( boolean parameter ) {
            Dragging = parameter;
            if ( Dragging == false ) {
                //pictureJPanel.setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
                pictureJPanel.setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
            } else {
                pictureJPanel.setCursor( new Cursor( Cursor.MOVE_CURSOR ) );
            }
        }

        /**
         *  Flag that lets the object know if the mouse is in dragging mode.
         */
        private boolean Dragging = false;


        /**
         * method that is invoked when the
         * user releases the mouse button.
         */
        @Override
        public void mouseReleased( MouseEvent e ) {
            logger.fine( "PicturePane.mouseReleased." );
            if ( Dragging ) {
                //Dragging has ended
                Dragging = false;
                pictureJPanel.setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
            }
        }
    }  //end class Listener
}



