package jpo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.Iterator;
import java.util.*;

/*
CategoryUsageJFrame.java:  Creates a Window in which the categories are shown

Copyright (C) 2002  Richard Eigenmann.
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
 * CategoryUsageJFrame.java:  Creates a GUI that shows the Categories that are defined. It
 *  visually shows which categories are applied to a selection of images. If updates are allowed
 *  it allows to update the pictures with the Categories being clicked.
 *
 **/
public class CategoryUsageJFrame extends JFrame implements ListSelectionListener {


	/**
	 *  the entry field that allows a new category to be added
	 */
	private JTextField categoryJTextField = new JTextField();
	

	private final DefaultListModel listModel = new DefaultListModel();

	private	final JList categoriesJList = new JList( listModel );

	private Vector selectedNodes = null;

	final JLabel numberOfPicturesJLabel = new JLabel("");

	/**
	 *  Creates a GUI to edit the categories of the collection
	 *
	 **/
	public CategoryUsageJFrame() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
				getRid();
			}
	        });  
		
		setTitle ( Settings.jpoResources.getString("CategoryUsageJFrameTitle") );

		final JPanel jPanel = new JPanel();
		jPanel.setBorder( BorderFactory.createEmptyBorder( 8,8,8,8 ) );
		jPanel.setLayout( new GridBagLayout() );
		
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx= 0;
		c.gridy= 0;

		c.weightx = 0.1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.PAGE_START;
		c.insets = new Insets( 0,0,3,5 );
		c.fill = GridBagConstraints.HORIZONTAL;


		final Dimension defaultButtonSize = new Dimension( 150, 25);
		final Dimension maxButtonSize = new Dimension( 150, 25);


		categoriesJList.setPreferredSize( new Dimension( 180, 250) );
		categoriesJList.setMinimumSize( new Dimension( 180, 50) );
		categoriesJList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		categoriesJList.addListSelectionListener( this );
		categoriesJList.setCellRenderer( new CategoryListCellRenderer() );


		final JScrollPane listJScrollPane = new JScrollPane( categoriesJList );
		listJScrollPane.setPreferredSize( new Dimension( 200, 270) );
		listJScrollPane.setMinimumSize( new Dimension( 200, 50) );
		c.gridx++;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 0.6;
		c.weighty = 0.6;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets( 0,0,0,0 );
		jPanel.add( listJScrollPane, c );

	
		final JPanel buttonJPanel = new JPanel();
		buttonJPanel.setLayout( new GridBagLayout() );
		GridBagConstraints bc = new GridBagConstraints();
		bc.gridx= 0;
		bc.gridy= 0;
		bc.fill = GridBagConstraints.NONE;

		numberOfPicturesJLabel.setHorizontalAlignment( JLabel.LEFT );
		buttonJPanel.add( numberOfPicturesJLabel, bc );

		final JButton modifyCateogryJButton = new JButton( Settings.jpoResources.getString( "modifyCateogryJButton" ) );
		modifyCateogryJButton.setPreferredSize( defaultButtonSize );
		modifyCateogryJButton.setMinimumSize( defaultButtonSize );
		modifyCateogryJButton.setMaximumSize( maxButtonSize );
		modifyCateogryJButton.addActionListener( new ActionListener() {
			public void actionPerformed (ActionEvent evt ) {
				new CategoryEditorJFrame();
			}
		} );
		bc.gridy++;
		buttonJPanel.add( modifyCateogryJButton, bc );


		final JButton refreshJButton = new JButton( Settings.jpoResources.getString( "refreshJButtonCUJF" ) );
		refreshJButton.setPreferredSize( defaultButtonSize );
		refreshJButton.setMinimumSize( defaultButtonSize );
		refreshJButton.setMaximumSize( maxButtonSize );
		refreshJButton.addActionListener( new ActionListener() {
			public void actionPerformed (ActionEvent evt ) {
				updateCategories();
			}
		} );
		bc.gridy++;
		buttonJPanel.add( refreshJButton, bc );



		final JButton updateJButton = new JButton( Settings.jpoResources.getString( "updateJButton" ) );
		updateJButton.setPreferredSize( defaultButtonSize );
		updateJButton.setMinimumSize( defaultButtonSize );
		updateJButton.setMaximumSize( maxButtonSize );
		updateJButton.addActionListener( new ActionListener() {
			public void actionPerformed (ActionEvent evt ) {
				storeSelection();
				getRid();
			}
		} );
		bc.gridy++;
		buttonJPanel.add( updateJButton, bc );



		final JButton cancelJButton = new JButton( Settings.jpoResources.getString( "cancelJButton" ) );
		cancelJButton.setPreferredSize( defaultButtonSize );
		cancelJButton.setMinimumSize( defaultButtonSize );
		cancelJButton.setMaximumSize( maxButtonSize );
		cancelJButton.addActionListener( new ActionListener() {
			public void actionPerformed (ActionEvent evt ) {
				getRid();
			}
		} );
		bc.gridy++;
		buttonJPanel.add( cancelJButton, bc );


		c.gridx++;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.weightx = 0.1;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		jPanel.add( buttonJPanel, c );

		getContentPane().add( jPanel, BorderLayout.CENTER );
			
	 	//  As per http://java.sun.com/developer/JDCTechTips/2003/tt1208.html#1
		Runnable runner = new FrameShower( this, Settings.anchorFrame );
        	EventQueue.invokeLater( runner );
	}


	/**
	 *  method that closes te frame and gets rid of it
	 */
	private void getRid() {
		setVisible ( false );
		dispose ();
	}


	/**
	 *  Method from the ListSelectionListener implementation that tracks when an 
	 *  element was selected.
	 */
	public void valueChanged( ListSelectionEvent e ) {
		if (e.getValueIsAdjusting())
			return;
		JList theList = (JList)e.getSource();
    		if ( ! theList.isSelectionEmpty() ) {
			int index = theList.getSelectedIndex();
			Category cat = (Category) theList.getModel().getElementAt( index );
			int status = cat.getStatus();
			if ( status == Category.undefined ) {
				cat.setStatus( Category.selected );
			} else if ( status == Category.selected ) {
				cat.setStatus( Category.unSelected );
			} else if ( status == Category.unSelected ) {
				cat.setStatus( Category.selected );
			} else if ( status == Category.both ) {
				cat.setStatus( Category.selected );
			}
			theList.clearSelection();
			categoriesJList.validate();
		}
	}
	


	/**
	 *  This method receives the selection the Category Editor is working on
	 */
	public void setSelection( Vector nodes ) {
		selectedNodes = nodes;
		updateCategories();
	}
	

	/**
	 *  This method receives the selection the Category Editor is to work on. Here we 
	 *  can pass a Group node and a flag whether the nodes are to be recursively searched for the pictures.
	 *  @param  groupNode    The node from which to add the pictures
	 *  @param  recurse	A flag whether to recurse the search into sub groups
	 */
	public void setGroupSelection( SortableDefaultMutableTreeNode groupNode, boolean recurse ) {
		selectedNodes = new Vector();
		SortableDefaultMutableTreeNode n;
		Enumeration nodes = groupNode.children();
		while ( nodes.hasMoreElements() ) {
			n = (SortableDefaultMutableTreeNode) nodes.nextElement();
			if ( n.getUserObject() instanceof PictureInfo) {
				selectedNodes.add( n );
			} else if ( ( n.getUserObject() instanceof GroupInfo) && recurse ) {
				Tools.log( "recurse not currently implemented" );
			}
		}
		updateCategories();
	}

	
	/**
	 *  This method reads the nodes and sets the categories accordingly
	 */
	public void updateCategories() {
		// load categories
		listModel.clear();
		Iterator i = Settings.top.getCategoryIterator();
		Integer key;
		String category;
		Category categoryObject;
		while ( i.hasNext() ) {
			key = (Integer) i.next();
			category = (String) Settings.top.getCategory( key );
			categoryObject = new Category( key, category );
			listModel.addElement( categoryObject );
		}


		if ( selectedNodes == null ) {
			Tools.log("selectedNodes is null!");
			return;
		}
		//Tools.log("Reading categories...");
		numberOfPicturesJLabel.setText( Integer.toString( selectedNodes.size() ) 
			+ Settings.jpoResources.getString("numberOfPicturesJLabel") );
		
		// zero out the categories
		Category c;
		Enumeration categoryEnumeration = listModel.elements();
		while ( categoryEnumeration.hasMoreElements() ) {
			c = (Category) categoryEnumeration.nextElement();
			Tools.log("Setting Status to undefined on Category: " + c.getKey().toString() + " " + c.toString() );
			c.setStatus( Category.undefined );
			// force screen update:
			listModel.setElementAt( c, listModel.indexOf( c ) ); 
		}
		
		Object[] pictureCategories;
		int pictureCategoryKey;
		int currentStatus;
		boolean found;
		PictureInfo pi;
		Enumeration pictureNodes;
		Object myObject;
		
		// loop through each category on the list and check we have a node that 
		categoryEnumeration = listModel.elements();
		while ( categoryEnumeration.hasMoreElements() ) {
			c = (Category) categoryEnumeration.nextElement();
			Tools.log("Checking Category: " + c.getKey().toString() + " " + c.toString() );

			pictureNodes = selectedNodes.elements();
			while ( pictureNodes.hasMoreElements() ) {
				myObject = ((SortableDefaultMutableTreeNode) pictureNodes.nextElement()).getUserObject();
				if ( myObject instanceof PictureInfo ) {
					pi = (PictureInfo) myObject;
					if ( pi.containsCategory( c.getKey() ) ) {
						currentStatus = c.getStatus();
						Tools.log("Status of category is: " + Integer.toString( currentStatus ) );
						if ( currentStatus == Category.undefined ) {
							c.setStatus( Category.selected );
							// force screen update:
							listModel.setElementAt( c, listModel.indexOf( c ) ); 
						} else if ( currentStatus == Category.unSelected ) {
							c.setStatus( Category.both );
							// force screen update:
							listModel.setElementAt( c, listModel.indexOf( c ) ); 
						}
						// ignore status both and selected as we would only be adding to that
					} else {
						// we get here if there was no category match
						currentStatus = c.getStatus();
						Tools.log("Status of category is: " + Integer.toString( currentStatus ) );
						if ( currentStatus == Category.undefined ) {
							c.setStatus( Category.unSelected );
							// force screen update:
							listModel.setElementAt( c, listModel.indexOf( c ) ); 
						} else if ( currentStatus == Category.selected ) {
							c.setStatus( Category.both );
							// force screen update:
							listModel.setElementAt( c, listModel.indexOf( c ) ); 
						}
						// ignore status unselected and both as nothing would change
					}
				}
			}
		}
	}


	/**
	 *  This method updates the selected pictures with the new category classification.
	 */
	public void storeSelection() {
		int status;
		Category c; 
		Enumeration e;
		
		// build a vector of the selected categories
		HashSet selectedCategories = new HashSet();
		e = listModel.elements();
		while ( e.hasMoreElements() ) {
			c = (Category) e.nextElement();
			status = c.getStatus();
			if ( status == Category.selected ) {
				selectedCategories.add( c.getKey() );
			}
		}
		
		// send the selected categories to listeners such as the AddFromCamera screen
		e = categoryGuiListeners.elements();
		while ( e.hasMoreElements() ) {
			((CategoryGuiListenerInterface) e.nextElement() ).categoriesChosen( selectedCategories );
		}

		
		// update the selected pictures
		if ( selectedNodes == null ) { Tools.log ("CategoryUsageJFrame.storeSelection: called with a null selection. Aborting."); return; }
		PictureInfo pi;
		Object o;
		Enumeration pictureNodes = selectedNodes.elements();
		while ( pictureNodes.hasMoreElements() ) {
			o = ((SortableDefaultMutableTreeNode) pictureNodes.nextElement()).getUserObject();
			if ( o instanceof PictureInfo ) {
				pi = (PictureInfo) o;
				e = listModel.elements();
				while ( e.hasMoreElements() ) {
					c = (Category) e.nextElement();
					status = c.getStatus();
					if ( status == Category.selected ) {
						pi.addCategoryAssignment( c.getKey() );
					} else if ( status == Category.unSelected ){
						pi.removeCategory( c.getKey() );
					}
				}
			}
		}
	}


	/**
	 *  This Vector hold references to categoryGuiListeners
	 */
	protected Vector categoryGuiListeners = new Vector();



	/**
	 *  This method registers the categoryGuiListener
	 */
	public void addCategoryGuiListener ( CategoryGuiListenerInterface listener) {
		categoryGuiListeners.add( listener );
	}


	/**
	 *  This method deregisters the categoryGuiListener
	 */
	public void removeCategoryGuiListener ( CategoryGuiListenerInterface listener ) {
		categoryGuiListeners.remove( listener );
	}

	

}
