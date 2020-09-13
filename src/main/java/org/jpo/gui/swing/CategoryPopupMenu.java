package org.jpo.gui.swing;

import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;

import javax.swing.*;

public class CategoryPopupMenu extends JPopupMenu {
    public CategoryPopupMenu(final SortableDefaultMutableTreeNode refferingNode) {
        super("Add a Category to the Picture");
        final JMenuItem addCategoryMenuItem = new JMenuItem("Add Category");
        this.add(addCategoryMenuItem);
        final PictureCollection pictureCollection = Settings.getPictureCollection();
        pictureCollection.getCategoryKeySet().forEach(category -> {
            final String categoryDescription = pictureCollection.getCategory(category);
            final JMenuItem categoryMenuItem = new JMenuItem();
            categoryMenuItem.addActionListener(e -> ((PictureInfo) refferingNode.getUserObject()).addCategoryAssignment(category)
            );
            categoryMenuItem.setText(categoryDescription);
            this.add(categoryMenuItem);
        });
    }

}
