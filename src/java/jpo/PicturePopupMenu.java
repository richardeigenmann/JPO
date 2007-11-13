package jpo;

import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.lang.*;

/*
PicturePopupMenu.java:  a popup menu for pictures
 
Copyright (C) 2002-2007  Richard Eigenmann.
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
 *  This class generates a popup menu over a picture node.
 *
 */

public class PicturePopupMenu extends JPopupMenu
        implements ActionListener,
        RecentDropNodeListener,
        CopyLocationsChangeListener,
        UserFunctionsChangeListener {
    
    
    
    /**
     *  submenu which presents the user with his user function
     */
    private JMenu userFunctionsJMenu
            = new JMenu( Settings.jpoResources.getString("userFunctionsJMenu") );
    
    
    /**
     *  array of menu items that allows the user to call up a user function
     *
     **/
    private JMenuItem [] userFunctionsJMenuItems
            = new JMenuItem[ Settings.maxUserFunctions ];
    
    
    /**
     *  submenu which presents the user with several rotation options
     */
    private JMenu rotationJMenu
            = new JMenu( Settings.jpoResources.getString("rotation") );
    
    /**
     *  menu item that allows the user to request regeneration of the
     *  thumbnail with a 90 Deg rotation.
     *
     **/
    private JMenuItem rotate90JMenuItem
            = new JMenuItem( Settings.jpoResources.getString("rotate90") );;
            
            /**
             *  menu item that allows the user to request regeneration of the
             *  thumbnail with a 180 Deg rotation.
             *
             **/
            private JMenuItem rotate180JMenuItem
                    = new JMenuItem( Settings.jpoResources.getString("rotate180") );;
                    
                    /**
                     *  menu item that allows the user to request regeneration of the
                     *  thumbnail with a 270 Deg rotation.
                     *
                     **/
                    private JMenuItem rotate270JMenuItem
                            = new JMenuItem( Settings.jpoResources.getString("rotate270") );;
                            
                            /**
                             *  menu item that allows the user to request regeneration of the
                             *  thumbnail with a 0 Deg rotation.
                             *
                             **/
                            private JMenuItem rotate0JMenuItem
                                    = new JMenuItem( Settings.jpoResources.getString("rotate0") );;
                                    
                                    
                                    
                                    
                                    /**
                                     *  menu item that allows the user to remove the picture node from the collection
                                     **/
                                    private JMenuItem pictureNodeRemove
                                            = new JMenuItem( Settings.jpoResources.getString("pictureNodeRemove") );
                                    
                                    
                                    /**
                                     *  submenu which presents the user with several copy targets
                                     */
                                    private JMenu copyImageJMenu
                                            = new JMenu( Settings.jpoResources.getString("copyImageJMenuLabel") );
                                    
                                    
                                    /**
                                     *  menu item that presents the user with file chooser for the picture copy
                                     *
                                     **/
                                    private JMenuItem copyToNewLocationJMenuItem
                                            = new JMenuItem( Settings.jpoResources.getString("copyToNewLocationJMenuItem") );
                                    
                                    
                                    /**
                                     *  array of menu items that allows the user to copy the picture to a memorised file location
                                     *
                                     **/
                                    private JMenuItem [] copyLocationsJMenuItems
                                            = new JMenuItem[ Settings.maxCopyLocations ];
                                    
                                    
                                    /**
                                     *  submenu which has several navigation options
                                     */
                                    private JMenu movePictureNodeJMenu
                                            = new JMenu( Settings.jpoResources.getString("moveNodeJMenuLabel") );
                                    
                                    
                                    /**
                                     *  menu item that allows the user to move the current node to the the first position of the owning group
                                     *
                                     **/
                                    private JMenuItem movePictureToTopJMenuItem
                                            = new JMenuItem( Settings.jpoResources.getString("movePictureToTopJMenuItem") );
                                    
                                    
                                    /**
                                     *  menu item that allows the user to move the current node up one position
                                     *
                                     **/
                                    private JMenuItem movePictureUpJMenuItem
                                            = new JMenuItem( Settings.jpoResources.getString("movePictureUpJMenuItem") );
                                    
                                    
                                    /**
                                     *  menu item that allows the user to move the current node down one position
                                     *
                                     **/
                                    private JMenuItem movePictureDownJMenuItem
                                            = new JMenuItem( Settings.jpoResources.getString("movePictureDownJMenuItem") );
                                    
                                    
                                    /**
                                     *  menu item that allows the user to move the current node to the the last position of the owning group
                                     *
                                     **/
                                    private JMenuItem movePictureToBottomJMenuItem
                                            = new JMenuItem( Settings.jpoResources.getString("movePictureToBottomJMenuItem") );
                                    
                                    
                                    /**
                                     *  menu item that allows indenting the group
                                     *
                                     **/
                                    private JMenuItem indentJMenuItem
                                            = new JMenuItem( Settings.jpoResources.getString("indentJMenuItem") );
                                    
                                    
                                    /**
                                     *  menu item that allows outdenting the group
                                     *
                                     **/
                                    private JMenuItem outdentJMenuItem
                                            = new JMenuItem( Settings.jpoResources.getString("outdentJMenuItem") );
                                    
                                    
                                    
                                    /**
                                     *  a separator for the Move menu. Declared here because other class methods want to turn on and off visible.
                                     */
                                    private JSeparator movePictureNodeSeparator = new JSeparator();
                                    
                                    
                                    /**
                                     *  This array of JMenuItems memorises the most recent drop locations and allows
                                     *  The user to quickly select a recently used drop location for the next drop.
                                     */
                                    private JMenuItem [] recentDropNodes = new JMenuItem[ Settings.maxDropNodes ];
                                    
                                    
                                    /**
                                     *  submenu which presents the user with commands to manipulate the file
                                     */
                                    private JMenu fileOperationsJMenu
                                            = new JMenu( Settings.jpoResources.getString("FileOperations") );
                                    
                                    
                                    /**
                                     *  menu item that allows the user to rename the file on the disk
                                     *
                                     **/
                                    private JMenuItem fileRenameJMenuItem
                                            = new JMenuItem( Settings.jpoResources.getString("fileRenameJMenuItem") );
                                    
                                    
                                    /**
                                     *  menu item that allows the image file to be deleted
                                     *
                                     **/
                                    private JMenuItem fileDeleteJMenuItem
                                            = new JMenuItem( Settings.jpoResources.getString("fileDeleteJMenuItem") );
                                    
                                    
                                    
                                    /**
                                     *  The node the popup menu was created for
                                     */
                                    private final SortableDefaultMutableTreeNode popupNode;
                                    
                                    
                                    /**
                                     *  Reference to the {@link ThumbnailBrowserInterface} which indicates the nodes being displayed.
                                     */
                                    private ThumbnailBrowserInterface mySetOfNodes = null;
                                    
                                    /**
                                     *  Index of the {@link #mySetOfNodes} being popped up.
                                     */
                                    private int index = 0;
                                    
                                    
                                    /**
                                     *  Reference to the picture viewer so that we can reposition.
                                     */
                                    private final PictureViewer pictureViewer;
                                    
                                    
                                    private SortableDefaultMutableTreeNode[] parentNodes = null;
                                    
                                    /**
                                     *   Constructor for the PicturePopupMenu where we do have a {@link PictureViewer} that should
                                     *   receive the picture.
                                     *
                                     *   @param  setOfNodes   The set of nodes from which the popup picture is coming
                                     *   @param  idx		The picture of the set for which the popup is being shown.
                                     *   @param  pictureViewer  the PictureViewer to notify
                                     */
                                    public PicturePopupMenu( ThumbnailBrowserInterface setOfNodes, int idx, PictureViewer pictureViewer  ) {
                                        Tools.log("PicturePopupMenu: constructor called with context.");
                                        this.mySetOfNodes = setOfNodes;
                                        this.index = idx;
                                        this.pictureViewer = pictureViewer;
                                        this.popupNode = mySetOfNodes.getNode( index );
                                        
                                        Settings.addRecentDropNodeListener( this );
                                        Settings.addCopyLocationsChangeListener( this );
                                        Settings.addUserFunctionsChangeListener( this );
                                        
                                        JMenuItem pictureShowJMenuItem
                                                = new JMenuItem( Settings.jpoResources.getString("pictureShowJMenuItemLabel") );
                                        pictureShowJMenuItem.addActionListener( new ActionListener() {
                                            public void actionPerformed( ActionEvent e ) {
                                                PictureViewer pictureViewer = new PictureViewer();
                                                if ( mySetOfNodes == null ) {
                                                    Tools.log("PicturePopupMenu.constructor: why does this PicturePopupMenu not know the context it is showing pictures in?");
                                                    mySetOfNodes = new SequentialBrowser( (SortableDefaultMutableTreeNode) popupNode.getParent() );
                                                    index = 0;
                                                    for ( int i=0; i <= mySetOfNodes.getNumberOfNodes(); i++ ) {
                                                        if ( mySetOfNodes.getNode( i ).equals( popupNode ) ) {
                                                            index = i;
                                                            i = mySetOfNodes.getNumberOfNodes() + 1;
                                                        }
                                                    }
                                                    //pictureViewer.changePicture( popupNode );
                                                }
                                                pictureViewer.changePicture( mySetOfNodes, index );
                                            }
                                        });
                                        add( pictureShowJMenuItem );
                                        
                                        
                                        parentNodes = Settings.pictureCollection.findParentGroups( popupNode );
                                        JMenu navigationJMenu = new JMenu( Settings.jpoResources.getString("navigationJMenu") );
                                        for ( int i = 0; i<parentNodes.length; i++ ) {
                                            JMenuItem navigateToRootNode = new JMenuItem( parentNodes[i].getUserObject().toString()  );
                                            final SortableDefaultMutableTreeNode targetNode = parentNodes[i];
                                            navigateToRootNode.addActionListener( new ActionListener() {
                                                final SortableDefaultMutableTreeNode node = targetNode;
                                                public void actionPerformed( ActionEvent e ) {
                                                    Settings.mainCollectionJTreeController.requestShowGroup( node );
                                                }
                                            });
                                            navigationJMenu.add( navigateToRootNode );
                                        }
                                        add( navigationJMenu );
                                        
                                        JMenuItem pictureEditJMenuItem
                                                = new JMenuItem( Settings.jpoResources.getString("pictureEditJMenuItemLabel") );
                                        pictureEditJMenuItem.addActionListener( new ActionListener() {
                                            public void actionPerformed( ActionEvent e ) {
                                                TreeNodeController.showEditGUI( popupNode );
                                            }
                                        });
                                        add( pictureEditJMenuItem );
                                        
                                        JMenuItem categoryUsagetJMenuItem
                                                = new JMenuItem( Settings.jpoResources.getString("categoryUsagetJMenuItem") );
                                        categoryUsagetJMenuItem.addActionListener( new ActionListener() {
                                            public void actionPerformed( ActionEvent e ) {
                                                if ( associatedPanel == null ) {
                                                    TreeNodeController.showCategoryUsageGUI( popupNode );
                                                } else {
                                                    if ( associatedPanel.countSelectedNodes() < 1 ) {
                                                        TreeNodeController.showCategoryUsageGUI( popupNode );
                                                    } else {
                                                        if ( ! associatedPanel.isSelected( popupNode ) ) {
                                                            associatedPanel.clearSelection();
                                                            TreeNodeController.showCategoryUsageGUI( popupNode );
                                                        } else {
                                                            CategoryUsageJFrame cujf = new CategoryUsageJFrame();
                                                            cujf.setSelection( associatedPanel.getSelectedNodesAsVector() );
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                        add( categoryUsagetJMenuItem );
                                        
                                        
                                        JMenuItem pictureMailSelectJMenuItem = new JMenuItem();
                                        JMenuItem pictureMailUnselectAllJMenuItem
                                                = new JMenuItem( Settings.jpoResources.getString("pictureMailUnselectAllJMenuItem") );
                                        if ( popupNode.getPictureCollection().isMailSelected( popupNode ) ) {
                                            pictureMailSelectJMenuItem.setText( Settings.jpoResources.getString("pictureMailUnselectJMenuItem") );
                                            pictureMailSelectJMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                        popupNode.getPictureCollection().removeFromMailSelection( popupNode );
                                                    } else {
                                                        Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                        SortableDefaultMutableTreeNode n;
                                                        while ( selection.hasMoreElements() ) {
                                                            n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                            if ( n.getUserObject() instanceof PictureInfo ) {
                                                                n.getPictureCollection().removeFromMailSelection( n );
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            add( pictureMailSelectJMenuItem );
                                            
                                            pictureMailUnselectAllJMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    popupNode.getPictureCollection().clearMailSelection();
                                                }
                                            });
                                            add( pictureMailUnselectAllJMenuItem );
                                        } else {
                                            pictureMailSelectJMenuItem.setText( Settings.jpoResources.getString("pictureMailSelectJMenuItem") );
                                            pictureMailSelectJMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                        popupNode.getPictureCollection().setMailSelected( popupNode );
                                                    } else {
                                                        Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                        SortableDefaultMutableTreeNode n;
                                                        while ( selection.hasMoreElements() ) {
                                                            n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                            if ( n.getUserObject() instanceof PictureInfo ) {
                                                                n.getPictureCollection().setMailSelected( n );
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            add( pictureMailSelectJMenuItem );
                                        }
                                        
                                        
                                        
                                        
                                        
                                        for ( int i = 0; i < Settings.maxUserFunctions; i++ ) {
                                            userFunctionsJMenuItems[ i ] = new JMenuItem();
                                            userFunctionsJMenuItems[ i ].addActionListener( this );
                                            userFunctionsJMenu.add( userFunctionsJMenuItems[ i ] );
                                        }
                                        add( userFunctionsJMenu );
                                        userFunctionsChanged();
                                        
                                        
                                        
                                        if ( popupNode.getPictureCollection().getAllowEdits() ) {
                                            add( rotationJMenu );
                                            rotate90JMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    popupNode.rotatePicture( 90 );
                                                }
                                            });
                                            rotate180JMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    popupNode.rotatePicture( 180 );
                                                }
                                            });
                                            rotate270JMenuItem.addActionListener(  new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    popupNode.rotatePicture( 270 );
                                                }
                                            });
                                            rotate0JMenuItem.addActionListener(  new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    popupNode.setPictureRotation( 0 );
                                                }
                                            });
                                            rotationJMenu.add( rotate90JMenuItem );
                                            rotationJMenu.add( rotate180JMenuItem );
                                            rotationJMenu.add( rotate270JMenuItem );
                                            rotationJMenu.add( rotate0JMenuItem );
                                        }
                                        
                                        
                                        JMenuItem pictureRefreshJMenuItem = new JMenuItem( Settings.jpoResources.getString("pictureRefreshJMenuItem") );
                                        pictureRefreshJMenuItem.addActionListener( new ActionListener() {
                                            public void actionPerformed( ActionEvent e ) {
                                                if ( associatedPanel == null ) {
                                                    popupNode.refreshThumbnail();
                                                } else if ( ! associatedPanel.isSelected( popupNode ) ) {
                                                    associatedPanel.clearSelection();
                                                    popupNode.refreshThumbnail();
                                                } else {
                                                    Object [] o = associatedPanel.getSelectedNodes();
                                                    for ( int i = 0; i < o.length; i++ ) {
                                                        ((SortableDefaultMutableTreeNode) o[i]).refreshThumbnail();
                                                    }
                                                }
                                            }
                                        });
                                        add( pictureRefreshJMenuItem );
                                        
                                        
                                        if ( popupNode.getPictureCollection().getAllowEdits() ) {
                                            add( movePictureNodeJMenu );
                                            
                                            for ( int i = 0; i < Settings.maxDropNodes; i++ ) {
                                                recentDropNodes[ i ] = new JMenuItem();
                                                recentDropNodes[ i ].addActionListener( this );
                                                movePictureNodeJMenu.add( recentDropNodes[ i ] );
                                            }
                                            movePictureNodeJMenu.add( movePictureNodeSeparator );
                                            recentDropNodesChanged();
                                            
                                            
                                            movePictureToTopJMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                        popupNode.moveNodeToTop();
                                                    } else {
                                                        Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                        SortableDefaultMutableTreeNode n;
                                                        while ( selection.hasMoreElements() ) {
                                                            n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                            if ( n.getUserObject() instanceof PictureInfo ) {
                                                                n.moveNodeToTop();
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            movePictureNodeJMenu.add( movePictureToTopJMenuItem );
                                            
                                            movePictureUpJMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                        popupNode.moveNodeUp();
                                                    } else {
                                                        Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                        SortableDefaultMutableTreeNode n;
                                                        while ( selection.hasMoreElements() ) {
                                                            n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                            if ( n.getUserObject() instanceof PictureInfo ) {
                                                                n.moveNodeUp();
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            movePictureNodeJMenu.add( movePictureUpJMenuItem );
                                            
                                            movePictureDownJMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                        popupNode.moveNodeDown();
                                                    } else {
                                                        Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                        SortableDefaultMutableTreeNode n;
                                                        while ( selection.hasMoreElements() ) {
                                                            n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                            if ( n.getUserObject() instanceof PictureInfo ) {
                                                                n.moveNodeDown();
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            movePictureNodeJMenu.add( movePictureDownJMenuItem );
                                            
                                            movePictureToBottomJMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                        popupNode.moveNodeToBottom();
                                                    } else {
                                                        Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                        SortableDefaultMutableTreeNode n;
                                                        while ( selection.hasMoreElements() ) {
                                                            n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                            if ( n.getUserObject() instanceof PictureInfo ) {
                                                                n.moveNodeToBottom();
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            movePictureNodeJMenu.add( movePictureToBottomJMenuItem );
                                            
                                            indentJMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                        popupNode.indentNode();
                                                    } else {
                                                        Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                        SortableDefaultMutableTreeNode n;
                                                        while ( selection.hasMoreElements() ) {
                                                            n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                            if ( n.getUserObject() instanceof PictureInfo ) {
                                                                TreeNodeController.copyToNewLocation( n );
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            movePictureNodeJMenu.add( indentJMenuItem );
                                            
                                            outdentJMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                        popupNode.outdentNode();
                                                    } else {
                                                        Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                        SortableDefaultMutableTreeNode n;
                                                        while ( selection.hasMoreElements() ) {
                                                            n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                            if ( n.getUserObject() instanceof PictureInfo ) {
                                                                TreeNodeController.copyToNewLocation( n );
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            movePictureNodeJMenu.add( outdentJMenuItem );
                                            
                                        }
                                        
                                        
                                        
                                        add( copyImageJMenu );
                                        
                                        copyToNewLocationJMenuItem.addActionListener( new ActionListener() {
                                            public void actionPerformed( ActionEvent e ) {
                                                if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                    TreeNodeController.copyToNewLocation( popupNode );
                                                } else {
                                                    Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                    SortableDefaultMutableTreeNode n;
                                                    while ( selection.hasMoreElements() ) {
                                                        n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                        if ( n.getUserObject() instanceof PictureInfo ) {
                                                            TreeNodeController.copyToNewLocation( n );
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                        copyImageJMenu.add( copyToNewLocationJMenuItem );
                                        
                                        copyImageJMenu.addSeparator();
                                        
                                        
                                        
                                        for ( int i = 0; i < Settings.maxCopyLocations; i++ ) {
                                            copyLocationsJMenuItems[ i ] = new JMenuItem();
                                            copyLocationsJMenuItems[ i ].addActionListener( this );
                                            copyImageJMenu.add( copyLocationsJMenuItems[ i ] );
                                        }
                                        copyLocationsChanged();
                                        
                                        if ( popupNode.getPictureCollection().getAllowEdits() ) {
                                            
                                            pictureNodeRemove.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                        popupNode.deleteNode();
                                                        
                                                    } else {
                                                        Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                        SortableDefaultMutableTreeNode n;
                                                        while ( selection.hasMoreElements() ) {
                                                            n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                            if ( n.getUserObject() instanceof PictureInfo ) {
                                                                n.deleteNode();
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            add( pictureNodeRemove );
                                            
                                            
                                            add( fileOperationsJMenu );
                                            
                                            fileRenameJMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                        TreeNodeController.fileRename( popupNode );
                                                    } else {
                                                        Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                        SortableDefaultMutableTreeNode n;
                                                        while ( selection.hasMoreElements() ) {
                                                            n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                            if ( n.getUserObject() instanceof PictureInfo ) {
                                                                TreeNodeController.fileRename( n );
                                                            }
                                                        }
                                                    }
                                                }
                                            });
                                            fileOperationsJMenu.add( fileRenameJMenuItem );
                                            
                                            fileDeleteJMenuItem.addActionListener( new ActionListener() {
                                                public void actionPerformed( ActionEvent e ) {
                                                    if (( associatedPanel == null ) || ( associatedPanel.countSelectedNodes() < 1 ) ){
                                                        popupNode.fileDelete();
                                                    } else {
                                                        Enumeration selection = associatedPanel.getSelectedNodesAsVector().elements();
                                                        SortableDefaultMutableTreeNode n;
                                                        while ( selection.hasMoreElements() ) {
                                                            n = (SortableDefaultMutableTreeNode) selection.nextElement();
                                                            if ( n.getUserObject() instanceof PictureInfo ) {
                                                                n.fileDelete();
                                                            }
                                                        }
                                                        associatedPanel.clearSelection();
                                                    }
                                                }
                                            });
                                            fileOperationsJMenu.add( fileDeleteJMenuItem );
                                        }
                                    }
                                    
                                    
                                    
                                    
                                    /**
                                     *  Here we receive notification that the nodes have been updated. The method
                                     *  then updates the menu items for the drop targets. Those that are null are
                                     *  not shown. If no drop targets are shown at all the seerator in the submenu
                                     *  is not shown either.
                                     */
                                    public void recentDropNodesChanged() {
                                        boolean dropNodesVisible = false;
                                        for ( int i = 0; i < Settings.maxDropNodes; i++ ) {
                                            if ( Settings.recentDropNodes[ i ] != null ) {
                                                recentDropNodes[ i ].setText(
                                                        Settings.jpoResources.getString("recentDropNodePrefix")
                                                        + Settings.recentDropNodes[ i ].toString() );
                                                recentDropNodes[ i ].setVisible( true );
                                                dropNodesVisible = true;
                                            } else {
                                                recentDropNodes[ i ].setVisible( false );
                                            }
                                        }
                                        if ( dropNodesVisible ) {
                                            movePictureNodeSeparator.setVisible( true );
                                        } else {
                                            movePictureNodeSeparator.setVisible( false );
                                        }
                                    }
                                    
                                    
                                    /**
                                     *  Here we receive notification that the copy locations were updated and then go
                                     *  and update the targets on the menu.
                                     */
                                    public void copyLocationsChanged() {
                                        for ( int i = 0; i < Settings.maxCopyLocations; i++ ) {
                                            if ( Settings.copyLocations[ i ] != null ) {
                                                copyLocationsJMenuItems[ i ].setText( Settings.copyLocations[ i ] );
                                                copyLocationsJMenuItems[ i ].setVisible( true );
                                            } else {
                                                copyLocationsJMenuItems[ i ].setVisible( false );
                                            }
                                        }
                                    }
                                    
                                    
                                    /**
                                     *  Here we receive notification that the user Functions were updated and then go
                                     *  and update the menu.
                                     */
                                    public void userFunctionsChanged() {
                                        for ( int i = 0; i < Settings.maxUserFunctions; i++ ) {
                                            if ( ( Settings.userFunctionNames[i] != null )
                                            && ( Settings.userFunctionNames[i].length() > 0 )
                                            && ( Settings.userFunctionCmd[i] != null )
                                            && ( Settings.userFunctionCmd[i].length() > 0 ) ) {
                                                userFunctionsJMenuItems[ i ].setText( Settings.userFunctionNames[i] );
                                                userFunctionsJMenuItems[ i ].setVisible( true );
                                            } else {
                                                userFunctionsJMenuItems[ i ].setVisible( false );
                                            }
                                        }
                                    }
                                    
                                    
                                    
                                    /**
                                     *  The PicturePopupMenu routes menu click events for the recent drop node, copy locations
                                     *  and user Functions here for processing. These are all array based lists.
                                     **/
                                    public void actionPerformed( ActionEvent e ) {
                                        //  was a recentDropNode picked?
                                        for ( int i = 0; i < Settings.maxDropNodes; i++ ) {
                                            if ( ( recentDropNodes[ i ] != null )
                                            && ( e.getSource().hashCode() == recentDropNodes[ i ].hashCode() ) ) {
                                                
                                                SortableDefaultMutableTreeNode nextChild =
                                                        (SortableDefaultMutableTreeNode) ((SortableDefaultMutableTreeNode) popupNode.getParent()).getChildAfter( popupNode );
                                                
                                                popupNode.moveToNode( Settings.recentDropNodes[ i ] );
                                                Settings.memorizeGroupOfDropLocation( Settings.recentDropNodes[ i ] );
                                                
                                                if ( pictureViewer != null ) {
                                                    if ( mySetOfNodes == null ) {
                                                        Tools.log("PicturePopupMenu.constructor: why does this PicturePopupMenu not know the context it is showing pictures in?");
                                                        mySetOfNodes = new SequentialBrowser( (SortableDefaultMutableTreeNode) nextChild.getParent() );
                                                        index = 0;
                                                        for ( int j=0; j <= mySetOfNodes.getNumberOfNodes(); j++ ) {
                                                            if ( mySetOfNodes.getNode( i ).equals( nextChild ) ) {
                                                                index = j;
                                                                j = mySetOfNodes.getNumberOfNodes() + 1;
                                                            }
                                                        }
                                                    }
                                                    pictureViewer.changePicture( mySetOfNodes, index );
                                                }
                                                return;
                                            }
                                        }
                                        
                                        //  was a copyLocationsJMenuItems item picked?
                                        for ( int i = 0; i < Settings.maxCopyLocations; i++ ) {
                                            if ( ( copyLocationsJMenuItems[ i ] != null )
                                            && ( e.getSource().hashCode() == copyLocationsJMenuItems[ i ].hashCode() ) ) {
                                                popupNode.validateAndCopyPicture( new File( Settings.copyLocations[ i ] ) );
                                                Settings.memorizeCopyLocation( Settings.copyLocations[ i ] );
                                                return;
                                            }
                                        }
                                        
                                        //  was a userFunctionsJMenuItems item chosen?
                                        for ( int i = 0; i < Settings.maxUserFunctions; i++ ) {
                                            if ( ( userFunctionsJMenuItems[ i ] != null )
                                            && ( e.getSource().hashCode() == userFunctionsJMenuItems[ i ].hashCode() ) ) {
                                                try {
                                                    Tools.runUserFunction( i, (PictureInfo) popupNode.getUserObject() );
                                                } catch ( ClassCastException x ) {
                                                    // Well, it was the wrong type
                                                } catch ( NullPointerException x ) {
                                                    // Well, it wasn't a good node anyway.
                                                }
                                                return;
                                            }
                                        }
                                    }
                                    
                                    
                                    /**
                                     *  This reference allows us to find out whether there is a selection in operation
                                     */
                                    private ThumbnailJScrollPane associatedPanel = null;
                                    
                                    /**
                                     * here you can pass in the ThumbnailJScrollPane that would hold a selection object
                                     * in case the popup function is supposed to work on multiple selected images.
                                     */
                                    public void setSelection( ThumbnailJScrollPane associatedPanel ) {
                                        this.associatedPanel = associatedPanel;
                                    }
                                    
}
