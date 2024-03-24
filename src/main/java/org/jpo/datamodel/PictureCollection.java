package org.jpo.datamodel;

import org.jpo.eventbus.*;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.util.Objects.isNull;


/*
 * Copyright (C) 2006-2023 Richard Eigenmann, Zurich, Switzerland This program
 * is free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation,
 * either version 2 of the License, or any later version. This program is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
 * Without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple Place
 * - Suite 330, Boston, MA 02111-1307, USA. The license is in gpl.txt. See
 * http://www.gnu.org/copyleft/gpl.html for the details.
 */

/**
 * This object holds the state of the picture collection
 */
public class PictureCollection {

    /**
     * Defines a LOGGER for this class
     */
    private static final Logger LOGGER = Logger.getLogger(PictureCollection.class.getName());
    /**
     * Holds the tree of nodes together that make up the collection
     */
    private final DefaultTreeModel treeModel;
    /**
     * This HashMap holds the categories that will be available for this
     * collection.
     */
    private final HashMap<Integer, String> categories = new HashMap<>();
    /**
     * This List holds references to the selected nodes for mailing. It works
     * just like the selection only that the purpose is a different one.
     */
    private final List<SortableDefaultMutableTreeNode> mailSelection = new ArrayList<>();
    /**
     * A reference to the selected nodes.
     */
    private final List<SortableDefaultMutableTreeNode> selection = new ArrayList<>();
    /**
     * controls whether updates should be fired from add, delete, insert methods
     */
    private boolean sendModelUpdates = true;
    /**
     * The root node of the tree data model. It holds all the branches to the
     * groups and pictures
     */
    private SortableDefaultMutableTreeNode rootNode;
    /**
     * This variable indicates whether uncommitted changes exist for this
     * collection. Care should be taken when adding removing or changing nodes
     * to update this flag. It should be queried before exiting the application.
     * Also, when a new collection is loaded this flag should be checked so as
     * not to lose modifications. This flag should be set only on the root
     * node.
     *
     * @see #setUnsavedUpdates()
     * @see #setUnsavedUpdates(boolean)
     * @see #getUnsavedUpdates()
     */
    private boolean unsavedUpdates; // default is false
    /**
     * This flag controls whether this collection can be edited. This is queried
     * by several menus and will restrict the options a use has if it returns
     * true.
     */
    private boolean allowEdits;
    /**
     * This variable holds the reference to the queries executed against the
     * collection.
     */
    private DefaultTreeModel queriesTreeModel;

    private DefaultMutableTreeNode yearsTreeNode;

    private DefaultMutableTreeNode categoriesTreeNode;
    /**
     * status variable to find out if a thread is loading a file
     */
    private boolean fileLoading;  // default is false
    /**
     * A file reference to the file that was loaded. It will come in handy when
     * a save instruction comes along.
     */
    private File xmlFile = null;

    /**
     * Constructs a new PictureCollection object with a root object
     */
    public PictureCollection() {
        final var node = new SortableDefaultMutableTreeNode(new GroupInfo(Settings.getJpoResources().getString("DefaultRootNodeText")));
        setRootNode(node);
        node.setPictureCollection(this);

        treeModel = new DefaultTreeModel(getRootNode());
        treeModel.addTreeModelListener(new PictureCollectionTreeModelListener());
        setAllowEdits(true);
        setUnsavedUpdates(false);
    }


    /**
     * Loads the collection indicated by the File at the supplied node
     *
     * @param fileToLoad The File object that is to be loaded.
     * @param node       the node to load it into
     * @throws FileNotFoundException When no good
     */
    public static void fileLoad(final File fileToLoad, final SortableDefaultMutableTreeNode node) throws FileNotFoundException {
        LOGGER.log(Level.INFO, "Loading file: {0}", fileToLoad);
        final InputStream is = new FileInputStream(fileToLoad);
        streamLoad(is, node);
    }

    /**
     * Loads the collection indicated by the Input stream at the "this" node.
     *
     * @param is   The InputStream that is to be loaded.
     * @param node the node to load it into
     */
    public static void streamLoad(final InputStream is, final SortableDefaultMutableTreeNode node) {
        final var pictureCollection = node.getPictureCollection();
        pictureCollection.setSendModelUpdates(false); // turn off model notification of each add for performance
        XmlReader.read(is, node);
        pictureCollection.setSendModelUpdates(true);
        pictureCollection.sendNodeStructureChanged(node);
        JpoEventBus.getInstance().post(new CollectionLockNotification(pictureCollection));
    }

    /**
     * This method wipes out the data in the picture collection. As it updates
     * the TreeModel it has been made synchronous on the EDT.
     */
    public void clearCollection() {
        final Runnable runnable = () -> {
            getRootNode().removeAllChildren();
            getRootNode().setUserObject(new GroupInfo(Settings.getJpoResources().getString("DefaultRootNodeText")));
            clearQueriesTreeModel();
            categories.clear();
            clearMailSelection();
            setAllowEdits(true);
            setUnsavedUpdates(false);
            setXmlFile(null);
            getTreeModel().reload();
            Settings.getRecentDropNodes().clear();
        };
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(runnable);
            } catch (InterruptedException | InvocationTargetException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * The DefaultTreeModel allows notification of tree change events to
     * listening objects.
     *
     * @return The tree Model
     */
    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    /**
     * Returns true if model updates should be sent to the listeners
     *
     * @return true if model updates are sent around
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
     * of the Collection's model. It makes sure the event is sent on the EDT
     *
     * @param changedNode The node that was changed
     */
    public void sendNodeStructureChanged(final TreeNode changedNode) {
        final Runnable r = () -> {
            LOGGER.log(Level.FINE, "Sending a node structure change on node: {0}", changedNode);
            getTreeModel().nodeStructureChanged(changedNode);
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * This method sends a nodeChanged event through to the listeners of the
     * Collection's model. It makes sure the event is sent on the EDT
     *
     * @param changedNode The node that was changed
     */
    public void sendNodeChanged(
            final TreeNode changedNode) {
        final Runnable r = () -> {
            LOGGER.log(Level.FINE, "Sending a node change on node: {0}", changedNode);
            getTreeModel().nodeChanged(changedNode);
        };
        SwingUtilities.invokeLater(r);
    }

    /**
     * This method sends a nodesWereInserted event through to the listeners of
     * the Collection's model.
     *
     * @param changedNode  The node that was inserted
     * @param childIndices The Child indices
     */
    public void sendNodesWereInserted( final TreeNode changedNode,final int[] childIndices) {
        final Runnable r = () -> {
            LOGGER.log(Level.FINE, "Sending a node was inserted notification on node: {0}", new Object[]{changedNode});
            getTreeModel().nodesWereInserted(changedNode, childIndices);
        };
        SwingUtilities.invokeLater(r);
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
        final Runnable r = () -> {
            LOGGER.log(Level.FINE, "Sending a node was removed change on node: {0}, childIndices: [{1}]",
                    new Object[]{
                            node,
                            Arrays
                                .stream(childIndices)
                                .mapToObj(String::valueOf)
                                .reduce((a, b) -> a.concat(",").concat(b))
                                .get()
                    });
            getTreeModel().nodesWereRemoved(node, childIndices, removedChildren);
        };
        SwingUtilities.invokeLater(r);
    }

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
     * This method marks the collection as having unsaved updates.
     *
     * @see #unsavedUpdates
     */
    public void setUnsavedUpdates() {
        setUnsavedUpdates(true);
    }

    /**
     * This method returns true if the collection has unsaved updates, false if it has
     * none
     *
     * @return true if there are unsaved updates, false if there are none
     * @see #unsavedUpdates
     */
    public boolean getUnsavedUpdates() {
        return unsavedUpdates;
    }

    /**
     * This method allows the programmer to set whether the tree has unsaved
     * updates or not.
     *
     * @param unsavedUpdates Set to true if there are unsaved updates, false if
     *                       there are none
     * @see #unsavedUpdates
     */
    public void setUnsavedUpdates(final boolean unsavedUpdates) {
        this.unsavedUpdates = unsavedUpdates;
    }

    /**
     * Returns true if edits are allowed on this collection
     *
     * @return true if edits are allowed on this collection
     */
    public boolean getAllowEdits() {
        return allowEdits;
    }

    /**
     * sets the allow-edit allowedEdits of this collection
     *
     * @param newAllowEdits pass true to allow edits, false to forbid
     */
    public void setAllowEdits(final boolean newAllowEdits) {
        if ( allowEdits != newAllowEdits ) {
            allowEdits = newAllowEdits;
            JpoEventBus.getInstance().post(new CollectionLockNotification(this));
        }
    }

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
     * Call this method when you need to set the TreeModel for the queries
     *
     * @param defaultTreeModel the tree model
     */
    public void setQueriesTreeModel(final DefaultTreeModel defaultTreeModel) {
        queriesTreeModel = defaultTreeModel;
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
     * Call this method when you need to create a new TreeModel for the queries.
     */
    public void createQueriesTreeModel() {
        setQueriesTreeModel(new DefaultTreeModel(new DefaultMutableTreeNode(Settings.getJpoResources().getString("queriesTreeModelRootNode"))));

        final DefaultMutableTreeNode byYearsTreeNode = new DefaultMutableTreeNode("By Year");
        setYearsTreeNode(byYearsTreeNode);
        getQueriesRootNode().add(byYearsTreeNode);
        final DefaultMutableTreeNode byCategoriesTreeNode = new DefaultMutableTreeNode("By Category");
        setCategoriesTreeNode(byCategoriesTreeNode);
        getQueriesRootNode().add(byCategoriesTreeNode);
    }

    /**
     * Remembers the node on which the years were added
     *
     * @param node The node
     */
    private void setYearsTreeNode(final DefaultMutableTreeNode node) {
        yearsTreeNode = node;
    }

    /**
     * Remembers the node on which the categories were added
     *
     * @param node The node
     */
    private void setCategoriesTreeNode(final DefaultMutableTreeNode node) {
        categoriesTreeNode = node;
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
     * Node for the Years tree
     *
     * @return the node
     */
    public DefaultMutableTreeNode getCategoriesTreeNode() {
        return categoriesTreeNode;
    }

    /**
     * Adds a year query
     *
     * @param year the year
     */
    public void addYearQuery(final String year) {
        final YearQuery yearQuery = new YearQuery(year);
        yearQuery.setStartNode(getRootNode());
        getYearsTreeNode().add(new DefaultMutableTreeNode(yearQuery));
    }

    /**
     * Clear out the nodes in the existing queries Tree Model
     */
    public void clearQueriesTreeModel() {
        final Runnable r = () -> getQueriesRootNode().removeAllChildren();
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(r);
            } catch (final InterruptedException | InvocationTargetException e) {
                LOGGER.log(Level.SEVERE, "Something went terribly wrong when clearing the queries: {0}", e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Adds a query to the Query Tree Model.
     *
     * @param query The new Query to add
     */
    public void addQueryToTreeModel(final Query query) {
        final DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(query);
        getQueriesRootNode().add(newNode);
        queriesTreeModel.nodesWereInserted(getQueriesRootNode(), new int[]{getQueriesRootNode().getIndex(newNode)});
    }

    /**
     * Adds a query to the Query Category Tree Model.
     *
     * @param query The new Query to add
     */
    public void addCategoryQueryToTreeModel(final Query query) {
        LOGGER.log(Level.FINE, "Adding query {0}", query);
        final DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(query);
        getCategoriesTreeNode().add(newNode);
        queriesTreeModel.nodesWereInserted(getCategoriesTreeNode(), new int[]{getCategoriesTreeNode().getIndex(newNode)});
    }

    /**
     * This adds a category to the HashMap
     *
     * @param index    The index
     * @param category The category
     */
    public void addCategory(final Integer index, final String category) {
        categories.put(index, category);
    }

    /**
     * This adds a category to the HashMap if it doesn't already exist and
     * returns the corresponding Integer code for the category.
     *
     * @param category The category to save or look up
     * @return the number at which the category was added
     */
    public Integer addCategory(final String category) {
        synchronized (categories) { // I'm worried that concurrent modifications could mess things up
            if (categories.isEmpty()) {
                addCategory(0, category);
                return 0;
            }

            if (categories.containsValue(category)) {
                for (final Map.Entry<Integer, String> entry : categories.entrySet()) {
                    if (Objects.equals(category, entry.getValue())) {
                        return entry.getKey();
                    }
                }
                LOGGER.log(Level.SEVERE, "Found category {0} in categories map but then is was gone...", category);
                return null; // how did this happen?
            }

            final Integer maxKey = Collections.max(categories.keySet());
            final Integer nextKey = maxKey + 1;
            addCategory(nextKey, category);
            JpoEventBus.getInstance().post(new CategoriesWereModified());
            return nextKey;
        }
    }

    /**
     * Renames a category in the HashMap
     *
     * @param key      The Key
     * @param category The category
     */
    public void renameCategory(final Integer key, final String category) {
        removeCategory(key);
        addCategory(key, category);
        JpoEventBus.getInstance().post(new CategoriesWereModified());
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
     * Returns a set of category keys
     *
     * @return a set of category keys
     */
    public Set<Integer> getCategoryKeySet() {
        return categories.keySet();
    }

    /**
     * Returns a Java Stream of sorted categories suitable for presenting the categories in a
     * sorted pick list.
     *
     * @return A Steam of Category entries
     */
    public Stream<Map.Entry<Integer, String>> getSortedCategoryStream() {
        return categories.entrySet().stream()
                .sorted(Map.Entry.comparingByValue());
    }

    /**
     * Returns the Value for the key
     *
     * @param key the key for the value to be returned-
     * @return Returns the Value for the Key
     */
    public String getCategory(final Integer key) {
        return categories.get(key);
    }

    /**
     * Removes the category associated with the
     *
     * @param key The Key to be removed
     */
    public void removeCategory(final Integer key) {
        categories.remove(key);
        JpoEventBus.getInstance().post(new CategoriesWereModified());
    }

    /**
     * Removes the category from the nodes using it
     *
     * @param key       The category to poll
     * @param startNode The node from which to start
     */
    public void removeCategoryUsage(final Object key,
                                    final SortableDefaultMutableTreeNode startNode) {
        final Enumeration<TreeNode> nodes = startNode.children();
        while (nodes.hasMoreElements()) {
            final SortableDefaultMutableTreeNode n = (SortableDefaultMutableTreeNode) nodes.nextElement();
            if (n.getUserObject() instanceof PictureInfo pictureInfo) {
                pictureInfo.removeCategory(key);
            }
            if (n.getChildCount() > 0) {
                removeCategoryUsage(key, n);
            }
        }
    }

    /**
     * This method places the current SDMTN into the mailSelection HashSet.
     *
     * @param node The node going into the selection
     */
    public void addToMailSelection(final SortableDefaultMutableTreeNode node) {
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
    public void toggleMailSelected(final SortableDefaultMutableTreeNode node) {
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
        //can't use iterator directly, or we have a concurrent modification exception
        final List<SortableDefaultMutableTreeNode> clone = new ArrayList<>(mailSelection.size());
        clone.addAll(mailSelection);

        for (final var node : clone) {
            LOGGER.log(Level.FINE, "Removing node: {0}", node);
            removeFromMailSelection(node);
        }
    }

    /**
     * This method removes the current SDMTN from the mailSelection HashSet.
     *
     * @param node the node to poll from the mail selection
     */
    public void removeFromMailSelection(final SortableDefaultMutableTreeNode node) {
        mailSelection.remove(node);
        if (node.getUserObject() instanceof PictureInfo pi) {
            pi.sendWasMailUnselectedEvent();
        }
    }

    /**
     * This returns whether the SDMTN is part of the mailSelection HashSet.
     *
     * @param node The node
     * @return true if part of the mailing set, false if not
     */
    public boolean isMailSelected(final SortableDefaultMutableTreeNode node) {
        try {
            return mailSelection.contains(node);
        } catch (final NullPointerException x) {
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
     * member of the collection. Otherwise, it returns false.
     *
     * @param file The File object of the file to check for
     * @return true if found, false if not
     */
    public boolean isInCollection(final File file) {
        LOGGER.log(Level.FINE, "Checking if File {0} exists in the collection", file);
        final Enumeration<TreeNode> e = getRootNode().preorderEnumeration();
        while (e.hasMoreElements()) {
            final SortableDefaultMutableTreeNode node = (SortableDefaultMutableTreeNode) e.nextElement();
            final Object nodeObject = node.getUserObject();
            if (nodeObject instanceof PictureInfo pi) {
                final File highresFile = pi.getImageFile();
                LOGGER.log(Level.FINE, "Checking: {0}", ((PictureInfo) nodeObject).getImageFile());
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
     * the collection. Otherwise, it returns false.
     *
     * @param sha256 The checksum of the picture to check for
     * @return true if found, false if not
     */
    public boolean isInCollection(final String sha256) {
        final var enumeration = getRootNode().preorderEnumeration();
        while (enumeration.hasMoreElements()) {
            final var node = (SortableDefaultMutableTreeNode) enumeration.nextElement();
            final var nodeObject = node.getUserObject();
            if (nodeObject instanceof PictureInfo pictureInfo) {
                LOGGER.log(Level.FINE, "Checking: {0}", ((PictureInfo) nodeObject).getImageFile());
                if (pictureInfo.getSha256().equals(sha256)) {
                    LOGGER.log(Level.FINE, "Found a match on: {0}", ((PictureInfo) nodeObject).getDescription());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Is a thread loading a file?
     *
     * @return true if a thread is loading a file
     */
    public boolean isFileLoading() {
        return fileLoading;
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
     * This method sets the file which represents the current collection. It
     * updates the title of the main application window too.
     *
     * @param file set the file name
     */
    public void setXmlFile(File file) {
        xmlFile = file;
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
            addCategoriesQueries();
            fileLoading = false;
        } catch (FileNotFoundException ex) {
            fileLoading = false;
            throw ex;
        }
    }

    private void addYearQueries() {
        final TreeSet<String> years = new TreeSet<>();
        for (final Enumeration<TreeNode> e = getRootNode().breadthFirstEnumeration(); e.hasMoreElements(); ) {
            final DefaultMutableTreeNode testNode = (DefaultMutableTreeNode) e.nextElement();
            final Object nodeObject = testNode.getUserObject();
            if (nodeObject instanceof PictureInfo pi) {
                final Calendar cal = pi.getCreationTimeAsDate();
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

    private void addCategoriesQueries() {
        SwingUtilities.invokeLater(
                () -> {
                    getCategoriesTreeNode().removeAllChildren();
                    getSortedCategoryStream().forEach(categoryEntry -> {
                        LOGGER.log(Level.FINE, "Adding category {0} to Tree", categoryEntry.getValue());
                        final CategoryQuery categoryQuery = new CategoryQuery(categoryEntry.getKey());
                        addCategoryQueryToTreeModel(categoryQuery);
                    });
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
            final ExportGroupToCollectionRequest exportRequest = new ExportGroupToCollectionRequest(getRootNode(), temporaryFile, false);
            JpoWriter.write(exportRequest);
            replaceFile(xmlFile, temporaryFile);
            setUnsavedUpdates(false);
            Settings.pushRecentCollection(xmlFile.toString());
            JpoEventBus.getInstance().post(new RecentCollectionsChangedEvent());
        }
    }

    /**
     * This method replaces the old file with the new file by renaming the old to
     * <<filename>>.orig and then moves the new file to that name. If there were no errors it
     * then deletes the old file.
     * @param oldFile the old file
     * @param newFile the new file
     */
    private static void replaceFile(final File oldFile, final File newFile) {
        final File backupOldFile = new File(oldFile.getPath() + ".orig");
        final boolean anOldFileExists = oldFile.exists();
        if (anOldFileExists && !oldFile.renameTo(backupOldFile)) {
            LOGGER.log(Level.SEVERE, "Could not rename {0} to {1}", new Object[]{oldFile, backupOldFile});
        } else {
            // the renameTo above worked (side effect)
            if (!newFile.renameTo(oldFile)) {
                LOGGER.log(Level.SEVERE, "Could not rename temp file {0} to {1}", new Object[]{newFile, oldFile});
            } else {
                // again, the renameTo above worked (side effect)
                if (anOldFileExists) {
                    try {
                        Files.delete(backupOldFile.toPath());
                    } catch (final IOException e) {
                        LOGGER.log(Level.SEVERE, "Could not delete backed up old file {0}\n{1}", new Object[]{backupOldFile, e.getMessage()});
                    }
                }
            }
        }
    }

    /**
     * This method returns a Set of the group nodes that hold a reference to the
     * picture filename of the supplied node. This is used in the Navigate-to
     * function of the pop-up menu
     *
     * @param suppliedNode The node with the picture for which the owning parent
     *                     nodes need to be found
     * @return group nodes that have a child with the same picture
     */
    public Set<SortableDefaultMutableTreeNode> findLinkingGroups(
            final SortableDefaultMutableTreeNode suppliedNode) {

        final var linkingGroups = new HashSet<SortableDefaultMutableTreeNode>();

        if (suppliedNode.getUserObject() instanceof PictureInfo pictureInfo) {
            final var comparingFile = pictureInfo.getImageFile();
            if (isNull(comparingFile)) {
                return linkingGroups;
            }
            for (final var e = getRootNode().preorderEnumeration(); e.hasMoreElements(); ) {
                final var testNode = (SortableDefaultMutableTreeNode) e.nextElement();
                if (testNode.getUserObject() instanceof PictureInfo pi && !isNull(pi.getImageFile()) && pi.getImageFile().equals(comparingFile)) {
                    linkingGroups.add(testNode.getParent());
                }
            }
        }
        return linkingGroups;
    }

    /**
     * This method places the current {@link SortableDefaultMutableTreeNode}
     * into the selection HashSet.
     *
     * @param node The node
     */
    public void addToSelectedNodes(final SortableDefaultMutableTreeNode node) {
        if (isSelected(node)) {
            LOGGER.log(Level.FINE, "The node {0} is already selected. Leaving it selected.", node);
            return;
        }
        selection.add(node);
        final Object userObject = node.getUserObject();
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
    public void removeFromSelection(final SortableDefaultMutableTreeNode node) {
        selection.remove(node);
        final Object userObject = node.getUserObject();
        if (userObject instanceof PictureInfo pictureInfo) {
            pictureInfo.sendWasUnselectedEvent();
        } else if (userObject instanceof GroupInfo groupInfo) {
            groupInfo.sendWasUnselectedEvent();
        }
    }

    /**
     * This method clears selection that refers to the selected highlighted
     * thumbnails and fires unselectedEvents
     */
    public void clearSelection() {
        //can't use iterator or there is a concurrent modification exception
        final Object[] array = selection.toArray();
        for (final Object node : array) {
            removeFromSelection((SortableDefaultMutableTreeNode) node);
        }
    }

    /**
     * This returns whether the SDMTN is part of the selection HashSet.
     *
     * @param node the node
     * @return true if the node is selected
     */
    public boolean isSelected(final SortableDefaultMutableTreeNode node) {
        try {
            return selection.contains(node);
        } catch (final NullPointerException x) {
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

    /**
     * Returns a common path between two Java Path objects.
     * Note that it makes a different which path comes first. If we are comparing against a result
     * of a prior request (like when you are comparing a whole list) then the commonPath has to
     * come second.
     * @see <a href="https://stackoverflow.com/a/54596165/804766">https://stackoverflow.com/a/54596165/804766</a>
     * @param path1 The first path
     * @param path2 The second path
     * @return The common path
     */
    static Path getCommonPath(final Path path1, final Path path2) {
        final var emptyPath = Paths.get("");
        if ( path1 == null || path2 == null || path1 == emptyPath || path2 == emptyPath) {
            return emptyPath;
        }
        LOGGER.log(Level.FINE, "Path1: {0} Path2: {1}", new Object[]{path1, path2});
        Path relativePath;
        try {
            relativePath = path1.relativize(path2).normalize();
        } catch ( IllegalArgumentException ex ) {
            LOGGER.log(Level.INFO, "Can not find a common path: {0}", ex.getMessage());
            return null;
        }
        while(relativePath != null && !relativePath.endsWith("..")) {
            relativePath = relativePath.getParent();
        }
        LOGGER.log(Level.FINE, "final relativePath is: {0}", relativePath);
        return path1.resolve(relativePath).normalize();
    }

    Path getCommonPath() {
        LOGGER.log(Level.FINE, "Searching for common path of the pictures");
        final var pictureNodes = getRootNode().getChildPictureNodes(true);
        if (pictureNodes.isEmpty()) {
            LOGGER.log(Level.INFO, "The collection has {0} pictures. Returning an empty common path.", pictureNodes.size());
            return Paths.get("");
        }
        final var firstNode = pictureNodes.get(0);
        final var firstPictureInfo = (PictureInfo) firstNode.getUserObject();
        var commonPath = firstPictureInfo.getImageFile().toPath();
        for (int i = 1; i<pictureNodes.size(); i++) {
            final var nextPictureInfo = (PictureInfo) pictureNodes.get(i).getUserObject();
            LOGGER.log(Level.FINE,"Next picture: {0}, File: {1}", new Object[]{nextPictureInfo.getDescription(), nextPictureInfo.getImageFile()});
            commonPath = getCommonPath(nextPictureInfo.getImageFile().toPath(), commonPath);
        }
        LOGGER.log(Level.FINE, "Common path is {0}", commonPath);
        return commonPath;
    }

    /**
     * Adds a treeModelListener to the collection.
     * Uses the treeModel associated with the PictureCollection to handle the details
     * @param treeModelListener The listener to add
     */
    public void addTreeModelListener( final TreeModelListener treeModelListener){
        getTreeModel().addTreeModelListener(treeModelListener);
    }

    /**
     * Removes a treeModelListener from the collection.
     * Uses the treeModel associated with the PictureCollection to handle the details
     * @param treeModelListener The listener to remove
     */
    public void removeTreeModelListener( final TreeModelListener treeModelListener){
        getTreeModel().addTreeModelListener(treeModelListener);
    }

    private class PictureCollectionTreeModelListener implements TreeModelListener {
        @Override
        public void treeNodesChanged(TreeModelEvent treeModelEvent) {
            // noop
        }

        @Override
        public void treeNodesInserted(TreeModelEvent treeModelEvent) {
            // noop
        }

        /**
         * When nodes are removed from the tree they can't be left as selected nodes.
         * @param treeModelEvent a {@code TreeModelEvent} describing changes to a tree model
         */
        @Override
        public void treeNodesRemoved(final TreeModelEvent treeModelEvent) {
            Arrays.stream(treeModelEvent.getChildren()).forEach(child -> removeFromSelection((SortableDefaultMutableTreeNode) child));
        }

        @Override
        public void treeStructureChanged(TreeModelEvent treeModelEvent) {
            // noop
        }
    }
}
