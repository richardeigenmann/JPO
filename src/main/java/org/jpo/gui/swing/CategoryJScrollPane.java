package org.jpo.gui.swing;

import org.jpo.dataModel.Category;
import org.jpo.dataModel.Settings;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

/*
 CategoryJScrollPane.java:  creates a JPanel in a JScrollPane that lists categories

 Copyright (C) 2006-2015  Richard Eigenmann.
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
 * CategoryJScrollPane.java: Creates a JPanel in a JScrollPane that lists
 * categories
 *
 * @author Richard Eigenmann
 */
public class CategoryJScrollPane extends JScrollPane implements ListSelectionListener {

    private final DefaultListModel<Category> defaultListModel = new DefaultListModel<>();

    private final JList<Category> categoriesJList = new JList<>( defaultListModel );

    /**
     * Creates a JPanel that lists the categories
     *
     *
     */
    public CategoryJScrollPane() {
        initComponents();
    }

    private void initComponents() {
        categoriesJList.setPreferredSize( new Dimension( 180, 250 ) );
        categoriesJList.setMinimumSize( new Dimension( 180, 50 ) );
        categoriesJList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        categoriesJList.setCellRenderer( new CategoryListCellRenderer() );
        categoriesJList.addListSelectionListener( this );

        this.setViewportView( categoriesJList );
        this.setPreferredSize( new Dimension( 200, 270 ) );
        this.setMinimumSize( new Dimension( 200, 50 ) );

    }

    /**
     * Returns the List Model used for this list
     *
     * @return the List Model used for this list
     */
    public DefaultListModel<Category> getDefaultListModel() {
        return defaultListModel;
    }

    /**
     * Returns the JList used for this list
     *
     * @return the JList
     */
    public JList<Category> getJList() {
        return categoriesJList;
    }

    /**
     * Load Categories
     * @param i the categories
     */
    public void loadCategories(final Iterator i) {
        // load categories
        defaultListModel.clear();
        //Iterator i = Settings.getPictureCollection().getCategoryIterator();
        Integer key;
        String category;
        Category categoryObject;
        while ( i.hasNext() ) {
            key = (Integer) i.next();
            category = Settings.getPictureCollection().getCategory( key );
            categoryObject = new Category( key, category );
            defaultListModel.addElement( categoryObject );
        }
    }

    /**
     * Method from the ListSelectionListener implementation that tracks when an
     * element was selected.
     *
     * @param e the list selection event
     */
    @Override
    public void valueChanged( ListSelectionEvent e ) {
        if ( e.getValueIsAdjusting() ) {
            return;
        }
        JList theList = (JList) e.getSource();
        if ( !theList.isSelectionEmpty() ) {
            int index = theList.getSelectedIndex();
            Category cat = (Category) theList.getModel().getElementAt( index );
            int status = cat.getStatus();
            if ( status == Category.UNDEFINED ) {
                cat.setStatus( Category.SELECTED );
            } else if ( status == Category.SELECTED ) {
                cat.setStatus( Category.UN_SELECTED );
            } else if ( status == Category.UN_SELECTED ) {
                cat.setStatus( Category.SELECTED );
            } else if ( status == Category.BOTH ) {
                cat.setStatus( Category.SELECTED );
            }
            theList.clearSelection();
            categoriesJList.validate();
        }
    }

    /**
     * Returns a HashSet of the selected Categories
     *
     * @return the hash set of the selected categories
     */
    public HashSet<Object> getSelectedCategories() {
        HashSet<Object> selectedCategories = new HashSet<>();
        Category c;
        int status;
        Enumeration e = defaultListModel.elements();
        while ( e.hasMoreElements() ) {
            c = (Category) e.nextElement();
            status = c.getStatus();
            if ( status == Category.SELECTED ) {
                selectedCategories.add( c.getKey() );
            }
        }
        return selectedCategories;
    }

}
