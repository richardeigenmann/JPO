package org.jpo.datamodel;

import org.jpo.cache.ThumbnailCreationQueue;
import org.jpo.eventbus.JpoEventBus;
import org.jpo.eventbus.RecentCollectionsChangedEvent;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * Copyright (C) 2006 - 2020 Richard Eigenmann, Zurich, Switzerland This program
 * is free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place
 * - Suite 330, Boston, MA 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * Information about the collection and owner of the treemodel
 */
public class PictureCollection {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PictureCollection.class.getName());

    /**
     * Constructs a new PictureCollection object with a root object
     */
    public PictureCollection() {
        setRootNode(new SortableDefaultMutableTreeNode());
        treeModel = new DefaultTreeModel(getRootNode());
        categories = new HashMap<>();
        mailSelection = new ArrayList<>();
        setAllowEdits(true);
        setUnsavedUpdates(false);
    }

    /**
     * This method wipes out the data in the picture collection. As it updates
     * the TreeModel it has been made synchronous on the EDT.
     */
    public void clearCollection() {
        Runnable runnable = () -> {
            getRootNode().removeAllChildren();
            getRootNode().setUserObject(new GroupInfo(Settings.jpoResources.getString("DefaultRootNodeText")));
            clearQueriesTreeModel();
            categories.clear();
            clearMailSelection();
            setAllowEdits(true);
            setUnsavedUpdates(false);
            setXmlFile(null);
            getTreeModel().reload();
            Settings.recentDropNodes.clear();
            ThumbnailCreationQueue.clear();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (InterruptedException | InvocationTargetException ex) {
                // Restore interrupted state...
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * This variable refers to the tree model.
     */
    private final DefaultTreeModel treeModel;

    /**
     * The DefaultTreeModel allows notification of tree change events to
     * listening objects.
     *
     * @return The tree Model
     */
    public DefaultTreeModel getTreeModel() {
        return (treeModel);
    }

    /**
     * controls whether updates should be fired from add, delete, insert methods
     */
    private boolean sendModelUpdates = true;

    /**
     * Returns true if edits are allowed on this collection
     *
     * @return true if edits are allowed, false if not
     */
    public synchronized boolean getSendModelUpdates() {
        return sendModelUpdates;
    }

    /**
     * Sets the flag whether to send model updates or not
     *
     * @param status the new flag value
     */
    public synchronized void setSendModelUpdates(boolean status) {
        sendModelUpdates = status;
    }

    /**
     * This method sends a nodeStructureChanged event through to the listeners
     * of the Collection's model
     *
     * @param changedNode The node that was changed
     */
    public void sendNodeStructureChanged(
            final TreeNode changedNode) {
        LOGGER.log(Level.FINE, "Sending a node structure change on node: {0}", changedNode);
        if (SwingUtilities.isEventDispatchThread()) {
            getTreeModel().nodeStructureChanged(changedNode);
        } else {
            SwingUtilities.invokeLater(
                    () -> getTreeModel().nodeStructureChanged(changedNode)
            );
        }
    }

    /**
     * This method sends a nodeChanged event through to the listeners of the
     * Collection's model. It makes sure the event is sent on the EDT
     *
     * @param changedNode The node that was changed
     */
    public void sendNodeChanged(
            final TreeNode changedNode) {
        LOGGER.log(Level.FINE, "Sending a node change on node: {0}", changedNode);
        if (SwingUtilities.isEventDispatchThread()) {
            getTreeModel().nodeChanged(changedNode);
        } else {
            SwingUtilities.invokeLater(
                    () -> getTreeModel().nodeChanged(changedNode)
            );
        }
    }

    /**
     * This method sends a nodesWereInserted event through to the listeners of
     * the Collection's model.
     * <p>
     * TODO: why does this method not ensure this is happening on the EDT?
     *
     * @param changedNode  The node that was inserted
     * @param childIndices The Child indices
     */
    public void sendNodesWereInserted(
            final TreeNode changedNode,
            final int[] childIndices) {
        LOGGER.log(Level.FINE, "Sending a node was inserted notification on node: {0}", changedNode);
        getTreeModel().nodesWereInserted(changedNode, childIndices);
    }

    /**
     * This method sends a nodesWereRemoved event through to the listeners of
     * the Collection's model. It makes sure the event is sent on the EDT
     *
     * @param node            parent node
     * @param childIndices    The Child indices
     * @param removedChildren the removed nodes
     */
    public void sendNodesWereRemoved(final TreeNode node,
                                     final int[] childIndices,
                                     final Object[] removedChildren) {
        LOGGER.log(Level.FINE, "Sending a node was removed change on node: {0}", node);
        if (SwingUtilities.isEventDispatchThread()) {
            getTreeModel().nodesWereRemoved(node, childIndices, removedChildren);
        } else {
            SwingUtilities.invokeLater(
                    () -> getTreeModel().nodesWereRemoved(node, childIndices, removedChildren));
        }
    }

    /**
     * The root node of the tree data model. It holds all the branches to the
     * groups and pictures
     */
    private SortableDefaultMutableTreeNode rootNode;

    /**
     * This method returns the root node of the collection
     *
     * @return the root node
     */
    public SortableDefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    /**
     * This method sets the root node of the collection
     *
     * @param rootNode The root node
     */
    private void setRootNode(SortableDefaultMutableTreeNode rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * This variable indicates whether uncommitted changes exist for this
     * collection. Care should be taken when adding removing or changing nodes
     * to update this flag. It should be queried before exiting the application.
     * Also when a new collection is loaded this flag should be checked so as
     * not to loose modifications. This flag should be set only on the root
     * node.
     *
     * @see #setUnsavedUpdates()
     * @see #setUnsavedUpdates(boolean)
     * @see #getUnsavedUpdates()
     */
    private boolean unsavedUpdates; // default is false

    /**
     * This method marks the root node of the tree as having unsaved updates.
     *
     * @see #unsavedUpdates
     */
    public void setUnsavedUpdates() {
        setUnsavedUpdates(true);
    }

    /**
     * This method allows the programmer to set whether the tree has unsaved
     * updates or not.
     *
     * @param unsavedUpdates Set to true if there are unsaved updates, false if
     *                       there are none
     * @see #unsavedUpdates
     */
    public void setUnsavedUpdates(boolean unsavedUpdates) {
        this.unsavedUpdates = unsavedUpdates;
    }

    /**
     * This method returns true is the tree has unsaved updates, false if it has
     * none
     *
     * @return true if there are unsaved updates, false if there are none
     * @see #unsavedUpdates
     */
    public boolean getUnsavedUpdates() {
        return unsavedUpdates;
    }

    /**
     * This flag controls whether this collection can be edited. This is queried
     * by several menus and will restrict the options a use has if it returns
     * true.
     */
    private boolean allowEdits;

    /**
     * Returns true if edits are allowed on this collection
     *
     * @return true if edits are allowed on this collection
     */
    public boolean getAllowEdits() {
        return allowEdits;
    }

    /**
     * sets the allow edit allowedEdits of this collection
     *
     * @param allowedEdits pass true to allow edits, false to forbid
     */
    public void setAllowEdits(boolean allowedEdits) {
        allowEdits = allowedEdits;
    }

    /**
     * This variable holds the reference to the queries executed against the
     * collection.
     */
    private DefaultTreeModel queriesTreeModel;

    /**
     * Call this method when you need the TreeModel for the queries
     *
     * @return The treemodel of the queries
     */
    public DefaultTreeModel getQueriesTreeModel() {
        if (queriesTreeModel == null) {
            createQueriesTreeModel();
        }
        return (queriesTreeModel);
    }

    /**
     * Call this method when you need the root Node for the queries
     *
     * @return the root node
     */
    public DefaultMutableTreeNode getQueriesRootNode() {
        return ((DefaultMutableTreeNode) getQueriesTreeModel().getRoot());
    }

    /**
     * Call this method when you need to set the TreeModel for the queries
     *
     * @param defaultTreeModel the tree model
     */
    public void setQueriesTreeModel(DefaultTreeModel defaultTreeModel) {
        queriesTreeModel = defaultTreeModel;
    }

    /**
     * Call this method when you need to create a new TreeModel for the queries.
     */
    public void createQueriesTreeModel() {
        setQueriesTreeModel(new DefaultTreeModel(new DefaultMutableTreeNode(Settings.jpoResources.getString("queriesTreeModelRootNode"))));

        DefaultMutableTreeNode byYearsTreeNode = new DefaultMutableTreeNode("By Year");
        rememberYearsTreeNode(byYearsTreeNode);
        getQueriesRootNode().add(byYearsTreeNode);
    }

    private DefaultMutableTreeNode yearsTreeNode;

    /**
     * Remembers the node on which the years were added
     *
     * @param node The node
     */
    private void rememberYearsTreeNode(DefaultMutableTreeNode node) {
        yearsTreeNode = node;
    }

    /**
     * Node for the Years tree
     *
     * @return the node
     */
    public DefaultMutableTreeNode getYearsTreeNode() {
        return yearsTreeNode;
    }

    /**
     * Adds a year query
     *
     * @param year the year
     */
    public void addYearQuery(String year) {
        YearQuery yearQuery = new YearQuery(year);
        yearQuery.setStartNode(getRootNode());
        getYearsTreeNode().add(new DefaultMutableTreeNode(yearQuery));
    }

    /**
     * Clear out the nodes in the existing queries Tree Model
     */
    public void clearQueriesTreeModel() {
        Tools.checkEDT();
        getQueriesRootNode().removeAllChildren();
    }

    /**
     * Adds a query to the Query Tree Model.
     *
     * @param query The new Query to add
     */
    public void addQueryToTreeModel(final Query query) {
        Tools.checkEDT();
        final DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(query);
        getQueriesRootNode().add(newNode);
        queriesTreeModel.nodesWereInserted(getQueriesRootNode(), new int[]{getQueriesRootNode().getIndex(newNode)});
    }

    /**
     * This HashMap holds the categories that will be available for this
     * collection. It is only populated on the root node.
     */
    private final HashMap<Integer, String> categories;


    /**
     * This adds a category to the HashMap
     *
     * @param index    The index
     * @param category The category
     */
    public void addCategory(Integer index, String category) {
        categories.put(index, category);

        // add a new CategoryQuery to the Searches tree
        final CategoryQuery categoryQuery = new CategoryQuery(index);
        SwingUtilities.invokeLater(
                () -> addQueryToTreeModel(categoryQuery)
        );
    }

    /**
     * This adds a category to the HashMap
     *
     * @param category The category
     * @return the number at which the category was added
     */
    public Integer addCategory(String category) {
        Integer key = null;
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            key = i;
            if (!categories.containsKey(key)) {
                break;
            }
        }
        addCategory(key, category);
        return key;
    }

    /**
     * Renames a category in the HashMap
     *
     * @param key      The Key
     * @param category The category
     */
    public void renameCategory(Integer key, String category) {
        removeCategory(key);
        addCategory(key, category);
    }

    /**
     * Returns an iterator through the categories keys
     *
     * @return an iterator over the categories keys
     */
    public Iterator<Integer> getCategoryIterator() {
        return categories.keySet().iterator();
    }

    /**
     * Returns a set of of category keys
     *
     * @return an set of category keys
     */
    public Set<Integer> getCategoryKeySet() {
        return categories.keySet();
    }

    /**
     * Returns the Value for the key
     *
     * @param key the key for the value to be returned-
     * @return Returns the Value for the Key
     */
    public String getCategory(Integer key) {
        return categories.get(key);
    }

    /**
     * Removes the category associated with the
     *
     * @param key The Key to be removed
     */
    public void removeCategory(Integer key) {
        categories.remove(key);
    }

    /**
     * Counts the number of nodes using the category
     *
     * @param key       The Key
     * @param startNode the node to start from
     * @return the number of nodes
     */
    public static int countCategoryUsage(Object key,
                                         SortableDefaultMutableTreeNode startNode) {
        final Enumeration<TreeNode> nodes = startNode.children();
        int count = 0;
        SortableDefaultMutableTreeNode n;
        while (nodes.hasMoreElements()) {
            n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if (n.getUserObject() instanceof PictureInfo
                    && ((PictureInfo) n.getUserObject()).containsCategory(key)) {
                count++;
            }
            if (n.getChildCount() > 0) {
                count += countCategoryUsage(key, n);
            }
        }
        return count;
    }

    /**
     * Returns an List of the nodes that match this category
     *
     * @param key       The key of the category to find
     * @param startNode the node at which to start
     * @return the list of nodes
     */
    public static List<SortableDefaultMutableTreeNode> getCategoryUsageNodes(
            Object key, SortableDefaultMutableTreeNode startNode) {
        final List<SortableDefaultMutableTreeNode> resultList = new ArrayList<>();
        final Enumeration<TreeNode> nodes = startNode.children();
        SortableDefaultMutableTreeNode n;
        while (nodes.hasMoreElements()) {
            n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if (n.getUserObject() instanceof PictureInfo
                    && ((PictureInfo) n.getUserObject()).containsCategory(key)) {
                resultList.add(n);
            }
            if (n.getChildCount() > 0) {
                resultList.addAll(getCategoryUsageNodes(key, n));
            }
        }
        return resultList;
    }

    /**
     * Removes the category from the nodes using it
     *
     * @param key       The category to poll
     * @param startNode The node from which to start
     */
    public void removeCategoryUsage(Object key,
                                    SortableDefaultMutableTreeNode startNode) {
        final Enumeration<TreeNode> nodes = startNode.children();
        while (nodes.hasMoreElements()) {
            SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if (n.getUserObject() instanceof PictureInfo) {
                ((PictureInfo) n.getUserObject()).removeCategory(key);
            }
            if (n.getChildCount() > 0) {
                removeCategoryUsage(key, n);
            }
        }
    }


    /**
     * This Hash Set hold references to the selected nodes for mailing. It works
     * just like the selection HashSet only that the purpose is a different one.
     * As such it has different behaviour.
     */
    private final List<SortableDefaultMutableTreeNode> mailSelection;

    /**
     * This method places the current SDMTN into the mailSelection HashSet.
     *
     * @param node The node going into the selection
     */
    public void addToMailSelection(SortableDefaultMutableTreeNode node) {
        if (isMailSelected(node)) {
            LOGGER.log(Level.FINE, "The node {0} is already selected. Leaving it selected.", node);
            return;
        }
        mailSelection.add(node);
        final Object userObject = node.getUserObject();
        if (userObject instanceof PictureInfo pi) {
            pi.sendWasMailSelectedEvent();
        }
    }

    /**
     * This method inverts the status of the node on the mail selection HashSet
     *
     * @param node The node
     */
    public void toggleMailSelected(SortableDefaultMutableTreeNode node) {
        if (isMailSelected(node)) {
            removeFromMailSelection(node);
        } else {
            addToMailSelection(node);
        }
    }

    /**
     * This method clears the mailSelection HashSet.
     */
    public void clearMailSelection() {
        //can't use iterator directly or we have a concurrent modification exception
        final List<SortableDefaultMutableTreeNode> clone = new ArrayList<>(mailSelection.size());
        clone.addAll(mailSelection);

        for (SortableDefaultMutableTreeNode node : clone) {
            LOGGER.log(Level.FINE, "Removing node: {0}", node.toString());
            removeFromMailSelection(node);
        }
    }

    /**
     * This method removes the current SDMTN from the mailSelection HashSet.
     *
     * @param node the node to poll from the mail selection
     */
    public void removeFromMailSelection(SortableDefaultMutableTreeNode node) {
        mailSelection.remove(node);
        Object userObject = node.getUserObject();
        if (userObject instanceof PictureInfo) {
            ((PictureInfo) userObject).sendWasMailUnselectedEvent();
        }
    }

    /**
     * This returns whether the SDMTN is part of the mailSelection HashSet.
     *
     * @param node The node
     * @return true if part of the mailing set, false if not
     */
    public boolean isMailSelected(SortableDefaultMutableTreeNode node) {
        try {
            return mailSelection.contains(node);
        } catch (NullPointerException x) {
            return false;
        }
    }

    /**
     * returns the count of mail-selected nodes
     *
     * @return the count of selected nodes
     */
    public int countMailSelectedNodes() {
        return mailSelection.size();
    }

    /**
     * Returns the email-selected nodes
     *
     * @return the nodes selected for emailing
     */
    public List<SortableDefaultMutableTreeNode> getMailSelectedNodes() {
        return mailSelection;
    }

    /**
     * This method returns true if the indicated picture file is already a
     * member of the collection. Otherwise it returns false.
     *
     * @param file The File object of the file to check for
     * @return true if found, false if not
     */
    public boolean isInCollection(File file) {
        LOGGER.log(Level.FINE, "Checking if File {0} exists in the collection", file);
        SortableDefaultMutableTreeNode node;
        Object nodeObject;
        File highresFile;
        final Enumeration<TreeNode> e = getRootNode().preorderEnumeration();
        while (e.hasMoreElements()) {
            node = (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = node.getUserObject();
            if (nodeObject instanceof PictureInfo pi) {
                highresFile = pi.getImageFile();
                LOGGER.log(Level.FINE, "Checking: {0}", ((PictureInfo) nodeObject).getImageFile().toString());
                if ((highresFile != null) && (highresFile.compareTo(file) == 0)) {
                    LOGGER.log(Level.INFO, "Found a match on: {0}", ((PictureInfo) nodeObject).getDescription());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method returns true if the indicated checksum is already a member of
     * the collection. Otherwise it returns false.
     *
     * @param checksum The checksum of the picture to check for
     * @return true if found, false if not
     */
    public boolean isInCollection(long checksum) {
        SortableDefaultMutableTreeNode node;
        Object nodeObject;
        final Enumeration<TreeNode> e = getRootNode().preorderEnumeration();
        while (e.hasMoreElements()) {
            node = (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = node.getUserObject();
            if (nodeObject instanceof PictureInfo pi) {
                LOGGER.log(Level.FINE, "Checking: {0}", ((PictureInfo) nodeObject).getImageFile().toString());
                if (pi.getChecksum() == checksum) {
                    LOGGER.log(Level.FINE, "Found a match on: {0}", ((PictureInfo) nodeObject).getDescription());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Is a thread loading a file?
     * @return true if a thread is loading a file
     */
    public boolean isFileLoading() {
        return fileLoading;
    }

    /**
     * status variable to find out if a thread is loading a file
     */
    private boolean fileLoading;  // default is false
    /**
     * A file reference to the file that was loaded. It will come in handy when
     * a save instruction comes along.
     */
    private File xmlFile;

    /**
     * This method sets the file which represents the current collection. It
     * updates the title of the main application window too.
     *
     * @param file set the file name
     */
    public void setXmlFile(File file) {
        xmlFile = file;
    }

    /**
     * This method returns the xml file for the collection
     *
     * @return The xml file of the collection
     */
    public File getXmlFile() {
        return xmlFile;
    }

    /**
     * Loads the specified file into the root node of the collection. It ought
     * to be called off the EDT. Then the clearCollection runs on the same
     * thread.
     *
     * @param file The file
     * @throws FileNotFoundException bubble-up exception
     */
    public void fileLoad(File file) throws FileNotFoundException {
        if (fileLoading) {
            LOGGER.log(Level.INFO, "{0}.fileLoad: already busy loading another file. Aborting", this.getClass());
            return;
        }
        fileLoading = true;
        clearCollection();
        setXmlFile(file);
        try {
            fileLoad(getXmlFile(), getRootNode());
            addYearQueries();
            fileLoading = false;
        } catch (FileNotFoundException ex) {
            fileLoading = false;
            throw ex;
        }
    }

    /**
     * Loads the collection indicated by the File at the supplied node
     *
     * @param fileToLoad The File object that is to be loaded.
     * @param node       the node to load it into
     * @throws FileNotFoundException When no good
     */
    public static void fileLoad(File fileToLoad, SortableDefaultMutableTreeNode node) throws FileNotFoundException {
        LOGGER.log(Level.INFO,"Loading file: {0}", fileToLoad);
        final InputStream is = new FileInputStream(fileToLoad);
        streamLoad(is, node);
    }

    /**
     * Loads the collection indicated by the Input stream at the "this" node.
     *
     * @param is   The InputStream that is to be loaded.
     * @param node the node to load it into
     */
    public static void streamLoad(InputStream is, SortableDefaultMutableTreeNode node) {
        node.getPictureCollection().setSendModelUpdates(false); // turn off model notification of each add for performance
        new XmlReader(is, node);
        node.getPictureCollection().setSendModelUpdates(true);
        node.getPictureCollection().sendNodeStructureChanged(node);
    }

    private void addYearQueries() {
        final TreeSet<String> years = new TreeSet<>();

        DefaultMutableTreeNode testNode;
        Object nodeObject;
        Calendar cal;
        for (final Enumeration<TreeNode> e = getRootNode().breadthFirstEnumeration(); e.hasMoreElements(); ) {
            testNode = (DefaultMutableTreeNode) e.nextElement();
            nodeObject = testNode.getUserObject();
            if (nodeObject instanceof PictureInfo pi) {
                cal = pi.getCreationTimeAsDate();
                if (cal != null) {
                    int year = cal.get(Calendar.YEAR);
                    years.add(Integer.toString(year));
                }
            }
        }

        SwingUtilities.invokeLater(
                () -> {
                    getYearsTreeNode().removeAllChildren();
                    years.forEach(this::addYearQuery);
                }
        );

    }

    /**
     * method that saves the entire index in XML format.
     */
    public void fileSave() {
        if (xmlFile == null) {
            LOGGER.severe("xmlFile is null. Not saving!");
        } else {
            final File temporaryFile = new File(xmlFile.getPath() + ".!!!");
            JpoWriter.write(temporaryFile, getRootNode(), false);
            final File backupOriginalFile = new File(xmlFile.getPath() + ".orig");
            if ( ! xmlFile.renameTo(backupOriginalFile) ) {
                LOGGER.log(Level.SEVERE,"Could not rename original file to {0}", backupOriginalFile);
            } else {
                if ( ! temporaryFile.renameTo(xmlFile) ) {
                    LOGGER.log(Level.SEVERE,"Could not rename temp file {0} to {1}", new Object[]{temporaryFile, xmlFile});
                } else {
                    setUnsavedUpdates(false);
                    try {
                        Files.delete(backupOriginalFile.toPath());
                    } catch (IOException e) {
                        LOGGER.log(Level.SEVERE,"Could not delete backed up original file {0}\n{1}", new Object[]{backupOriginalFile, e.getMessage()});
                    }
                    Settings.pushRecentCollection(xmlFile.toString());
                    JpoEventBus.getInstance().post(new RecentCollectionsChangedEvent());
                }
            }
        }
    }

    /**
     * This method returns an array of the groups that hold a reference to the
     * picture filename of the supplied node. This is used in the Navigate-to
     * function of the pop-up menu
     *
     * @param suppliedNode The node with the picture for which the owning parent
     *                     nodes need to be found
     * @return group nodes that have a child with the same picture
     */
    public SortableDefaultMutableTreeNode[] findParentGroups(
            SortableDefaultMutableTreeNode suppliedNode) {
        final Object userObject = suppliedNode.getUserObject();
        if (!(userObject instanceof PictureInfo)) {
            return new SortableDefaultMutableTreeNode[0];
        }

        final List<SortableDefaultMutableTreeNode> parentGroups = new ArrayList<>();

        final File comparingFile = ((PictureInfo) userObject).getImageFile();
        SortableDefaultMutableTreeNode testNode;
        SortableDefaultMutableTreeNode testNodeParent;
        Object nodeObject;
        for (final Enumeration<TreeNode> e = getRootNode().preorderEnumeration(); e.hasMoreElements(); ) {
            testNode = (SortableDefaultMutableTreeNode) e.nextElement();
            nodeObject = testNode.getUserObject();
            if (nodeObject instanceof PictureInfo pi && pi.getImageFile().equals(comparingFile)) {
                    testNodeParent = testNode.getParent();
                    if (!parentGroups.contains(testNodeParent)) {
                        LOGGER.log(Level.FINE, "adding node: {0}", testNodeParent.toString());
                        parentGroups.add(testNodeParent);
                    }
                }
            }
        }
        return parentGroups.toArray(new SortableDefaultMutableTreeNode[0]);
    }

    /**
     * A reference to the selected nodes.
     */
    private final List<SortableDefaultMutableTreeNode> selection = new ArrayList<>();

    /**
     * This method places the current {@link SortableDefaultMutableTreeNode}
     * into the selection HashSet.
     *
     * @param node The node
     */
    public void addToSelectedNodes(SortableDefaultMutableTreeNode node) {
        if (isSelected(node)) {
            LOGGER.fine(String.format("The node %s is already selected. Leaving it selected.", node.toString()));
            return;
        }
        selection.add(node);
        Object userObject = node.getUserObject();
        if (userObject instanceof PictureInfo pi) {
            pi.sendWasSelectedEvent();
        } else if (userObject instanceof GroupInfo gi) {
            gi.sendWasSelectedEvent();
        }
    }

    /**
     * This method removes the current SDMTN from the selection
     *
     * @param node the node to poll
     */
    public void removeFromSelection(SortableDefaultMutableTreeNode node) {
        selection.remove(node);
        Object userObject = node.getUserObject();
        if (userObject instanceof PictureInfo) {
            ((PictureInfo) userObject).sendWasUnselectedEvent();
        } else if (userObject instanceof GroupInfo) {
            ((GroupInfo) userObject).sendWasUnselectedEvent();
        }
    }

    /**
     * This method clears selection that refers to the selected highlighted
     * thumbnails and fires unselectedEvents
     */
    public void clearSelection() {
        //can't use iterator or there is a concurrent modification exception
        Object[] array = selection.toArray();
        for (Object node : array) {
            removeFromSelection((SortableDefaultMutableTreeNode) node);
        }
    }

    /**
     * This returns whether the SDMTN is part of the selection HashSet.
     *
     * @param node the node
     * @return true if the node is selected
     */
    public boolean isSelected(SortableDefaultMutableTreeNode node) {
        try {
            return selection.contains(node);
        } catch (NullPointerException x) {
            return false;
        }
    }


    /**
     * returns an array of the selected nodes.
     *
     * @return an array of the selected nodes
     */
    public List<SortableDefaultMutableTreeNode> getSelection() {
        return selection;
    }

    /**
     * returns the count of selected nodes
     *
     * @return the count of selected nodes
     */
    public int countSelectedNodes() {
        return selection.size();
    }
}
