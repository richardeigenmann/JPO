package jpo;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/*
PictureViewer.java:  class that displays a window in which a picture is shown.
 
Copyright (C) 2002-2007  Richard Eigenmann, Zürich, Switzerland
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
 *   PictureViewer is a Controller that manages a window which displays a picture. It provides navigation control
 *   the collection as well as mouse and keyboard control over the zooming
 *
 *   <img src="../PictureViewer.png" border=0>
 **/
public class PictureViewer
        implements ScalablePictureListener,
        AdvanceTimerInterface,
        ChangeWindowInterface,
        PictureInfoChangeListener,
        TreeModelListener {
    
    
    /**
     *  indicator that specifies what sort of window should be created
     */
    public int windowMode = ResizableJFrame.WINDOW_DEFAULT;
    
    
    
    /**
     *  Brings up a window in which a picture node is displayed. This class
     *  handles all the user interaction such as zoom in / out, drag, navigation,
     *  information display and keyboard keys.
     **/
    public PictureViewer() {
        initGui();
        Settings.pictureCollection.getTreeModel().addTreeModelListener( this );
        pictureJPanel.addStatusListener( this );
    }
    
    
    /**
     *   method to close the PictureViewer and all dangling references.
     */
    public void closeViewer() {
        Settings.pictureCollection.getTreeModel().removeTreeModelListener( this );
        stopTimer();
        closeMyWindow();
    }
    
    
    
    // GUI Widgets
    /**
     *  The Window in which the viewer will place it's components.
     **/
    public ResizableJFrame myJFrame;
    
    /**
     *  The root JPanel
     */
    private JPanel viewerPanel = new JPanel();
    
    /**
     *   The pane that handles the image drawing aspects.
     **/
    public PicturePane pictureJPanel = new PicturePane();
    
    /**
     *   progress bar to track the pictures loaded so far
     */
    private JProgressBar loadJProgressBar = new JProgressBar();
    
    /**
     *   Description Panel
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
        pictureJPanel.setFocusable( false );
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
        descriptionJTextField.setFont( Font.decode( Settings.jpoResources.getString("PictureViewerDescriptionFont") ) );
        descriptionJTextField.setWrapStyleWord( true );
        descriptionJTextField.setLineWrap( true );
        descriptionJTextField.setEditable( true );
        descriptionJTextField.setForeground( Settings.PICTUREVIEWER_TEXT_COLOR );
        descriptionJTextField.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        descriptionJTextField.setOpaque( true );
        descriptionJTextField.setBorder( new EmptyBorder(2,12,0,0) );
        descriptionJTextField.setMinimumSize( new Dimension( 80, 26 ) );
        
        JScrollPane descriptionJScrollPane = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
        descriptionJScrollPane.setViewportView( descriptionJTextField );
        descriptionJScrollPane.setBorder(new EmptyBorder(0,0,0,0));
        descriptionJScrollPane.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        descriptionJScrollPane.setOpaque(true);
        c.weightx = 1;
        c.weighty = 0.01;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 1;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        viewerPanel.add( descriptionJScrollPane, c ) ;
        
        
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 1;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        navBar = new PictureViewerNavBar( this );
        viewerPanel.add( navBar ,c );
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
        myJFrame = new ResizableJFrame( Settings.jpoResources.getString("PictureViewerTitle"), decorateWindow, initialDimension );
        myJFrame.setBackground( Settings.PICTUREVIEWER_BACKGROUND_COLOR );
        
        
        if ( Settings.maximisePictureViewerWindow ) windowMode = ResizableJFrame.WINDOW_FULLSCREEN;
        myJFrame.resizeTo( windowMode );
        myJFrame.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                closeViewer();
            }
        });
        // set up the glass pane over the window so that
        // it intercepts the keystrokes. The Glass pane needs
        // to be kept in focus.
        myJFrame.getGlassPane().addKeyListener( myViewerKeyAdapter );
        
        // set layout manager and add the PictureViewer Panel
        myJFrame.getContentPane().setLayout(new BorderLayout() );
        myJFrame.getContentPane().add( "Center", viewerPanel );
        
        
        //myJFrame.getGlassPane().setVisible( true );
        myJFrame.getGlassPane().setFocusable( true );
        myJFrame.getGlassPane().requestFocusInWindow();
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
     *  flag that specifies whether the window should be drawn with decoration or not
     */
    private boolean decorateWindow = true;
    
    /**
     *  request that the window showing the picture be changed be changed.
     *  @param  newMode  {@link ResizableJFrame#WINDOW_FULLSCREEN}, {@link ResizableJFrame#WINDOW_LEFT},
     *		{@link ResizableJFrame#WINDOW_RIGHT},  {@link ResizableJFrame#WINDOW_TOP_LEFT},
     *		{@link ResizableJFrame#WINDOW_TOP_RIGHT}, {@link ResizableJFrame#WINDOW_BOTTOM_LEFT},
     *		{@link ResizableJFrame#WINDOW_BOTTOM_RIGHT} or {@link ResizableJFrame#WINDOW_DEFAULT}
     *		need to be indicated.
     *
     */
    public void switchWindowMode( int newMode ) {
        Tools.log("PictureViewer.switchWindowMode: old mode: " + Integer.toString( windowMode ) + " new: " + Integer.toString( newMode ));
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
     *  This method turns on or truns off the frame around the window. It works by closing
     *  the window and creatig a new one with the correct decorations. It uses the decorateWindow
     *  flag to determine if the decorations are being shown.
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
     *  a reference to the currently displayed node of the model to allows navigation forwards and backwards
     *  TODO: Why is this public? Should this not be replaced by the next 2 objects?
     */
    public SortableDefaultMutableTreeNode currentNode = null;
    
    /**
     *  the context of the browsing
     */
    private ThumbnailBrowserInterface mySetOfNodes = null;
    
    /**
     *  the position in the context being shown
     */
    private int myIndex = 0;
    
    
    
    
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
    
    
    private class ViewerKeyAdapter extends KeyAdapter {
        /**
         *  method that analysed the key that was pressed
         */
        public void keyPressed(KeyEvent e) {
            int k = e.getKeyCode();
            if ((k == KeyEvent.VK_I))
                pictureJPanel.cylceInfoDisplay();
            else if ((k == KeyEvent.VK_N))
                requestNextPicture();
            else if ((k == KeyEvent.VK_M))
                requestPopupMenu();
            else if ((k == KeyEvent.VK_P))
                requestPriorPicture();
            else if ((k == KeyEvent.VK_F))
                requestScreenSizeMenu();
            else if ((k == KeyEvent.VK_SPACE) || (k == KeyEvent.VK_HOME))
                resetPicture();
            else if ((k == KeyEvent.VK_PAGE_UP))
                pictureJPanel.zoomIn();
            else if ((k == KeyEvent.VK_PAGE_DOWN))
                pictureJPanel.zoomOut();
            else if ((k == KeyEvent.VK_1))
                pictureJPanel.zoomFull();
            else if ((k == KeyEvent.VK_UP) || (k == KeyEvent.VK_KP_UP))
                pictureJPanel.scrollDown();
            else if ((k == KeyEvent.VK_DOWN) || (k == KeyEvent.VK_KP_DOWN))
                pictureJPanel.scrollUp();
            else if ((k == KeyEvent.VK_LEFT) || (k == KeyEvent.VK_KP_LEFT))
                pictureJPanel.scrollRight();
            else if ((k == KeyEvent.VK_RIGHT) || (k == KeyEvent.VK_KP_RIGHT))
                pictureJPanel.scrollLeft();
            else
                JOptionPane.showMessageDialog( myJFrame,
                        Settings.jpoResources.getString("PictureViewerKeycodes"),
                        Settings.jpoResources.getString("PictureViewerKeycodesTitle"),
                        JOptionPane.INFORMATION_MESSAGE);
            myJFrame.getGlassPane().requestFocusInWindow();
        }
    }
    
    
    
    /**
     *  method to toggle to a frameless window.
     **/
    public void requestScreenSizeMenu() {
        changeWindowPopupMenu.show( navBar.fullScreenJButton, 0, (int) (0 - changeWindowPopupMenu.getSize().getHeight()) );
        myJFrame.getGlassPane().requestFocusInWindow();
    }
    
    
    /**
     *  method to toggle to a frameless window.
     **/
    public void requestPopupMenu() {
        PicturePopupMenu pm = new PicturePopupMenu( mySetOfNodes, myIndex, this );
        pm.show( navBar.fullScreenJButton, 0, (int) (0 - pm.getSize().getHeight()) );
        myJFrame.getGlassPane().requestFocus();
    }
    
    
    /**
     *  call this method to request a picture to be shown.
     *
     *  @param mySetOfNodes  The set of nodes which holds the links to the images
     *  @param myIndex  The index of the pictures to be shown.
     */
    public void changePicture( ThumbnailBrowserInterface mySetOfNodes, int myIndex ) {
        //Tools.log("PictureViewer.changePicture: called the good new way.");
        this.mySetOfNodes = mySetOfNodes;
        this.myIndex = myIndex;
        SortableDefaultMutableTreeNode node = mySetOfNodes.getNode( myIndex );
        if ( node == null ) {
            Tools.log("PictureViewer.changePicture: null node recieved. Not valid, aborting.");
            return;
        }
        
        if ( ! ( node.getUserObject() instanceof PictureInfo) ) {
            Tools.log( "PictureViewer:changePicture: ignoring request because new node is not a PictureInfo");
            return;
        }
        
        if ( currentNode == node) {
            Tools.log( "PictureViewer.changePicture: ignoring request because new node is the same as the current one");
            return;
        }
        
        if (myJFrame == null) {
            createWindow();
        }
        
        // unattach the change listener  and figure out if the description was changed and update
        // it if edits to the collection are allowed.
        if ( ( this.currentNode != null )
        && ( this.currentNode.getUserObject() instanceof  PictureInfo ) ) {
            PictureInfo pi = (PictureInfo) currentNode.getUserObject();
            pi.removePictureInfoChangeListener( this );
            
            if ( ( ! getDescription().equals( descriptionJTextField.getText() ) )
            && ( currentNode.getPictureCollection().getAllowEdits() ) ) {
                Tools.log("PictureViewer.changePicture: The description was modified and is being saved");
                pi.setDescription( descriptionJTextField.getText() );
            }
        }
        
        
        
        currentNode = node;
        descriptionJTextField.setText( getDescription() );
        pictureJPanel.setPicture( (PictureInfo) node.getUserObject() );
        
        
        // attach the change listener
        PictureInfo pi = (PictureInfo) currentNode.getUserObject();
        pi.addPictureInfoChangeListener( this );
        
        
        navBar.setIconDecorations();
        
        // request cacheing of next pictures
        SortableDefaultMutableTreeNode cacheNextNode = node.getNextPicture();
        if ( ( cacheNextNode != null ) && Settings.maxCache > 2 ) {
            new PictureCacheLoader( cacheNextNode );
            
            SortableDefaultMutableTreeNode cacheAfterNextNode = cacheNextNode.getNextPicture();
            if ( ( cacheAfterNextNode != null ) && Settings.maxCache > 3 ) {
                new PictureCacheLoader( cacheAfterNextNode );
            }
        }
    }
    
    
    
    /**
     *   Changes the displayed picture to that of the supplied node. It calls setIconDecorations
     *   to set the previous and next button icon accordingly. It also checks if the user changed
     *   the description and saves it.
     *
     *   @param	node	The node whose userObject of type PictureInfo
     *                      the PictureViewer is supposed to display.
     *   @deprecated use the changePicture above.
     **/
    
    public void changePicture( SortableDefaultMutableTreeNode node ) {
        //Tools.log("PictureViewer.changePicture: called the old way.");
        if ( node == null ) {
            Tools.log("PictureViewer.changePicture: null node recieved. Not valid, aborting.");
            return;
        }
        
        if ( ! ( node.getUserObject() instanceof PictureInfo) ) {
            Tools.log( "PictureViewer:changePicture: ignoring request because new node is not a PictureInfo");
            return;
        }
        
        if ( currentNode == node) {
            Tools.log( "PictureViewer.changePicture: ignoring request because new node is the same as the current one");
            return;
        }
        
        if (myJFrame == null) {
            createWindow();
        }
        
        // unattach the change listener  and figure out if the description was changed and update
        // it if edits to the collection are allowed.
        if ( ( this.currentNode != null )
        && ( this.currentNode.getUserObject() instanceof  PictureInfo ) ) {
            PictureInfo pi = (PictureInfo) currentNode.getUserObject();
            pi.removePictureInfoChangeListener( this );
            
            if ( ( ! getDescription().equals( descriptionJTextField.getText() ) )
            && ( currentNode.getPictureCollection().getAllowEdits() ) ) {
                Tools.log("PictureViewer.changePicture: The description was modified and is being saved");
                pi.setDescription( descriptionJTextField.getText() );
            }
        }
        
        
        
        currentNode = node;
        descriptionJTextField.setText( getDescription() );
        pictureJPanel.setPicture( (PictureInfo) node.getUserObject() );
        
        
        // attach the change listener
        PictureInfo pi = (PictureInfo) currentNode.getUserObject();
        pi.addPictureInfoChangeListener( this );
        
        
        navBar.setIconDecorations();
        
        // request cacheing of next pictures
        SortableDefaultMutableTreeNode cacheNextNode = node.getNextPicture();
        if ( ( cacheNextNode != null ) && Settings.maxCache > 2 ) {
            new PictureCacheLoader( cacheNextNode );
            
            SortableDefaultMutableTreeNode cacheAfterNextNode = cacheNextNode.getNextPicture();
            if ( ( cacheAfterNextNode != null ) && Settings.maxCache > 3 ) {
                new PictureCacheLoader( cacheAfterNextNode );
            }
        }
        
    }
    
    
    /**
     *  here we get notified by the PictureInfo object that something has
     *  changed.
     */
    public void pictureInfoChangeEvent( PictureInfoChangeEvent e ) {
        if ( e.getDescriptionChanged() ) {
            descriptionJTextField.setText( e.getPictureInfo().getDescription() );
        }
        if ( e.getHighresLocationChanged() ) {
            pictureJPanel.setPicture( (PictureInfo) currentNode.getUserObject() );
        }
        if ( e.getRotationChanged() ) {
            pictureJPanel.setPicture( (PictureInfo) currentNode.getUserObject() );
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
     */
    public void treeNodesChanged( TreeModelEvent e ) {
        navBar.setIconDecorations();
    }
    
    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     */
    public void treeNodesInserted( TreeModelEvent e ) {
        navBar.setIconDecorations();
    }
    
    /**
     *  The TreeModelListener interface tells us of tree node removal events.
     *  If we receive a removal event we need to find out if the PictureViewer is
     *  displaying an image and if it is whether this is the node being removed or
     *  a descendant of it. If so it must switch to the next node. If the next node
     *  is a group then the viewer is closed.
     *  If the picture browser is not in the foreground it is closed.
     */
    public void treeNodesRemoved( TreeModelEvent e ) {
        //Tools.log("PictureViewer.treeNodesRemoved was invoked");
        if ( currentNode == null ) return;
        TreePath removedChild;
        TreePath currentNodeTreePath = new TreePath( currentNode.getPath() );
        Object [] children = e.getChildren();
        for ( int i = 0; i < children.length; i++ ) {
            removedChild = new TreePath( children[ i ] );
            if ( removedChild.isDescendant( currentNodeTreePath ) ) {
                                /*Tools.log("PictureViewer.treeNodesRemoved: "
                                        + currentNodeTreePath.toString()
                                        + " is a descendant of "
                                        +  removedChild.toString() ); */
                int[] childIndices = e.getChildIndices();
                SortableDefaultMutableTreeNode parentNode =
                        (SortableDefaultMutableTreeNode) e.getTreePath().getLastPathComponent();
                try {
                    SortableDefaultMutableTreeNode nextChild =
                            (SortableDefaultMutableTreeNode) parentNode.getChildAt( childIndices[i] );
                    if ( nextChild.getUserObject() instanceof PictureInfo )
                        changePicture( nextChild );
                    else
                        closeViewer();
                } catch ( ArrayIndexOutOfBoundsException x ) {
                    closeViewer();
                }
            }
        }
        
        navBar.setIconDecorations();
    }
    
    /**
     *   implemented here to satisfy the TreeModelListener interface; not used.
     */
    public void treeStructureChanged( TreeModelEvent e ) {
        navBar.setIconDecorations();
    }
    
    
    
    
    
    /**
     *   This method is invoked by the GUI button or keyboard shortcut to
     *   advance the picture. It calls {@link SortableDefaultMutableTreeNode#getNextPicture} to find
     *   the image. If the call returned a non null node {@link #changePicture}
     *   is called to request the loading and display of the new picture.
     *
     *  @return  true if the next picture was located, false if none available
     *
     * @see #requestPriorPicture()
     */
    public boolean requestNextPicture() {
        if ( mySetOfNodes == null ) {
            //Tools.log("PictureViewer.requestNextPicture: using non context aware step forward");
            SortableDefaultMutableTreeNode nextNode = currentNode.getNextPicture();
            if ( nextNode != null )  {
                changePicture( nextNode );
                return true;
            } else {
                return false;
            }
        } else {
            // use context aware step forward
            //Tools.log("PictureViewer.requestNextPicture: using the context aware step forward. The browser contains: " + Integer.toString( mySetOfNodes.getNumberOfNodes() ) + " pictures");
            if ( mySetOfNodes.getNumberOfNodes() > myIndex ) {
                //Tools.log("PictureViewer.requestNextPicture: requesting node: " + Integer.toString( myIndex + 1) );
                changePicture( mySetOfNodes, myIndex + 1 );
                return true;
            } else {
                return false;
            }
        }
    }
    
    
    
    
    
    
    /**
     *  method that cancels a timer if one is running or calls the method to
     *  bring up the dialog.
     */
    public void requestAutoAdvance() {
        if ( advanceTimer != null ) {
            stopTimer();
            navBar.clockJButton.setIcon( navBar.iconClockOff );
        } else {
            doAutoAdvanceDialog();
        }
    }
    
    
    
    
    /**
     * method that tells the AdvanceTimer whether it is ok to advance the picture or not
     * This important to avoice the submission of new picture requests before the old
     * ones have been met.
     */
    public boolean readyToAdvance() {
        int status = pictureJPanel.getScalablePicture().getStatusCode();
        if ( ( status == ScalablePicture.READY )
        || ( status == ScalablePicture.ERROR ) )
            return true;
        else
            return false;
    }
    
    
    /**
     *  method that brings up a dialog box and asks the user how he would
     *  like autoadvance to work
     */
    private void doAutoAdvanceDialog() {
        JRadioButton randomAdvanceJRadioButton = new JRadioButton( Settings.jpoResources.getString("randomAdvanceJRadioButtonLabel") );
        JRadioButton sequentialAdvanceJRadioButton = new JRadioButton( Settings.jpoResources.getString("sequentialAdvanceJRadioButtonLabel") );
        ButtonGroup advanceButtonGroup = new ButtonGroup();
        advanceButtonGroup.add( randomAdvanceJRadioButton );
        advanceButtonGroup.add( sequentialAdvanceJRadioButton );
        //if ( randomAdvance )
        randomAdvanceJRadioButton.setSelected( true );
        //else
        //sequentialAdvanceJRadioButton.setSelected( true );
        
        JRadioButton restrictToGroupJRadioButton = new JRadioButton( Settings.jpoResources.getString("restrictToGroupJRadioButtonLabel") );
        JRadioButton useAllPicturesJRadioButton = new JRadioButton( Settings.jpoResources.getString("useAllPicturesJRadioButtonLabel") );
        ButtonGroup cycleButtonGroup = new ButtonGroup();
        cycleButtonGroup.add( restrictToGroupJRadioButton );
        cycleButtonGroup.add( useAllPicturesJRadioButton );
        //if ( cycleAll )
        useAllPicturesJRadioButton.setSelected( true );
        //else
        //restrictToGroupJRadioButton.setSelected( true );
        
        JLabel timerSecondsJLabel = new JLabel( Settings.jpoResources.getString("timerSecondsJLabelLabel") );
        //JTextField timerSecondsJTextField = new JTextField();
        WholeNumberField timerSecondsField = new WholeNumberField( 4, 3 );
        timerSecondsField.setPreferredSize( new Dimension( 50, 20 ) );
        timerSecondsField.setMaximumSize( new Dimension( 50, 20 ) );
        Object [] objects = { randomAdvanceJRadioButton,
        sequentialAdvanceJRadioButton,
        restrictToGroupJRadioButton,
        useAllPicturesJRadioButton,
        timerSecondsJLabel,
        timerSecondsField };
        
        int selectedValue = JOptionPane.showOptionDialog(
                myJFrame,
                objects,
                Settings.jpoResources.getString("autoAdvanceDialogTitle"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null);
        
        if ( selectedValue == 0 ) {
            //randomAdvance = randomAdvanceJRadioButton.isSelected();
            //cycleAll = useAllPicturesJRadioButton.isSelected();
            
            if ( randomAdvanceJRadioButton.isSelected() ) {
                if ( useAllPicturesJRadioButton.isSelected() )
                    //enumerateAndAddToList( pictureNodesArrayList, (SortableDefaultMutableTreeNode) currentNode.getRoot()  );
                    mySetOfNodes = new RandomBrowser( (SortableDefaultMutableTreeNode) currentNode.getRoot() );
                else
                    //enumerateAndAddToList( pictureNodesArrayList, (SortableDefaultMutableTreeNode) currentNode.getParent() );
                    mySetOfNodes = new RandomBrowser( (SortableDefaultMutableTreeNode) currentNode.getParent() );
            } else {
                if ( useAllPicturesJRadioButton.isSelected() )
                    mySetOfNodes = new SequentialBrowser( (SortableDefaultMutableTreeNode) currentNode.getRoot() );
                else
                    mySetOfNodes = new SequentialBrowser( (SortableDefaultMutableTreeNode) currentNode.getParent() );
                myIndex = 0;
                changePicture( mySetOfNodes, myIndex );
            }
            myIndex = 0;
            changePicture( mySetOfNodes, myIndex );
            startAdvanceTimer( timerSecondsField.getValue() );
        }
    }
    
    
    /**
     * This method sets up the Advance Timer
     */
    public void startAdvanceTimer( int seconds ) {
        advanceTimer = new AdvanceTimer( this, seconds );
        navBar.clockJButton.setIcon( navBar.iconClockOn );
    }
    
    
    
    
    
    /**
     *   this method is invoked from the timer thread that notifies
     *   our oject that it is time to advance to the next picture.
     */
    public void requestAdvance() {
        requestNextPicture();
                /*myIndex++;
                if ( myIndex >= mySetOfNodes.getNumberOfNodes() ) {
                        myIndex = 0;
                }
                changePicture( mySetOfNodes, myIndex );*/
    }
    
    
    /**
     *  method to stop any timer that might be running
     */
    public void stopTimer() {
        if ( advanceTimer != null )
            advanceTimer.stopThread();
        advanceTimer = null;
        //pictureNodesArrayList = null;
    }
    
    
    
    
    
    
    
    /**
     *  if a request comes in to show the previous picture the data model is asked for the prior image
     *  and if one is returned it is displayed.
     *
     * @see #requestNextPicture()
     */
    public void requestPriorPicture() {
        if ( mySetOfNodes == null ) {
            Tools.log("PictureViewer.requestPriorPicture: using non context aware step backward");
            if (currentNode != null) {
                SortableDefaultMutableTreeNode prevNode = currentNode.getPreviousPicture();
                if ( prevNode != null ) {
                    changePicture( prevNode );
                }
            }
        } else {
            // use context aware step forward
            Tools.log("PictureViewer.requestPriorPicture: using the context aware step backward");
            if ( myIndex > 0 ) {
                changePicture( mySetOfNodes, myIndex - 1 );
            }
        }
    }
    
    
    
    
    
    
    /**
     *   this method gets invoked from the PicturePane object
     *   to notifying of status changes. It updates the description
     *   panel at the bottom of the screen with the status. If the
     *   status was a notification of the image starting to load
     *   the progress bar is made le. Any other status hides
     *   the progress bar.
     **/
    public void scalableStatusChange(int pictureStatusCode, String pictureStatusMessage) {
        Tools.log("PictureViewer.scalableStatusChange: Got a status change: " + pictureStatusMessage);
        //loadJProgressBar.setString( pictureStatusMessage );
        switch( pictureStatusCode ) {
            case ScalablePicture.UNINITIALISED:
                loadJProgressBar.setVisible( false );
                break;
            case ScalablePicture.GARBAGE_COLLECTION:
                loadJProgressBar.setVisible( false );
                break;
            case ScalablePicture.LOADING:
                if ( myJFrame != null )
                    loadJProgressBar.setVisible( true );
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
                if ( myJFrame != null )
                    myJFrame.toFront();
                //descriptionJTextField.setText( getDescription() );
                break;
            case ScalablePicture.ERROR:
                loadJProgressBar.setVisible( false );;
                break;
            default:
                Tools.log( "PictureViewer.scalableStatusChange: get called with a code that is not understood: " + Integer.toString( pictureStatusCode ) + " " + pictureStatusMessage );
                break;
        }
    }
    
    
    /**
     *  This helper method returns the description of the node and if that is not available for
     *  some reason it returns a blank string.
     */
    private String getDescription() {
        Object uo = currentNode.getUserObject();
        if ( uo != null ) {
            if ( uo instanceof PictureInfo ) {
                return ((PictureInfo) uo).getDescription() ;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
    
    
    
    /**
     *  method that gets invoked from the PicturePane object to notify of status changes
     **/
    public void sourceLoadProgressNotification( int statusCode, int percentage ) {
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
    
    /**
     *  method to scale the picture to the current screen size and to center it there.
     */
    public void resetPicture() {
        pictureJPanel.zoomToFit();
        pictureJPanel.centerImage();
    }
    
    
}



