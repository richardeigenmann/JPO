package org.jpo.gui.swing;

import org.jpo.datamodel.Category;
import org.jpo.gui.Settings;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

/*
 Copyright (C) 2006-2025 Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed 
 in the hope that it will be useful, but WITHOUT ANY WARRANTY,
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
public class CategoryJScrollPane extends JScrollPane {

    private final DefaultListModel<Category> defaultListModel = new DefaultListModel<>();

    private final JList<Category> categoriesJList = new JList<>(defaultListModel);

    /**
     * Creates a JPanel that lists the categories
     */
    public CategoryJScrollPane() {
        initComponents();
    }

    private void initComponents() {
        categoriesJList.setPreferredSize(new Dimension(180, 250));
        categoriesJList.setMinimumSize(new Dimension(180, 50));
        categoriesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoriesJList.setCellRenderer(new CategoryListCellRenderer());
        categoriesJList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            if (!categoriesJList.isSelectionEmpty()) {
                int index = categoriesJList.getSelectedIndex();
                final Category cat = categoriesJList.getModel().getElementAt(index);
                int status = cat.getStatus();
                if (status == Category.UNDEFINED) {
                    cat.setStatus(Category.SELECTED);
                } else if (status == Category.SELECTED) {
                    cat.setStatus(Category.UN_SELECTED);
                } else if (status == Category.UN_SELECTED) {
                    cat.setStatus(Category.SELECTED);
                } else if (status == Category.BOTH) {
                    cat.setStatus(Category.SELECTED);
                }
                categoriesJList.clearSelection();
                categoriesJList.validate();
            }

        });

        this.setViewportView(categoriesJList);
        this.setPreferredSize(new Dimension(200, 270));
        this.setMinimumSize(new Dimension(200, 50));

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
     *
     * @param i the categories
     */
    public void loadCategories(final Iterator<Integer> i) {
        // load categories
        defaultListModel.clear();
        while (i.hasNext()) {
            final Integer key = i.next();
            final String category = Settings.getPictureCollection().getCategory(key);
            final Category categoryObject = new Category(key, category);
            defaultListModel.addElement(categoryObject);
        }
    }


    /**
     * Returns a Collection of the selected Categories
     *
     * @return the hash set of the selected categories
     */
    public Collection<Integer> getSelectedCategories() {
        final HashSet<Integer> selectedCategories = new HashSet<>();
        final Enumeration<Category> e = defaultListModel.elements();
        while (e.hasMoreElements()) {
            final Category category = e.nextElement();
            if (category.getStatus() == Category.SELECTED) {
                selectedCategories.add(category.getKey());
            }
        }
        return selectedCategories;
    }

}
