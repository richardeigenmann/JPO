package jpo.gui;

import jpo.dataModel.RecentFilesChangeListener;
import jpo.dataModel.Settings;
import jpo.dataModel.SortableDefaultMutableTreeNode;
import jpo.dataModel.RecentDropNodeListener;
import jpo.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import jpo.dataModel.Tools;

/*
GroupPopupMenu.java: popup menu for groups

Copyright (C) 2002-2009  Richard Eigenmann.
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
 * A class that generates a popup menu for a group node. This became nescessary primarily because
 * the code was getting a bit long and was clutterin up a different class. Seperating out the 
 * popup menu and making it an object and forcing an interface on the object instantiating
 * it is propbably more in line with the OO philosophy.
 *
 */
public class GroupPopupMenu extends JPopupMenu
        implements RecentDropNodeListener, RecentFilesChangeListener {

    /**
     *   An array of recently opened collections.
     */
    private final JMenuItem[] recentOpenedfileJMenuItem = new JMenuItem[Settings.recentFiles];
    /**
     *  a separator for the Move menu
     */
    private JSeparator movePictureNodeSeparator = new JSeparator();
    /**
     *  menu items for the recently dropped group nodes
     */
    private JMenuItem[] recentDropNodes = new JMenuItem[Settings.maxDropNodes];
    /**
     *  object that must implement the functions dealing with the user
     *  request
     */
    private GroupPopupInterface caller;
    /**
     *  the node we are doing the popup menu for
     */
    private final SortableDefaultMutableTreeNode popupNode;

    /**
     *   Creates a popup menu for a group.
     *   @param  caller   the caller that will get the requests to do things
     *   @param  node 	the node for which the popup menu is being created.
     */
    public GroupPopupMenu(final GroupPopupInterface caller, final SortableDefaultMutableTreeNode node) {
        this.caller = caller;
        this.popupNode = node;
        Runnable r = new Runnable() {

            public void run() {
                initComponents();
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            SwingUtilities.invokeLater(r);
        }

    }

    private void initComponents() {
        JMenuItem groupShowJMenuItem = new JMenuItem(Settings.jpoResources.getString("groupShowJMenuItem"));
        groupShowJMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                caller.requestShowGroup(popupNode);
            }
        });
        add(groupShowJMenuItem);

        JMenuItem groupSlideshowJMenuItem = new JMenuItem(Settings.jpoResources.getString("groupSlideshowJMenuItem"));
        groupSlideshowJMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                caller.requestSlideshow();
            }
        });
        add(groupSlideshowJMenuItem);

        JMenuItem groupFindJMenuItem = new JMenuItem(Settings.jpoResources.getString("groupFindJMenuItemLabel"));
        groupFindJMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                caller.requestFind();
            }
        });
        add(groupFindJMenuItem);

        addSeparator();

        if (popupNode.getPictureCollection().getAllowEdits()) {

            JMenuItem groupEditJMenuItem = new JMenuItem(Settings.jpoResources.getString("groupEditJMenuItem"));
            groupEditJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestEditGroupNode();
                }
            });
            add(groupEditJMenuItem);

            JMenuItem categoryUsagetJMenuItem = new JMenuItem(Settings.jpoResources.getString("categoryUsagetJMenuItem"));
            categoryUsagetJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.showCategoryUsageGUI();
                }
            });
            add(categoryUsagetJMenuItem);


            JMenuItem groupRefreshJMenuItem = new JMenuItem(Settings.jpoResources.getString("groupRefreshJMenuItem"));
            groupRefreshJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    popupNode.refreshThumbnail();
                }
            });
            add(groupRefreshJMenuItem);

            addSeparator();

            JMenuItem groupTableJMenuItem = new JMenuItem(Settings.jpoResources.getString("groupTableJMenuItemLabel"));
            groupTableJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestEditGroupTable();
                }
            });
            add(groupTableJMenuItem);

            addSeparator();

            //submenu which has several navigation options
            JMenu addGroupJMenu = new JMenu(Settings.jpoResources.getString("addGroupJMenuLabel"));
            add(addGroupJMenu);

            // menu item that allows adding a new blank group
            JMenuItem addNewGroupJMenuItem = new JMenuItem(Settings.jpoResources.getString("addNewGroupJMenuItemLabel"));
            addNewGroupJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestAddGroup();
                }
            });
            addGroupJMenu.add(addNewGroupJMenuItem);

            JMenuItem addPicturesJMenuItem = new JMenuItem(Settings.jpoResources.getString("addPicturesJMenuItemLabel"));
            addPicturesJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestAdd();
                }
            });
            addGroupJMenu.add(addPicturesJMenuItem);

            // Add Collections
            // submenu which offers the choice of either loading from a file or from one of the recent collections
            JMenu addCollectionJMenu = new JMenu(Settings.jpoResources.getString("addCollectionJMenuItemLabel"));
            addGroupJMenu.add(addCollectionJMenu);

            // menu item that allows adding a collection of pictures
            JMenuItem addCollectionFormFile = new JMenuItem(Settings.jpoResources.getString("addCollectionFormFile"));
            addCollectionFormFile.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestAddCollection();
                }
            });
            addCollectionJMenu.add(addCollectionFormFile);

            // add the recently opened files to the menu
            for (int i = 0; i < Settings.recentFiles; i++) {
                recentOpenedfileJMenuItem[i] = new JMenuItem();
                final int index = i;  // the anonymous innter class needs a final variable
                recentOpenedfileJMenuItem[i].addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        caller.requestAddCollection(new File(Settings.recentCollections[index]));
                    }
                });
                recentOpenedfileJMenuItem[i].setVisible(false);
                addCollectionJMenu.add(recentOpenedfileJMenuItem[i]);
            }
            //Settings.addRecentFilesChangeListener( this );  // not needed as Group Popups are created on demand
            recentFilesChanged();


            // menu item that allows adding from a list of filenames
            JMenuItem addFlatFileJMenuItem = new JMenuItem(Settings.jpoResources.getString("addFlatFileJMenuItemLabel"));
            addFlatFileJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    TreeNodeController.addFlatFile(popupNode);
                }
            });
            addGroupJMenu.add(addFlatFileJMenuItem);

            // submenu which has several navigation options
            JMenu moveGroupNodeJMenu = new JMenu(Settings.jpoResources.getString("moveNodeJMenuLabel"));
            add(moveGroupNodeJMenu);

            for (int i = 0; i < Settings.maxDropNodes; i++) {
                recentDropNodes[i] = new JMenuItem();
                recentDropNodes[i].addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                });
                moveGroupNodeJMenu.add(recentDropNodes[i]);
            }
            moveGroupNodeJMenu.add(movePictureNodeSeparator);
            recentDropNodesChanged();

            //menu item that allows move to top op list
            JMenuItem moveGroupToTopJMenuItem = new JMenuItem(Settings.jpoResources.getString("moveGroupToTopJMenuItem"));
            moveGroupToTopJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestMoveGroupToTop();
                }
            });
            moveGroupNodeJMenu.add(moveGroupToTopJMenuItem);

            // menu item that allows move up in the list
            JMenuItem moveGroupUpJMenuItem = new JMenuItem(Settings.jpoResources.getString("moveGroupUpJMenuItem"));
            moveGroupUpJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestMoveGroupUp();
                }
            });
            moveGroupNodeJMenu.add(moveGroupUpJMenuItem);

            //menu item that allows move up in the list
            JMenuItem moveGroupDownJMenuItem = new JMenuItem(Settings.jpoResources.getString("moveGroupDownJMenuItem"));
            moveGroupDownJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestMoveGroupDown();
                }
            });
            moveGroupNodeJMenu.add(moveGroupDownJMenuItem);

            // menu item that allows move to top op list
            JMenuItem moveGroupToBottomJMenuItem = new JMenuItem(Settings.jpoResources.getString("moveGroupToBottomJMenuItem"));
            moveGroupToBottomJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestMoveGroupToBottom();
                }
            });
            moveGroupNodeJMenu.add(moveGroupToBottomJMenuItem);
//menu item that allows indenting the group
            JMenuItem indentJMenuItem = new JMenuItem(Settings.jpoResources.getString("indentJMenuItem"));
            indentJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    popupNode.indentNode();
                }
            });
            moveGroupNodeJMenu.add(indentJMenuItem);

            // menu item that allows outdenting the group
            JMenuItem outdentJMenuItem = new JMenuItem(Settings.jpoResources.getString("outdentJMenuItem"));
            outdentJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    popupNode.outdentNode();
                }
            });
            moveGroupNodeJMenu.add(outdentJMenuItem);

            JMenuItem groupRemove = new JMenuItem(Settings.jpoResources.getString("groupRemoveLabel"));
            groupRemove.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestGroupRemove();
                }
            });
            add(groupRemove);

            addSeparator();

            // menu item that brings a dialog to ask where to consolidate the files to
            JMenuItem consolidateMoveJMenuItem = new JMenuItem(Settings.jpoResources.getString("consolidateMoveLabel"));
            consolidateMoveJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestConsolidateGroup();
                }
            });
            add(consolidateMoveJMenuItem);

            addSeparator();
            //submenu which has several sort options
            JMenu sortJMenu = new JMenu(Settings.jpoResources.getString("sortJMenu"));
            add(sortJMenu);
            //requests a sort by Description
            JMenuItem sortByDescriptionJMenuItem = new JMenuItem(Settings.jpoResources.getString("sortByDescriptionJMenuItem"));
            sortByDescriptionJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestSort(Settings.DESCRIPTION);
                }
            });
            sortJMenu.add(sortByDescriptionJMenuItem);

            //requests a sort by Film Reference
            JMenuItem sortByFilmReferenceJMenuItem = new JMenuItem(Settings.jpoResources.getString("sortByFilmReferenceJMenuItem"));
            sortByFilmReferenceJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestSort(Settings.FILM_REFERENCE);
                }
            });
            sortJMenu.add(sortByFilmReferenceJMenuItem);

            //requests a sort by Creation Time
            JMenuItem sortByCreationTimeJMenuItem = new JMenuItem(Settings.jpoResources.getString("sortByCreationTimeJMenuItem"));
            sortByCreationTimeJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestSort(Settings.CREATION_TIME);
                }
            });
            sortJMenu.add(sortByCreationTimeJMenuItem);

            // requests a sort by Comment
            JMenuItem sortByCommentJMenuItem = new JMenuItem(Settings.jpoResources.getString("sortByCommentJMenuItem"));
            sortByCommentJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestSort(Settings.COMMENT);
                }
            });
            sortJMenu.add(sortByCommentJMenuItem);

            // requests a sort by Photographer
            JMenuItem sortByPhotographerJMenuItem = new JMenuItem(Settings.jpoResources.getString("sortByPhotographerJMenuItem"));
            sortByPhotographerJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestSort(Settings.PHOTOGRAPHER);
                }
            });
            sortJMenu.add(sortByPhotographerJMenuItem);

            // requests a sort by Copyright Holder
            JMenuItem sortByCopyrightHolderTimeJMenuItem = new JMenuItem(Settings.jpoResources.getString("sortByCopyrightHolderTimeJMenuItem"));
            sortByCopyrightHolderTimeJMenuItem.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    caller.requestSort(Settings.COPYRIGHT_HOLDER);
                }
            });
            sortJMenu.add(sortByCopyrightHolderTimeJMenuItem);

            addSeparator();
        }

        JMenuItem groupExportHtml = new JMenuItem(Settings.jpoResources.getString("groupExportHtmlMenuText"));
        groupExportHtml.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                caller.requestGroupExportHtml();
            }
        });
        add(groupExportHtml);

        //  menu item that allows the user to export the group to several different formats
        JMenuItem groupExportNewCollection = new JMenuItem(Settings.jpoResources.getString("groupExportNewCollectionMenuText"));
        groupExportNewCollection.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                caller.requestGroupExportNewCollection();
            }
        });
        add(groupExportNewCollection);

        /* Disabled because it's not thought through and the resulting jar file is
        too difficult to load.
        // menu item that allows the user to export the group to several different formats
        JMenuItem groupExportJar = new JMenuItem(Settings.jpoResources.getString("groupExportJarMenuText"));
        groupExportJar.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
        caller.requestGroupExportJar();
        }
        });

        add(groupExportJar);
         */

        // menu item that allows the user to export the group to a flat list of filenames
        JMenuItem groupExportFlatFile = new JMenuItem(Settings.jpoResources.getString("groupExportFlatFileMenuText"));
        groupExportFlatFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                caller.requestGroupExportFlatFile();
            }
        });
        add(groupExportFlatFile);

    }

    /**
     *  Here we receive notification that the nodes have been updated
     */
    public void recentDropNodesChanged() {
        boolean dropNodesVisible = false;
        for (int i = 0; i < Settings.maxDropNodes; i++) {
            if ((Settings.recentDropNodes[i] != null)) {
                recentDropNodes[i].setText("To Group: " + Settings.recentDropNodes[i].toString());
                recentDropNodes[i].setVisible(true);
                dropNodesVisible = true;
            } else {
                recentDropNodes[i].setVisible(false);
            }
        }
        if (dropNodesVisible) {
            movePictureNodeSeparator.setVisible(true);
        } else {
            movePictureNodeSeparator.setVisible(false);
        }

    }

    /**
     *  checks if the event object is one of the drop nodes
     *  @return returns true if the object was found in the list and the action was submitted.
     */
    private boolean checkDropNodes(Object o) {
        for (int i = 0; i < Settings.maxDropNodes; i++) {
            if ((recentDropNodes[i] != null) && (o.hashCode() == recentDropNodes[i].hashCode())) {
                caller.requestMoveToNode(Settings.recentDropNodes[i]);
                Settings.memorizeGroupOfDropLocation(Settings.recentDropNodes[i]);
                return true;
            }
        }
        return false;
    }

    /**
     *  Sets up the menu entries in the File|OpenRecent sub menu from the recentCollections
     *  in Settings. Can be called by the interface from the listener on the Settings object.
     */
    public void recentFilesChanged() {
        Tools.checkEDT();
        for (int i = 0; i < Settings.recentCollections.length; i++) {
            if (Settings.recentCollections[i] != null) {
                recentOpenedfileJMenuItem[i].setText(Integer.toString(i + 1) + ": " + Settings.recentCollections[i]);
                recentOpenedfileJMenuItem[i].setVisible(true);
            } else {
                recentOpenedfileJMenuItem[i].setVisible(false);
            }
        }
    }
}
