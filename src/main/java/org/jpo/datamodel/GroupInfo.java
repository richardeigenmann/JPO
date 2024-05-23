package org.jpo.datamodel;

import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
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
 * A GroupInfo object carries a description and its owning node can have zero or
 * many GroupOrPicture carrying nodes.
 * <p>
 * This class must implement the Serializable interface or Drag and Drop will
 * not work.
 *
 * @see PictureInfo
 */
public class GroupInfo implements Serializable, GroupOrPicture {

    /**
     * Keep serialisation happy
     */
    @Serial
    private static final long serialVersionUID = 1;

    /**
     * Defines a logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger( GroupInfo.class.getName() );

    /**
     * The description of the GroupInfo.
     *
     */
    private StringBuilder groupName = new StringBuilder();

    /**
     * Constructor to create a new GroupInfo object.
     *
     * @param    description    The description of the Group
     */
    public GroupInfo(final String description) {
        setGroupName(description);
    }

    /**
     * toString method that returns the description of the group
     *
     * @return description of the group
     */
    @Override
    public String toString() {
        return groupName.toString();
    }

    /**
     * Returns the description of the group.
     *
     * @return	The description of the Group.
     * @see #setGroupName
     *
     */
    public String getGroupName() {
        return groupName.toString();
    }

    /**
     * Returns the description of the group with the HTML characters safely
     * escaped.
     *
     * @return	The description of the Group.
     * @see #getGroupName
     *
     */
    public String getGroupNameHtml() {
        return StringEscapeUtils.escapeHtml4( groupName.toString() );
    }

    /**
     * Set name of the GroupInfo
     *
     * @param name The new description of the GroupInfo
     * @see #getGroupName
     *
     */
    public void setGroupName(final String name) {
        if (!groupName.toString().equals(name)) {
            groupName = new StringBuilder(name);
            sendGroupNameChangedEvent();
        }

    }

    /**
     * Creates a GroupInfoChangedEvent and sends it to inform listening objects
     * that the description was updated.
     */
    private void sendGroupNameChangedEvent() {
        LOGGER.log(Level.FINE, "preparing to send GroupName changed event");
        final var owningNode = getOwningNode();
        if (owningNode == null ) {
            // no owning node, no change notification
            return;
        }
        final var pictureCollection = owningNode.getPictureCollection();
        if ( ( pictureCollection != null ) && ( pictureCollection.getSendModelUpdates() ) ) {
            final var groupInfoChangeEvent = new GroupInfoChangeEvent(this);
            groupInfoChangeEvent.setGroupNameChanged();
            sendGroupInfoChangedEvent(groupInfoChangeEvent);
            LOGGER.log(Level.FINE, "sent description changed event");
            pictureCollection.setUnsavedUpdates();
        }
    }

    /**
     * this method writes all attributes of the group to the JPO xml data
     * format with the highres locations passed in as parameters.
     *
     * @param out        The BufferedWriter receiving the xml data. I use a BufferedWriter because it has a newLine method
     * @param rootNode   The starting node
     * @param protection Whether the collection is protected or not
     * @throws IOException If there was an IO error
     */
    public void dumpToXml(final BufferedWriter out, final boolean rootNode, final boolean protection)
            throws IOException {

        if (!rootNode) {
            out.write( "<group group_name=\"" + StringEscapeUtils.escapeXml11( getGroupName() ) + "\">" );
        }
        out.newLine();
    }

    /**
     * Closes the xml output
     *
     * @param out The output stream
     * @param rootNode true if this si the root node
     * @throws IOException if something went wrong
     */
    public void endGroupXML( BufferedWriter out, boolean rootNode )
            throws IOException {

        if ( !rootNode ) {  // if it is root Node then the XmlDistiller adds the categories and end collection tag.
            out.write( "</group>" );
        }
        out.newLine();
    }

    /**
     * Creates a GroupInfoChangedEvent and sends it to inform listening objects
     * that the node was selected. Strictly speaking this is not a GroupInfo
     * level event but a node level event. However, because I have the
     * GroupInfoChangeEvent structure in place this is a good place to put this
     * notification.
     */
    public void sendWasSelectedEvent() {
        final var groupInfoChangeEvent = new GroupInfoChangeEvent(this);
        groupInfoChangeEvent.setWasSelected();
        sendGroupInfoChangedEvent(groupInfoChangeEvent);
    }

    /**
     * Creates a PictureChangedEvent and sends it to inform listening objects
     * that the node was unselected. Strictly speaking this is not a PictureInfo
     * level event but a node level event. However, because I have the
     * PictureInfoChangeEvent structure in place this is a good place to put
     * this notification.
     */
    public void sendWasUnselectedEvent() {
        LOGGER.fine("Sending unselected event");
        final var groupInfoChangeEvent = new GroupInfoChangeEvent(this);
        groupInfoChangeEvent.setWasUnselected();
        sendGroupInfoChangedEvent(groupInfoChangeEvent);
    }

    /**
     * The listeners to notify about changes on this GroupInfo object
     */
    private final transient Set<GroupInfoChangeListener> groupInfoListeners = Collections.synchronizedSet(new HashSet<>() );

    /**
     * Adds a change listener
     *
     * @param groupInfoChangeListener	The object that will receive
     * notifications.
     */
    public void addGroupInfoChangeListener( GroupInfoChangeListener groupInfoChangeListener ) {
        groupInfoListeners.add( groupInfoChangeListener );
    }

    /**
     * Removes the change listener
     *
     * @param groupInfoChangeListener	The listener that doesn't want to
     * receive notifications.
     */
    public void removeGroupInfoChangeListener( GroupInfoChangeListener groupInfoChangeListener ) {
        groupInfoListeners.remove( groupInfoChangeListener );
    }

    /**
     * Send PictureInfoChangeEvents.
     *
     * @param groupInfoChangeEvent The Event we want to notify.
     */
    private void sendGroupInfoChangedEvent(final GroupInfoChangeEvent groupInfoChangeEvent) {
        final var pictureCollection = getOwningNode().getPictureCollection();
        if (pictureCollection != null && pictureCollection.getSendModelUpdates()) {
            synchronized (groupInfoListeners) {
                groupInfoListeners.forEach(groupInfoChangeListener
                        -> groupInfoChangeListener.groupInfoChangeEvent(groupInfoChangeEvent)
                );
            }
        }
    }

    /**
     * Returns a new GroupInfo object which is identical to the current one.
     *
     * @return a clone of the current PictureInfo object.
     */
    public GroupInfo getClone() {
        return new GroupInfo(this.getGroupName());
    }


    /**
     * Defines how GroupInfo objects compare themselves
     *
     * @param otherGroupInfo The other GroupInfo object
     * @return negative number if this is less than other Zero if same or positive number if other is less than this
     */
    public int compareTo(final @NotNull GroupInfo otherGroupInfo) {
        return (this.getGroupName().compareTo(otherGroupInfo.getGroupName()));
    }


    private SortableDefaultMutableTreeNode myOwningNode = null;
    @Override
    public void setOwningNode(SortableDefaultMutableTreeNode sortableDefaultMutableTreeNode) {
        myOwningNode = sortableDefaultMutableTreeNode;
    }

    @Override
    public SortableDefaultMutableTreeNode getOwningNode( ) {
        return myOwningNode;
    }

}
