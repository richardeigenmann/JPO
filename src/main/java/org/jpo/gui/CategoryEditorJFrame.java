package org.jpo.gui;

import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

/*
 Copyright (C) 2002-2024 Richard Eigenmann.
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
 * CategoryEditorJFrame.java: Creates a GUI to edit the categories of the
 * collection
 */
public class CategoryEditorJFrame
        extends JFrame {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(CategoryEditorJFrame.class.getName());

    /**
     * the entry field that allows a new category to be added
     */
    private final JTextField categoryJTextField = new JTextField();

    private final DefaultListModel<Category> categoriesListModel = new DefaultListModel<>();
    private static final Dimension MAX_BUTTON_SIZE = new Dimension(150, 25);
    private static final Dimension DEFAULT_BUTTON_SIZE = new Dimension(150, 25);

    private final transient PictureCollection pictureCollection;

    /**
     * Creates a GUI to edit the categories of the collection
     */
    public CategoryEditorJFrame(final PictureCollection pictureCollection) {
        this.pictureCollection = pictureCollection;
        setTitle(Settings.getJpoResources().getString("CategoryEditorJFrameTitle"));
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                getRid();
            }
        });

        final var jPanel = new JPanel();
        jPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        jPanel.setLayout(new MigLayout(""));

        final var categoriesJList = getCategoriesJList();

        final var listJScrollPane = new JScrollPane(categoriesJList);
        listJScrollPane.setPreferredSize(new Dimension(250, 370));
        listJScrollPane.setMinimumSize(new Dimension(200, 50));
        jPanel.add(listJScrollPane, "push, grow");


        final var rightColumn = new JPanel();
        rightColumn.setLayout(new MigLayout());

        final var categoryJLabel = new JLabel(Settings.getJpoResources().getString("categoryJLabel"));
        categoryJLabel.setHorizontalAlignment(SwingConstants.LEFT);
        rightColumn.add(categoryJLabel, "wrap");

        categoryJTextField.setPreferredSize(new Dimension(200, 25));
        categoryJTextField.setMinimumSize(new Dimension(200, 25));
        categoryJTextField.setMaximumSize(new Dimension(600, 25));
        rightColumn.add(categoryJTextField, "wrap");

        final var addCategoryJButton = getAddCategoryJButton();
        rightColumn.add(addCategoryJButton, "wrap");
        final var deleteCategoryJButton = getDeleteCategoryJButton(categoriesJList);
        rightColumn.add(deleteCategoryJButton, "wrap");
        final var renameCategoryJButton = getRenameCategoryJButton(categoriesJList);
        rightColumn.add(renameCategoryJButton, "wrap");
        final var doneJButton = getDoneJButton();
        rightColumn.add(doneJButton, "wrap");

        jPanel.add(rightColumn, "aligny top, wrap");

        getContentPane().add(jPanel);
        pack();
        setLocationRelativeTo(Settings.getAnchorFrame());
        setVisible(true);
    }


    @NotNull
    private JButton getRenameCategoryJButton(final JList<Category> categoriesJList) {
        final var renameCategoryJButton = new JButton(Settings.getJpoResources().getString("renameCategoryJButton"));
        renameCategoryJButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
        renameCategoryJButton.setMinimumSize(DEFAULT_BUTTON_SIZE);
        renameCategoryJButton.setMaximumSize(MAX_BUTTON_SIZE);
        renameCategoryJButton.addActionListener((ActionEvent evt) -> {
            LOGGER.info("I want to rename the selected category ");
            int index = categoriesJList.getSelectedIndex();
            if (index < 0) {
                return; // nothing selected
            } // nothing selected
            final var selectedCategory = categoriesJList.getModel().getElementAt(index);
            categoriesListModel.remove(index);
            final var renamedCategory = categoryJTextField.getText();
            pictureCollection.renameCategory(selectedCategory.getKey(), renamedCategory);
            final Category newCategoryObject = new Category(selectedCategory.getKey(), renamedCategory);
            categoriesListModel.insertElementAt(newCategoryObject, index);
            categoryJTextField.setText("");
        });
        return renameCategoryJButton;
    }

    @NotNull
    private JButton getDeleteCategoryJButton(final JList<Category> categoriesJList) {
        final var deleteCategoryJButton = new JButton(Settings.getJpoResources().getString("deleteCategoryJButton"));
        deleteCategoryJButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
        deleteCategoryJButton.setMinimumSize(DEFAULT_BUTTON_SIZE);
        deleteCategoryJButton.setMaximumSize(MAX_BUTTON_SIZE);
        deleteCategoryJButton.addActionListener((ActionEvent evt) -> {
            var index = categoriesJList.getSelectedIndex();
            if (index < 0) {
                return; // nothing selected
            } // nothing selected
            final Category cat = categoriesJList.getModel().getElementAt(index);
            int count = countCategoryUsage(cat.getKey(), pictureCollection.getRootNode());
            if (count > 0) {
                int answer = JOptionPane.showConfirmDialog(CategoryEditorJFrame.this,
                        Settings.getJpoResources().getString("countCategoryUsageWarning1") + count + Settings.getJpoResources().getString("countCategoryUsageWarning2"),
                        Settings.getJpoResources().getString("genericWarning"),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.CANCEL_OPTION) {
                    return;
                } else {
                    pictureCollection.removeCategoryUsage(cat.getKey(), pictureCollection.getRootNode());
                }

            }
            categoriesListModel.remove(index);
            pictureCollection.removeCategory(cat.getKey());
        });
        return deleteCategoryJButton;
    }

    /**
     * Counts the number of nodes using the category
     *
     * @param key       The Key
     * @param startNode the node to start from
     * @return the number of nodes
     */
    private static int countCategoryUsage(final Integer key,
                                          final SortableDefaultMutableTreeNode startNode) {
        final var nodes = startNode.children();
        var count = 0;
        while (nodes.hasMoreElements()) {
            final var node = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if (node.getUserObject() instanceof PictureInfo pictureInfo
                    && pictureInfo.containsCategory(key)) {
                count++;
            }
            if (node.getChildCount() > 0) {
                count += countCategoryUsage(key, node);
            }
        }
        return count;
    }

    @NotNull
    private JButton getAddCategoryJButton() {
        final var addCategoryJButton = new JButton(Settings.getJpoResources().getString("addCategoryJButton"));
        addCategoryJButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
        addCategoryJButton.setMinimumSize(DEFAULT_BUTTON_SIZE);
        addCategoryJButton.setMaximumSize(MAX_BUTTON_SIZE);
        addCategoryJButton.addActionListener((ActionEvent evt) -> {
            final var category = categoryJTextField.getText();
            if (! category.isEmpty()) {
                final Integer key = pictureCollection.addCategory(category);
                final Category categoryObject = new Category(key, category);
                categoriesListModel.addElement(categoryObject);
                categoryJTextField.setText("");
            }
        });
        return addCategoryJButton;
    }

    @NotNull
    private JButton getDoneJButton() {
        final var doneJButton = new JButton(Settings.getJpoResources().getString("doneJButton"));
        doneJButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
        doneJButton.setMinimumSize(DEFAULT_BUTTON_SIZE);
        doneJButton.setMaximumSize(MAX_BUTTON_SIZE);
        doneJButton.addActionListener((ActionEvent evt) -> getRid());
        return doneJButton;
    }


    private JList<Category> getCategoriesJList() {
        final var categoriesJList = new JList<>(categoriesListModel);
        categoriesJList.setMinimumSize(new Dimension(180, 50));
        categoriesJList.setVisibleRowCount(5);
        categoriesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoriesJList.addListSelectionListener(e ->

        {
            if (e.getValueIsAdjusting()) {
                return;
            }
            if (!categoriesJList.isSelectionEmpty()) {
                final var index = categoriesJList.getSelectedIndex();
                final var cat = categoriesJList.getModel().getElementAt(index);
                categoryJTextField.setText(cat.getValue());
            }

        });

        pictureCollection.
                getSortedCategoryStream().
                forEach(categoryEntry ->
                {
                    final var category = new Category(categoryEntry.getKey(), categoryEntry.getValue());
                    categoriesListModel.addElement(category);
                });
        return categoriesJList;
    }

    /**
     * method that closes the frame and gets rid of it
     */
    public void getRid() {
        setVisible(false);
        dispose();
    }

}
