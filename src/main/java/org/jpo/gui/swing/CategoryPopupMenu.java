package org.jpo.gui.swing;

import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.OpenCategoryEditorRequest;

import javax.swing.*;
import java.util.List;

public class CategoryPopupMenu extends JPopupMenu {
    public CategoryPopupMenu(final List<SortableDefaultMutableTreeNode> referringNodes) {
        super("Add a Category to the Picture");
        CategoryPopupMenu.addMenuItems(this, referringNodes);
    }

    /**
     * This method adds a bunch of JMenuItems to the supplied parent menu.
     * I'm using the C-Style modify input parameter because I need to call add several times.
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
        pictureCollection.getCategoryKeySet().forEach(category -> {
            final String categoryDescription = pictureCollection.getCategory(category);
            final JMenuItem categoryMenuItem = new JMenuItem();
            categoryMenuItem.addActionListener(e -> {
                for (final SortableDefaultMutableTreeNode node : referringNodes) {
                    if (node.getUserObject() instanceof PictureInfo pi) {
                        pi.addCategoryAssignment(category);
                    }
                }
            });
            categoryMenuItem.setText(categoryDescription);
            parentMenu.add(categoryMenuItem);
        });

    }

}
