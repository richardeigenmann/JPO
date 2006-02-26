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
CategoryJScrollPane.java:  creates a JPanel in a JScrollPane that lists categories

Copyright (C) 2006  Richard Eigenmann.
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
 * CategoryJScrollPane.java:  Creates a JPanel in a JScrollPane that lists categories
 *
 **/
public class CategoryJScrollPane extends JScrollPane implements ListSelectionListener {


	private final DefaultListModel defaultListModel = new DefaultListModel();

	private	final JList categoriesJList = new JList( defaultListModel );


	/**
	 *  Creates a JPanel that lists the categories
	 *
	 **/
	public CategoryJScrollPane() {

		categoriesJList.setPreferredSize( new Dimension( 180, 250) );
		categoriesJList.setMinimumSize( new Dimension( 180, 50) );
		categoriesJList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		categoriesJList.setCellRenderer( new CategoryListCellRenderer() );
		categoriesJList.addListSelectionListener( this );

		this.setViewportView( categoriesJList );
		this.setPreferredSize( new Dimension( 200, 270) );
		this.setMinimumSize( new Dimension( 200, 50) );
	}


	/**
	 *  Returns the List Model used for this list
	 **/
	public DefaultListModel getDefaultListModel() {
		return defaultListModel;
	}
	

	/**
	 *  Returns the JList used for this list
	 **/
	public JList getJList() {
		return categoriesJList;
	}


	/**
	 *  Load Categories
	 */
	public void loadCategories() {
		// load categories
		defaultListModel.clear();
		Iterator i = Settings.top.getCategoryIterator();
		Integer key;
		String category;
		Category categoryObject;
		while ( i.hasNext() ) {
			key = (Integer) i.next();
			category = (String) Settings.top.getCategory( key );
			categoryObject = new Category( key, category );
			defaultListModel.addElement( categoryObject );
		}
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
	 *  Returns a HashSet of the selected Categories
	 */
	public HashSet getSelectedCategories() {
		HashSet selectedCategories = new HashSet();
		Category c; int status;
		Enumeration e = defaultListModel.elements();
		while ( e.hasMoreElements() ) {
			c = (Category) e.nextElement();
			status = c.getStatus();
			if ( status == Category.selected ) {
				selectedCategories.add( c.getKey() );
			}
		}
		return selectedCategories;
	}


	
}
