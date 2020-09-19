package org.jpo.gui;

import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.Category;
import org.jpo.datamodel.PictureCollection;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.logging.Logger;

/*
 CategoryEditorJFrame.java:  creates a GUI to allow the user to specify his search

 Copyright (C) 2002 - 2020  Richard Eigenmann.
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

    /**
     * Creates a GUI to edit the categories of the collection
     *
     *
     */
    public CategoryEditorJFrame() {
        Tools.checkEDT();
        initComponents();
    }

    private void initComponents() {
        addWindowListener( new WindowAdapter() {

            @Override
            public void windowClosing( WindowEvent e ) {
                setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
                getRid();
            }
        } );

        setTitle(Settings.getJpoResources().getString("CategoryEditorJFrameTitle"));

        final JPanel jPanel = new JPanel();
        jPanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) );
        jPanel.setLayout( new MigLayout("") );

        final JLabel categoryJLabel = new JLabel(Settings.getJpoResources().getString("categoryJLabel"));
        categoryJLabel.setHorizontalAlignment( SwingConstants.LEFT );
        jPanel.add( categoryJLabel );

        categoryJTextField.setPreferredSize( new Dimension( 200, 25 ) );
        categoryJTextField.setMinimumSize( new Dimension( 200, 25 ) );
        categoryJTextField.setMaximumSize( new Dimension( 600, 25 ) );
        jPanel.add( categoryJTextField );

        final DefaultListModel<Category> categoriesListModel = new DefaultListModel<>();

        final Dimension defaultButtonSize = new Dimension( 150, 25 );
        final Dimension maxButtonSize = new Dimension( 150, 25 );

        final JButton addCategoryJButton = new JButton(Settings.getJpoResources().getString("addCategoryJButton"));
        addCategoryJButton.setPreferredSize( defaultButtonSize );
        addCategoryJButton.setMinimumSize( defaultButtonSize );
        addCategoryJButton.setMaximumSize( maxButtonSize );
        addCategoryJButton.addActionListener(( ActionEvent evt ) -> {
            final String category = categoryJTextField.getText();
            final Integer key = Settings.getPictureCollection().addCategory(category);
            final Category categoryObject = new Category(key, category);
            categoriesListModel.addElement(categoryObject);
            categoryJTextField.setText("");
        });
        jPanel.add(addCategoryJButton, "alignx center, wrap");

        final JLabel categoriesJLabel = new JLabel(Settings.getJpoResources().getString("categoriesJLabel"));
        categoriesJLabel.setHorizontalAlignment(SwingConstants.LEFT);
        jPanel.add(categoriesJLabel);

        final JList<Category> categoriesJList = new JList<>(categoriesListModel);
        categoriesJList.setPreferredSize(new Dimension(180, 250));
        categoriesJList.setMinimumSize(new Dimension(180, 50));
        categoriesJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoriesJList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            if (!categoriesJList.isSelectionEmpty()) {
                final int index = categoriesJList.getSelectedIndex();
                final Category cat = (Category) categoriesJList.getModel().getElementAt(index);
                categoryJTextField.setText(cat.getValue());
            }

        });

        final Iterator<Integer> i = Settings.getPictureCollection().getCategoryIterator();
        while (i.hasNext()) {
            final Integer key = i.next();
            final String category = Settings.getPictureCollection().getCategory(key);
            final Category categoryObject = new Category(key, category);
            categoriesListModel.addElement(categoryObject);
        }

        final JScrollPane listJScrollPane = new JScrollPane(categoriesJList);
        listJScrollPane.setPreferredSize( new Dimension( 200, 270 ) );
        listJScrollPane.setMinimumSize(new Dimension(200, 50));
        jPanel.add(listJScrollPane, "push, grow");

        final JPanel buttonJPanel = new JPanel();
        buttonJPanel.setLayout( new MigLayout() );

        final JButton deleteCategoryJButton = new JButton(Settings.getJpoResources().getString("deleteCategoryJButton"));
        deleteCategoryJButton.setPreferredSize( defaultButtonSize );
        deleteCategoryJButton.setMinimumSize( defaultButtonSize );
        deleteCategoryJButton.setMaximumSize( maxButtonSize );
        deleteCategoryJButton.addActionListener(( ActionEvent evt ) -> {
            int index = categoriesJList.getSelectedIndex();
            if ( index < 0 ) {
                return; // nothing selected
            } // nothing selected
            final Category cat = categoriesJList.getModel().getElementAt( index );
            int count = PictureCollection.countCategoryUsage( cat.getKey(), Settings.getPictureCollection().getRootNode() );
            if ( count > 0 ) {
                int answer = JOptionPane.showConfirmDialog(CategoryEditorJFrame.this,
                        Settings.getJpoResources().getString("countCategoryUsageWarning1") + count + Settings.getJpoResources().getString("countCategoryUsageWarning2"),
                        Settings.getJpoResources().getString("genericWarning"),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if ( answer == JOptionPane.CANCEL_OPTION ) {
                    return;
                } else {
                    Settings.getPictureCollection().removeCategoryUsage( cat.getKey(), Settings.getPictureCollection().getRootNode() );
                }

            }
            categoriesListModel.remove(index);
            Settings.getPictureCollection().removeCategory(cat.getKey());
        });
        buttonJPanel.add( deleteCategoryJButton, "wrap" );

        final JButton renameCategoryJButton = new JButton(Settings.getJpoResources().getString("renameCategoryJButton"));
        renameCategoryJButton.setPreferredSize( defaultButtonSize );
        renameCategoryJButton.setMinimumSize( defaultButtonSize );
        renameCategoryJButton.setMaximumSize( maxButtonSize );
        renameCategoryJButton.addActionListener(( ActionEvent evt ) -> {
            LOGGER.info( "I want to rename the selected category " );
            int index = categoriesJList.getSelectedIndex();
            if ( index < 0 ) {
                return; // nothing selected
            } // nothing selected
            Category cat = categoriesJList.getModel().getElementAt( index );
            categoriesListModel.remove(index);
            String category1 = categoryJTextField.getText();
            Settings.getPictureCollection().renameCategory( cat.getKey(), category1 );
            Category categoryObject1 = new Category( cat.getKey(), category1 );
            categoriesListModel.insertElementAt(categoryObject1, index);
            categoryJTextField.setText("");
        });
        buttonJPanel.add( renameCategoryJButton, "wrap" );

        final JButton doneJButton = new JButton(Settings.getJpoResources().getString("doneJButton"));
        doneJButton.setPreferredSize( defaultButtonSize );
        doneJButton.setMinimumSize( defaultButtonSize );
        doneJButton.setMaximumSize( maxButtonSize );
        doneJButton.addActionListener(( ActionEvent evt ) -> getRid());
        buttonJPanel.add( doneJButton, "wrap" );

        jPanel.add( buttonJPanel, "aligny top, wrap" );

        getContentPane().add( jPanel, BorderLayout.CENTER );
        pack();
        setLocationRelativeTo(Settings.getAnchorFrame());
        setVisible(true);
    }

    /**
     * method that closes the frame and gets rid of it
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }

}
