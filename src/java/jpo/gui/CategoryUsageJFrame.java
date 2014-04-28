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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;
import jpo.EventBus.ShowCategoryUsageEditorRequest;
import jpo.dataModel.Category;
import jpo.dataModel.GroupInfo;
import jpo.dataModel.PictureInfo;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;

/*
 CategoryUsageJFrame.java:  Creates a Window in which the categories are shown

 Copyright (C) 2002-2014  Richard Eigenmann.
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
    final JLabel numberOfPicturesJLabel = new JLabel( "" );

    /**
     * Creates a GUI to edit the categories of the collection
     *
     *
     */
    public CategoryUsageJFrame() {
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosing( WindowEvent e ) {
                setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
                getRid();
            }
        } );

        categoryJScrollPane = new CategoryJScrollPane();
        listModel = categoryJScrollPane.getDefaultListModel();

        initComponents();
    }

    /**
     * Creates a GUI to edit the categories of the collection
     *
     *
     * @param request
     */
    public CategoryUsageJFrame( ShowCategoryUsageEditorRequest request ) {
        this();
        
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
        jPanel.setLayout( new GridBagLayout() );

        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;

        c.weightx = 0.1;
        c.weighty = 0;
        c.anchor = GridBagConstraints.PAGE_START;
        c.insets = new Insets( 0, 0, 3, 5 );
        c.fill = GridBagConstraints.HORIZONTAL;

        final Dimension defaultButtonSize = new Dimension( 150, 25 );
        final Dimension maxButtonSize = new Dimension( 150, 25 );

        c.gridx++;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        c.weightx = 0.6;
        c.weighty = 0.6;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets( 0, 0, 0, 0 );
        jPanel.add( categoryJScrollPane, c );

        final JPanel buttonJPanel = new JPanel();
        buttonJPanel.setLayout( new GridBagLayout() );
        GridBagConstraints bc = new GridBagConstraints();
        bc.gridx = 0;
        bc.gridy = 0;
        bc.fill = GridBagConstraints.NONE;

        numberOfPicturesJLabel.setHorizontalAlignment( JLabel.LEFT );
        buttonJPanel.add( numberOfPicturesJLabel, bc );

        final JButton modifyCategoryJButton = new JButton( Settings.jpoResources.getString( "modifyCategoryJButton" ) );
        modifyCategoryJButton.setPreferredSize( defaultButtonSize );
        modifyCategoryJButton.setMinimumSize( defaultButtonSize );
        modifyCategoryJButton.setMaximumSize( maxButtonSize );
        modifyCategoryJButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent evt ) {
                new CategoryEditorJFrame();
            }
        } );
        bc.gridy++;
        buttonJPanel.add( modifyCategoryJButton, bc );

        final JButton refreshJButton = new JButton( Settings.jpoResources.getString( "refreshJButtonCUJF" ) );
        refreshJButton.setPreferredSize( defaultButtonSize );
        refreshJButton.setMinimumSize( defaultButtonSize );
        refreshJButton.setMaximumSize( maxButtonSize );
        refreshJButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent evt ) {
                updateCategories();
            }
        } );
        bc.gridy++;
        buttonJPanel.add( refreshJButton, bc );

        final JButton updateJButton = new JButton( Settings.jpoResources.getString( "updateJButton" ) );
        updateJButton.setPreferredSize( defaultButtonSize );
        updateJButton.setMinimumSize( defaultButtonSize );
        updateJButton.setMaximumSize( maxButtonSize );
        updateJButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent evt ) {
                storeSelection();
                getRid();
            }
        } );
        bc.gridy++;
        buttonJPanel.add( updateJButton, bc );

        final JButton cancelJButton = new JButton( Settings.jpoResources.getString( "cancelJButton" ) );
        cancelJButton.setPreferredSize( defaultButtonSize );
        cancelJButton.setMinimumSize( defaultButtonSize );
        cancelJButton.setMaximumSize( maxButtonSize );
        cancelJButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent evt ) {
                getRid();
            }
        } );
        bc.gridy++;
        buttonJPanel.add( cancelJButton, bc );

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
     * method that closes the frame and gets rid of it
     */
    private void getRid() {
        setVisible( false );
        dispose();
    }

    /**
     * This method receives the selection the Category Editor is working on
     *
     * @param nodes
     */
    public void setSelection( Set<SortableDefaultMutableTreeNode> nodes ) {
        selectedNodes = nodes;
        updateCategories();
    }

    /**
     * This method receives the selection the Category Editor is to work on.
     * Here we can pass a Group node and a flag whether the nodes are to be
     * recursively searched for the pictures.
     *
     * @param groupNode The node from which to add the pictures
     * @param recurse	A flag whether to recurse the search into sub groups
     */
    public void setGroupSelection( SortableDefaultMutableTreeNode groupNode,
            boolean recurse ) {
        selectedNodes = new HashSet<SortableDefaultMutableTreeNode>();
        SortableDefaultMutableTreeNode n;
        Enumeration nodes = groupNode.children();
        while ( nodes.hasMoreElements() ) {
            n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if ( n.getUserObject() instanceof PictureInfo ) {
                selectedNodes.add( n );
            } else if ( ( n.getUserObject() instanceof GroupInfo ) && recurse ) {
                LOGGER.info( "recurse not currently implemented" );
            }
        }
        updateCategories();
    }

    /**
     * This method reads the nodes and sets the categories accordingly
     */
    public void updateCategories() {
        if ( selectedNodes == null ) {
            LOGGER.info( "selectedNodes is null!" );
            return;
        }
        numberOfPicturesJLabel.setText( String.format( Settings.jpoResources.getString( "numberOfPicturesJLabel" ), selectedNodes.size() ) );

        categoryJScrollPane.loadCategories();

        // zero out the categories
        Category c;
        Enumeration categoryEnumeration = listModel.elements();
        while ( categoryEnumeration.hasMoreElements() ) {
            c = (Category) categoryEnumeration.nextElement();
            LOGGER.info( "Setting Status to undefined on Category: " + c.getKey().toString() + " " + c.toString() );
            c.setStatus( Category.UNDEFINED );
            // force screen update:
            listModel.setElementAt( c, listModel.indexOf( c ) );
        }

        Object[] pictureCategories;
        int pictureCategoryKey;
        int currentStatus;
        boolean found;
        PictureInfo pi;
        Enumeration pictureNodes;
        Object myObject;

        // loop through each category on the list and check we have a node that
        categoryEnumeration = listModel.elements();
        while ( categoryEnumeration.hasMoreElements() ) {
            c = (Category) categoryEnumeration.nextElement();
            LOGGER.info( "Checking Category: " + c.getKey().toString() + " " + c.toString() );

            for ( SortableDefaultMutableTreeNode pictureNode : selectedNodes ) {
                //pictureNodes = selectedNodes.elements();
                //while ( pictureNodes.hasMoreElements() ) {
                //myObject = ( (SortableDefaultMutableTreeNode) pictureNodes.nextElement() ).getUserObject();
                myObject = pictureNode.getUserObject();
                if ( myObject instanceof PictureInfo ) {
                    pi = (PictureInfo) myObject;
                    if ( pi.containsCategory( c.getKey() ) ) {
                        currentStatus = c.getStatus();
                        LOGGER.info( "Status of category is: " + Integer.toString( currentStatus ) );
                        if ( currentStatus == Category.UNDEFINED ) {
                            c.setStatus( Category.SELECTED );
                            // force screen update:
                            listModel.setElementAt( c, listModel.indexOf( c ) );
                        } else if ( currentStatus == Category.UN_SELECTED ) {
                            c.setStatus( Category.BOTH );
                            // force screen update:
                            listModel.setElementAt( c, listModel.indexOf( c ) );
                        }
                        // ignore status both and selected as we would only be adding to that
                    } else {
                        // we get here if there was no category match
                        currentStatus = c.getStatus();
                        LOGGER.info( "Status of category is: " + Integer.toString( currentStatus ) );
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
    public void storeSelection() {
        int status;
        Category c;
        Enumeration e;

        HashSet<Object> selectedCategories = categoryJScrollPane.getSelectedCategories();
synchronized( categoryGuiListeners ) {
        for ( CategoryGuiListenerInterface listener : categoryGuiListeners ) {
            listener.categoriesChosen( selectedCategories );
        }
}

        // update the selected pictures
        if ( selectedNodes == null ) {
            LOGGER.info( "CategoryUsageJFrame.storeSelection: called with a null selection. Aborting." );
            return;
        }
        PictureInfo pictureInfo;
        Object userObject;
        for ( SortableDefaultMutableTreeNode selectedNode : selectedNodes ) {
            userObject = selectedNode.getUserObject();
            if ( userObject instanceof PictureInfo ) {
                pictureInfo = (PictureInfo) userObject;
                e = listModel.elements();
                while ( e.hasMoreElements() ) {
                    c = (Category) e.nextElement();
                    status = c.getStatus();
                    if ( status == Category.SELECTED ) {
                        pictureInfo.addCategoryAssignment( c.getKey() );
                    } else if ( status == Category.UN_SELECTED ) {
                        pictureInfo.removeCategory( c.getKey() );
                    }
                }
            }
        }
    }

    /**
     * This list holds references to categoryGuiListeners
     */
    private final Set<CategoryGuiListenerInterface> categoryGuiListeners = Collections.synchronizedSet( new HashSet<CategoryGuiListenerInterface>() );

    /**
     * This method registers the categoryGuiListener
     *
     * @param listener
     */
    public void addCategoryGuiListener( CategoryGuiListenerInterface listener ) {
        categoryGuiListeners.add( listener );
    }

    /**
     * This method deregisters the categoryGuiListener
     *
     * @param listener
     */
    public void removeCategoryGuiListener( CategoryGuiListenerInterface listener ) {
        categoryGuiListeners.remove( listener );
    }
}
