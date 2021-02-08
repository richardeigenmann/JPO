package org.jpo.gui.swing;

import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.AddCategoriesToPictureNodesRequest;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.OpenCategoryEditorRequest;

import javax.swing.*;
import java.util.List;

/*
 Copyright (C) 2002-2021  Richard Eigenmann, Zürich, Switzerland
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
 * Creates a JPopupMenu with entries for all the categories
 */
public class CategoryPopupMenu extends JPopupMenu {
    public CategoryPopupMenu(final List<SortableDefaultMutableTreeNode> referringNodes) {
        super(Settings.getJpoResources().getString("CategoryPopupMenu"));
        CategoryPopupMenu.addMenuItems(this, referringNodes);
    }

    /**
     * This method adds a bunch of JMenuItems to the supplied parent menu.
     * The input parameter parentMenu is modified because I need to call add several times.
     * I can't send back a collection of JMenuItems that wil then be added to the parent
     * JPopupMenu or JMenu.
     *
     * @param parentMenu     the JPopupMenu or JMenu to add the JMenuItems to
     * @param referringNodes The nodes on which the selection is to be applied
     */
    public static void addMenuItems(final JComponent parentMenu, final List<SortableDefaultMutableTreeNode> referringNodes) {
        final JMenuItem addCategoryMenuItem = new JMenuItem("Add Category");
        addCategoryMenuItem.addActionListener(e -> JpoEventBus.getInstance().post(new OpenCategoryEditorRequest()));
        parentMenu.add(addCategoryMenuItem);
        final PictureCollection pictureCollection = Settings.getPictureCollection();
        pictureCollection.getSortedCategoryStream().forEach(category -> {
            final String categoryDescription = category.getValue();
            final JMenuItem categoryMenuItem = new JMenuItem();
            categoryMenuItem.addActionListener(e -> JpoEventBus.getInstance().post(new AddCategoriesToPictureNodesRequest(category.getKey(), referringNodes)));
            categoryMenuItem.setText(categoryDescription);
            parentMenu.add(categoryMenuItem);
        });

    }

}
