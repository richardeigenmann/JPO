package jpo;

import java.util.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.WindowConstants.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.io.*;


/*
PictureViewer.java:  class that displays a window in which a picture is shown.

Copyright (C) 2002-2006  Richard Eigenmann.
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
 *   PictureViewer manages a window which displays a picture. It provides navigation control 
 *   the collection as well as mouse and keyboard control over the zooming
 *
 *   <img src="../PictureViewer.png" border=0>
 **/
public class PictureViewer extends JPanel 
		 	   implements ScalablePictureListener,
				      AdvanceTimerInterface,
				      ChangeWindowInterface,
				      PictureInfoChangeListener,
				      TreeModelListener
				       {



	/**
	 *  The PictureViewer is deliberately just a JPanel so that it can attach 
	 *  itself to a JFrame. The JFrame will be destroyed and recrecated depending
	 *  on how the user would like to see this window.
	 **/
	private JFrame myJFrame;

	
	/**
	 *  flag that specifies whether the window should be drawn with decoration or not
	 */
	private boolean decorateWindow = true;
	
	
	/**
	 *  flag that specifies whether the navication components should be added
	 */
	private boolean displayNavigationControls = true;



	/**
	 *  indicator that specifies what sort of window should be created
	 */
	public int windowMode = WINDOW_DEFAULT;

	
	/**
	 *  constant to indicate that a Fullscreen window should be created.
	 */
	public static final int WINDOW_FULLSCREEN = 1;
	
	/**
	 *  constant to indicate that the window should be created on the LEFT half of the display
	 */
	public static final int WINDOW_LEFT = WINDOW_FULLSCREEN + 1;

	/**
	 *  constant to indicate that the window should be created on the LEFT half of the display
	 */
	public static final int WINDOW_RIGHT = WINDOW_LEFT + 1;


	/**
	 *  constant to indicate that the window should be created on the TOP LEFT quarter of the display
	 */
	public static final int WINDOW_TOP_LEFT = WINDOW_RIGHT + 1;


	/**
	 *  constant to indicate that the window should be created on the TOP RIGHT quarter of the display
	 */
	public static final int WINDOW_TOP_RIGHT = WINDOW_TOP_LEFT + 1;


	/**
	 *  constant to indicate that the window should be created on the BOTTOM LEFT quarter of the display
	 */
	public static final int WINDOW_BOTTOM_LEFT = WINDOW_TOP_RIGHT + 1;


	/**
	 *  constant to indicate that the window should be created on the BOTTOM RIGHT quarter of the display
	 */
	public static final int WINDOW_BOTTOM_RIGHT = WINDOW_BOTTOM_LEFT + 1;


	/**
	 *  constant to indicate that the window should be created on the Default area
	 */
	public static final int WINDOW_DEFAULT = WINDOW_BOTTOM_RIGHT + 1;



	/**
	 *  a reference to the currently displayed node of the model to allows navigation forwards and backwards
	 */
	private SortableDefaultMutableTreeNode currentNode = null;


	/**
	 *  the context of the browsing
	 */
	private ThumbnailBrowserInterface mySetOfNodes = null;
	
	/**
	 *  the position in the context being shown
	 */
	private int myIndex = 0;
	

	/**
	 *   The pane that handles the image drawing aspects.
	 **/
	private PicturePane pictureJPanel = new PicturePane();


	/**
	 *   progress bar to track the pictures loaded so far
	 */
	private JProgressBar loadJProgressBar = new JProgressBar();


	/**
	 *   Description Panel 
	 **/
	private JTextArea descriptionJTextField = new JTextArea();



	/** 
	 *   icon for the simple next picture
	 */
	private static final ImageIcon nextImageIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_next.gif" ) );

	/** 
	 *   icon to indicate the next picture is from a new group
	 */
	private static final ImageIcon iconNextNext = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_nextnext.gif" ) );


	/** 
	 *   icon to indicate the next picture is from a new group
	 */
	private static final ImageIcon iconNoNext = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_nonext.gif" ) );


	/**
	 *   icon to indicate that there is a previous image in the same group
	 */
	private static final ImageIcon iconPrevious = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_previous.gif" ) );
	
	
	/**
	 *   icon to indicate that there is an image in the previous group
	 */
	private static final ImageIcon iconPrevPrev = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_prevprev.gif" ) );

	
	/**
	 *   icon to indicate that there are no images before the current one in the album
	 */
	private static final ImageIcon iconNoPrev = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_noprev.gif" ) );


	/**
	 *   icon to rotate right
	 */
	private static final ImageIcon iconRotateRight = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_RotCWDown.gif" ) );

	/**
	 *  button to rotate right
	 */
	private JButton rotateRightJButton = new JButton( iconRotateRight );

	/**
	 *   icon to rotate left
	 */
	private static final ImageIcon iconRotateLeft = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_RotCCDown.gif" ) );


	/**
	 *  button to rotate left
	 */
	private JButton rotateLeftJButton = new JButton( iconRotateLeft );


	/**
	 *  Button that is put in the NavigationPanel to allow the user to navigate to the previous
	 *  picture. The {@link #changePicture} analyses the context (previous pictures in the group, picture
	 *  in previous group, beginning of pictures) and displayes the icon {@link #iconPrevious}, {@link #iconPrevPrev}
	 *  {@link #iconNoPrev} as appropriate.
	 */
	private JButton previousJButton = new JButton( iconPrevious );
 

	/**
	 *   icon to indicate that the timer is available
	 */
	private static final ImageIcon iconClockOff = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_clock_off.gif" ) );
	

	/**
	 *   icon to indicate that the timer is active
	 */
	private static final ImageIcon iconClockOn = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_clock_on.gif" ) );

	/**
	 *   Button to move to the next image.
	 */
	private JButton nextJButton = new JButton( nextImageIcon );
	
	/**
	 *  Button to expand the windo to full screen or a different window size.
	 */
	private JButton fullScreenJButton = new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_Frames.gif" ) ) );
	
	/**
	 *  Button to bring up the popup menu to do things to the image.
	 */
	private JButton popupMenuJButton = new JButton(new ImageIcon( Settings.cl.getResource( "jpo/images/icon_FingerUp.gif")));
	
	/**
	 *  Button to turn on the blending in of info or turn it off.
	 */
	private JButton infoJButton = new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_info.gif" ) ) );
	
	/**
	 *  Button to rezise the image so that it fits in the screen.
	 */
	private JButton resetJButton = new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/icon_reset.gif" ) ) );
	
	/**
	 *   Button to start the auto timer or turn it off.
	 */
	private JButton clockJButton = new JButton( iconClockOff );
	
	
	/**
	 *   icon to close the image
	 */	
	private static final ImageIcon closeIcon = new ImageIcon( Settings.cl.getResource( "jpo/images/icon_close2.gif" ) );
		
	/**
	 *  button to close the window
	 */
	private JButton closeJButton = new JButton( closeIcon );



	/**
	 *  variable which controls whether the autoadvance cycles through the current 
	 *  group only or whether it is allowed to cycle through images in the collection
	 */
	private boolean cycleAll = true;

	
	/**
	 *  variable that controls whether the automatic advance selects the next
	 *  picture at random or whether it follows the collection.
	 */
	private boolean randomAdvance = true;


	/**
	 *  the timer that can call back into the object with the instruction to
	 *  load the next image
	 */
	private AdvanceTimer advanceTimer = null;


	/**
	 *  a brute force way of getting a random picture out of the JTree. I copy all 
	 *  Nodes (or rather a reference to them) to the ArrayList and then use random
	 *  to access an element
	 */
	//private ArrayList pictureNodesArrayList = null;
	



	/**
	 *  popup menu for window mode changing
	 */
	private ChangeWindowPopupMenu changeWindowPopupMenu = new ChangeWindowPopupMenu ( this );



	/**
	 *  Brings up a window in which a picture node is displayed. This class 
	 *  handles all the user interaction such as zoom in / out, drag, navigation,
	 *  information display and keyboard keys.
	 **/
	public PictureViewer() {
		setBackground( Color.black );
		setOpaque( true );
		setFocusable( false );


		// set this up so that we can close the GUI if the picture node is deleted while we 
		// are displaying it.
		Settings.pictureCollection.getTreeModel().addTreeModelListener( this );


		GridBagConstraints c = new GridBagConstraints();
		this.setLayout( new GridBagLayout() );


		// Picture Painter Pane
		pictureJPanel.setBackground(Color.black);
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
		add( pictureJPanel, c );


		pictureJPanel.addStatusListener( this );

		loadJProgressBar.setPreferredSize( new Dimension( 120, 20 ) );
		loadJProgressBar.setMaximumSize( new Dimension( 140, 20 ) );
		loadJProgressBar.setMinimumSize( new Dimension( 80, 20 ) );
		loadJProgressBar.setBackground( Color.black ); 
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
		add( loadJProgressBar, c );


		// The Description_Panel
		descriptionJTextField.setFont( Font.decode( Settings.jpoResources.getString("PictureViewerDescriptionFont") ) );
		descriptionJTextField.setWrapStyleWord( true );
		descriptionJTextField.setLineWrap( true );
		descriptionJTextField.setEditable( true );
		descriptionJTextField.setForeground( Color.white );
		descriptionJTextField.setBackground( Color.black );
		descriptionJTextField.setOpaque( true );
		descriptionJTextField.setBorder( new EmptyBorder(2,12,0,0) );
		descriptionJTextField.setMinimumSize( new Dimension( 80, 26 ) );
		/*descriptionJTextField.getDocument().addDocumentListener( new DocumentListener() {
			public void insertUpdate( DocumentEvent e ) {
				Tools.log( "insert received" + e.toString() );
				changeDescription();
			}
			public void removeUpdate( DocumentEvent e ) {
				Tools.log( "remove received" );
				changeDescription();
			}
			public void changedUpdate( DocumentEvent e ) {
				Tools.log( "changed received" );
				changeDescription();
			}
			public void changeDescription() {
				Tools.log("got a document event");
				/*Object uo = currentNode.getUserObject();
				if ( uo != null ) {
					if ( uo instanceof PictureInfo ) {
						((PictureInfo) uo).setDescription( descriptionJTextField.getText () );
					}
				}* /
			}
		}); */

		JScrollPane descriptionJScrollPane = new JScrollPane( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		descriptionJScrollPane.setViewportView( descriptionJTextField );
		descriptionJScrollPane.setBorder(new EmptyBorder(0,0,0,0));
		descriptionJScrollPane.setBackground( Color.black );
		descriptionJScrollPane.setOpaque(true);
		c.weightx = 1;
		c.weighty = 0.01;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = 1;
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add( descriptionJScrollPane, c ) ;



		//  The NavigationPanel
		final int numButtons = 8;
		final Dimension navButtonSize = new Dimension( 24, 24);
		final JToolBar NavigationPanel = new JToolBar( Settings.jpoResources.getString("NavigationPanel") );

		NavigationPanel.setBackground( Color.black );
		NavigationPanel.setFloatable( true );
		NavigationPanel.setMinimumSize(new Dimension(36 * numButtons, 26));
		NavigationPanel.setPreferredSize(new Dimension(36 * numButtons, 26));
		NavigationPanel.setMaximumSize(new Dimension(36 * numButtons, 50));
		NavigationPanel.setRollover( true );
		NavigationPanel.setBorderPainted( false );

		previousJButton.setMnemonic(KeyEvent.VK_P);
		previousJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				requestPriorPicture();
				myJFrame.getGlassPane().requestFocus();
			}
		});
		previousJButton.setToolTipText( Settings.jpoResources.getString("previousJButton.ToolTipText") );
		previousJButton.setBorderPainted(false);
		previousJButton.setBackground(Color.black);
		previousJButton.setPreferredSize( navButtonSize );
		NavigationPanel.add(previousJButton);


		nextJButton.setMnemonic(KeyEvent.VK_N);
		nextJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				requestNextPicture();
				myJFrame.getGlassPane().requestFocus();
			}
		});
		nextJButton.setToolTipText( Settings.jpoResources.getString("nextJButton.ToolTipText") );
		nextJButton.setBorderPainted(false);
		nextJButton.setBackground(Color.black);
		nextJButton.setPreferredSize( navButtonSize );
		NavigationPanel.add(nextJButton);


		rotateLeftJButton.setMnemonic(KeyEvent.VK_L);
		rotateLeftJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				currentNode.rotatePicture( 270 );
				myJFrame.getGlassPane().requestFocus();
			}
		});
		rotateLeftJButton.setToolTipText( Settings.jpoResources.getString("rotateLeftJButton.ToolTipText") );
		rotateLeftJButton.setBorderPainted( false );
		rotateLeftJButton.setBackground( Color.black );
		rotateLeftJButton.setPreferredSize( navButtonSize );
		NavigationPanel.add( rotateLeftJButton );




		rotateRightJButton.setMnemonic(KeyEvent.VK_R);
		rotateRightJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				currentNode.rotatePicture( 90 );
				myJFrame.getGlassPane().requestFocus();
			}
		});
		rotateRightJButton.setToolTipText( Settings.jpoResources.getString("rotateRightJButton.ToolTipText") );
		rotateRightJButton.setBorderPainted( false );
		rotateRightJButton.setBackground( Color.black );
		rotateRightJButton.setPreferredSize( navButtonSize );
		NavigationPanel.add( rotateRightJButton );


		fullScreenJButton.setMnemonic( KeyEvent.VK_F );
		fullScreenJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				requestScreenSizeMenu();
				myJFrame.getGlassPane().requestFocus();
			}
		});
		fullScreenJButton.setBorderPainted( false );
		fullScreenJButton.setToolTipText( Settings.jpoResources.getString("fullScreenJButton.ToolTipText") );
		fullScreenJButton.setBackground( Color.black );
		fullScreenJButton.setPreferredSize( navButtonSize );
		NavigationPanel.add( fullScreenJButton );


		popupMenuJButton.setMnemonic( KeyEvent.VK_M );
		popupMenuJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				requestPopupMenu();
				myJFrame.getGlassPane().requestFocus();
			}
		});
		popupMenuJButton.setBorderPainted( false );
		popupMenuJButton.setToolTipText( Settings.jpoResources.getString("popupMenuJButton.ToolTipText") );
		popupMenuJButton.setBackground( Color.black );
		popupMenuJButton.setPreferredSize( navButtonSize );
		popupMenuJButton.setVisible( true );
		NavigationPanel.add( popupMenuJButton );

		infoJButton.setMnemonic(KeyEvent.VK_I);
		infoJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				pictureJPanel.cylceInfoDisplay();
				myJFrame.getGlassPane().requestFocus();
			}
		});
		infoJButton.setBorderPainted(false);
		infoJButton.setToolTipText( Settings.jpoResources.getString("infoJButton.ToolTipText") );
		infoJButton.setBackground(Color.black);
		infoJButton.setPreferredSize( navButtonSize );
		NavigationPanel.add(infoJButton);

		resetJButton.setMnemonic(KeyEvent.VK_ESCAPE);
		resetJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				resetPicture();
				myJFrame.getGlassPane().requestFocus();
			}
		});
		resetJButton.setBorderPainted(false);
		resetJButton.setToolTipText( Settings.jpoResources.getString("resetJButton.ToolTipText") );
		resetJButton.setBackground(Color.black);
		resetJButton.setPreferredSize( navButtonSize );
		NavigationPanel.add(resetJButton);

		clockJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				requestAutoAdvance();
				myJFrame.getGlassPane().requestFocus();
			}
		});
		clockJButton.setBorderPainted(false);
		clockJButton.setToolTipText( Settings.jpoResources.getString("clockJButton.ToolTipText") );
		clockJButton.setBackground( Color.black );
		clockJButton.setPreferredSize( navButtonSize );
		NavigationPanel.add( clockJButton );
		
		closeJButton.setMnemonic(KeyEvent.VK_C);
		closeJButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				closeViewer();
			}
		});
		closeJButton.setToolTipText( Settings.jpoResources.getString("closeJButton.ToolTipText") );
		closeJButton.setBorderPainted(false);
		closeJButton.setBackground(Color.black);
		closeJButton.setPreferredSize( navButtonSize );
		NavigationPanel.add( closeJButton );

		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.gridwidth = 1;
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LAST_LINE_END;
		this.add( NavigationPanel ,c );
	}



	/**
	 *  request that the window mode be changed.
	 */
	public void switchWindowMode ( int newMode ) {
		windowMode = newMode;
		// some intelligence as to when to have window decorations and when not.
		switch ( newMode ) {
			case WINDOW_FULLSCREEN: 
				decorateWindow = false;
				break;
			case WINDOW_LEFT: 
				decorateWindow = false;
				break;
			case WINDOW_RIGHT: 
				decorateWindow = false;
				break;
			case WINDOW_TOP_LEFT: 
				decorateWindow = true;
				break;
			case WINDOW_TOP_RIGHT: 
				decorateWindow = true;
				break;
			case WINDOW_BOTTOM_LEFT: 
				decorateWindow = true;
				break;
			case WINDOW_BOTTOM_RIGHT: 
				decorateWindow = true;
				break;
			case WINDOW_DEFAULT: 
				decorateWindow = true;
				break;
		}
		createWindow();
	}
	


	/**
	 *  This method turns on or truns off the frame around the window. It works by closing 
	 *  the window and creatig a new one with the correct decorations. It uses the decorateWindow 
	 *  flag to determine if the decorations are being shown.
	 *  @param newDecorations    Set to true if you want a frame, false if you do not.
	 */
	public void switchDecorations ( boolean newDecorations ) {
		if ( decorateWindow != newDecorations ) {
			decorateWindow = newDecorations;
			if ( myJFrame.isDisplayable() ) {
				myJFrame.dispose();
				myJFrame.setUndecorated( ! decorateWindow );
				myJFrame.setVisible( true );
			} else {
				myJFrame.setUndecorated( ! decorateWindow );
			}				
		}
	}



	/**
	 *  Method that creates the JFrame and attaches the this JPanel object to it. 
	 **/
	private void createWindow() {

		// if the window exists, get rid of it.	
		closeMyWindow();

		myJFrame = new JFrame( Settings.jpoResources.getString("PictureViewerTitle") );
		myJFrame.setUndecorated( ! decorateWindow );
		myJFrame.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				closeViewer();
			}
		});


		Rectangle virtualBounds = Tools.getScreenDimensions();
		virtualBounds.height = virtualBounds.height - Settings.leaveForPanel;

		if ( windowMode == WINDOW_FULLSCREEN ) {
			myJFrame.setBounds( virtualBounds );
		} else if ( windowMode == WINDOW_LEFT ) {
			myJFrame.setBounds( new Rectangle (
				0, 
				0,
				(int) virtualBounds.width / 2,
				virtualBounds.height ) );
		} else if ( windowMode == WINDOW_RIGHT ) {
			myJFrame.setBounds( new Rectangle (
				(int) virtualBounds.width / 2, 
				0,
				(int) virtualBounds.width / 2,
				virtualBounds.height ) );
		} else if ( windowMode == WINDOW_TOP_LEFT ) {
			myJFrame.setBounds( new Rectangle (
				0, 
				0,
				(int) virtualBounds.width / 2,
				(int) virtualBounds.height / 2) );
		} else if ( windowMode == WINDOW_TOP_RIGHT ) {
			myJFrame.setBounds( new Rectangle (
				(int) virtualBounds.width / 2, 
				0,
				(int) virtualBounds.width / 2,
				(int) virtualBounds.height / 2 ) );
		} else if ( windowMode == WINDOW_BOTTOM_LEFT ) {
			myJFrame.setBounds( new Rectangle (
				0,
				(int) virtualBounds.height / 2, 
				(int) virtualBounds.width / 2,
				(int) virtualBounds.height / 2 ) );
		} else if ( windowMode == WINDOW_BOTTOM_RIGHT ) {
			myJFrame.setBounds( new Rectangle (
				(int) virtualBounds.width / 2,
				(int) virtualBounds.height / 2, 
				(int) virtualBounds.width / 2,
				(int) virtualBounds.height / 2 ) );
		} else if ( windowMode == WINDOW_DEFAULT ) {
			
			myJFrame.setSize( Settings.pictureViewerDefaultDimensions.getSize() );
			myJFrame.setLocation( Settings.pictureViewerDefaultDimensions.getLocation() );
			
		}



		// set layout manager and add the PictureViewer Panel
		myJFrame.getContentPane().setLayout(new BorderLayout() );
		myJFrame.getContentPane().add( "Center", this);

		myJFrame.setBackground(Color.black );
		myJFrame.setVisible(true);

		// set up the glass pane over the window so that 
		// it intercepts the keystrokes. The Glass pane needs
		// to be kept in focus.
		myJFrame.getGlassPane().addKeyListener(
			new KeyAdapter () { 
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
					myJFrame.getGlassPane().requestFocus();
				}
			}
		);
		myJFrame.getGlassPane().setVisible( true );
		myJFrame.getGlassPane().setFocusable( true );
		myJFrame.getGlassPane().requestFocus();

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
	 *   method to close the PictureViewer. This different from closing the 
	 *   window in which the PictureViewer is being displayed because unlike most
	 *   apps I have in this instance chosen the the JPanel owns the JFrame which 
	 *   displays it. This is to allow the JPanel to open and close the JFrame 
	 *   with different options upon user request.
	 */
	private void closeViewer() {	
		Settings.pictureCollection.getTreeModel().removeTreeModelListener( this );
		stopTimer();
		closeMyWindow ();
	}
		

	/**
	 *  method to toggle to a frameless window.
	 **/
	private void requestScreenSizeMenu() {
		changeWindowPopupMenu.show( fullScreenJButton, 0, (int) (0 - changeWindowPopupMenu.getSize().getHeight()) );
		myJFrame.getGlassPane().requestFocus();
	}


	/**
	 *  method to toggle to a frameless window.
	 **/
	private void requestPopupMenu() {
		PicturePopupMenu pm = new PicturePopupMenu( currentNode );
		pm.show( fullScreenJButton, 0, (int) (0 - pm.getSize().getHeight()) );
		myJFrame.getGlassPane().requestFocus();
	}


	/**
	 *  call this method to request a picture to be shown.
	 *
	 *  @param mySetOfNodes  The set of nodes which holds the links to the images
	 *  @param myIndex  The index of the pictures to be shown.
	 */
	public void changePicture( ThumbnailBrowserInterface mySetOfNodes, int myIndex ) {
		Tools.log("PictureViewer.changePicture: called the good new way.");
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
				pi.setDescription( descriptionJTextField.getText () );
			}	
		}


		
		currentNode = node;
		descriptionJTextField.setText( getDescription() );
		pictureJPanel.setPicture( (PictureInfo) node.getUserObject() ); 


		// attach the change listener
		PictureInfo pi = (PictureInfo) currentNode.getUserObject();
		pi.addPictureInfoChangeListener( this );

		
		setIconDecorations();

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
				pi.setDescription( descriptionJTextField.getText () );
			}	
		}


		
		currentNode = node;
		descriptionJTextField.setText( getDescription() );
		pictureJPanel.setPicture( (PictureInfo) node.getUserObject() ); 


		// attach the change listener
		PictureInfo pi = (PictureInfo) currentNode.getUserObject();
		pi.addPictureInfoChangeListener( this );

		
		setIconDecorations();

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
			descriptionJTextField.setText( e.getPictureInfo().getDescription () );
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
	public void treeNodesChanged ( TreeModelEvent e ) {
		setIconDecorations();
	}

	/**
	 *   implemented here to satisfy the TreeModelListener interface; not used.
	 */
	public void treeNodesInserted ( TreeModelEvent e ) {
		setIconDecorations();
	}

	/**
	 *  The TreeModelListener interface tells us of tree node removal events.
	 *  If we receive a removal event we need to find out if the PictureViewer is 
	 *  displaying an image and if it is whether this is the node being removed or 
	 *  a descendant of it. If so it must switch to the next node. If the next node 
	 *  is a group then the viewer is closed.
	 *  If the picture browser is not in the foreground it is closed.
	 */
	public void treeNodesRemoved ( TreeModelEvent e ) {
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
			
		setIconDecorations();
	}

	/**
	 *   implemented here to satisfy the TreeModelListener interface; not used.
	 */
	public void treeStructureChanged ( TreeModelEvent e ) {
		setIconDecorations();
	}


	/**
	 *  This method looks at the position the currentNode is in regard to it's siblings and
	 *  changes the forward and back icons to reflect the position of the current node.
	 */
	public void setIconDecorations() {
		// Set the next and back icons
		if ( currentNode != null ) {
			DefaultMutableTreeNode NextNode = currentNode.getNextSibling();
			if ( NextNode != null ) {
				Object nodeInfo = NextNode.getUserObject();
				if (nodeInfo instanceof PictureInfo) {
					// because there is a next sibling object of type 
					// PictureInfo we should set the next icon to the 
					// icon that indicates a next picture in the group
					nextJButton.setIcon( nextImageIcon );
				} else {
					// it must be a GroupInfo node
					// since we must descend into it it gets a nextnext icon.
					nextJButton.setIcon( iconNextNext );
				}
			} else {
				// the getNextSibling() method returned null
				// if the getNextNode also returns null this was the end of the album
				// otherwise there are more pictures in the next group.
				if ( currentNode.getNextNode() != null )
					nextJButton.setIcon( iconNextNext );
				else
					nextJButton.setIcon( iconNoNext );
			}
			
			// let's see what we have in the way of previous siblings..
			
			if (currentNode.getPreviousSibling() != null)
				previousJButton.setIcon(iconPrevious);
			else {
				// deterine if there are any previous nodes that are not groups.
				DefaultMutableTreeNode testNode;
				testNode = currentNode.getPreviousNode();
				while ((testNode != null) && (! (testNode.getUserObject() instanceof PictureInfo))) {
					testNode = testNode.getPreviousNode();
				}
				if (testNode == null)
					previousJButton.setIcon(iconNoPrev);
				else
					previousJButton.setIcon(iconPrevPrev);
			}
		}
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
			Tools.log("PictureViewer.requestNextPicture: using non context aware step forward");
			SortableDefaultMutableTreeNode nextNode = currentNode.getNextPicture();
			if ( nextNode != null )  {
				changePicture( nextNode );
				return true;
			} else {
				return false;
			}
		} else {
			// use context aware step forward
			Tools.log("PictureViewer.requestNextPicture: using the context aware step forward. The browser contains: " + Integer.toString( mySetOfNodes.getNumberOfNodes() ) + " pictures");
			if ( mySetOfNodes.getNumberOfNodes() > myIndex ) {
				Tools.log("PictureViewer.requestNextPicture: requesting node: " + Integer.toString( myIndex + 1) );
				changePicture( mySetOfNodes, myIndex + 1 );
				return true;
			} else {
				return false;
			}
		}
	}






	/**
	 *  method that cancels a timer is one is running or calls the method to
	 *  bring up the dialog.
	 */
	private void requestAutoAdvance() {
		if ( advanceTimer != null ) {
			stopTimer();
			clockJButton.setIcon( iconClockOff );
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
		if ( randomAdvance ) 
			randomAdvanceJRadioButton.setSelected( true );
		else 
			sequentialAdvanceJRadioButton.setSelected( true );
		
		JRadioButton restrictToGroupJRadioButton = new JRadioButton( Settings.jpoResources.getString("restrictToGroupJRadioButtonLabel") );
		JRadioButton useAllPicturesJRadioButton = new JRadioButton( Settings.jpoResources.getString("useAllPicturesJRadioButtonLabel") );
		ButtonGroup cycleButtonGroup = new ButtonGroup();
		cycleButtonGroup.add( restrictToGroupJRadioButton );
		cycleButtonGroup.add( useAllPicturesJRadioButton );
		if ( cycleAll ) 
			useAllPicturesJRadioButton.setSelected( true );
		else
			restrictToGroupJRadioButton.setSelected( true );

		JLabel timerSecondsJLabel = new JLabel ( Settings.jpoResources.getString("timerSecondsJLabelLabel") );
		//JTextField timerSecondsJTextField = new JTextField();
		WholeNumberField timerSecondsField = new WholeNumberField ( 4, 3 );
		timerSecondsField.setPreferredSize( new Dimension ( 50, 20 ) );
		timerSecondsField.setMaximumSize( new Dimension ( 50, 20 ) );
		Object [] objects = { randomAdvanceJRadioButton, 
			sequentialAdvanceJRadioButton,
			restrictToGroupJRadioButton,
			useAllPicturesJRadioButton,
			timerSecondsJLabel,
			timerSecondsField };
			
		int selectedValue = JOptionPane.showOptionDialog (
	 		this,
			objects, 
			Settings.jpoResources.getString("autoAdvanceDialogTitle"),
       			JOptionPane.OK_CANCEL_OPTION,
			JOptionPane.PLAIN_MESSAGE,
			null,
			null,
			null);

		if ( selectedValue == 0 ) {
			randomAdvance = randomAdvanceJRadioButton.isSelected();
			cycleAll = useAllPicturesJRadioButton.isSelected();
			
			if ( randomAdvance ) {
				if ( cycleAll ) 
					//enumerateAndAddToList( pictureNodesArrayList, (SortableDefaultMutableTreeNode) currentNode.getRoot()  );
					mySetOfNodes = new RandomBrowser( (SortableDefaultMutableTreeNode) currentNode.getRoot() );
				else
					//enumerateAndAddToList( pictureNodesArrayList, (SortableDefaultMutableTreeNode) currentNode.getParent() );
					mySetOfNodes = new RandomBrowser( (SortableDefaultMutableTreeNode) currentNode.getParent() );
			} else {
				if ( cycleAll ) 
					mySetOfNodes = new SequentialBrowser( (SortableDefaultMutableTreeNode) currentNode.getRoot() );
				else
					mySetOfNodes = new SequentialBrowser( (SortableDefaultMutableTreeNode) currentNode.getParent() );
				myIndex = 0;
				changePicture( mySetOfNodes, myIndex );
			}
			myIndex = 0;
			changePicture( mySetOfNodes, myIndex );
			advanceTimer = new AdvanceTimer( this, timerSecondsField.getValue() );
			clockJButton.setIcon( iconClockOn );

		}
	}


	/**
	 *  method that enumerates groupNodes and adds picture nodes to an ArrayList
	 *
	public void enumerateAndAddToList (ArrayList myList, SortableDefaultMutableTreeNode startNode) {
		Enumeration kids = startNode.children();
		SortableDefaultMutableTreeNode n;
		
		while (kids.hasMoreElements()) {
			n = (SortableDefaultMutableTreeNode) kids.nextElement();
			if (n.getUserObject() instanceof GroupInfo)
				enumerateAndAddToList (myList, n);
			else if (n.getUserObject() instanceof PictureInfo) 
				myList.add(n);
		}
	}*/



	/**
	 *   this method is invoked from the timer thread that notifies
	 *   our oject that it is time to advance to the next picture.
	 *
	public void requestAdvance() {
		SortableDefaultMutableTreeNode nextNode = null;
		
		if ( randomAdvance )
			nextNode = getRandomNode ();
		else 
			if ( cycleAll ) {
				nextNode =  currentNode.getNextPicture();
				if (nextNode == null) {
					nextNode = ((SortableDefaultMutableTreeNode) currentNode.getRoot()).getNextPicture();
				}
			} else {
				nextNode = currentNode.getNextGroupPicture();
				if (nextNode == null) {
					nextNode = ((SortableDefaultMutableTreeNode) ( (DefaultMutableTreeNode) currentNode.getParent() ).getFirstChild()).getNextGroupPicture();
				}
			}
				
		if ( nextNode == null ) {
			stopTimer();
		} else {
			changePicture( nextNode );
		}

	} */

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
	public void stopTimer () {
		if ( advanceTimer != null )
			advanceTimer.stopThread();
		advanceTimer = null;
		//pictureNodesArrayList = null; 
	}





	/**
	 *  returns the next node in the group and if it reaches the end of the list 
	 *  starts from the of the group. If there are no nodes it returns null.
	 *
	public SortableDefaultMutableTreeNode getRandomNode() {
		int nodeIndex = (int) (Math.random() * pictureNodesArrayList.size()) ;
		return (SortableDefaultMutableTreeNode) pictureNodesArrayList.get( nodeIndex );
	}*/




	/**
	 *  if a request comes in to show the previous picture the data model is asked for the prior image
	 *  and if one is returned it is displayed.
	 * 
	 * @see #requestNextPicture()
  	*/
	private void requestPriorPicture() {
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



