package jpo.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;
import jpo.gui.ChangeWindowInterface;
import jpo.gui.PictureViewerActions;

/*
PictureFrame.java:  Class that manages the frame and display of the Picutre

Copyright (C) 2002-2010  Richard Eigenmann.
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
 * Class that manages the frame and display of the Picture.
 * @author Richard Eigenmann
 */
public class PictureFrame implements ChangeWindowInterface {

    /**
     * Constructor. Initialises the GUI widgets.
     */
    public PictureFrame( final PictureViewerActions pictureViewerController ) {
        this.pictureViewerController = pictureViewerController;
        initGui();
    }
    /**
     * A handle back to the Controller the keystrokes can request actions
     */
    private final PictureViewerActions pictureViewerController;
    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( PictureFrame.class.getName() );
    /**
     *   The pane that handles the image drawing aspects.
     **/
    private PicturePane pictureJPanel = new PicturePane();

    /**
     * Provides direct access to the panel that shows the picture.
     * @return the Picture Panel
     */
    public PicturePane getPictureJPanel() {
        return pictureJPanel;
    }
    /**
     *  Navigation Panel
     */
    private final PictureViewerNavBar navButtonPanel = new PictureViewerNavBar();

    /**
     * Provides direct access to the navButtonPanel
     * @return the navButtonPanel
     */
    public PictureViewerNavBar getPictureViewerNavBar() {
        return navButtonPanel;
    }
    /**
     *  The Window in which the viewer will place it's components.
     **/
    public ResizableJFrame myJFrame;
    /**
     *  indicator that specifies what sort of window should be created
     */
    private int windowMode = ResizableJFrame.WINDOW_DEFAULT;
    /**
     *  The root JPanel
     */
    private final JPanel viewerPanel = new JPanel();
    /**
     *   progress bar to track the pictures loaded so far
     */
    public final JProgressBar loadJProgressBar = new JProgressBar();
    /**
     *   This textarea shows the description of the picture being shown
     **/
    public JTextArea descriptionJTextField = new JTextArea();

    /**
     *  This method creates all the GUI widgets and connects them for the
     *  PictureViewer.
     * TODO: Make this use MIG Layout...
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
        descriptionJTextField.setCaretColor( Settings.PICTUREVIEWER_TEXT_COLOR );
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
        navButtonPanel.setPictureViewer( pictureViewerController );
        viewerPanel.add( navButtonPanel, c );
    }

    /**
     *  Method that creates the JFrame and attaches the viewerPanel to it.
     **/
    public void createWindow() {
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
                pictureViewerController.closeViewer();
            }
        } );

        // set layout manager and add the PictureViewer Panel
        myJFrame.getContentPane().setLayout( new BorderLayout() );
        myJFrame.getContentPane().add( "Center", viewerPanel );
    }

    /**
     *   method to close the active window.
     */
    public void closeMyWindow() {
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
    @Override
    public void switchWindowMode( final int newMode ) {
        LOGGER.fine( "PictureViewer.switchWindowMode: old mode: " + Integer.toString( windowMode ) + " new: " + Integer.toString( newMode ) );
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
    @Override
    public void switchDecorations( boolean newDecoration ) {
        if ( decorateWindow != newDecoration ) {
            decorateWindow = newDecoration;
            myJFrame.getContentPane().remove( viewerPanel );
            closeMyWindow();
            createWindow();
        }
    }

    /**
     * This method sends the text of the textbox to the pictureinfo.
     * This updates the description if it has changed.
     */
    private void updateDescription() {
        Object userObject = pictureViewerController.getCurrentNode().getUserObject();
        if ( userObject != null ) {
            if ( userObject instanceof PictureInfo ) {
                LOGGER.fine( "Sending description update to " + descriptionJTextField.getText() );
                ( (PictureInfo) userObject ).setDescription(
                        descriptionJTextField.getText() );
            }

        }
    }
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
                pictureViewerController.requestNextPicture();
            } else if ( ( k == KeyEvent.VK_M ) ) {
                pictureViewerController.requestPopupMenu();
            } else if ( ( k == KeyEvent.VK_P ) ) {
                pictureViewerController.requestPriorPicture();
            } else if ( ( k == KeyEvent.VK_F ) ) {
                pictureViewerController.requestScreenSizeMenu();
            } else if ( ( k == KeyEvent.VK_SPACE ) || ( k == KeyEvent.VK_HOME ) ) {
                pictureViewerController.resetPicture();
            } else if ( ( k == KeyEvent.VK_PAGE_UP ) ) {
                pictureViewerController.zoomIn();
            } else if ( ( k == KeyEvent.VK_PAGE_DOWN ) ) {
                pictureViewerController.zoomOut();
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
}
