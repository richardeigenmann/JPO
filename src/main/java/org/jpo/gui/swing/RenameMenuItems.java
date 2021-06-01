package org.jpo.gui.swing;

import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.datamodel.Tools;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.RenameFileRequest;
import org.jpo.eventbus.RenamePictureRequest;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static java.util.Objects.isNull;

/*
 Copyright (C) 2020 - 2021  Richard Eigenmann.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or any later version. This program is distributed
 in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 Without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 more details. You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 The license is in gpl.txt.
 See http://www.gnu.org/copyleft/gpl.html for the details.
 */


/**
 * static class to create a collection of Rename menu items.
 */
public class RenameMenuItems {

    private RenameMenuItems() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Returns a collection of rename menu items for various popup menus to fix filenames.
     * Will only suggest to fix underscores and space characters if the list contains only a single item.
     *
     * @param popupNodes The list of nodes to rename
     * @return a list of items to add to a JMenu or JPopUpMenu
     */
    public static Iterable<JComponent> getRenameMenuItems(final Collection<SortableDefaultMutableTreeNode> popupNodes) {
        final Collection<JComponent> menuItems = new ArrayList<>();

        final var fileRenameJMenuItem = new JMenuItem(String.format(Settings.getJpoResources().getString("fileRenameJMenuItem"), popupNodes.size()));
        fileRenameJMenuItem.addActionListener((ActionEvent e) ->
                JpoEventBus.getInstance().post(new RenamePictureRequest(popupNodes))
        );
        menuItems.add(fileRenameJMenuItem);

        if (popupNodes.size() == 1) {
            final SortableDefaultMutableTreeNode node = popupNodes.iterator().next();
            final PictureInfo pi = (PictureInfo) node.getUserObject();

            if (!isNull(pi.getImageFile())) {
                // add a menu item to replace if we can find %20 strings in the filename
                final Optional<String> potentialNewFilename = PicturePopupMenu.replaceEscapedSpaces(pi.getImageFile().getName());
                if (potentialNewFilename.isPresent()) {
                    final var suggestedFileName = Tools.inventFilename(pi.getImageFile().getParentFile(), potentialNewFilename.get());
                    final var renameSpaceJMenuItem = new JMenuItem("To: " + suggestedFileName.getName());
                    renameSpaceJMenuItem.addActionListener(e -> JpoEventBus.getInstance().post(new RenameFileRequest(node, suggestedFileName.getName())));
                    menuItems.add(renameSpaceJMenuItem);
                }

                // add a menu item to replace if we can find %20 strings in the filename
                final Optional<String> potentialNewFilename2 = PicturePopupMenu.replace2520(pi.getImageFile().getName());
                if (potentialNewFilename2.isPresent()) {
                    final var suggestedFileName2 = Tools.inventFilename(pi.getImageFile().getParentFile(), potentialNewFilename2.get());
                    final var renameSpaceJMenuItem2 = new JMenuItem("To: " + suggestedFileName2.getName());
                    renameSpaceJMenuItem2.addActionListener(e -> JpoEventBus.getInstance().post(new RenameFileRequest(node, suggestedFileName2.getName())));
                    menuItems.add(renameSpaceJMenuItem2);
                }

                final Optional<String> potentialNewFilenameWithoutUndescores = PicturePopupMenu.replaceUnderscore(pi.getImageFile().getName());
                if (potentialNewFilenameWithoutUndescores.isPresent()) {
                    final var suggestedFileName3 = Tools.inventFilename(pi.getImageFile().getParentFile(), potentialNewFilenameWithoutUndescores.get());
                    final var renameUnderscoreJMenuItem = new JMenuItem("To: " + suggestedFileName3.getName());
                    renameUnderscoreJMenuItem.addActionListener(e -> JpoEventBus.getInstance().post(new RenameFileRequest(node, suggestedFileName3.getName())));
                    menuItems.add(renameUnderscoreJMenuItem);
                }
            }
        }

        return menuItems;
    }
}
