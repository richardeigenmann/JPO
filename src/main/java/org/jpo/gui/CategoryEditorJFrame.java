package org.jpo.gui;

import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.NotNull;
import org.jpo.datamodel.Category;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;

/*
 CategoryEditorJFrame.java:  creates a GUI to allow the user to specify his search

 Copyright (C) 2002 - 2021  Richard Eigenmann.
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

    /**
     * Creates a GUI to edit the categories of the collection
     */
    public CategoryEditorJFrame() {
        setTitle(Settings.getJpoResources().getString("CategoryEditorJFrameTitle"));
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                getRid();
            }
        });

        final JPanel jPanel = new JPanel();
        jPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        jPanel.setLayout(new MigLayout(""));

        final JList<Category> categoriesJList = getCategoriesJList();

        final JScrollPane listJScrollPane = new JScrollPane(categoriesJList);
        listJScrollPane.setPreferredSize(new Dimension(250, 370));
        listJScrollPane.setMinimumSize(new Dimension(200, 50));
        jPanel.add(listJScrollPane, "push, grow");


        final JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new MigLayout());

        final JLabel categoryJLabel = new JLabel(Settings.getJpoResources().getString("categoryJLabel"));
        categoryJLabel.setHorizontalAlignment(SwingConstants.LEFT);
        rightColumn.add(categoryJLabel, "wrap");

        categoryJTextField.setPreferredSize(new Dimension(200, 25));
        categoryJTextField.setMinimumSize(new Dimension(200, 25));
        categoryJTextField.setMaximumSize(new Dimension(600, 25));
        rightColumn.add(categoryJTextField, "wrap");

        final JButton addCategoryJButton = getAddCategoryJButton();
        rightColumn.add(addCategoryJButton, "wrap");
        final JButton deleteCategoryJButton = getDeleteCategoryJButton(categoriesJList);
        rightColumn.add(deleteCategoryJButton, "wrap");
        final JButton renameCategoryJButton = getRenameCategoryJButton(categoriesJList);
        rightColumn.add(renameCategoryJButton, "wrap");
        final JButton doneJButton = getDoneJButton();
        rightColumn.add(doneJButton, "wrap");

        jPanel.add(rightColumn, "aligny top, wrap");

        getContentPane().add(jPanel);
        pack();
        setLocationRelativeTo(Settings.getAnchorFrame());
        setVisible(true);
    }


    @NotNull
    private JButton getRenameCategoryJButton(final JList<Category> categoriesJList) {
        final JButton renameCategoryJButton = new JButton(Settings.getJpoResources().getString("renameCategoryJButton"));
        renameCategoryJButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
        renameCategoryJButton.setMinimumSize(DEFAULT_BUTTON_SIZE);
        renameCategoryJButton.setMaximumSize(MAX_BUTTON_SIZE);
        renameCategoryJButton.addActionListener((ActionEvent evt) -> {
            LOGGER.info("I want to rename the selected category ");
            int index = categoriesJList.getSelectedIndex();
            if (index < 0) {
                return; // nothing selected
            } // nothing selected
            final Category selectedCategory = categoriesJList.getModel().getElementAt(index);
            categoriesListModel.remove(index);
            final String renamedCategory = categoryJTextField.getText();
            Settings.getPictureCollection().renameCategory(selectedCategory.getKey(), renamedCategory);
            final Category newCategoryObject = new Category(selectedCategory.getKey(), renamedCategory);
            categoriesListModel.insertElementAt(newCategoryObject, index);
            categoryJTextField.setText("");
        });
        return renameCategoryJButton;
    }

    @NotNull
    private JButton getDeleteCategoryJButton(final JList<Category> categoriesJList) {
        final JButton deleteCategoryJButton = new JButton(Settings.getJpoResources().getString("deleteCategoryJButton"));
        deleteCategoryJButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
        deleteCategoryJButton.setMinimumSize(DEFAULT_BUTTON_SIZE);
        deleteCategoryJButton.setMaximumSize(MAX_BUTTON_SIZE);
        deleteCategoryJButton.addActionListener((ActionEvent evt) -> {
            int index = categoriesJList.getSelectedIndex();
            if (index < 0) {
                return; // nothing selected
            } // nothing selected
            final Category cat = categoriesJList.getModel().getElementAt(index);
            int count = PictureCollection.countCategoryUsage(cat.getKey(), Settings.getPictureCollection().getRootNode());
            if (count > 0) {
                int answer = JOptionPane.showConfirmDialog(CategoryEditorJFrame.this,
                        Settings.getJpoResources().getString("countCategoryUsageWarning1") + count + Settings.getJpoResources().getString("countCategoryUsageWarning2"),
                        Settings.getJpoResources().getString("genericWarning"),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (answer == JOptionPane.CANCEL_OPTION) {
                    return;
                } else {
                    Settings.getPictureCollection().removeCategoryUsage(cat.getKey(), Settings.getPictureCollection().getRootNode());
                }

            }
            categoriesListModel.remove(index);
            Settings.getPictureCollection().removeCategory(cat.getKey());
        });
        return deleteCategoryJButton;
    }

    @NotNull
    private JButton getAddCategoryJButton() {
        final JButton addCategoryJButton = new JButton(Settings.getJpoResources().getString("addCategoryJButton"));
        addCategoryJButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
        addCategoryJButton.setMinimumSize(DEFAULT_BUTTON_SIZE);
        addCategoryJButton.setMaximumSize(MAX_BUTTON_SIZE);
        addCategoryJButton.addActionListener((ActionEvent evt) -> {
            final String category = categoryJTextField.getText();
            if (category.length() > 0) {
                final Integer key = Settings.getPictureCollection().addCategory(category);
                final Category categoryObject = new Category(key, category);
                categoriesListModel.addElement(categoryObject);
                categoryJTextField.setText("");
            }
        });
        return addCategoryJButton;
    }

    @NotNull
    private JButton getDoneJButton() {
        final JButton doneJButton = new JButton(Settings.getJpoResources().getString("doneJButton"));
        doneJButton.setPreferredSize(DEFAULT_BUTTON_SIZE);
        doneJButton.setMinimumSize(DEFAULT_BUTTON_SIZE);
        doneJButton.setMaximumSize(MAX_BUTTON_SIZE);
        doneJButton.addActionListener((ActionEvent evt) -> getRid());
        return doneJButton;
    }


    private JList<Category> getCategoriesJList() {
        final JList<Category> categoriesJList = new JList<>(categoriesListModel);
        categoriesJList.setMinimumSize(new Dimension(180, 50));
        categoriesJList.setVisibleRowCount(5);
        categoriesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoriesJList.addListSelectionListener(e ->

        {
            if (e.getValueIsAdjusting()) {
                return;
            }
            if (!categoriesJList.isSelectionEmpty()) {
                final int index = categoriesJList.getSelectedIndex();
                final Category cat = categoriesJList.getModel().getElementAt(index);
                categoryJTextField.setText(cat.getValue());
            }

        });

        Settings.getPictureCollection().

                getSortedCategoryStream().

                forEach(categoryEntry ->

                {
                    final Category category = new Category(categoryEntry.getKey(), categoryEntry.getValue());
                    categoriesListModel.addElement(category);
                });
        return categoriesJList;
    }

    /**
     * method that closes the frame and gets rid of it
     */
    private void getRid() {
        setVisible(false);
        dispose();
    }

}
