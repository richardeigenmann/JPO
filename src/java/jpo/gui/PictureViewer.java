package jpo.gui;

import jpo.dataModel.FlatGroupNavigator;
import jpo.gui.swing.PicturePane;
import jpo.dataModel.Settings;
import jpo.dataModel.PictureInfo;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import jpo.dataModel.ExifInfo;
import jpo.dataModel.NodeNavigator;
import jpo.dataModel.PictureInfoChangeEvent;
import jpo.dataModel.PictureInfoChangeListener;
import jpo.dataModel.RandomNavigator;
import jpo.dataModel.NodeNavigatorListener;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.Tools;
import jpo.gui.swing.PictureFrame;


/*
PictureViewer.java:  Controller and Viewer class that browses a set of pictures.

Copyright (C) 2002 - 2011  Richard Eigenmann, Zürich, Switzerland
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
        implements PictureViewerActions, ScalablePictureListener,
        AdvanceTimerInterface,
        PictureInfoChangeListener,
        NodeNavigatorListener {

    public PictureFrame pictureFrame = new PictureFrame( this );
    /**
     *   The pane that handles the image drawing aspects.
     **/
    private PicturePane pictureJPanel = pictureFrame.getPictureJPanel();

    /**
     *  Brings up a window in which a picture node is displayed. This class
     *  handles all the user interaction such as zoom in / out, drag, navigation,
     *  information display and keyboard keys.
     **/
    public PictureViewer() {
        pictureJPanel.addStatusListener( this );

        // register an interest in mouse events
        Listener MouseListener = new Listener();
        pictureJPanel.addMouseListener( MouseListener );
        pictureJPanel.addMouseMotionListener( MouseListener );

        pictureFrame.myJFrame.addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent we ) {
                closeViewer();
            }
        } );


    }
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( PictureViewer.class.getName() );

    /**
     * Closes the PictureViewer and all dangling references.
     */
    @Override
    public void closeViewer() {
        //getRid of the old navigator
        mySetOfNodes.removeNodeNavigatorListener( this );
        mySetOfNodes.getRid(); // help Grabage collection remove the listener
        stopTimer();
        pictureFrame.myJFrame.dispose();
    }
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
     * TODO: Shouldn't this be more context aware?
     */
    @Override
    public SortableDefaultMutableTreeNode getCurrentNode() {
        try {
            return mySetOfNodes.getNode( myIndex );
        } catch ( NullPointerException npe ) {
            LOGGER.warning( String.format( "Got a npe on node %d. Message: %s", myIndex, npe.getMessage() ) );
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
    private ChangeWindowPopupMenu changeWindowPopupMenu = new ChangeWindowPopupMenu( pictureFrame.myJFrame );

    /**
     * Shows a resize popup menu
     **/
    @Override
    public void requestScreenSizeMenu() {
        changeWindowPopupMenu.show( pictureFrame.getPictureViewerNavBar(), 96, (int) ( 0 - changeWindowPopupMenu.getSize().getHeight() ) );
        pictureJPanel.requestFocusInWindow();
    }

    /**
     *  Requests that the popup menu be shown
     **/
    @Override
    public void requestPopupMenu() {
        PicturePopupMenu picturePopupMenu = new PicturePopupMenu( mySetOfNodes, myIndex );
        picturePopupMenu.show( pictureFrame.getPictureViewerNavBar(), 120, (int) ( 0 - picturePopupMenu.getSize().getHeight() ) );
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
        LOGGER.fine( String.format( "Navigator: %s Nodes: %d Index: %d", mySetOfNodes.toString(), mySetOfNodes.getNumberOfNodes(), myIndex ) );
        Tools.checkEDT();

        // Validate the inputs
        SortableDefaultMutableTreeNode node = mySetOfNodes.getNode( myIndex );
        if ( node == null ) {
            LOGGER.severe( String.format( "The new node is null. Aborting. mySetOfNodes: %s, index: %d", mySetOfNodes.toString(), myIndex ) );
            closeViewer();
            return;
        }

        Object uo = node.getUserObject();
        if ( !( uo instanceof PictureInfo ) ) {
            LOGGER.severe( String.format( "The new node is not for a PictureInfo object. Aborting. userObject class: %s, mySetOfNodes: %s, index: %d", node.getUserObject().getClass().toString(), mySetOfNodes.toString(), myIndex ) );
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
            mySetOfNodes.addNodeNavigatorListener( this );
        } else {
            //did we get a new navigator?
            if ( !this.mySetOfNodes.equals( mySetOfNodes ) ) {
                LOGGER.info( String.format( "Got a new navigator: old: %s new: %s", this.mySetOfNodes.toString(), mySetOfNodes.toString() ) );
                //get rid of the old navigator
                this.mySetOfNodes.removeNodeNavigatorListener( this );
                this.mySetOfNodes.getRid(); // help Grabage collection remove the listener
                // add viewer to the new one
                this.mySetOfNodes = mySetOfNodes;
                mySetOfNodes.addNodeNavigatorListener( this );
            }
        }

        this.myIndex = myIndex;

        pictureFrame.descriptionJTextField.setText( pictureInfo.getDescription() );
        setPicture( pictureInfo );


        pictureFrame.getPictureViewerNavBar().setIconDecorations();
        pictureJPanel.requestFocusInWindow();
    }

    /**
     *  brings up the indicated picture on the display.
     *  @param pi  The PicutreInfo object that should be displayed
     */
    private void setPicture( PictureInfo pi ) {
        LOGGER.fine( "Set picture to PictureInfo: " + pi.toString() );
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
            LOGGER.severe( "MarformedURLException trapped on: " + pi.getHighresLocation() + "\nReason: " + x.getMessage() );
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
     * Requests that the current picture be rotated from it's current rotation by 
     * the specified amount.
     * @param angle The angle by which the picture should be rotated
     */
    @Override
    public void rotate( int angle ) {
        PictureInfo pi = (PictureInfo) getCurrentNode().getUserObject();
        pi.rotate( angle );
        pictureJPanel.requestFocusInWindow();
    }

    /**
     *  here we get notified by the PictureInfo object that something has
     *  changed.
     */
    @Override
    public void pictureInfoChangeEvent( PictureInfoChangeEvent e ) {
        if ( e.getDescriptionChanged() ) {
            String s = e.getPictureInfo().getDescription();
            if ( s == null ) {
                LOGGER.warning( "Got called without a description in the picture. " + e.toString() );
            } else {
                pictureFrame.descriptionJTextField.setText( s );
            }

        }
        if ( e.getHighresLocationChanged() ) {
            SortableDefaultMutableTreeNode node = mySetOfNodes.getNode( myIndex );
            if ( node == null ) {
                LOGGER.warning( "Highres change got called without a node. Index: " + Integer.toString( myIndex ) + mySetOfNodes.toString() );
                return;

            }

            PictureInfo pi = (PictureInfo) node.getUserObject();
            if ( pi == null ) {
                LOGGER.warning( "Highres location change got called without a PictureInfor user object. " + e.toString() );
            } else {
                setPicture( pi );
            }

        }
        if ( e.getRotationChanged() ) {
            PictureInfo pi = (PictureInfo) mySetOfNodes.getNode( myIndex ).getUserObject();
            if ( pi == null ) {
                LOGGER.warning( "Rotation change got called without a PictureInfor user object. " + e.toString() );
            } else {
                setPicture( pi );
            }

        }
    }

    /**
     * gets called when the Navigator notices a change
     */
    @Override
    public void nodeLayoutChanged() {
        LOGGER.info( String.format( "Got notified to relayout" ) );
        show( mySetOfNodes, myIndex );

    }

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
    @Override
    public boolean requestNextPicture() {
        LOGGER.fine( String.format( "Using the context aware step forward. The browser contains: %d pictures and we are on picture %d", mySetOfNodes.getNumberOfNodes(), myIndex ) );
        if ( mySetOfNodes.getNumberOfNodes() > myIndex + 1 ) {
            LOGGER.fine( "PictureViewer.requestNextPicture: requesting node: " + Integer.toString( myIndex + 1 ) );
            Runnable r = new Runnable() {

                @Override
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
     * @return true if successful, false if not.
     */
    @Override
    public boolean requestPriorPicture() {
        LOGGER.fine( "PictureViewer.requestPriorPicture: using the context aware step backward" );
        if ( myIndex > 0 ) {
            Runnable r = new Runnable() {

                @Override
                public void run() {
                    show( mySetOfNodes, myIndex - 1 );
                }
            };
            SwingUtilities.invokeLater( r );
            return true;
        }
        return false;
    }

    /**
     *  method that cancels a timer if one is running or calls the method to
     *  bring up the dialog to start a timer
     */
    @Override
    public void requestAutoAdvance() {
        if ( advanceTimer != null ) {
            stopTimer();
            pictureFrame.getPictureViewerNavBar().setClockIdle();
        } else {
            doAutoAdvanceDialog();
        }

        pictureJPanel.requestFocusInWindow();
    }

    /**
     *  method that brings up a dialog box and asks the user how he would
     *  like auto advance to work
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
                pictureFrame.myJFrame,
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
    public void startAdvanceTimer( int seconds ) {
        advanceTimer = new AdvanceTimer( this, seconds );
        pictureFrame.getPictureViewerNavBar().setClockBusy();
        pictureFrame.getPictureViewerNavBar().showDelaySilder();
    }

    /**
     * This method sets up the Advance Timer
     * @param delay the delay (in seconds?)
     */
    @Override
    public void setTimerDelay( int delay ) {
        if ( advanceTimer != null ) {
            advanceTimer.setDelay( delay );
        }
    }

    /**
     *  method to stop any timer that might be running
     */
    public void stopTimer() {
        if ( advanceTimer != null ) {
            advanceTimer.stopThread();
        }

        advanceTimer = null;
        pictureFrame.getPictureViewerNavBar().hideDelaySilder();
    }

    /**
     * method that tells the AdvanceTimer whether it is OK to advance the picture or not
     * This important to avoid the submission of new picture requests before the old
     * ones have been met.
     */
    @Override
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
    @Override
    public void requestAdvance() {
        requestNextPicture();
    }

    /**
     *   this method gets invoked from the PicturePane object
     *   to notify of status changes. It updates the description
     *   panel at the bottom of the screen with the status. If the
     *   status was a notification of the image starting to load
     *   the progress bar is made visible. Any other status hides
     *   the progress bar.
     *
     * @param pictureStatusCode
     * @param pictureStatusMessage
     */
    @Override
    public void scalableStatusChange( final int pictureStatusCode,
            final String pictureStatusMessage ) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                switch ( pictureStatusCode ) {
                    case ScalablePicture.UNINITIALISED:
                        pictureFrame.loadJProgressBar.setVisible( false );
                        break;

                    case ScalablePicture.GARBAGE_COLLECTION:
                        pictureFrame.loadJProgressBar.setVisible( false );
                        break;

                    case ScalablePicture.LOADING:
                        if ( pictureFrame.myJFrame != null ) {
                            pictureFrame.loadJProgressBar.setVisible( true );
                        }

                        break;
                    case ScalablePicture.LOADED:
                        pictureFrame.loadJProgressBar.setVisible( false );
                        //descriptionJTextField.setText( getDescription() );
                        break;

                    case ScalablePicture.SCALING:
                        pictureFrame.loadJProgressBar.setVisible( false );
                        break;

                    case ScalablePicture.READY:
                        pictureFrame.loadJProgressBar.setVisible( false );
                        if ( pictureFrame.myJFrame != null ) {
                            pictureFrame.myJFrame.toFront();
                        }

                        break;
                    case ScalablePicture.ERROR:
                        pictureFrame.loadJProgressBar.setVisible( false );
                        ;
                        break;

                    default:

                        LOGGER.warning( "Got called with a code that is not understood: " + Integer.toString( pictureStatusCode ) + " " + pictureStatusMessage );
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
     *  method that gets invoked from the PicturePane object to notify of status changes
     *
     * @param statusCode
     * @param percentage
     */
    @Override
    public void sourceLoadProgressNotification( final int statusCode,
            final int percentage ) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                switch ( statusCode ) {
                    case SourcePicture.LOADING_STARTED:
                        pictureFrame.loadJProgressBar.setValue( 0 );
                        pictureFrame.loadJProgressBar.setVisible( true );
                        break;

                    case SourcePicture.LOADING_PROGRESS:
                        pictureFrame.loadJProgressBar.setValue( percentage );
                        pictureFrame.loadJProgressBar.setVisible( true );
                        break;

                    case SourcePicture.LOADING_COMPLETED:
                        pictureFrame.loadJProgressBar.setVisible( false );
                        pictureFrame.loadJProgressBar.setValue( 0 ); // prepare for the next load
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
     * Sets the scale of the picture to the current screen size and centres it there.
     */
    @Override
    public void resetPicture() {
        pictureJPanel.zoomToFit();
        pictureJPanel.centerImage();
        pictureJPanel.requestFocusInWindow();
    }

    /**
     *  This function cycles to the next info display. The first is DISPLAY_NONE, DISPLAY_PHOTOGRAPHIC
     *  and DISPLAY_APPLICATION
     **/
    @Override
    public void cylceInfoDisplay() {
        pictureJPanel.cylceInfoDisplay();
        pictureJPanel.requestFocusInWindow();
    }

    /**
     * Makes the PictureJPanel zoom in
     */
    @Override
    public void zoomIn() {
        pictureJPanel.zoomIn();
    }

    /**
     * Makes the PictureJPanel zoom out
     */
    @Override
    public void zoomOut() {
        pictureJPanel.zoomOut();
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
            LOGGER.fine( "PicturePane.mouseClicked" );
            if ( e.getButton() == 3 ) {
                // Right Mousebutton zooms out
                pictureJPanel.centerWhenScaled = false;
                zoomOut();
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
                zoomIn();
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
                LOGGER.fine( "PicturePane.mouseDragged: Switching to drag mode." );
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
            LOGGER.fine( "PicturePane.mouseReleased." );
            if ( Dragging ) {
                //Dragging has ended
                Dragging = false;
                pictureJPanel.setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
            }
        }
    }  //end class Listener
}
