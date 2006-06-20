package jpo; 
 
import javax.swing.*; 
import javax.swing.border.*; 
import java.awt.event.*; 
import java.awt.*; 

import java.awt.image.*; 
import javax.swing.tree.*; 
import javax.swing.text.*; 
import javax.swing.event.*; 
import java.util.*; 
 
/*
ThumbnailJScrollPane.java:  a JScrollPane that shows thumbnails

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
 *  The ThumbnailJScrollPane manages a JPanel in a JScrollPane that displays a group of pictures
 *  in a grid of thumbnails or ad hoc search results. Real pictures are shown as a thumbnail 
 *  of the image whilst sub-groups are shown as a folder icon. Each thumbnail has it's caption 
 *  under the image. <p>
 *
 *  If the size of the component is changed the images are re-laid out and can take advantage of the 
 *  extra space if there is some.
 *
 */ 
  
public class ThumbnailJScrollPane 
	extends JScrollPane { 
 
	/**
	 *  color for title
	 */
	private static final Color GRAY_BLUE_LIGHT	= new Color (204,204,255);
	
	/**
	 *  color for title
	 */
	private static final Color GRAY_BLUE_DARK	= new Color (51,51,102);

 
	/** 
	 * the <code>JPanel</code> that is placed inside the <code>JScrollPane</code> 
	 * which holds the title and the ThumbnailComponents
	 */ 
	public JPanel ThumbnailPane = new JPanel(); 


	/**
	 *  the JLabel that holds the description of the group being shown.
	 */
	private JLabel title = new JLabel(); 


	/** 
	 * JLabel for holding the page count text 
	 * */
	private JLabel lblPage = new JLabel();										// JA
	
	/** 
	 * Number of pages currently needed for displaying a group 
	 * */
	private int pageCount;														// JA
	
	/** 
	 * currently displayed page 
	 * */
	private int curPage = 1;
	
	
	/**
	 *   progress bar to track the pictures loaded so far
	 */
	//public JProgressBar loadProgress = new JProgressBar();
 
 
	/** 
	 *   A Thread that will load the images.
	 */ 
	private ThumbnailLoaderThread tl = null; 
	 
	
	/**
	 *  variable that will signal to the thread to stop loading images.
	 */
	public boolean stopThread; 
	 
	 
	/** 
	 * the layout object that handles all layouting in the ThumbnailPane 
	 */ 
	public GridBagLayout ThumbnailLayout; 
 
 
 	/**
	 *  a GridBagConstraints object to position the Thumbnails on the panel;
	 */
	private GridBagConstraints thumbnailConstraints = new GridBagConstraints();

 	/**
	 *  a GridBagConstraints object to position theDescriptions on the panel;
	 */
	private GridBagConstraints descriptionConstraints = new GridBagConstraints();
 
 
	/**
	 *  This object refers to the set of Nodes that is being browsed in the ThumbnailJScrollPane
	 */
	public ThumbnailBrowserInterface mySetOfNodes;
 
 
	/** 
	 * the number of columns that are being displayed in the pane. When the  
	 * pane is resized the ComponentListener fires off code with repositions the 
	 * thumbnails if the number of columns has changed. 
	 */ 
	private int cols = 1; 
 

 
	/** 
	 *  a variable to hold the current starting position of thumbnails being
	 *  displayed out of a group or search.<p>
	 *
	 *  This was invented to allow the number of thumbnails to be restricted  
	 *  so that <<Out of memory>> errors may be averted on long lists of  
	 *  pictures. 
	 *
	 **/ 
	private int startIndex;  
	  
	  

 
	/** 
	 *  a button that is added when there are more than the maximum number of  
	 *  thumbnails to be shown for a search or a group. 
	 **/ 
	private JButton nextThumbnailsButton = 
		new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/Forward24.gif" ) ) ); 
 
 
	/** 
	 *  a button that is added when the first thumbnails being shown is not the 
	 *  first of the group or search. Essentially this is controlled by the  
	 *  startIndex variable.  
	 *
	 *  @see ThumbnailJScrollPane#startIndex 
	 **/ 
	private JButton previousThumbnailsButton = 
		new JButton( new ImageIcon( Settings.cl.getResource( "jpo/images/Back24.gif" ) ) );  
 
 
	/**
	 *   A reference to the root node of the tree. This allows the Thumbnail pane to
	 *   communicate with the data model and receive notifactions from it.
	 */
	public SortableDefaultMutableTreeNode rootNode;


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
	 *  This varaible keeps track of how many thumbnails per page the component was initialised
	 *  with. If the number changes because the user changed it in the settings then the difference
	 *  is recognized and the arrays are recreated.
	 */
	private int initialisedMaxThumbnails = Integer.MIN_VALUE;


	/**
	 *  The largest size for the thumbnail slider
	 */
	private static final int THUMBNAILSIZE_MIN = 1;
	/**
	 *  The smallest size for the thumbnail slider
	 */
	private static final int THUMBNAILSIZE_MAX = 4;
	/**
	 *  The starting position for the thumbnail slider
	 */
	private static final int THUMBNAILSIZE_INIT = 1;   
	
	/**
	 *   Slider to control the size of the thumbnails
	 */
	private JSlider resizeJSlider = new JSlider( JSlider.HORIZONTAL,
                                      THUMBNAILSIZE_MIN, THUMBNAILSIZE_MAX, THUMBNAILSIZE_INIT);

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
	public ThumbnailJScrollPane( SortableDefaultMutableTreeNode rootNode ) { 
		this.rootNode = rootNode;
		
		thumbnailConstraints.fill = GridBagConstraints.NONE;
		thumbnailConstraints.anchor = GridBagConstraints.SOUTH;
		thumbnailConstraints.ipadx = horizontalPadding;
		thumbnailConstraints.ipady = verticalPadding;

		descriptionConstraints.fill = GridBagConstraints.NONE;
		descriptionConstraints.anchor = GridBagConstraints.NORTH;
		descriptionConstraints.ipadx = horizontalPadding;
		descriptionConstraints.ipady = verticalPadding;

		
		ThumbnailLayout = new GridBagLayout(); 
		ThumbnailPane.setLayout( ThumbnailLayout ); 
		calculateCols();
		initThumbnailsArray();
		
		ThumbnailPane.setBackground(Color.white); 
		setViewportView( ThumbnailPane ); 
		setWheelScrollingEnabled (true ); 
		
		//  set the amount by which the panel scrolls down when the user clicks the 
		//  little down or up arrow in the scrollbar
		getVerticalScrollBar().setUnitIncrement(80);

		// register a component listener to track resize events. 
		// Got a bit confused: I am attaching the resize listener to the JScrollPane because 
		// I want to reconstrain the thumbnails if the display size can accomodate more or less
		// columns.
		// I then need to know the width minus the vertical scrollbar to determine the columns.
		this.addComponentListener(new ComponentAdapter() { 
			public void componentResized( ComponentEvent e ) { 
				Tools.log("ThumbnailJScrollPane.componentResized activated.");
				if ( calculateCols() ){ 
					for ( int i = 0; i < thumbnails.length; i++) {
						calculateConstraints( i );
						ThumbnailLayout.setConstraints( thumbnails[i], thumbnailConstraints );
						ThumbnailLayout.setConstraints( thumbnailDescriptionJPanels[i], descriptionConstraints );
					}
	 			}
				Tools.log("ThumbnailJScrollPane.componentResized ran calling validate.");
			} 
	        });   
 
 
		previousThumbnailsButton.setBorder(BorderFactory.createEmptyBorder(1,1,1,1)); 	// JA changed to none 
		//previousThumbnailsButton.setBorder(BorderFactory.createLineBorder(Color.black)); 
		previousThumbnailsButton.setPreferredSize(new Dimension(25, 25)); 
		previousThumbnailsButton.setVerticalAlignment(JLabel.CENTER); 
		previousThumbnailsButton.setOpaque(false); 
		previousThumbnailsButton.setFocusPainted(false);
		previousThumbnailsButton.setToolTipText("Previous page");						// JA
		previousThumbnailsButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				startIndex = startIndex - Settings.maxThumbnails; 
				if (startIndex < 0) {startIndex = 0;}
				getVerticalScrollBar().setValue(0);
				curPage--;
				layoutThumbnailsInThread();
				setButtonVisibility();
			}
		} ); 
		
		nextThumbnailsButton.setVerticalAlignment(JLabel.CENTER); 
		nextThumbnailsButton.setOpaque(false); 
		nextThumbnailsButton.setFocusPainted(false); 
		//nextThumbnailsButton.setBorder(BorderFactory.createLineBorder(Color.black)); 
		nextThumbnailsButton.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));    	// JA Changed to none 
		nextThumbnailsButton.setToolTipText("Next page");
		nextThumbnailsButton.setPreferredSize(new Dimension(25, 25)); 
		nextThumbnailsButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				startIndex = startIndex + Settings.maxThumbnails;
				getVerticalScrollBar().setValue(0);
				curPage++;
				layoutThumbnailsInThread(); 
				setButtonVisibility();
			}
		} ); 
 
		title.setFont( Settings.titleFont ); 
		//title.setBackground( Color.white ); 
		//title.setBorder( BorderFactory.createEmptyBorder(4,5,3,5) ); 
		//title.setOpaque( true ); 

		JPanel titleJPanel = new JPanel(); 
		BoxLayout bl = new BoxLayout( titleJPanel , BoxLayout.X_AXIS );
		titleJPanel.setBorder( BorderFactory.createBevelBorder(BevelBorder.RAISED) );	// JA
		titleJPanel.setLayout( bl );
		//titleJPanel.setBackground( Color.white );
		titleJPanel.setBackground( Color.LIGHT_GRAY ); 
		titleJPanel.add( Box.createRigidArea( new Dimension(5,0) ) );
		//titleJPanel.add( loadProgress );
		titleJPanel.add( previousThumbnailsButton );
		titleJPanel.add( nextThumbnailsButton );
		lblPage.setBorder(BorderFactory.createEmptyBorder(0,10,0,10));					// JA
		titleJPanel.add( lblPage );
		titleJPanel.add( title );
		resizeJSlider.setInverted( true );
		resizeJSlider.setSnapToTicks( true );
		resizeJSlider.setMaximumSize( new Dimension ( 80,40 ) );
		resizeJSlider.setMajorTickSpacing( 1 );
		resizeJSlider.setMinorTickSpacing( 0 );
		resizeJSlider.setPaintTicks( true );
		resizeJSlider.setPaintLabels( false );
		titleJPanel.add( resizeJSlider );
		
		resizeJSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if ( ! source.getValueIsAdjusting() ) {
					thumbnailSizeFactor = 1 / (float) source.getValue();
					Tools.log ("New Value: " + Float.toString( thumbnailSizeFactor ) );
					if ( calculateCols() ) { 
						for ( int i = 0; i < thumbnails.length; i++) {
							thumbnails[i].setFactor( thumbnailSizeFactor );
							thumbnailDescriptionJPanels[i].setFactor( thumbnailSizeFactor );
							calculateConstraints( i );
							ThumbnailLayout.setConstraints( thumbnails[i], thumbnailConstraints );
							ThumbnailLayout.setConstraints( thumbnailDescriptionJPanels[i], descriptionConstraints );
						}
	 				
						ThumbnailPane.validate();
						validate();
					}
				}
			}
		} );




		previousThumbnailsButton.setVisible( false );
		nextThumbnailsButton.setVisible( false );
		
		setColumnHeaderView( titleJPanel ); 
		 
		JPanel whiteArea = new JPanel(); 
		setCorner( JScrollPane.UPPER_RIGHT_CORNER, whiteArea ); 
		
		ThumbnailPane.addMouseListener( new MouseInputAdapter() {
			public void mousePressed( MouseEvent e ) {
				//Tools.log("Thumbnail pane registered a mousePressed event");
				mousePressedPoint = e.getPoint();
			}
			
			public void mouseReleased( MouseEvent e ) {
				//Tools.log("Thumbnail pane registered a mouseReleased event");
				Graphics g = ThumbnailPane.getGraphics();
				ThumbnailPane.paint(g); //cheap way of undoing old rectancle...
				
				//now find the thumbnails that touch the rectangle
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
				
				clearSelection();
				Rectangle thumbnailRectangle = new Rectangle();
				SortableDefaultMutableTreeNode n;
				for ( int i = 0; i < thumbnails.length; i++) {
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

		ThumbnailPane.addMouseMotionListener( new MouseInputAdapter() {
			public void mouseDragged( MouseEvent e ) {
				//Tools.log("Thumbnail pane registered a mouseDragged event");
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
				Graphics g = ThumbnailPane.getGraphics();
				ThumbnailPane.paint(g); //cheap way of undoing old rectancle...
				g.drawRect( r.x, r.y, r.width, r.height );
				
				// find out if we need to scroll the window
				Rectangle viewRect = getViewport().getViewRect();
				JScrollBar verticalScrollBar = getVerticalScrollBar();
				final int scrolltrigger = 40; 
				if ( mouseMovedToPoint.y - viewRect.y - viewRect.height > - scrolltrigger ) {
					// Tools.log("must scroll down");
					int increment = verticalScrollBar.getUnitIncrement( 1 );
					int position = verticalScrollBar.getValue();
					if ( position < verticalScrollBar.getMaximum() ) {
						verticalScrollBar.setValue( position + increment );
					}
				} else if ( mouseMovedToPoint.y - viewRect.y < scrolltrigger ) {
					//Tools.log("must scroll up");
					int increment = verticalScrollBar.getUnitIncrement( 1 );
					int position = verticalScrollBar.getValue();
					if ( position > verticalScrollBar.getMinimum() ) {
						verticalScrollBar.setValue( position - increment );
					}
					
				}
				
			}
		} );




			
	} 
 
 

	/**
	 *  creates the arrays for the thumbnails and the descriptions and adds them to the ThubnailPane.
	 */
	public void initThumbnailsArray () {
		//Tools.log("ThumbnailJscrollPane.initThumbnailsArray: invoked.");
		thumbnails = new Thumbnail[ Settings.maxThumbnails ];
		thumbnailDescriptionJPanels = new ThumbnailDescriptionJPanel[ Settings.maxThumbnails ];
		ThumbnailPane.removeAll();
		initialisedMaxThumbnails = Settings.maxThumbnails;
		for ( int i=0;  i < Settings.maxThumbnails; i++ ) {
			thumbnails[i] = new Thumbnail( this );
			thumbnailDescriptionJPanels[i] = new ThumbnailDescriptionJPanel( i, this );
			calculateConstraints( i );
			//Tools.log("ThumbnailJScropplPane.initThumbnailsArray: Adding thumbnail " + Integer.toString(i) + " with constraints: x=" + Integer.toString( thumbnailConstraints.gridx ) + " y=" + Integer.toString( thumbnailConstraints.gridy ) );
			ThumbnailPane.add( thumbnails[i], thumbnailConstraints );
			ThumbnailPane.add( thumbnailDescriptionJPanels[i], descriptionConstraints);
		}
		//Tools.log("ThumbnailJscrollPane.initThumbnailsArray: exit.");
	}



	/**
	 *   calls showGroup but also makes sure the JTree positions itself on the selected group.
	 *   @param showNode 	The GroupNode to be displayed.
	 *   @see #showGroup
	 */
	public void requestShowGroup ( SortableDefaultMutableTreeNode showNode ) {
		showGroup( showNode );
		if ( associatedCollectionJTree != null ) {
			associatedCollectionJTree.setSelectedNode( showNode );
		}
		if ( associatedInfoPanel != null ) {
			associatedInfoPanel.showInfo( showNode );
		}
	}


 	/**
	 *   forces the specified group to be displayed.
	 *   @param showNode 	The GroupNode to be displayed.
	 *   @see #requestShowGroup
	 */
	public void showGroup ( SortableDefaultMutableTreeNode showNode ) {
		if ( ! ( showNode.getUserObject() instanceof GroupInfo ) ) {
			return;
		}
		if ( mySetOfNodes == null ) {
			mySetOfNodes = new GroupBrowser( showNode );
			mySetOfNodes.addRelayoutListener( this );
		} else if ( mySetOfNodes instanceof GroupBrowser ) {
			// we are switching groups; don't change listeners and stuff
			( (GroupBrowser) mySetOfNodes).setNode( showNode );
		} else {
			// must have been a different type of ThumbnailBrowserInterface
			mySetOfNodes.removeRelayoutListener( this );
			mySetOfNodes.cleanup();
			mySetOfNodes = new GroupBrowser( showNode );
			mySetOfNodes.addRelayoutListener( this );
		}
			
		clearSelection();
		getVerticalScrollBar().setValue(0);				 
		startIndex = 0; 
		curPage = 1;
		layoutThumbnailsInThread(); 
	}


 	/**
	 *   forces the specified set of nodes to be displayed
	 *   @param mySetOfNodes 	The Interface with the collection of nodes
	 */
	public void show ( ThumbnailBrowserInterface mySetOfNodes ) {
		this.mySetOfNodes = mySetOfNodes;
		mySetOfNodes.addRelayoutListener( this );
			
		clearSelection();
		getVerticalScrollBar().setValue(0);				 
		startIndex = 0; 
		curPage = 1;
		layoutThumbnailsInThread(); 
	}

 
 
 	/** 
	 *  this method runs through all the thumbnails on the panel and makes sure they are set to the
	 *  correct node. It also sets the tile of the JScrollPane.
	*/ 
	private void layoutThumbnails() { 
		if (mySetOfNodes == null) {
			return;
		}
		
		setTitle( mySetOfNodes.getTitle() );
		
		if ( initialisedMaxThumbnails != Settings.maxThumbnails ) {
			initThumbnailsArray();
		}
		
		int groupCount = mySetOfNodes.getNumberOfNodes();
		
		pageCount = groupCount / Settings.maxThumbnails;
		if (groupCount % Settings.maxThumbnails > 0) {
			pageCount++;
		}
		setPageStats();
		
		setButtonVisibility();
		// take the thumbnails off the creation queue if they were on it.
		// as setNode is now internally synchronised this can slow doen removal 
		// from the queue
		for ( int i=0;  i < Settings.maxThumbnails; i++ ) {
			thumbnails[i].unqueue();
		}

		for ( int i=0;  i < Settings.maxThumbnails; i++ ) {
			SortableDefaultMutableTreeNode newNode = mySetOfNodes.getNode( i + startIndex );
			thumbnails[i].setNode( newNode );
			thumbnailDescriptionJPanels[i].setNode( newNode );
		}
	} 
 


	/**
	 *   This method fires off a Thread that lays out the Thumbnails on the Pane.
	 */
	public void layoutThumbnailsInThread() { 
		killThread(); 
		tl = new ThumbnailLoaderThread( this ); 
	}



 





	/**
	 *   This is a reference to the assoicated CollectionJTree which allows group show requests 
	 *   to be passed between the Thumbnail pane and the JTree.
	 */
	private CollectionJTree associatedCollectionJTree;

	/**
	 *   This is a reference to the assoicated InfoPanel which shows info from selections
	 */
	private InfoPanel associatedInfoPanel;

	

	/**
	 *   sets the assoiciated JTree
	 */
	public void setAssociatedCollectionJTree ( CollectionJTree associatedCollectionJTree ) {
		this.associatedCollectionJTree = associatedCollectionJTree;
	}
	/**
	 *   sets the assoiciated InfoPanel
	 */
	public void setAssociatedInfoPanel ( InfoPanel associatedInfoPanel ) {
		this.associatedInfoPanel= associatedInfoPanel;
	}
	
	/**
	 *   returns the associated JTree
	 */
	public CollectionJTree getAssociatedCollectionJTree() {
		return associatedCollectionJTree;
	}
	/**
	 *   returns the associated InfoPanel
	 */
	public InfoPanel getAssociatedInfoPanel() {
		return associatedInfoPanel;
	}



	






 
 
 
 	/**
	 *  tells how many colums we have on the panel;
	 */
 	public int getCols() {
		return cols;
	}
 
 
 	/**
	 *  sets the number of colums we have on the panel;
	 */
 	public void setCols( int cols ) {
		this.cols = cols;
	}
 

 	/**
	 *  calculates the number of colums we have on the panel;
	 *  @return  true if the number of columns changed, false if not changed
	 */
 	public boolean calculateCols() {
		int width = getViewportBorderBounds().width;
		int newCols = (int) ( width / ( ( Settings.thumbnailSize * thumbnailSizeFactor ) + ( horizontalPadding * 2 ) ));  
		if ( newCols < 1 ) { newCols = 1; } 
		Tools.log("ThumbnailJScrollPane.calculateCols: width: " + Integer.toString( width ) +
			" newCols: " + Integer.toString( newCols ) + " oldCols: " + Integer.toString(getCols()));
		if ( newCols != getCols() ) {
			setCols( newCols );
			return true;
		} else {
			return false;
		}
	}
 

	/**
	 *  this method returns a GridBagContraint object which has the x and y of the 
	 *  component in the panel. This depends on the position and the number of columns
	 */
	private void calculateConstraints( int position ) {
		thumbnailConstraints.gridy = ((int) ( position / cols ) ) * 2;
		descriptionConstraints.gridy = thumbnailConstraints.gridy + 1;
		
		thumbnailConstraints.gridx = position % cols;
		descriptionConstraints.gridx = thumbnailConstraints.gridx;
		
		/*Tools.log( "ThumbnailJScrollPane.calculateConstraints(): position=" 
			+ Integer.toString( position ) 
			+ " cols= "
			+ Integer.toString( cols )
			+ " y= "
			+ Integer.toString( thumbnailConstraints.gridy ) 
			+ " x= "
			+ Integer.toString( thumbnailConstraints.gridx ) );
		*/
	}




  
	/** 
	 *   method that requests the thread to die nicely and waits 300ms for it to complete.
	 *   It is stopped nicely by setting the variable stopThread to true. The loop checks
	 *   this variable every time round and aborts cleanly if this is set. The variable is
	 *   set to false when the method exits.
	 *  
	 */ 
	public void killThread() { 
		if (tl != null) 
			if (tl.isAlive()) { 
				//Tools.log("ThumbnailJScrollPane.killThread: Thread is alive");
				stopThread = true;
				try { 
					tl.join( 500 );  // this might not kill the thread
					if ( tl.isAlive() ) {
						//Tools.log("The join failed. Now forcing an interrupt and destroy on the thread");
						tl.interrupt();
					}
					//Tools.log("ThumbnailJScrollPane.killThread: Thread was joined i.e. the current thread waited for the other to die or waited 5 Sec.");
					//Tools.log("ThumbnailJScrollPane.killThread: Thread was destroyed");
				} catch (InterruptedException x) { 
					//Tools.log("ThumbnailJScrollPane.killThread: InterruptedException: " + x.getMessage() );
					// ignore problems 
				} 
				stopThread = false;  
			} 
	} 
 
 
 
 
	/**  
	 *   changes the title at the top of the page
	 * 
	 * @param	titleString	The text to be printed across the top 
	 *				of all columns. Usually this will be  
	 *				the name of the group 
	 */ 
	public void setTitle(String titleString) { 
		title.setText(titleString); 
	} 
 
 
	/**
	 * Sets the text in the title for displaying page count information
	 */
	private void setPageStats() {												// JA
		lblPage.setText("Page "+curPage+"/"+pageCount);							// JA	
	}																			// JA


	/**
	 *  This method sets whether the previous and next buttons are visible or not
	 */
	public void setButtonVisibility()  {
		if ( startIndex == 0 ) 	
			previousThumbnailsButton.setVisible( false );
		else
			previousThumbnailsButton.setVisible( true );

		int count = mySetOfNodes.getNumberOfNodes();
		lblPage.setVisible(count != 0);
		if ( ( startIndex + Settings.maxThumbnails ) < count )
			nextThumbnailsButton.setVisible( true );
		else
			nextThumbnailsButton.setVisible( false ); 
	}
 




	/**
	 *   This Hash Set hold references to the selected nodes. 
	 */
	public final HashSet selection = new HashSet();

	/**
	 *  This method places the current SDMTN into the selection HashSet.
	 */
	public void setSelected( SortableDefaultMutableTreeNode node ) {
		selection.add( node );
		Object userObject = node.getUserObject();
		if ( userObject instanceof PictureInfo ) {
			((PictureInfo) userObject).sendWasSelectedEvent();
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
			userObject = ((SortableDefaultMutableTreeNode) o).getUserObject(); 
			if ( userObject instanceof PictureInfo ) {
				((PictureInfo) userObject).sendWasUnselectedEvent();
				
			}
		}
	}


	/**
	 *  This method removes the current SDMTN from the selection HashSet.
	 */
	public void removeFromSelection( SortableDefaultMutableTreeNode node ) {
		selection.remove( node );
		Object userObject = node.getUserObject();
		if ( userObject instanceof PictureInfo ) {
			((PictureInfo) userObject).sendWasUnselectedEvent();
		}
	}


	/**
	 *  This returns whether the SDMTN is part of the selection HashSet.
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
	 */
	public Object [] getSelectedNodes() {
		return selection.toArray();
	}


	/**
	 *  returns a Vector of the selected nodes.
	 */
	public Vector getSelectedNodesAsVector() {
		return new Vector( selection );
	}


	/**
	 *  returns the amount of selected nodes
	 */
	public int countSelectedNodes() {
		return selection.size();
	}




	/**
	 *  This object creates a thread which calls the methods in the ThumbnailJScrollPane object.
	 *  It's purpose is to get the loading of the thumbnails out of the main application
	 *  thread because these slow processes block the screen from refreshing and freeze the
	 *  GUI.<p>
	 * 
	 *  The thread is created with an action variable which tells the thread which method to call.
	 *  This was done so as not to have more than one type of thread which would need to be checked 
	 *  and killed.
	 *
	 */
	public class ThumbnailLoaderThread extends Thread {

		/**
		 * reference to the calling object
		 */
		ThumbnailJScrollPane caller;
	
		/**
		 *  Constructor for the thread that loads thumbnails
		 *  @param caller 	a handle back to the calling object to notify of 
		 *   		  	status changes.
		 */
		public ThumbnailLoaderThread ( ThumbnailJScrollPane caller ) {
			this.caller = caller;
			caller.stopThread = false;
			start();
		}
	
		/**
		 *  we call back the method defined in the calling object in our new thread.
		 *  The title of the Thumbnail pane is made grey and when the job is done it is 
		 *  made black.
		 */
		public void run() {
			caller.title.setForeground( Color.gray );
			caller.layoutThumbnails();
			caller.title.setForeground( Color.black );
		}
	}  // end of inner class ThumbnailLoaderThread


} 
 
 
 
 
 
 
 
 
