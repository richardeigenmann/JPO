package org.jpo.gui;

import net.miginfocom.swing.MigLayout;
import org.jpo.datamodel.Category;
import org.jpo.datamodel.PictureInfo;
import org.jpo.datamodel.Settings;
import org.jpo.datamodel.SortableDefaultMutableTreeNode;
import org.jpo.eventbus.ShowCategoryUsageEditorRequest;
import org.jpo.gui.swing.CategoryJScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 Copyright (C) 2002-2020  Richard Eigenmann.
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
 * CategoryUsageJFrame.java: Creates a GUI that shows the Categories that are
 * defined. It visually shows which categories are applied to a selection of
 * images. If updates are allowed it allows to update the pictures with the
 * Categories being clicked.
 *
 *
 */
public class CategoryUsageJFrame extends JFrame {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger( CategoryUsageJFrame.class.getName() );

    private final DefaultListModel<Category> listModel;

    private final CategoryJScrollPane categoryJScrollPane;
    /**
     * An Array to record the selected nodes
     */
    private Set<SortableDefaultMutableTreeNode> selectedNodes;
    private final JLabel numberOfPicturesJLabel = new JLabel( "" );

    /**
     * Creates a GUI to edit the categories of the collection
     *
     * @param request The request
     */
    public CategoryUsageJFrame(final ShowCategoryUsageEditorRequest request) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                getRid();
            }
        });

        categoryJScrollPane = new CategoryJScrollPane();
        listModel = categoryJScrollPane.getDefaultListModel();

        initComponents();

        Set<SortableDefaultMutableTreeNode> nodes = request.getNodes();
        setSelection( nodes );
    }

    /**
     * initialises the components
     */
    private void initComponents() {
        setTitle( Settings.jpoResources.getString( "CategoryUsageJFrameTitle" ) );

        final JPanel jPanel = new JPanel();
        jPanel.setBorder( BorderFactory.createEmptyBorder( 8, 8, 8, 8 ) );
        jPanel.setLayout( new MigLayout() );

        final Dimension defaultButtonSize = new Dimension( 150, 25 );
        final Dimension maxButtonSize = new Dimension( 150, 25 );

        jPanel.add( categoryJScrollPane );

        final JPanel buttonJPanel = new JPanel();
        buttonJPanel.setLayout( new MigLayout() );

        numberOfPicturesJLabel.setHorizontalAlignment( SwingConstants.LEFT );
        buttonJPanel.add( numberOfPicturesJLabel, "wrap" );

        final JButton modifyCategoryJButton = new JButton( Settings.jpoResources.getString( "modifyCategoryJButton" ) );
        modifyCategoryJButton.setPreferredSize( defaultButtonSize );
        modifyCategoryJButton.setMinimumSize( defaultButtonSize );
        modifyCategoryJButton.setMaximumSize( maxButtonSize );
        modifyCategoryJButton.addActionListener( ( ActionEvent evt ) -> new CategoryEditorJFrame());
        buttonJPanel.add( modifyCategoryJButton, "wrap" );

        final JButton refreshJButton = new JButton( Settings.jpoResources.getString( "refreshJButtonCUJF" ) );
        refreshJButton.setPreferredSize( defaultButtonSize );
        refreshJButton.setMinimumSize( defaultButtonSize );
        refreshJButton.setMaximumSize( maxButtonSize );
        refreshJButton.addActionListener( ( ActionEvent evt ) -> updateCategories());
        buttonJPanel.add( refreshJButton, "wrap" );

        final JButton updateJButton = new JButton( Settings.jpoResources.getString( "updateJButton" ) );
        updateJButton.setPreferredSize( defaultButtonSize );
        updateJButton.setMinimumSize( defaultButtonSize );
        updateJButton.setMaximumSize( maxButtonSize );
        updateJButton.addActionListener( ( ActionEvent evt ) -> {
            storeSelection();
            getRid();
        } );
        buttonJPanel.add( updateJButton, "wrap" );

        final JButton cancelJButton = new JButton( Settings.jpoResources.getString( "cancelJButton" ) );
        cancelJButton.setPreferredSize( defaultButtonSize );
        cancelJButton.setMinimumSize( defaultButtonSize );
        cancelJButton.setMaximumSize( maxButtonSize );
        cancelJButton.addActionListener( ( ActionEvent evt ) -> getRid());
        buttonJPanel.add( cancelJButton, "wrap" );

        jPanel.add( buttonJPanel, "aligny top" );

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

    /**
     * This method receives the selection the Category Editor is working on
     *
     * @param nodes The nodes
     */
    private void setSelection(Set<SortableDefaultMutableTreeNode> nodes) {
        selectedNodes = nodes;
        updateCategories();
    }

    /**
     * This method reads the nodes and sets the categories accordingly
     */
    private void updateCategories() {
        if ( selectedNodes == null ) {
            LOGGER.info( "selectedNodes is null!" );
            return;
        }
        numberOfPicturesJLabel.setText( String.format( Settings.jpoResources.getString( "numberOfPicturesJLabel" ), selectedNodes.size() ) );

        categoryJScrollPane.loadCategories( Settings.getPictureCollection().getCategoryIterator() );

        // zero out the categories
        Category c;
        final Enumeration<Category> categoryEnumeration = listModel.elements();
        while ( categoryEnumeration.hasMoreElements() ) {
            c = categoryEnumeration.nextElement();
            LOGGER.log(Level.INFO, "Setting Status to undefined on Category: {0} {1}", new Object[]{c.getKey().toString(), c.toString()});
            c.setStatus(Category.UNDEFINED);
            // force screen update:
            listModel.setElementAt(c, listModel.indexOf(c));
        }

        int currentStatus;
        Object myObject;

        // loop through each category on the list and check we have a node that
        final Enumeration<Category> categoryEnumeration2 = listModel.elements();
        while (categoryEnumeration2.hasMoreElements()) {
            c = categoryEnumeration2.nextElement();
            LOGGER.log(Level.INFO, "Checking Category: {0} {1}", new Object[]{c.getKey().toString(), c.toString()});

            for (final SortableDefaultMutableTreeNode pictureNode : selectedNodes) {
                myObject = pictureNode.getUserObject();
                if (myObject instanceof PictureInfo pi) {
                    if (pi.containsCategory(c.getKey())) {
                        currentStatus = c.getStatus();
                        LOGGER.log(Level.INFO, "Status of category is: {0}", Integer.toString(currentStatus));
                        if (currentStatus == Category.UNDEFINED) {
                            c.setStatus(Category.SELECTED);
                            // force screen update:
                            listModel.setElementAt(c, listModel.indexOf(c));
                        } else if (currentStatus == Category.UN_SELECTED) {
                            c.setStatus(Category.BOTH);
                            // force screen update:
                            listModel.setElementAt( c, listModel.indexOf( c ) );
                        }
                        // ignore status both and selected as we would only be adding to that
                    } else {
                        // we get here if there was no category match
                        currentStatus = c.getStatus();
                        LOGGER.log( Level.INFO, "Status of category is: {0}", Integer.toString( currentStatus ) );
                        if ( currentStatus == Category.UNDEFINED ) {
                            c.setStatus( Category.UN_SELECTED );
                            // force screen update:
                            listModel.setElementAt( c, listModel.indexOf( c ) );
                        } else if ( currentStatus == Category.SELECTED ) {
                            c.setStatus( Category.BOTH );
                            // force screen update:
                            listModel.setElementAt( c, listModel.indexOf( c ) );
                        }
                        // ignore status unselected and both as nothing would change
                    }
                }
            }
        }
    }

    /**
     * This method updates the selected pictures with the new category
     * classification.
     */
    private void storeSelection() {
        int status;
        Category c;


        final Collection<Integer> selectedCategories = categoryJScrollPane.getSelectedCategories();
        synchronized (categoryGuiListeners) {
            categoryGuiListeners.forEach(listener
                    -> listener.categoriesChosen(selectedCategories)
            );
        }

        // update the selected pictures
        if (selectedNodes == null) {
            LOGGER.info("CategoryUsageJFrame.storeSelection: called with a null selection. Aborting.");
            return;
        }
        for (final SortableDefaultMutableTreeNode selectedNode : selectedNodes) {
            if (selectedNode.getUserObject() instanceof PictureInfo pictureInfo) {
                final Enumeration<Category> e = listModel.elements();
                while (e.hasMoreElements()) {
                    c = e.nextElement();
                    status = c.getStatus();
                    if (status == Category.SELECTED) {
                        pictureInfo.addCategoryAssignment(c.getKey());
                    } else if (status == Category.UN_SELECTED) {
                        pictureInfo.removeCategory(c.getKey());
                    }
                }
            }
        }
    }

    /**
     * This list holds references to categoryGuiListeners
     */
    private final Set<CategoryGuiListenerInterface> categoryGuiListeners = Collections.synchronizedSet( new HashSet<>() );

}
