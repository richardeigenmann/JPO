package jpo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import jpo.dataModel.Category;
import jpo.dataModel.PictureCollection;
import jpo.dataModel.Settings;
import jpo.dataModel.Tools;

/*
 CategoryEditorJFrame.java:  creates a GUI to allow the user to specify his search

 Copyright (C) 2002 - 2012  Richard Eigenmann.
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
 *
 *
 */
public class CategoryEditorJFrame
        extends JFrame
        implements ListSelectionListener {

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CategoryEditorJFrame.class.getName() );

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

        setTitle( Settings.jpoResources.getString( "CategoryEditorJFrameTitle" ) );

        final JPanel jPanel = new JPanel();
        jPanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) );
        jPanel.setLayout( new GridBagLayout() );

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;

        final JLabel categoryJLabel = new JLabel( Settings.jpoResources.getString( "categoryJLabel" ) );
        categoryJLabel.setHorizontalAlignment( JLabel.LEFT );
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets( 0, 0, 3, 5 );
        jPanel.add( categoryJLabel, c );

        categoryJTextField.setPreferredSize( new Dimension( 200, 25 ) );
        categoryJTextField.setMinimumSize( new Dimension( 200, 25 ) );
        categoryJTextField.setMaximumSize( new Dimension( 600, 25 ) );
        c.gridx++;
        c.weightx = 0.6;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets( 0, 0, 3, 0 );
        jPanel.add( categoryJTextField, c );

        final DefaultListModel<Category> listModel = new DefaultListModel<Category>();

        final Dimension defaultButtonSize = new Dimension( 150, 25 );
        final Dimension maxButtonSize = new Dimension( 150, 25 );

        final JButton addCategoryJButton = new JButton( Settings.jpoResources.getString( "addCategoryJButton" ) );
        addCategoryJButton.setPreferredSize( defaultButtonSize );
        addCategoryJButton.setMinimumSize( defaultButtonSize );
        addCategoryJButton.setMaximumSize( maxButtonSize );
        addCategoryJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent evt ) {
                String category = categoryJTextField.getText();
                Integer key = Settings.pictureCollection.addCategory( category );
                Category categoryObject = new Category( key, category );
                listModel.addElement( categoryObject );
                categoryJTextField.setText( "" );
                //logger.info("I want to add a category: " + categoryJTextField.getText() );
            }
        } );
        c.gridx++;
        c.weightx = 0.1;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.fill = GridBagConstraints.NONE;
        jPanel.add( addCategoryJButton, c );

        final JLabel categoriesJLabel = new JLabel( Settings.jpoResources.getString( "categoriesJLabel" ) );
        categoriesJLabel.setHorizontalAlignment( JLabel.LEFT );
        c.gridy++;
        c.gridx = 0;
        c.weightx = 0.1;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.insets = new Insets( 0, 0, 0, 5 );
        jPanel.add( categoriesJLabel, c );

        final JList<Category> categoriesJList = new JList<Category>( listModel );
        categoriesJList.setPreferredSize( new Dimension( 180, 250 ) );
        categoriesJList.setMinimumSize( new Dimension( 180, 50 ) );
        //categoriesJList.setMaximumSize( new Dimension( 1000, 500) );
        categoriesJList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        categoriesJList.addListSelectionListener( this );

        Iterator i = Settings.pictureCollection.getCategoryIterator();
        Integer key;
        String category;
        Category categoryObject;
        while ( i.hasNext() ) {
            key = (Integer) i.next();
            category = Settings.pictureCollection.getCategory( key );
            categoryObject = new Category( key, category );
            listModel.addElement( categoryObject );
        }

        final JScrollPane listJScrollPane = new JScrollPane( categoriesJList );
        listJScrollPane.setPreferredSize( new Dimension( 200, 270 ) );
        listJScrollPane.setMinimumSize( new Dimension( 200, 50 ) );
        c.gridx++;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.6;
        c.weighty = 0.6;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets( 0, 0, 0, 0 );
        jPanel.add( listJScrollPane, c );

        final JPanel buttonJPanel = new JPanel();
        buttonJPanel.setLayout( new GridBagLayout() );
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0;
        bc.gridy = 0;
        bc.fill = GridBagConstraints.NONE;

        final JButton deleteCategoryJButton = new JButton( Settings.jpoResources.getString( "deleteCategoryJButton" ) );
        deleteCategoryJButton.setPreferredSize( defaultButtonSize );
        deleteCategoryJButton.setMinimumSize( defaultButtonSize );
        deleteCategoryJButton.setMaximumSize( maxButtonSize );
        deleteCategoryJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent evt ) {
                //logger.info("I want to remove the selected category " );
                int index = categoriesJList.getSelectedIndex();
                if ( index < 0 ) {
                    return; // nothing selected
                } // nothing selected
                Category cat = (Category) categoriesJList.getModel().getElementAt( index );
                int count = PictureCollection.countCategoryUsage( cat.getKey(), Settings.pictureCollection.getRootNode() );
                if ( count > 0 ) {
                    int answer = JOptionPane.showConfirmDialog( CategoryEditorJFrame.this,
                            Settings.jpoResources.getString( "countCategoryUsageWarning1" ) + Integer.toString( count ) + Settings.jpoResources.getString( "countCategoryUsageWarning2" ),
                            Settings.jpoResources.getString( "genericWarning" ),
                            JOptionPane.OK_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE );
                    if ( answer == JOptionPane.CANCEL_OPTION ) {
                        return;
                    } else {
                        Settings.pictureCollection.removeCategoryUsage( cat.getKey(), Settings.pictureCollection.getRootNode() );
                    }

                }
                listModel.remove( index );
                Settings.pictureCollection.removeCategory( cat.getKey() );
                //logger.info("I want to delete: " + cat.value.toString());
            }
        } );
        buttonJPanel.add( deleteCategoryJButton, bc );

        final JButton renameCategoryJButton = new JButton( Settings.jpoResources.getString( "renameCategoryJButton" ) );
        renameCategoryJButton.setPreferredSize( defaultButtonSize );
        renameCategoryJButton.setMinimumSize( defaultButtonSize );
        renameCategoryJButton.setMaximumSize( maxButtonSize );
        renameCategoryJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent evt ) {
                LOGGER.info( "I want to rename the selected category " );
                int index = categoriesJList.getSelectedIndex();
                if ( index < 0 ) {
                    return; // nothing selected
                } // nothing selected
                Category cat = (Category) categoriesJList.getModel().getElementAt( index );
                listModel.remove( index );

                String category = categoryJTextField.getText();
                Settings.pictureCollection.renameCategory( cat.getKey(), category );
                Category categoryObject = new Category( cat.getKey(), category );
                listModel.insertElementAt( categoryObject, index );
                categoryJTextField.setText( "" );
            }
        } );
        bc.gridy++;
        buttonJPanel.add( renameCategoryJButton, bc );

        final JButton doneJButton = new JButton( Settings.jpoResources.getString( "doneJButton" ) );
        doneJButton.setPreferredSize( defaultButtonSize );
        doneJButton.setMinimumSize( defaultButtonSize );
        doneJButton.setMaximumSize( maxButtonSize );
        doneJButton.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed( ActionEvent evt ) {
                getRid();
            }
        } );
        bc.gridy++;
        buttonJPanel.add( doneJButton, bc );

        c.gridx++;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.weightx = 0.1;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        jPanel.add( buttonJPanel, c );

        getContentPane().add( jPanel, BorderLayout.CENTER );
        pack();
        setLocationRelativeTo( Settings.anchorFrame );
        setVisible( true );
    }

    /**
     * method that closes te frame and gets rid of it
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }

    /**
     * Method from the ListSelectionListener implementation that tracks when an
     * element was selected.
     *
     * @param e
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
            categoryJTextField.setText( cat.getValue().toString() );
        }
    }
}
